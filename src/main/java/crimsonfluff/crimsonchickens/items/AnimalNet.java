package crimsonfluff.crimsonchickens.items;

import crimsonfluff.crimsonchickens.CrimsonChickens;
import crimsonfluff.crimsonchickens.blocks.NestTileEntity;
import crimsonfluff.crimsonchickens.entity.ResourceChickenEntity;
import crimsonfluff.crimsonchickens.init.initBlocks;
import crimsonfluff.crimsonchickens.init.initItems;
import crimsonfluff.crimsonchickens.init.initSounds;
import crimsonfluff.crimsonchickens.json.ResourceChickenData;
import crimsonfluff.crimsonchickens.registry.ChickenRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;

public class AnimalNet extends Item {
    public AnimalNet() {
        super(new Properties().tab(CrimsonChickens.TAB).durability(16));
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack itemStack, Player playerIn, LivingEntity entityIn, InteractionHand handIn) {
        if (playerIn.level.isClientSide) return InteractionResult.SUCCESS;

        ResourceChickenData chickenData = null;
        if (entityIn instanceof ResourceChickenEntity) {
            chickenData = ((ResourceChickenEntity) entityIn).chickenData;

            if (chickenData.name.equals("grave")) return InteractionResult.CONSUME;     // can't pick up grave chicken
            if (chickenData.name.equals("angry")) return InteractionResult.CONSUME;     // can't pick up angry chicken

        } else {
            if (entityIn.getClassification(false) == MobCategory.MONSTER) return InteractionResult.CONSUME;
            if (entityIn instanceof Player) return InteractionResult.CONSUME;           // just in case?
        }

        // checks for ClientSide and isDamageable and Creative
        itemStack.hurtAndBreak(1, playerIn, (player)-> { player.broadcastBreakEvent(handIn); });
        if (itemStack.isEmpty()) return InteractionResult.CONSUME;

        CompoundTag compoundStack = itemStack.getOrCreateTag();
        CompoundTag compound = new CompoundTag();
        entityIn.save(compound);

        // strip out nonsense, not used for our purposes, efficiency, memory etc
        compound.remove("UUID");
        compound.remove("Motion");
        compound.remove("Rotation");
        compound.remove("Pos");

        compoundStack.putInt("CustomModelData", 1);     // update model predicate to change texture to animal_net_full
        compoundStack.put("entityCaptured", compound);
        if (entityIn instanceof ResourceChickenEntity)
            compoundStack.putString("entityDescription", chickenData.displayName);
        else
            compoundStack.putString("entityDescription", entityIn.getType().getDescriptionId());

//        itemStack.setTag(compoundStack.copy());

//        CrimsonChickens.LOGGER.info("Hello: " + compoundStack);
//        CrimsonChickens.LOGGER.info("Hello: " + itemStack.getOrCreateTag());
        playerIn.sweepAttack();
        playerIn.level.playSound(null, playerIn.blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1f, 1f);

        entityIn.remove(Entity.RemovalReason.DISCARDED);
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
//        if (context.getPlayer() == null) return super.useOn(context);       // in case a mob can use items?
//        if (context.getPlayer().level.isClientSide) return super.useOn(context);
        if (context.getPlayer() == null) return InteractionResult.CONSUME;       // in case a mob can use items?
        if (context.getPlayer().level.isClientSide) return InteractionResult.SUCCESS;

        CompoundTag compound = context.getItemInHand().getTagElement("entityCaptured");

        if (context.getLevel().getBlockState(context.getClickedPos()).getBlock() == initBlocks.NEST_BLOCK.get()) {
            NestTileEntity te = (NestTileEntity) context.getLevel().getBlockEntity(context.getClickedPos());
            if (te == null) return InteractionResult.CONSUME;

            if (te.entityCaptured != null && compound == null) {
                // extract chicken from nest
                //context.getPlayer().displayClientMessage(new StringTextComponent("remove the chicken"), false);

                context.getItemInHand().getOrCreateTag().putInt("CustomModelData", 1);        // used to change item texture
                te.entityCaptured.putInt("EggLayTime", te.eggLayTime);                                  // update egg lay time
                context.getItemInHand().getOrCreateTag().put("entityCaptured", te.entityCaptured);
                context.getItemInHand().getOrCreateTag().putString("entityDescription", te.entityDescription);

                te.entityRemove(true);

                context.getLevel().playSound(null, context.getPlayer().blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1f, 1f);
                context.getPlayer().sweepAttack();

                // checks for ClientSide and isDamageable and Creative
                context.getItemInHand().hurtAndBreak(1, context.getPlayer(), player -> {
                    player.broadcastBreakEvent(context.getHand());
                });

            } else if (te.entityCaptured == null && compound != null) {
                //insert chicken into nest
                //context.getPlayer().displayClientMessage(new StringTextComponent("insert the chicken"), false);

                te.entitySet(compound, context.getItemInHand().getOrCreateTag().getString("entityDescription"), true);

                ResourceChickenData chickenData = ChickenRegistry.getRegistry().getChickenDataFromID(compound.getString("id"));
                context.getLevel().playSound(null, context.getPlayer().blockPosition(),
                    context.getLevel().random.nextInt(2) == 0
                        ? SoundEvents.CHICKEN_EGG
                        : chickenData.hasTrait == 1 ? initSounds.DUCK_AMBIENT.get() : SoundEvents.CHICKEN_AMBIENT
                    , SoundSource.PLAYERS, 1f, 1f);

                context.getItemInHand().removeTagKey("entityCaptured");
                context.getItemInHand().removeTagKey("entityDescription");
                context.getItemInHand().removeTagKey("CustomModelData");        // used to change item texture

            } else
                return InteractionResult.CONSUME;       // stops the arm swing animation

            ((ServerLevel) context.getLevel()).sendParticles(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(initItems.NEST_BLOCK_ITEM.get())),
                context.getClickedPos().getX() + 0.5, context.getClickedPos().getY() + 0.2, context.getClickedPos().getZ() + 0.5,
                10, 0.3, 0.2, 0.3,0);

            return InteractionResult.SUCCESS;
        }

        if (compound == null) return InteractionResult.FAIL;

        Entity entity = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(compound.getString("id"))).create(context.getLevel());
        if (entity != null) {
            BlockPos pos = context.getClickedPos().offset(context.getClickedFace().getNormal());
            int yPos = pos.getY(); if (context.getClickedFace() != Direction.UP) yPos += 0.5;

            entity.load(compound);
            entity.setPos(pos.getX() + 0.5, yPos, pos.getZ() + 0.5);
            context.getLevel().addFreshEntity(entity);

            context.getItemInHand().removeTagKey("entityCaptured");
            context.getItemInHand().removeTagKey("entityDescription");
            context.getItemInHand().removeTagKey("CustomModelData");        // used to change item texture

            context.getLevel().playSound(null, context.getPlayer().blockPosition(), SoundEvents.CHICKEN_EGG, SoundSource.PLAYERS, 1f, 1f);

            return InteractionResult.SUCCESS;

        } else
            return InteractionResult.CONSUME;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, worldIn, tooltip, tooltipFlag);

        CompoundTag compound = itemStack.getTagElement("entityCaptured");
        if (compound != null) {
            tooltip.add(new TranslatableComponent(itemStack.getOrCreateTag().getString("entityDescription")));

            if (compound.contains("CustomName", 8))
                tooltip.add(Component.Serializer.fromJson(compound.getString("CustomName")).withStyle(ChatFormatting.ITALIC));

            if (compound.getBoolean("analyzed")) {
                tooltip.add(new TranslatableComponent("tip.crimsonchickens.growth", compound.getInt("growth")).withStyle(ChatFormatting.GRAY));
                tooltip.add(new TranslatableComponent("tip.crimsonchickens.gain", compound.getInt("gain")).withStyle(ChatFormatting.GRAY));
                tooltip.add(new TranslatableComponent("tip.crimsonchickens.strength", compound.getInt("strength")).withStyle(ChatFormatting.GRAY));
            }
        }
    }
}

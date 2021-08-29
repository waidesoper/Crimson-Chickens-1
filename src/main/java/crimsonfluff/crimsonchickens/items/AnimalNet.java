package crimsonfluff.crimsonchickens.items;

import crimsonfluff.crimsonchickens.CrimsonChickens;
import crimsonfluff.crimsonchickens.blocks.NestTileEntity;
import crimsonfluff.crimsonchickens.entity.ResourceChickenEntity;
import crimsonfluff.crimsonchickens.init.initBlocks;
import crimsonfluff.crimsonchickens.init.initItems;
import crimsonfluff.crimsonchickens.json.ResourceChickenData;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;

public class AnimalNet extends Item {
    public AnimalNet() {
        super(new Properties().tab(CrimsonChickens.TAB).durability(16));
    }

    @Override
    public ActionResultType interactLivingEntity(ItemStack itemStack, PlayerEntity playerIn, LivingEntity entityIn, Hand handIn) {
        if (playerIn.level.isClientSide) return super.interactLivingEntity(itemStack, playerIn, entityIn, handIn);

// this is not needed because AnimalNet has durability, so no need to split stack
// would be nice to set max_durability. carry stack of 8 nets then split them when used??

//        ItemStack newStack;
//        if (itemStack.getCount() > 1) {
//            if (! playerIn.abilities.instabuild) itemStack.shrink(1);
//            newStack = new ItemStack(initItems.ANIMAL_NET.get());
//
//            if (! playerIn.inventory.add(newStack)) playerIn.drop(newStack, false);
//
//        } else
//            newStack = itemStack;

        if (entityIn.getClassification(false) == EntityClassification.MONSTER) return ActionResultType.FAIL;

        ResourceChickenData chickenData = null;
        if (entityIn instanceof ResourceChickenEntity) {
            chickenData = ((ResourceChickenEntity) entityIn).chickenData;
            if (chickenData.name.equals("grave")) return ActionResultType.FAIL;     // can't pick up grave chicken
        }

        // checks for ClientSide and isDamageable and Creative
        itemStack.hurtAndBreak(1, playerIn, (player)-> { player.broadcastBreakEvent(handIn); });
        if (itemStack.isEmpty()) return ActionResultType.FAIL;

        CompoundNBT compoundStack = itemStack.getOrCreateTag();
        CompoundNBT compound = new CompoundNBT();
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
        itemStack.setTag(compoundStack);        // TODO: not working in Creative !?

        playerIn.sweepAttack();
        playerIn.level.playSound(null, playerIn.blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 1f, 1f);

        entityIn.remove();
        return ActionResultType.SUCCESS;
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        if (context.getPlayer() == null) return super.useOn(context);       // in case a mob can use items?
        if (context.getPlayer().level.isClientSide) return super.useOn(context);

        CompoundNBT compound = context.getItemInHand().getTagElement("entityCaptured");

        if (context.getLevel().getBlockState(context.getClickedPos()).getBlock() == initBlocks.NEST_BLOCK.get()) {
            NestTileEntity te = (NestTileEntity) context.getLevel().getBlockEntity(context.getClickedPos());
            if (te == null) return ActionResultType.FAIL;

            if (te.entityCaptured != null && compound == null) {
                // extract chicken from nest
                //context.getPlayer().displayClientMessage(new StringTextComponent("remove the chicken"), false);

                context.getItemInHand().getOrCreateTag().putInt("CustomModelData", 1);        // used to change item texture
                te.entityCaptured.putInt("EggLayTime", te.eggLayTime);                                  // update egg lay time
                context.getItemInHand().getOrCreateTag().put("entityCaptured", te.entityCaptured);
                context.getItemInHand().getOrCreateTag().putString("entityDescription", te.entityDescription);

                te.entityRemove(true);

                context.getLevel().playSound(null, context.getPlayer().blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 1f, 1f);
                context.getPlayer().sweepAttack();

                // checks for ClientSide and isDamageable and Creative
                context.getItemInHand().hurtAndBreak(1, context.getPlayer(), player -> {
                    player.broadcastBreakEvent(context.getHand());
                });

            } else if (te.entityCaptured == null && compound != null) {
                //insert chicken into nest
                //context.getPlayer().displayClientMessage(new StringTextComponent("insert the chicken"), false);

                te.entitySet(compound, context.getItemInHand().getOrCreateTag().getString("entityDescription"), true);

                context.getItemInHand().removeTagKey("entityCaptured");
                context.getItemInHand().removeTagKey("entityDescription");
                context.getItemInHand().removeTagKey("CustomModelData");        // used to change item texture

                context.getLevel().playSound(null, context.getPlayer().blockPosition(), SoundEvents.CHICKEN_EGG, SoundCategory.PLAYERS, 1f, 1f);

            } else
                return ActionResultType.CONSUME;       // stops the arm swing animation ?

            ((ServerWorld) context.getLevel()).sendParticles(new ItemParticleData(ParticleTypes.ITEM, new ItemStack(initItems.NEST_BLOCK_ITEM.get())),
                context.getClickedPos().getX() + 0.5, context.getClickedPos().getY() + 0.2, context.getClickedPos().getZ() + 0.5,
                20, 0.3, 0.2, 0.3,0);

            return ActionResultType.SUCCESS;
        }

        if (compound == null) return ActionResultType.FAIL;

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

            context.getLevel().playSound(null, context.getPlayer().blockPosition(), SoundEvents.CHICKEN_EGG, SoundCategory.PLAYERS, 1f, 1f);

            return ActionResultType.SUCCESS;

        } else
            return ActionResultType.FAIL;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, worldIn, tooltip, tooltipFlag);

        CompoundNBT compound = itemStack.getTagElement("entityCaptured");
        if (compound != null) {
            tooltip.add(new TranslationTextComponent(itemStack.getOrCreateTag().getString("entityDescription")));

            if (compound.contains("CustomName", 8))
                tooltip.add(ITextComponent.Serializer.fromJson(compound.getString("CustomName")).withStyle(TextFormatting.ITALIC));

            if (compound.getBoolean("analyzed")) {
                tooltip.add(new TranslationTextComponent("tip.crimsonchickens.growth", compound.getInt("growth")).withStyle(TextFormatting.GRAY));
                tooltip.add(new TranslationTextComponent("tip.crimsonchickens.gain", compound.getInt("gain")).withStyle(TextFormatting.GRAY));
                tooltip.add(new TranslationTextComponent("tip.crimsonchickens.strength", compound.getInt("strength")).withStyle(TextFormatting.GRAY));
            }
        }
    }
}

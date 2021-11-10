package crimsonfluff.crimsonchickens.items;

import crimsonfluff.crimsonchickens.CrimsonChickens;
import crimsonfluff.crimsonchickens.blocks.NestTileEntity;
import crimsonfluff.crimsonchickens.entity.ResourceChickenEntity;
import crimsonfluff.crimsonchickens.init.initBlocks;
import crimsonfluff.crimsonchickens.init.initItems;
import crimsonfluff.crimsonchickens.init.initSounds;
import crimsonfluff.crimsonchickens.json.ResourceChickenData;
import crimsonfluff.crimsonchickens.registry.ChickenRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AnimalNet extends Item {
    public AnimalNet() {
        super(new FabricItemSettings().group(CrimsonChickens.CRIMSON_CHICKENS_TAB).maxDamage(16));
    }

    @Override
    public ActionResult useOnEntity(ItemStack itemStack, PlayerEntity playerIn, LivingEntity entityIn, Hand handIn) {
        if (playerIn.world.isClient) return ActionResult.PASS;

        // config.SpawnType=1 would mean Resource Chicken would be MONSTER classification
        ResourceChickenData chickenData = null;
        if (entityIn instanceof ResourceChickenEntity) {
            chickenData = ((ResourceChickenEntity) entityIn).chickenData;

            if (chickenData.name.equals("grave")) return ActionResult.FAIL;     // can't pick up grave chicken
            if (chickenData.name.equals("angry")) return ActionResult.FAIL;     // can't pick up angry chicken

        }
        else {
            // TODO: getClassification()
            if (entityIn instanceof HostileEntity) return ActionResult.FAIL;
            if (entityIn instanceof PlayerEntity) return ActionResult.FAIL;
        }

        // Moved damage to when releasing chicken, to avoid 1 durability break when picking up chicken

        NbtCompound compoundStack = itemStack.getOrCreateTag();
        NbtCompound compound = new NbtCompound();
        entityIn.saveNbt(compound);

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
            compoundStack.putString("entityDescription", entityIn.getType().getTranslationKey());

        // TODO: sweepAttack()
        playerIn.spawnSweepAttackParticles();
        playerIn.world.playSound(null, playerIn.getBlockPos(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 1f, 1f);

        entityIn.remove();
        return ActionResult.SUCCESS;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getPlayer() == null) return super.useOnBlock(context);       // in case a mob can use items?
        if (context.getPlayer().world.isClient) return super.useOnBlock(context);

        NbtCompound compound = context.getStack().getSubTag("entityCaptured");

        if (context.getWorld().getBlockState(context.getBlockPos()).getBlock() == initBlocks.NEST_BLOCK) {
            NestTileEntity te = (NestTileEntity) context.getWorld().getBlockEntity(context.getBlockPos());
            if (te == null) return ActionResult.FAIL;

            if (te.entityCaptured != null && compound == null) {
                // extract chicken from nest
                //context.getPlayer().displayClientMessage(new StringTextComponent("remove the chicken"), false);

                context.getStack().getOrCreateTag().putInt("CustomModelData", 1);        // used to change item texture
                te.entityCaptured.putInt("EggLayTime", te.eggLayTime);                                  // update egg lay time
                context.getStack().getOrCreateTag().put("entityCaptured", te.entityCaptured);
                context.getStack().getOrCreateTag().putString("entityDescription", te.entityDescription);

                te.entityRemove(true);

                // TODO: sweepAttack()
                context.getPlayer().spawnSweepAttackParticles();
                context.getWorld().playSound(null, context.getPlayer().getBlockPos(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 1f, 1f);

            }
            else if (te.entityCaptured == null && compound != null) {
                //insert chicken into nest
                //context.getPlayer().displayClientMessage(new StringTextComponent("insert the chicken"), false);

                // Make sure it's a chicken.  Sheep dont like the stasis chamber
                String s = compound.getString("id");
                if (s.startsWith("crimsonchickens:")) {
                    te.entitySet(compound, context.getStack().getOrCreateTag().getString("entityDescription"), true);

                    ResourceChickenData chickenData = ChickenRegistry.getRegistry().getChickenDataFromID(s);
                    context.getWorld().playSound(null, context.getPlayer().getBlockPos(),
                        context.getWorld().random.nextInt(2) == 0
                            ? SoundEvents.ENTITY_CHICKEN_EGG
                            : chickenData.hasTrait == 1 ? initSounds.DUCK_AMBIENT : SoundEvents.ENTITY_CHICKEN_AMBIENT
                        , SoundCategory.PLAYERS, 1f, 1f);

                    context.getStack().removeSubTag("entityCaptured");
                    context.getStack().removeSubTag("entityDescription");
                    context.getStack().removeSubTag("CustomModelData");        // used to change item texture

                    // checks for ClientSide and isDamageable and Creative
                    // TODO: re-check Hand
                    context.getStack().damage(1, context.getPlayer(), plyr -> plyr.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));

                }
                else
                    return ActionResult.CONSUME;       // stops the arm swing animation ?

            }
            else
                return ActionResult.CONSUME;       // stops the arm swing animation ?

            ((ServerWorld) context.getWorld()).spawnParticles(new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(initItems.NEST_BLOCK_ITEM)),
                context.getBlockPos().getX() + 0.5, context.getBlockPos().getY() + 0.2, context.getBlockPos().getZ() + 0.5,
                20, 0.3, 0.2, 0.3, 0);

            return ActionResult.SUCCESS;
        }

        if (compound == null) return ActionResult.FAIL;

//        Entity entity = ForgeRegistries.ENTITIES.getValue(new Identifier(compound.getString("id"))).create(context.getWorld());
        Entity entity = Registry.ENTITY_TYPE.get(new Identifier(compound.getString("id"))).create(context.getWorld());

        if (entity != null) {
            BlockPos pos = context.getBlockPos().offset(context.getSide());
            int yPos = pos.getY();
            if (context.getSide() != Direction.UP) yPos += 0.5;

            entity.readNbt(compound);
            entity.setPos(pos.getX() + 0.5, yPos, pos.getZ() + 0.5);
            context.getWorld().spawnEntity(entity);

            context.getStack().removeSubTag("entityCaptured");
            context.getStack().removeSubTag("entityDescription");
            context.getStack().removeSubTag("CustomModelData");        // used to change item texture

            context.getWorld().playSound(null, context.getPlayer().getBlockPos(), SoundEvents.ENTITY_CHICKEN_EGG, SoundCategory.PLAYERS, 1f, 1f);

            // checks for ClientSide and isDamageable and Creative
//            context.getItemInHand().hurtAndBreak(1, context.getPlayer(), player -> player.broadcastBreakEvent(context.getHand()));
            // TODO: re-check Hand
            context.getStack().damage(1, context.getPlayer(), plyr -> plyr.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));

            return ActionResult.SUCCESS;

        }
        else
            return ActionResult.FAIL;
    }

    @Override
    public void appendTooltip(ItemStack itemStack, @Nullable World worldIn, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(itemStack, worldIn, tooltip, context);

        NbtCompound compound = itemStack.getSubTag("entityCaptured");
        if (compound != null) {
            tooltip.add(new TranslatableText(itemStack.getOrCreateTag().getString("entityDescription")));

            if (compound.contains("CustomName", 8))
                tooltip.add(Text.Serializer.fromJson(compound.getString("CustomName")).formatted(Formatting.ITALIC));

            if (compound.getBoolean("analyzed")) {
                tooltip.add(new TranslatableText("tip.crimsonchickens.growth", compound.getInt("growth")).formatted(Formatting.GRAY));
                tooltip.add(new TranslatableText("tip.crimsonchickens.gain", compound.getInt("gain")).formatted(Formatting.GRAY));
                tooltip.add(new TranslatableText("tip.crimsonchickens.strength", compound.getInt("strength")).formatted(Formatting.GRAY));
            }
        }
    }
}

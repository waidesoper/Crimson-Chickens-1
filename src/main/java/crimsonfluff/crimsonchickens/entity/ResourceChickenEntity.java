package crimsonfluff.crimsonchickens.entity;

import crimsonfluff.crimsonchickens.CrimsonChickens;
import crimsonfluff.crimsonchickens.init.ModEntities;
import crimsonfluff.crimsonchickens.json.ResourceChickenData;
import crimsonfluff.crimsonchickens.registry.ChickenRegistry;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootTable;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;

import javax.annotation.Nullable;
import java.util.Random;

public class ResourceChickenEntity extends ChickenEntity {
    private static final Ingredient FOOD_ITEMS = Ingredient.of(Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.BEETROOT_SEEDS);
    public ResourceChickenData chickenData;

    public int eggTime;
    private int noJumpDelay;
    private int inLove;


// TODO how to change the localisation
//    @Override
//    public ITextComponent getDisplayName() { return new StringTextComponent(chickenData.displayName); }

//    @Nullable
//    @Override
//    public ITextComponent getCustomName() { return new StringTextComponent(chickenData.displayName); }


    public ResourceChickenEntity(EntityType<? extends ChickenEntity> type, World world, ResourceChickenData chickenData) {
        super(type, world);
        this.chickenData = chickenData;

        this.eggTime = this.random.nextInt(chickenData.eggLayTime) + chickenData.eggLayTime;
    }

    public static AttributeModifierMap.MutableAttribute createChickenAttributes(String name) {
        ResourceChickenData chickenData = ChickenRegistry.getRegistry().getChickenData(name);

        return createMobAttributes()
            .add(Attributes.MAX_HEALTH, chickenData.baseHealth)
            .add(Attributes.MOVEMENT_SPEED, chickenData.baseSpeed);
    }

    // Override all main aiStep()s - mainly because calling super causes eggs to be dropped from ChickenEntity.class
    // I decided I want control over EVERYTHING that gets dropped
    @Override
    public void aiStep() {
// AnimalEntity
        if (this.getAge() != 0) {
            this.inLove = 0;
        }

        if (this.inLove > 0) {
            --this.inLove;
            if (this.inLove % 10 == 0) {
                double d0 = this.random.nextGaussian() * 0.02D;
                double d1 = this.random.nextGaussian() * 0.02D;
                double d2 = this.random.nextGaussian() * 0.02D;
                this.level.addParticle(ParticleTypes.HEART, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
            }
        }

// AgeableEntity.class
        if (this.level.isClientSide) {
            if (this.forcedAgeTimer > 0) {
                if (this.forcedAgeTimer % 4 == 0) {
                    this.level.addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 0.0D, 0.0D, 0.0D);
                }

                --this.forcedAgeTimer;
            }
        } else if (this.isAlive()) {
            int i = this.getAge();
            if (i < 0) {
                ++i;
                this.setAge(i);
            } else if (i > 0) {
                --i;
                this.setAge(i);
            }
        }

// LivingEntity.class
        if (this.noJumpDelay > 0) {
            --this.noJumpDelay;
        }

        if (this.isControlledByLocalInstance()) {
            this.lerpSteps = 0;
            this.setPacketCoordinates(this.getX(), this.getY(), this.getZ());
        }

        if (this.lerpSteps > 0) {
            double d0 = this.getX() + (this.lerpX - this.getX()) / (double)this.lerpSteps;
            double d2 = this.getY() + (this.lerpY - this.getY()) / (double)this.lerpSteps;
            double d4 = this.getZ() + (this.lerpZ - this.getZ()) / (double)this.lerpSteps;
            double d6 = MathHelper.wrapDegrees(this.lerpYRot - (double)this.yRot);
            this.yRot = (float)((double)this.yRot + d6 / (double)this.lerpSteps);
            this.xRot = (float)((double)this.xRot + (this.lerpXRot - (double)this.xRot) / (double)this.lerpSteps);
            --this.lerpSteps;
            this.setPos(d0, d2, d4);
            this.setRot(this.yRot, this.xRot);
        } else if (!this.isEffectiveAi()) {
            this.setDeltaMovement(this.getDeltaMovement().scale(0.98D));
        }

        if (this.lerpHeadSteps > 0) {
            this.yHeadRot = (float)((double)this.yHeadRot + MathHelper.wrapDegrees(this.lyHeadRot - (double)this.yHeadRot) / (double)this.lerpHeadSteps);
            --this.lerpHeadSteps;
        }

        Vector3d vector3d = this.getDeltaMovement();
        double d1 = vector3d.x;
        double d3 = vector3d.y;
        double d5 = vector3d.z;
        if (Math.abs(vector3d.x) < 0.003D) {
            d1 = 0.0D;
        }

        if (Math.abs(vector3d.y) < 0.003D) {
            d3 = 0.0D;
        }

        if (Math.abs(vector3d.z) < 0.003D) {
            d5 = 0.0D;
        }

        this.setDeltaMovement(d1, d3, d5);
        this.level.getProfiler().push("ai");
        if (this.isImmobile()) {
            this.jumping = false;
            this.xxa = 0.0F;
            this.zza = 0.0F;
        } else if (this.isEffectiveAi()) {
            this.level.getProfiler().push("newAi");
            this.serverAiStep();
            this.level.getProfiler().pop();
        }

        this.level.getProfiler().pop();
        this.level.getProfiler().push("jump");
        if (this.jumping && this.isAffectedByFluids()) {
            double d7;
            if (this.isInLava()) {
                d7 = this.getFluidHeight(FluidTags.LAVA);
            } else {
                d7 = this.getFluidHeight(FluidTags.WATER);
            }

            boolean flag = this.isInWater() && d7 > 0.0D;
            double d8 = this.getFluidJumpThreshold();
            if (!flag || this.onGround && !(d7 > d8)) {
                if (!this.isInLava() || this.onGround && !(d7 > d8)) {
                    if ((this.onGround || flag && d7 <= d8) && this.noJumpDelay == 0) {
                        this.jumpFromGround();
                        this.noJumpDelay = 10;
                    }
                } else {
                    this.jumpInLiquid(FluidTags.LAVA);
                }
            } else {
                this.jumpInLiquid(FluidTags.WATER);
            }
        } else {
            this.noJumpDelay = 0;
        }

        this.level.getProfiler().pop();
        this.level.getProfiler().push("travel");
        this.xxa *= 0.98F;
        this.zza *= 0.98F;
        AxisAlignedBB axisalignedbb = this.getBoundingBox();
        this.travel(new Vector3d(this.xxa, this.yya, this.zza));
        this.level.getProfiler().pop();
        this.level.getProfiler().push("push");
        if (this.autoSpinAttackTicks > 0) {
            --this.autoSpinAttackTicks;
            this.checkAutoSpinAttack(axisalignedbb, this.getBoundingBox());
        }

        this.pushEntities();
        this.level.getProfiler().pop();
        if (!this.level.isClientSide && this.isSensitiveToWater() && this.isInWaterRainOrBubble()) {
            this.hurt(DamageSource.DROWN, 1.0F);
        }


// ChickenEntity.class
        this.oFlap = this.flap;
        this.oFlapSpeed = this.flapSpeed;
        this.flapSpeed = (float)((double)this.flapSpeed + (double)(this.onGround ? -1 : 4) * 0.3D);
        this.flapSpeed = MathHelper.clamp(this.flapSpeed, 0.0F, 1.0F);
        if (!this.onGround && this.flapping < 1.0F) {
            this.flapping = 1.0F;
        }

        this.flapping = (float)((double)this.flapping * 0.9D);
        vector3d = this.getDeltaMovement();
        if (!this.onGround && vector3d.y < 0.0D) {
            this.setDeltaMovement(vector3d.multiply(1.0D, 0.6D, 1.0D));
        }

        this.flap += this.flapping * 2.0F;


        if (!this.level.isClientSide && this.isAlive() && !this.isBaby() && !this.isChickenJockey && --this.eggTime <= 0) {
            this.playSound(SoundEvents.CHICKEN_EGG, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);

            if (chickenData.dropItemItem.getItem() != Items.AIR) {
                ItemStack itm = new ItemStack(chickenData.dropItemItem);
                if (chickenData.dropItemNBT != null) itm.setTag(chickenData.dropItemNBT.copy());

                this.spawnAtLocation(itm);
            }

            this.eggTime = this.random.nextInt(chickenData.eggLayTime) + chickenData.eggLayTime;
//
////            if (CrimsonTwitch.CONFIGURATION.chickenEggChance.get() != 0)
//                if (this.level.random.nextInt(100) + 1 <= CrimsonChickens.CONFIGURATION.chickenEggChance.get())
//                    this.spawnAtLocation(Items.EGG);
        }
    }

    @Override
    public ChickenEntity getBreedOffspring(ServerWorld worldIn, AgeableEntity ageableEntity) {
        if (CrimsonChickens.CONFIGURATION.masterSwitchBreeding.get() == 0) return null;

        ResourceChickenEntity rce = null;
        if (ageableEntity instanceof ResourceChickenEntity)
            rce = (ResourceChickenEntity) ageableEntity;

        if (CrimsonChickens.CONFIGURATION.masterSwitchBreeding.get() == 2) {
            if (! chickenData.canBreed) return null;

            if (rce != null)
                if (! rce.chickenData.canBreed) return null;
        }

        // if chickens are same type and allow breeding then go for it...
        if (rce != null) {
            if (chickenData.name.equals(rce.chickenData.name))
                return ModEntities.getModChickens().get(chickenData.name).get().create(worldIn);

            else {
                // TODO: Work out cross breeding types
                worldIn.playSound(null, this.blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundCategory.BLOCKS, 1f, 1f);
                return super.getBreedOffspring(worldIn, ageableEntity);
            }

        } else {
            // breeding with vanilla/other modded
            int r = worldIn.random.nextInt(100) + 1;
            if (r <= CrimsonChickens.CONFIGURATION.allowBreedingWithVanilla.get())
                return ModEntities.getModChickens().get(chickenData.name).get().create(worldIn);
            else
                return super.getBreedOffspring(worldIn, ageableEntity);     // super in case its an other modded chicken?
        }
    }

    @Override
    public boolean isFood(ItemStack stack) {
// TODO: What about NBT items?
        if (CrimsonChickens.CONFIGURATION.masterSwitchBreedingItem.get())
            return (stack.getItem() == chickenData.dropItemItem);

        else
            return FOOD_ITEMS.test(stack);
    }

    @Override
    public ITextComponent getName() {
        return new StringTextComponent(chickenData.displayName);
    }

    @Override
    public void setInLove(@Nullable PlayerEntity player) {
        if (player != null) {
            if (player instanceof FakePlayer)
                if (! CrimsonChickens.CONFIGURATION.allowFakeplayerBreeding.get()) return;
        }

        super.setInLove(player);
    }

//    @Override
//    public void die(DamageSource p_70645_1_) {
//        super.die(p_70645_1_);
//        CrimsonChickens.LOGGER.info("DEATH OCCURED");
//    }

    @Override
    protected void dropFromLootTable(DamageSource damageSource, boolean p_213354_2_) {
        if (! CrimsonChickens.CONFIGURATION.allowFakeplayerLootDrops.get()) return;

        ResourceLocation resourcelocation = new ResourceLocation("minecraft", "entities/chicken");

        LootTable loottable = this.level.getServer().getLootTables().get(resourcelocation);
        LootContext.Builder lootcontext$builder = this.createLootContext(p_213354_2_, damageSource);
        LootContext ctx = lootcontext$builder.create(LootParameterSets.ENTITY);

        loottable.getRandomItems(ctx).forEach(this::spawnAtLocation);
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource damageSource, int fortune, boolean p_213333_3_) {
        if (! CrimsonChickens.CONFIGURATION.allowFakeplayerLootDrops.get()) return;

        int r = new Random().nextInt(100) + 1;
        if (r <= CrimsonChickens.CONFIGURATION.allowDeathDropResource.get()) {
            ItemStack item = new ItemStack(chickenData.dropItemItem, fortune + 1);
            if (chickenData.dropItemNBT != null)
                item.setTag(chickenData.dropItemNBT.copy());

            spawnAtLocation(item);
        }
    }
}

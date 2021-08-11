package crimsonfluff.crimsonchickens.entity;

import crimsonfluff.crimsonchickens.CrimsonChickens;
import crimsonfluff.crimsonchickens.init.initEntities;
import crimsonfluff.crimsonchickens.init.initItems;
import crimsonfluff.crimsonchickens.init.initSounds;
import crimsonfluff.crimsonchickens.json.ResourceChickenData;
import crimsonfluff.crimsonchickens.registry.ChickenRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
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
import net.minecraft.potion.EffectInstance;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.RegistryObject;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
@ParametersAreNonnullByDefault
public class ResourceChickenEntity extends ChickenEntity {
    private static final Ingredient FOOD_ITEMS = Ingredient.of(Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.BEETROOT_SEEDS);
    public ResourceChickenData chickenData;

    public int eggTime;
    private int noJumpDelay;
    private int inLove;

    public ResourceChickenEntity(EntityType<? extends ChickenEntity> type, World world, ResourceChickenData chickenData) {
        super(type, world);
        this.chickenData = chickenData;
        this.eggTime = this.random.nextInt(chickenData.eggLayTime) + chickenData.eggLayTime;

        //LOGGER.info("CREATED: " + chickenData.displayName);
    }

    public static AttributeModifierMap.MutableAttribute createChickenAttributes(String name) {
        ResourceChickenData chickenData = ChickenRegistry.getRegistry().getChickenData(name);

        return createMobAttributes()
            .add(Attributes.MAX_HEALTH, chickenData.baseHealth)
            .add(Attributes.MOVEMENT_SPEED, chickenData.baseSpeed);
    }

    @Override
    public int getMaxSpawnClusterSize() { return 4; }

//    @Override
//    public CreatureAttribute getMobType() { return CreatureAttribute.UNDEAD; }
//
//    @Override
//    public ILivingEntityData finalizeSpawn(IServerWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
//        return super.finalizeSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
//    }
//
    @Override
    public boolean checkSpawnRules(IWorld worldIn, SpawnReason spawnReason) {
        //return spawnReason == SpawnReason.SPAWNER || spawnReason == SpawnReason.NATURAL;        // out of control spawns !!
        return super.checkSpawnRules(worldIn, spawnReason);
    }

    @Override
    public float getWalkTargetValue(BlockPos p_205022_1_, IWorldReader p_205022_2_) {
        return 10.0f;        //p_205022_2_.getBlockState(p_205022_1_.below()).is(Blocks.GRASS_BLOCK) ? 10.0F : p_205022_2_.getBrightness(p_205022_1_) - 0.5F;
    }

    // Override all main aiStep()s - mainly because calling super causes eggs to be dropped from ChickenEntity.class
    // I want control over EVERYTHING that gets dropped
    @Override
    public void aiStep() {
// AnimalEntity
//        if (this.getAge() != 0) {
//            this.inLove = 0;
//        }
//
//        if (this.inLove > 0) {
//            --this.inLove;
//            if (this.inLove % 10 == 0) {
//                double d0 = this.random.nextGaussian() * 0.02D;
//                double d1 = this.random.nextGaussian() * 0.02D;
//                double d2 = this.random.nextGaussian() * 0.02D;
//                this.level.addParticle(ParticleTypes.HEART, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
//            }
//        }

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

            if (chickenData.hasTrait == 1)
                this.spawnAtLocation(new ItemStack(initItems.EGG_DUCK.get()));

            else if (chickenData.dropItemItem.getItem() != Items.AIR) {
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

        if (this.level.isClientSide) {
            if (chickenData.hasTrait == 2) {
                this.level.addParticle(ParticleTypes.PORTAL, this.getRandomX(0.5D), this.getRandomY() - 0.25D, this.getRandomZ(0.5D), (this.random.nextDouble() - 0.5D) * 2.0D, -this.random.nextDouble(), (this.random.nextDouble() - 0.5D) * 2.0D);
                this.level.addParticle(ParticleTypes.PORTAL, this.getRandomX(0.5D), this.getRandomY() - 0.25D, this.getRandomZ(0.5D), (this.random.nextDouble() - 0.5D) * 2.0D, -this.random.nextDouble(), (this.random.nextDouble() - 0.5D) * 2.0D);
                if (this.random.nextInt(100) == 0 && !this.isSilent()) {
                    this.level.playLocalSound(this.getX() + 0.5D, this.getY() + 0.5D, this.getZ() + 0.5D, SoundEvents.PORTAL_AMBIENT, SoundCategory.NEUTRAL,0.5F, this.random.nextFloat() * 0.4F + 0.8F, false);
                }
            }

            if (chickenData.hasTrait == 5) {
                this.level.addParticle(ParticleTypes.FLAME, this.getRandomX(0.5D), this.getRandomY() + 0.7D, this.getRandomZ(0.5D), 0, 0, 0);
                this.level.addParticle(ParticleTypes.SMOKE, this.getRandomX(0.5D), this.getRandomY() + 0.7D, this.getRandomZ(0.5D), 0, 0, 0);

                if (this.random.nextInt(24) == 0 && !this.isSilent()) {
                    this.level.playLocalSound(this.getX() + 0.5D, this.getY() + 0.5D, this.getZ() + 0.5D, SoundEvents.BLAZE_BURN, SoundCategory.NEUTRAL, 1.0F + this.random.nextFloat(), this.random.nextFloat() * 0.7F + 0.3F, false);
                }
            }
        }
    }

    /*
     TODO: better way to return null/cancel breeding
     otherwise both chickens get stuck 'kissing'
    */
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
            // if both are ducks...
            if (chickenData.hasTrait == 1 && rce.chickenData.hasTrait == 1) {
            // chance of getting either a source duck, or a target duck, in case a mod extends my duck?
                return worldIn.random.nextInt(2) == 0
                    ? initEntities.getModChickens().get(chickenData.name).get().create(worldIn)
                    : initEntities.getModChickens().get(rce.chickenData.name).get().create(worldIn);
            }

            // only ducks can breed with ducks !
            if (chickenData.hasTrait == 1 || rce.chickenData.hasTrait == 1) return null;

            // if both chickens are the same...
            if (chickenData.name.equals(rce.chickenData.name))
                return initEntities.getModChickens().get(chickenData.name).get().create(worldIn);

            // Work out cross-breeding types
            if (CrimsonChickens.CONFIGURATION.masterSwitchCrossBreeding.get()) {
                String parentA = this.chickenData.getEntityTypeRegistryID().toString();
                String parentB = ((ResourceChickenEntity) ageableEntity).chickenData.getEntityTypeRegistryID().toString();
//                CrimsonChickens.LOGGER.info("Parent: " + parentA + " : " + parentB);

                boolean a, b;
                for (Map.Entry<String, RegistryObject<EntityType<? extends ResourceChickenEntity>>> entry : initEntities.getModChickens().entrySet()) {
                    String string = entry.getKey();
                    RegistryObject<EntityType<? extends ResourceChickenEntity>> thing = entry.getValue();
                    ResourceChickenData chickenData = ChickenRegistry.getRegistry().getChickenData(string);

//                    a = (Objects.equals(chickenData.parentA, parentA) || Objects.equals(chickenData.parentA, parentB));
//                    b = (Objects.equals(chickenData.parentB, parentA) || Objects.equals(chickenData.parentB, parentB));
                    a = (Objects.equals(chickenData.parentA, parentA) && Objects.equals(chickenData.parentB, parentB));
                    b = (Objects.equals(chickenData.parentA, parentB) && Objects.equals(chickenData.parentB, parentA));

                    if (a || b) {
                        worldIn.playSound(null, this.blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundCategory.BLOCKS, 1f, 1f);
                        return thing.get().create(worldIn);
                    }
                }
            }

            return null;

        } else {
            // breeding with vanilla/other modded
            int r = worldIn.random.nextInt(100) + 1;
            if (r <= CrimsonChickens.CONFIGURATION.allowBreedingWithVanilla.get())
                return initEntities.getModChickens().get(chickenData.name).get().create(worldIn);
            else
                return super.getBreedOffspring(worldIn, ageableEntity);     // super in case its an other modded chicken?
        }
    }

    @Override
    public boolean isFood(ItemStack stack) {
        if (stack.isEmpty()) return false;

        if (CrimsonChickens.CONFIGURATION.masterSwitchBreedingItem.get()) {
            if (stack.hasTag() && !chickenData.dropItemNBT.isEmpty())
                return chickenData.dropItemNBT == stack.getTag();                               // TODO: Test this
            else
                return (stack.getItem() == chickenData.dropItemItem);                           // MUST use resource drop as food/breed item
        }

        else
            return FOOD_ITEMS.test(stack) || (stack.getItem() == chickenData.dropItemItem);     // CAN use resource drop as food/breed, *and* regular food (seeds)
    }

    @Override
    public ITextComponent getName() {
        return this.hasCustomName() ? this.getCustomName() : new StringTextComponent(chickenData.displayName);
    }

    @Override
    public void setInLove(@Nullable PlayerEntity player) {
        if (player != null) {
            if (player instanceof FakePlayer)
                if (! CrimsonChickens.CONFIGURATION.allowFakeplayerBreeding.get()) return;
        }

        super.setInLove(player);
    }

    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);

        //TODO: WIP
        if (damageSource.getEntity() instanceof FakePlayer) return;

        if (chickenData.hasTrait == 3) {
            Explosion.Mode explosion$mode = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level, this) ? Explosion.Mode.DESTROY : Explosion.Mode.NONE;
            this.level.explode(this, this.getX(), this.getY(), this.getZ(), 2, explosion$mode);

            if (! this.level.isClientSide)
                damageSource.getEntity().hurt(new DamageSource("chicken.explode"), 10);
        }
        else if (chickenData.hasTrait == 4) {
            if (! this.level.isClientSide)
                damageSource.getEntity().hurt(new DamageSource("chicken.thorns"), 1);
        }
    }

    @Override
    public boolean hurt(DamageSource damageSource, float p_70097_2_) {
        boolean wasHurt = super.hurt(damageSource, p_70097_2_);

        if (damageSource.getEntity() instanceof FakePlayer) return wasHurt;

        if (this.isAlive()) {
            if (chickenData.hasTrait == 2) {
                if (! this.level.isClientSide() && (damageSource.getEntity() instanceof LivingEntity) && this.random.nextInt(10) != 0) {
//                this.teleport();
                    for (int i = 0; i < 64; ++ i) {
                        if (this.teleport()) return wasHurt;
                    }

//                LOGGER.info("TELEPORT");
//                return false;
                }
            }
            else if (chickenData.hasTrait == 4) {
                if (! this.level.isClientSide)
                    damageSource.getEntity().hurt(new DamageSource("chicken.thorns"), 1);
            }
        }

        return wasHurt;
    }

    protected boolean teleport() {
        double d0 = this.getX() + (this.random.nextDouble() - 0.5D) * 64.0D;
        double d1 = this.getY() + (double)(this.random.nextInt(64) - 32);
        double d2 = this.getZ() + (this.random.nextDouble() - 0.5D) * 64.0D;
        return this.teleport(d0, d1, d2);
    }

    private boolean teleport(double p_70825_1_, double p_70825_3_, double p_70825_5_) {
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(p_70825_1_, p_70825_3_, p_70825_5_);

        while(blockpos$mutable.getY() > 0 && !this.level.getBlockState(blockpos$mutable).getMaterial().blocksMotion()) {
            blockpos$mutable.move(Direction.DOWN);
        }

        BlockState blockstate = this.level.getBlockState(blockpos$mutable);
        boolean flag = blockstate.getMaterial().blocksMotion();
//        boolean flag1 = blockstate.getFluidState().is(FluidTags.WATER);
//        if (flag && !flag1) {
        if (flag) {
            net.minecraftforge.event.entity.living.EnderTeleportEvent event = new net.minecraftforge.event.entity.living.EnderTeleportEvent(this, p_70825_1_, p_70825_3_, p_70825_5_, 0);
            if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event)) return false;
            boolean flag2 = this.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true);
            if (flag2 && !this.isSilent()) {
                this.level.playSound(null, this.xo, this.yo, this.zo, SoundEvents.ENDERMAN_TELEPORT, this.getSoundSource(), 1.0F, 1.0F);
                this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
            }

            return flag2;
        } else {
            return false;
        }
    }

    private void spawnLingeringCloud() {
        Collection<EffectInstance> collection = this.getActiveEffects();
        if (!collection.isEmpty()) {
            AreaEffectCloudEntity areaeffectcloudentity = new AreaEffectCloudEntity(this.level, this.getX(), this.getY(), this.getZ());
            areaeffectcloudentity.setRadius(2.5F);
            areaeffectcloudentity.setRadiusOnUse(-0.5F);
            areaeffectcloudentity.setWaitTime(10);
            areaeffectcloudentity.setDuration(areaeffectcloudentity.getDuration() / 2);
            areaeffectcloudentity.setRadiusPerTick(-areaeffectcloudentity.getRadius() / (float)areaeffectcloudentity.getDuration());

            for(EffectInstance effectinstance : collection) {
                areaeffectcloudentity.addEffect(new EffectInstance(effectinstance));
            }

            this.level.addFreshEntity(areaeffectcloudentity);
        }
    }

    @Override
    protected void dropFromLootTable(DamageSource damageSource, boolean p_213354_2_) {
        if (! CrimsonChickens.CONFIGURATION.allowFakeplayerLootDrops.get()) return;

        ResourceLocation resourcelocation;
        if (chickenData.hasTrait == 1)
            resourcelocation = new ResourceLocation(CrimsonChickens.MOD_ID, "entities/duck");
        else
            resourcelocation = new ResourceLocation("minecraft", "entities/chicken");

        LootTable loottable = this.level.getServer().getLootTables().get(resourcelocation);
        LootContext.Builder lootcontext$builder = this.createLootContext(p_213354_2_, damageSource);
        LootContext ctx = lootcontext$builder.create(LootParameterSets.ENTITY);

        loottable.getRandomItems(ctx).forEach(this::spawnAtLocation);
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource damageSource, int fortune, boolean p_213333_3_) {
        if (! CrimsonChickens.CONFIGURATION.allowFakeplayerLootDrops.get()) return;

        if (chickenData.dropItemItem.getItem() == Items.AIR) return;

        int r = new Random().nextInt(100) + 1;
        if (r <= CrimsonChickens.CONFIGURATION.allowDeathDropResource.get()) {
            ItemStack item = new ItemStack(chickenData.dropItemItem, fortune + 1);
            if (chickenData.dropItemNBT != null)
                item.setTag(chickenData.dropItemNBT.copy());

            spawnAtLocation(item);
        }
    }

    protected SoundEvent getAmbientSound() {
        //if (chickenData.hasTrait == 1) return soundsInit.DUCK_DEATH.get();
        //if (chickenData.hasTrait == 5) return null;     // Blaze/Fire Chicken have own sound effects in aiStep()

        return chickenData.hasTrait == 1 ? initSounds.DUCK_AMBIENT.get() : SoundEvents.CHICKEN_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        if (chickenData.hasTrait == 1) return initSounds.DUCK_DEATH.get();
        if (chickenData.hasTrait == 7) return SoundEvents.GHAST_HURT;
        if (chickenData.hasTrait == 8) return SoundEvents.SKELETON_HURT;

        return SoundEvents.CHICKEN_HURT;
    }

    protected SoundEvent getDeathSound() {
        if (chickenData.hasTrait == 1) return initSounds.DUCK_DEATH.get();
        if (chickenData.hasTrait == 6) return SoundEvents.GLASS_BREAK;
        if (chickenData.hasTrait == 7) return SoundEvents.GHAST_DEATH;
        if (chickenData.hasTrait == 8) return SoundEvents.SKELETON_DEATH;

        return SoundEvents.CHICKEN_DEATH;
    }


}

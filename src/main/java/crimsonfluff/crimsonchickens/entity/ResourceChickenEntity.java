package crimsonfluff.crimsonchickens.entity;

/**
 *  This class uses the stat's system from (increaseStats, inheritStats and calculateNewStat)
 *  @setycz's Chickens mod
 *  @Licence: MIT
 *  @https://www.curseforge.com/minecraft/mc-mods/chickens
 **/

import crimsonfluff.crimsonchickens.CrimsonChickens;
import crimsonfluff.crimsonchickens.compat.ITOPInfoEntityProvider;
import crimsonfluff.crimsonchickens.init.initEntities;
import crimsonfluff.crimsonchickens.init.initSounds;
import crimsonfluff.crimsonchickens.json.ResourceChickenData;
import crimsonfluff.crimsonchickens.registry.ChickenRegistry;
import mcjty.theoneprobe.api.IProbeHitEntityData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import mcp.mobius.waila.api.EntityAccessor;
import mcp.mobius.waila.api.ITooltip;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fmllegacy.RegistryObject;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@ParametersAreNonnullByDefault
public class ResourceChickenEntity extends Chicken implements ITOPInfoEntityProvider {
    private static final Ingredient FOOD_ITEMS = Ingredient.of(Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.BEETROOT_SEEDS);
    private static final EntityDataAccessor<Boolean> ANALYZED = SynchedEntityData.defineId(ResourceChickenEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> GROWTH = SynchedEntityData.defineId(ResourceChickenEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> GAIN = SynchedEntityData.defineId(ResourceChickenEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> STRENGTH = SynchedEntityData.defineId(ResourceChickenEntity.class, EntityDataSerializers.INT);
    public ResourceChickenData chickenData;

    private int noJumpDelay;        // now private

    public ResourceChickenEntity(EntityType<? extends Chicken> type, Level world, ResourceChickenData chickenData) {
        super(type, world);
        this.chickenData = chickenData;

        // cant set in defineSynchedData, NPE
        this.eggTime = CrimsonChickens.calcNewEggLayTime(this.random, this.chickenData, 1);
    }

    public static AttributeSupplier.Builder createChickenAttributes(String name) {
        ResourceChickenData chickenData = ChickenRegistry.getRegistry().getChickenData(name);

        return createMobAttributes()
            .add(Attributes.MAX_HEALTH, chickenData.baseHealth)
            .add(Attributes.MOVEMENT_SPEED, chickenData.baseSpeed);
    }

    @Override
    public int getMaxSpawnClusterSize() { return 4; }

//    @Override
//    public Component getDescription() {
//        return new StringTextComponent(chickenData.displayName);
//    }
//    public String getDescriptionId() {
//        return chickenData.displayName;
//    }

    @Override
    protected Component getTypeName() {
        return new TextComponent(chickenData.displayName);
    }

    @Override
    public float getWalkTargetValue(BlockPos p_205022_1_, LevelReader p_205022_2_) {
        return 10.0f;        //p_205022_2_.getBlockState(p_205022_1_.below()).is(Blocks.GRASS_BLOCK) ? 10.0F : p_205022_2_.getBrightness(p_205022_1_) - 0.5F;
    }

    // Override all main aiStep()s - mainly because calling super causes eggs to be dropped from ChickenEntity.class
    // I want control over EVERYTHING that gets dropped
    @Override
    public void aiStep() {
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
            double d6 = Mth.wrapDegrees(this.lerpYRot - (double)this.getYRot());
            this.setYRot((float)((double)this.getYRot() + d6 / (double)this.lerpSteps));
            this.setXRot((float)((double)this.getXRot() + (this.lerpXRot - (double)this.getXRot()) / (double)this.lerpSteps));
            --this.lerpSteps;
            this.setPos(d0, d2, d4);
            this.setRot(this.getYRot(), this.getXRot());
        } else if (!this.isEffectiveAi()) {
            this.setDeltaMovement(this.getDeltaMovement().scale(0.98D));
        }

        if (this.lerpHeadSteps > 0) {
            this.yHeadRot = (float)((double)this.yHeadRot + Mth.wrapDegrees(this.lyHeadRot - (double)this.yHeadRot) / (double)this.lerpHeadSteps);
            --this.lerpHeadSteps;
        }

        Vec3 vector3d = this.getDeltaMovement();
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
        AABB axisalignedbb = this.getBoundingBox();
        this.travel(new Vec3(this.xxa, this.yya, this.zza));
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
        this.flapSpeed = Mth.clamp(this.flapSpeed, 0.0F, 1.0F);
        if (!this.onGround && this.flapping < 1.0F) {
            this.flapping = 1.0F;
        }

        this.flapping = (float)((double)this.flapping * 0.9D);
        vector3d = this.getDeltaMovement();
        if (!this.onGround && vector3d.y < 0.0D) {
            this.setDeltaMovement(vector3d.multiply(1.0D, 0.6D, 1.0D));
        }

        this.flap += this.flapping * 2.0F;


        if (this.chickenData.eggLayTime != 0) {
            if (!this.level.isClientSide && this.isAlive() && !this.isBaby() && !this.isChickenJockey && --this.eggTime <= 0) {
                CrimsonChickens.calcDrops(this.entityData.get(GAIN), chickenData, 0).forEach(this::spawnAtLocation);
                this.playSound(SoundEvents.CHICKEN_EGG, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);

                // New egg lay time based on stats
                this.eggTime = CrimsonChickens.calcNewEggLayTime(this.random, chickenData, this.entityData.get(GROWTH));
            }
        }

        if (this.level.isClientSide) {
            if (chickenData.hasTrait == 2) {
                this.level.addParticle(ParticleTypes.PORTAL, this.getRandomX(0.5D), this.getRandomY() - 0.25D, this.getRandomZ(0.5D), (this.random.nextDouble() - 0.5D) * 2.0D, -this.random.nextDouble(), (this.random.nextDouble() - 0.5D) * 2.0D);
                this.level.addParticle(ParticleTypes.PORTAL, this.getRandomX(0.5D), this.getRandomY() - 0.25D, this.getRandomZ(0.5D), (this.random.nextDouble() - 0.5D) * 2.0D, -this.random.nextDouble(), (this.random.nextDouble() - 0.5D) * 2.0D);

                if (this.random.nextInt(100) == 0 && !this.isSilent())
                    this.level.playLocalSound(this.getX() + 0.5D, this.getY() + 0.5D, this.getZ() + 0.5D, SoundEvents.PORTAL_AMBIENT, SoundSource.NEUTRAL, 0.5F, this.random.nextFloat() * 0.4F + 0.8F, false);

            } else if (chickenData.hasTrait == 5) {
                this.level.addParticle(ParticleTypes.FLAME, this.getRandomX(0.5D), this.getRandomY() + 0.7D, this.getRandomZ(0.5D), 0, 0, 0);
                this.level.addParticle(ParticleTypes.SMOKE, this.getRandomX(0.5D), this.getRandomY() + 0.7D, this.getRandomZ(0.5D), 0, 0, 0);

                if (this.random.nextInt(24) == 0 && !this.isSilent())
                    this.level.playLocalSound(this.getX() + 0.5D, this.getY() + 0.5D, this.getZ() + 0.5D, SoundEvents.BLAZE_BURN, SoundSource.NEUTRAL, 1.0F + this.random.nextFloat(), this.random.nextFloat() * 0.7F + 0.3F, false);
            }
        }
    }

    @Override
    public boolean canMate(Animal entityIn) {
        if (CrimsonChickens.CONFIGURATION.masterSwitchBreeding.get() == 0) return false;

        if (this.isInLove() && entityIn.isInLove()) {
            ResourceChickenEntity rce = null;
            if (entityIn instanceof ResourceChickenEntity)
                rce = (ResourceChickenEntity) entityIn;

            if (rce == null) return false;

            if (CrimsonChickens.CONFIGURATION.masterSwitchBreeding.get() == 2) {
                if (! chickenData.canBreed) return false;
                if (! rce.chickenData.canBreed) return false;
            }

            // only ducks can breed with ducks !
            if (chickenData.hasTrait == 1 && rce.chickenData.hasTrait != 1) return false;
            if (chickenData.hasTrait != 1 && rce.chickenData.hasTrait == 1) return false;
            if (chickenData.hasTrait == 1) return true; // simplified !  && rce.chickenData.hasTrait == 1

            // if both chickens are the same...
            if (chickenData.name.equals(rce.chickenData.name)) return true;

            // if breeding with vanilla-replacement chickens...
            if (chickenData.name.equals("chicken") || rce.chickenData.name.equals("chicken"))
                return (CrimsonChickens.CONFIGURATION.allowBreedingWithVanilla.get() > 0);

            return (CrimsonChickens.CONFIGURATION.masterSwitchCrossBreeding.get());
        }

        return false;
    }

    @Override       // TODO: Randomise combining of names?
    public Chicken getBreedOffspring(ServerLevel worldIn, AgeableMob ageableEntity) {
        ResourceChickenEntity rce = null;
        ResourceChickenEntity newChicken = null;

        if (ageableEntity instanceof ResourceChickenEntity)
            rce = (ResourceChickenEntity) ageableEntity;

        if (rce != null) {
            // if both chickens are the same... coal and coal, mr duck and mr duck
            if (chickenData.name.equals(rce.chickenData.name)) {
                if ((newChicken = initEntities.getModChickens().get(chickenData.name).get().create(worldIn)) != null)
                    increaseStats(newChicken, this, rce, worldIn.random);

                return newChicken;
            }

            // if both are ducks... must be different ducks else names would match and wouldn't get this far
            if (chickenData.hasTrait == 1 && rce.chickenData.hasTrait == 1) {
                // chance of getting either a source duck, or a target duck, in case a mod extends my duck?
                // or two or more registered ducks (Mr_Duck and Mrs_Duck eg)
                if (worldIn.random.nextInt(2) == 0)
                    newChicken = initEntities.getModChickens().get(chickenData.name).get().create(worldIn);
                else
                    newChicken = initEntities.getModChickens().get(rce.chickenData.name).get().create(worldIn);

                return newChicken;
            }

            // breeding with Vanilla chicken replacement
            // work out which one is the 'vanilla' chicken so we return the right one if by chance
            if (chickenData.name.equals("chicken")) {
                int r = worldIn.random.nextInt(100) + 1;

                if (r <= CrimsonChickens.CONFIGURATION.allowBreedingWithVanilla.get())
                    newChicken = initEntities.getModChickens().get(rce.chickenData.name).get().create(worldIn);
                else
                    newChicken = initEntities.getModChickens().get("chicken").get().create(worldIn);

                return newChicken;

            } else if (rce.chickenData.name.equals("chicken")) {
                int r = worldIn.random.nextInt(100) + 1;

                if (r <= CrimsonChickens.CONFIGURATION.allowBreedingWithVanilla.get())
                    newChicken = initEntities.getModChickens().get(chickenData.name).get().create(worldIn);
                else
                    newChicken = initEntities.getModChickens().get(rce.chickenData.name).get().create(worldIn);

                return newChicken;
            }

            // Work out cross-breeding types
            if (CrimsonChickens.CONFIGURATION.masterSwitchCrossBreeding.get()) {
                String parentA = this.chickenData.getEntityTypeRegistryID().toString();
                String parentB = ((ResourceChickenEntity) ageableEntity).chickenData.getEntityTypeRegistryID().toString();

                List<String> lst = new ArrayList<>();
                lst.add(this.chickenData.name);
                lst.add(rce.chickenData.name);

                boolean a, b;
                for (Map.Entry<String, RegistryObject<EntityType<? extends ResourceChickenEntity>>> entry : initEntities.getModChickens().entrySet()) {
                    String string = entry.getKey();
                    ResourceChickenData chickenData = ChickenRegistry.getRegistry().getChickenData(string);

                    a = (Objects.equals(chickenData.parentA, parentA) && Objects.equals(chickenData.parentB, parentB));
                    b = (Objects.equals(chickenData.parentA, parentB) && Objects.equals(chickenData.parentB, parentA));

                    if (a || b) {
                        lst.add(string);

                        break;
                    }
                }

                // chance to produce parentA, parentB or crossBreedChild (if there is one)
                worldIn.playSound(null, this.blockPosition(), SoundEvents.CHICKEN_EGG, SoundSource.BLOCKS, 1f, 1f);

                int r = this.level.random.nextInt(lst.size());
                newChicken = initEntities.getModChickens().get(lst.get(r)).get().create(this.level);

                // any new chicken should be 1/1/1
                // stats should only increase when breeding 2 of same chicken/duck

                return newChicken;
            }

            return null;        // this should never be reached?
        }

        return null;        // this should never be reached?
    }

    @Override
    public boolean isFood(ItemStack stack) {
        if (stack.isEmpty()) return false;

        // Can use resource drop as food/breed, *and* regular food (seeds)
        if (CrimsonChickens.CONFIGURATION.masterSwitchBreedingItem.get()) {
            if (stack.hasTag() && !chickenData.dropItemNBT.isEmpty())
                return chickenData.dropItemNBT == stack.getTag();                               // TODO: Test this
            else
                return (stack.getItem().toString().equals(chickenData.dropItemItem));           // MUST use resource drop as food/breed item
        }

        else
            return FOOD_ITEMS.test(stack);
    }

    @Override
    public Component getName() {
        return this.hasCustomName() ? this.getCustomName() : new TextComponent(chickenData.displayName);
    }

    @Override        //TODO: TEST
    public void setInLove(@Nullable Player player) {
        if (player != null) {
            if (player instanceof FakePlayer)
                if (! CrimsonChickens.CONFIGURATION.allowFakeplayerBreeding.get()) return;
        }

        super.setInLove(player);
    }

    @Override        //TODO: TEST
    public void die(DamageSource damageSource) {
        if (damageSource.getEntity() instanceof FakePlayer) return;

        super.die(damageSource);

        if (chickenData.hasTrait == 3) {
            Explosion.BlockInteraction explosion$blockinteraction = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level, this) ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.NONE;
            this.level.explode(this, this.getX(), this.getY(), this.getZ(), 2, explosion$blockinteraction);

            if (! this.level.isClientSide)
                if (damageSource.getEntity() != null) damageSource.getEntity().hurt(new DamageSource("chicken.explode"), 10);
        }
        else if (chickenData.hasTrait == 4) {
            if (! this.level.isClientSide)
                if (damageSource.getEntity() != null) damageSource.getEntity().hurt(new DamageSource("chicken.thorns"), 1);
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
                    if (damageSource.getEntity() != null) damageSource.getEntity().hurt(new DamageSource("chicken.thorns"), 1);
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
        BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos(p_70825_1_, p_70825_3_, p_70825_5_);

        while(blockpos$mutable.getY() > 0 && !this.level.getBlockState(blockpos$mutable).getMaterial().blocksMotion()) {
            blockpos$mutable.move(Direction.DOWN);
        }

        BlockState blockstate = this.level.getBlockState(blockpos$mutable);
        boolean flag = blockstate.getMaterial().blocksMotion();
//        boolean flag1 = blockstate.getFluidState().is(FluidTags.WATER);
//        if (flag && !flag1) {
        if (flag) {
            net.minecraftforge.event.entity.EntityTeleportEvent event = net.minecraftforge.event.ForgeEventFactory.onEnderTeleport(this, p_70825_1_, p_70825_3_, p_70825_5_);
            if (event.isCanceled()) return false;

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

    @Override
    protected void dropFromLootTable(DamageSource damageSource, boolean applyLuckFromLastHurtByPlayer) {
        if (! CrimsonChickens.CONFIGURATION.allowFakeplayerLootDrops.get()) return;

        ResourceLocation resourcelocation;
        if (chickenData.hasTrait == 1)
            resourcelocation = new ResourceLocation(CrimsonChickens.MOD_ID, "entities/duck");
        else
            resourcelocation = new ResourceLocation("minecraft", "entities/chicken");

        LootTable loottable = this.level.getServer().getLootTables().get(resourcelocation);
        LootContext.Builder lootcontext$builder = this.createLootContext(applyLuckFromLastHurtByPlayer, damageSource);
        LootContext ctx = lootcontext$builder.create(LootContextParamSets.ENTITY);

        loottable.getRandomItems(ctx).forEach(this::spawnAtLocation);
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource damageSource, int fortune, boolean p_213333_3_) {
        if (! CrimsonChickens.CONFIGURATION.allowFakeplayerLootDrops.get()) return; // TODO: <- redo this

        //if (chickenData.dropItemItem.isEmpty()) return;

        if (chickenData.name.equals("grave")) {
            // restore player inventory
            // if player has item already in slot then drop item and restore original item
            if (! (damageSource.getEntity() instanceof Player)) return;
            Player playerIn = (Player) damageSource.getEntity();
            Inventory playerInv = playerIn.getInventory();

            ListTag lst = this.getPersistentData().getList("Inventory", 10);

            for(int i = 0; i < lst.size(); ++i) {
                CompoundTag compoundnbt = lst.getCompound(i);
                int j = compoundnbt.getByte("Slot") & 255;
                ItemStack itemstack = ItemStack.of(compoundnbt);

                if (! itemstack.isEmpty()) {
                    if (j < playerInv.items.size()) {
                        if (! playerInv.items.get(j).isEmpty()) playerIn.drop(playerInv.items.get(j), false);  // p_71019_2 is include ThrowerID()
                        playerInv.items.set(j, itemstack);

                    } else if (j >= 100 && j < playerInv.armor.size() + 100) {
                        if (! playerInv.armor.get(j - 100).isEmpty()) playerIn.drop(playerInv.armor.get(j - 100), false);  // p_71019_2 is include ThrowerID()
                        playerInv.armor.set(j - 100, itemstack);

                    } else if (j >= 150 && j < playerInv.offhand.size() + 150) {
                        if (! playerInv.offhand.get(j - 150).isEmpty()) playerIn.drop(playerInv.offhand.get(j - 150), false);  // p_71019_2 is include ThrowerID()
                        playerInv.offhand.set(j - 150, itemstack);
                    }
                }
            }

        } else {

            int r = new Random().nextInt(100) + 1;
            if (r <= CrimsonChickens.CONFIGURATION.allowDeathDropResource.get())
                CrimsonChickens.calcDrops(this.entityData.get(GAIN), chickenData, fortune).forEach(this::spawnAtLocation);
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if (chickenData.hasTrait == 1) return initSounds.DUCK_AMBIENT.get();
        if (chickenData.hasTrait == 9) return initSounds.RADIATION.get();

        return SoundEvents.CHICKEN_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        if (chickenData.hasTrait == 1) return initSounds.DUCK_DEATH.get();
        if (chickenData.hasTrait == 7) return SoundEvents.GHAST_HURT;
        if (chickenData.hasTrait == 8) return SoundEvents.SKELETON_HURT;

        return SoundEvents.CHICKEN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        if (chickenData.hasTrait == 1) return initSounds.DUCK_DEATH.get();
        if (chickenData.hasTrait == 6) return SoundEvents.GLASS_BREAK;
        if (chickenData.hasTrait == 7) return SoundEvents.GHAST_DEATH;
        if (chickenData.hasTrait == 8) return SoundEvents.SKELETON_DEATH;

        return SoundEvents.CHICKEN_DEATH;
    }

    @Override
    public void addProbeEntityInfo(ProbeMode mode, IProbeInfo probeInfo, Player player, Level world, Entity entity, IProbeHitEntityData data) {
        //ResourceChickenEntity chicken = (ResourceChickenEntity) entity;
        if (this.entityData.get(ANALYZED)) {
            probeInfo.text(new TranslatableComponent("tip.crimsonchickens.growth", this.entityData.get(GROWTH)));
            probeInfo.text(new TranslatableComponent("tip.crimsonchickens.gain", this.entityData.get(GAIN)));
            probeInfo.text(new TranslatableComponent("tip.crimsonchickens.strength", this.entityData.get(STRENGTH)));
        }

        if (! this.isBaby()) {
            if (chickenData.eggLayTime != 0) {
                int secs = this.eggTime / 20;
                probeInfo.text(new TranslatableComponent("tip.crimsonchickens.egg", String.format("%02d:%02d", secs / 60, secs % 60)));
            }
        }
    }

    public void addWailaEntityInfo(ITooltip tooltip, EntityAccessor entityAccessor) {
        // todo: remove 'eggDrop' and replace with 'Next Drop'
        //tooltip.clear();        // removes healthbar and egg drop time

        ResourceChickenEntity chicken = (ResourceChickenEntity) entityAccessor.getEntity();
        if (chicken.entityData.get(ANALYZED)) {
            tooltip.add(new TranslatableComponent("tip.crimsonchickens.growth", chicken.entityData.get(GROWTH)));
            tooltip.add(new TranslatableComponent("tip.crimsonchickens.gain", chicken.entityData.get(GAIN)));
            tooltip.add(new TranslatableComponent("tip.crimsonchickens.strength", chicken.entityData.get(STRENGTH)));
        }

//        if (! chicken.isBaby()) {
//            if (chicken.chickenData.eggLayTime != 0) {
//                int secs = chicken.eggTime / 20;
//                tooltip.add(new TranslatableComponent("tip.crimsonchickens.egg", String.format("%02d:%02d", secs / 60, secs % 60)));
//            }
//        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(ANALYZED, true);
        this.entityData.define(GROWTH, 1);
        this.entityData.define(GAIN, 1);
        this.entityData.define(STRENGTH, 1);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);

        // this stops missing nbt causing stats to be 0
        if (compoundNBT.contains("analyzed")) {
            this.entityData.set(ANALYZED, compoundNBT.getBoolean("analyzed"));
            this.entityData.set(GROWTH, compoundNBT.getInt("growth"));
            this.entityData.set(GAIN, compoundNBT.getInt("gain"));
            this.entityData.set(STRENGTH, compoundNBT.getInt("strength"));
        }

        this.eggTime = CrimsonChickens.calcNewEggLayTime(this.random, this.chickenData, this.entityData.get(GROWTH));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);

        compoundNBT.putBoolean("analyzed", this.entityData.get(ANALYZED));
        compoundNBT.putInt("growth", this.entityData.get(GROWTH));
        compoundNBT.putInt("gain", this.entityData.get(GAIN));
        compoundNBT.putInt("strength", this.entityData.get(STRENGTH));
    }

    private static void increaseStats(ResourceChickenEntity newChicken, ResourceChickenEntity parent1, ResourceChickenEntity parent2, Random rand) {
        int parent1Strength = parent1.entityData.get(STRENGTH);
        int parent2Strength = parent2.entityData.get(STRENGTH);

        newChicken.entityData.set(GROWTH, calculateNewStat(parent1Strength, parent2Strength, parent1.entityData.get(GROWTH), parent2.entityData.get(GROWTH), rand));
        newChicken.entityData.set(GAIN, calculateNewStat(parent1Strength, parent2Strength, parent2.entityData.get(GAIN), parent2.entityData.get(GAIN), rand));
        newChicken.entityData.set(STRENGTH, calculateNewStat(parent1Strength, parent2Strength, parent1Strength, parent2Strength, rand));
    }

    private static int calculateNewStat(int thisStrength, int mateStrength, int stat1, int stat2, Random rand) {
        int mutation = rand.nextInt(2) + 1;
        int newStatValue = (stat1 * thisStrength + stat2 * mateStrength) / (thisStrength + mateStrength) + mutation;
        if (newStatValue <= 1) return 1;

        return Math.min(newStatValue, 10);
    }

    private static void inheritStats(ResourceChickenEntity newChicken, ResourceChickenEntity parent) {
        newChicken.entityData.set(GROWTH, parent.entityData.get(GROWTH));
        newChicken.entityData.set(GAIN, parent.entityData.get(GAIN));
        newChicken.entityData.set(STRENGTH, parent.entityData.get(STRENGTH));
    }

    @Override
    public void setAge(int age) {
        super.setAge(calcNewAge(age, this.entityData.get(GROWTH)));
    }

    public int calcNewAge(int age, int growth) {
        int childAge = -24000;
        boolean resetToChild = age == childAge;
        if (resetToChild) age = Math.min(- 1, (childAge * (10 - growth + 1)) / 10);

        int loveAge = 6000;
        boolean resetLoveAfterBreeding = age == loveAge;
        if (resetLoveAfterBreeding) age = Math.max(1, (loveAge * (10 - growth + 1)) / 10);

        return age;
    }

    @Override       // when ResourceChickenEntity collides with another entity
    protected void doPush(Entity entity) {
        entity.push(this);

        if (! this.level.isClientSide) {
            if (this.chickenData.hasTrait == 4) entity.hurt(DamageSource.thorns(entity), 1 + (this.entityData.get(STRENGTH) / 2f));
            if (this.chickenData.hasTrait == 5) entity.setSecondsOnFire(1 + (this.entityData.get(STRENGTH) / 2));
            if (this.chickenData.hasTrait == 9) {
                ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.POISON, 4 * 20));
            }
        }
    }
}

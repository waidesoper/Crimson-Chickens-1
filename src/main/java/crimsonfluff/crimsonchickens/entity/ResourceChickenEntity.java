package crimsonfluff.crimsonchickens.entity;

/**
 * This class uses the stat's system from (increaseStats, inheritStats and calculateNewStat)
 *
 * @setycz's Chickens mod
 * @Licence: MIT
 * @https://www.curseforge.com/minecraft/mc-mods/chickens
 **/

import crimsonfluff.crimsonchickens.CrimsonChickens;
import crimsonfluff.crimsonchickens.init.initItems;
import crimsonfluff.crimsonchickens.init.initRegistry;
import crimsonfluff.crimsonchickens.init.initSounds;
import crimsonfluff.crimsonchickens.json.ResourceChickenData;
import crimsonfluff.crimsonchickens.registry.ChickenRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShearsItem;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ResourceChickenEntity extends ChickenEntity {
    public static final Ingredient FOOD_ITEMS = Ingredient.ofItems(Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.BEETROOT_SEEDS);
    public static final TrackedData<Boolean> ANALYZED = DataTracker.registerData(ResourceChickenEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Integer> GROWTH = DataTracker.registerData(ResourceChickenEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> GAIN = DataTracker.registerData(ResourceChickenEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> STRENGTH = DataTracker.registerData(ResourceChickenEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public ResourceChickenData chickenData;

    // cached for performance (from NBT)
    public String conversionType = "";
    public String conversionDescID = "";
    public int conversionRequired = 0;
    public int conversionCount = 0;
    private int jumpingCooldown;

    //public int eggLayTime;
    private int noJumpDelay;

    public ResourceChickenEntity(EntityType<? extends ChickenEntity> type, World world, ResourceChickenData chickenData) {
        super(type, world);
        this.chickenData = chickenData;

        // cant set in defineSynchedData, NPE
        this.eggLayTime = CrimsonChickens.calcNewEggLayTime(this.random, this.chickenData, 1);
    }

    public static DefaultAttributeContainer.Builder createChickenAttributes(String name) {
        ResourceChickenData chickenData = ChickenRegistry.getRegistry().getChickenData(name);

        return createMobAttributes()
            .add(EntityAttributes.GENERIC_MAX_HEALTH, chickenData.baseHealth)
            .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, chickenData.baseSpeed);
    }

    @Override
    public int getLimitPerChunk() { return 4; }

    @Override
    protected Text getDefaultName() { return new LiteralText(chickenData.displayName); }

    @Override
    public float getPositionTargetRange() { return 10.0f; }

    // Override all main aiStep()s - mainly because calling super causes eggs to be dropped from ChickenEntity.class
    // I want control over EVERYTHING that gets dropped
    @Override
    public void tickMovement() {
        // PassiveEntity.class
        if (this.world.isClient) {
            if (this.happyTicksRemaining > 0) {
                if (this.happyTicksRemaining % 4 == 0) {
                    this.world.addParticle(ParticleTypes.HAPPY_VILLAGER, this.getParticleX(1.0D), this.getRandomBodyY() + 0.5D, this.getParticleZ(1.0D), 0.0D, 0.0D, 0.0D);
                }

                --this.happyTicksRemaining;
            }
        } else if (this.isAlive()) {
            int i = this.getBreedingAge();
            if (i < 0) {
                ++i;
                this.setBreedingAge(i);
            } else if (i > 0) {
                --i;
                this.setBreedingAge(i);
            }
        }

// LivingEntity.class
        if (this.jumpingCooldown > 0) {
            --this.jumpingCooldown;
        }

        if (this.isLogicalSideForUpdatingMovement()) {
            this.bodyTrackingIncrements = 0;
            this.updateTrackedPosition(this.getX(), this.getY(), this.getZ());
        }

        if (this.bodyTrackingIncrements > 0) {
            double d = this.getX() + (this.serverX - this.getX()) / (double)this.bodyTrackingIncrements;
            double e = this.getY() + (this.serverY - this.getY()) / (double)this.bodyTrackingIncrements;
            double f = this.getZ() + (this.serverZ - this.getZ()) / (double)this.bodyTrackingIncrements;
            double g = MathHelper.wrapDegrees(this.serverYaw - (double)this.getYaw());
            this.setYaw((float) ((double)this.getYaw() + g / (double)this.bodyTrackingIncrements));
            this.setPitch((float) ((double)this.getPitch() + (this.serverPitch - (double)this.getPitch()) / (double)this.bodyTrackingIncrements));
            --this.bodyTrackingIncrements;
            this.setPosition(d, e, f);
            this.setRotation(this.getYaw(), this.getPitch());
        } else if (!this.canMoveVoluntarily()) {
            this.setVelocity(this.getVelocity().multiply(0.98D));
        }

        if (this.headTrackingIncrements > 0) {
            this.headYaw = (float)((double)this.headYaw + MathHelper.wrapDegrees(this.serverHeadYaw - (double)this.headYaw) / (double)this.headTrackingIncrements);
            --this.headTrackingIncrements;
        }

        Vec3d d = this.getVelocity();
        double h = d.x;
        double i = d.y;
        double j = d.z;
        if (Math.abs(d.x) < 0.003D) {
            h = 0.0D;
        }

        if (Math.abs(d.y) < 0.003D) {
            i = 0.0D;
        }

        if (Math.abs(d.z) < 0.003D) {
            j = 0.0D;
        }

        this.setVelocity(h, i, j);
        this.world.getProfiler().push("ai");
        if (this.isImmobile()) {
            this.jumping = false;
            this.sidewaysSpeed = 0.0F;
            this.forwardSpeed = 0.0F;
        } else if (this.canMoveVoluntarily()) {
            this.world.getProfiler().push("newAi");
            this.tickNewAi();
            this.world.getProfiler().pop();
        }

        this.world.getProfiler().pop();
        this.world.getProfiler().push("jump");
        if (this.jumping && this.shouldSwimInFluids()) {
            double k;
            if (this.isInLava()) {
                k = this.getFluidHeight(FluidTags.LAVA);
            } else {
                k = this.getFluidHeight(FluidTags.WATER);
            }

            boolean bl = this.isTouchingWater() && k > 0.0D;
            double l = this.getSwimHeight();
            if (!bl || this.onGround && !(k > l)) {
                if (!this.isInLava() || this.onGround && !(k > l)) {
                    if ((this.onGround || bl && k <= l) && this.jumpingCooldown == 0) {
                        this.jump();
                        this.jumpingCooldown = 10;
                    }
                } else {
                    this.swimUpward(FluidTags.LAVA);
                }
            } else {
                this.swimUpward(FluidTags.WATER);
            }
        } else {
            this.jumpingCooldown = 0;
        }

        this.world.getProfiler().pop();
        this.world.getProfiler().push("travel");
        this.sidewaysSpeed *= 0.98F;
        this.forwardSpeed *= 0.98F;
//        this.tickFallFlying();        // Elytra stuffs
        Box k = this.getBoundingBox();
        this.travel(new Vec3d(this.sidewaysSpeed, this.upwardSpeed, this.forwardSpeed));
        this.world.getProfiler().pop();
        this.world.getProfiler().push("push");
        if (this.riptideTicks > 0) {
            --this.riptideTicks;
            this.tickRiptide(k, this.getBoundingBox());
        }

        this.tickCramming();
        this.world.getProfiler().pop();
        if (!this.world.isClient && this.hurtByWater() && this.isWet()) {
            this.damage(DamageSource.DROWN, 1.0F);
        }


// ChickenEntity.class
        this.prevFlapProgress = this.flapProgress;
        this.prevMaxWingDeviation = this.maxWingDeviation;
        this.maxWingDeviation = (float)((double)this.maxWingDeviation + (double)(this.onGround ? -1 : 4) * 0.3D);
        this.maxWingDeviation = MathHelper.clamp(this.maxWingDeviation, 0.0F, 1.0F);
        if (!this.onGround && this.flapSpeed < 1.0F) {
            this.flapSpeed = 1.0F;
        }

        this.flapSpeed = (float)((double)this.flapSpeed * 0.9D);
        Vec3d vec3d = this.getVelocity();
        if (!this.onGround && vec3d.y < 0.0D) {
            this.setVelocity(vec3d.multiply(1.0D, 0.6D, 1.0D));
        }

        this.flapProgress += this.flapSpeed * 2.0F;

        if (this.chickenData.eggLayTime != 0) {
            if (! this.world.isClient && this.isAlive() && ! this.isBaby() && ! this.hasJockey() && -- this.eggLayTime <= 0) {
                CrimsonChickens.calcDrops(this.dataTracker.get(GAIN), chickenData, 0).forEach(this::dropStack);
                this.playSound(SoundEvents.ENTITY_CHICKEN_EGG, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);

                // New egg lay time based on stats
                this.eggLayTime = CrimsonChickens.calcNewEggLayTime(this.random, chickenData, this.dataTracker.get(GROWTH));
            }
        }

        if (this.world.isClient) {
            if (chickenData.hasTrait == 2) {
                this.world.addParticle(ParticleTypes.PORTAL, this.getParticleX(0.5D), this.getRandomBodyY() - 0.25D, this.getParticleZ(0.5D), (this.random.nextDouble() - 0.5D) * 2.0D, - this.random.nextDouble(), (this.random.nextDouble() - 0.5D) * 2.0D);
                this.world.addParticle(ParticleTypes.PORTAL, this.getParticleX(0.5D), this.getRandomBodyY() - 0.25D, this.getParticleZ(0.5D), (this.random.nextDouble() - 0.5D) * 2.0D, - this.random.nextDouble(), (this.random.nextDouble() - 0.5D) * 2.0D);

                if (this.random.nextInt(100) == 0 && ! this.isSilent())
                    this.world.playSound(this.getX() + 0.5D, this.getY() + 0.5D, this.getZ() + 0.5D, SoundEvents.BLOCK_PORTAL_AMBIENT, SoundCategory.NEUTRAL, 0.5F, this.random.nextFloat() * 0.4F + 0.8F, false);

            }
            else if (chickenData.hasTrait == 5) {
                this.world.addParticle(ParticleTypes.FLAME, this.getParticleX(0.5D), this.getRandomBodyY() + 0.7D, this.getParticleZ(0.5D), 0, 0, 0);
                this.world.addParticle(ParticleTypes.SMOKE, this.getParticleX(0.5D), this.getRandomBodyY() + 0.7D, this.getParticleZ(0.5D), 0, 0, 0);

                if (this.random.nextInt(24) == 0 && ! this.isSilent())
                    this.world.playSound(this.getX() + 0.5D, this.getY() + 0.5D, this.getZ() + 0.5D, SoundEvents.ENTITY_BLAZE_BURN, SoundCategory.NEUTRAL, 1.0F + this.random.nextFloat(), this.random.nextFloat() * 0.7F + 0.3F, false);
            }
        }
    }

    @Override
    public boolean canBreedWith(AnimalEntity entityIn) {
        if (CrimsonChickens.CONFIGURATION.masterSwitchBreeding == 0) return false;

        if (this.isInLove() && entityIn.isInLove()) {
            ResourceChickenEntity rce = null;
            if (entityIn instanceof ResourceChickenEntity)
                rce = (ResourceChickenEntity) entityIn;

            if (rce == null) return false;

            if (CrimsonChickens.CONFIGURATION.masterSwitchBreeding == 2) {
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
                return (CrimsonChickens.CONFIGURATION.allowBreedingWithVanilla > 0);

            return (CrimsonChickens.CONFIGURATION.allowCrossBreeding);
        }

        return false;
    }

    @Override       // TODO: Randomise combining of names?
    public ChickenEntity createChild(ServerWorld worldIn, PassiveEntity ageableEntity) {
        ResourceChickenEntity rce = null;
        ResourceChickenEntity newChicken;

        if (ageableEntity instanceof ResourceChickenEntity)
            rce = (ResourceChickenEntity) ageableEntity;

        if (rce != null) {
            // if both chickens are the same... coal and coal, mr duck and mr duck
            if (chickenData.name.equals(rce.chickenData.name)) {
                if ((newChicken = initRegistry.MOD_CHICKENS.get(chickenData.name).create(worldIn)) != null)
                    increaseStats(newChicken, this, rce, worldIn.random);

                return newChicken;
            }

            // if both are ducks... must be different ducks else names would match and wouldn't get this far
            if (chickenData.hasTrait == 1 && rce.chickenData.hasTrait == 1) {
                // chance of getting either a source duck, or a target duck, in case a mod extends my duck?
                // or two or more registered ducks (Mr_Duck and Mrs_Duck eg)
                if (worldIn.random.nextInt(2) == 0)
                    newChicken = initRegistry.MOD_CHICKENS.get(chickenData.name).create(worldIn);
                else
                    newChicken = initRegistry.MOD_CHICKENS.get(rce.chickenData.name).create(worldIn);

                return newChicken;
            }

            // breeding with Vanilla chicken replacement
            if (chickenData.name.equals("chicken")) {
                int r = worldIn.random.nextInt(100) + 1;

                if (r <= CrimsonChickens.CONFIGURATION.allowBreedingWithVanilla)
                    newChicken = initRegistry.MOD_CHICKENS.get(rce.chickenData.name).create(worldIn);
                else
                    newChicken = initRegistry.MOD_CHICKENS.get("chicken").create(worldIn);

                return newChicken;

            }
            else if (rce.chickenData.name.equals("chicken")) {
                int r = worldIn.random.nextInt(100) + 1;

                if (r <= CrimsonChickens.CONFIGURATION.allowBreedingWithVanilla)
                    newChicken = initRegistry.MOD_CHICKENS.get(chickenData.name).create(worldIn);
                else
                    newChicken = initRegistry.MOD_CHICKENS.get(rce.chickenData.name).create(worldIn);

                return newChicken;
            }

            // Work out cross-breeding types
            if (CrimsonChickens.CONFIGURATION.allowCrossBreeding) {
                String parentA = this.chickenData.getEntityTypeRegistryID().toString();
                String parentB = ((ResourceChickenEntity) ageableEntity).chickenData.getEntityTypeRegistryID().toString();

                List<String> lst = new ArrayList<>();
                lst.add(this.chickenData.name);
                lst.add(rce.chickenData.name);

                boolean a, b;
                for (Map.Entry<String, EntityType<? extends ResourceChickenEntity>> entry : initRegistry.MOD_CHICKENS.entrySet()) {
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
                worldIn.playSound(null, this.getBlockPos(), SoundEvents.ENTITY_CHICKEN_EGG, SoundCategory.BLOCKS, 1f, 1f);

                int r = this.world.random.nextInt(lst.size());
                newChicken = initRegistry.MOD_CHICKENS.get(lst.get(r)).create(this.world);
                return newChicken;
            }

            return null;        // this should never be reached?
        }

        return null;        // this should never be reached?
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        if (stack.isEmpty()) return false;

        // Can use resource drop as food/breed, *and* regular food (seeds)
        if (CrimsonChickens.CONFIGURATION.dropAsBreedingItem) {
            if (stack.hasNbt() && ! chickenData.dropItemNBT.isEmpty())
                return chickenData.dropItemNBT == stack.getNbt();                               // TODO: Test this
            else
                return (stack.getItem().toString().equals(chickenData.dropItemItem));           // MUST use resource drop as food/breed item
        }

        else
            return FOOD_ITEMS.test(stack);
    }

    @Override
    public Text getName() {
        return this.hasCustomName() ? this.getCustomName() : new LiteralText(chickenData.displayName);
    }

    @Override        //TODO: TEST
    public void lovePlayer(@Nullable PlayerEntity player) {
//        if (player != null) {
//            if (player instanceof FakePlayer)
//                if (! CrimsonChickens.CONFIGURATION.allowFakeplayerBreeding.get()) return;
//        }

        super.lovePlayer(player);
    }

    @Override        //TODO: TEST
    public void onDeath(DamageSource damageSource) {
//        if (damageSource.getSource() instanceof FakePlayer) return;

        super.onDeath(damageSource);


        if (this.chickenData.name.equals("grave")) {
            // restore player inventory
            // if player has item already in slot then drop item and restore original item
            if (! (damageSource.getSource() instanceof PlayerEntity)) return;
            PlayerEntity playerIn = (PlayerEntity) damageSource.getSource();
            PlayerInventory playerInv = playerIn.getInventory();

//            NbtList lst = this.getPersistentData().getList("Inventory", 10);
            NbtList lst = new NbtList();

            for (int i = 0; i < lst.size(); ++ i) {
                NbtCompound NbtCompound = lst.getCompound(i);
                int j = NbtCompound.getByte("Slot") & 255;
                ItemStack itemstack = ItemStack.fromNbt(NbtCompound);

                if (! itemstack.isEmpty()) {
                    if (j < playerInv.size()) {
                        if (! playerInv.getStack(j).isEmpty())
                            playerIn.dropItem(playerInv.getStack(j), false);  // p_71019_2 is include ThrowerID()
                        playerInv.setStack(j, itemstack);

                    }
                    else if (j >= 100 && j < playerInv.armor.size() + 100) {
                        if (! playerInv.armor.get(j - 100).isEmpty())
                            playerIn.dropItem(playerInv.armor.get(j - 100), false);
                        playerInv.armor.set(j - 100, itemstack);

                    }
                    else if (j >= 150 && j < playerInv.offHand.size() + 150) {
                        if (! playerInv.offHand.get(j - 150).isEmpty())
                            playerIn.dropItem(playerInv.offHand.get(j - 150), false);
                        playerInv.offHand.set(j - 150, itemstack);
                    }
                }
            }
        }

// TODO: explosion size based on chicken strength
        if (chickenData.hasTrait == 3) {
            if (! this.world.isClient) {
                Explosion.DestructionType destructionType = this.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING) ? Explosion.DestructionType.DESTROY : Explosion.DestructionType.NONE;
                this.dead = true;
                this.world.createExplosion(this, this.getX(), this.getY(), this.getZ(), 10, destructionType);
                this.remove(RemovalReason.DISCARDED);

                // TODO:
//                if (damageSource.getSource() != null) damageSource.getSource().damage(new DamageSource("chicken.explode"), 10);
            }
        }
        else if (chickenData.hasTrait == 4) {
//            if (! this.world.isClient)
//                // TODO:
//                if (damageSource.getSource() != null) damageSource.getSource().damage(new DamageSource("chicken.thorns"), 1);
        }
    }

    @Override
    public boolean damage(DamageSource damageSource, float amount) {
        boolean wasHurt = super.damage(damageSource, amount);

//        if (damageSource.getSource() instanceof FakePlayer) return wasHurt;

        if (this.isAlive()) {
            if (chickenData.hasTrait == 2) {
                if (! this.world.isClient && (damageSource.getSource() instanceof LivingEntity) && this.random.nextInt(10) != 0) {
//                this.teleport();
                    for (int i = 0; i < 64; ++ i) {
                        if (this.teleport()) return wasHurt;
                    }

//                LOGGER.info("TELEPORT");
//                return false;
                }
            }
            else if (chickenData.hasTrait == 4) {
//TODO:
                //                if (! this.world.isClient)
//                    if (damageSource.getSource() != null) damageSource.getSource().damage(new DamageSource("chicken.thorns"), 1);
            }
        }

        return wasHurt;
    }

    protected boolean teleport() {
        double d0 = this.getX() + (this.random.nextDouble() - 0.5D) * 64.0D;
        double d1 = this.getY() + (double) (this.random.nextInt(64) - 32);
        double d2 = this.getZ() + (this.random.nextDouble() - 0.5D) * 64.0D;
        return this.teleportTo(d0, d1, d2);
    }

    private boolean teleportTo(double x, double y, double z) {
        BlockPos.Mutable mutable = new BlockPos.Mutable(x, y, z);

        while(mutable.getY() > 0 && !this.world.getBlockState(mutable).getMaterial().blocksMovement()) {
            mutable.move(Direction.DOWN);
        }

        BlockState blockState = this.world.getBlockState(mutable);
        boolean bl = blockState.getMaterial().blocksMovement();
        boolean bl2 = blockState.getFluidState().isIn(FluidTags.WATER);
        if (bl && !bl2) {
            boolean bl3 = this.teleport(x, y, z, true);
            if (bl3 && !this.isSilent()) {
                this.world.playSound(null, this.prevX, this.prevY, this.prevZ, SoundEvents.ENTITY_ENDERMAN_TELEPORT, this.getSoundCategory(), 1.0F, 1.0F);
                this.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
            }

            return bl3;
        } else {
            return false;
        }
    }

    @Override
    protected void dropLoot(DamageSource damageSource, boolean applyLuckFromLastHurtByPlayer) {
        if (! CrimsonChickens.CONFIGURATION.allowFakeplayerLootDrops) return;

        Identifier resourcelocation;
        if (chickenData.hasTrait == 1)
            resourcelocation = new Identifier(CrimsonChickens.MOD_ID, "entities/duck");
        else
            resourcelocation = new Identifier("minecraft", "entities/chicken");

        LootTable loottable = this.world.getServer().getLootManager().getTable(resourcelocation);
        if (loottable != LootTable.EMPTY) {
            LootContext.Builder lootcontext$builder = this.getLootContextBuilder(applyLuckFromLastHurtByPlayer, damageSource);
            LootContext ctx = lootcontext$builder.build(LootContextTypes.ENTITY);

            loottable.generateLoot(ctx).forEach(this::dropStack);
        }
    }

    @Override
    protected void dropEquipment(DamageSource damageSource, int lootingMultiplier, boolean allowDrops) {
        if (! CrimsonChickens.CONFIGURATION.allowFakeplayerLootDrops) return; // TODO: <- redo this

        int r = new Random().nextInt(100) + 1;
        if (r <= CrimsonChickens.CONFIGURATION.allowDeathDropResource)
            CrimsonChickens.calcDrops(this.dataTracker.get(GAIN), chickenData, lootingMultiplier).forEach(this::dropStack);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if (chickenData.hasTrait == 1) return initSounds.DUCK_AMBIENT;
        if (chickenData.hasTrait == 9) return initSounds.RADIATION;

        return SoundEvents.ENTITY_CHICKEN_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        if (chickenData.hasTrait == 1) return initSounds.DUCK_DEATH;
        if (chickenData.hasTrait == 7) return SoundEvents.ENTITY_GHAST_HURT;
        if (chickenData.hasTrait == 8) return SoundEvents.ENTITY_SKELETON_HURT;

        return SoundEvents.ENTITY_CHICKEN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        if (chickenData.hasTrait == 1) return initSounds.DUCK_DEATH;
        if (chickenData.hasTrait == 6) return SoundEvents.BLOCK_GLASS_BREAK;
        if (chickenData.hasTrait == 7) return SoundEvents.ENTITY_GHAST_DEATH;
        if (chickenData.hasTrait == 8) return SoundEvents.ENTITY_SKELETON_DEATH;

        return SoundEvents.ENTITY_CHICKEN_DEATH;
    }

//    @Override
//    public void addProbeEntityInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, Entity entity, IProbeHitdataTracker data) {
//        if (this.dataTracker.get(ANALYZED)) {
//            probeInfo.text(new TranslatableText("tip.crimsonchickens.growth", this.dataTracker.get(GROWTH)));
//            probeInfo.text(new TranslatableText("tip.crimsonchickens.gain", this.dataTracker.get(GAIN)));
//            probeInfo.text(new TranslatableText("tip.crimsonchickens.strength", this.dataTracker.get(STRENGTH)));
//        }
//
//        if (! this.isBaby()) {
//            if (this.chickenData.eggLayTime != 0) {
//                //int secs = this.eggLayTime / 20;
//                probeInfo.text(new TranslatableText("tip.crimsonchickens.egg", CrimsonChickens.formatTime(this.eggLayTime)));
//            }
//        }
//
//        if (this.conversionCount != 0) {
//            probeInfo.text(new TranslatableText("tip.crimsonchickens.conv", new TranslatableText(this.conversionDescID)));
//            probeInfo.progress(this.conversionCount, this.conversionRequired);
//        }
//    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();

        this.dataTracker.startTracking(ANALYZED, true);
        this.dataTracker.startTracking(GROWTH, 1);
        this.dataTracker.startTracking(GAIN, 1);
        this.dataTracker.startTracking(STRENGTH, 1);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound NbtCompound) {
        super.readCustomDataFromNbt(NbtCompound);

        // this stops missing nbt causing stats to be 0
        if (NbtCompound.contains("analyzed")) {
            this.dataTracker.set(ANALYZED, NbtCompound.getBoolean("analyzed"));
            this.dataTracker.set(GROWTH, NbtCompound.getInt("growth"));
            this.dataTracker.set(GAIN, NbtCompound.getInt("gain"));
            this.dataTracker.set(STRENGTH, NbtCompound.getInt("strength"));
        }

        if (NbtCompound.contains("Mutation")) {
            NbtCompound nbt = NbtCompound.getCompound("Mutation");
            this.conversionCount = nbt.getInt("count");
            this.conversionRequired = nbt.getInt("req");
            this.conversionType = nbt.getString("type");
            this.conversionDescID = Registry.ITEM.get(new Identifier(this.conversionType)).getTranslationKey();
        }

        this.eggLayTime = CrimsonChickens.calcNewEggLayTime(this.random, this.chickenData, this.dataTracker.get(GROWTH));
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound NbtCompound) {
        super.writeCustomDataToNbt(NbtCompound);

        NbtCompound.putBoolean("analyzed", this.dataTracker.get(ANALYZED));
        NbtCompound.putInt("growth", this.dataTracker.get(GROWTH));
        NbtCompound.putInt("gain", this.dataTracker.get(GAIN));
        NbtCompound.putInt("strength", this.dataTracker.get(STRENGTH));

        if (this.conversionCount != 0) {
            NbtCompound nbt = new NbtCompound();
            nbt.putInt("count", this.conversionCount);
            nbt.putInt("req", this.conversionRequired);
            nbt.putString("type", this.conversionType);
            NbtCompound.put("Mutation", nbt);
        }
    }

    private static void increaseStats(ResourceChickenEntity newChicken, ResourceChickenEntity parent1, ResourceChickenEntity parent2, Random rand) {
        int parent1Strength = parent1.dataTracker.get(STRENGTH);
        int parent2Strength = parent2.dataTracker.get(STRENGTH);

        newChicken.dataTracker.set(GROWTH, calculateNewStat(parent1Strength, parent2Strength, parent1.dataTracker.get(GROWTH), parent2.dataTracker.get(GROWTH), rand));
        newChicken.dataTracker.set(GAIN, calculateNewStat(parent1Strength, parent2Strength, parent2.dataTracker.get(GAIN), parent2.dataTracker.get(GAIN), rand));
        newChicken.dataTracker.set(STRENGTH, calculateNewStat(parent1Strength, parent2Strength, parent1Strength, parent2Strength, rand));
    }

    private static int calculateNewStat(int thisStrength, int mateStrength, int stat1, int stat2, Random rand) {
        int mutation = rand.nextInt(2) + 1;
        int newStatValue = (stat1 * thisStrength + stat2 * mateStrength) / (thisStrength + mateStrength) + mutation;
        if (newStatValue <= 1) return 1;

        return Math.min(newStatValue, 10);
    }

    private static void inheritStats(ResourceChickenEntity newChicken, ResourceChickenEntity parent) {
        newChicken.dataTracker.set(GROWTH, parent.dataTracker.get(GROWTH));
        newChicken.dataTracker.set(GAIN, parent.dataTracker.get(GAIN));
        newChicken.dataTracker.set(STRENGTH, parent.dataTracker.get(STRENGTH));
    }

    @Override
    public void growUp(int age) {
        super.growUp(calcNewAge(age, this.dataTracker.get(GROWTH)), false);
    }

    public int calcNewAge(int age, int growth) {
        int childAge = - 24000;
        boolean resetToChild = age == childAge;
        if (resetToChild) age = Math.min(- 1, (childAge * (10 - growth + 1)) / 10);

        int loveAge = 6000;
        boolean resetLoveAfterBreeding = age == loveAge;
        if (resetLoveAfterBreeding) age = Math.max(1, (loveAge * (10 - growth + 1)) / 10);

        return age;
    }

    @Override       // when ResourceChickenEntity collides with another entity
    protected void pushAway(Entity entity) {
        entity.pushAwayFrom(this);

        if (! this.world.isClient) {
            if (this.chickenData.hasTrait == 4) entity.damage(DamageSource.thorns(entity), 1 + (this.dataTracker.get(STRENGTH) / 2f));
            if (this.chickenData.hasTrait == 5) entity.setOnFireFor(1 + (this.dataTracker.get(STRENGTH) / 2));
            if (this.chickenData.hasTrait == 9) ((LivingEntity) entity).addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 4 * 20));
        }
    }

    @Override       // shearing and converting
    public ActionResult interactMob(PlayerEntity playerIn, Hand handIn) {
        // fired once per hand, client & server
        if ((! playerIn.world.isClient) && playerIn.getActiveHand() == handIn) {
            ItemStack itemStack = playerIn.getMainHandStack();

            if (! itemStack.isEmpty()) {
                if (FOOD_ITEMS.test(itemStack))
                    return super.interactMob(playerIn, handIn);     // mainly controls food

                if (itemStack.getItem() instanceof ShearsItem) {
                    if (CrimsonChickens.CONFIGURATION.allowShearingChickens) {
                        itemStack.damage(1, playerIn, plyr -> plyr.sendToolBreakStatus(handIn));

                        World world = playerIn.world;
                        BlockPos pos = playerIn.getBlockPos();

                        // TODO:
//                        this.damage(new DamageSource("death.attack.shears"), 1);
                        ((ServerWorld) world).spawnParticles(ParticleTypes.CRIT, pos.getX(), pos.getY() + this.getEyeY(), pos.getZ(), 10, 0.5, 0.5, 0.5, 0);

                        world.playSound(null, pos, SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.PLAYERS, 1f, 1f);

                        world.spawnEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(),
                            this.chickenData.hasTrait == 1
                                ? new ItemStack(initItems.FEATHER_DUCK)
                                : new ItemStack(Items.FEATHER)));

                        return ActionResult.SUCCESS;
                    }

                    return ActionResult.FAIL;
                }

                // Conversion only works on 'Vanilla' chickens
                if (! CrimsonChickens.CONFIGURATION.allowConvertingVanilla) return ActionResult.FAIL;
                if (! this.chickenData.name.equals("chicken")) return ActionResult.FAIL;

                // loop thru registry and find dropItem that matches item player is holding (itemStack)
                for (Map.Entry<String, EntityType<? extends ResourceChickenEntity>> entry : initRegistry.MOD_CHICKENS.entrySet()) {
                    String name = entry.getKey();
                    ResourceChickenData chickenData = ChickenRegistry.getRegistry().getChickenData(name);

//                    playerIn.displayClientMessage(new StringTextComponent(chickenData.dropItemItem).append(" : ").append(itemStack.getItem().getRegistryName().toString()), false);

                    if (chickenData.dropItemItem.equals(Registry.ITEM.getId(itemStack.getItem()).toString())) {
                        if (! playerIn.isCreative()) itemStack.decrement(1);

                        if (this.conversionCount % 4 == 0) {
                            ((ServerWorld) playerIn.world).spawnParticles(ParticleTypes.HAPPY_VILLAGER,
                                this.getX(), this.getY() + 0.5, this.getZ(),
                                50, 0.5, 0.5, 0.5, 0);
                        }

                        // if converting a partly already converted chicken then reset/remove type/count
                        if (! chickenData.dropItemItem.equals(this.conversionType)) {
                            // TODO:
                            // this.getPersistentData().remove("Mutation");

                            NbtCompound nbtCompound = this.writeNbt(new NbtCompound());
                            nbtCompound.remove("Mutation");
                            this.readNbt(nbtCompound);

                            this.conversionCount = 1;
                            this.conversionRequired = chickenData.conversion;
                            this.conversionType = chickenData.dropItemItem;
                            this.conversionDescID = Registry.ITEM.get(new Identifier(this.conversionType)).getTranslationKey();
                        }
                        else {
                            this.conversionCount++;
                        }

                        if (this.conversionCount >= chickenData.conversion) {
                            this.remove(RemovalReason.DISCARDED);

                            playerIn.world.playSound(null, this.getBlockPos(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.AMBIENT, 1f, 1f);
                            ((ServerWorld) playerIn.world).spawnParticles(ParticleTypes.POOF,
                                this.getX(), this.getY() + 0.5, this.getZ(),100, 1, 1, 1, 0);

                            ResourceChickenEntity newChick = initRegistry.MOD_CHICKENS.get(chickenData.name).create(playerIn.world);
                            if (newChick != null) {
                                newChick.copyFrom(this);
                                newChick.setUuid(UUID.randomUUID());        // TODO: remove uuid,  Duplicate ID error when spawning

                                NbtCompound nbtCompound = newChick.writeNbt(new NbtCompound());
                                nbtCompound.remove("Mutation");
                                newChick.readNbt(nbtCompound);

                                //playerIn.sendMessage(new LiteralText(nbtCompound.asString()), false);

                                if (this.hasCustomName()) {
                                    newChick.setCustomName(this.getCustomName());
                                    newChick.setCustomNameVisible(this.isCustomNameVisible());
                                }

                                newChick.setInvulnerable(this.isInvulnerable());

//                        newChick.setYHeadRot(targetChicken.getYHeadRot());
                                playerIn.world.spawnEntity(newChick);
                            }
                        }

                        return ActionResult.SUCCESS;
                    }
                }

                return ActionResult.FAIL;
            }
        }

        return super.interactMob(playerIn, handIn);     // mainly controls food
    }
}

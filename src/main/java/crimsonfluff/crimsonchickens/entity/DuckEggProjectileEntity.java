package crimsonfluff.crimsonchickens.entity;

import crimsonfluff.crimsonchickens.client.DuckEggProjectileSpawnPacket;
import crimsonfluff.crimsonchickens.client.initClient;
import crimsonfluff.crimsonchickens.init.initItems;
import crimsonfluff.crimsonchickens.init.initRegistry;
import crimsonfluff.crimsonchickens.registry.ChickenRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class DuckEggProjectileEntity extends ThrownItemEntity implements FlyingItemEntity {
    public DuckEggProjectileEntity(EntityType<? extends DuckEggProjectileEntity> p_i50154_1_, World p_i50154_2_) {
        super(p_i50154_1_, p_i50154_2_);
    }

    public DuckEggProjectileEntity(World p_i1780_1_, LivingEntity p_i1780_2_) {
        super(initRegistry.DUCK_EGG, p_i1780_2_, p_i1780_1_);
    }

    public DuckEggProjectileEntity(World p_i1781_1_, double p_i1781_2_, double p_i1781_4_, double p_i1781_6_) {
        super(initRegistry.DUCK_EGG, p_i1781_2_, p_i1781_4_, p_i1781_6_, p_i1781_1_);
    }

    // or it will not show break particles
    @Environment(EnvType.CLIENT)
    public void handleStatus(byte status) {
        if (status == 3) {
            for(int i = 0; i < 8; ++i) {
                this.world.addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, this.getStack()), this.getX(), this.getY(), this.getZ(), ((double)this.random.nextFloat() - 0.5D) * 0.08D, ((double)this.random.nextFloat() - 0.5D) * 0.08D, ((double)this.random.nextFloat() - 0.5D) * 0.08D);
            }
        }
    }

    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        entityHitResult.getEntity().damage(DamageSource.thrownProjectile(this, this.getOwner()), 0.0F);
    }

    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (! this.world.isClient) {
            if (this.random.nextInt(8) == 0) {
                int i = 1;
                if (this.random.nextInt(32) == 0) i = 4;

                //search ChickenRegistry for all duck types and randomly choose one to spawn
                List<String> lst = new ArrayList<>();
                ChickenRegistry.getRegistry().getChickens().forEach((s, chicken) -> {
                    if (chicken.hasTrait == 1) lst.add(s);
                });

                if (lst.size() != 0) {
                    ResourceChickenEntity duck = initRegistry.MOD_CHICKENS.get(lst.get(this.random.nextInt(lst.size()))).create(this.world);

                    if (duck != null) {
                        for (int j = 0; j < i; ++ j) {
                            duck.setBreedingAge(- 24000);
                            duck.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.yaw, 0.0F);
                            this.world.spawnEntity(duck);
                        }
                    }
                }
            }

            this.world.sendEntityStatus(this, (byte)3);
            this.remove();
        }
    }

    @Override
    public ItemStack getItem() {return new ItemStack(initItems.EGG_DUCK);}

    @Override
    protected Item getDefaultItem() {return initItems.EGG_DUCK;}

    @Override
    public Packet createSpawnPacket() {
        return DuckEggProjectileSpawnPacket.create(this, initClient.DUCK_EGG_SPAWN_PACKET);
    }
}

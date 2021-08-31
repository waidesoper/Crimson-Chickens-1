package crimsonfluff.crimsonchickens.entity;

import crimsonfluff.crimsonchickens.init.initEntities;
import crimsonfluff.crimsonchickens.init.initItems;
import crimsonfluff.crimsonchickens.registry.ChickenRegistry;
import crimsonfluff.crimsonchickens.registry.RegistryHandler;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class DuckEggProjectileEntity extends ThrowableItemProjectile {
    public DuckEggProjectileEntity(EntityType<? extends ThrowableItemProjectile> p_i50154_1_, Level p_i50154_2_) {
        super(p_i50154_1_, p_i50154_2_);
    }

    public DuckEggProjectileEntity(Level p_i1780_1_, LivingEntity p_i1780_2_) {
        super(RegistryHandler.DUCK_EGG.get(), p_i1780_2_, p_i1780_1_);
    }

    // or it will not show break particles
    @OnlyIn(Dist.CLIENT)
    @Override
    public void handleEntityEvent(byte p_70103_1_) {
        if (p_70103_1_ == 3) {
            for(int i = 0; i < 8; ++i) {
                this.level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, this.getItem()), this.getX(), this.getY(), this.getZ(), ((double)this.random.nextFloat() - 0.5D) * 0.08D, ((double)this.random.nextFloat() - 0.5D) * 0.08D, ((double)this.random.nextFloat() - 0.5D) * 0.08D);
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult p_213868_1_) {
        super.onHitEntity(p_213868_1_);
        p_213868_1_.getEntity().hurt(DamageSource.thrown(this, this.getOwner()), 0.0F);
    }

    @Override
    protected void onHit(HitResult p_70227_1_) {
        super.onHit(p_70227_1_);
        if (!this.level.isClientSide) {
            if (this.random.nextInt(8) == 0) {
                int i = 1;
                if (this.random.nextInt(32) == 0) i = 4;

                //search ChickenRegistry for all duck types and randomly choose one to spawn
                List<String> lst = new ArrayList<>();
                ChickenRegistry.getRegistry().getChickens().forEach((s, chicken) -> {
                    if (chicken.hasTrait == 1) lst.add(s);
                });

                if (lst.size() != 0) {
                    ResourceChickenEntity duck = initEntities.getModChickens()
                        .get(lst.get(this.random.nextInt(lst.size()))).get().create(this.level);

                    for (int j = 0; j < i; ++ j) {
                        duck.setAge(- 24000);
                        duck.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
                        this.level.addFreshEntity(duck);
                    }
                }
            }

            this.level.broadcastEntityEvent(this, (byte)3);
            this.remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    public ItemStack getItem() { return new ItemStack(initItems.EGG_DUCK.get()); }

    @Override
    protected Item getDefaultItem() { return initItems.EGG_DUCK.get(); }

    @Nonnull
    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}

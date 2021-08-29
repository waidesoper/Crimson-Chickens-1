package crimsonfluff.crimsonchickens.entity;

import crimsonfluff.crimsonchickens.init.initEntities;
import crimsonfluff.crimsonchickens.init.initItems;
import crimsonfluff.crimsonchickens.registry.ChickenRegistry;
import crimsonfluff.crimsonchickens.registry.RegistryHandler;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class DuckEggProjectileEntity extends ProjectileItemEntity {
    public DuckEggProjectileEntity(EntityType<? extends DuckEggProjectileEntity> p_i50154_1_, World p_i50154_2_) {
        super(p_i50154_1_, p_i50154_2_);
    }

    public DuckEggProjectileEntity(World p_i1780_1_, LivingEntity p_i1780_2_) {
        super(RegistryHandler.DUCK_EGG.get(), p_i1780_2_, p_i1780_1_);
    }

//    @OnlyIn(Dist.CLIENT)
    public DuckEggProjectileEntity(World p_i1781_1_, double p_i1781_2_, double p_i1781_4_, double p_i1781_6_) {
        super(RegistryHandler.DUCK_EGG.get(), p_i1781_2_, p_i1781_4_, p_i1781_6_, p_i1781_1_);
    }

    // or it will not show break particles
    @OnlyIn(Dist.CLIENT)
    @Override
    public void handleEntityEvent(byte p_70103_1_) {
        if (p_70103_1_ == 3) {
            for(int i = 0; i < 8; ++i) {
                this.level.addParticle(new ItemParticleData(ParticleTypes.ITEM, this.getItem()), this.getX(), this.getY(), this.getZ(), ((double)this.random.nextFloat() - 0.5D) * 0.08D, ((double)this.random.nextFloat() - 0.5D) * 0.08D, ((double)this.random.nextFloat() - 0.5D) * 0.08D);
            }
        }
    }

    @Override
    protected void onHitEntity(EntityRayTraceResult p_213868_1_) {
        super.onHitEntity(p_213868_1_);
        p_213868_1_.getEntity().hurt(DamageSource.thrown(this, this.getOwner()), 0.0F);
    }

    @Override
    protected void onHit(RayTraceResult p_70227_1_) {
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
                        duck.moveTo(this.getX(), this.getY(), this.getZ(), this.yRot, 0.0F);
                        this.level.addFreshEntity(duck);
                    }
                }
            }

            this.level.broadcastEntityEvent(this, (byte)3);
            this.remove();
        }
    }

    @Override
    public ItemStack getItem() { return new ItemStack(initItems.EGG_DUCK.get()); }

    @Override
    protected Item getDefaultItem() { return initItems.EGG_DUCK.get(); }

    // or it will not render !
    @Override
    public IPacket<?> getAddEntityPacket() { return NetworkHooks.getEntitySpawningPacket(this); }
}

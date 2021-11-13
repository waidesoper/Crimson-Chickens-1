package crimsonfluff.crimsonchickens.compat.waila;

import crimsonfluff.crimsonchickens.entity.ResourceChickenEntity;
import mcp.mobius.waila.api.IServerDataProvider;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public class WailaChickenEntityProvider implements IServerDataProvider<LivingEntity> {
    // data that is constantly changing should be put into ServerDataCompoundTag
    // this ServerDataCompoundTag is read from NestComponentProvider

    @Override
    public void appendServerData(NbtCompound compoundNBT, ServerPlayerEntity player, World world, LivingEntity entity) {
        if (entity instanceof ResourceChickenEntity) {
            ResourceChickenEntity chicken = (ResourceChickenEntity) entity;

            //compoundNBT.putInt("chickenAge", chicken.getAge());
            compoundNBT.putInt("eggLayTime", chicken.chickenData.eggLayTime);
            compoundNBT.putInt("eggTime", chicken.eggLayTime);

            if (chicken.conversionCount != 0) {
                NbtCompound nbt = new NbtCompound();
                nbt.putInt("count", chicken.conversionCount);
                nbt.putInt("req", chicken.conversionRequired);
                nbt.putString("type", chicken.conversionType);
                compoundNBT.put("Mutation", nbt);
            }
        }
    }
}

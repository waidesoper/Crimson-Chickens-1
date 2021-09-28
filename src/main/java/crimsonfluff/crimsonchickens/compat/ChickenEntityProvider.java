package crimsonfluff.crimsonchickens.compat;

import crimsonfluff.crimsonchickens.entity.ResourceChickenEntity;
import mcp.mobius.waila.api.IServerDataProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

public class ChickenEntityProvider implements IServerDataProvider<Entity> {
    // data that is constantly changing should be put into ServerDataCompoundTag
    // this ServerDataCompoundTag is read from NestComponentProvider

    @Override
    public void appendServerData(CompoundNBT compoundNBT, ServerPlayerEntity player, World world, Entity entity) {
        if (entity instanceof ResourceChickenEntity) {
            ResourceChickenEntity chicken = (ResourceChickenEntity) entity;

            //compoundNBT.putInt("chickenAge", chicken.getAge());
            compoundNBT.putInt("eggLayTime", chicken.chickenData.eggLayTime);
            compoundNBT.putInt("eggTime", chicken.eggTime);

            if (chicken.conversionCount != 0) {
                CompoundNBT nbt = new CompoundNBT();
                nbt.putInt("count", chicken.conversionCount);
                nbt.putInt("req", chicken.conversionRequired);
                nbt.putString("type", chicken.conversionType);
                compoundNBT.put("Mutation", nbt);
            }
        }
    }
}

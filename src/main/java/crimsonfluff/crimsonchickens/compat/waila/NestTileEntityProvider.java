package crimsonfluff.crimsonchickens.compat.waila;

import crimsonfluff.crimsonchickens.blocks.NestTileEntity;
import mcp.mobius.waila.api.IServerDataProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public class NestTileEntityProvider implements IServerDataProvider<BlockEntity> {
    // data that is constantly changing should be put into ServerDataCompoundTag
    // this ServerDataCompoundTag is read from NestComponentProvider

    @Override
    public void appendServerData(NbtCompound compoundNBT, ServerPlayerEntity player, World world, BlockEntity tileEntity) {
        if (tileEntity instanceof NestTileEntity) {
            NestTileEntity tile = (NestTileEntity) tileEntity;

            if (tile.entityCaptured != null) {
                compoundNBT.putInt("chickenAge", tile.chickenAge);
                compoundNBT.putInt("eggLayTime", tile.eggLayTime);
                compoundNBT.putBoolean("requiresSeeds", tile.getStack(0).isEmpty());
            }
        }
    }
}

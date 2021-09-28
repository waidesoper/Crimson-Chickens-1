package crimsonfluff.crimsonchickens.compat;

import crimsonfluff.crimsonchickens.blocks.NestTileEntity;
import mcp.mobius.waila.api.IServerDataProvider;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class NestTileEntityProvider implements IServerDataProvider<TileEntity> {
    // data that is constantly changing should be put into ServerDataCompoundTag
    // this ServerDataCompoundTag is read from NestComponentProvider

    @Override
    public void appendServerData(CompoundNBT compoundNBT, ServerPlayerEntity player, World world, TileEntity tileEntity) {
        if (tileEntity instanceof NestTileEntity) {
            NestTileEntity tile = (NestTileEntity) tileEntity;

            if (tile.entityCaptured != null) {
                compoundNBT.putInt("chickenAge", tile.chickenAge);
                compoundNBT.putInt("eggLayTime", tile.eggLayTime);
                compoundNBT.putBoolean("requiresSeeds", tile.storedItems.getStackInSlot(0).isEmpty());
            }
        }
    }
}

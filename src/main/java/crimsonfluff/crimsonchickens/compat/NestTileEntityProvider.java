package crimsonfluff.crimsonchickens.compat;

import crimsonfluff.crimsonchickens.blocks.NestTileEntity;
import mcp.mobius.waila.api.IServerDataProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class NestTileEntityProvider implements IServerDataProvider<BlockEntity> {
    // data that is constantly changing should be put into ServerDataCompoundTag
    // this ServerDataCompoundTag is read from NestComponentProvider
    @Override
    public void appendServerData(CompoundTag compoundTag, ServerPlayer serverPlayer, Level level, BlockEntity tileEntity, boolean showDetails) {
        if (tileEntity instanceof NestTileEntity tile) {
            if (tile.entityCaptured != null) {
                compoundTag.putInt("chickenAge", tile.chickenAge);
                compoundTag.putInt("eggLayTime", tile.eggLayTime);
                compoundTag.putBoolean("requiresSeeds", tile.storedItems.getStackInSlot(0).isEmpty());
            }
        }
    }
}

package crimsonfluff.crimsonchickens.compat;

import crimsonfluff.crimsonchickens.blocks.NestTileEntity;
import mcp.mobius.waila.api.BlockAccessor;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.config.IPluginConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class NestComponentProvider implements IComponentProvider {
    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (accessor.getBlockEntity() instanceof NestTileEntity tile) {
            if (tile.entityCaptured != null) {
                CompoundTag data = accessor.getServerData();                // read changing tile information from NestTileEntityProvider$ServerDataTag
                tooltip.add(new TextComponent(tile.entityDescription));

                if (tile.entityCustomName != null)
                    tooltip.add(tile.entityCustomName);

                if (tile.entityCaptured.getBoolean("analyzed")) {
                    tooltip.add(new TranslatableComponent("tip.crimsonchickens.growth", tile.chickenGrowth));
                    tooltip.add(new TranslatableComponent("tip.crimsonchickens.gain", tile.chickenGain));
                    tooltip.add(new TranslatableComponent("tip.crimsonchickens.strength", tile.chickenStrength));
                }

                int chickenAge = data.getInt("chickenAge");
                int eggLayTime = data.getInt("eggLayTime");
                boolean requiresSeeds = data.getBoolean("requiresSeeds");

                int secs;
                if (chickenAge < 0) {
                    secs = -chickenAge / 20;
                    tooltip.add(new TranslatableComponent("tip.crimsonchickens.growing", String.format("%02d:%02d", secs / 60, secs % 60)));

                } else {
                    if (requiresSeeds) {
                        tooltip.add(new TranslatableComponent("tip.crimsonchickens.seeds").withStyle(ChatFormatting.YELLOW));

                    } else {
                        if (eggLayTime != 0) {
                            secs = eggLayTime / 20;
                            tooltip.add(new TranslatableComponent("tip.crimsonchickens.egg", String.format("%02d:%02d", secs / 60, secs % 60)));
                        }
                    }
                }
            }
        }
    }
}

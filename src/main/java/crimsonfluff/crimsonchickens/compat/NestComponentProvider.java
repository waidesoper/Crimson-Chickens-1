package crimsonfluff.crimsonchickens.compat;

import crimsonfluff.crimsonchickens.blocks.NestTileEntity;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IDataAccessor;
import mcp.mobius.waila.api.IPluginConfig;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

public class NestComponentProvider implements IComponentProvider {
    @Override
    public void appendBody(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
        if (accessor.getTileEntity() instanceof NestTileEntity) {
            NestTileEntity tile = (NestTileEntity) accessor.getTileEntity();

            if (tile.entityCaptured != null) {
                CompoundNBT data = accessor.getServerData();                // read changing tile information from NestTileEntityProvider$ServerDataTag
                tooltip.add(new StringTextComponent(tile.entityDescription));

                if (tile.entityCustomName != null)
                    tooltip.add(tile.entityCustomName);

                if (tile.entityCaptured.getBoolean("analyzed")) {
                    tooltip.add(new TranslationTextComponent("tip.crimsonchickens.growth", tile.chickenGrowth));
                    tooltip.add(new TranslationTextComponent("tip.crimsonchickens.gain", tile.chickenGain));
                    tooltip.add(new TranslationTextComponent("tip.crimsonchickens.strength", tile.chickenStrength));
                }

                int chickenAge = data.getInt("chickenAge");
                int eggLayTime = data.getInt("eggLayTime");
                boolean requiresSeeds = data.getBoolean("requiresSeeds");

                int secs;
                if (chickenAge < 0) {
                    secs = -chickenAge / 20;
                    tooltip.add(new TranslationTextComponent("tip.crimsonchickens.growing", String.format("%02d:%02d", secs / 60, secs % 60)));

                } else {
                    if (requiresSeeds) {
                        tooltip.add(new TranslationTextComponent("tip.crimsonchickens.seeds").withStyle(TextFormatting.YELLOW));

                    } else {
                        if (eggLayTime != 0) {
                            secs = eggLayTime / 20;
                            tooltip.add(new TranslationTextComponent("tip.crimsonchickens.egg", String.format("%02d:%02d", secs / 60, secs % 60)));
                        }
                    }
                }
            }
        }
    }
}

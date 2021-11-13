package crimsonfluff.crimsonchickens.compat.waila;

import crimsonfluff.crimsonchickens.CrimsonChickens;
import crimsonfluff.crimsonchickens.blocks.NestTileEntity;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IDataAccessor;
import mcp.mobius.waila.api.IPluginConfig;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import java.util.List;

public class NestComponentProvider implements IComponentProvider {
    @Override
    public void appendBody(List<Text> tooltip, IDataAccessor accessor, IPluginConfig config) {
        if (accessor.getBlockEntity() instanceof NestTileEntity) {
            NestTileEntity tile = (NestTileEntity) accessor.getBlockEntity();

            if (tile.entityCaptured != null) {
                NbtCompound data = accessor.getServerData();                // read changing tile information from NestTileEntityProvider$ServerDataTag
                tooltip.add(new LiteralText(tile.entityDescription));

                if (tile.entityCustomName != null)
                    tooltip.add(new LiteralText('"' + tile.entityCustomName.getString() + '"').formatted(Formatting.ITALIC));

                if (tile.entityCaptured.getBoolean("analyzed")) {
                    tooltip.add(new TranslatableText("tip.crimsonchickens.growth", tile.chickenGrowth));
                    tooltip.add(new TranslatableText("tip.crimsonchickens.gain", tile.chickenGain));
                    tooltip.add(new TranslatableText("tip.crimsonchickens.strength", tile.chickenStrength));
                }

                int chickenAge = data.getInt("chickenAge");
                int eggLayTime = data.getInt("eggLayTime");
                boolean requiresSeeds = data.getBoolean("requiresSeeds");

                if (chickenAge < 0) {
                    tooltip.add(new TranslatableText("tip.crimsonchickens.growing", CrimsonChickens.formatTime(-chickenAge)));

                } else {
                    if (requiresSeeds) {
                        tooltip.add(new TranslatableText("tip.crimsonchickens.seeds").formatted(Formatting.YELLOW));

                    } else {
                        if (eggLayTime != 0) {
                            tooltip.add(new TranslatableText("tip.crimsonchickens.egg", CrimsonChickens.formatTime(eggLayTime)));
                        }
                    }
                }
            }
        }
    }
}

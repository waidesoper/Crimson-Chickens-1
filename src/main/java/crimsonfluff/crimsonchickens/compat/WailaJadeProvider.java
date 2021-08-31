package crimsonfluff.crimsonchickens.compat;

import crimsonfluff.crimsonchickens.blocks.Nest;
import crimsonfluff.crimsonchickens.blocks.NestTileEntity;
import mcp.mobius.waila.api.BlockAccessor;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.config.IPluginConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class WailaJadeProvider implements IComponentProvider {
    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        //((Nest) blockAccessor.getBlock()).AddWailaInfo(iTooltip, blockAccessor);

        NestTileEntity te = (NestTileEntity) blockAccessor.getBlockEntity(); //blockAccessor.getLevel().getBlockEntity(blockAccessor.getPosition());
        if (te == null) return;

        if (te.entityCaptured != null) {
            iTooltip.add(new TextComponent(te.entityDescription));

            if (te.entityCustomName != null)
                iTooltip.add(te.entityCustomName);

            if (te.entityCaptured.getBoolean("analyzed")) {
                iTooltip.add(new TranslatableComponent("tip.crimsonchickens.growth", te.chickenGrowth));
                iTooltip.add(new TranslatableComponent("tip.crimsonchickens.gain", te.chickenGain));
                iTooltip.add(new TranslatableComponent("tip.crimsonchickens.strength", te.chickenStrength));
            }

//            int secs;
//            if (te.chickenAge < 0) {
//                secs = - te.chickenAge / 20;
//                iTooltip.add(new TextComponent("Growing Time: " + String.format("%02d:%02d", secs / 60, secs % 60)));
//
//            }
//            else {
//                if (te.storedItems.getStackInSlot(0).isEmpty()) {
//                    iTooltip.add(new TextComponent("Requires seeds").withStyle(ChatFormatting.YELLOW));
//
//                }
//                else {
//                    if (te.entityCaptured.getInt("EggLayTime") != 0) {
//                        secs = te.eggLayTime / 20;
//                        iTooltip.add(new TextComponent("Next Drop: " + String.format("%02d:%02d", secs / 60, secs % 60)));
//                    }
//                }
//            }
        }
    }
}

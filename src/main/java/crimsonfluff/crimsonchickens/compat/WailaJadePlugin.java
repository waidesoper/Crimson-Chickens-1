package crimsonfluff.crimsonchickens.compat;

import crimsonfluff.crimsonchickens.CrimsonChickens;
import crimsonfluff.crimsonchickens.blocks.Nest;
import crimsonfluff.crimsonchickens.blocks.NestTileEntity;
import crimsonfluff.crimsonchickens.entity.ResourceChickenEntity;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaPlugin;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

@WailaPlugin(value = CrimsonChickens.MOD_ID)
public class WailaJadePlugin implements IWailaPlugin {
    @Override
    public void register(IRegistrar registrar) {
        registrar.registerComponentProvider(new WailaJadeProvider(), TooltipPosition.BODY, Nest.class);
        registrar.registerComponentProvider((tooltip, accessor, config) -> { ((ResourceChickenEntity) accessor.getEntity()).addWailaEntityInfo(tooltip); }, TooltipPosition.BODY, ResourceChickenEntity.class);

//        registrar.registerComponentProvider((tooltip, accessor, config) -> {
            //            if (! (accessor.getBlockEntity() instanceof NestTileEntity te)) return;
//
//            if (te.entityCaptured != null) {
//                tooltip.add(new TextComponent(te.entityDescription));
//
//                if (te.entityCustomName != null)
//                    tooltip.add(te.entityCustomName);
//
//                if (te.entityCaptured.getBoolean("analyzed")) {
//                    tooltip.add(new TranslatableComponent("tip.crimsonchickens.growth", te.chickenGrowth));
//                    tooltip.add(new TranslatableComponent("tip.crimsonchickens.gain", te.chickenGain));
//                    tooltip.add(new TranslatableComponent("tip.crimsonchickens.strength", te.chickenStrength));
//                }

//                int secs;
//                if (te.chickenAge < 0) {
//                    secs = -te.chickenAge / 20;
//                    tooltip.add(new TextComponent("Growing Time: " + String.format("%02d:%02d", secs / 60, secs % 60)));
//
//                } else {
//                    if (te.contents().getStackInSlot(0).isEmpty()) {
//                        tooltip.add(new TextComponent("Requires seeds").withStyle(ChatFormatting.YELLOW));
//
//                    } else {
//                        if (te.entityCaptured.getInt("EggLayTime") != 0) {
//                            secs = te.eggLayTime / 20;
//                            tooltip.add(new TextComponent("Next Drop: " + String.format("%02d:%02d", secs / 60, secs % 60)));
//                        }
//                    }
//                }
//            }
//        }, TooltipPosition.BODY, Nest.class);

//        registrar.registerComponentProvider(new WailaEntityProvider(), TooltipPosition.BODY, ResourceChickenEntity.class);
//        registrar.registerComponentProvider((tooltip, accessor, config) -> {
//            ((ResourceChickenEntity) accessor.getEntity()).addWailaEntityInfo(tooltip);
//
//        }, TooltipPosition.BODY, ResourceChickenEntity.class);
    }
}

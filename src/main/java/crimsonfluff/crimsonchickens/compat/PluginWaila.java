package crimsonfluff.crimsonchickens.compat;

import crimsonfluff.crimsonchickens.CrimsonChickens;
import crimsonfluff.crimsonchickens.blocks.NestTileEntity;
import crimsonfluff.crimsonchickens.entity.ResourceChickenEntity;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaPlugin;

@WailaPlugin(CrimsonChickens.MOD_ID)
public class PluginWaila implements IWailaPlugin {
    @Override
    public void register(IRegistrar registrar) {
        registrar.registerComponentProvider(new WailaEntityProvider(), TooltipPosition.BODY, ResourceChickenEntity.class);
        registrar.registerComponentProvider(new WailaProvider(), TooltipPosition.BODY, NestTileEntity.class);
    }
}

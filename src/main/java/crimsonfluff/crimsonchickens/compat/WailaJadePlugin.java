package crimsonfluff.crimsonchickens.compat;

import crimsonfluff.crimsonchickens.CrimsonChickens;
import crimsonfluff.crimsonchickens.blocks.Nest;
import crimsonfluff.crimsonchickens.blocks.NestTileEntity;
import crimsonfluff.crimsonchickens.entity.ResourceChickenEntity;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaPlugin;

@WailaPlugin(CrimsonChickens.MOD_ID)
public class WailaJadePlugin implements IWailaPlugin {
    @Override
    public void register(IRegistrar registrar) {
        registrar.registerBlockDataProvider(new NestTileEntityProvider(), NestTileEntity.class);
        registrar.registerComponentProvider(new NestComponentProvider(), TooltipPosition.BODY, Nest.class);

        registrar.registerEntityDataProvider(new ChickenEntityProvider(), ResourceChickenEntity.class);
        registrar.registerComponentProvider(new ChickenComponentProvider(), TooltipPosition.BODY, ResourceChickenEntity.class);
    }
}

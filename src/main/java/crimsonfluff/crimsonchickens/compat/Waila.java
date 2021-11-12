package crimsonfluff.crimsonchickens.compat;

import crimsonfluff.crimsonchickens.blocks.Nest;
import crimsonfluff.crimsonchickens.blocks.NestTileEntity;
import crimsonfluff.crimsonchickens.entity.ResourceChickenEntity;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;

public class Waila implements IWailaPlugin {
    public void register(final IRegistrar registrar) {
        registrar.registerEntityDataProvider(new WailaChickenEntityProvider(), ResourceChickenEntity.class);
        registrar.registerComponentProvider(new WailaChickenComponentProvider(), TooltipPosition.BODY, ResourceChickenEntity.class);

        registrar.registerBlockDataProvider(new NestTileEntityProvider(), NestTileEntity.class);
        registrar.registerComponentProvider(new NestComponentProvider(), TooltipPosition.BODY, Nest.class);
    }
}

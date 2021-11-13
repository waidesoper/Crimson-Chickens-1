package crimsonfluff.crimsonchickens.compat.waila;

import crimsonfluff.crimsonchickens.blocks.Nest;
import crimsonfluff.crimsonchickens.blocks.NestTileEntity;
import crimsonfluff.crimsonchickens.entity.ResourceChickenEntity;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;

public class Waila implements IWailaPlugin {
    public void register(final IRegistrar registrar) {
        registrar.addEntityData(new WailaChickenEntityProvider(), ResourceChickenEntity.class);
        registrar.addComponent(new WailaChickenComponentProvider(), TooltipPosition.BODY, ResourceChickenEntity.class);

        registrar.addBlockData(new NestTileEntityProvider(), NestTileEntity.class);
        registrar.addComponent(new NestComponentProvider(), TooltipPosition.BODY, Nest.class);
    }
}

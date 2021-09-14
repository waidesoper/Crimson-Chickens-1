package crimsonfluff.crimsonchickens.compat;

import crimsonfluff.crimsonchickens.entity.ResourceChickenEntity;
import mcp.mobius.waila.api.EntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.config.IPluginConfig;

public class WailaEntityProvider implements IEntityComponentProvider {
    @Override
    public void appendTooltip(ITooltip iTooltip, EntityAccessor entityAccessor, IPluginConfig iPluginConfig) {
        ((ResourceChickenEntity) entityAccessor.getEntity()).addWailaEntityInfo(iTooltip, entityAccessor);
    }
}

package crimsonfluff.crimsonchickens.compat;

import crimsonfluff.crimsonchickens.entity.ResourceChickenEntity;
import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

public class WailaEntityProvider implements IEntityComponentProvider {
    @Override
    public void appendBody(List<ITextComponent> tooltip, IEntityAccessor accessor, IPluginConfig config) {
        ((ResourceChickenEntity) accessor.getEntity()).addWailaEntityInfo(tooltip, accessor);
    }
}

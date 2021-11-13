package crimsonfluff.crimsonchickens.compat.waila;

import crimsonfluff.crimsonchickens.CrimsonChickens;
import crimsonfluff.crimsonchickens.entity.ResourceChickenEntity;
import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;

public class WailaChickenComponentProvider implements IEntityComponentProvider {
    @Override
    public void appendBody(List<Text> tooltip, IEntityAccessor accessor, IPluginConfig config) {
        if (accessor.getEntity() instanceof ResourceChickenEntity) {
            ResourceChickenEntity chicken = (ResourceChickenEntity) accessor.getEntity();

            if (chicken.getDataTracker().get(ResourceChickenEntity.ANALYZED)) {
                tooltip.add(new TranslatableText("tip.crimsonchickens.growth", chicken.getDataTracker().get(ResourceChickenEntity.GROWTH)));
                tooltip.add(new TranslatableText("tip.crimsonchickens.gain", chicken.getDataTracker().get(ResourceChickenEntity.GAIN)));
                tooltip.add(new TranslatableText("tip.crimsonchickens.strength", chicken.getDataTracker().get(ResourceChickenEntity.STRENGTH)));
            }

            NbtCompound data = accessor.getServerData();                // read changing information from ChickenEntityProvider$ServerDataTag
            NbtCompound NBT = data.getCompound("Mutation");

            if (! chicken.isBaby()) {
                if (data.getInt("eggLayTime") != 0) {
//                    int secs = data.getInt("eggTime") / 20;
                    tooltip.add(new TranslatableText("tip.crimsonchickens.egg", CrimsonChickens.formatTime(data.getInt("eggTime"))));
                }
            }

            if (! NBT.isEmpty()) {
                Item itm = Registry.ITEM.get(new Identifier(NBT.getString("type")));
                if (itm != Items.AIR) {
                    tooltip.add(new TranslatableText("tip.crimsonchickens.conv"));
                    tooltip.add(new TranslatableText(itm.getTranslationKey()).append(" (" + NBT.getInt("count") + " / " + NBT.getInt("req") + ")"));
                }
            }
        }
    }
}

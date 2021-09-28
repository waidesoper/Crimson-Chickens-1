package crimsonfluff.crimsonchickens.compat;

import crimsonfluff.crimsonchickens.entity.ResourceChickenEntity;
import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class ChickenComponentProvider implements IEntityComponentProvider {
    @Override
    public void appendBody(List<ITextComponent> tooltip, IEntityAccessor accessor, IPluginConfig config) {
        if (accessor.getEntity() instanceof ResourceChickenEntity) {
            ResourceChickenEntity chicken = (ResourceChickenEntity) accessor.getEntity();

            if (chicken.getEntityData().get(ResourceChickenEntity.ANALYZED)) {
                tooltip.add(new TranslationTextComponent("tip.crimsonchickens.growth", chicken.getEntityData().get(ResourceChickenEntity.GROWTH)));
                tooltip.add(new TranslationTextComponent("tip.crimsonchickens.gain", chicken.getEntityData().get(ResourceChickenEntity.GAIN)));
                tooltip.add(new TranslationTextComponent("tip.crimsonchickens.strength", chicken.getEntityData().get(ResourceChickenEntity.STRENGTH)));
            }

            CompoundNBT data = accessor.getServerData();                // read changing information from ChickenEntityProvider$ServerDataTag
            CompoundNBT NBT = data.getCompound("Mutation");

            if (! chicken.isBaby()) {
                if (data.getInt("eggLayTime") != 0) {
                    int secs = data.getInt("eggTime") / 20;
                    tooltip.add(new TranslationTextComponent("tip.crimsonchickens.egg", String.format("%02d:%02d", secs / 60, secs % 60)));
                }
            }

            if (! NBT.isEmpty()) {
                Item itm = ForgeRegistries.ITEMS.getValue(new ResourceLocation(NBT.getString("type")));
                if (itm != Items.AIR) {
                    tooltip.add(new TranslationTextComponent("tip.crimsonchickens.conv", ""));
                    tooltip.add(new TranslationTextComponent(itm.getDescriptionId()).append(" (" + NBT.getInt("count") + " / " + NBT.getInt("req") + ")"));
                }
            }
        }
    }
}

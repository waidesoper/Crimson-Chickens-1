package crimsonfluff.crimsonchickens.items;

import crimsonfluff.crimsonchickens.CrimsonChickens;
import crimsonfluff.crimsonchickens.json.ResourceChickenData;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.RegistryObject;

import java.util.ArrayList;
import java.util.List;

// Work-around: Items are created before Entities, but need entity to make the spawn egg
// this routine by-passes that by passing a supplier instead of the actual entity

public class SupplierSpawnEggItem extends SpawnEggItem {
    private final RegistryObject<?> supplier;
    private final String descriptionId;
    private final ResourceChickenData chickenData;
    protected static final List<SupplierSpawnEggItem> eggsToAdd = new ArrayList<>();

    public SupplierSpawnEggItem(RegistryObject<?> supplierIn, ResourceChickenData chickenData)
    {
        super(null, chickenData.eggPrimaryColor, chickenData.eggSecondaryColor, new Item.Properties().tab(CrimsonChickens.TAB));
        this.supplier = supplierIn;
        this.chickenData = chickenData;

        this.descriptionId = chickenData.displayName + " Spawn Egg"; // " " + new TranslationTextComponent("tip.crimsonchickens.spawn_egg").getString();
        eggsToAdd.add(this);
    }

    public static int getColor(ItemStack stack, int tintIndex) {
        ResourceChickenData colorData = ((SupplierSpawnEggItem)stack.getItem()).chickenData;
        return tintIndex == 0 ? colorData.eggPrimaryColor : colorData.eggSecondaryColor;
    }

    @Override
    public EntityType<?> getType(CompoundNBT compoundNBT) { return (EntityType<?>) supplier.get(); }

    @Override
    public ITextComponent getName(ItemStack itemStack) { return new StringTextComponent(descriptionId); }

    public static void initSpawnEggs() {
        for (final SpawnEggItem spawnEgg : eggsToAdd) BY_ID.put(spawnEgg.getType(null), spawnEgg);

        eggsToAdd.clear();
    }
}

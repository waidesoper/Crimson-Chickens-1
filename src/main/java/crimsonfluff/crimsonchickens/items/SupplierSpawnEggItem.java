package crimsonfluff.crimsonchickens.items;

import crimsonfluff.crimsonchickens.CrimsonChickens;
import crimsonfluff.crimsonchickens.json.ResourceChickenData;
import crimsonfluff.crimsonchickens.registry.ChickenRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.fmllegacy.RegistryObject;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

// Work-around: Items are created before Entities, but need entity to make the spawn egg
// this routine by-passes that by passing a supplier instead of the actual entity

public class SupplierSpawnEggItem extends SpawnEggItem {
    private final RegistryObject<?> supplier;
    private final String descriptionId;
    private final ResourceChickenData chickenData;
    protected static final List<SupplierSpawnEggItem> eggsToAdd = new ArrayList<>();

    public SupplierSpawnEggItem(RegistryObject<?> supplierIn, ResourceChickenData chickenData) {
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
    public EntityType<?> getType(CompoundTag compoundNBT) { return (EntityType<?>) supplier.get(); }

    @Override
    public Component getName(ItemStack itemStack) { return new TextComponent(descriptionId); }

    @Override
    public Component getDescription() { return new TextComponent(descriptionId); }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(itemStack, worldIn, tooltip, flag);

        ResourceChickenData chickenData = ((SupplierSpawnEggItem)itemStack.getItem()).chickenData;
        if (chickenData != null) {
            if (chickenData.spawnNaturally) {
                tooltip.add(new TextComponent(""));
                tooltip.add(new TextComponent("Spawns naturally"));
            }

            if (! chickenData.canBreed) {
                tooltip.add(new TextComponent(""));
                tooltip.add(new TextComponent("Cannot be bred"));
            }

            if (! chickenData.parentA.isEmpty() && ! chickenData.parentB.isEmpty()) {
                tooltip.add(new TextComponent(""));
                tooltip.add(new TextComponent("Parents:"));

                ResourceChickenData rce = ChickenRegistry.getRegistry().getChickenDataFromID(chickenData.parentA);
                if (rce != null) tooltip.add(new TextComponent(" - " + rce.displayName));
                else tooltip.add(new TextComponent(" - Unknown: " + chickenData.parentA).withStyle(ChatFormatting.DARK_GRAY));

                rce = ChickenRegistry.getRegistry().getChickenDataFromID(chickenData.parentB);
                if (rce != null) tooltip.add(new TextComponent(" - " + rce.displayName));
                else tooltip.add(new TextComponent(" - Unknown: " + chickenData.parentB).withStyle(ChatFormatting.DARK_GRAY));
            }
        }
    }
}

package crimsonfluff.crimsonchickens.items;

import crimsonfluff.crimsonchickens.CrimsonChickens;
import crimsonfluff.crimsonchickens.json.ResourceChickenData;
import crimsonfluff.crimsonchickens.registry.ChickenRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.RegistryObject;
import org.lwjgl.glfw.GLFW;

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
        ResourceChickenData colorData = ((SupplierSpawnEggItem) stack.getItem()).chickenData;
        return tintIndex == 0 ? colorData.eggPrimaryColor : colorData.eggSecondaryColor;
    }

    @Override
    public EntityType<?> getType(CompoundNBT compoundNBT) { return (EntityType<?>) supplier.get(); }

    @Override
    public ITextComponent getName(ItemStack itemStack) { return new StringTextComponent(descriptionId); }

    @Override
    public ITextComponent getDescription() { return new StringTextComponent(descriptionId); }

    public static void initSpawnEggs() {
        for (final SpawnEggItem spawnEgg : eggsToAdd) BY_ID.put(spawnEgg.getType(null), spawnEgg);

        eggsToAdd.clear();
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flag) {
        super.appendHoverText(itemStack, worldIn, tooltip, flag);

        ResourceChickenData chickenData = ((SupplierSpawnEggItem) itemStack.getItem()).chickenData;
        if (chickenData != null) {
            long WINDOW = Minecraft.getInstance().getWindow().getWindow();
            boolean shift = InputMappings.isKeyDown(WINDOW, GLFW.GLFW_KEY_LEFT_SHIFT) || InputMappings.isKeyDown(WINDOW, GLFW.GLFW_KEY_RIGHT_SHIFT);
            boolean doShift = false;

            if (! chickenData.canBreed)
                tooltip.add(new TranslationTextComponent("tip.crimsonchickens.bred").withStyle(TextFormatting.GRAY).withStyle(TextFormatting.ITALIC));

            if (chickenData.spawnNaturally) {
                if (chickenData.biomesWhitelist != null) {
                    doShift = true;

                    if (shift) {
                        tooltip.add(new TranslationTextComponent("tip.crimsonchickens.spawns").withStyle(TextFormatting.GRAY));
                        int b = 0;
                        String s = "";
                        String biome;

                        for (int a = 0; a < chickenData.biomesWhitelist.size(); a++) {
                            biome = chickenData.biomesWhitelist.get(a).toString();
                            biome = biome.replace("\"", "");
                            biome = biome.replace(":", ".");
                            biome = new TranslationTextComponent("biome." + biome).getString();

                            s = s.concat(biome);
                            if (b < 2) s = s.concat(", ");

                            b++;
                            if (b == 3) {
                                b = 0;
                                tooltip.add(new StringTextComponent(" - " + s).withStyle(TextFormatting.GRAY));
                                s = "";
                            }
                        }
                        if (b != 0 && (b < 3)) {
                            tooltip.add(new StringTextComponent(" - " + s.substring(0, s.length() - 2)).withStyle(TextFormatting.GRAY));
                        }
                    }
                }
            }

            if (! chickenData.parentA.isEmpty() && ! chickenData.parentB.isEmpty()) {
                doShift = true;
                if (shift) {
                    tooltip.add(new TranslationTextComponent("tip.crimsonchickens.parents").withStyle(TextFormatting.GRAY));

                    ResourceChickenData rce = ChickenRegistry.getRegistry().getChickenDataFromID(chickenData.parentA);
                    if (rce != null)
                        tooltip.add(new StringTextComponent(" - " + rce.displayName).withStyle(TextFormatting.GRAY));
                    else
                        tooltip.add(new TranslationTextComponent("tip.crimsonchickens.unknown", chickenData.parentA).withStyle(TextFormatting.DARK_GRAY));

                    rce = ChickenRegistry.getRegistry().getChickenDataFromID(chickenData.parentB);
                    if (rce != null)
                        tooltip.add(new StringTextComponent(" - " + rce.displayName).withStyle(TextFormatting.GRAY));
                    else
                        tooltip.add(new TranslationTextComponent("tip.crimsonchickens.unknown", chickenData.parentB).withStyle(TextFormatting.DARK_GRAY));
                }
            }

            if ((! shift) && doShift)
                tooltip.add(new TranslationTextComponent("tip.crimsonchickens.hold").withStyle(TextFormatting.YELLOW));
        }
    }
}

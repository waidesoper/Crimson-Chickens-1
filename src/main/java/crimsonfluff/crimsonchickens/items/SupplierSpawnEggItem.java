package crimsonfluff.crimsonchickens.items;

import com.mojang.blaze3d.platform.InputConstants;
import crimsonfluff.crimsonchickens.CrimsonChickens;
import crimsonfluff.crimsonchickens.json.ResourceChickenData;
import crimsonfluff.crimsonchickens.registry.ChickenRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.fmllegacy.RegistryObject;
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

        this.descriptionId = chickenData.displayName + " Spawn Egg"; // " " + new TranslatableComponent("tip.crimsonchickens.spawn_egg").getString();
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

        ResourceChickenData chickenData = ((SupplierSpawnEggItem) itemStack.getItem()).chickenData;
        if (chickenData != null) {
            long WINDOW = Minecraft.getInstance().getWindow().getWindow();
            boolean shift = InputConstants.isKeyDown(WINDOW, GLFW.GLFW_KEY_LEFT_SHIFT) || InputConstants.isKeyDown(WINDOW, GLFW.GLFW_KEY_RIGHT_SHIFT);
            boolean doShift = false;

            if (! chickenData.canBreed)
                tooltip.add(new TranslatableComponent("tip.crimsonchickens.bred").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));

            if (chickenData.spawnNaturally) {
                if (chickenData.biomesWhitelist != null) {
                    doShift = true;

                    if (shift) {
                        tooltip.add(new TranslatableComponent("tip.crimsonchickens.spawns").withStyle(ChatFormatting.GRAY));
                        int b = 0;
                        String s = "";
                        String biome;

                        for (int a = 0; a < chickenData.biomesWhitelist.size(); a++) {
                            biome = chickenData.biomesWhitelist.get(a).toString();
                            biome = biome.replace("\"", "");
                            biome = biome.replace(":", ".");
                            biome = new TranslatableComponent("biome." + biome).getString();

                            s = s.concat(biome);
                            if (b < 2) s = s.concat(", ");

                            b++;
                            if (b == 3) {
                                b = 0;
                                tooltip.add(new TextComponent(" - " + s).withStyle(ChatFormatting.GRAY));
                                s = "";
                            }
                        }
                        if (b != 0 && (b < 3)) {
                            tooltip.add(new TextComponent(" - " + s.substring(0, s.length() - 2)).withStyle(ChatFormatting.GRAY));
                        }
                    }
                }
            }

            if (! chickenData.parentA.isEmpty() && ! chickenData.parentB.isEmpty()) {
                doShift = true;
                if (shift) {
                    tooltip.add(new TranslatableComponent("tip.crimsonchickens.parents").withStyle(ChatFormatting.GRAY));

                    ResourceChickenData rce = ChickenRegistry.getRegistry().getChickenDataFromID(chickenData.parentA);
                    if (rce != null)
                        tooltip.add(new TextComponent(" - " + rce.displayName).withStyle(ChatFormatting.GRAY));
                    else
                        tooltip.add(new TranslatableComponent("tip.crimsonchickens.unknown", chickenData.parentA).withStyle(ChatFormatting.DARK_GRAY));

                    rce = ChickenRegistry.getRegistry().getChickenDataFromID(chickenData.parentB);
                    if (rce != null)
                        tooltip.add(new TextComponent(" - " + rce.displayName).withStyle(ChatFormatting.GRAY));
                    else
                        tooltip.add(new TranslatableComponent("tip.crimsonchickens.unknown", chickenData.parentB).withStyle(ChatFormatting.DARK_GRAY));
                }
            }

            if ((! shift) && doShift)
                tooltip.add(new TranslatableComponent("tip.crimsonchickens.hold").withStyle(ChatFormatting.YELLOW));
        }
    }
}

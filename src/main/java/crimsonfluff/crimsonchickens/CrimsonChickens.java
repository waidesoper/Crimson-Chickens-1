package crimsonfluff.crimsonchickens;

import crimsonfluff.crimsonchickens.entity.AngryChickenEntity;
import crimsonfluff.crimsonchickens.entity.ResourceChickenEntity;
import crimsonfluff.crimsonchickens.init.*;
import crimsonfluff.crimsonchickens.json.ResourceChickenData;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//  /summon crimsonchickens:blaze_chicken ~ ~ ~ {Age:-24000,analyzed:1,strength:10,gain:10,growth:10}
//  /summon crimsonchickens:blaze_chicken ~ ~ ~ {analyzed:1,strength:10,gain:10,growth:10}

public class CrimsonChickens implements ModInitializer {
    public static final String MOD_ID = "crimsonchickens";
    public static final Logger LOGGER = LogManager.getLogger(CrimsonChickens.class);

//    public static CrimsonChickensConfig CONFIG;

    public static final ItemGroup CREATIVE_TAB = FabricItemGroupBuilder.build(new Identifier(MOD_ID, "tab"), () -> new ItemStack(initItems.EGG_DUCK));


    @Override
    public void onInitialize() {
//        AutoConfig.register(SimpleMagnetConfig.class, GsonConfigSerializer::new);
//        CONFIG = AutoConfig.getConfigHolder(CrimsonChickensConfig.class).getConfig();

        initConfigs.register();
        initItems.register();
        initBlocks.register();
        initTiles.register();
        initSounds.register();
        initRegistry.register();

        initChickenConfigs.loadConfigs();

        initRegistry.MOD_CHICKENS.forEach((s, resourceChicken) -> {
            if (s.equals("angry"))
                FabricDefaultAttributeRegistry.register(resourceChicken, AngryChickenEntity.createChickenAttributes(s));
            else
                FabricDefaultAttributeRegistry.register(resourceChicken, ResourceChickenEntity.createChickenAttributes(s));
        });

//        ModPacketsC2S.register();
    }



    public static int calcNewEggLayTime(Random r, ResourceChickenData rcd, int growth) {
        if (rcd.eggLayTime == 0) return 0;

        int egg = r.nextInt(rcd.eggLayTime) + rcd.eggLayTime;
        return (int) Math.max(1.0f, (egg * (10.f - growth + 1.f)) / 10.f);
    }

    public static int calcDropQuantity(int gain) {
        if (gain < 5) return 1;         // between 1-4
        if (gain < 10) return 2;        // between 5-9
        return 3;                       // 10
    }

    public static List<ItemStack> calcDrops(int gain, ResourceChickenData chickenData, int fortune) {
        // return a list of item drops
        // done like this to avoid making stacks of non-stackable items
        List<ItemStack> lst = new ArrayList<>();

        // TODO: if no drop item then try and find a loot table?
        if (! chickenData.dropItemItem.equals("")) {
            ItemStack itemStack = new ItemStack(Registry.ITEM.get(new Identifier(chickenData.dropItemItem)));

            if (! itemStack.isEmpty()) {
                if (chickenData.dropItemNBT != null) itemStack.setTag(chickenData.dropItemNBT.copy());
                int dropQuantity = calcDropQuantity(gain) + fortune;

                if (itemStack.isStackable()) {
                    itemStack.setCount(dropQuantity);

                    lst.add(itemStack);
                }
                else {
                    for (int a = 0; a < dropQuantity; a++) {
                        ItemStack itm = itemStack.copy();
                        lst.add(itm);
                    }
                }
            }
        }

        Random r = new Random();
        if (r.nextInt(8) == 0) lst.add(chickenData.hasTrait == 1 ? new ItemStack(initItems.FEATHER_DUCK) : new ItemStack(Items.FEATHER));

        return lst;
    }

    public static String formatTime(int milli) {
        int secs = milli / 20;
        int mins = secs / 60;
        int hours = mins / 60;

        if (hours == 0)
            return String.format("%02d:%02d", mins, secs % 60);
        else
            return String.format("%02d:%02d:%02d", hours, mins, secs % 60);
    }
}

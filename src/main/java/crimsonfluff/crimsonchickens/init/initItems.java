package crimsonfluff.crimsonchickens.init;

import crimsonfluff.crimsonchickens.CrimsonChickens;
import crimsonfluff.crimsonchickens.items.AnimalNet;
import crimsonfluff.crimsonchickens.items.DuckEgg;
import crimsonfluff.crimsonchickens.items.xpItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.BlockItem;
import net.minecraft.item.FoodComponents;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class initItems {
    public static final Item EGG_DUCK = new DuckEgg();
    public static final Item FEATHER_DUCK = new Item(new FabricItemSettings().group(CrimsonChickens.CREATIVE_TAB));

//    public static final Item WATER_EGG = new LiquidEgg(Fluids.WATER.getStill());
//    public static final Item LAVA_EGG = new LiquidEgg(Fluids.LAVA.getStill());

    public static final Item ANIMAL_NET = new AnimalNet();

    public static final Item XP_ITEM = new xpItem();

//    public static final FoodComponent DUCK = (new FoodComponent.Builder()).hunger(5).saturationModifier(0.6F).build();

//    public static final Block NEST_BLOCK = new Block(FabricBlockSettings.of(Material.METAL).strength(4.0f));

    public static final BlockItem NEST_BLOCK_ITEM = new BlockItem(initBlocks.NEST_BLOCK, new FabricItemSettings().group(CrimsonChickens.CREATIVE_TAB));

    public static void register() {
        Registry.register(Registry.ITEM, new Identifier(CrimsonChickens.MOD_ID, "duck_egg"), EGG_DUCK);
        Registry.register(Registry.ITEM, new Identifier(CrimsonChickens.MOD_ID, "duck"), new Item(new FabricItemSettings().group(CrimsonChickens.CREATIVE_TAB).food(FoodComponents.CHICKEN)));
        Registry.register(Registry.ITEM, new Identifier(CrimsonChickens.MOD_ID, "duck_cooked"), new Item(new FabricItemSettings().group(CrimsonChickens.CREATIVE_TAB).food(FoodComponents.COOKED_CHICKEN)));
        Registry.register(Registry.ITEM, new Identifier(CrimsonChickens.MOD_ID, "duck_feather"), FEATHER_DUCK);

//        Registry.register(Registry.ITEM, new Identifier(CrimsonChickens.MOD_ID, "water_egg"), WATER_EGG);
//        Registry.register(Registry.ITEM, new Identifier(CrimsonChickens.MOD_ID, "lava_egg"), LAVA_EGG);

        Registry.register(Registry.ITEM, new Identifier(CrimsonChickens.MOD_ID, "animal_net"), ANIMAL_NET);

        Registry.register(Registry.ITEM, new Identifier(CrimsonChickens.MOD_ID, "xp_item"), XP_ITEM);


        // Block Items ?
        Registry.register(Registry.ITEM, new Identifier(CrimsonChickens.MOD_ID, "nest"), NEST_BLOCK_ITEM);
        Registry.register(Registry.ITEM, new Identifier(CrimsonChickens.MOD_ID, "duck_egg_block"), new BlockItem(initBlocks.DUCK_EGG_BLOCK, new FabricItemSettings().group(CrimsonChickens.CREATIVE_TAB)));
        Registry.register(Registry.ITEM, new Identifier(CrimsonChickens.MOD_ID, "egg_block"), new BlockItem(initBlocks.EGG_BLOCK, new FabricItemSettings().group(CrimsonChickens.CREATIVE_TAB)));
    }
}

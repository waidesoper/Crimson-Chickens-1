package crimsonfluff.crimsonchickens.init;

import crimsonfluff.crimsonchickens.CrimsonChickens;
import crimsonfluff.crimsonchickens.items.AnimalNet;
import crimsonfluff.crimsonchickens.items.DuckEgg;
import crimsonfluff.crimsonchickens.items.LiquidEgg;
import crimsonfluff.crimsonchickens.items.xpItem;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.EggItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class initItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CrimsonChickens.MOD_ID);

    // Items
    public static final RegistryObject<Item> EGG_DUCK = ITEMS.register("duck_egg", DuckEgg::new);
    public static final RegistryObject<Item> FEATHER_DUCK = ITEMS.register("duck_feather", ()-> new Item(new Item.Properties().tab(CrimsonChickens.TAB)));
    public static final RegistryObject<Item> DUCK = ITEMS.register("duck", ()-> new Item(new Item.Properties().tab(CrimsonChickens.TAB).food(Foods.CHICKEN)));
    public static final RegistryObject<Item> COOKED_DUCK = ITEMS.register("duck_cooked", ()-> new Item(new Item.Properties().tab(CrimsonChickens.TAB).food(Foods.COOKED_CHICKEN)));

    public static final RegistryObject<EggItem> WATER_EGG = ITEMS.register("water_egg", ()-> new LiquidEgg(Fluids.WATER.getSource()));
    public static final RegistryObject<EggItem> LAVA_EGG = ITEMS.register("lava_egg", ()-> new LiquidEgg(Fluids.LAVA.getSource()));

    public static final RegistryObject<Item> ANIMAL_NET = ITEMS.register("animal_net", AnimalNet::new);

    public static final RegistryObject<Item> XP_ITEM = ITEMS.register("xp_item", xpItem::new);


    // BlockItems
    public static final RegistryObject<BlockItem> NEST_BLOCK_ITEM = ITEMS.register("nest", ()->
        new BlockItem(initBlocks.NEST_BLOCK.get(), new Item.Properties().tab(CrimsonChickens.TAB)));

    public static final RegistryObject<BlockItem> DUCK_EGG_BLOCK_ITEM = ITEMS.register("duck_egg_block", ()->
        new BlockItem(initBlocks.DUCK_EGG_BLOCK.get(), new Item.Properties().tab(CrimsonChickens.TAB)));

    public static final RegistryObject<BlockItem> EGG_BLOCK_ITEM = ITEMS.register("egg_block", ()->
        new BlockItem(initBlocks.EGG_BLOCK.get(), new Item.Properties().tab(CrimsonChickens.TAB)));
}

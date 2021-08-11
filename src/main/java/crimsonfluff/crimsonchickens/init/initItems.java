package crimsonfluff.crimsonchickens.init;

import crimsonfluff.crimsonchickens.CrimsonChickens;
import crimsonfluff.crimsonchickens.items.Duck;
import crimsonfluff.crimsonchickens.items.DuckCooked;
import crimsonfluff.crimsonchickens.items.DuckEgg;
import crimsonfluff.crimsonchickens.items.DuckFeather;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class initItems {
    private initItems() { throw new IllegalStateException("Utility Class"); }

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CrimsonChickens.MOD_ID);

    public static final RegistryObject<Item> EGG_DUCK = ITEMS.register("duck_egg", DuckEgg::new);
    public static final RegistryObject<Item> FEATHER_DUCK = ITEMS.register("duck_feather", DuckFeather::new);
    public static final RegistryObject<Item> DUCK = ITEMS.register("duck", Duck::new);
    public static final RegistryObject<Item> COOKED_DUCK = ITEMS.register("duck_cooked", DuckCooked::new);
}

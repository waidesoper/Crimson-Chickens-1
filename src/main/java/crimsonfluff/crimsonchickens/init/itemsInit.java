package crimsonfluff.crimsonchickens.init;

import crimsonfluff.crimsonchickens.CrimsonChickens;
import crimsonfluff.crimsonchickens.SupplierSpawnEggItem;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class itemsInit {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CrimsonChickens.MOD_ID);

    public static final RegistryObject<SpawnEggItem> BONE_CHICKEN_SPAWN_EGG = ITEMS.register("bone_chicken_spawn_egg",
            () -> new SupplierSpawnEggItem(null, entitiesInit.BONE_CHICKEN, 0xFF329F, 0x16777119, new Item.Properties().group(CrimsonChickens.TAB)));

    public static final RegistryObject<SpawnEggItem> LAPIS_CHICKEN_SPAWN_EGG = ITEMS.register("lapis_chicken_spawn_egg",
            () -> new SupplierSpawnEggItem(null, entitiesInit.LAPIS_CHICKEN, 0xFF329F, 0x16777119, new Item.Properties().group(CrimsonChickens.TAB)));


}

package crimsonfluff.crimsonchickens.init;

import crimsonfluff.crimsonchickens.CrimsonChickens;
import net.minecraft.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {
    private ModItems() { throw new IllegalStateException("Utility Class"); }

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CrimsonChickens.MOD_ID);
}

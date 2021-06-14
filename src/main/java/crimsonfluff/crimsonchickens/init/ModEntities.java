package crimsonfluff.crimsonchickens.init;

import crimsonfluff.crimsonchickens.entity.ResourceChickenEntity;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.RegistryObject;

import java.util.HashMap;
import java.util.Map;

public class ModEntities {
    private ModEntities() {
        throw new IllegalStateException("Utility Class");
    }

    private static final Map<String, RegistryObject<EntityType<? extends ResourceChickenEntity>>> MOD_CHICKENS = new HashMap<>();

    public static Map<String, RegistryObject<EntityType<? extends ResourceChickenEntity>>> getModChickens() {
        return MOD_CHICKENS;
    }
}

package crimsonfluff.crimsonchickens.init;

import crimsonfluff.crimsonchickens.entity.ResourceChickenEntity;
import net.minecraft.entity.EntityType;

import java.util.HashMap;
import java.util.Map;

public class initEntities {
    private static final Map<String, EntityType<? extends ResourceChickenEntity>> MOD_CHICKENS = new HashMap<>();

    public static Map<String, EntityType<? extends ResourceChickenEntity>> getModChickens() {
        return MOD_CHICKENS;
    }
}

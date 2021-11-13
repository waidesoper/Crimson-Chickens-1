package crimsonfluff.crimsonchickens.init;

import crimsonfluff.crimsonchickens.CrimsonChickens;
import crimsonfluff.crimsonchickens.entity.AngryChickenEntity;
import crimsonfluff.crimsonchickens.entity.DuckEggProjectileEntity;
import crimsonfluff.crimsonchickens.entity.ResourceChickenEntity;
import crimsonfluff.crimsonchickens.items.ChickenSpawnEggItem;
import crimsonfluff.crimsonchickens.json.ResourceChickenData;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Map;

public class initRegistry {
    public static EntityType<DuckEggProjectileEntity> DUCK_EGG;

    public static final Map<String, EntityType<? extends ResourceChickenEntity>> MOD_CHICKENS = new HashMap<>();

    public static void register() {
        DUCK_EGG = Registry.register(Registry.ENTITY_TYPE,
            new Identifier(CrimsonChickens.MOD_ID, "duck_egg"),
            FabricEntityTypeBuilder.<DuckEggProjectileEntity>create(SpawnGroup.MISC, DuckEggProjectileEntity::new)
                .dimensions(EntityDimensions.fixed(0.25F, 0.25F))
                .trackedUpdateRate(10)
                .trackRangeBlocks(4)
                .build());
    }

    public static void registerChicken(String name, ResourceChickenData chickenData) {
        EntityType<ResourceChickenEntity> ENTITY;
        final Identifier IDENTIFIER = new Identifier(CrimsonChickens.MOD_ID, name);     // was name + "_chicken" - Forge World Breaking !

//        if (name.equals("angry")) {
//            if (chickenData.isFireImmune) {
//                ENTITY = Registry.register(Registry.ENTITY_TYPE,
//                    IDENTIFIER,
//                    FabricEntityTypeBuilder.<ResourceChickenEntity>create(SpawnGroup.MONSTER, (type, world) -> new AngryChickenEntity(type, world, chickenData))
//                        .dimensions(EntityDimensions.fixed(0.4f, 0.7f))
//                        .fireImmune()
//                        .build());
//            } else {
//                ENTITY = Registry.register(Registry.ENTITY_TYPE,
//                    IDENTIFIER,
//                    FabricEntityTypeBuilder.<ResourceChickenEntity>create(SpawnGroup.MONSTER, (type, world) -> new AngryChickenEntity(type, world, chickenData))
//                        .dimensions(EntityDimensions.fixed(0.4f, 0.7f))
//                        .build());
//            }
//        }
//        else {
            if (chickenData.isFireImmune) {
                ENTITY = Registry.register(Registry.ENTITY_TYPE,
                    IDENTIFIER,
                    FabricEntityTypeBuilder.<ResourceChickenEntity>create(
                        chickenData.spawnType,
                        name.equals("angry")
                            ? (type, world) -> new AngryChickenEntity(type, world, chickenData)
                            : (type, world) -> new ResourceChickenEntity(type, world, chickenData))
                        .dimensions(EntityDimensions.fixed(0.4f, 0.7f))
                        .fireImmune()
                        .build());
            }
            else {
                ENTITY = Registry.register(Registry.ENTITY_TYPE,
                    IDENTIFIER,
                    FabricEntityTypeBuilder.<ResourceChickenEntity>create(
                        chickenData.spawnType,
                            name.equals("angry")
                                ? (type, world) -> new AngryChickenEntity(type, world, chickenData)
                                : (type, world) -> new ResourceChickenEntity(type, world, chickenData))
                        .dimensions(EntityDimensions.fixed(0.4f, 0.7f))
                        .build());
            }
//        }

        final Item SPAWN_EGG = new ChickenSpawnEggItem(ENTITY, chickenData.eggPrimaryColor, chickenData.eggSecondaryColor, new Item.Settings().group(CrimsonChickens.CREATIVE_TAB), chickenData);
        Registry.register(Registry.ITEM, new Identifier(CrimsonChickens.MOD_ID, name + "_spawn_egg"), SPAWN_EGG);

        MOD_CHICKENS.put(name, ENTITY);
        chickenData.setEntityTypeRegistryID(IDENTIFIER);
        chickenData.spawnEggItem = SPAWN_EGG;
    }
}

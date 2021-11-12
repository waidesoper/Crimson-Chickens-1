package crimsonfluff.crimsonchickens.init;

import crimsonfluff.crimsonchickens.CrimsonChickens;
import crimsonfluff.crimsonchickens.entity.AngryChickenEntity;
import crimsonfluff.crimsonchickens.entity.DuckEggProjectileEntity;
import crimsonfluff.crimsonchickens.entity.ResourceChickenEntity;
import crimsonfluff.crimsonchickens.json.ResourceChickenData;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Map;

public class initRegistry {
//    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, CrimsonChickens.MOD_ID);

    public static EntityType<DuckEggProjectileEntity> DUCK_EGG;

    public static final Map<String, EntityType<? extends ResourceChickenEntity>> MOD_CHICKENS = new HashMap<>();


    public static void register() {
        DUCK_EGG = Registry.register(Registry.ENTITY_TYPE,
            new Identifier(CrimsonChickens.MOD_ID, "duck_egg"),
            FabricEntityTypeBuilder.<DuckEggProjectileEntity>create(SpawnGroup.MISC, DuckEggProjectileEntity::new)
                .dimensions(EntityDimensions.fixed(0.4f, 0.7f))
                .build());
    }

    public static void registerChicken(String name, ResourceChickenData chickenData) {
//        final RegistryKey<EntityType<? extends ResourceChickenEntity>> customChickenEntity;

        EntityType<ResourceChickenEntity> ENTITY;
        final Identifier IDENTIFIER = new Identifier(CrimsonChickens.MOD_ID, name + "_chicken");

        if (name.equals("angry")) {
            ENTITY = Registry.register(Registry.ENTITY_TYPE,
                IDENTIFIER,
                FabricEntityTypeBuilder.<ResourceChickenEntity>create(SpawnGroup.MONSTER, (type, world) -> new AngryChickenEntity(type, world, chickenData))
                    .dimensions(EntityDimensions.fixed(0.4f, 0.7f))
                    .build());
        }
        else {
            if (chickenData.isFireImmune) {
                ENTITY = Registry.register(Registry.ENTITY_TYPE,
                    IDENTIFIER,
                    FabricEntityTypeBuilder.<ResourceChickenEntity>create(
                        chickenData.spawnType == 0 ? SpawnGroup.CREATURE : SpawnGroup.MONSTER,
                        (type, world) -> new ResourceChickenEntity(type, world, chickenData))
                        .dimensions(EntityDimensions.fixed(0.4f, 0.7f))
                        .fireImmune()
                        .build());
            }
            else {
                ENTITY = Registry.register(Registry.ENTITY_TYPE,
                    IDENTIFIER,
                    FabricEntityTypeBuilder.<ResourceChickenEntity>create(
                        chickenData.spawnType == 0 ? SpawnGroup.CREATURE : SpawnGroup.MONSTER,
                        (type, world) -> new ResourceChickenEntity(type, world, chickenData))
                        .dimensions(EntityDimensions.fixed(0.4f, 0.7f))
                        .build());
            }
        }

//        final RegistryKey<Item> SPAWN_EGG = Registry.ITEM.add(name + "_chicken_spawn_egg",
//            () -> new SpawnEggItem(customChickenEntity, chickenData));

        MOD_CHICKENS.put(name, ENTITY);
        chickenData.setEntityTypeRegistryID(IDENTIFIER);
//        chickenData.setSpawnEggItemRegistryObject(SPAWN_EGG);
    }
}

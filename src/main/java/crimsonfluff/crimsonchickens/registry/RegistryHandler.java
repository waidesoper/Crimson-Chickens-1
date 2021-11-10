package crimsonfluff.crimsonchickens.registry;

import crimsonfluff.crimsonchickens.CrimsonChickens;
import crimsonfluff.crimsonchickens.entity.AngryChickenEntity;
import crimsonfluff.crimsonchickens.entity.DuckEggProjectileEntity;
import crimsonfluff.crimsonchickens.entity.ResourceChickenEntity;
import crimsonfluff.crimsonchickens.init.initEntities;
import crimsonfluff.crimsonchickens.items.SupplierSpawnEggItem;
import crimsonfluff.crimsonchickens.json.ResourceChickenData;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class RegistryHandler {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, CrimsonChickens.MOD_ID);

    public static final RegistryObject<EntityType<DuckEggProjectileEntity>> DUCK_EGG = ENTITY_TYPES.register("duck_egg",
        () -> EntityType.Builder.<DuckEggProjectileEntity>of(DuckEggProjectileEntity::new, EntityClassification.MISC)
            .sized(0.25F, 0.25F)
            .clientTrackingRange(4)
            .updateInterval(10)
            .build(new Identifier(CrimsonChickens.MOD_ID, "duck_egg").toString()));


    public static void onEntityAttributeCreationEvent(EntityAttributeCreationEvent event) {
        initEntities.getModChickens().forEach((s, customChicken) -> {
            if (s.equals("angry"))
                event.put(customChicken.get(), AngryChickenEntity.createChickenAttributes(s).build());
            else
                event.put(customChicken.get(), ResourceChickenEntity.createChickenAttributes(s).build());

            EntitySpawnPlacementRegistry.register(customChicken.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (animal, world, reason, pos, random) -> true);
        });
    }

    public static void registerChicken(String name, ResourceChickenData chickenData) {
        final RegistryObject<EntityType<? extends ResourceChickenEntity>> customChickenEntity;

        if (name.equals("angry")) {
            customChickenEntity = ENTITY_TYPES.register(name + "_chicken", () -> EntityType.Builder
                .<ResourceChickenEntity>of((type, world) -> new AngryChickenEntity(type, world, chickenData), EntityClassification.MONSTER)
                .sized(0.4f, 0.7f)
                .fireImmune()
                .build(name + "_chicken"));

        }
        else {

            if (chickenData.isFireImmune) {
                customChickenEntity = ENTITY_TYPES.register(name + "_chicken", () -> EntityType.Builder
                    .<ResourceChickenEntity>of((type, world) -> new ResourceChickenEntity(type, world, chickenData), chickenData.spawnType == 0 ? EntityClassification.CREATURE : EntityClassification.MONSTER)
                    .sized(0.4f, 0.7f)
                    .fireImmune()
                    .build(name + "_chicken"));
            }
            else {
                customChickenEntity = ENTITY_TYPES.register(name + "_chicken", () -> EntityType.Builder
                    .<ResourceChickenEntity>of((type, world) -> new ResourceChickenEntity(type, world, chickenData), chickenData.spawnType == 0 ? EntityClassification.CREATURE : EntityClassification.MONSTER)
                    .sized(0.4f, 0.7f)
                    .build(name + "_chicken"));
            }
        }

        final RegistryObject<Item> SPAWN_EGG = initItems.ITEMS.register(name + "_chicken_spawn_egg",
            () -> new SupplierSpawnEggItem(customChickenEntity, chickenData));

        initEntities.getModChickens().put(name, customChickenEntity);
        chickenData.setEntityTypeRegistryID(customChickenEntity.getId());
        chickenData.setSpawnEggItemRegistryObject(SPAWN_EGG);
    }
}

package crimsonfluff.crimsonchickens.registry;

import crimsonfluff.crimsonchickens.CrimsonChickens;
import crimsonfluff.crimsonchickens.SupplierSpawnEggItem;
import crimsonfluff.crimsonchickens.entity.ResourceChickenEntity;
import crimsonfluff.crimsonchickens.init.ModEntities;
import crimsonfluff.crimsonchickens.init.ModItems;
import crimsonfluff.crimsonchickens.json.ResourceChickenData;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class RegistryHandler {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, CrimsonChickens.MOD_ID);

    public static void onEntityAttributeCreationEvent(EntityAttributeCreationEvent event) {
        ModEntities.getModChickens().forEach((s, customChicken) -> event.put(customChicken.get(), ResourceChickenEntity.createChickenAttributes(s).build()));
    }

    public static void registerChicken(String name, ResourceChickenData chickenData) {
        final RegistryObject<EntityType<? extends ResourceChickenEntity>> customChickenEntity;

        if (chickenData.isFireImmune) {
                customChickenEntity = ENTITY_TYPES.register(name + "_chicken", () -> EntityType.Builder
                .<ResourceChickenEntity>of((type, world) -> new ResourceChickenEntity(type, world, chickenData), EntityClassification.CREATURE)
                .sized(0.4f, 0.7f)
                .fireImmune()
                .build(name + "_chicken"));
        } else {
            customChickenEntity = ENTITY_TYPES.register(name + "_chicken", () -> EntityType.Builder
                .<ResourceChickenEntity>of((type, world) -> new ResourceChickenEntity(type, world, chickenData), EntityClassification.CREATURE)
                .sized(0.4f, 0.7f)
                .build(name + "_chicken"));
        }

//        final RegistryObject<Item> customSpawnEgg = ModItems.ITEMS.register(name + "_chicken_spawn_egg",
//            () -> new ChickenSpawnEggItem(customChickenEntity, 0x303030, 0x303030, chickenData, new Item.Properties().tab(CrimsonChickens.TAB)));

        final RegistryObject<SpawnEggItem> SPAWN_EGG = ModItems.ITEMS.register(name + "_chicken_spawn_egg",
            () -> new SupplierSpawnEggItem(null, customChickenEntity, 0x6441a5, 0xFFFFFF, new Item.Properties().tab(CrimsonChickens.TAB)));

        ModEntities.getModChickens().put(name, customChickenEntity);
        chickenData.setEntityTypeRegistryID(customChickenEntity.getId());

//        chickenData.setSpawnEggItemRegistryObject(customBeeSpawnEgg);
    }
}

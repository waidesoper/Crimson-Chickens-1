package crimsonfluff.crimsonchickens.init;

import crimsonfluff.crimsonchickens.CrimsonChickens;
import crimsonfluff.crimsonchickens.entity.*;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class entitiesInit {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, CrimsonChickens.MOD_ID);

    public static final RegistryObject<EntityType<BoneChickenEntity>> BONE_CHICKEN = ENTITIES.register("bone_chicken",
            () -> EntityType.Builder.create(BoneChickenEntity::new, EntityClassification.CREATURE)
                .size(0.4f, 0.7f)
                .build(new ResourceLocation(CrimsonChickens.MOD_ID, "bone_chicken").toString()));

    public static final RegistryObject<EntityType<LapisChickenEntity>> LAPIS_CHICKEN = ENTITIES.register("lapis_chicken",
            () -> EntityType.Builder.create(LapisChickenEntity::new, EntityClassification.CREATURE)
                .size(0.4f, 0.7f)
                .build(new ResourceLocation(CrimsonChickens.MOD_ID, "lapis_chicken").toString()));

    public static final RegistryObject<EntityType<IronChickenEntity>> IRON_CHICKEN = ENTITIES.register("iron_chicken",
            () -> EntityType.Builder.create(IronChickenEntity::new, EntityClassification.CREATURE)
                    .size(0.4f, 0.7f)
                    .build(new ResourceLocation(CrimsonChickens.MOD_ID, "iron_chicken").toString()));

    public static final RegistryObject<EntityType<RedstoneChickenEntity>> REDSTONE_CHICKEN = ENTITIES.register("redstone_chicken",
            () -> EntityType.Builder.create(RedstoneChickenEntity::new, EntityClassification.CREATURE)
                    .size(0.4f, 0.7f)
                    .build(new ResourceLocation(CrimsonChickens.MOD_ID, "redstone_chicken").toString()));

    public static final RegistryObject<EntityType<CoalChickenEntity>> COAL_CHICKEN = ENTITIES.register("coal_chicken",
            () -> EntityType.Builder.create(CoalChickenEntity::new, EntityClassification.CREATURE)
                    .size(0.4f, 0.7f)
                    .build(new ResourceLocation(CrimsonChickens.MOD_ID, "coal_chicken").toString()));

    public static final RegistryObject<EntityType<GlowstoneChickenEntity>> GLOW_CHICKEN = ENTITIES.register("glowstone_chicken",
            () -> EntityType.Builder.create(GlowstoneChickenEntity::new, EntityClassification.CREATURE)
                    .size(0.4f, 0.7f)
                    .build(new ResourceLocation(CrimsonChickens.MOD_ID, "glowstone_chicken").toString()));


}

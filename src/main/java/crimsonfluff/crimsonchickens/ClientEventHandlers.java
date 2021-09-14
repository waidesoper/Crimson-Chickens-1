package crimsonfluff.crimsonchickens;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import crimsonfluff.crimsonchickens.blocks.NestRenderer;
import crimsonfluff.crimsonchickens.entity.ResourceChickenRenderer;
import crimsonfluff.crimsonchickens.init.initBlocks;
import crimsonfluff.crimsonchickens.init.initEntities;
import crimsonfluff.crimsonchickens.init.initTiles;
import crimsonfluff.crimsonchickens.items.SupplierSpawnEggItem;
import crimsonfluff.crimsonchickens.json.ResourceChickenData;
import crimsonfluff.crimsonchickens.registry.ChickenRegistry;
import crimsonfluff.crimsonchickens.registry.IResourceChickenRegistry;
import crimsonfluff.crimsonchickens.registry.RegistryHandler;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Map;

// Done like this because onItemColors had a major 'can't reference blah with non/static context blah blah`
// then everything exploded
// so moved ClientStuffs into here

public class ClientEventHandlers {
    private static final IResourceChickenRegistry CHICKEN_REGISTRY = ChickenRegistry.getRegistry();
    private static final Multimap<ResourceLocation, ResourceLocation> MODEL_MAP = LinkedHashMultimap.create();

    private ClientEventHandlers() { throw new IllegalStateException("ClientEventHandlers Utility Class"); }

    public static void clientStuff() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientEventHandlers::registerRenderers);
        //FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientEventHandlers::registerLayerDefinitions);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientEventHandlers::onColorHandlerEvent);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientEventHandlers::onModelRegistryEvent);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientEventHandlers::onModelBakeEvent);
    }

    private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        initEntities.getModChickens().forEach((s, resourceChicken) -> event.registerEntityRenderer(resourceChicken.get(), manager -> {
            ResourceChickenData chickenData = ChickenRegistry.getRegistry().getChickenData(s);
            return new ResourceChickenRenderer(manager, chickenData);
        }));

        ItemBlockRenderTypes.setRenderLayer(initBlocks.NEST_BLOCK.get(), RenderType.cutout());

        event.registerEntityRenderer(RegistryHandler.DUCK_EGG.get(), ThrownItemRenderer::new);

        event.registerBlockEntityRenderer(initTiles.NEST_BLOCK_TILE.get(), NestRenderer::new);
    }

//    private static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
//        event.registerLayerDefinition(CrimsonChickens.CHICKEN_NO_FEET, NestChickenModel::createBodyLayer);
//    }

    private static void onModelBakeEvent(ModelBakeEvent event) {
        Map<ResourceLocation, BakedModel> modelRegistry = event.getModelRegistry();
        BakedModel missingModel = modelRegistry.get(ModelBakery.MISSING_MODEL_LOCATION);

        MODEL_MAP.asMap().forEach((resourceLocation, resourceLocations) -> {
            BakedModel defaultModel = modelRegistry.getOrDefault(resourceLocation, missingModel);
            resourceLocations.forEach(modelLocation -> modelRegistry.put(modelLocation, defaultModel));
        });

        MODEL_MAP.clear();  // discard once used
    }

    // basically make all resource spawn eggs use minecraft:spawn_egg model file
    // still shows as warns in debug tho'
    private static void onModelRegistryEvent(ModelRegistryEvent event) {
        initEntities.getModChickens().forEach((s, resourceChicken) -> {
            ModelResourceLocation defaultModelLocation = new ModelResourceLocation("minecraft:template_spawn_egg", "inventory");
            ModelLoader.addSpecialModel(defaultModelLocation);
            MODEL_MAP.put(defaultModelLocation, new ModelResourceLocation(CrimsonChickens.MOD_ID + ":" + s + "_chicken_spawn_egg","inventory"));
        });
    }

    private static void onColorHandlerEvent(ColorHandlerEvent.Item event) {
        ItemColors colors = event.getItemColors();
        CHICKEN_REGISTRY.getChickens().forEach((s, resourceChickenData) -> colors.register(SupplierSpawnEggItem::getColor, resourceChickenData.getSpawnEggItemRegistryObject().get()));
    }
}

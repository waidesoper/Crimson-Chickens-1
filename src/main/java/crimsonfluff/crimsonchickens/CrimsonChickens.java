package crimsonfluff.crimsonchickens;

import crimsonfluff.crimsonchickens.entity.*;
import crimsonfluff.crimsonchickens.init.entitiesInit;
import crimsonfluff.crimsonchickens.init.itemsInit;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(CrimsonChickens.MOD_ID)
public class CrimsonChickens {
    public static final String MOD_ID = "crimsonchickens";
    final IEventBus MOD_EVENTBUS = FMLJavaModLoadingContext.get().getModEventBus();
    public static final Logger LOGGER = LogManager.getLogger(CrimsonChickens.MOD_ID);

    public CrimsonChickens() {
        MOD_EVENTBUS.addListener(this::doSetup);
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> MOD_EVENTBUS.addListener(this::doClientStuff));

        entitiesInit.ENTITIES.register(MOD_EVENTBUS);
        itemsInit.ITEMS.register(MOD_EVENTBUS);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void doSetup(final FMLCommonSetupEvent event) {
        DeferredWorkQueue.runLater(() -> {
            GlobalEntityTypeAttributes.put(entitiesInit.BONE_CHICKEN.get(), BoneChickenEntity.func_234187_eI_().create());
            GlobalEntityTypeAttributes.put(entitiesInit.LAPIS_CHICKEN.get(), LapisChickenEntity.func_234187_eI_().create());
            GlobalEntityTypeAttributes.put(entitiesInit.IRON_CHICKEN.get(), IronChickenEntity.func_234187_eI_().create());
            GlobalEntityTypeAttributes.put(entitiesInit.REDSTONE_CHICKEN.get(), RedstoneChickenEntity.func_234187_eI_().create());
            GlobalEntityTypeAttributes.put(entitiesInit.COAL_CHICKEN.get(), CoalChickenEntity.func_234187_eI_().create());
            GlobalEntityTypeAttributes.put(entitiesInit.GLOW_CHICKEN.get(), GlowstoneChickenEntity.func_234187_eI_().create());
        });
    }

    @OnlyIn(Dist.CLIENT)
    private void doClientStuff(final FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(entitiesInit.BONE_CHICKEN.get(), BoneChickenRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(entitiesInit.LAPIS_CHICKEN.get(), LapisChickenRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(entitiesInit.IRON_CHICKEN.get(), IronChickenRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(entitiesInit.REDSTONE_CHICKEN.get(), RedstoneChickenRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(entitiesInit.COAL_CHICKEN.get(), CoalChickenRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(entitiesInit.GLOW_CHICKEN.get(), GlowstoneChickenRenderer::new);
    }

    public static final ItemGroup TAB = new ItemGroup(CrimsonChickens.MOD_ID) {
        @OnlyIn(Dist.CLIENT)
        @Override
        public ItemStack createIcon() { return new ItemStack(Items.CHICKEN_SPAWN_EGG); }
    };
}

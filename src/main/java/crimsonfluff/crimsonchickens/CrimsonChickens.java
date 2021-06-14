package crimsonfluff.crimsonchickens;

import crimsonfluff.crimsonchickens.entity.ResourceChickenEntity;
import crimsonfluff.crimsonchickens.entity.ResourceChickenRenderer;
import crimsonfluff.crimsonchickens.init.LoadChickensConfigs;
import crimsonfluff.crimsonchickens.init.ModEntities;
import crimsonfluff.crimsonchickens.init.ModItems;
import crimsonfluff.crimsonchickens.json.ResourceChickenData;
import crimsonfluff.crimsonchickens.registry.ChickenRegistry;
import crimsonfluff.crimsonchickens.registry.RegistryHandler;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

@Mod(CrimsonChickens.MOD_ID)
public class CrimsonChickens {
    public static final String MOD_ID = "crimsonchickens";
    public static final Logger LOGGER = LogManager.getLogger(CrimsonChickens.MOD_ID);
    public static final ConfigBuilder CONFIGURATION = new ConfigBuilder();

    private final IEventBus MOD_EVENTBUS = FMLJavaModLoadingContext.get().getModEventBus();


    public CrimsonChickens() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CONFIGURATION.COMMON);
//        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CONFIGURATION.CLIENT);

        ModItems.ITEMS.register(MOD_EVENTBUS);
        RegistryHandler.ENTITY_TYPES.register(MOD_EVENTBUS);
        LoadChickensConfigs.loadConfigs();

        MOD_EVENTBUS.addListener(RegistryHandler::onEntityAttributeCreationEvent);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> MOD_EVENTBUS.addListener(this::onFMLClientSetupEvent));

        MinecraftForge.EVENT_BUS.register(this);
    }


    @OnlyIn(Dist.CLIENT)
    private void onFMLClientSetupEvent(final FMLClientSetupEvent event) {
        ModEntities.getModChickens().forEach((s, customChicken) -> RenderingRegistry.registerEntityRenderingHandler(customChicken.get(), manager -> {
            ResourceChickenData chickenData = ChickenRegistry.getRegistry().getChickenData(s);
            return new ResourceChickenRenderer(manager, chickenData);
        }));
    }

    public static final ItemGroup TAB = new ItemGroup(CrimsonChickens.MOD_ID) {
        @OnlyIn(Dist.CLIENT)
        @Override
        public ItemStack makeIcon() { return new ItemStack(Items.CHICKEN_SPAWN_EGG); }
    };

    @SubscribeEvent
    public void onPlayerEntityInteract(PlayerInteractEvent.EntityInteract event) {
    // fired once per hand !, so twice !!
        if (event.getSide().isClient()) return;

        if (event.getPlayer().getMainHandItem().isEmpty()) return;
        if (event.getPlayer().getUsedItemHand() != event.getHand()) return;     // !!
        if (! CONFIGURATION.masterSwitchAllowConvertingVanilla.get()) return;

        if (event.getTarget() instanceof ChickenEntity) {
            if (event.getTarget() instanceof ResourceChickenEntity) return;

            ChickenEntity targetChicken = (ChickenEntity) event.getTarget();

            for (Map.Entry<String, RegistryObject<EntityType<? extends ResourceChickenEntity>>> entry : ModEntities.getModChickens().entrySet()) {
                String name = entry.getKey();
                ResourceChickenData chickenData = ChickenRegistry.getRegistry().getChickenData(name);

                if (chickenData.dropItemItem == event.getPlayer().getMainHandItem().getItem()) {
                    targetChicken.getPersistentData().putInt("conversion", targetChicken.getPersistentData().getInt("conversion") + 1);

                    if (targetChicken.getPersistentData().getInt("conversion") == chickenData.conversion) {
                        targetChicken.remove();

                        event.getWorld().playSound(null, event.getPos(), SoundEvents.GENERIC_EXPLODE, SoundCategory.AMBIENT, 1f, 1f);
                        ((ServerWorld) event.getWorld()).sendParticles(ParticleTypes.POOF,
                            event.getPos().getX() + 0.5,
                            event.getPos().getY() + 0.5,
                            event.getPos().getZ() + 0.5,
                            200, 1, 1, 1, 0);

                        ModEntities.getModChickens().get(chickenData.name).get()
                            .spawn((ServerWorld) event.getWorld(), null, null, event.getPos(), SpawnReason.SPAWN_EGG, false, false);

                        if (! event.getPlayer().isCreative())
                            event.getPlayer().getMainHandItem().shrink(1);

                        return;
                    }
                }
            }
        }
    }
}

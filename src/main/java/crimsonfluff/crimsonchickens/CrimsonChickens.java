package crimsonfluff.crimsonchickens;

import crimsonfluff.crimsonchickens.entity.ResourceChickenEntity;
import crimsonfluff.crimsonchickens.init.*;
import crimsonfluff.crimsonchickens.items.SupplierSpawnEggItem;
import crimsonfluff.crimsonchickens.json.ResourceChickenData;
import crimsonfluff.crimsonchickens.registry.ChickenRegistry;
import crimsonfluff.crimsonchickens.registry.RegistryHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShearsItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

//  /forge entity list
//  /kill @e[type=!player]
//  /effect give @e[type=crimsonchickens:coal_chicken] minecraft:glowing 60
//  /effect give @e[type=crimsonchickens:soulsand_chicken] minecraft:glowing 60
//  /effect give @e[type=crimsonchickens:enderman_chicken] minecraft:glowing 60

@Mod(CrimsonChickens.MOD_ID)
public class CrimsonChickens {
    public static final String MOD_ID = "crimsonchickens";
    public static final Logger LOGGER = LogManager.getLogger(CrimsonChickens.MOD_ID);
    public static final initConfigBuilder CONFIGURATION = new initConfigBuilder();

    public CrimsonChickens() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CONFIGURATION.COMMON);

        IEventBus MOD_EVENTBUS = FMLJavaModLoadingContext.get().getModEventBus();
        initSounds.SOUNDS.register(MOD_EVENTBUS);       // NOTE: Remember the sounds.json file !!
        initItems.ITEMS.register(MOD_EVENTBUS);
        RegistryHandler.ENTITY_TYPES.register(MOD_EVENTBUS);
        initChickenConfigs.loadConfigs();

        MOD_EVENTBUS.addListener(this::loadComplete);
        MOD_EVENTBUS.addListener(RegistryHandler::onEntityAttributeCreationEvent);

        MinecraftForge.EVENT_BUS.addListener(this::onBiomeLoadingEvent);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ClientEventHandlers::clientStuff);

        MinecraftForge.EVENT_BUS.register(this);
    }

    public void onBiomeLoadingEvent(BiomeLoadingEvent event) {
        initEntities.getModChickens().forEach((s, resourceChicken) -> {
            ResourceChickenData chickenData = ChickenRegistry.getRegistry().getChickenData(s);

            if (chickenData.spawnNaturally) {
                String biomeString = '"' + event.getName().toString() + '"';
                EntityClassification classType;

                switch (chickenData.spawnType) {
                    default:
                    case 0:
                        classType = EntityClassification.CREATURE;
                        break;
                    case 1:
                        classType = EntityClassification.MONSTER;
                        break;
                }

                if (chickenData.biomesWhitelist != null && chickenData.biomesWhitelist.size() != 0) {
                    if (chickenData.biomesWhitelist.toString().contains(biomeString)) {
                        LOGGER.info("BIOME_WHITELIST: " + biomeString + " : " + s);

                        event.getSpawns().getSpawner(classType).add(new MobSpawnInfo.Spawners(resourceChicken.get(), chickenData.spawnWeight, 2, 4));
                    }

                } else if (chickenData.biomesBlacklist != null && chickenData.biomesBlacklist.size() != 0) {
                    if (! chickenData.biomesWhitelist.toString().contains(biomeString)) {
                        LOGGER.info("BIOME_BLACKLIST: " + biomeString + " : " + s);

                        event.getSpawns().getSpawner(classType).add(new MobSpawnInfo.Spawners(resourceChicken.get(), chickenData.spawnWeight, 2, 4));
                    }

                } else {
                    LOGGER.info("BIOME_NATURAL: " + biomeString + " : " + s);

                    event.getSpawns().getSpawner(classType).add(new MobSpawnInfo.Spawners(resourceChicken.get(), chickenData.spawnWeight, 2, 4));
                }
            }
        });
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

        Entity entityTarget = event.getTarget();

        if (entityTarget instanceof ChickenEntity) {
            PlayerEntity player = event.getPlayer();

            if (player.getMainHandItem().getItem() instanceof ShearsItem) {
                if (CONFIGURATION.allowShearingChickens.get()) {
                    player.getMainHandItem().hurtAndBreak(1, player, playerIn -> {
                        playerIn.broadcastBreakEvent(event.getHand());
                    });

                    World world = event.getWorld();
                    BlockPos pos = event.getPos();

                    entityTarget.hurt(new DamageSource("death.attack.shears"), 1);
                    ((ServerWorld) world).sendParticles(ParticleTypes.CRIT, pos.getX(), pos.getY() + entityTarget.getEyeHeight(), pos.getZ(), 10, 0.5, 0.5, 0.5, 0);

                    world.playSound(null, pos, SoundEvents.SHEEP_SHEAR, SoundCategory.PLAYERS, 1f, 1f);
                    if (entityTarget instanceof ResourceChickenEntity) {
                        if (((ResourceChickenEntity) entityTarget).chickenData.hasTrait == 1) {
                            world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(initItems.FEATHER_DUCK.get())));
                            return;
                        }
                    }

                    world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.FEATHER)));
                }

                return;
            }

            if (event.getTarget() instanceof ResourceChickenEntity) return;

            ChickenEntity targetChicken = (ChickenEntity) event.getTarget();

            for (Map.Entry<String, RegistryObject<EntityType<? extends ResourceChickenEntity>>> entry : initEntities.getModChickens().entrySet()) {
                String name = entry.getKey();
                ResourceChickenData chickenData = ChickenRegistry.getRegistry().getChickenData(name);

                if (chickenData.dropItemItem == event.getPlayer().getMainHandItem().getItem()) {
                    CompoundNBT parentNBT = targetChicken.getPersistentData();
                    CompoundNBT NBT = parentNBT.getCompound("Mutation");

                    // if converting a partly already converted chicken then reset type/count
                    if (! chickenData.dropItemItem.getRegistryName().toString().equals(NBT.getString("type"))) {
                        targetChicken.getPersistentData().remove("Mutation");
//                        CrimsonChickens.LOGGER.info("Removed Mutation");
                        NBT = new CompoundNBT();
                    }

                    //  Store type/count were converting chicken into
                    NBT.putString("type", chickenData.dropItemItem.getRegistryName().toString());
                    NBT.putInt("count", NBT.getInt("count") + 1);
                    targetChicken.getPersistentData().put("Mutation", NBT);

                    if (NBT.getInt("count") >= chickenData.conversion) {
                        targetChicken.remove();

                        event.getWorld().playSound(null, event.getPos(), SoundEvents.GENERIC_EXPLODE, SoundCategory.AMBIENT, 1f, 1f);
                        ((ServerWorld) event.getWorld()).sendParticles(ParticleTypes.POOF,
                            event.getPos().getX() + 0.5,
                            event.getPos().getY() + 0.5,
                            event.getPos().getZ() + 0.5,
                            200, 1, 1, 1, 0);

                        ResourceChickenEntity newChick = initEntities.getModChickens().get(chickenData.name).get().create(event.getWorld());
//                        newChick.setPos(targetChicken.position().x, targetChicken.position().y(), targetChicken.position().z());
                        newChick.copyPosition(targetChicken);

                        if (targetChicken.hasCustomName()) {
                            newChick.setCustomName(targetChicken.getCustomName());
                            newChick.setCustomNameVisible(targetChicken.isCustomNameVisible());
                        }

                        newChick.setInvulnerable(targetChicken.isInvulnerable());

//                        newChick.setYHeadRot(targetChicken.getYHeadRot());
                        event.getWorld().addFreshEntity(newChick);
                    }

                    if (! event.getPlayer().isCreative())
                        event.getPlayer().getMainHandItem().shrink(1);

                    if (NBT.getInt("count") % 4 == 0) {
                        ((ServerWorld) event.getWorld()).sendParticles(ParticleTypes.HAPPY_VILLAGER,
                            event.getPos().getX() + 0.5,
                            event.getPos().getY() + 0.5,
                            event.getPos().getZ() + 0.5,
                            200, 1, 1, 1, 0);
                        //this.level.addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 0.0D, 0.0D, 0.0D);
                    }
                }
            }
        }
    }

    private void loadComplete(FMLLoadCompleteEvent event) {
        SupplierSpawnEggItem.initSpawnEggs();
    }

//    @SubscribeEvent
//    public void registerNetherWorldSpawn(WorldEvent.PotentialSpawns event) {
//        if (event.getType() == EntityClassification.MONSTER) {
////            LOGGER.info("EVER HERE");
//
//            entitiesInit.getModChickens().forEach((s, resourceChicken) -> {
//                event.getList().add(new MobSpawnInfo.Spawners(resourceChicken.get(), 100, 10, 20));
//            });
//        }
//    }
}

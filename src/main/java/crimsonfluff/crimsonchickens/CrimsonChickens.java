package crimsonfluff.crimsonchickens;

import crimsonfluff.crimsonchickens.compat.compatTOP;
import crimsonfluff.crimsonchickens.entity.AngryChickenEntity;
import crimsonfluff.crimsonchickens.entity.ResourceChickenEntity;
import crimsonfluff.crimsonchickens.init.*;
import crimsonfluff.crimsonchickens.items.SupplierSpawnEggItem;
import crimsonfluff.crimsonchickens.json.ResourceChickenData;
import crimsonfluff.crimsonchickens.registry.ChickenRegistry;
import crimsonfluff.crimsonchickens.registry.RegistryHandler;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Random;

//  /forge entity list
//  /kill @e[type=!player]
//  /effect give @e[type=crimsonchickens:coal_chicken] minecraft:glowing 60
//  /effect give @e[type=crimsonchickens:soulsand_chicken] minecraft:glowing 60
//  /effect give @e[type=crimsonchickens:enderman_chicken] minecraft:glowing 60
//  /summon crimsonchickens:blaze_chicken ~ ~ ~ {Age:-24000,analyzed:1,strength:10,gain:10,growth:10}
//  /summon crimsonchickens:blaze_chicken ~ ~ ~ {analyzed:1,strength:10,gain:10,growth:10}

// Spawn_Egg Background is the smaller parts (the spots) Foreground is the larger part

@Mod(CrimsonChickens.MOD_ID)
public class CrimsonChickens {
    public static final String MOD_ID = "crimsonchickens";
    public static final Logger LOGGER = LogManager.getLogger(CrimsonChickens.MOD_ID);
    public static final initConfigBuilder CONFIGURATION = new initConfigBuilder();

    public CrimsonChickens() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CONFIGURATION.COMMON);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CONFIGURATION.CLIENT);

        IEventBus MOD_EVENTBUS = FMLJavaModLoadingContext.get().getModEventBus();
        initSounds.SOUNDS.register(MOD_EVENTBUS);
        initItems.ITEMS.register(MOD_EVENTBUS);
        initBlocks.BLOCKS.register(MOD_EVENTBUS);
        initTiles.TILES.register(MOD_EVENTBUS);
        RegistryHandler.ENTITY_TYPES.register(MOD_EVENTBUS);

        initChickenConfigs.loadConfigs();

        MOD_EVENTBUS.addListener(this::onInterModEnqueueEvent);
        MOD_EVENTBUS.addListener(this::onFMLLoadCompleteEvent);
        MOD_EVENTBUS.addListener(RegistryHandler::onEntityAttributeCreationEvent);

        MinecraftForge.EVENT_BUS.addListener(this::onBiomeLoadingEvent);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ClientEventHandlers::clientStuff);

        MinecraftForge.EVENT_BUS.register(this);
    }

    public void onBiomeLoadingEvent(BiomeLoadingEvent event) {
        initEntities.getModChickens().forEach((s, resourceChicken) -> {
            ResourceChickenData chickenData = ChickenRegistry.getRegistry().getChickenData(s);

            if (chickenData.spawnNaturally) {
                if (s.equals("chicken")) {
                    List<MobSpawnInfo.Spawners> spawns = event.getSpawns().getSpawner(EntityClassification.CREATURE);

//        spawns.forEach(mob-> {
//            if (mob.type == EntityType.CHICKEN)
//                CrimsonChickens.LOGGER.info("CHICKEN_SPAWN: " + mob.weight + " : " + mob.minCount + " : " + mob.maxCount);
//        });

                    if (spawns.removeIf(e -> e.type == EntityType.CHICKEN))
                        spawns.add(new MobSpawnInfo.Spawners(resourceChicken.get(), chickenData.spawnWeight, 4, 4));    // same as vanilla

                } else {
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

                    if (chickenData.biomesWhitelist != null) {
                        if (chickenData.biomesWhitelist.toString().contains(biomeString)) {
                            //LOGGER.info("BIOME_WHITELIST: " + biomeString + " : " + s);

                            event.getSpawns().getSpawner(classType).add(new MobSpawnInfo.Spawners(resourceChicken.get(), chickenData.spawnWeight, 1, 4));
                        }

                    } else if (chickenData.biomesBlacklist != null) {
                        if (! chickenData.biomesBlacklist.toString().contains(biomeString)) {
                            //LOGGER.info("BIOME_BLACKLIST: " + biomeString + " : " + s);

                            event.getSpawns().getSpawner(classType).add(new MobSpawnInfo.Spawners(resourceChicken.get(), chickenData.spawnWeight, 1, 4));
                        }

                    } else {
                        //LOGGER.info("BIOME_NATURAL: " + biomeString + " : " + s);

                        event.getSpawns().getSpawner(classType).add(new MobSpawnInfo.Spawners(resourceChicken.get(), chickenData.spawnWeight, 1, 4));
                    }
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
    public void onEntityJoinWorldEvent(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof ResourceChickenEntity) return;
        if (event.getEntity() instanceof AngryChickenEntity) return;

        // from spawners, spawn_eggs
        if (event.getEntity() instanceof ChickenEntity) {
            event.setCanceled(true);

            ResourceChickenData chickenData = ChickenRegistry.getRegistry().getChickenData("chicken");
            if (chickenData != null) {
                if (chickenData.enabled && chickenData.spawnNaturally) {
                    ResourceChickenEntity newChicken = initEntities.getModChickens().get("chicken").get().create(event.getWorld());
                    if (newChicken != null) {
                        // copy all/any NBT to new chicken, isBaby, Invulnerable, CustomName etc
                        CompoundNBT compound = new CompoundNBT();
                        event.getEntity().save(compound);

                        compound.putBoolean("analyzed", true);
                        compound.putInt("growth", 1);
                        compound.putInt("gain", 1);
                        compound.putInt("strength", 1);

                        newChicken.load(compound);

                        newChicken.copyPosition(event.getEntity());
                        event.getWorld().addFreshEntity(newChicken);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onEntityDeath(LivingDeathEvent event) {
        if (event.getEntity().level instanceof ServerWorld) {
            if (event.getEntity() instanceof PlayerEntity) {
                if (initEntities.getModChickens().containsKey("grave")) {   // must be enabled else it wouldn't be loaded into the list
                    ResourceChickenEntity newChicken = initEntities.getModChickens().get("grave").get().create(event.getEntity().level);

                    if (newChicken != null) {
                        PlayerEntity playerIn = (PlayerEntity) event.getEntity();

                        newChicken.setCustomName(playerIn.getDisplayName().copy()
                            .append(": Death #")
                            .append("" + ((ServerPlayerEntity) playerIn).getStats().getValue(Stats.CUSTOM.get(Stats.DEATHS))));

                        newChicken.copyPosition(event.getEntity());
                        newChicken.setPersistenceRequired();
                        newChicken.getPersistentData().put("Inventory", playerIn.inventory.save(new ListNBT()));

                        event.getEntity().level.addFreshEntity(newChicken);

                        // Note: might not work well with modded 'SoulBound' items ?
                        playerIn.inventory.items.clear();
                        playerIn.inventory.armor.clear();
                        playerIn.inventory.offhand.clear();
                    }
                }
            }
        }
    }

    private void onFMLLoadCompleteEvent(FMLLoadCompleteEvent event) {
        SupplierSpawnEggItem.initSpawnEggs();
    }

    private void onInterModEnqueueEvent(final InterModEnqueueEvent event) {
        if (ModList.get().isLoaded("theoneprobe")) compatTOP.register();
    }

    public static int calcNewEggLayTime(Random r, ResourceChickenData rcd, int growth) {
        if (rcd.eggLayTime == 0) return 0;

        int egg = r.nextInt(rcd.eggLayTime) + rcd.eggLayTime;
        return (int) Math.max(1.0f, (egg * (10.f - growth + 1.f)) / 10.f);
    }

    public static int calcDropQuantity(int gain) {
        if (gain < 5) return 1;         // between 1-4
        if (gain < 10) return 2;        // between 5-9
        return 3;                       // 10
    }

    public static List<ItemStack> calcDrops(int gain, ResourceChickenData chickenData, int fortune) {
        // return a list of item drops
        // done like this to avoid making stacks of non-stackable items
        List<ItemStack> lst = NonNullList.create();

        // TODO: if no drop item then try and find a loot table?
        if (! chickenData.dropItemItem.equals("")) {
            ItemStack itemStack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(chickenData.dropItemItem)));

            if (! itemStack.isEmpty()) {
                if (chickenData.dropItemNBT != null) itemStack.setTag(chickenData.dropItemNBT.copy());
                int dropQuantity = calcDropQuantity(gain) + fortune;

                if (itemStack.isStackable()) {
                    itemStack.setCount(dropQuantity);

                    lst.add(itemStack);
                }
                else {
                    for (int a = 0; a < dropQuantity; a++) {
                        ItemStack itm = itemStack.copy();
                        lst.add(itm);
                    }
                }
            }
        }

        Random r = new Random();
        if (r.nextInt(8) == 0) lst.add(chickenData.hasTrait == 1 ? new ItemStack(initItems.FEATHER_DUCK.get()) : new ItemStack(Items.FEATHER));

        return lst;
    }
}

package crimsonfluff.crimsonchickens;

import crimsonfluff.crimsonchickens.compat.compatTOP;
import crimsonfluff.crimsonchickens.entity.ResourceChickenEntity;
import crimsonfluff.crimsonchickens.init.*;
import crimsonfluff.crimsonchickens.json.ResourceChickenData;
import crimsonfluff.crimsonchickens.registry.ChickenRegistry;
import crimsonfluff.crimsonchickens.registry.RegistryHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
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
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
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

    //public static final ModelLayerLocation CHICKEN_NO_FEET = new ModelLayerLocation(new ResourceLocation(CrimsonChickens.MOD_ID, "chicken.nofeet"), "chicken.nofeet");

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
                    List<MobSpawnSettings.SpawnerData> spawns = event.getSpawns().getSpawner(MobCategory.CREATURE);

//        spawns.forEach(mob-> {
//            if (mob.type == EntityType.CHICKEN)
//                CrimsonChickens.LOGGER.info("CHICKEN_SPAWN: " + mob.weight + " : " + mob.minCount + " : " + mob.maxCount);
//        });

                    if (spawns.removeIf(e -> e.type == EntityType.CHICKEN))
                        spawns.add(new MobSpawnSettings.SpawnerData(resourceChicken.get(), chickenData.spawnWeight, 4, 4));    // same as vanilla

                } else {
                    String biomeString = '"' + event.getName().toString() + '"';
                    MobCategory classType;

                    switch (chickenData.spawnType) {
                        default:
                        case 0:
                            classType = MobCategory.CREATURE;
                            break;

                        case 1:
                            classType = MobCategory.MONSTER;
                            break;
                    }

                    if (chickenData.biomesWhitelist != null) {
                        if (chickenData.biomesWhitelist.toString().contains(biomeString)) {
                            //LOGGER.info("BIOME_WHITELIST: " + biomeString + " : " + s);

                            event.getSpawns().getSpawner(classType).add(new MobSpawnSettings.SpawnerData(resourceChicken.get(), chickenData.spawnWeight, 1, 4));
                        }

                    } else if (chickenData.biomesBlacklist != null) {
//                        if (! chickenData.biomesWhitelist.toString().contains(biomeString)) {
                        if (! chickenData.biomesBlacklist.toString().contains(biomeString)) {
                            //LOGGER.info("BIOME_BLACKLIST: " + biomeString + " : " + s);

                            event.getSpawns().getSpawner(classType).add(new MobSpawnSettings.SpawnerData(resourceChicken.get(), chickenData.spawnWeight, 1, 4));
                        }

                    } else {
                        //LOGGER.info("BIOME_NATURAL: " + biomeString + " : " + s);

                        event.getSpawns().getSpawner(classType).add(new MobSpawnSettings.SpawnerData(resourceChicken.get(), chickenData.spawnWeight, 1, 4));
                    }
                }
            }
        });
    }

    public static final CreativeModeTab TAB = new CreativeModeTab(CrimsonChickens.MOD_ID) {
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

        if (! (event.getTarget() instanceof ResourceChickenEntity)) return;


        ResourceChickenEntity targetChicken = (ResourceChickenEntity) event.getTarget();
        Player player = event.getPlayer();

        if (player.getMainHandItem().getItem() instanceof ShearsItem) {
            if (CONFIGURATION.allowShearingChickens.get()) {
                player.getMainHandItem().hurtAndBreak(1, player, playerIn -> {
                    playerIn.broadcastBreakEvent(event.getHand());
                });

                Level world = event.getWorld();
                BlockPos pos = event.getPos();

                targetChicken.hurt(new DamageSource("death.attack.shears"), 1);
                ((ServerLevel) world).sendParticles(ParticleTypes.CRIT, pos.getX(), pos.getY() + targetChicken.getEyeHeight(), pos.getZ(), 10, 0.5, 0.5, 0.5, 0);

                world.playSound(null, pos, SoundEvents.SHEEP_SHEAR, SoundSource.PLAYERS, 1f, 1f);

                world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(),
                    targetChicken.chickenData.hasTrait == 1
                    ? new ItemStack(initItems.FEATHER_DUCK.get())
                    : new ItemStack(Items.FEATHER)));
            }

            return;
        }

        // are we converting a chicken?
        // only allow converting 'vanilla' chickens

        if (! CONFIGURATION.allowConvertingVanilla.get()) return;
        if (! targetChicken.chickenData.name.equals("chicken")) return;

        for (Map.Entry<String, RegistryObject<EntityType<? extends ResourceChickenEntity>>> entry : initEntities.getModChickens().entrySet()) {
            String name = entry.getKey();
            ResourceChickenData chickenData = ChickenRegistry.getRegistry().getChickenData(name);

            if (chickenData.dropItemItem.equals(event.getPlayer().getMainHandItem().getItem().getRegistryName().toString())) {
                CompoundTag parentNBT = targetChicken.getPersistentData();
                CompoundTag NBT = parentNBT.getCompound("Mutation");

                // if converting a partly already converted chicken then reset/remove type/count
                if (! chickenData.dropItemItem.equals(NBT.getString("type"))) {
                    targetChicken.getPersistentData().remove("Mutation");
                    NBT = new CompoundTag();
                }

                //  Store type/count were converting chicken into
                NBT.putString("type", chickenData.dropItemItem);
                NBT.putInt("count", NBT.getInt("count") + 1);
                targetChicken.getPersistentData().put("Mutation", NBT);

                if (NBT.getInt("count") >= chickenData.conversion) {
                    targetChicken.remove(Entity.RemovalReason.DISCARDED);

                    event.getWorld().playSound(null, event.getPos(), SoundEvents.GENERIC_EXPLODE, SoundSource.AMBIENT, 1f, 1f);
                    ((ServerLevel) event.getWorld()).sendParticles(ParticleTypes.POOF,
                        event.getPos().getX() + 0.5,
                        event.getPos().getY() + 0.5,
                        event.getPos().getZ() + 0.5,
                        200, 1, 1, 1, 0);

                    ResourceChickenEntity newChick = initEntities.getModChickens().get(chickenData.name).get().create(event.getWorld());
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
                    ((ServerLevel) event.getWorld()).sendParticles(ParticleTypes.HAPPY_VILLAGER,
                        event.getPos().getX() + 0.5,
                        event.getPos().getY() + 0.5,
                        event.getPos().getZ() + 0.5,
                        200, 1, 1, 1, 0);
                }
            }
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorldEvent(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof ResourceChickenEntity) return;

        // from spawners, spawn_eggs
        if (event.getEntity() instanceof Chicken) {
            event.setCanceled(true);

            ResourceChickenData chickenData = ChickenRegistry.getRegistry().getChickenData("chicken");
            if (chickenData != null) {
                if (chickenData.enabled && chickenData.spawnNaturally) {
                    ResourceChickenEntity newChicken = initEntities.getModChickens().get("chicken").get().create(event.getWorld());
                    if (newChicken != null) {
                        // copy all/any NBT to new chicken, isBaby, Invulnerable, CustomName etc
                        CompoundTag compound = new CompoundTag();
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
        if (event.getEntity().level instanceof ServerLevel) {
            if (event.getEntity() instanceof Player) {
                if (initEntities.getModChickens().containsKey("grave")) {   // must be enabled else it wouldn't be loaded into the list
                    ResourceChickenEntity newChicken = initEntities.getModChickens().get("grave").get().create(event.getEntity().level);

                    if (newChicken != null) {
                        Player playerIn = (Player) event.getEntity();

                        newChicken.setCustomName(playerIn.getDisplayName().copy()
                            .append(": Death #")
                            .append("" + ((ServerPlayer) playerIn).getStats().getValue(Stats.CUSTOM.get(Stats.DEATHS))));

                        newChicken.copyPosition(event.getEntity());
                        newChicken.setPersistenceRequired();
                        newChicken.getPersistentData().put("Inventory", playerIn.getInventory().save(new ListTag()));

                        event.getEntity().level.addFreshEntity(newChicken);

                        // Note: might not work well with modded 'SoulBound' items ?
                        playerIn.getInventory().items.clear();
                        playerIn.getInventory().armor.clear();
                        playerIn.getInventory().offhand.clear();
                    }
                }
            }
        }
    }

    private void onFMLLoadCompleteEvent(FMLLoadCompleteEvent event) {
        //SupplierSpawnEggItem.initSpawnEggs();
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

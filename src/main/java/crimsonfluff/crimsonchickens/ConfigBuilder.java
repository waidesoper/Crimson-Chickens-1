package crimsonfluff.crimsonchickens;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigBuilder {
    public final ForgeConfigSpec COMMON;
//    public final ForgeConfigSpec CLIENT;

    public ForgeConfigSpec.IntValue masterSwitchBreeding;
    public ForgeConfigSpec.BooleanValue masterSwitchBreedingItem;
    public ForgeConfigSpec.IntValue allowBreedingWithVanilla;
    public ForgeConfigSpec.IntValue allowDeathDropResource;
    public ForgeConfigSpec.BooleanValue masterSwitchAllowConvertingVanilla;

    public ForgeConfigSpec.BooleanValue allowFakeplayerBreeding;
    public ForgeConfigSpec.BooleanValue allowFakeplayerLootDrops;


    public ConfigBuilder() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("All Chickens");

        masterSwitchBreeding = builder
            .comment("Allow all chickens to breed.  0=no breeding, 1=all can breed, 2=set by individual configs.  Default: 2")
            .defineInRange("masterSwitchBreeding", 2,0,2);

        masterSwitchBreedingItem = builder
            .comment("All chickens require their drop item for food/breeding.  Default: false")
            .define("masterSwitchBreedingItem", false);

        allowBreedingWithVanilla = builder
            .comment("% Chance that breeding with vanilla chickens produce resource chickens. 0=Don't produce resource chickens.  Default: 80")
            .defineInRange("allowBreedingWithVanilla", 80, 0, 100);

        // Per Chicken now, in their config
        masterSwitchAllowConvertingVanilla = builder
            .comment("Allow conversion of vanilla chickens to resource chickens.  Default: true")
            .define("masterSwitchAllowConvertingVanilla", true);

        allowDeathDropResource = builder
            .comment("% Chance that resource is dropped upon death. 0=No resource dropped.  Default: 80")
            .defineInRange("allowDeathDropResource", 80, 0, 100);

        builder.pop();


        builder.push("Fake Player Interaction");

        allowFakeplayerBreeding = builder
            .comment("Allow Fakeplayer/automation of resource chicken breeding.  Default: true")
            .define("allowFakeplayerBreeding", true);

        allowFakeplayerLootDrops = builder
            .comment("Allow Fakeplayer kills to produce loot drops.  Default: true")
            .define("allowFakeplayerLootDrops", true);

        builder.pop();


        COMMON = builder.build();


    // CLIENT
//        builder = new ForgeConfigSpec.Builder();
//
//        jarCommunityEnable = builder
//            .comment("Enable the Community Jar.  Default: true")
//            .define("jarCommunityEnable",true);
//
//        builder.pop();
//        CLIENT = builder.build();
    }
}

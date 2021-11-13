package crimsonfluff.crimsonchickens.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "crimsonchickens")
public class CrimsonChickensConfig implements ConfigData {
//    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Category(value = "chickens")
//    @ConfigEntry.Gui.Tooltip()
    @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
    public int allowDeathDropResource = 80;

    @ConfigEntry.Category(value = "chickens")
//    @ConfigEntry.Gui.Tooltip()
    @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
    public int allowBreedingWithVanilla = 80;

    @ConfigEntry.Category(value = "chickens")
    public boolean allowCrossBreeding = true;

    @ConfigEntry.Category(value = "chickens")
    @ConfigEntry.BoundedDiscrete(min = 0, max = 2)
    public int masterSwitchBreeding = 2;

    @ConfigEntry.Category(value = "chickens")
    public boolean dropAsBreedingItem = false;

    @ConfigEntry.Category(value = "chickens")
    public boolean allowConvertingVanilla = true;

//    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Category(value = "all")
    public boolean analyzeChickens = false;

    @ConfigEntry.Category(value = "all")
    public boolean allowShearingChickens = true;

//    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Category(value = "fakeplayers")
    public boolean allowFakeplayerLootDrops = true;

    @ConfigEntry.Category(value = "fakeplayers")
    public boolean allowFakeplayerBreeding = true;
}

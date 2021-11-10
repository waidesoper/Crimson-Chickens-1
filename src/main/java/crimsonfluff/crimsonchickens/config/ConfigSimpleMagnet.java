package crimsonfluff.crimsonchickens.config;

import me.shedaniel.autoconfig.annotation.ConfigEntry;

public class ConfigSimpleMagnet {
    @ConfigEntry.Category("General Settings")
    @ConfigEntry.Gui.CollapsibleObject
    @ConfigEntry.Gui.Tooltip
    public ConfigGeneral general = new ConfigGeneral();

}

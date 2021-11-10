package crimsonfluff.crimsonchickens.config;

import me.shedaniel.autoconfig.annotation.ConfigEntry;

public class ConfigGeneral {

    @ConfigEntry.Gui.Tooltip()
    public int pullRadius = 16;

    @ConfigEntry.Gui.Tooltip()
    public int maxDamage = 256;

}

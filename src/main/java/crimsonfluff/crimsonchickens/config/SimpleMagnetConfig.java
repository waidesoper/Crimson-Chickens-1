package crimsonfluff.crimsonchickens.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "simplemagnet")
public class SimpleMagnetConfig implements ConfigData {
    @ConfigEntry.Category("Simple Magnet")
    @ConfigEntry.Gui.Tooltip()

    public ConfigSimpleMagnet simpleMagnet = new ConfigSimpleMagnet();
}

package crimsonfluff.crimsonchickens.init;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.TranslatableText;

public class initConfigs {
    public static void register() {
        ConfigBuilder builder = ConfigBuilder.create().setTitle(new TranslatableText("title.crimsonchickens.config"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
    }
}

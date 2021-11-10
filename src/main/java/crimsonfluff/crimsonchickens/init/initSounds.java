package crimsonfluff.crimsonchickens.init;

import crimsonfluff.crimsonchickens.CrimsonChickens;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class initSounds {
    public static final SoundEvent DUCK_AMBIENT = new SoundEvent(new Identifier(CrimsonChickens.MOD_ID, "duck.ambient"));
    public static final SoundEvent DUCK_DEATH = new SoundEvent(new Identifier(CrimsonChickens.MOD_ID, "duck.death"));
    public static final SoundEvent RADIATION = new SoundEvent(new Identifier(CrimsonChickens.MOD_ID, "radiation"));


    public static void register() {
        Registry.register(Registry.SOUND_EVENT, DUCK_AMBIENT.getId(), DUCK_AMBIENT);
        Registry.register(Registry.SOUND_EVENT, DUCK_DEATH.getId(), DUCK_DEATH);
        Registry.register(Registry.SOUND_EVENT, RADIATION.getId(), RADIATION);
    }
}

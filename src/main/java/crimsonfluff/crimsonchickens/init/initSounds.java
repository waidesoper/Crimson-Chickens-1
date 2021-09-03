package crimsonfluff.crimsonchickens.init;

import crimsonfluff.crimsonchickens.CrimsonChickens;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class initSounds {
        public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, CrimsonChickens.MOD_ID);

        public static final RegistryObject<SoundEvent> DUCK_AMBIENT = SOUNDS.register("duck.ambient",
                () -> new SoundEvent(new ResourceLocation(CrimsonChickens.MOD_ID, "duck.ambient")));

        public static final RegistryObject<SoundEvent> DUCK_DEATH = SOUNDS.register("duck.death",
                () -> new SoundEvent(new ResourceLocation(CrimsonChickens.MOD_ID, "duck.death")));

        public static final RegistryObject<SoundEvent> RADIATION = SOUNDS.register("radiation",
            () -> new SoundEvent(new ResourceLocation(CrimsonChickens.MOD_ID, "radiation")));
}

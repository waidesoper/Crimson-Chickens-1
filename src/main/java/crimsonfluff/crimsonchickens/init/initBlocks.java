package crimsonfluff.crimsonchickens.init;

import crimsonfluff.crimsonchickens.CrimsonChickens;
import crimsonfluff.crimsonchickens.blocks.Nest;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class initBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CrimsonChickens.MOD_ID);

    public static final RegistryObject<Block> NEST_BLOCK = BLOCKS.register("nest", Nest::new);

    public static final RegistryObject<Block> DUCK_EGG_BLOCK = BLOCKS.register("duck_egg_block", ()->
        new Block(BlockBehaviour.Properties.of(Material.EGG, MaterialColor.COLOR_LIGHT_GRAY)
            .requiresCorrectToolForDrops()
            .strength(0.5f)
            .sound(SoundType.CROP)
            ));

    public static final RegistryObject<Block> EGG_BLOCK = BLOCKS.register("egg_block", ()->
        new Block(BlockBehaviour.Properties.of(Material.EGG, MaterialColor.COLOR_YELLOW)
            .requiresCorrectToolForDrops()
            .strength(0.5f)
            .sound(SoundType.CROP)
            ));
}

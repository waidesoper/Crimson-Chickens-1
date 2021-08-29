package crimsonfluff.crimsonchickens.init;

import crimsonfluff.crimsonchickens.CrimsonChickens;
import crimsonfluff.crimsonchickens.blocks.Nest;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class initBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CrimsonChickens.MOD_ID);

    public static final RegistryObject<Block> NEST_BLOCK = BLOCKS.register("nest", Nest::new);

    public static final RegistryObject<Block> DUCK_EGG_BLOCK = BLOCKS.register("duck_egg_block", ()->
        new Block(AbstractBlock.Properties.of(Material.EGG, MaterialColor.COLOR_LIGHT_GRAY)
            .requiresCorrectToolForDrops()
            .strength(0.5f)
            .sound(SoundType.CROP)
            .harvestTool(ToolType.AXE)));

    public static final RegistryObject<Block> EGG_BLOCK = BLOCKS.register("egg_block", ()->
        new Block(AbstractBlock.Properties.of(Material.EGG, MaterialColor.COLOR_YELLOW)
            .requiresCorrectToolForDrops()
            .strength(0.5f)
            .sound(SoundType.CROP)
            .harvestTool(ToolType.AXE)));
}

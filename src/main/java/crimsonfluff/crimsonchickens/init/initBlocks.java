package crimsonfluff.crimsonchickens.init;

import crimsonfluff.crimsonchickens.CrimsonChickens;
import crimsonfluff.crimsonchickens.blocks.Nest;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class initBlocks {
    public static final Block NEST_BLOCK = new Nest();
    public static final Block EGG_BLOCK = new Block(AbstractBlock.Settings.of(Material.EGG, MapColor.LIGHT_GRAY)
        .requiresTool()
        .strength(0.5f)
        .sounds(BlockSoundGroup.CROP)
    );
    public static final Block DUCK_EGG_BLOCK = new Block(AbstractBlock.Settings.of(Material.EGG, MapColor.LIGHT_GRAY)
        .requiresTool()
        .strength(0.5f)
        .sounds(BlockSoundGroup.CROP)
    );

    public static void register() {
        Registry.register(Registry.BLOCK, new Identifier(CrimsonChickens.MOD_ID, "nest"), NEST_BLOCK);

        // TODO set harvest tool (AXE)
        Registry.register(Registry.BLOCK, new Identifier(CrimsonChickens.MOD_ID, "duck_egg_block"), EGG_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(CrimsonChickens.MOD_ID, "egg_block"), DUCK_EGG_BLOCK);
    }
}

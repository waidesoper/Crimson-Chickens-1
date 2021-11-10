package crimsonfluff.crimsonchickens.init;

import crimsonfluff.crimsonchickens.CrimsonChickens;
import crimsonfluff.crimsonchickens.blocks.NestTileEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class initTiles {

    public static BlockEntityType<NestTileEntity> NEST_BLOCK_TILE;

    public static void register() {
        NEST_BLOCK_TILE = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(CrimsonChickens.MOD_ID, "nest"), BlockEntityType.Builder.create(NestTileEntity::new, initBlocks.NEST_BLOCK).build(null));
    }
}

package crimsonfluff.crimsonchickens.init;

import crimsonfluff.crimsonchickens.CrimsonChickens;
import crimsonfluff.crimsonchickens.blocks.NestTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class initTiles {
    public static final DeferredRegister<TileEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, CrimsonChickens.MOD_ID);

    public static final RegistryObject<TileEntityType<NestTileEntity>> NEST_BLOCK_TILE = TILES.register(
        "nest_block_tile", () -> TileEntityType.Builder.of(NestTileEntity::new, initBlocks.NEST_BLOCK.get()).build(null));
}

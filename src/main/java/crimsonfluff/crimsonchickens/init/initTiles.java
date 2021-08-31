package crimsonfluff.crimsonchickens.init;

import crimsonfluff.crimsonchickens.CrimsonChickens;
import crimsonfluff.crimsonchickens.blocks.NestTileEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class initTiles {
    public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, CrimsonChickens.MOD_ID);

    public static final RegistryObject<BlockEntityType<NestTileEntity>> NEST_BLOCK_TILE = TILES.register(
        "nest_block_tile", () -> BlockEntityType.Builder.of(NestTileEntity::new, initBlocks.NEST_BLOCK.get()).build(null));
}

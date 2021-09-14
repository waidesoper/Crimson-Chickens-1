package crimsonfluff.crimsonchickens.blocks;

import crimsonfluff.crimsonchickens.compat.ITOPInfoProvider;
import crimsonfluff.crimsonchickens.init.initTiles;
import mcjty.theoneprobe.api.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.NameTagItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.stream.Stream;

public class Nest extends BaseEntityBlock implements ITOPInfoProvider {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public Nest() {
        super(BlockBehaviour.Properties.of(Material.GRASS, MaterialColor.COLOR_YELLOW)
            .strength(0.5f)
            .sound(SoundType.GRASS)
            .noOcclusion());

        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getShape(BlockState p_48816_, BlockGetter p_48817_, BlockPos p_48818_, CollisionContext p_48819_) {
        return Stream.of(Block.box(12, 2.1, 5, 13, 3.1, 7),
                Block.box(5, 0.1, 5, 11, 1.1, 11),
                Block.box(12, 1.1, 7, 13, 2.1, 9),
                Block.box(3, 1.1, 7, 4, 2.1, 9),
                Block.box(4, 0.1, 7, 5, 1.1, 9),
                Block.box(11, 0.1, 7, 12, 1.1, 9),
                Block.box(7, 1.1, 3, 9, 2.1, 4),
                Block.box(7, 0.1, 4, 9, 1.1, 5),
                Block.box(7, 0.1, 11, 9, 1.1, 12),
                Block.box(7, 1.1, 12, 9, 2.1, 13),
                Block.box(4, 1.1, 9, 5, 2.1, 11),
                Block.box(11, 1.1, 9, 12, 2.1, 11),
                Block.box(11, 2.1, 11, 12, 3.1, 12),
                Block.box(11, 2.1, 4, 12, 3.1, 5),
                Block.box(4, 2.1, 4, 5, 3.1, 5),
                Block.box(4, 2.1, 11, 5, 3.1, 12),
                Block.box(11, 1.1, 5, 12, 2.1, 7),
                Block.box(4, 1.1, 5, 5, 2.1, 7),
                Block.box(13, 2.1, 7, 14, 3.1, 9),
                Block.box(2, 2.1, 7, 3, 3.1, 9),
                Block.box(7, 2.1, 2, 9, 3.1, 3),
                Block.box(7, 2.1, 13, 9, 3.1, 14),
                Block.box(12, 2.1, 9, 13, 3.1, 11),
                Block.box(9, 2.1, 12, 11, 3.1, 13),
                Block.box(5, 2.1, 12, 7, 3.1, 13),
                Block.box(5, 1.1, 11, 7, 2.1, 12),
                Block.box(5, 1.1, 4, 7, 2.1, 5),
                Block.box(9, 1.1, 4, 11, 2.1, 5),
                Block.box(9, 1.1, 11, 11, 2.1, 12),
                Block.box(3, 2.1, 9, 4, 3.1, 11),
                Block.box(3, 2.1, 5, 4, 3.1, 7),
                Block.box(5, 2.1, 3, 7, 3.1, 4),
                Block.box(9, 2.1, 3, 11, 3.1, 4))
            .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    }

    @Override
    public void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, Random random) {
        BlockEntity blockEntity = serverLevel.getBlockEntity(blockPos);

        if (blockEntity instanceof NestTileEntity) ((NestTileEntity) blockEntity).tick();
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return ! level.isClientSide ? createTickerHelper(blockEntityType, initTiles.NEST_BLOCK_TILE.get(),
            (level1, blockPos, blockState1, te) -> te.tick()) : null;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    public RenderShape getRenderShape(BlockState state) {return RenderShape.MODEL;}

    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {return new NestTileEntity(pos, state);}

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult p_225533_6_) {
        if (world.isClientSide) return InteractionResult.CONSUME;

        NestTileEntity te = (NestTileEntity) world.getBlockEntity(pos);
        if (te == null) return InteractionResult.CONSUME;

        ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.isEmpty()) return InteractionResult.CONSUME;      // stops arm swing animation
        Item item = itemStack.getItem();

        // try and insert item into the Nest, it only accepts seeds, so if returns .isEmpty()
        // then it must have been a seed, and must have been inserted
        if (ItemHandlerHelper.insertItem(te.storedItems, new ItemStack(item, 1), false).isEmpty()) {
//                CrimsonChickens.LOGGER.info("ADDED SEEDS");

            for (int a = 0; a < 4; a++) {
                double d0 = this.RANDOM.nextGaussian() * 0.2D;
                double d1 = this.RANDOM.nextGaussian() * 0.2D;
                double d2 = this.RANDOM.nextGaussian() * 0.2D;

                ((ServerLevel) world).sendParticles(ParticleTypes.HEART, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 1, d0, d1, d2, 0);
            }

            if (! player.getAbilities().instabuild) itemStack.shrink(1);
            return InteractionResult.SUCCESS;
        }

        // inc. modded name tags?
        // blank name tags will remove name
        if (item instanceof NameTagItem) {
            te.entitySetCustomName(itemStack.getTagElement("display"));
            te.sendUpdates();

            if (! player.getAbilities().instabuild) itemStack.shrink(1);
            return InteractionResult.SUCCESS;
        }

        return super.use(state, world, pos, player, hand, p_225533_6_);
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean p_220069_6_) {
        if (pos.below().equals(fromPos))
            ((NestTileEntity) world.getBlockEntity(pos)).updateCache();
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean p_196243_5_) {
        if (! state.is(newState.getBlock())) {
            NestTileEntity te = (NestTileEntity) world.getBlockEntity(pos);

            if (te != null) {
                te.storedItems.contents().forEach(item -> {
                    Containers.dropItemStack(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, item);
                });

                if (te.entityCaptured != null) {
                    Entity entity = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(te.entityCaptured.getString("id"))).create(world);

                    if (entity != null) {
                        entity.load(te.entityCaptured);
                        entity.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                        world.addFreshEntity(entity);

                        world.playSound(null, pos, SoundEvents.CHICKEN_EGG, SoundSource.PLAYERS, 1f, 1f);
                    }
                }
            }

            super.onRemove(state, world, pos, newState, p_196243_5_);
        }
    }

    @Override
    public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, Player playerEntity, Level world, BlockState blockState, IProbeHitData iProbeHitData) {
        NestTileEntity te = (NestTileEntity) world.getBlockEntity(iProbeHitData.getPos());
        if (te == null) return;

        if (te.entityCaptured != null) {
            iProbeInfo.text(new TextComponent(te.entityDescription));

            if (te.entityCustomName != null)
                iProbeInfo.text(te.entityCustomName);

            if (te.entityCaptured.getBoolean("analyzed")) {
                iProbeInfo.text(new TranslatableComponent("tip.crimsonchickens.growth", te.chickenGrowth));
                iProbeInfo.text(new TranslatableComponent("tip.crimsonchickens.gain", te.chickenGain));
                iProbeInfo.text(new TranslatableComponent("tip.crimsonchickens.strength", te.chickenStrength));
            }

            int secs;
            if (te.chickenAge < 0) {
                secs = - te.chickenAge / 20;
                iProbeInfo.text(new TextComponent("Growing Time: " + String.format("%02d:%02d", secs / 60, secs % 60)));

            }
            else {
                if (te.storedItems.getStackInSlot(0).isEmpty()) {
                    iProbeInfo.text(CompoundText.create().style(TextStyleClass.WARNING).text("Requires seeds"));

                }
                else {
                    if (te.entityCaptured.getInt("EggLayTime") != 0) {
                        secs = te.eggLayTime / 20;
                        iProbeInfo.text(new TextComponent("Next Drop: " + String.format("%02d:%02d", secs / 60, secs % 60)));
                    }
                }
            }
        }
    }
}

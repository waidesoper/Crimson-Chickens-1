package crimsonfluff.crimsonchickens.blocks;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.NameTagItem;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.*;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.stream.Stream;

public class Nest extends Block implements BlockEntityProvider  {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;

    public Nest() {
        super(Settings.of(Material.SOLID_ORGANIC, MapColor.YELLOW)
            .strength(0.5F)
            .sounds(BlockSoundGroup.GRASS));

        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override       // TODO: client config for simple bounding box - maybe save some FPS ?  or make static
    public VoxelShape getOutlineShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context) {
        return Stream.of(Block.createCuboidShape(12, 2.1, 5, 13, 3.1, 7),
            Block.createCuboidShape(5, 0.1, 5, 11, 1.1, 11),
            Block.createCuboidShape(12, 1.1, 7, 13, 2.1, 9),
            Block.createCuboidShape(3, 1.1, 7, 4, 2.1, 9),
            Block.createCuboidShape(4, 0.1, 7, 5, 1.1, 9),
            Block.createCuboidShape(11, 0.1, 7, 12, 1.1, 9),
            Block.createCuboidShape(7, 1.1, 3, 9, 2.1, 4),
            Block.createCuboidShape(7, 0.1, 4, 9, 1.1, 5),
            Block.createCuboidShape(7, 0.1, 11, 9, 1.1, 12),
            Block.createCuboidShape(7, 1.1, 12, 9, 2.1, 13),
            Block.createCuboidShape(4, 1.1, 9, 5, 2.1, 11),
            Block.createCuboidShape(11, 1.1, 9, 12, 2.1, 11),
            Block.createCuboidShape(11, 2.1, 11, 12, 3.1, 12),
            Block.createCuboidShape(11, 2.1, 4, 12, 3.1, 5),
            Block.createCuboidShape(4, 2.1, 4, 5, 3.1, 5),
            Block.createCuboidShape(4, 2.1, 11, 5, 3.1, 12),
            Block.createCuboidShape(11, 1.1, 5, 12, 2.1, 7),
            Block.createCuboidShape(4, 1.1, 5, 5, 2.1, 7),
            Block.createCuboidShape(13, 2.1, 7, 14, 3.1, 9),
            Block.createCuboidShape(2, 2.1, 7, 3, 3.1, 9),
            Block.createCuboidShape(7, 2.1, 2, 9, 3.1, 3),
            Block.createCuboidShape(7, 2.1, 13, 9, 3.1, 14),
            Block.createCuboidShape(12, 2.1, 9, 13, 3.1, 11),
            Block.createCuboidShape(9, 2.1, 12, 11, 3.1, 13),
            Block.createCuboidShape(5, 2.1, 12, 7, 3.1, 13),
            Block.createCuboidShape(5, 1.1, 11, 7, 2.1, 12),
            Block.createCuboidShape(5, 1.1, 4, 7, 2.1, 5),
            Block.createCuboidShape(9, 1.1, 4, 11, 2.1, 5),
            Block.createCuboidShape(9, 1.1, 11, 11, 2.1, 12),
            Block.createCuboidShape(3, 2.1, 9, 4, 3.1, 11),
            Block.createCuboidShape(3, 2.1, 5, 4, 3.1, 7),
            Block.createCuboidShape(5, 2.1, 3, 7, 3.1, 4),
            Block.createCuboidShape(9, 2.1, 3, 11, 3.1, 4))
        .reduce((v1, v2) -> VoxelShapes.combine(v1, v2, BooleanBiFunction.OR)).get();
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING, OPEN});
    }

    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }


    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockView world) { return new NestTileEntity(); }

//    @Override
//    public boolean hasTileEntity(BlockState state) {return true;}

//    @Override
//    public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, PlayerEntity playerEntity, World world, BlockState blockState, IProbeHitData iProbeHitData) {
//        NestTileEntity te = (NestTileEntity) world.getBlockEntity(iProbeHitData.getPos());
//        if (te == null) return;
//
//        if (te.entityCaptured != null) {
//            iProbeInfo.text(new StringTextComponent(te.entityDescription));
//
//            if (te.entityCustomName != null)
//                iProbeInfo.text(te.entityCustomName);
//
//            if (te.entityCaptured.getBoolean("analyzed")) {
//                iProbeInfo.text(new TranslationTextComponent("tip.crimsonchickens.growth", te.chickenGrowth));
//                iProbeInfo.text(new TranslationTextComponent("tip.crimsonchickens.gain", te.chickenGain));
//                iProbeInfo.text(new TranslationTextComponent("tip.crimsonchickens.strength", te.chickenStrength));
//            }
//
//            if (te.chickenAge < 0) {
//                iProbeInfo.text(new TranslationTextComponent("tip.crimsonchickens.growing", CrimsonChickens.formatTime(- te.chickenAge)));
//
//            }
//            else {
//                if (te.storedItems.getStackInSlot(0).isEmpty()) {
//                    iProbeInfo.text(CompoundText.create().style(TextStyleClass.WARNING).text(new TranslationTextComponent("tip.crimsonchickens.seeds")));
//
//                }
//                else {
//                    if (te.entityCaptured.getInt("EggLayTime") != 0) {
//                        iProbeInfo.text(new TranslationTextComponent("tip.crimsonchickens.egg", CrimsonChickens.formatTime(te.eggLayTime)));
//                    }
//                }
//            }
//        }
//    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.CONSUME;

        NestTileEntity te = (NestTileEntity) world.getBlockEntity(pos);
        if (te == null) return ActionResult.CONSUME;

        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.isEmpty()) return ActionResult.CONSUME;
        Item item = itemStack.getItem();

        // Note: Can not shift click seeds into the nest. This method is not called
        // try and insert item into the Nest, it only accepts seeds, so if returns .isEmpty()
        // then it must have been a seed, and must have been inserted
        if (ItemHandlerHelper.insertItem(te.STORED_ITEMS, new ItemStack(item, 1), false).isEmpty()) {
            Random r = new Random();
            for (int a = 0; a < 4; a++) {
                double d0 = r.nextGaussian() * 0.2D;
                double d1 = r.nextGaussian() * 0.2D;
                double d2 = r.nextGaussian() * 0.2D;

                if (te.entityCaptured != null)
                    ((ServerWorld) world).spawnParticles(ParticleTypes.HEART, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 1, d0, d1, d2, 0);
            }

            if (! player.isCreative()) itemStack.decrement(1);
            te.sendUpdates();

            return ActionResult.SUCCESS;
        }

        // inc. modded name tags?
        // blank name tags will remove name
        if (item instanceof NameTagItem) {
            te.entitySetCustomName(itemStack.getSubTag("display"));
            te.sendUpdates();

            if (! player.isCreative()) itemStack.decrement(1);
            return ActionResult.SUCCESS;
        }

        return super.onUse(state, world, pos, player, hand, hit);
    }

//    @Override
//    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean p_220069_6_) {
//        if (pos.below().equals(fromPos))
//            ((NestTileEntity) world.getBlockEntity(pos)).updateCache();
//    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean p_196243_5_) {
        if (! state.isOf(newState.getBlock())) {
            NestTileEntity te = (NestTileEntity) world.getBlockEntity(pos);

            if (te != null) {
                if (! te.STORED_ITEMS.isEmpty()) {
                    te.STORED_ITEMS.forEach(item -> {
                        Block.dropStack(world, pos, item);
                    });
                }

                if (te.entityCaptured != null) {
                    Entity entity = Registry.ENTITY_TYPE.get(new Identifier(te.entityCaptured.getString("id"))).create(world);

                    if (entity != null) {
                        entity.readNbt(te.entityCaptured);
                        entity.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                        world.spawnEntity(entity);

                        world.playSound(null, pos, SoundEvents.ENTITY_CHICKEN_EGG, SoundCategory.PLAYERS, 1f, 1f);
                    }
                }
            }

            super.onStateReplaced(state, world, pos, newState, p_196243_5_);
        }
    }
}

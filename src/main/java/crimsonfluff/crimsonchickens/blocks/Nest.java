package crimsonfluff.crimsonchickens.blocks;

import crimsonfluff.crimsonchickens.compat.ITOPInfoProvider;
import mcjty.theoneprobe.api.*;
import mcp.mobius.waila.api.IDataAccessor;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.NameTagItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Stream;

public class Nest extends ContainerBlock implements ITOPInfoProvider {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public Nest() {
        super(AbstractBlock.Properties.of(Material.GRASS, MaterialColor.COLOR_YELLOW)
            .strength(0.5f)
            .sound(SoundType.GRASS)
            .harvestTool(ToolType.HOE)
            .noOcclusion());

        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
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
            .reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.MODEL;
    }

    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(IBlockReader iBlockReader) { return new NestTileEntity(); }

    @Override
    public boolean hasTileEntity(BlockState state) { return true; }

    @Override
    public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, PlayerEntity playerEntity, World world, BlockState blockState, IProbeHitData iProbeHitData) {
        NestTileEntity te = (NestTileEntity) world.getBlockEntity(iProbeHitData.getPos());
        if (te == null) return;

        if (te.entityCaptured != null) {
            iProbeInfo.text(new StringTextComponent(te.entityDescription));

            if (te.entityCaptured.contains("CustomName", 8))
                iProbeInfo.text(ITextComponent.Serializer.fromJson(te.entityCaptured.getString("CustomName")));

            if (te.entityCaptured.getBoolean("analyzed")) {
                iProbeInfo.text(new TranslationTextComponent("tip.crimsonchickens.growth", te.chickenGrowth));
                iProbeInfo.text(new TranslationTextComponent("tip.crimsonchickens.gain", te.chickenGain));
                iProbeInfo.text(new TranslationTextComponent("tip.crimsonchickens.strength", te.chickenStrength));
            }

            int secs;
            if (te.chickenAge < 0) {
                secs = -te.chickenAge / 20;
                iProbeInfo.text(new StringTextComponent("Growing Time: " + String.format("%02d:%02d", secs / 60, secs % 60)));

            } else {
                if (te.storedItems.getStackInSlot(0).isEmpty()) {
                    iProbeInfo.text(CompoundText.create().style(TextStyleClass.WARNING).text("Requires seeds"));

                } else {
                    if (te.entityCaptured.getInt("EggLayTime") != 0) {
                        secs = te.eggLayTime / 20;
                        iProbeInfo.text(new StringTextComponent("Next Drop: " + String.format("%02d:%02d", secs / 60, secs % 60)));
                    }
                }
            }
        }
//        else {
//            iProbeInfo.text(new StringTextComponent("Empty"));
//        }
        //iProbeInfo.text(new StringTextComponent(te.getTileData().toString()));
    }

    public void addWailaEntityInfo(List<ITextComponent> tooltip, IDataAccessor accessor) {
        NestTileEntity te = (NestTileEntity) accessor.getWorld().getBlockEntity(accessor.getPosition()); //.getBlockEntity(iProbeHitData.getPos());
        if (te == null) return;

        if (te.getTileData().contains("entityCaptured")) {
            tooltip.add(new StringTextComponent(te.getTileData().getString("entityDescription")));

            if (te.getTileData().contains("CustomName", 8))
                tooltip.add(ITextComponent.Serializer.fromJson(te.getTileData().getString("CustomName")));

            CompoundNBT compound = te.getTileData().getCompound("entityCaptured");
            if (compound.getBoolean("analyzed")) {
                tooltip.add(new TranslationTextComponent("tip.crimsonchickens.growth", compound.getInt("growth")));
                tooltip.add(new TranslationTextComponent("tip.crimsonchickens.gain", compound.getInt("gain")));
                tooltip.add(new TranslationTextComponent("tip.crimsonchickens.strength", compound.getInt("strength")));
            }

            int secs;
            if (te.chickenAge < 0) {
                secs = -te.chickenAge / 20;
                tooltip.add(new StringTextComponent("Growing Time: " + String.format("%02d:%02d", secs / 60, secs % 60)));

            } else {
                if (te.storedItems.getStackInSlot(0).isEmpty()) {
                    tooltip.add(new StringTextComponent("Requires seeds"));

                } else {
                    secs = te.eggLayTime / 20;
                    tooltip.add(new StringTextComponent("Next Drop: " + String.format("%02d:%02d", secs / 60, secs % 60)));
                }
            }

        } else {
            tooltip.add(new StringTextComponent("Empty"));
        }
        //iProbeInfo.text(new StringTextComponent(te.getTileData().toString()));
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult p_225533_6_) {
        if (world.isClientSide) return ActionResultType.SUCCESS;

        NestTileEntity te = (NestTileEntity) world.getBlockEntity(pos);
        if (te == null) return ActionResultType.FAIL;

        Item item = player.getItemInHand(hand).getItem();

        // try and insert item into the Nest, it only accepts seeds, so if returns .isEmpty()
        // then it must have been a seed, and must have been inserted
        if (ItemHandlerHelper.insertItem(te.storedItems, new ItemStack(item, 1), false).isEmpty()) {
//                CrimsonChickens.LOGGER.info("ADDED SEEDS");

            for (int a = 0; a < 4; a++) {
                double d0 = this.RANDOM.nextGaussian() * 0.2D;
                double d1 = this.RANDOM.nextGaussian() * 0.2D;
                double d2 = this.RANDOM.nextGaussian() * 0.2D;

                ((ServerWorld) world).sendParticles(ParticleTypes.HEART, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 1, d0, d1, d2,0);
            }

            if (! player.abilities.instabuild) player.getItemInHand(hand).shrink(1);
            return ActionResultType.SUCCESS;
        }

        // inc. modded name tags?
        // blank name tags will remove name
        if (item instanceof NameTagItem) {
            te.entitySetCustomName(player.getItemInHand(hand).getTagElement("display"));
            te.sendUpdates();

            if (! player.abilities.instabuild) player.getItemInHand(hand).shrink(1);
            return ActionResultType.SUCCESS;
        }

        return super.use(state, world, pos, player, hand, p_225533_6_);
    }

    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean p_220069_6_) {
        if (pos.below().equals(fromPos))
            ((NestTileEntity) world.getBlockEntity(pos)).updateCache();
    }

    @Override
    public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean p_196243_5_) {
        if (! state.is(newState.getBlock())) {
            NestTileEntity te = (NestTileEntity) world.getBlockEntity(pos);

            if (te != null) {
                te.storedItems.contents().forEach(item -> {
                    InventoryHelper.dropItemStack(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, item);
                });

                if (te.entityCaptured != null) {
                    Entity entity = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(te.entityCaptured.getString("id"))).create(world);

                    if (entity != null) {
                        entity.load(te.entityCaptured);
                        entity.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                        world.addFreshEntity(entity);

                        world.playSound(null, pos, SoundEvents.CHICKEN_EGG, SoundCategory.PLAYERS, 1f, 1f);
                    }
                }
            }

            super.onRemove(state, world, pos, newState, p_196243_5_);
        }
    }
}

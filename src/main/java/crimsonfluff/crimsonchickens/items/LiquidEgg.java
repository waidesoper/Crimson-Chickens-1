package crimsonfluff.crimsonchickens.items;

import crimsonfluff.crimsonchickens.CrimsonChickens;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.EggItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LiquidEgg extends EggItem {
    private final Fluid fluidType;      // Lava, Water etc

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        FluidHandlerItemStack fluidHandlerItemStack = new FluidHandlerItemStack(stack, 1000) {
            @Nonnull
            @Override
            public ItemStack getContainer() { return ItemStack.EMPTY; }

            @Nonnull
            @Override
            public FluidStack getFluid() { return new FluidStack(fluidType,1000); }

            @Override
            public int getTanks() { return 1; }

            @Override
            public boolean isFluidValid(int tank, @Nonnull FluidStack stack) { return false; }

            @Override
            public boolean canFillFluidType(FluidStack fluid) { return false; }

            @Nonnull
            @Override
            public FluidStack drain(int maxDrain, FluidAction action) {
                if (maxDrain < 1000) return FluidStack.EMPTY;
                return super.drain(maxDrain, action);
            }
        };

        fluidHandlerItemStack.fill(new FluidStack(fluidType, 1000), IFluidHandler.FluidAction.EXECUTE);
        return fluidHandlerItemStack;
    }

    public LiquidEgg(Fluid p_i49025_1_) {
        super(new Properties().tab(CrimsonChickens.TAB).stacksTo(16));
        this.fluidType = p_i49025_1_;
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemStack = playerIn.getItemInHand(handIn);
        BlockRayTraceResult rayTraceResult = getPlayerPOVHitResult(worldIn, playerIn, RayTraceContext.FluidMode.NONE);

        ActionResult<ItemStack> ret = net.minecraftforge.event.ForgeEventFactory.onBucketUse(playerIn, worldIn, itemStack, rayTraceResult);
        if (ret != null) return ret;

        if (rayTraceResult.getType() == RayTraceResult.Type.MISS) {
            return ActionResult.pass(itemStack);

        } else if (rayTraceResult.getType() != RayTraceResult.Type.BLOCK) {
            return ActionResult.pass(itemStack);

        } else {
            BlockPos blockPos = rayTraceResult.getBlockPos();
            Direction direction = rayTraceResult.getDirection();
            BlockPos blockPos1 = blockPos.relative(direction);

            BlockState blockState = worldIn.getBlockState(blockPos);
            BlockPos blockPos2 = canBlockContainFluid(worldIn, blockPos, blockState) ? blockPos : blockPos1;

            if (worldIn.dimensionType().ultraWarm() && this.fluidType.is(FluidTags.WATER)) {
                worldIn.playSound(playerIn, blockPos2, SoundEvents.FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (worldIn.random.nextFloat() - worldIn.random.nextFloat()) * 0.8F);

                for (int a = 0; a < 8; ++a) {
                    worldIn.addParticle(ParticleTypes.LARGE_SMOKE,
            (double) blockPos2.getX() + Math.random(), (double) blockPos2.getY() + Math.random(), (double) blockPos2.getZ() + Math.random(), 0.0D, 0.0D, 0.0D);
                }

                if (! playerIn.abilities.instabuild) itemStack.shrink(1);

                playerIn.awardStat(Stats.ITEM_USED.get(this));
                return ActionResult.sidedSuccess(itemStack, worldIn.isClientSide());

            } else if (worldIn.setBlockAndUpdate(blockPos2, fluidType.defaultFluidState().createLegacyBlock())) {
                if (playerIn instanceof ServerPlayerEntity)
                    CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity) playerIn, blockPos2, itemStack);

                playEmptySound(playerIn, worldIn, blockPos2);
                if (! playerIn.abilities.instabuild) itemStack.shrink(1);

                playerIn.awardStat(Stats.ITEM_USED.get(this));
                return ActionResult.sidedSuccess(itemStack, worldIn.isClientSide());
            }
        }

        return ActionResult.fail(itemStack);
    }

    private boolean canBlockContainFluid(World worldIn, BlockPos posIn, BlockState blockstate) {
        return blockstate.getBlock() instanceof ILiquidContainer && ((ILiquidContainer) blockstate.getBlock()).canPlaceLiquid(worldIn, posIn, blockstate, this.fluidType);
    }

    protected void playEmptySound(@Nullable PlayerEntity p_203791_1_, IWorld p_203791_2_, BlockPos p_203791_3_) {
        SoundEvent emptySound = this.fluidType.getAttributes().getEmptySound();

        if(emptySound == null) emptySound = this.fluidType.is(FluidTags.LAVA) ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY;
        p_203791_2_.playSound(p_203791_1_, p_203791_3_, emptySound, SoundCategory.BLOCKS, 1.0F, 1.0F);
    }
}

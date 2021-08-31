package crimsonfluff.crimsonchickens.items;

import crimsonfluff.crimsonchickens.CrimsonChickens;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EggItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;

public class LiquidEgg extends EggItem {
    private final Fluid content;

    public LiquidEgg(Fluid p_i49025_1_) {
        super(new Properties().tab(CrimsonChickens.TAB).stacksTo(16));
        this.content = p_i49025_1_;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        BlockHitResult BlockHitResult = getPlayerPOVHitResult(worldIn, playerIn, ClipContext.Fluid.NONE);

        InteractionResultHolder<ItemStack> ret = net.minecraftforge.event.ForgeEventFactory.onBucketUse(playerIn, worldIn, itemstack, BlockHitResult);
        if (ret != null) return ret;

        if (BlockHitResult.getType() == HitResult.Type.MISS) {
            return InteractionResultHolder.pass(itemstack);

        } else if (BlockHitResult.getType() != HitResult.Type.BLOCK) {
            return InteractionResultHolder.pass(itemstack);

        } else {
            BlockPos blockpos = BlockHitResult.getBlockPos();
            Direction direction = BlockHitResult.getDirection();
            BlockPos blockpos1 = blockpos.relative(direction);

            BlockState blockstate = worldIn.getBlockState(blockpos);
            BlockPos blockpos2 = canBlockContainFluid(worldIn, blockpos, blockstate) ? blockpos : blockpos1;

            if (worldIn.dimensionType().ultraWarm() && this.content.is(FluidTags.WATER)) {
                int i = blockpos2.getX();
                int j = blockpos2.getY();
                int k = blockpos2.getZ();
                worldIn.playSound(null, blockpos2, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (worldIn.random.nextFloat() - worldIn.random.nextFloat()) * 0.8F);

                for (int l = 0; l < 8; ++ l) {
                    worldIn.addParticle(ParticleTypes.LARGE_SMOKE, (double) i + Math.random(), (double) j + Math.random(), (double) k + Math.random(), 0.0D, 0.0D, 0.0D);
                }

                return InteractionResultHolder.sidedSuccess(itemstack, worldIn.isClientSide());
            }

            if (worldIn.setBlockAndUpdate(blockpos2, content.defaultFluidState().createLegacyBlock())) {
                if (playerIn instanceof ServerPlayer)
                    CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer) playerIn, blockpos2, itemstack);

                this.playEmptySound(playerIn, worldIn, blockpos2);

                playerIn.awardStat(Stats.ITEM_USED.get(this));
                return InteractionResultHolder.sidedSuccess(itemstack, worldIn.isClientSide());
            }
        }

        return InteractionResultHolder.fail(itemstack);
    }

    private boolean canBlockContainFluid(Level worldIn, BlockPos posIn, BlockState blockstate) {
        return blockstate.getBlock() instanceof LiquidBlockContainer && ((LiquidBlockContainer)blockstate.getBlock()).canPlaceLiquid(worldIn, posIn, blockstate, this.content);
    }

    protected void playEmptySound(@Nullable Player p_40696_, LevelAccessor p_40697_, BlockPos p_40698_) {
        SoundEvent soundevent = this.content.getAttributes().getEmptySound();
        if(soundevent == null) soundevent = this.content.is(FluidTags.LAVA) ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY;

        p_40697_.playSound(p_40696_, p_40698_, soundevent, SoundSource.BLOCKS, 1.0F, 1.0F);
        p_40697_.gameEvent(p_40696_, GameEvent.FLUID_PLACE, p_40698_);
    }
}

package crimsonfluff.crimsonchickens.items;

import crimsonfluff.crimsonchickens.CrimsonChickens;
import crimsonfluff.crimsonchickens.init.initItems;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class xpItem extends Item {
    public xpItem() { super(new Properties().tab(CrimsonChickens.TAB).rarity(Rarity.UNCOMMON)); }

    @Override
    public boolean isFoil(ItemStack itemStack) { return true; }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        if (worldIn.isClientSide) return InteractionResultHolder.success(playerIn.getItemInHand(handIn));
        playerIn.level.playSound(null, playerIn.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1f, 1f);

        int isAmount = 0;
        ItemStack stack;

        Vec3 vec = playerIn.position().add(playerIn.getLookAngle().multiply(2, 0, 2));
        ((ServerLevel) worldIn).sendParticles(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(initItems.XP_ITEM.get())),
            vec.x, playerIn.getEyeY(), vec.z, 20, 0.5f, 0.5f, 0.5f,0);

        if (playerIn.isShiftKeyDown()) {
            for (int a = 0; a < playerIn.getInventory().items.size(); a++) {
                stack = playerIn.getInventory().getItem(a);

                if (stack.getItem() == initItems.XP_ITEM.get()) {
                    //isAmount += stack.getCount();
                    isAmount += 3 + worldIn.random.nextInt(5) + worldIn.random.nextInt(5);  // between 3 and 11 xp, same as Bottle O'Enchanting
                    if (! playerIn.isCreative()) stack.setCount(0);
                }
            }

        } else {
            stack = playerIn.getItemInHand(handIn);
            isAmount += 3 + worldIn.random.nextInt(5) + worldIn.random.nextInt(5);

            if (! playerIn.isCreative()) stack.setCount(0);
        }

        playerIn.giveExperiencePoints(isAmount);

        return InteractionResultHolder.success(playerIn.getItemInHand(handIn));
    }
}

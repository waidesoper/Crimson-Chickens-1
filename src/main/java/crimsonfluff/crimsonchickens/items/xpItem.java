package crimsonfluff.crimsonchickens.items;

import crimsonfluff.crimsonchickens.CrimsonChickens;
import crimsonfluff.crimsonchickens.init.initItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class xpItem extends Item {
    public xpItem() { super(new Properties().tab(CrimsonChickens.TAB).rarity(Rarity.UNCOMMON)); }

    @Override
    public boolean isFoil(ItemStack itemStack) { return true; }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if (worldIn.isClientSide) return ActionResult.success(playerIn.getItemInHand(handIn));
        playerIn.level.playSound(null, playerIn.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1f, 1f);

        int isAmount = 0;
        ItemStack stack;

        Vector3d vec = playerIn.position().add(playerIn.getLookAngle().multiply(2, 0, 2));
        ((ServerWorld) worldIn).sendParticles(new ItemParticleData(ParticleTypes.ITEM, new ItemStack(initItems.XP_ITEM.get())),
            vec.x, playerIn.getEyeY(), vec.z, 20, 0.5f, 0.5f, 0.5f,0);

        if (playerIn.isShiftKeyDown()) {
            for (int a = 0; a < playerIn.inventory.items.size(); a++) {
                stack = playerIn.inventory.getItem(a);

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

        return ActionResult.success(playerIn.getItemInHand(handIn));
    }
}

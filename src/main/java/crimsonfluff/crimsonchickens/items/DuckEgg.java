package crimsonfluff.crimsonchickens.items;

import crimsonfluff.crimsonchickens.CrimsonChickens;
import crimsonfluff.crimsonchickens.entity.DuckEggProjectileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class DuckEgg extends Item {
    public DuckEgg() { super(new Properties().tab(CrimsonChickens.TAB).stacksTo(16)); }

    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        worldIn.playSound(null, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SoundEvents.EGG_THROW, SoundCategory.PLAYERS, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));

        if (! worldIn.isClientSide) {
            DuckEggProjectileEntity eggentity = new DuckEggProjectileEntity(worldIn, playerIn);
            eggentity.setItem(itemstack);
            eggentity.shootFromRotation(playerIn, playerIn.xRot, playerIn.yRot, 0.0F, 1.5F, 1.0F);
            worldIn.addFreshEntity(eggentity);
        }

        playerIn.awardStat(Stats.ITEM_USED.get(this));
        if (! playerIn.abilities.instabuild) itemstack.shrink(1);

        return ActionResult.sidedSuccess(itemstack, worldIn.isClientSide());
    }
}

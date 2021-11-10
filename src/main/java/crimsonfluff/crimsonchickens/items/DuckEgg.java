package crimsonfluff.crimsonchickens.items;

import crimsonfluff.crimsonchickens.CrimsonChickens;
import crimsonfluff.crimsonchickens.entity.DuckEggProjectileEntity;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class DuckEgg extends Item {
    public DuckEgg() {super(new FabricItemSettings().group(CrimsonChickens.CRIMSON_CHICKENS_TAB).maxCount(16));}

    public TypedActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getStackInHand(handIn);
        worldIn.playSound(null, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SoundEvents.ENTITY_EGG_THROW, SoundCategory.PLAYERS, 0.5F, 0.4F / (Item.RANDOM.nextFloat() * 0.4F + 0.8F));

        if (! worldIn.isClient) {
            DuckEggProjectileEntity eggentity = new DuckEggProjectileEntity(worldIn, playerIn);
            eggentity.setItem(itemstack);
            eggentity.setProperties(playerIn, playerIn.pitch, playerIn.yaw, 0.0F, 1.5F, 1.0F);
            worldIn.spawnEntity(eggentity);
        }

        playerIn.incrementStat(Stats.USED.getOrCreateStat(this));
        if (! playerIn.isCreative()) itemstack.decrement(1);

        return TypedActionResult.success(itemstack, worldIn.isClient);
    }
}

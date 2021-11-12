package crimsonfluff.crimsonchickens.items;

import crimsonfluff.crimsonchickens.CrimsonChickens;
import crimsonfluff.crimsonchickens.init.initItems;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class xpItem extends Item {
    public xpItem() {super(new FabricItemSettings().group(CrimsonChickens.CREATIVE_TAB).rarity(Rarity.UNCOMMON));}

    @Override
    public boolean hasGlint(ItemStack itemStack) {return true;}

    @Override
    public TypedActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if (worldIn.isClient) return TypedActionResult.success(playerIn.getStackInHand(handIn));
        playerIn.world.playSound(null, playerIn.getBlockPos(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1f, 1f);

        int isAmount = 0;
        ItemStack stack;

        Vec3d vec = playerIn.getPos().add(playerIn.getRotationVector().multiply(2, 0, 2));
        ((ServerWorld) worldIn).spawnParticles(new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(initItems.XP_ITEM)),
            vec.x, playerIn.getEyeY(), vec.z, 20, 0.5f, 0.5f, 0.5f, 0);

        if (playerIn.isSneaking()) {
            for (int a = 0; a < playerIn.inventory.size(); a++) {
                stack = playerIn.inventory.getStack(a);

                if (stack.getItem() == initItems.XP_ITEM) {
                    //isAmount += stack.getCount();
                    isAmount += 3 + worldIn.random.nextInt(5) + worldIn.random.nextInt(5);  // between 3 and 11 xp, same as Bottle O'Enchanting
                    if (! playerIn.isCreative()) stack.setCount(0);
                }
            }
        }
        else {
            stack = playerIn.getStackInHand(handIn);
            isAmount += 3 + worldIn.random.nextInt(5) + worldIn.random.nextInt(5);

            if (! playerIn.isCreative()) stack.setCount(0);
        }

        playerIn.addExperience(isAmount);

        return TypedActionResult.success(playerIn.getStackInHand(handIn));
    }
}

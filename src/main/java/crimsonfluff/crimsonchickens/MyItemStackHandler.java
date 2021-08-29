package crimsonfluff.crimsonchickens;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.List;

/*
 Custom ItemInsert method
 This is because we dont want mods to insert anything other than Seeds into the Nest
 therefore isValidItem always returns false
 so we can't use ItemStackHandler to insert the 'drop' items into the Nest

 items that can't fit into any slots are simply voided, actually they are simply un processed
 simulate is unused in this mod instance
*/

public class MyItemStackHandler extends ItemStackHandler {
    public MyItemStackHandler(int size) {
        stacks = NonNullList.withSize(size, ItemStack.EMPTY);
    }

    public void insertItemAnySlot(@Nonnull ItemStack stack) {
        if (stack.isEmpty()) return;

        if (! stack.isStackable()) {
            for (int slot = 1; slot < stacks.size(); slot++) {
                ItemStack existing = this.stacks.get(slot);
                if (existing.isEmpty()) {
                    stacks.set(slot, stack);
                    return;
                }
            }

        } else {
//            CrimsonChickens.LOGGER.info("HERE_1");
            // fill up existing stacks first
            for (int slot = 1; slot < stacks.size(); slot++) {
                ItemStack existing = this.stacks.get(slot);

                if (existing.getItem() == stack.getItem()) {
//                    CrimsonChickens.LOGGER.info("HERE_2");
                    int freeSpace = existing.getMaxStackSize() - existing.getCount();

                    if (freeSpace > 0) {
                        int toInsert = Integer.min(freeSpace, stack.getCount());
                        existing.grow(toInsert);
                        stack.shrink(toInsert);

                        if (stack.getCount() == 0) return;
                    }
                }
            }

            // put remainder in any empty slots
            for (int slot = 1; slot < stacks.size(); slot++) {
                ItemStack existing = this.stacks.get(slot);

                if (existing.isEmpty()) {
//                    CrimsonChickens.LOGGER.info("HERE_3");
                    stacks.set(slot, stack);
                    return;
                }
            }
        }
    }

    public List<ItemStack> contents() { return stacks; }
}

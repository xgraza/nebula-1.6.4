package wtf.nebula.util.world.player.inventory;

import net.minecraft.src.ItemStack;
import wtf.nebula.util.Globals;

import java.util.function.Predicate;

public class InventoryUtil implements Globals {
    public static int findSlot(InventoryRegion region, Predicate<ItemStack> filter) {
        for (int i = region.start; i < region.end; ++i) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            if (stack == null) {
                continue;
            }

            if (filter.test(stack)) {
                return i;
            }
        }

        return -1;
    }
}

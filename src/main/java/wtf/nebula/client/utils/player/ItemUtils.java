package wtf.nebula.client.utils.player;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import wtf.nebula.client.utils.client.Wrapper;

import java.util.Map;

public class ItemUtils implements Wrapper {

    /**
     * Checks if this item is a 32k in terms of enchantments
     * @param stack the stack in question
     * @return if the stack has a single 32k enchantment
     */
    public static boolean is32k(ItemStack stack) {
        if (stack == null || !stack.hasEffect()) {
            return false;
        }

        Map<Integer, Integer> enchantmentMap = EnchantmentHelper.getEnchantments(stack);
        if (enchantmentMap != null && !enchantmentMap.isEmpty()) {
            for (int level : enchantmentMap.values()) {
                if (level >= Short.MAX_VALUE) {
                    return true;
                }
            }
        }

        return false;
    }
}

package lol.nebula.util.player;

import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;

import java.util.Map;

/**
 * @author aesthetical
 * @since 04/30/23
 */
public class InventoryUtils {
    /**
     * The minecraft game instance
     */
    private static final Minecraft mc = Minecraft.getMinecraft();

    /**
     * Checks if an item is infinite
     * @param stack the stack
     * @return if the stack size is less than 0
     */
    public static boolean isInfinite(ItemStack stack) {
        return stack.stackSize < 0;
    }

    /**
     * Checks if an item has 32k enchantments (max value short)
     * @param stack the stack
     * @return if the stack is a 32k
     */
    public static boolean is32k(ItemStack stack) {
        if (!stack.hasEffect()) return false;

        Map<Integer, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
        if (enchantments.isEmpty()) return false;

        for (int level : enchantments.values()) {
            if (level >= Short.MAX_VALUE) return true;
        }

        return false;
    }
}

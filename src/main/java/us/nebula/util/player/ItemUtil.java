package us.nebula.util.player;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;

/**
 * @author xgraza
 * @since 03/26/25
 */
public final class ItemUtil
{
    public static int getEnchantLevel(final Enchantment enchantment, final ItemStack itemStack)
    {
        int level = EnchantmentHelper.getEnchantmentLevel(enchantment.effectId, itemStack);
        if (level > enchantment.getMaxLevel())
        {
            level = enchantment.getMaxLevel();
        }
        return level;
    }

    public static boolean isIllegal(final ItemStack itemStack)
    {
        return isInfinite(itemStack) || is32k(itemStack);
    }

    public static boolean isInfinite(final ItemStack itemStack)
    {
        return itemStack != null && (itemStack.stackSize < 0 || itemStack.stackSize > itemStack.getMaxStackSize());
    }

    public static boolean is32k(final ItemStack itemStack)
    {
        return itemStack != null
                && EnchantmentHelper.getEnchantments(itemStack)
                .keySet()
                .stream()
                .anyMatch((x) -> x >= Short.MAX_VALUE);
    }
}

package us.nebula.util.player;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;

/**
 * @author xgraza
 * @since 03/26/25
 */
public final class ItemUtil
{
    public static float getToolScore(final ItemStack itemStack, final Block attackedBlock)
    {
        float damage = itemStack.getStrVsBlock(attackedBlock);
        if (damage <= 1.0f)
        {
            return 0.0f;
        }
        damage += getEnchantLevel(Enchantment.unbreaking, itemStack) * 1.5f;
        damage += getEnchantLevel(Enchantment.efficiency, itemStack) * 1.2f;
        damage += getEnchantLevel(Enchantment.looting, itemStack) * 1.5f;
        damage += getEnchantLevel(Enchantment.fortune, itemStack) * 1.5f;
        return damage;
    }

    public static int getEnchantLevelNoLimit(final Enchantment enchantment, final ItemStack itemStack)
    {
        return EnchantmentHelper.getEnchantmentLevel(enchantment.effectId, itemStack);
    }

    public static int getEnchantLevel(final Enchantment enchantment, final ItemStack itemStack)
    {
        return Math.min(getEnchantLevelNoLimit(enchantment, itemStack),
                enchantment.getMaxLevel());
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

package us.nebula.util.player;

import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;

/**
 * @author xgraza
 * @since 03/01/25
 */
public final class InventoryUtil
{
    private static final Minecraft MC = Minecraft.getMinecraft();

    public static final int PLAYER_INVENTORY_SIZE = 36;
    public static final int HOTBAR_SIZE = 9;

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

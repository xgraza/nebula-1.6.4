package nebula.client.util.player;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;

/**
 * @author Gavin
 * @since 08/18/23
 */
public class ItemUtils {

  /**
   * Checks if an ItemStack is an infinite item
   * @param itemStack the stack
   * @return if the stack size is less than 0 or greater than the max size
   */
  public static boolean infinite(ItemStack itemStack) {
    if (itemStack == null) return false;
    return itemStack.stackSize < 0
      || itemStack.stackSize > itemStack.getMaxStackSize();
  }

  /**
   * Gets the enchantment level capped by its max level
   * @param effectId the enchantment effect id
   * @param itemStack the item stack
   * @return the level (capped at getMaxLevel())
   */
  public static int enchantment(int effectId, ItemStack itemStack) {
    int level = EnchantmentHelper.getEnchantmentLevel(effectId, itemStack);
    return Math.min(Enchantment.enchantmentsList[effectId].getMaxLevel(), level);
  }
}

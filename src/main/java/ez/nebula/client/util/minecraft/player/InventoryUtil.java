package ez.nebula.client.util.minecraft.player;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

/**
 * @author xgraza
 * @since 03/01/25
 */
public final class InventoryUtil
{
    private static final Minecraft MC = Minecraft.getMinecraft();

    public static final int INVALID_SLOT = -1;
    public static final int PLAYER_INVENTORY_SIZE = 36;
    public static final int HOTBAR_SIZE = 9;

    public static final Predicate<ItemStack> BLOCK_FILTER =
            (stack) -> stack.getItem() instanceof ItemBlock;

    public static int getBestToolSlotFor(final Block attackedBlock)
    {
        float bestScore = 1.0f;
        int slot = -1;
        for (int i = 0; i < 9; ++i)
        {
            final ItemStack itemStack = MC.thePlayer.inventory.getStackInSlot(i);
            if (itemStack == null)
            {
                continue;
            }
            final float score = ItemUtil.getToolScore(itemStack, attackedBlock);
            if (score > bestScore)
            {
                bestScore = score;
                slot = i;
            }
        }
        return slot;
    }

    @SafeVarargs
    public static int getHotbarItem(final Class<? extends Item>... items)
    {
        if (items.length == 0)
        {
            return INVALID_SLOT;
        }
        for (int slot = 0; slot < HOTBAR_SIZE; ++slot)
        {
            final ItemStack itemStack = MC.thePlayer.inventory.getStackInSlot(slot);
            if (itemStack == null || itemStack.getItem() == null)
            {
                continue;
            }
            for (final Class<? extends Item> type : items)
            {
                if (type.isAssignableFrom(itemStack.getItem().getClass()))
                {
                    return slot;
                }
            }
        }
        return INVALID_SLOT;
    }

    public static int getHotbarSlot(final Predicate<ItemStack> filter)
    {
        return getSlot(0, HOTBAR_SIZE, filter);
    }

    public static int getSlot(final int start,
                              final int end,
                              final Predicate<ItemStack> filter)
    {
        for (int slot = start; slot < end; ++slot)
        {
            final ItemStack itemStack = MC.thePlayer.inventory.getStackInSlot(slot);
            if (itemStack != null && filter.test(itemStack))
            {
                return slot;
            }
        }
        return INVALID_SLOT;
    }
}

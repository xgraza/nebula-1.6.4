package ez.nebula.client.impl.module.player;

import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.listener.event.game.EventUpdate;
import ez.nebula.client.api.listener.event.world.EventChangeWorld;
import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.setting.NumberSetting;
import ez.nebula.client.api.setting.Setting;
import ez.nebula.client.util.minecraft.player.InventoryUtil;
import ez.nebula.client.util.minecraft.player.ItemUtil;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xgraza
 * @since 7/2/26
 */
@ModuleManifest(name = "HotbarRefill",
        description = "Automatically refills your hotbar",
        category = ModuleCategory.PLAYER)
public final class HotbarRefillModule extends Module
{
    private final Map<Integer, ItemStack> slotItemStackMap = new HashMap<>();

    private final NumberSetting<Double> percentSetting = numberBuilder("Percent", 45.0)
            .setMin(1.0)
            .setMax(99.0)
            .setScale(0.1)
            .setDescription("What percentage of a stack should be depleted before refilling")
            .build();
    private final Setting<Boolean> rememberSetting = builder("Remember", false)
            .setDescription("If to remember what was in a slot before it got removed to then replace it when the item comes again")
            .build();

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        cacheHotbarSlots();
        for (final int slot : slotItemStackMap.keySet())
        {
            final ItemStack itemStack = MC.thePlayer.inventory.getStackInSlot(slot);

            double percentage = 0.0;
            if (itemStack != null)
            {
                percentage = (itemStack.stackSize / (double) itemStack.getMaxStackSize()) * 100.0;
            }
            if (percentSetting.getValue() < percentage)
            {
                continue;
            }
            final int matchingSlot = getMatchingSlot(itemStack == null ? slotItemStackMap.get(slot) : itemStack);
            if (matchingSlot == -1)
            {
                continue;
            }

            final ItemStack stack = MC.thePlayer.inventory.getStackInSlot(matchingSlot);
            boolean hasLeftover = itemStack != null && stack.stackSize + itemStack.stackSize > itemStack.getMaxStackSize();

            InventoryUtil.windowClick(InventoryUtil.toPacketSlot(matchingSlot), InventoryUtil.ClickType.PICKUP);
            InventoryUtil.windowClick(InventoryUtil.toPacketSlot(slot), InventoryUtil.ClickType.PICKUP);
            if (hasLeftover)
            {
                InventoryUtil.windowClick(InventoryUtil.toPacketSlot(matchingSlot), InventoryUtil.ClickType.PICKUP);
            }
            return;
        }
    };

    @Subscribe
    private final EventListener<EventChangeWorld> changeWorldEventListener = event ->
    {
        slotItemStackMap.clear();
    };

    private int getMatchingSlot(final ItemStack itemStack)
    {
        for (int i = 9; i < 36; ++i)
        {
            final ItemStack stack = MC.thePlayer.inventory.getStackInSlot(i);
            if (stack != null && itemStack.isItemEqual(stack) && !ItemUtil.isInfinite(stack))
            {
                return i;
            }
        }
        return -1;
    }

    private void cacheHotbarSlots()
    {
        for (int i = 0; i < 9; ++i)
        {
            final ItemStack itemStack = MC.thePlayer.inventory.getStackInSlot(i);
            if (itemStack == null)
            {
                if (!rememberSetting.getValue())
                {
                    slotItemStackMap.remove(i);
                }
                continue;
            }
            if (ItemUtil.isInfinite(itemStack) || itemStack.stackSize >= itemStack.getMaxStackSize())
            {
                continue;
            }
            slotItemStackMap.put(i, itemStack);
        }
    }
}

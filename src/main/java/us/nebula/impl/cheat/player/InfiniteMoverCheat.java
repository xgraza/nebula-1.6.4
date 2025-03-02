package us.nebula.impl.cheat.player;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.Slot;
import us.nebula.api.listener.EventListener;
import us.nebula.api.listener.Subscribe;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.impl.event.player.EventContainerAction;
import us.nebula.util.ChatUtil;
import us.nebula.util.InventoryUtil;

import static us.nebula.util.InventoryUtil.HOTBAR_SIZE;
import static us.nebula.util.InventoryUtil.PLAYER_INVENTORY_SIZE;

/**
 * @author xgraza
 * @since 08/18/23 (ported on 03/01/25)
 */
@CheatManifest(name = "InfiniteMover",
        description = "Allows infinite items to be moved freely in inventories or containers",
        category = CheatCategory.PLAYER)
public final class InfiniteMoverCheat extends Cheat
{
    @Subscribe
    private final EventListener<EventContainerAction> containerActionEventListener = event ->
    {
        // checks to see if the item is infinite - or exists at all
        if (event.getSlot() == null
                || !event.getSlot().getHasStack()
                || !InventoryUtil.isInfinite(event.getSlot().getStack()))
        {
            return;
        }

        // if we are not shift clicking the item
        if (event.getAction() != 1)
        {
            return;
        }

        // don't do anything
        event.setCanceled(true);

        final Container container = MC.thePlayer.openContainer;

        // the slot to move the infinite item to
        int moveSlot = -1;

        // handle chest containers & player inventory containers

        if (container instanceof ContainerChest)
        {
            // get the actual container size
            // inventorySlots.size() returns the size of the container + the player inventory
            // we only want the size of the chest container
            final int containerSize = container.inventorySlots.size()
                    - PLAYER_INVENTORY_SIZE - 1;

            // if the click was within the container
            // container slots go to 0-(size - 1)
            final boolean inContainer = containerSize >= event.getSlotIndex();

            final int start = inContainer
                    ? containerSize + 1
                    : 0;
            final int end = (inContainer
                    ? containerSize + PLAYER_INVENTORY_SIZE
                    : containerSize)
                    + 1;

            for (int containerSlot = start; containerSlot < end; ++containerSlot)
            {
                final Slot slot = (Slot) container.inventorySlots.get(containerSlot);
                if (slot != null && slot.getHasStack())
                {
                    continue;
                }

                moveSlot = containerSlot;
                if (!inContainer)
                {
                    break;
                }
            }
        } else if (container instanceof ContainerPlayer)
        {

            // slots 0-8 are the armor slots & crafting slots
            // the actual inventory starts at slot 9
            // then 9 + the inventory size (27) = 36
            // but the first hotbar slot starts at 36 (since the last internal slot is 35 due to indices)
            final boolean inHotbar = event.getSlotIndex() >= PLAYER_INVENTORY_SIZE;

            final int start = inHotbar
                    ? HOTBAR_SIZE
                    : PLAYER_INVENTORY_SIZE;
            final int end = inHotbar
                    ? PLAYER_INVENTORY_SIZE
                    : PLAYER_INVENTORY_SIZE + HOTBAR_SIZE;

            for (int invSlot = start; invSlot < end; ++invSlot)
            {
                final Slot slot = (Slot) container.inventorySlots.get(invSlot);
                if (slot != null && !slot.getHasStack())
                {
                    moveSlot = invSlot;
                    break;
                }
            }
        }
        ChatUtil.send("moveSlot: " +moveSlot);

        // if we did not find a slot, don't do anything
        if (moveSlot == -1)
        {
            return;
        }

        // pickup the item from the container/inventory
        MC.playerController.windowClick(event.getWindowId(),
                event.getSlotIndex(), 0, 0, MC.thePlayer);
        // put the item into the slot where it would have been if it were shift clicked
        MC.playerController.windowClick(event.getWindowId(),
                moveSlot, 0, 0, MC.thePlayer);
    };
}

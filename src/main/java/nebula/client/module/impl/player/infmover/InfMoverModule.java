package nebula.client.module.impl.player.infmover;

import nebula.client.listener.bus.Listener;
import nebula.client.listener.bus.Subscribe;
import nebula.client.listener.event.player.inventory.EventSlotClick;
import nebula.client.module.Module;
import nebula.client.module.ModuleMeta;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.Slot;

import static nebula.client.util.player.InventoryUtils.HOTBAR_SIZE;
import static nebula.client.util.player.InventoryUtils.PLAYER_INVENTORY_SIZE;
import static nebula.client.util.player.ItemUtils.infinite;

/**
 * @author Gavin
 * @since 08/18/23
 */
@SuppressWarnings("unused")
@ModuleMeta(name = "InfMover",
  description = "Moves infinite items in and out of containers")
public class InfMoverModule extends Module {

  @Subscribe
  private final Listener<EventSlotClick> slotClick = event -> {

    // checks to see if the item is infinite - or exists at all
    if (event.containerSlot() == null
      || !event.containerSlot().getHasStack()
      || !infinite(event.containerSlot().getStack())) return;

    // if we are not shift clicking the item
    if (event.action() != 1) return;

    // don't do anything
    event.setCanceled(true);

    Container container = mc.thePlayer.openContainer;

    // the slot to move the infinite item to
    int moveSlot = -1;

    // handle chest containers & player inventory containers

    if (container instanceof ContainerChest) {
      // get the actual container size
      // inventorySlots.size() returns the size of the container + the player inventory
      // we only want the size of the chest container
      int containerSize = container.inventorySlots.size()
        - PLAYER_INVENTORY_SIZE - 1;

      // if the click was within the container
      // container slots go to 0-(size - 1)
      boolean inContainer = containerSize >= event.slot();

      int start = inContainer
        ? containerSize + 1
        : 0;
      int end = (inContainer
        ? containerSize + PLAYER_INVENTORY_SIZE
        : containerSize)
        + 1;

      for (int containerSlot = start; containerSlot < end; ++containerSlot) {
        Slot slot = (Slot) container.inventorySlots.get(containerSlot);
        if (slot.getHasStack()) continue;

        moveSlot = containerSlot;
        if (!inContainer) break;
      }
    } else if (container instanceof ContainerPlayer) {

      // slots 0-8 are the armor slots & crafting slots
      // the actual inventory starts at slot 9
      // then 9 + the inventory size (27) = 36
      // but the first hotbar slot starts at 36 (since the last internal slot is 35 due to indices)
      boolean inHotbar = event.slot() >= PLAYER_INVENTORY_SIZE;

      int start = inHotbar
        ? HOTBAR_SIZE
        : PLAYER_INVENTORY_SIZE;
      int end = inHotbar
        ? PLAYER_INVENTORY_SIZE
        : PLAYER_INVENTORY_SIZE + HOTBAR_SIZE;

      for (int invSlot = start; invSlot < end; ++invSlot) {
        Slot slot = (Slot) container.inventorySlots.get(invSlot);
        if (!slot.getHasStack()) {
          moveSlot = invSlot;
          break;
        }
      }
    }

    // if we did not find a slot, don't do anything
    if (moveSlot == -1) return;

    // pickup the item from the container/inventory
    mc.playerController.windowClick(event.windowId(),
      event.slot(), 0, 0, mc.thePlayer);
    // put the item into the slot where it would have been if it were shift clicked
    mc.playerController.windowClick(event.windowId(),
      moveSlot, 0, 0, mc.thePlayer);
  };
}

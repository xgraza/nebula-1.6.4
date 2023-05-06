package lol.nebula.module.player;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.render.gui.EventContainerSlotClick;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import static lol.nebula.util.player.InventoryUtils.isInfinite;
import static lol.nebula.util.player.InventoryUtils.normalize;

/**
 * @author aesthetical
 * @since 04/30/23
 */
public class InfiniteMover extends Module {
    public InfiniteMover() {
        super("Infinite Mover", "Moves infinite items", ModuleCategory.PLAYER);
    }

    @Listener
    public void onContainerSlotClick(EventContainerSlotClick event) {

        // if the action is not a shift click, return
        if (event.getAction() != 1) return;
        int clickedSlot = event.getSlot();

        // get the stack that it clicked on
        ItemStack stack = null;
        if (mc.currentScreen instanceof GuiChest) {
            GuiChest container = (GuiChest) mc.currentScreen;
            int slots = container.field_147002_h.inventorySlots.size();
            if (clickedSlot >= slots) {
                clickedSlot -= 36;
                stack = mc.thePlayer.inventory.getStackInSlot(clickedSlot);
            } else {
                Slot s = (Slot) container.field_147002_h.inventorySlots.get(clickedSlot);
                if (s != null && s.getHasStack() && s.getStack() != null) stack = s.getStack();
            }
        } else {
            if (clickedSlot >= 36) clickedSlot -= 36;
            stack = mc.thePlayer.inventory.getStackInSlot(clickedSlot);
        }

        // if the stack is null or is not an infinite, return
        if (stack == null || !isInfinite(stack)) return;

        // this will be the slot the infinite is put in
        int clickTo = -1;
        if (mc.currentScreen instanceof GuiChest) {
            GuiChest chest = (GuiChest) mc.currentScreen;
            int slots = chest.field_147015_w.getSizeInventory();

            // chest container
            if (event.getSlot() <= slots - 1) {

                int emptySlot = -1;
                for (int i = 0; i < 36; ++i) {
                    ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);
                    if (itemStack == null) {
                        emptySlot = i;
                        break;
                    }
                }

                if (emptySlot != -1) {
                    if (emptySlot < 9) {
                        clickTo = slots + (36 - (9 - emptySlot));
                    } else {
                        clickTo = ((slots + 27 - (35 - emptySlot)) - 1);
                    }
                }

            } else {
                for (int i = 0; i < slots; ++i) {
                    Slot slot = (Slot) chest.field_147002_h.inventorySlots.get(i);
                    if (slot == null || slot.getStack() == null) {
                        clickTo = i;
                        break;
                    }
                }
            }

        } else {

            // if the inf is in a hotbar slot, we're going to want to put it into the inventory
            int start = clickedSlot <= 8 ? 9 : 0;
            int end = clickedSlot <= 8 ? 36 : 9;

            for (int i = start; i < end; ++i) {
                ItemStack s = mc.thePlayer.inventory.getStackInSlot(i);
                if (s == null) {
                    clickTo = i;
                    break;
                }
            }

            if (clickTo != -1) clickTo = normalize(clickTo);
        }

        if (clickTo != -1) {
            event.cancel();
            mc.playerController.windowClick(event.getWindowId(), event.getSlot(), 0, 0, mc.thePlayer);
            mc.playerController.windowClick(event.getWindowId(), clickTo, 0, 0, mc.thePlayer);
        }
    }
}

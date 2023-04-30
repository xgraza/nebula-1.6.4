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
        int slot = event.getSlot();

        ItemStack stack = null;
        if (mc.currentScreen instanceof GuiChest) {
            GuiChest container = (GuiChest) mc.currentScreen;
            int slots = container.field_147002_h.inventorySlots.size();
            if (slot >= slots) {
                slot -= 36;
                stack = mc.thePlayer.inventory.getStackInSlot(slot);
            } else {
                Slot s = (Slot) container.field_147002_h.inventorySlots.get(slot);
                if (s != null && s.getHasStack() && s.getStack() != null) stack = s.getStack();
            }
        } else {
            if (slot >= 36) slot -= 36;
            stack = mc.thePlayer.inventory.getStackInSlot(slot);
        }

        // if the stack is null or is not an infinite, return
        if (stack == null || !isInfinite(stack)) return;

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

                if (emptySlot != -1) clickTo = emptySlot < 9
                        ? slots + (36 - (9 - emptySlot))
                        : (emptySlot + slots) - 1;

            } else {
                for (int i = 0; i < slots; ++i) {
                    Slot s = (Slot) chest.field_147002_h.inventorySlots.get(i);
                    if (s == null || !s.getHasStack()) {
                        clickTo = i;
                        break;
                    }
                }
            }

        } else {

            // if the inf is in a hotbar slot, we're going to want to put it into the inventory
            int start = slot <= 8 ? 9 : 0;
            int end = slot <= 8 ? 36 : 9;

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
            mc.playerController.windowClick(event.getWindowId(), event.getSlot(), 0, 0, mc.thePlayer);
            mc.playerController.windowClick(event.getWindowId(), clickTo, 0, 0, mc.thePlayer);
        }
    }
}

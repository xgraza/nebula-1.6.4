package wtf.nebula.client.impl.module.miscellaneous;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.impl.event.impl.inventory.EventWindowClick;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;

public class InfiniteMover extends ToggleableModule {
    public InfiniteMover() {
        super("Infinite Mover", new String[]{"infintemover", "infmover"}, ModuleCategory.MISCELLANEOUS);
    }

    @EventListener
    public void onWindowClick(EventWindowClick event) {
        if (event.getAction() != 1) {
            return;
        }

        int slot = event.getSlot();

        ItemStack stack = null;

        if (mc.currentScreen instanceof GuiChest) {
            GuiChest container = (GuiChest) mc.currentScreen;
            int slots = container.container.inventorySlots.size();
            if (slot >= slots) {
                slot -= 36;
                stack = mc.thePlayer.inventory.getStackInSlot(slot);
            } else {
                Slot s = (Slot) container.container.inventorySlots.get(slot);
                if (s != null && s.getHasStack() && s.getStack() != null) {
                    stack = s.getStack();
                }
            }
        } else {
            if (slot >= 36) {
                slot -= 36;
            }

            stack = mc.thePlayer.inventory.getStackInSlot(slot);
        }

        if (stack != null && stack.stackSize < 0) {

            int clickTo = -1;

            if (mc.currentScreen instanceof GuiChest) {

                GuiChest chest = (GuiChest) mc.currentScreen;
                int slots = chest.lowerInventory.getSizeInventory();

                // chest container
                if (event.getSlot() <= slots - 1) {

                    int emptySlot = -1;

                    for (int i = 0; i < 36; ++i) {
                        ItemStack s = mc.thePlayer.inventory.getStackInSlot(i);
                        if (s == null) {
                            emptySlot = i;
                            break;
                        }
                    }

                    if (emptySlot != -1) {

                        if (emptySlot < 9) {
                            clickTo = slots + (36 - (9 - emptySlot));
                        } else {
                            clickTo = (emptySlot + slots) - 1;
                        }
                    }

                } else {

                    for (int i = 0; i < slots; ++i) {
                        Slot s = (Slot) chest.container.inventorySlots.get(i);
                        if (s == null || !s.getHasStack()) {
                            clickTo = i;
                            break;
                        }
                    }
                }

//                if (clickTo != -1) {
//                    clickTo = clickTo < 9 ? clickTo + 36 : clickTo;
//                }

            } else {

                int start, end;

                // if the inf is in a hotbar slot, we're gonna want to put it into the inventory
                if (slot <= 8) {

                    start = 9;
                    end = 36;

                } else {

                    start = 0;
                    end = 9;
                }

                for (int i = start; i < end; ++i) {
                    ItemStack s = mc.thePlayer.inventory.getStackInSlot(i);
                    if (s == null) {
                        clickTo = i;
                        break;
                    }
                }

                // print(String.valueOf(clickTo));

                if (clickTo != -1) {
                    clickTo = clickTo < 9 ? clickTo + 36 : clickTo;
                }

            }

            if (clickTo != -1) {
                mc.playerController.windowClick(event.getWindowId(), event.getSlot(), 0, 0, mc.thePlayer);
                mc.playerController.windowClick(event.getWindowId(), clickTo, 0, 0, mc.thePlayer);
            }
        }
    }
}

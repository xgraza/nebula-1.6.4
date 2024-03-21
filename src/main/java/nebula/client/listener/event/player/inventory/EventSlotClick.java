package nebula.client.listener.event.player.inventory;

import nebula.client.listener.bus.Cancelable;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;

/**
 * @author Gavin
 * @since 08/18/23
 */
public class EventSlotClick extends Cancelable {

  private final Slot containerSlot;
  private final GuiContainer gui;
  private int windowId, slot, mouseButton, action;

  public EventSlotClick(Slot containerSlot, GuiContainer gui, int windowId, int slot, int mouseButton, int action) {
    this.containerSlot = containerSlot;
    this.gui = gui;
    this.windowId = windowId;
    this.slot = slot;
    this.mouseButton = mouseButton;
    this.action = action;
  }

  public Slot containerSlot() {
    return containerSlot;
  }

  public GuiContainer gui() {
    return gui;
  }

  public int windowId() {
    return windowId;
  }

  public void setWindowId(int windowId) {
    this.windowId = windowId;
  }

  public int slot() {
    return slot;
  }

  public void setSlot(int slot) {
    this.slot = slot;
  }

  public int mouseButton() {
    return mouseButton;
  }

  public void setMouseButton(int mouseButton) {
    this.mouseButton = mouseButton;
  }

  public int action() {
    return action;
  }

  public void setAction(int action) {
    this.action = action;
  }
}

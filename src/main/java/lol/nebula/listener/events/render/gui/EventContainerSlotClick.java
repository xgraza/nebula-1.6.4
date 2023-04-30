package lol.nebula.listener.events.render.gui;

import lol.nebula.listener.bus.CancelableEvent;

/**
 * @author aesthetical
 * @since 04/30/23
 */
public class EventContainerSlotClick extends CancelableEvent {
    private final int windowId, slot, mouseButton, action;

    public EventContainerSlotClick(int windowId, int slot, int mouseButton, int action) {
        this.windowId = windowId;
        this.slot = slot;
        this.mouseButton = mouseButton;
        this.action = action;
    }

    public int getWindowId() {
        return windowId;
    }

    public int getSlot() {
        return slot;
    }

    public int getMouseButton() {
        return mouseButton;
    }

    public int getAction() {
        return action;
    }
}

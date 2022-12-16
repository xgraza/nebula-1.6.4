package wtf.nebula.client.impl.event.impl.inventory;

import me.bush.eventbus.event.Event;

public class EventWindowClick extends Event {
    private final int windowId, slot, mouseButton, action;

    public EventWindowClick(int windowId, int slot, int mouseButton, int action) {
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

    @Override
    protected boolean isCancellable() {
        return true;
    }
}

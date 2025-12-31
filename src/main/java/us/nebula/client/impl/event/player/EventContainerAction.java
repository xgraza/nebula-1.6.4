package us.nebula.client.impl.event.player;

import net.minecraft.inventory.Slot;
import us.nebula.client.api.listener.Event;

/**
 * @author xgraza
 * @since 03/01/25
 */
public final class EventContainerAction extends Event
{
    private final int windowId, slotIndex, action, mouseButton;
    private final Slot slot;

    public EventContainerAction(int windowId, int slotIndex, int action, int mouseButton, Slot slot)
    {
        this.windowId = windowId;
        this.slotIndex = slotIndex;
        this.action = action;
        this.mouseButton = mouseButton;
        this.slot = slot;
    }

    public int getWindowId()
    {
        return windowId;
    }

    public int getSlotIndex()
    {
        return slotIndex;
    }

    public int getAction()
    {
        return action;
    }

    public int getMouseButton()
    {
        return mouseButton;
    }

    public Slot getSlot()
    {
        return slot;
    }
}

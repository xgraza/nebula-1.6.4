package wtf.nebula.client.impl.event.impl.render;

import me.bush.eventbus.event.Event;
import net.minecraft.client.gui.GuiScreen;

public class EventOpenGUI extends Event {
    private final GuiScreen current, opened;

    public EventOpenGUI(GuiScreen current, GuiScreen opened) {
        this.current = current;
        this.opened = opened;
    }

    public GuiScreen getCurrent() {
        return current;
    }

    public GuiScreen getOpened() {
        return opened;
    }

    @Override
    protected boolean isCancellable() {
        return true;
    }
}

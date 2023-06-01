package lol.nebula.listener.events.render.gui;

import net.minecraft.client.gui.GuiScreen;

public class EventDisplayGui {
    private final GuiScreen current, displayed;

    public EventDisplayGui(GuiScreen current, GuiScreen displayed) {
        this.current = current;
        this.displayed = displayed;
    }

    public GuiScreen getCurrent() {
        return current;
    }

    public GuiScreen getDisplayed() {
        return displayed;
    }
}

package wtf.nebula.event;

import me.bush.eventbus.event.Event;
import net.minecraft.client.gui.GuiScreen;

public class GuiOpenEvent extends Event {
    private final GuiScreen newScreen, oldScreen;

    public GuiOpenEvent(GuiScreen newScreen, GuiScreen oldScreen) {
        this.newScreen = newScreen;
        this.oldScreen = oldScreen;
    }

    public GuiScreen getNewScreen() {
        return newScreen;
    }

    public GuiScreen getOldScreen() {
        return oldScreen;
    }

    @Override
    protected boolean isCancellable() {
        return false;
    }
}

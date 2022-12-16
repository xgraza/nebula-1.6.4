package wtf.nebula.client.impl.event.impl.render;

import me.bush.eventbus.event.Event;
import net.minecraft.client.gui.GuiPlayerInfo;

public class EventRenderTabListName extends Event {
    private final GuiPlayerInfo info;
    private String text;

    public EventRenderTabListName(GuiPlayerInfo info, String text) {
        this.info = info;
        this.text = text;
    }

    public GuiPlayerInfo getInfo() {
        return info;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    protected boolean isCancellable() {
        return true;
    }
}

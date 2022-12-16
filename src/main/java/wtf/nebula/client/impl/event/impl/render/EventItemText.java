package wtf.nebula.client.impl.event.impl.render;

import me.bush.eventbus.event.Event;
import net.minecraft.item.ItemStack;

public class EventItemText extends Event {
    private final ItemStack stack;
    private String text;

    public EventItemText(ItemStack stack, String text) {
        this.stack = stack;
        this.text = text;
    }

    public ItemStack getStack() {
        return stack;
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

package wtf.nebula.client.impl.event.impl.client;

import me.bush.eventbus.event.Event;
import net.minecraft.util.IChatComponent;

public class EventAddChatMessage extends Event {
    private final IChatComponent chatComponent;
    private final int messageId;

    public EventAddChatMessage(IChatComponent chatComponent, int messageId) {
        this.chatComponent = chatComponent;
        this.messageId = messageId;
    }

    public IChatComponent getChatComponent() {
        return chatComponent;
    }

    public int getMessageId() {
        return messageId;
    }

    @Override
    protected boolean isCancellable() {
        return true;
    }
}

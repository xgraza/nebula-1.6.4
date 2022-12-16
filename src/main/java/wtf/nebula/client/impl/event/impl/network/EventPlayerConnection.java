package wtf.nebula.client.impl.event.impl.network;

import me.bush.eventbus.event.Event;

public class EventPlayerConnection extends Event {
    private final Action action;
    private final String username;

    public EventPlayerConnection(Action action, String username) {
        this.action = action;
        this.username = username;
    }

    public Action getAction() {
        return action;
    }

    public String getUsername() {
        return username;
    }

    @Override
    protected boolean isCancellable() {
        return false;
    }

    public enum Action {
        JOIN, LEAVE
    }
}

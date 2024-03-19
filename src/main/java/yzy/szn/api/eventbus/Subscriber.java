package yzy.szn.api.eventbus;

import java.util.function.Consumer;

/**
 * @author graza
 * @since 02/17/24
 */
public final class Subscriber {

    private final Object parent;
    private final Consumer<Event> listener;
    private final Subscribe subscribe;

    public Subscriber(final Object parent, final Consumer<Event> listener, final Subscribe eventLink) {
        this.parent = parent;
        this.listener = listener;
        this.subscribe = eventLink;
    }

    public void invoke(Event event) {
        listener.accept(event);
    }

    public Object getParent() {
        return parent;
    }

    public Subscribe getEventLink() {
        return subscribe;
    }
}

package yzy.szn.api.eventbus;

/**
 * @author graza
 * @since 02/17/24
 */
public class Event {

    private final boolean cancelable;
    private boolean canceled;

    public Event() {
        this(false);
    }

    public Event(final boolean cancelable) {
        this.cancelable = cancelable;
    }

    public boolean isCancelable() {
        return cancelable;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public boolean isCanceled() {
        return canceled;
    }
}

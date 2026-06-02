package ez.nebula.client.api.listener;

/**
 * @author xgraza
 * @since 02/14/25
 */
@SuppressWarnings({ "raw" })
public final class Subscriber
{
    private final EventListener<Event> eventListener;
    private final Subscribe properties;
    private final Object parent;

    public Subscriber(final EventListener<Event> eventListener, final Subscribe properties, final Object parent)
    {
        this.eventListener = eventListener;
        this.properties = properties;
        this.parent = parent;
    }

    public void invoke(final Event event)
    {
        eventListener.execute(event);
    }

    public EventListener<?> getEventListener()
    {
        return eventListener;
    }

    public Subscribe getProperties()
    {
        return properties;
    }

    public Object getParent()
    {
        return parent;
    }
}

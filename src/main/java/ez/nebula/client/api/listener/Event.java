package ez.nebula.client.api.listener;

/**
 * @author xgraza
 * @since 02/14/25
 */
public class Event
{
    private boolean canceled;

    public void cancel()
    {
        canceled = !canceled;
    }

    public void setCanceled(final boolean canceled)
    {
        this.canceled = canceled;
    }

    public boolean isCanceled()
    {
        return canceled;
    }
}

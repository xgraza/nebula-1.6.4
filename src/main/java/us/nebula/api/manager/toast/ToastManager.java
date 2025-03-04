package us.nebula.api.manager.toast;

import us.nebula.api.listener.EventBus;
import us.nebula.api.listener.EventListener;
import us.nebula.api.listener.Subscribe;
import us.nebula.api.manager.ITypedManager;
import us.nebula.impl.event.render.EventRender2D;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author xgraza
 * @since 03/03/25
 */
public final class ToastManager implements ITypedManager<Toast>
{
    private static final AtomicInteger TOAST_ID = new AtomicInteger();
    private static final double TOAST_PADDING = 2.5;

    private final Map<Integer, Toast> toastIdMap = new ConcurrentHashMap<>();
    private final List<Toast> toastList = new CopyOnWriteArrayList<>();

    @Subscribe
    private final EventListener<EventRender2D> render2DEventListener = event ->
    {
        double posY = event.getResolution().getScaledHeight() - 50;
        for (final Toast toast : toastList)
        {
            if (toast.isDead())
            {
                toastIdMap.remove(toast.getId());
                toastList.remove(toast);
                continue;
            }
            posY -= (toast.render(posY, event.getResolution()) + TOAST_PADDING);
        }
    };

    @Override
    public void init()
    {
        EventBus.subscribe(this);
    }

    public int info(final String title, final String details, final long lifetimeMS)
    {
        final int id = TOAST_ID.getAndIncrement();
        addToast(id, new Toast(id, ToastType.INFO, title, details, lifetimeMS));
        return id;
    }

    public void info(final int id, final String title, final String details, final long lifetimeMS)
    {
        addToast(id, new Toast(id, ToastType.INFO, title, details, lifetimeMS));
    }

    public int error(final String title, final String details, final long lifetimeMS)
    {
        final int id = TOAST_ID.getAndIncrement();
        addToast(id, new Toast(id, ToastType.ERROR, title, details, lifetimeMS));
        return id;
    }

    public void error(final int id, final String title, final String details, final long lifetimeMS)
    {
        addToast(id, new Toast(id, ToastType.ERROR, title, details, lifetimeMS));
    }

    public int warn(final String title, final String details, final long lifetimeMS)
    {
        final int id = TOAST_ID.getAndIncrement();
        addToast(id, new Toast(id, ToastType.WARNING, title, details, lifetimeMS));
        return id;
    }

    public void warn(final int id, final String title, final String details, final long lifetimeMS)
    {
        addToast(id, new Toast(id, ToastType.WARNING, title, details, lifetimeMS));
    }

    public void edit(final int id, final String title, final String details)
    {
        final Toast toast = toastIdMap.get(id);
        if (toast == null)
        {
            return;
        }
        toast.setTitle(title);
        toast.setDetails(details);
        toast.resetTime();
    }

    private void addToast(final int id, final Toast toast)
    {
        if (toastIdMap.containsKey(id))
        {
            edit(id, toast.getTitle(), toast.getDetails());
            return;
        }
        toastIdMap.put(id, toast);
        toastList.add(toast);
    }

    @Override
    public List<Toast> getAll()
    {
        return toastList;
    }
}

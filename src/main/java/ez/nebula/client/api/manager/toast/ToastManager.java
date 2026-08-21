package ez.nebula.client.api.manager.toast;

import ez.nebula.client.api.manager.toast.trait.ToastType;
import ez.nebula.client.api.listener.EventBus;
import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.manager.ITypedManager;
import ez.nebula.client.api.listener.event.render.EventRender2D;
import net.minecraft.client.Minecraft;

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
    private static final Minecraft MC = Minecraft.getMinecraft();
    private static final AtomicInteger TOAST_ID = new AtomicInteger();
    private static final double TOAST_PADDING = 2.5;

    private final Map<Integer, Toast> toastIdMap = new ConcurrentHashMap<>();
    private final List<Toast> toastList = new CopyOnWriteArrayList<>();

    @Subscribe
    private final EventListener<EventRender2D> render2DEventListener = event ->
    {
        if (toastList.isEmpty())
        {
            return;
        }
        MC.mcProfiler.startSection("toasts");
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
        MC.mcProfiler.endSection();
    };

    @Override
    public void init()
    {
        EventBus.subscribe(this);
    }

    public void info(final String title, final String details, final long lifetimeMS)
    {
        final int id = TOAST_ID.getAndIncrement();
        addToast(id, new Toast(id, ToastType.INFO, title, details, lifetimeMS));
    }

    public void info(final int id, final String title, final String details, final long lifetimeMS)
    {
        addToast(id, new Toast(id, ToastType.INFO, title, details, lifetimeMS));
    }

    public void error(final String title, final String details, final long lifetimeMS)
    {
        final int id = TOAST_ID.getAndIncrement();
        addToast(id, new Toast(id, ToastType.ERROR, title, details, lifetimeMS));
    }

    public void error(final int id, final String title, final String details, final long lifetimeMS)
    {
        addToast(id, new Toast(id, ToastType.ERROR, title, details, lifetimeMS));
    }

    public void warn(final String title, final String details, final long lifetimeMS)
    {
        final int id = TOAST_ID.getAndIncrement();
        addToast(id, new Toast(id, ToastType.WARNING, title, details, lifetimeMS));
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

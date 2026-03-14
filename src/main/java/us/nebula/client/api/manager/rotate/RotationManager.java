package us.nebula.client.api.manager.rotate;

import net.minecraft.client.Minecraft;
import us.nebula.client.api.listener.EventBus;
import us.nebula.client.api.listener.EventListener;
import us.nebula.client.api.listener.Subscribe;
import us.nebula.client.api.manager.IManager;
import us.nebula.client.impl.event.player.EventMoveUpdate;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author xgraza
 * @since 03/22/25
 */
public final class RotationManager implements IManager
{
    private static final Minecraft MC = Minecraft.getMinecraft();

    private final float[] serverAngles = new float[2];
    private final float[] spoofedAngles = { Float.NaN, Float.NaN };
    private int spoofPrority = -1;

    private final Queue<QueuedRotation> queuedRotationQueue = new ConcurrentLinkedQueue<>();
    private QueuedRotation queuedRotation;

    @Subscribe
    private final EventListener<EventMoveUpdate> moveUpdateEventListener = event ->
    {
        if (queuedRotation == null)
        {
            if (!queuedRotationQueue.isEmpty())
            {
                queuedRotation = queuedRotationQueue.poll();
            }
        } else
        {
            if (queuedRotation.priority >= spoofPrority)
            {
                spoof(queuedRotation.yaw, queuedRotation.pitch, queuedRotation.priority);
            }
            if (serverAngles[0] == queuedRotation.yaw && serverAngles[1] == queuedRotation.pitch)
            {
                queuedRotation.callback.onServerRotateConfirm(serverAngles[0], serverAngles[1]);
                queuedRotation = null;
            }
        }

        if (isRotationValid(spoofedAngles))
        {
            event.setYaw(spoofedAngles[0]);
            event.setPitch(spoofedAngles[1]);
            setInvalid(spoofedAngles);
        }
        serverAngles[0] = event.getYaw();
        serverAngles[1] = event.getPitch();

        MC.thePlayer.rotationYawHead = serverAngles[0];
        MC.thePlayer.renderPitch = serverAngles[1];

    };

    @Override
    public void init()
    {
        EventBus.subscribe(this);
    }

    public void spoofAndConfirm(final float yaw,
                                final float pitch,
                                final int priority,
                                final RotationConfirmation callback)
    {
        if (!queuedRotationQueue.isEmpty()
                && (queuedRotation != null
                && !queuedRotation.equals(yaw, pitch, priority)))
        {
            for (final QueuedRotation queuedRotation : queuedRotationQueue)
            {
                if (queuedRotation.equals(yaw, pitch, priority))
                {
                    return;
                }
            }
        }
        queuedRotationQueue.add(new QueuedRotation(yaw, pitch, priority, callback));
    }

    public void spoof(final float yaw, final float pitch, final int priority)
    {
        if (spoofPrority > priority)
        {
            return;
        }
        spoofPrority = priority;
        spoofedAngles[0] = yaw;
        spoofedAngles[1] = pitch;
    }

    private void setInvalid(final float[] angles)
    {
        angles[0] = Float.NaN;
        angles[1] = Float.NaN;
        spoofPrority = -1;
    }

    private boolean isRotationValid(final float[] angles)
    {
        return !Float.isNaN(angles[0]) && !Float.isNaN(angles[1]);
    }

    private static class QueuedRotation
    {
        private final float yaw, pitch;
        private final int priority;
        private final RotationConfirmation callback;

        public QueuedRotation(float yaw, float pitch, int priority, RotationConfirmation callback)
        {
            this.yaw = yaw;
            this.pitch = pitch;
            this.priority = priority;
            this.callback = callback;
        }

        public boolean equals(float yaw, float pitch, int priority)
        {
            return this.yaw == yaw && this.pitch == pitch && this.priority == priority;
        }
    }
}

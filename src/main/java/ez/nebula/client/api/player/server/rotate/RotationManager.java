package ez.nebula.client.api.player.server.rotate;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import ez.nebula.client.api.listener.EventBus;
import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.manager.IManager;
import ez.nebula.client.api.listener.event.player.EventMoveUpdate;

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

    public boolean spoof(final float yaw, final float pitch, final int priority)
    {
        if (spoofPrority > priority)
        {
            return false;
        }
        spoofPrority = priority;
        spoofedAngles[0] = yaw;
        spoofedAngles[1] = pitch;
        return true;
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

    public Vec3 getLook(float rotationYaw, float rotationPitch)
    {
        float var2 = MathHelper.cos(-rotationYaw * 0.017453292F - (float) Math.PI);
        float var3 = MathHelper.sin(-rotationYaw * 0.017453292F - (float) Math.PI);
        float var4 = -MathHelper.cos(-rotationPitch * 0.017453292F);
        float var5 = MathHelper.sin(-rotationPitch * 0.017453292F);
        return MC.theWorld.getWorldVec3Pool().getVecFromPool(var3 * var4, var5, var2 * var4);
    }

    public Vec3 getLook()
    {
        return getLook(serverAngles[0], serverAngles[1]);
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

package ez.nebula.client.api.manager.module.type;

import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.player.server.RotationManager;
import ez.nebula.client.Nebula;

import java.util.function.Consumer;

/**
 * @author xgraza
 * @since 9/4/26
 */
public abstract class RotationModule extends Module
{
    private final int rotationPriority;

    public RotationModule(final int rotationPriority)
    {
        super();
        this.rotationPriority = rotationPriority;
    }

    public RotationModule()
    {
        super();
        if (!getClass().isAnnotationPresent(RotationPriority.class))
        {
            throw new RuntimeException("A module extending RotationModule must have a @RotationPriority annotation");
        }
        rotationPriority = getClass().getDeclaredAnnotation(RotationPriority.class).value();
    }

    /**
     * Spoofs the server rotation angles
     * @param angles a two element float array containing [0] yaw and [1] pitch
     * @return if the rotation was able to be submitted based on priority
     */
    protected boolean rotate(final float[] angles)
    {
        if (angles == null || angles.length != 2)
        {
            return false;
        }
        return rotate(angles[0], angles[1]);
    }

    /**
     * Spoofs the server rotation angles
     * @param yaw the y rotation
     * @param pitch the x rotation
     * @return if the rotation was able to be submitted based on priority
     */
    protected boolean rotate(final float yaw, final float pitch)
    {
        return Nebula.INSTANCE.getRotationManager().spoof(yaw, pitch, rotationPriority);
    }

    /**
     * Spoofs angles, and waits until the server gets the angles to continue
     * @param angles a float[] of angles, [0] - yaw, [1] - pitch
     * @return false if the server has not spoofed there yet, else true if the server angles match the provided angles
     * @apiNote this is only good for a static rotation. for variable rotations, {@link RotationModule#queue(float[], Consumer)}
     */
    protected boolean rotateAndWait(final float[] angles)
    {
        if (angles == null || angles.length != 2)
        {
            return false;
        }
        return rotateAndWait(angles[0], angles[1]);
    }

    /**
     * Spoofs angles, and waits until the server gets the angles to continue
     * @param yaw the y rotation
     * @param pitch the x rotation
     * @return false if the server has not spoofed there yet, else true if the server angles match the provided angles
     * @apiNote this is only good for a static rotation. for variable rotations, {@link RotationModule#queue(float, float, Consumer)}
     */
    protected boolean rotateAndWait(final float yaw, final float pitch)
    {
        if (!rotate(yaw, pitch))
        {
            return false;
        }
        final float[] angles = Nebula.INSTANCE.getRotationManager().getServerAngles();
        return angles[0] == yaw && angles[1] == pitch;
    }

    /**
     * Queues angles to be submitted to the server
     * @param angles a float[] of angles, [0] - yaw, [1] - pitch
     * @param callback the callback to run once the rotation has been confirmed
     */
    protected void queue(final float[] angles, final Consumer<RotationManager.Rotation> callback)
    {
        queue(angles[0], angles[1], callback);
    }

    /**
     * Queues angles to be submitted to the server
     * @param yaw the y rotation
     * @param pitch the x rotation
     * @param callback the callback to run once the rotation has been confirmed
     */
    protected void queue(final float yaw, final float pitch, final Consumer<RotationManager.Rotation> callback)
    {
        Nebula.INSTANCE.getRotationManager().queue(yaw, pitch, rotationPriority, callback);
    }

    /**
     * Checks if based off of this module's rotation priority if it can submit a rotation
     * @return if we are able to rotate without overriding a more "important" feature
     */
    protected boolean canRotate()
    {
        return Nebula.INSTANCE.getRotationManager().canTakePrecedent(rotationPriority);
    }
}

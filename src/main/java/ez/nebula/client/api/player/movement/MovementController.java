package ez.nebula.client.api.player.movement;

import ez.nebula.client.api.listener.EventBus;
import ez.nebula.client.api.listener.event.input.EventUpdateInput;
import ez.nebula.client.api.player.movement.pathfinding.Node;
import ez.nebula.client.api.player.movement.pathfinding.Pathfinder;
import net.minecraft.client.Minecraft;
import net.minecraft.src.BlockPos;
import net.minecraft.util.MovementInput;
import net.minecraft.util.Vec3;

import java.util.List;

public final class MovementController
{
    private static final Minecraft MC = Minecraft.getMinecraft();

    private final Pathfinder pathfinder = new Pathfinder();
    private CustomInput input;
    private MovementInput movementInput;

    public List<Node> getPathTo(final BlockPos origin, final BlockPos pos)
    {
        return pathfinder.getPathList(origin, pos);
    }

    public void sneak(final boolean sneaking)
    {
        if (input == null)
        {
            return;
        }
        input.sneak = sneaking;
    }

    public boolean isSneaking()
    {
        return input != null && input.sneak;
    }

    public void jump(final boolean jumping)
    {
        if (input == null)
        {
            return;
        }
        input.jump = jumping;
    }

    public boolean isJumping()
    {
        return input != null && input.jump;
    }

    public void setMovement(final float[] movement)
    {
        setMovement(movement[0], movement[1]);
    }

    public void setMovement(final float forward, final float strafe)
    {
        if (input == null)
        {
            return;
        }
        input.moveStrafe = strafe;
        input.moveForward = forward;
    }

    public float[] getMovementFor(final Vec3 vector)
    {
        final Vec3 normalized = Vec3.createVectorHelper(vector.xCoord - MC.thePlayer.posX,
                vector.yCoord - MC.thePlayer.boundingBox.minY,
                vector.zCoord - MC.thePlayer.posZ).normalize();

        final double yaw = MC.thePlayer.rotationYaw * (Math.PI / 180.0f);
        double forward = normalized.dotProduct(Vec3.createVectorHelper(-Math.sin(yaw), 0, Math.cos(yaw)));
        double strafe = normalized.dotProduct(Vec3.createVectorHelper(Math.cos(yaw), 0, Math.sin(yaw)));

        final double length = Math.sqrt(forward + forward * strafe * strafe);
        if (length > 1.0)
        {
            forward /= length;
            strafe /= length;
        }

        return new float[]{ (float) forward, (float) strafe };
    }

    public void override()
    {
        if (movementInput != null)
        {
            return;
        }
        movementInput = MC.thePlayer.movementInput;
        input = new CustomInput();
        MC.thePlayer.movementInput = input;
    }

    public void restore()
    {
        if (movementInput == null)
        {
            return;
        }
        MC.thePlayer.movementInput = movementInput;
        movementInput = null;
        input = null;
    }

    public static final class CustomInput extends MovementInput
    {
        @Override
        public void updatePlayerMoveState()
        {
            final EventUpdateInput.Post event = new EventUpdateInput.Post(this);
            EventBus.dispatch(event);
            if (sneak && event.isModifySneaking())
            {
                moveStrafe *= 0.3f;
                moveForward *= 0.3f;
            }
        }
    }
}

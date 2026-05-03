package us.nebula.client.api.movement;

import net.minecraft.client.Minecraft;
import net.minecraft.src.BlockPos;
import net.minecraft.util.MovementInput;
import net.minecraft.util.Vec3;
import us.nebula.client.api.listener.EventBus;
import us.nebula.client.api.movement.pathfinding.Pathfinder;
import us.nebula.client.impl.event.player.EventSneakSlowdown;

import java.util.List;

public final class MovementController
{
    private static final Minecraft MC = Minecraft.getMinecraft();

    private final Pathfinder pathfinder = new Pathfinder();
    private CustomInput input;
    private MovementInput movementInput;

    public List<BlockPos> getPathTo(final BlockPos origin, final BlockPos pos)
    {
        return pathfinder.pathfindTo(origin, pos);
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
        return input.sneak;
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
        return input.jump;
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

        return new float[] { (float) forward, (float) strafe };
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
            if (sneak && !EventBus.dispatch(new EventSneakSlowdown(this)))
            {
                moveStrafe *= 0.3f;
                moveForward *= 0.3f;
            }
        }
    }
}

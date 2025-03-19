package us.nebula.util.player;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import us.nebula.impl.event.player.EventMove;

/**
 * @author xgraza
 * @since 03/17/25
 */
public final class MoveUtil
{
    private static final Minecraft MC = Minecraft.getMinecraft();

    private static final double[] NULL_VELOCITY = new double[2];
    private static final double NCP_BASE_SPEED = 0.2873f;

    public static double getPlayerMoveDistance()
    {
        final double deltaX = MC.thePlayer.posX - MC.thePlayer.prevPosX;
        final double deltaZ = MC.thePlayer.posZ - MC.thePlayer.prevPosZ;
        return Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
    }

    public static double getPlayerSpeed()
    {
        return Math.sqrt(MC.thePlayer.motionX * MC.thePlayer.motionX
                + MC.thePlayer.motionZ * MC.thePlayer.motionZ);
    }

    public static void setSpeed(final EventMove event, final double moveSpeed)
    {
        final double[] strafe = getStrafeMotion(moveSpeed);
        if (event != null)
        {
            event.setX(strafe[0]);
            event.setZ(strafe[1]);
        } else
        {
            MC.thePlayer.motionX = strafe[0];
            MC.thePlayer.motionZ = strafe[1];
        }
    }

    public static double[] getStrafeMotion(final double moveSpeed)
    {
        if (moveSpeed <= 0.0)
        {
            return NULL_VELOCITY;
        }
        final float radians = getDirectionYaw(MC.thePlayer, MC.thePlayer.rotationYaw);
        return new double[] { -Math.sin(radians) * moveSpeed, Math.cos(radians) * moveSpeed };
    }

    public static float getDirectionYaw(final EntityPlayer player, float yaw)
    {
        if (player.moveForward < 0.0f)
        {
            yaw += 180.0f;
        }

        float forward = 1.0f;
        if (player.moveForward < 0.0f)
        {
            forward = -0.5f;
        } else if (player.moveForward > 0.0f)
        {
            forward = 0.5f;
        }

        if (player.moveStrafing > 0.0f)
        {
            yaw -= 90.0f * forward;
        }
        if (player.moveStrafing < 0.0f)
        {
            yaw += 90.0f * forward;
        }

        return yaw * 0.017453292f;
    }

    public static double getBaseNcpSpeed(final int minPotionTime)
    {
        double speed = NCP_BASE_SPEED;
        if (MC.thePlayer.isPotionActive(Potion.moveSpeed.id))
        {
            final PotionEffect effect = MC.thePlayer.getActivePotionEffect(Potion.moveSpeed);
            if (effect.getDuration() > minPotionTime)
            {
                speed *= 1.0 + (0.2 * (effect.getAmplifier() + 1));
            }
        }
        if (MC.thePlayer.isPotionActive(Potion.moveSlowdown.id))
        {
            final PotionEffect effect = MC.thePlayer.getActivePotionEffect(Potion.moveSlowdown);
            if (effect.getDuration() > minPotionTime)
            {
                speed /= 1.0 + (0.2 * (effect.getAmplifier() + 1));
            }
        }
        return speed;
    }

    public static double getJumpHeight(double base)
    {
        if (MC.thePlayer.isPotionActive(Potion.jump.id))
        {
            base += (MC.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1.0) * 0.1;
        }
        return base;
    }

    public static boolean isMoving()
    {
        return MC.thePlayer.movementInput.moveForward != 0.0f
                || MC.thePlayer.movementInput.moveStrafe != 0.0f;
    }
}

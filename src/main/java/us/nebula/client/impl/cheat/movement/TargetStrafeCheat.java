package us.nebula.client.impl.cheat.movement;

import net.minecraft.entity.EntityLivingBase;
import us.nebula.client.api.listener.EventListener;
import us.nebula.client.api.listener.IEventPriorities;
import us.nebula.client.api.listener.Subscribe;
import us.nebula.client.api.manager.cheat.Cheat;
import us.nebula.client.api.manager.cheat.CheatCategory;
import us.nebula.client.api.manager.cheat.CheatManifest;
import us.nebula.client.api.value.Setting;
import us.nebula.client.impl.cheat.combat.KillAuraCheat;
import us.nebula.client.impl.event.player.EventMove;
import us.nebula.client.util.player.MoveUtil;

/**
 * @author xgraza
 * @since 3/16/26
 */
@CheatManifest(name = "TargetStrafe",
        description = "Strafes around your KillAura target",
        category = CheatCategory.MOVEMENT)
public final class TargetStrafeCheat extends Cheat
{
    private final Setting<Float> rangeSetting = new Setting<>(
            "Range", 4.2f, 1.0f, 6.0f, 0.1f);
    private final Setting<Double> reductionSetting = new Setting<>(
            "Reduction", 0.0, 0.0, 1.0, 0.05);

    private boolean directional = true;

    @Subscribe(priority = IEventPriorities.LOW)
    private final EventListener<EventMove> moveEventListener = event ->
    {
        if (!KillAuraCheat.INSTANCE.isAttacking() || !SpeedCheat.INSTANCE.isToggled() || !MoveUtil.isMoving())
        {
            return;
        }

        if (MC.thePlayer.isCollidedHorizontally)
        {
            directional = !directional;
        } else
        {
            if (MC.gameSettings.keyBindLeft.pressed)
            {
                directional = true;
            } else if (MC.gameSettings.keyBindRight.pressed)
            {
                directional = false;
            }
        }

        final EntityLivingBase target = KillAuraCheat.INSTANCE.getTarget();
        final double moveSpeed = Math.sqrt(event.getX() * event.getX() + event.getZ() * event.getZ())
                * (1.0 - reductionSetting.getValue());

        double degree = Math.atan2(MC.thePlayer.posZ - target.posZ, MC.thePlayer.posX - target.posX);
        degree += (moveSpeed / MC.thePlayer.getDistanceToEntity(target)) * (directional ? 1 : -1);

        double dist = rangeSetting.getValue() - Math.max(MC.thePlayer.movementInput.moveForward, 0.0);

        double x = target.posX + dist * Math.cos(degree);
        double z = target.posZ + dist * Math.sin(degree);

        float yaw = (float) (Math.toDegrees(Math.atan2(z - MC.thePlayer.posZ, x - MC.thePlayer.posX)) - 90.0);

        double rad = Math.toRadians(yaw);
        event.setX(moveSpeed * -Math.sin(rad));
        event.setZ(moveSpeed * Math.cos(rad));
    };
}

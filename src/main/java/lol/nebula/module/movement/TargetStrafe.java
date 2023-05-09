package lol.nebula.module.movement;

import lol.nebula.Nebula;
import lol.nebula.listener.events.entity.move.EventMove;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.module.combat.KillAura;
import lol.nebula.setting.Setting;
import lol.nebula.util.player.MoveUtils;
import net.minecraft.entity.EntityLivingBase;

/**
 * @author aesthetical
 * @since 05/09/23
 */
public class TargetStrafe extends Module {

    private static TargetStrafe instance;
    private static int direction = 1;

    private static final Setting<Double> range = new Setting<>(2.0, 0.1, 1.0, 5.0, "Range");

    public TargetStrafe() {
        super("Target Strafe", "Strafes around your target", ModuleCategory.MOVEMENT);
        instance = this;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        direction = 1;
    }

    public static void strafe(EventMove event, double moveSpeed) {
        if (!instance.isToggled()) {
            MoveUtils.strafe(event, moveSpeed);
            return;
        }

        KillAura aura = Nebula.getInstance().getModules().get(KillAura.class);
        if (aura == null || !aura.isToggled() || aura.getTarget() == null) {
            MoveUtils.strafe(event, moveSpeed);
            return;
        }

        EntityLivingBase target = aura.getTarget();

        if (mc.thePlayer.isCollidedHorizontally) {
            direction = -direction;
        } else {
            if (mc.gameSettings.keyBindLeft.pressed) {
                direction = 1;
            } else if (mc.gameSettings.keyBindRight.pressed) {
                direction = -1;
            }
        }

        double degree = Math.atan2(mc.thePlayer.posZ - target.posZ, mc.thePlayer.posX - target.posX);
        degree += (moveSpeed / mc.thePlayer.getDistanceToEntity(target)) * (double) direction;

        double dist = range.getValue() - Math.max(mc.thePlayer.movementInput.moveForward, 0.0);

        double x = target.posX + dist * Math.cos(degree);
        double z = target.posZ + dist * Math.sin(degree);

        float yaw = (float) (Math.toDegrees(Math.atan2(z - mc.thePlayer.posZ, x - mc.thePlayer.posX)) - 90.0);

        double rad = Math.toRadians(yaw);
        event.setX(moveSpeed * -Math.sin(rad));
        event.setZ(moveSpeed * Math.cos(rad));
    }

    public static TargetStrafe getInstance() {
        return instance;
    }
}
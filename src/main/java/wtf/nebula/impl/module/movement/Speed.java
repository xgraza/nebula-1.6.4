package wtf.nebula.impl.module.movement;

import me.bush.eventbus.annotation.EventListener;
import org.lwjgl.input.Keyboard;
import wtf.nebula.event.TickEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.impl.module.combat.KillAura;
import wtf.nebula.impl.module.combat.TargetStrafe;
import wtf.nebula.impl.value.Value;
import wtf.nebula.repository.impl.ModuleRepository;
import wtf.nebula.util.MathUtil;
import wtf.nebula.util.world.player.MotionUtil;

public class Speed extends Module {
    public Speed() {
        super("Speed", ModuleCategory.MOVEMENT);
        setBind(Keyboard.KEY_R);
    }

    public final Value<Mode> mode = new Value<>("Mode", Mode.STRAFE);

    private double moveSpeed = 0.0;
    private boolean slow = false;

    @Override
    protected void onDeactivated() {
        super.onDeactivated();

        moveSpeed = 0.0;
    }

    @EventListener
    public void onTick(TickEvent event) {
        if (MotionUtil.isMoving()) {
            if (mc.thePlayer.onGround) {
                mc.thePlayer.motionY = MotionUtil.getJumpHeight();
                moveSpeed = 1.72 * MotionUtil.getBaseNcpSpeed() - 0.01;
                slow = true;
            }

            else {
                if (slow) {
                    moveSpeed -= MotionUtil.getBaseNcpSpeed() * 0.5;
                    slow = false;
                }

                else {
                    moveSpeed = moveSpeed - moveSpeed / 159.0;
                }
            }

            moveSpeed = Math.max(moveSpeed, MotionUtil.getBaseNcpSpeed());

            double[] strafe = null;

            TargetStrafe targetStrafe = ModuleRepository.get().getModule(TargetStrafe.class);
            if (targetStrafe.getState()) {
                KillAura killAura = ModuleRepository.get().getModule(KillAura.class);
                if (killAura.target != null && killAura.target.getDistanceSqToEntity(mc.thePlayer) < targetStrafe.range.getValue() * targetStrafe.range.getValue()) {
                    strafe = MotionUtil.strafe(moveSpeed, MathUtil.calcYawTo(killAura.target));
                }
            }

            if (strafe == null) {
                strafe = MotionUtil.strafe(moveSpeed);
            }

            mc.thePlayer.motionX = strafe[0];
            mc.thePlayer.motionZ = strafe[1];
        }

        else {

            mc.thePlayer.motionX = 0.0;
            mc.thePlayer.motionZ = 0.0;
        }
    }

    public enum Mode {
        STRAFE, ONGROUND, YPORT
    }
}

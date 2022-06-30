package wtf.nebula.impl.module.movement;

import me.bush.eventbus.annotation.EventListener;
import org.lwjgl.input.Keyboard;
import wtf.nebula.event.MotionUpdateEvent;
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

    private double lastTickMoveSpeed = 0.0;
    private double moveSpeed = 0.0;
    private int stage = 4;
    private boolean slow = false;

    private int limited = 0;

    @Override
    protected void onDeactivated() {
        super.onDeactivated();

        moveSpeed = 0.0;
        mc.timer.timerSpeed = 1.0f;
        stage = 4;
    }

    @EventListener
    public void onTick(TickEvent event) {
        if (mode.getValue().equals(Mode.STRAFE)) {

            if (mc.thePlayer.onGround && MotionUtil.isMoving()) {
                stage = 2;
            }

            if (stage < 2) {
                mc.timer.timerSpeed = 1.0f;
            }

            else {

                if (slow) {
                    mc.timer.timerSpeed = 1.11f;
                } else {
                    mc.timer.timerSpeed = 1.0f;
                }
            }

            if (stage == 1) {
                stage = 2;
                moveSpeed = 1.38 * MotionUtil.getBaseNcpSpeed() - 0.01;
            }

            else if (stage == 2) {
                stage = 3;

                if (mc.thePlayer.onGround && MotionUtil.isMoving()) {
                    mc.thePlayer.motionY = MotionUtil.getJumpHeight();
                    moveSpeed *= slow ? 1.36 : 1.602;
                }
            }

            else if (stage == 3) {
                stage = 4;

                double adjusted = 0.86 * (lastTickMoveSpeed - MotionUtil.getBaseNcpSpeed());
                moveSpeed = lastTickMoveSpeed - adjusted;

                slow = !slow;
            }

            else {
                //List boxes = mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.boundingBox.offset(0.0, mc.thePlayer.motionY, 0.0));
                if (/* (!boxes.isEmpty() || */ mc.thePlayer.isCollidedVertically && stage > 1) {
                    stage = 1;
                }

                moveSpeed = moveSpeed - moveSpeed / 159.0;
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

            if (MotionUtil.isMoving()) {

                mc.thePlayer.motionX = strafe[0];
                mc.thePlayer.motionZ = strafe[1];
            }

            else {

                mc.thePlayer.motionX = 0.0;
                mc.thePlayer.motionZ = 0.0;
            }
        }
    }

    @EventListener
    public void onMotionUpdate(MotionUpdateEvent event) {
        double diffX = mc.thePlayer.posX - mc.thePlayer.lastTickPosX;
        double diffZ = mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ;

        lastTickMoveSpeed = Math.sqrt(diffX * diffX + diffZ * diffZ);
    }

    public enum Mode {
        STRAFE, ONGROUND, YPORT
    }
}

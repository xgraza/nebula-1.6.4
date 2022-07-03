package wtf.nebula.impl.module.movement;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.src.Packet10Flying;
import net.minecraft.src.Packet13PlayerLookMove;
import wtf.nebula.event.MotionUpdateEvent;
import wtf.nebula.event.PacketEvent;
import wtf.nebula.event.PacketEvent.Era;
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
    }

    public final Value<Mode> mode = new Value<>("Mode", Mode.STRAFE);
    public final Value<Boolean> timer = new Value<>("Timer", true);

    private double lastTickMoveSpeed = 0.0;
    private double moveSpeed = 0.0;
    private int stage = 4;
    private boolean slow = false;

    private int lagTicks = 0;

    private int timerTicks = 0;
    private int limiter = 0;

    @Override
    protected void onDeactivated() {
        super.onDeactivated();

        moveSpeed = 0.0;
        mc.timer.timerSpeed = 1.0f;
        stage = 4;
        lagTicks = 0;
        timerTicks = 0;
        limiter = 0;
    }

    @EventListener
    public void onTick(TickEvent event) {
        if (mode.getValue().equals(Mode.STRAFE)) {

            --lagTicks;
            if (lagTicks > 0) {
                return;
            }

            if (!timer.getValue()) {
                mc.timer.timerSpeed = 1.0f;
            }

            if (mc.thePlayer.onGround && MotionUtil.isMoving()) {
                stage = 2;
            }

            if (stage == 1) {
                stage = 2;
                moveSpeed = 1.35 * MotionUtil.getBaseNcpSpeed() - 0.01;
            }

            else if (stage == 2) {
                stage = 3;

                mc.timer.timerSpeed = 1.0f;

                if (mc.thePlayer.onGround && MotionUtil.isMoving()) {
                    mc.thePlayer.motionY = MotionUtil.getJumpHeight();

                    if (timer.getValue()) {
                        moveSpeed *= slow ? 1.3605 : 1.41;
                    }

                    else {
                        moveSpeed *= slow ? 1.3605 : 1.705;
                    }
                }
            }

            else if (stage == 3) {
                if (timer.getValue()) {
                    mc.timer.timerSpeed = 1.088f;
                }

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

                moveSpeed = moveSpeed - moveSpeed / 139.0;
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

        else if (mode.getValue().equals(Mode.ONGROUND)) {
            --lagTicks;
            if (lagTicks > 0) {
                return;
            }

            mc.thePlayer.motionX *= 1.59000003337860;
            mc.thePlayer.motionZ *= 1.59000003337860;
        }

        else if (mode.getValue().equals(Mode.YPORT)) {
            --lagTicks;
            if (lagTicks > 0) {
                return;
            }

            // idk why yport bypasses better without sprinting but hey...
            mc.thePlayer.setSprinting(false);

            moveSpeed = MotionUtil.getBaseNcpSpeed();

            if (mc.thePlayer.onGround) {
                mc.thePlayer.jump();
                moveSpeed *= slow ? 1.12 : 1.31;
            }

            else {
                slow = !slow;
                mc.thePlayer.motionY = -1.0;
            }

            double[] strafe = MotionUtil.strafe(moveSpeed);

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

    @EventListener
    public void onPacketSend(PacketEvent.Send event) {

        if (event.getPacket() instanceof Packet10Flying && event.getEra().equals(Era.PRE)) {

            Packet10Flying packet = event.getPacket();

            if (!mode.getValue().equals(Mode.ONGROUND)) {
                return;
            }

            if (stage == 4) {
                packet.onGround = true;
            }

            else {
                packet.yPosition += 0.198;
                packet.stance += 0.198;
                packet.onGround = false;
            }
        }
    }

    @EventListener
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof Packet13PlayerLookMove) {
            moveSpeed = MotionUtil.getBaseNcpSpeed();
            stage = 4;
            lastTickMoveSpeed = 0.0;

            mc.timer.timerSpeed = 1.0f;
            lagTicks = 13;
        }
    }

    public enum Mode {
        STRAFE, ONGROUND, YPORT
    }
}

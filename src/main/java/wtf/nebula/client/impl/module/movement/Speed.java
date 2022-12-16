package wtf.nebula.client.impl.module.movement;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.impl.event.base.Era;
import wtf.nebula.client.impl.event.impl.client.EventTick;
import wtf.nebula.client.impl.event.impl.move.EventMotion;
import wtf.nebula.client.impl.event.impl.move.EventMotionUpdate;
import wtf.nebula.client.impl.event.impl.network.EventPacket;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;
import wtf.nebula.client.utils.player.MoveUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class Speed extends ToggleableModule {
    private final Property<Mode> mode = new Property<>(Mode.STRAFE, "Mode", "m", "type");
    private final Property<Boolean> useTimer = new Property<>(true, "Use Timer", "timer", "timerboost")
            .setVisibility(() -> !mode.getValue().equals(Mode.GROUNDED));
    private final Property<Boolean> strafeBoost = new Property<>(false, "Strafe Boost", "strafeboost", "damageboost")
            .setVisibility(() -> !mode.getValue().equals(Mode.GROUNDED));

    private int stage = 4;
    private double moveSpeed = 0.0;
    private double distance = 0.0;
    private boolean boost = false;

    private double speedBoost = 0.0;
    private int boostTicks = 0;

    private int lagTime = 0;

    private int onGroundPhase = 2;

    public Speed() {
        super("Speed", new String[]{"speed", "vroom"}, ModuleCategory.MOVEMENT);
        offerProperties(mode, useTimer, strafeBoost);
    }

    @Override
    public String getTag() {
        return mode.getFixedValue();
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        rubberband();
        lagTime = 0;

        speedBoost = 0.0;
        boostTicks = 0;
        onGroundPhase = 2;
    }

    @EventListener
    public void onMotion(EventMotion event) {
        if (--lagTime > 0) {
            return;
        }

        if (mode.getValue().equals(Mode.STRAFE) /*|| mode.getValue().equals(Mode.LOWHOP) */) {

            if (!MoveUtils.isMoving()) {
                return;
            }

            if (useTimer.getValue()) {
                mc.timer.timerSpeed = boost ? 1.088f : 1.094f;
            } else {
                mc.timer.timerSpeed = 1.0f;
            }

//            if (mode.getValue().equals(Mode.LOWHOP)) {
//
//                if (!mc.thePlayer.isCollidedHorizontally) {
//
//                    double diff = mc.thePlayer.boundingBox.minY - (int) mc.thePlayer.boundingBox.minY;
//
//                    if (round(diff, 3) == round(0.4, 3)) {
//                        event.y = mc.thePlayer.motionY = MoveUtils.getJumpHeight(0.31);
//                    } else if (round(diff, 3) == round(0.71, 3)) {
//                        event.y = mc.thePlayer.motionY = MoveUtils.getJumpHeight(0.04);
//                    } else if (round(diff, 3) == round(0.75, 3)) {
//                        event.y = mc.thePlayer.motionY = MoveUtils.getJumpHeight(-0.2);
//                    } else if (round(diff, 3) == round(0.55, 3)) {
//                        event.y = mc.thePlayer.motionY = MoveUtils.getJumpHeight(-0.14);
//                    } else if (round(diff, 3) == round(0.41, 3)) {
//                        event.y = mc.thePlayer.motionY = MoveUtils.getJumpHeight(-0.2);
//                    }
//                }
//            }

            if (stage == 1 && MoveUtils.isMoving()) {
                moveSpeed = 1.35 * MoveUtils.getBaseNcpSpeed() - 0.01;
            } else if (stage == 2 && MoveUtils.isMoving()) {
                event.y = mc.thePlayer.motionY = MoveUtils.getJumpHeight(true);

                //if (mode.getValue().equals(Mode.STRAFE)) {
                    moveSpeed *= boost ? 1.6835 : 1.395;
                //}
//                else if (mode.getValue().equals(Mode.LOWHOP)) {
//                    moveSpeed *= boost ? 1.5685 : 1.3445;
//                }
            } else if (stage == 3) {
                moveSpeed = distance - 0.66 * (distance - MoveUtils.getBaseNcpSpeed());
                boost = !boost;
            } else {
                List boxes = mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.boundingBox.copy().offset(0.0, mc.thePlayer.motionY, 0.0));
                if ((boxes.size() > 0 || mc.thePlayer.isCollidedVertically) && stage > 0) {
                    stage = MoveUtils.isMoving() ? 1 : 0;
                }

                moveSpeed = distance - distance / 159.0;
            }

            moveSpeed = Math.max(moveSpeed, MoveUtils.getBaseNcpSpeed());

            double[] strafe = MoveUtils.calcStrafe(moveSpeed);

            event.x = strafe[0];
            event.z = strafe[1];

            if (MoveUtils.isMoving()) {
                ++stage;
            }

        } else if (mode.getValue().equals(Mode.YPORT)) {

            double[] strafing = TargetStrafe.calcStrafe(moveSpeed);

            event.x = strafing[0];
            event.z = strafing[1];
        } else if (mode.getValue().equals(Mode.GROUNDED)) {

            if (TargetStrafe.isMoving()) {

                moveSpeed = MoveUtils.getBaseNcpSpeed() - 0.01;
                if (!mc.thePlayer.onGround) {
                    moveSpeed = MoveUtils.getBaseNcpSpeed() - 0.01;
                }

                if (mc.thePlayer.isSneaking()) {
                    return;
                }

                double[] strafing = TargetStrafe.calcStrafe(moveSpeed);

                event.x = strafing[0];
                event.z = strafing[1];
            } else {
                event.x = 0.0;
                event.z = 0.0;
            }
        } else if (mode.getValue().equals(Mode.ON_GROUND)) {

            if (mc.thePlayer.onGround || onGroundPhase == 3) {

                if (!(mc.thePlayer.isCollidedHorizontally && mc.thePlayer.moveForward != 0.0f) || mc.thePlayer.moveStrafing != 0.0f) {

                    if (onGroundPhase == 2) {
                        moveSpeed *= 2.149;
                        onGroundPhase = 3;
                    } else if (onGroundPhase == 3) {
                        onGroundPhase = 2;
                        moveSpeed = distance - 0.76 * (distance - MoveUtils.getBaseNcpSpeed());
                    } else {

                        if (mc.thePlayer.isCollidedVertically) {
                            onGroundPhase = 1;
                        }
                    }
                }

                moveSpeed = Math.max(moveSpeed, MoveUtils.getBaseNcpSpeed());

                double[] strafe = MoveUtils.calcStrafe(moveSpeed);

                event.x = strafe[0];
                event.z = strafe[1];
            }

        }
    }

    @EventListener
    public void onTick(EventTick event) {
        if (mode.getValue().equals(Mode.YPORT)) {

            if (TargetStrafe.isMoving()) {

                mc.thePlayer.setSprinting(true);

                moveSpeed = 1.25 * MoveUtils.getBaseNcpSpeed() - 0.1;

                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump();

                    if (useTimer.getValue()) {
                        if (mc.thePlayer.ticksExisted % 10 == 0) {
                            mc.timer.timerSpeed = 1.35f;
                        } else {
                            mc.timer.timerSpeed = boost ? 1.088f : 1.098f;
                        }

                        moveSpeed *= boost ? 1.62 : 1.526;

                    } else {
                        mc.timer.timerSpeed = 1.0f;
                        moveSpeed *= boost ? 1.622 : 1.545;
                    }
                } else {
                    boost = !boost;
                    mc.thePlayer.motionY = -4.0;
                    mc.timer.timerSpeed = 1.0f;
                }
            }
        }
    }

    @EventListener
    public void onMotionUpdate(EventMotionUpdate event) {

        if (event.getEra().equals(Era.PRE)) {

            if (mode.getValue().equals(Mode.ON_GROUND)) {

                if (onGroundPhase == 3) {
                    event.y += 0.3995;
                    event.stance += 0.418;
                    event.onGround = false;
                }

            }

            double diffX = mc.thePlayer.posX - mc.thePlayer.prevPosX;
            double diffZ = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;

            distance = Math.sqrt(diffX * diffX + diffZ * diffZ);

        }
    }

    @EventListener(recieveCancelled = true)
    public void onPacket(EventPacket event) {
        if (event.getPacket() instanceof S08PacketPlayerPosLook && !event.isCancelled()) {
            rubberband();
            lagTime = 20;
            onGroundPhase = 2;
        }

        if (strafeBoost.getValue() && mode.getValue().equals(Mode.STRAFE)) {

            if (event.getPacket() instanceof S27PacketExplosion) {
                S27PacketExplosion packet = event.getPacket();

                if (mc.thePlayer.getDistanceSq(packet.getX(), packet.getY(), packet.getZ()) > 6.0 * 6.0) {
                    return;
                }

                double boostX = packet.getMotionX() / 8000.0;
                double boostZ = packet.getMotionZ() / 8000.0;

                speedBoost = Math.sqrt(boostX * boostX + boostZ * boostZ);
                boostTicks = 3;
            } else if (event.getPacket() instanceof S12PacketEntityVelocity) {

            }
        }
    }

//    public static double round(double in, int dp) {
//        if (dp < 0) {
//            throw new IllegalArgumentException();
//        }
//        BigDecimal bigDecimal = new BigDecimal(in);
//        bigDecimal = bigDecimal.setScale(dp, RoundingMode.HALF_UP);
//        return bigDecimal.doubleValue();
//    }

    private void rubberband() {
        stage = 4;
        moveSpeed = 0.0;
        distance = 0.0;
        boost = false;
        mc.timer.timerSpeed = 1.0f;
    }

    public enum Mode {
        STRAFE, /* LOWHOP, */ GROUNDED, YPORT, ON_GROUND
    }
}

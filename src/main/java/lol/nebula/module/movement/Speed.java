package lol.nebula.module.movement;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.entity.move.EventMove;
import lol.nebula.listener.events.entity.EventUpdate;
import lol.nebula.listener.events.entity.move.EventWalkingUpdate;
import lol.nebula.listener.events.net.EventPacket;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.setting.Setting;
import lol.nebula.util.math.timing.Timer;
import lol.nebula.util.player.MoveUtils;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

/**
 * @author aesthetical
 * @since 04/28/23
 */
public class Speed extends Module {
    private final Setting<Mode> mode = new Setting<>(Mode.HOP, "Mode");
    private final Setting<Boolean> damageBoost = new Setting<>(true, "Damage Boost");

    private double distance, speed;
    private int stage;
    private boolean boost;

    private final Timer damageBoostTimer = new Timer();
    private int damageBoostTicks;

    public Speed() {
        super("Speed", "oooah she a trackstar", ModuleCategory.MOVEMENT);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        stage = 4;
        distance = 0.0;
        speed = 0.0;
        boost = false;
        damageBoostTicks = 0;

        mc.timer.timerSpeed = 1.0f;
    }

    @Override
    public String getMetadata() {
        return (damageBoostTicks > 0 ? "Boosted " : "") + Setting.formatEnumName(mode.getValue());
    }

    @Listener
    public void onUpdate(EventUpdate event) {
        if (mode.getValue() == Mode.PORT && MoveUtils.isMoving()) {

            mc.thePlayer.setSprinting(true);

            speed = 1.25 * MoveUtils.getBaseNcpSpeed(0) - 0.1;

            if (mc.thePlayer.onGround) {
                mc.thePlayer.jump();

                if (mc.thePlayer.ticksExisted % 10 == 0) {
                    mc.timer.timerSpeed = 1.35f;
                } else {
                    mc.timer.timerSpeed = boost ? 1.088f : 1.098f;
                }

                speed *= boost ? 1.62 : 1.526;
            } else {
                boost = !boost;
                mc.thePlayer.motionY = -4.0;
                mc.timer.timerSpeed = 1.0f;
            }
        }
    }

    @Listener
    public void onMove(EventMove event) {
        if (mode.getValue() == Mode.HOP) {

            mc.timer.timerSpeed = 1.088f;

            if (mc.thePlayer.onGround && MoveUtils.isMoving()) {
                stage = 2;
            }

            if (stage == 1) {
                speed = 1.38 * MoveUtils.getBaseNcpSpeed(20) - 0.01;
                stage = 2;
            } else if (stage == 2) {

                if (mc.thePlayer.onGround) {
                    mc.timer.timerSpeed = 1.0f;
                    mc.thePlayer.motionY = MoveUtils.getJumpHeight(0.3995);
                    event.setY(mc.thePlayer.motionY);
                    speed *= boost ? 1.624 : 1.543;
                }

                stage = 3;
            } else if (stage == 3) {
                double adjust = (boost ? 0.72 : 0.66) * (distance - MoveUtils.getBaseNcpSpeed(20));
                speed = distance - adjust;

                boost = !boost;
                stage = 4;
            } else {
                if (damageBoostTicks > 0) {
                    speed -= speed / 100.0;
                } else {
                    speed -= speed / 159.0;
                }
            }

            speed = Math.max(speed, MoveUtils.getBaseNcpSpeed(20));
            if (damageBoostTicks-- > 0) {
                speed = 1.99 * MoveUtils.getBaseNcpSpeed(20);
            }

            if (MoveUtils.isMoving()) {
                MoveUtils.strafe(event, speed);
            }
        } else if (mode.getValue() == Mode.PORT) {
            if (MoveUtils.isMoving()) {
                MoveUtils.strafe(event, speed);
            }
        }
    }

    @Listener
    public void onWalkingUpdate(EventWalkingUpdate event) {

        // calculate move speed
        double x = mc.thePlayer.posX - mc.thePlayer.prevPosX;
        double z = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
        distance = Math.sqrt(x * x + z * z);
    }

    @Listener(receiveCanceled = true)
    public void onPacketInbound(EventPacket.Inbound event) {
        if (!damageBoostTimer.ticks(40, false)) return;

        if (event.getPacket() instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity packet = event.getPacket();
            if (damageBoost.getValue() && packet.func_149412_c() == mc.thePlayer.getEntityId()) {
                damageBoostTimer.resetTime();
                damageBoostTicks = 10;
            }
        } else if (event.getPacket() instanceof S27PacketExplosion) {
            S27PacketExplosion packet = event.getPacket();

            double dist = mc.thePlayer.getDistanceSq(packet.func_149148_f(), packet.func_149143_g(), packet.func_149145_h());
            if (damageBoost.getValue() && dist < 6 * 6) {
                damageBoostTimer.resetTime();
                damageBoostTicks = 10;
            }
        }
    }

    public enum Mode {
        HOP, PORT
    }
}

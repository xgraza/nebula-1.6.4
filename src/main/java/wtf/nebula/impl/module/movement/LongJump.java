package wtf.nebula.impl.module.movement;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.src.Packet11PlayerPosition;
import net.minecraft.src.Packet13PlayerLookMove;
import org.lwjgl.input.Keyboard;
import wtf.nebula.event.PacketEvent;
import wtf.nebula.event.TickEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.impl.value.Value;
import wtf.nebula.repository.impl.ModuleRepository;
import wtf.nebula.util.world.player.MotionUtil;
import wtf.nebula.util.world.player.PlayerUtil;

// Credits: Direkt longjump
// This longjump is used in Future client, but it's slightly modified
public class LongJump extends Module {
    private final double[] AIR_SPEED = {
            0.420606D, 0.417924D, 0.415258D, 0.412609D, 0.409977D, 0.407361D, 0.404761D, 0.402178D, 0.399611D, 0.39706D,
            0.394525D, 0.392D, 0.3894D, 0.38644D, 0.383655D, 0.381105D, 0.37867D, 0.37625D, 0.37384D, 0.37145D,
            0.369D, 0.3666D, 0.3642D, 0.3618D, 0.35945D, 0.357D, 0.354D, 0.351D, 0.348D, 0.345D,
            0.342D, 0.339D, 0.336D, 0.333D, 0.33D, 0.327D, 0.324D, 0.321D, 0.318D, 0.315D,
            0.312D, 0.309D, 0.307D, 0.305D, 0.303D, 0.3D, 0.297D, 0.295D, 0.293D, 0.291D,
            0.289D, 0.287D, 0.285D, 0.283D, 0.281D, 0.279D, 0.277D, 0.275D, 0.273D, 0.271D,
            0.269D, 0.267D, 0.265D, 0.263D, 0.261D, 0.259D, 0.257D, 0.255D, 0.253D, 0.251D,
            0.249D, 0.247D, 0.245D, 0.243D, 0.241D, 0.239D, 0.237D };

    public LongJump() {
        super("LongJump", ModuleCategory.MOVEMENT);
    }

    public final Value<Boolean> glide = new Value<>("Glide", false);

    private int airTicks;
    private int groundTicks;

    @Override
    protected void onDeactivated() {
        super.onDeactivated();
        mc.timer.timerSpeed = 1.0f;
    }

    @EventListener
    public void onTick(TickEvent event) {
        if (PlayerUtil.isOnLiquid(true)) {
            setState(false);
            return;
        }

        float dir = MotionUtil.getMovement()[2];

        float xDir = (float) Math.cos((dir + 90.0f) * Math.PI / 180.0);
        float zDir = (float) Math.sin((dir + 90.0f) * Math.PI / 180.0);

        if (!mc.thePlayer.isCollidedVertically) {
            airTicks++;

            groundTicks = 0;

            if (mc.thePlayer.motionY == -0.07190068807140403D) {
                mc.thePlayer.motionY *= 0.3499999940395355D;
            }

            else if (mc.thePlayer.motionY == -0.10306193759436909D) {
                mc.thePlayer.motionY *= 0.550000011920929D;
            }

            else if (mc.thePlayer.motionY == -0.13395038817442878D) {
                mc.thePlayer.motionY *= 0.6700000166893005D;
            }

            else if (mc.thePlayer.motionY == -0.16635183030382D) {
                mc.thePlayer.motionY *= 0.6899999976158142D;
            }

            else if (mc.thePlayer.motionY == -0.19088711097794803D) {
                mc.thePlayer.motionY *= 0.7099999785423279D;
            }

            else if (mc.thePlayer.motionY == -0.21121925191528862D) {
                mc.thePlayer.motionY *= 0.20000000298023224D;
            }

            else if (mc.thePlayer.motionY == -0.11979897632390576D) {
                mc.thePlayer.motionY *= 0.9300000071525574D;
            }

            else if (mc.thePlayer.motionY == -0.18758479151225355D) {
                mc.thePlayer.motionY *= 0.7200000286102295D;
            }

            else if (mc.thePlayer.motionY == -0.21075983825251726D) {
                mc.thePlayer.motionY *= 0.7599999904632568D;
            }

            if (mc.thePlayer.motionY < -0.2D && mc.thePlayer.motionY > -0.24D) {
                mc.thePlayer.motionY *= 0.7D;
            }

            if (mc.thePlayer.motionY < -0.25D && mc.thePlayer.motionY > -0.32D) {
                mc.thePlayer.motionY *= 0.8D;
            }

            if (mc.thePlayer.motionY < -0.35D && mc.thePlayer.motionY > -0.8D) {
                mc.thePlayer.motionY *= 0.98D;
            }

            if (mc.thePlayer.motionY < -0.8D && mc.thePlayer.motionY > -1.6D) {
                mc.thePlayer.motionY *= 0.99D;
            }

            if (glide.getValue()) {
                if (!mc.theWorld.getCollidingBlockBounds(mc.thePlayer.boundingBox.instantCopy().offset(0, -0.4, 0)).isEmpty() &&
                        !mc.theWorld.getCollidingBlockBounds(mc.thePlayer.boundingBox.instantCopy().offset(0, mc.thePlayer.motionY, 0)).isEmpty()) {

                    mc.thePlayer.motionY = -0.001;
                }
            }

            mc.timer.timerSpeed = 0.8f;

            if (Keyboard.isKeyDown(mc.gameSettings.keyBindForward.keyCode)) {
                try {
                    double val = AIR_SPEED[airTicks - 1] * 3.0;

                    mc.thePlayer.motionX = xDir * val;
                    mc.thePlayer.motionZ = zDir * val;
                } catch (IndexOutOfBoundsException ignored) {

                }
            }

            else {
                mc.thePlayer.motionX = 0.0;
                mc.thePlayer.motionZ = 0.0;
            }
        }

        else {

            mc.timer.timerSpeed = 1.0f;

            airTicks = 0;
            groundTicks++;

            mc.thePlayer.motionX /= 13.0;
            mc.thePlayer.motionZ /= 13.0;


            if (groundTicks == 1) {
                sendPacketPos(0.0, 0.0, 0.0, 0.0);
                sendPacketPos(0.0624, 0.0, 0.0, 0.0);
                sendPacketPos(0.0, 0.419, 0.419, 0.0);
                sendPacketPos(0.0624, 0.0, 0.0, 0.0);
                sendPacketPos(0.0, 0.419, 0.419, 0.0);
            }

            if (groundTicks > 2) {
                groundTicks = 0;
                mc.thePlayer.motionX = xDir * 0.3;
                mc.thePlayer.motionZ = zDir * 0.3;
                mc.thePlayer.motionY = 0.42399999499320984;
            }
        }

    }

    @EventListener
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof Packet13PlayerLookMove) {
            setState(false);
        }
    }

    private void sendPacketPos(double x, double y, double stance, double z) {
        mc.thePlayer.sendQueue.addToSendQueueSilent(new Packet11PlayerPosition(
                mc.thePlayer.posX + x,
                mc.thePlayer.boundingBox.minY + stance,
                mc.thePlayer.posY + y,
                mc.thePlayer.posZ + z,
                mc.thePlayer.onGround
        ));
    }
}

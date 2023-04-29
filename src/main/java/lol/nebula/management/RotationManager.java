package lol.nebula.management;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.EventStage;
import lol.nebula.listener.events.entity.EventWalkingUpdate;
import lol.nebula.listener.events.net.EventPacket;
import lol.nebula.util.math.timing.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;

/**
 * @author aesthetical
 * @since 04/27/23
 */
public class RotationManager {

    /**
     * The minecraft game instance
     */
    private static final Minecraft mc = Minecraft.getMinecraft();

    /**
     * How long to keep spoofing rotations
     */
    private static final int TICK_KEEP_TIME = 6;

    /**
     * A timer for how long to keep spoofing rotations to the server
     */
    private final Timer keepRotationsTimer = new Timer();

    /**
     * The client and server rotations
     */
    private float[] server, client;

    public RotationManager() {
        server = new float[] { 0.0f, 0.0f };
    }

    @Listener
    public void onPacketOutbound(EventPacket.Outbound event) {
        if (event.getPacket() instanceof C03PacketPlayer) {
            C03PacketPlayer packet = event.getPacket();
            if (packet.hasLook()) {
                server[0] = packet.getYaw();
                server[1] = packet.getPitch();
            }
        }
    }

    @Listener
    public void onWalkingUpdate(EventWalkingUpdate event) {
        if (client != null) {

            if (keepRotationsTimer.ticks(TICK_KEEP_TIME, false)) {
                client = null;
                return;
            }

            event.setYaw(client[0]);
            event.setPitch(client[1]);

            if (event.getStage() == EventStage.PRE) {
                mc.thePlayer.rotationYawHead = server[0];
                mc.thePlayer.renderYawOffset = server[0];
                mc.thePlayer.rotationPitchHead = server[1];
            }
        }
    }

    /**
     * Sets the client rotations
     * @param rotations the rotation array (len 2)
     */
    public void spoof(float[] rotations) {
        keepRotationsTimer.resetTime();
        client = rotations;
    }

    /**
     * Gets if the client rotations are being spoofed
     * @return if client is spoofing rotations
     */
    public boolean isRotating() {
        return client != null;
    }
}

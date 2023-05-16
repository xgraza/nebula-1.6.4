package lol.nebula.management;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.EventStage;
import lol.nebula.listener.events.entity.move.EventWalkingUpdate;
import lol.nebula.listener.events.net.EventPacket;
import lol.nebula.util.math.timing.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.MathHelper;

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
                func_110146_f();
                mc.thePlayer.clientYaw = server[0];
                mc.thePlayer.clientPitch = server[1];
            }
        }
    }

    private float func_110146_f()
    {
        double var9 = mc.thePlayer.posX - mc.thePlayer.prevPosX;
        double var10 = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
        float diff = (float)(var9 * var9 + var10 * var10);

        float par2 = 0.0f, par1 = mc.thePlayer.renderYawOffset;
        if (diff > 0.0025000002F)
        {
            par2 = (float)Math.sqrt((double) diff) * 3.0F;
            par1 = (float)Math.atan2(var10, var9) * 180.0F / (float)Math.PI - 90.0F;
        }

        float var3 = MathHelper.wrapAngleTo180_float(par1 - mc.thePlayer.renderYawOffset);
        mc.thePlayer.renderYawOffset += var3 * 0.3F;
        float var4 = MathHelper.wrapAngleTo180_float(server[0] - mc.thePlayer.renderYawOffset);
        boolean var5 = var4 < -90.0F || var4 >= 90.0F;

        if (var4 < -75.0F)
        {
            var4 = -75.0F;
        }

        if (var4 >= 75.0F)
        {
            var4 = 75.0F;
        }

        mc.thePlayer.renderYawOffset = server[0] - var4;

        if (var4 * var4 > 2500.0F)
        {
            mc.thePlayer.renderYawOffset += var4 * 0.2F;
        }

        if (var5)
        {
            par2 *= -1.0F;
        }

        return par2;
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

    public float[] getServer() {
        return server;
    }
}

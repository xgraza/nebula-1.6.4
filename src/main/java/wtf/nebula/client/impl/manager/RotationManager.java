package wtf.nebula.client.impl.manager;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.network.play.client.C03PacketPlayer;
import wtf.nebula.client.core.Launcher;
import wtf.nebula.client.impl.event.impl.client.TickEvent;
import wtf.nebula.client.impl.event.impl.move.MotionUpdateEvent;
import wtf.nebula.client.impl.event.impl.network.PacketEvent;
import wtf.nebula.client.impl.event.impl.render.RenderRotationsEvent;
import wtf.nebula.client.utils.client.Timer;
import wtf.nebula.client.utils.client.Wrapper;

public class RotationManager implements Wrapper {
    public float[] rotations;
    public float[] serverRotation = { 0.0f, 0.0f };

    private final Timer timer = new Timer();

    public RotationManager() {
        Launcher.BUS.subscribe(this);
    }

    @EventListener
    public void onMotionUpdate(MotionUpdateEvent event) {
        if (rotations != null) {
            if (timer.hasPassed(450L, false)) {
                rotations = null;
            } else {
                event.yaw = rotations[0];
                event.pitch = rotations[1];

                mc.thePlayer.renderYawOffset = rotations[0];
                mc.thePlayer.rotationYawHead = rotations[0];
            }
        }
    }

    @EventListener
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof C03PacketPlayer) {
            C03PacketPlayer packet = event.getPacket();
            if (packet.rotating) {
                serverRotation[0] = packet.yaw;
                serverRotation[1] = packet.pitch;
            }
        }
    }

    @EventListener
    public void onRenderRotations(RenderRotationsEvent event) {
        if (rotations != null) {
            event.yaw = serverRotation[1];
            event.pitch = serverRotation[1];
            event.setCancelled(true);
        }
    }

    public void setRotations(float[] rotations) {
        this.rotations = rotations;
        timer.resetTime();
    }

    public void resetRotation() {
        rotations = null;
    }
}

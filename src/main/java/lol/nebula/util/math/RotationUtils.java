package lol.nebula.util.math;

import lol.nebula.Nebula;
import lol.nebula.listener.events.entity.EventWalkingUpdate;
import net.minecraft.client.Minecraft;

/**
 * @author aesthetical
 * @since 04/27/23
 */
public class RotationUtils {

    /**
     * The minecraft instance
     */
    protected static final Minecraft mc = Minecraft.getMinecraft();

    /**
     * Sets server-side rotations
     * @param event the event to modify
     * @param rotations the spoofed rotations
     */
    public static void setRotations(EventWalkingUpdate event, float[] rotations) {
        Nebula.getInstance().getRotations().spoof(rotations);
        event.setYaw(rotations[0]);
        event.setPitch(rotations[1]);
    }
}

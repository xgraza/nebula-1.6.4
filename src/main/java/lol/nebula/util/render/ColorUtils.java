package lol.nebula.util.render;

import java.awt.*;

/**
 * @author aesthetical
 * @since 04/27/23
 */
public class ColorUtils {

    /**
     * Creates a rainbow cycle
     * @param delay the delay from the initial current time
     * @param speed the speed to cycle through the rainbow
     * @return the hex color code for the rainbow cycle
     */
    public static int rainbowCycle(int delay, double speed) {
        double hue = (((System.currentTimeMillis() + 10) + delay) * (speed / 100.0)) % 360.0;
        return Color.HSBtoRGB((float) hue / 360.0f, 1.0f, 1.0f);
    }
}

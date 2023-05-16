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

    /**
     * Changes the alpha bit in a hex color
     * @param color the color with an alpha value
     * @param alpha the new alpha value
     * @return the new color
     */
    public static int withAlpha(int color, int alpha) {
        return (color & 0x00ffffff) | (alpha << 24);
    }
}

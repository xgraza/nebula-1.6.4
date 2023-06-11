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
     * Creates a gradient rainbow
     * @param color the color
     * @param minBrightness the minimum brightness value
     * @param delay the delay between colors
     * @return the hex color code for this rainbow cycle
     */
    public static int gradientRainbow(Color color, float minBrightness, int delay) {
        float[] hsb = Color.RGBtoHSB(
                color.getRed(), color.getGreen(), color.getBlue(), null);
        float brightness = Math.abs(
                ((float) (System.currentTimeMillis() % 2000L)
                        / 1000.0f + 45.0F / (delay + 100.0f) * 2.0f) % 2.0f - 1.0f);
        brightness = ((minBrightness + (1.0f - minBrightness) * brightness)) % 2.0f;
        return Color.getHSBColor(hsb[0], hsb[1], brightness).getRGB();
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

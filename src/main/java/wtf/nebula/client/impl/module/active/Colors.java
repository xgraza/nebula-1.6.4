package wtf.nebula.client.impl.module.active;

import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.impl.module.Module;

import java.awt.*;

public class Colors extends Module {
    private static final Property<Float> hue = new Property<>(0.0f, 0.0f, 360.0f, "Hue", "h");
    private static final Property<Float> saturation = new Property<>(100.0f, 0.0f, 100.0f, "Saturation", "sat");
    private static final Property<Float> brightness = new Property<>(100.0f, 0.0f, 100.0f, "Brightness", "lightness", "light", "b");
    public static final Property<Boolean> rainbow = new Property<>(false, "Rainbow");

    public Colors() {
        super("Colors", new String[]{"colours", "clientcolors", "theme"});
        offerProperties(hue, saturation, brightness, rainbow);
    }

    public static int getClientColor() {
        return getClientColorAwt().getRGB();
    }

    public static int getClientColor(int alpha) {
        Color c = getClientColorAwt();
        return new Color(c.getRed(), c.getBlue(), c.getGreen(), alpha).getRGB();
    }

    public static int getColorInt() {
        return getColor().getRGB();
    }

    public static Color getColor() {
        Color c = getClientColorAwt();
        return new Color(c.getRed(), c.getBlue(), c.getGreen(), 255);
    }

    private static Color getClientColorAwt() {
        return Color.getHSBColor(hue.getValue() / 360.0f, saturation.getValue() / 100.0f, brightness.getValue() / 100.0f);
    }

    public static int getClientRainbow(int delay) {
        Color c = getClientRainbowAwt(delay);
        return new Color(c.getRed(), c.getBlue(), c.getGreen(), 255).getRGB();
    }

    public static Color getClientRainbowAwt(int delay) {
        double state = Math.ceil((System.currentTimeMillis() + delay) / 20.0) % 360;
        return Color.getHSBColor((float) (state / 360.f), saturation.getValue() / 100.0f, brightness.getValue() / 100.0f);
    }
}

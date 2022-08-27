package wtf.nebula.client.impl.module.active;

import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.impl.module.Module;

import java.awt.*;

public class Colors extends Module {
    private static final Property<Float> hue = new Property<>(0.0f, 0.0f, 360.0f, "Hue", "h");
    private static final Property<Float> saturation = new Property<>(100.0f, 0.0f, 100.0f, "Saturation", "sat");
    private static final Property<Float> brightness = new Property<>(100.0f, 0.0f, 100.0f, "Brightness", "lightness", "light", "b");

    public Colors() {
        super("Colors", new String[]{"colours", "clientcolors", "theme"});
        offerProperties(hue, saturation, brightness);
    }

    public static int getClientColor() {
        return getClientColorAwt().getRGB();
    }

    public static int getClientColor(int alpha) {
        Color c = getClientColorAwt();
        return new Color(c.getRed(), c.getBlue(), c.getGreen(), alpha).getRGB();
    }

    private static Color getClientColorAwt() {
        return Color.getHSBColor(
                hue.getValue() / 360.0f,
                saturation.getValue() / 100.0f,
                brightness.getValue() / 100.0f);
    }
}

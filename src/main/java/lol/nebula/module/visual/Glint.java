package lol.nebula.module.visual;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.render.gui.EventRenderGlintColor;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.setting.Setting;

import java.awt.*;

import static lol.nebula.util.render.ColorUtils.withAlpha;

/**
 * @author aesthetical
 * @since 05/16/23
 */
public class Glint extends Module {

    private final Setting<Color> color = new Setting<>(new Color(255, 0, 0), "Color");
    private final Setting<Float> intensity = new Setting<>(1.0f, 0.01f, 0.1f, 1.0f, "Intensity");

    public Glint() {
        super("Glint", "Renders a custom color over glint", ModuleCategory.VISUAL);
    }

    @Listener
    public void onRenderGlintColor(EventRenderGlintColor event) {
        event.setColor(withAlpha(color.getValue().getRGB(), (int) (intensity.getValue() * 255.0f)));
    }
}

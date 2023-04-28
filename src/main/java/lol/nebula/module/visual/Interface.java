package lol.nebula.module.visual;

import lol.nebula.Nebula;
import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.render.EventRender2D;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.setting.Setting;
import lol.nebula.util.render.ColorUtils;

import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author aesthetical
 * @since 04/27/23
 */
public class Interface extends Module {
    public static final Setting<Color> color = new Setting<>(new Color(45, 124, 224), "Color");

    public Interface() {
        super("Interface", "Renders an overlay over the vanilla HUD", ModuleCategory.VISUAL);

        // by default, set on
        setState(true);

        // do not draw to array list by default
        setDrawn(false);
    }

    @Listener
    public void onRender2D(EventRender2D event) {

        // if the F3 debug menu is open, do not render over it
        if (mc.gameSettings.showDebugInfo) return;

        mc.fontRenderer.drawStringWithShadow(Nebula.getName() + " v" + Nebula.getVersion(), 3, 3, -1);

        List<Module> enabled = Nebula.getInstance().getModules().getModules()
                .stream()
                .filter((m) -> m.isDrawn() && (m.isToggled() || m.getAnimation().getFactor() > 0.0))
                .sorted(Comparator.comparingInt((m) -> -mc.fontRenderer.getStringWidth(m.getTag())))
                .collect(Collectors.toList());

        if (!enabled.isEmpty()) {
            double y = 3.0;
            for (int i = 0; i < enabled.size(); ++i) {
                Module module = enabled.get(i);

                double x = event.getRes().getScaledWidth_double()
                        - ((3.0 + mc.fontRenderer.getStringWidth(module.getTag())) * module.getAnimation().getFactor());

                mc.fontRenderer.drawStringWithShadow(module.getTag(), (int) x, (int) y, ColorUtils.rainbowCycle(i * 100, 5.0));

                y += (mc.fontRenderer.FONT_HEIGHT + 2) * module.getAnimation().getFactor();
            }
        }
    }
}

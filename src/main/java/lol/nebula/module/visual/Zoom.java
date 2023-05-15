package lol.nebula.module.visual;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.render.gui.EventOptifineZoom;
import lol.nebula.listener.events.render.world.EventRender3D;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.setting.Setting;
import lol.nebula.util.feature.DevelopmentFeature;
import lol.nebula.util.render.animation.Animation;
import lol.nebula.util.render.animation.Easing;

/**
 * @author aesthetical
 * @since 05/15/23
 */
@DevelopmentFeature
public class Zoom extends Module {
    private final Setting<Double> speed = new Setting<>(3.5, 0.1, 0.0, 5.0, "Speed");
    private final Setting<Float> max = new Setting<>(35.0f, 0.01f, 1.0f, 70.0f ,"Max");

    private final Animation animation = new Animation(Easing.CUBIC_IN_OUT, 200, false);
    private float oldFoV = -1.0f;

    public Zoom() {
        super("Zoom", "Modifies optifine zoom", ModuleCategory.VISUAL);
    }

    @Listener
    public void onRender3D(EventRender3D event) {
        if (speed.getValue() == speed.getMax().doubleValue() || oldFoV == -1.0f) return;

        animation.setAnimationTime((speed.getMax().doubleValue() - speed.getValue()) * 100.0);
        animation.setEasing(Easing.CUBIC_IN_OUT);

        if (animation.getState() != mc.gameSettings.ofKeyBindZoom.pressed)
            animation.setState(mc.gameSettings.ofKeyBindZoom.pressed);

        float factor = (float) animation.getFactor();
        if (animation.getState()) {
            mc.gameSettings.fovSetting *= (1.0f - factor);
        } else {
            float f = oldFoV / 110.0f;

            mc.gameSettings.fovSetting = f * factor;
            if (factor <= 0.0f) {
                mc.gameSettings.fovSetting = oldFoV;
                oldFoV = -1.0f;
            }
        }

        float m = max.getValue() / 70.0f;
        if (mc.gameSettings.fovSetting < m) mc.gameSettings.fovSetting = m;
    }

    @Listener
    public void onOptifineZoom(EventOptifineZoom event) {
        if (speed.getValue() == speed.getMax().doubleValue()) return;

        if (oldFoV == -1.0f) oldFoV = 1.0f;
        event.cancel();
    }
}

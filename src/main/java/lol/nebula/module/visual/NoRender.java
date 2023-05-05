package lol.nebula.module.visual;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.render.gui.overlay.*;
import lol.nebula.listener.events.render.overlay.*;
import lol.nebula.listener.events.render.world.EventWeather;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.setting.Setting;

/**
 * @author aesthetical
 * @since 04/29/23
 */
public class NoRender extends Module {

    private final Setting<Boolean> hurtCamera = new Setting<>(false, "Hurt Camera");
    private final Setting<Boolean> burning = new Setting<>(false, "Burning");
    private final Setting<Boolean> suffocating = new Setting<>(true, "Suffocating");
    private final Setting<Boolean> pumpkin = new Setting<>(false, "Pumpkin");
    private final Setting<Boolean> weather = new Setting<>(true, "Weather");
    private final Setting<Boolean> voidParticles = new Setting<>(true, "Void Particles");
    private final Setting<Boolean> dynamicFoV = new Setting<>(true, "Dynamic FoV");

    public NoRender() {
        super("No Render", "Stops annoying things from rendering", ModuleCategory.VISUAL);
    }

    @Listener
    public void onHurtCamera(EventHurtCamera event) {
        if (hurtCamera.getValue()) event.cancel();
    }

    @Listener
    public void onBurningOverlay(EventBurningOverlay event) {
        if (burning.getValue()) event.cancel();
    }

    @Listener
    public void onSuffocatingOverlay(EventSuffocatingOverlay event) {
        if (suffocating.getValue()) event.cancel();
    }

    @Listener
    public void onPumpkinBlur(EventPumpkinBlur event) {
        if (pumpkin.getValue()) event.cancel();
    }

    @Listener
    public void onWeather(EventWeather event) {
        if (weather.getValue()) {
            event.setRainStrength(0.0f);
            event.setThunderStrength(0.0f);
        }
    }

    @Listener
    public void onVoidParticles(EventVoidParticles event) {
        if (voidParticles.getValue()) event.cancel();
    }

    @Listener
    public void onFoV(EventFoV event) {
        if (dynamicFoV.getValue()) event.cancel();
    }
}

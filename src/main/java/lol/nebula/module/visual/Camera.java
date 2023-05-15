package lol.nebula.module.visual;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.render.world.EventCameraDistance;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.setting.Setting;

/**
 * @author aesthetical
 * @since 05/07/23
 */
public class Camera extends Module {
    private final Setting<Double> clipDistance = new Setting<>(3.0, 0.1, 1.0, 20.0, "Clip Distance");

    public Camera() {
        super("Camera", "Camera tweaks", ModuleCategory.VISUAL);
    }

    @Listener
    public void onCameraDistance(EventCameraDistance event) {
        event.setDistance(clipDistance.getValue());
        event.cancel();
    }
}

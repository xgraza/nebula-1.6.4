package lol.nebula.module.visual;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.entity.EventArmSwingSpeed;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.setting.Setting;

/**
 * @author aesthetical
 * @since 04/30/23
 *
 * @see net.minecraft.client.renderer.ItemRenderer
 */
public class Animations extends Module {

    public final Setting<Animation> animation = new Setting<>(Animation.DEFAULT, "Animation");
    public final Setting<Double> translateX = new Setting<>(0.0, 0.01, -5.0, 5.0, "Translate X");
    public final Setting<Double> translateY = new Setting<>(0.0, 0.01, -5.0, 5.0, "Translate Y");
    public final Setting<Double> translateZ = new Setting<>(0.0, 0.01, -5.0, 5.0, "Translate Z");
    public final Setting<Double> scaleX = new Setting<>(1.0, 0.01, -5.0, 5.0, "Scale X");
    public final Setting<Double> scaleY = new Setting<>(1.0, 0.01, -5.0, 5.0, "Scale Y");
    public final Setting<Double> scaleZ = new Setting<>(1.0, 0.01, -5.0, 5.0, "Scale Z");
    private final Setting<Integer> swingSpeed = new Setting<>(14, 0, 20, "Swing Speed");

    public Animations() {
        super("Animations", "Changes your swing animations", ModuleCategory.VISUAL);
    }

    @Listener
    public void onArmSwingSpeed(EventArmSwingSpeed event) {
        if (event.getEntity().equals(mc.thePlayer)) {
            event.setSwingSpeed(swingSpeed.getMax().intValue() - swingSpeed.getValue());
        }
    }

    public enum Animation {
        DEFAULT, EXHIBITION, AVATAR
    }
}

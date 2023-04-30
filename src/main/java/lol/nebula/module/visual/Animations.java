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

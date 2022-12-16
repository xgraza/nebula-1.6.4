package wtf.nebula.client.impl.module.visuals;

import me.bush.eventbus.annotation.EventListener;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.impl.event.impl.player.EventSwingArm;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;

public class Animations extends ToggleableModule {
    public final Property<Mode> mode = new Property<>(Mode.NONE, "Mode", "m", "type");
    private final Property<Boolean> silentSwing = new Property<>(false, "Silent Swing", "sswing");

    public final Property<Double> scale = new Property<>(1.0, 0.1, 5.0, "Scaling", "scale");
    public final Property<Float> speed = new Property<>(1.0f, 0.1f, 1.0f, "Speed", "s");

    public Animations() {
        super("Animations", new String[]{"swordanimations", "blockanimations"}, ModuleCategory.VISUALS);
        offerProperties(mode, silentSwing, scale, speed);
    }

    @EventListener
    public void onSwingEvent(EventSwingArm event) {
        if (event.getPlayer().equals(mc.thePlayer) && silentSwing.getValue()) {
            event.setCancelled(true);
            mc.thePlayer.swingItemSilent();
        }
    }

    public enum Mode {
        NONE, TAP, JIGSAW, AVATAR, SIGMA, NEBULA, STELLA, EXHIBITION, FATHUM
    }
}

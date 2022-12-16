package wtf.nebula.client.impl.module.movement;

import me.bush.eventbus.annotation.EventListener;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.impl.event.impl.client.EventTick;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;

public class Sprint extends ToggleableModule {
    private final Property<Mode> mode = new Property<>(Mode.LEGIT, "Mode", "m", "type");

    public Sprint() {
        super("Sprint", new String[]{"sprint", "autosprint", "keepsprint"}, ModuleCategory.MOVEMENT);
        offerProperties(mode);
    }

    @Override
    public String getTag() {
        return mode.getFixedValue();
    }

    @EventListener
    public void onTick(EventTick event) {
        if (!mc.thePlayer.isSprinting()) {
            switch (mode.getValue()) {
                case LEGIT: {
                    mc.thePlayer.setSprinting(
                            !mc.thePlayer.isSneaking() &&
                                    !mc.thePlayer.isUsingItem() &&
                                    !mc.thePlayer.isCollidedHorizontally &&
                                    mc.thePlayer.getFoodStats().getFoodLevel() > 6 &&
                                    mc.thePlayer.movementInput.moveForward > 0.0f);
                    break;
                }

                case RAGE: {
                    mc.thePlayer.setSprinting(true);
                    break;
                }
            }
        }
    }

    public enum Mode {
        LEGIT, RAGE
    }
}

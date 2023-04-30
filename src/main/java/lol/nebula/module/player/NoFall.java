package lol.nebula.module.player;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.entity.EventWalkingUpdate;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.setting.Setting;

import static net.minecraft.client.resources.I18n.format;

/**
 * @author aesthetical
 * @since 04/29/23
 */
public class NoFall extends Module {
    private final Setting<Mode> mode = new Setting<>(Mode.SPOOF, "Mode");
    private final Setting<Float> distance = new Setting<>(3.0f, 0.01f, 3.0f, 40.0f, "Distance");

    public NoFall() {
        super("No Fall", "Negates fall damage", ModuleCategory.PLAYER);
    }

    @Override
    public String getMetadata() {
        return format("%s %.1f", Setting.formatEnumName(mode.getValue()), distance.getValue());
    }

    @Listener
    public void onWalkingUpdate(EventWalkingUpdate event) {
        if (mc.thePlayer.fallDistance >= distance.getValue()) {

            mc.thePlayer.fallDistance = 0.0f;
            if (mode.getValue() == Mode.SPOOF) {
                event.setOnGround(true);
            } else if (mode.getValue() == Mode.LAG_BACK) {
                event.setY(event.getY() + 10.0);
                event.setStance(event.getStance() + 10.0);
            }
        }
    }

    public enum Mode {
        SPOOF, LAG_BACK
    }
}

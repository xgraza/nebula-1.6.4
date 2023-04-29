package lol.nebula.module.player;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.entity.EventWalkingUpdate;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.setting.Setting;

/**
 * @author aesthetical
 * @since 04/29/23
 */
public class NoFall extends Module {
    private final Setting<Float> distance = new Setting<>(3.0f, 0.01f, 3.0f, 40.0f, "Distance");

    public NoFall() {
        super("No Fall", "Negates fall damage", ModuleCategory.PLAYER);
    }

    @Listener
    public void onWalkingUpdate(EventWalkingUpdate event) {
        if (mc.thePlayer.fallDistance >= distance.getValue()) {
            event.setOnGround(true);
            mc.thePlayer.fallDistance = 0.0f;
        }
    }
}

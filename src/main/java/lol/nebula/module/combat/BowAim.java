package lol.nebula.module.combat;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.EventStage;
import lol.nebula.listener.events.entity.move.EventWalkingUpdate;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.setting.Setting;

/**
 * @author aesthetical
 * @since 05/31/23
 */
public class BowAim extends Module {
    private final Setting<Boolean> silent = new Setting<>(false, "Silent");

    public BowAim() {
        super("Bow Aim", "Automatically aims your bow towards a target", ModuleCategory.COMBAT);
    }

    @Listener
    public void onWalkingUpdate(EventWalkingUpdate event) {
        if (event.getStage() != EventStage.PRE) return;
    }
}

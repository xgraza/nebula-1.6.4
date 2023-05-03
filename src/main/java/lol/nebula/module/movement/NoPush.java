package lol.nebula.module.movement;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.entity.EventPushOutOfBlocks;
import lol.nebula.listener.events.entity.EventWaterPush;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.setting.Setting;

/**
 * @author aesthetical
 * @since 05/03/23
 */
public class NoPush extends Module {
    private final Setting<Boolean> water = new Setting<>(true, "Water");
    private final Setting<Boolean> blocks = new Setting<>(true, "Blocks");

    public NoPush() {
        super("No Push", "Prevents the player from getting pushed", ModuleCategory.MOVEMENT);
    }

    @Listener
    public void onWaterPush(EventWaterPush event) {
        if (water.getValue()) event.cancel();
    }

    @Listener
    public void onPushOutOfBlocks(EventPushOutOfBlocks event) {
        if (blocks.getValue()
                && mc.thePlayer != null
                && mc.thePlayer.equals(event.getEntity())) event.cancel();
    }
}

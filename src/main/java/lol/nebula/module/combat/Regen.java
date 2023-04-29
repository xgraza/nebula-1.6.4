package lol.nebula.module.combat;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.entity.EventUpdate;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.setting.Setting;
import net.minecraft.network.play.client.C03PacketPlayer;

/**
 * @author aesthetical
 * @since 04/28/23
 */
public class Regen extends Module {
    private final Setting<Float> health = new Setting<>(16.0f, 0.01f, 1.0f, 19.0f, "Health");

    public Regen() {
        super("Regen", "Regenerates health faster than vanilla", ModuleCategory.COMBAT);
    }

    @Listener
    public void onUpdate(EventUpdate event) {
        if (mc.thePlayer.getHealth() < health.getValue()) {
            for (int i = 0; i < 20; ++i) {
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer());
            }
        }
    }
}

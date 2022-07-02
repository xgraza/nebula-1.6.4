package wtf.nebula.impl.module.misc;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.src.Packet205ClientCommand;
import wtf.nebula.event.TickEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.impl.value.Value;

public class AutoRespawn extends Module {
    public AutoRespawn() {
        super("AutoRespawn", ModuleCategory.MISC);
    }

    @EventListener
    public void onTick(TickEvent event) {
        if (mc.thePlayer.isDead || mc.thePlayer.getHealth() <= 0.0f) {
            mc.thePlayer.sendQueue.addToSendQueue(new Packet205ClientCommand(1));
        }
    }
}

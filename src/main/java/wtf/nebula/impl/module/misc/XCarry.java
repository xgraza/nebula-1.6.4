package wtf.nebula.impl.module.misc;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.src.Packet101CloseWindow;
import wtf.nebula.event.PacketEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;

public class XCarry extends Module {
    public XCarry() {
        super("XCarry", ModuleCategory.MISC);
    }

    @EventListener
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof Packet101CloseWindow) {
            event.setCancelled(true);
        }
    }
}

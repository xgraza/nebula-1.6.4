package wtf.nebula.impl.module.misc;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import wtf.nebula.event.PacketEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;

public class XCarry extends Module {
    public XCarry() {
        super("XCarry", ModuleCategory.MISC);
    }

    @EventListener
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof C0DPacketCloseWindow) {
            event.setCancelled(true);
        }
    }
}

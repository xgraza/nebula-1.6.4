package wtf.nebula.impl.module.world;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import wtf.nebula.event.PacketEvent;
import wtf.nebula.event.PacketEvent.Era;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;

public class ThunderHack extends Module {
    public ThunderHack() {
        super("ThunderHack", ModuleCategory.WORLD);
    }

    @EventListener
    public void onPacketReceive(PacketEvent.Receive event) {

        if (event.getPacket() instanceof S29PacketSoundEffect && event.getEra().equals(Era.PRE)) {
            S29PacketSoundEffect packet = event.getPacket();

            if (packet.func_149212_c().equals("ambient.weather.thunder")) {
                sendChatMessage("Thunder sounded at " + packet.x + ", " + packet.y + ", " + packet.z);
            }
        }
    }
}

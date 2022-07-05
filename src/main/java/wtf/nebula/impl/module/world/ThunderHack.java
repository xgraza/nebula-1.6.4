package wtf.nebula.impl.module.world;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.src.Packet62LevelSound;
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

        if (event.getPacket() instanceof Packet62LevelSound && event.getEra().equals(Era.PRE)) {
            Packet62LevelSound packet = event.getPacket();

            if (packet.getSoundName().equals("ambient.weather.thunder")) {
                sendChatMessage("Thunder sounded at " + packet.getEffectX() + ", " + packet.getEffectY() + ", " + packet.getEffectZ());
            }
        }
    }
}

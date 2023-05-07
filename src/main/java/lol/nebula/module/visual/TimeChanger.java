package lol.nebula.module.visual;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.net.EventPacket;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.setting.Setting;
import net.minecraft.network.play.server.S03PacketTimeUpdate;

/**
 * @author aesthetical
 * @since 05/07/23
 */
public class TimeChanger extends Module {
    private final Setting<Double> hour = new Setting<>(22.0, 0.1, 0.0, 24.0, "Hour");

    public TimeChanger() {
        super("Time Changer", "Changes time client side", ModuleCategory.VISUAL);
    }

    @Listener
    public void onPacketInbound(EventPacket.Inbound event) {
        if (event.getPacket() instanceof S03PacketTimeUpdate) {
            S03PacketTimeUpdate packet = event.getPacket();

            long time = (long) (hour.getValue() * 1000.0);
            packet.setField_149369_a(time);
            packet.setField_149368_b(time);
        }
    }
}

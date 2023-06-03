package lol.nebula.module.player;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.net.EventPacket;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.util.feature.DevelopmentFeature;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;

/**
 * @author aesthetical
 * @since 06/03/23
 */
@DevelopmentFeature
public class InventorySync extends Module {

    public InventorySync() {
        super("Inventory Sync", "Prevents inventory de-synchronization", ModuleCategory.PLAYER);
    }

    @Listener
    public void onPacketInbound(EventPacket.Inbound event) {
        if (event.getPacket() instanceof C0FPacketConfirmTransaction) {
            C0FPacketConfirmTransaction packet = event.getPacket();
            print("C0F: windowId: " + packet.func_149532_c() + ", uid: " + packet.func_149533_d());
        }
    }

    @Listener
    public void onPacketOutbound(EventPacket.Outbound event) {
        if (event.getPacket() instanceof S32PacketConfirmTransaction) {
            S32PacketConfirmTransaction packet = event.getPacket();
            print("S32: windowId: " + packet.func_148889_c() + ", uid: " + packet.func_148890_d() + ", accepted: " + packet.func_148888_e());

        }
    }
}

package wtf.nebula.client.impl.module.miscellaneous;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import wtf.nebula.client.impl.event.impl.client.TickEvent;
import wtf.nebula.client.impl.event.impl.network.PacketEvent;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;

public class XCarry extends ToggleableModule {
    private int windowId = -1;

    public XCarry() {
        super("XCarry", new String[]{"xcarry", "craftingcarry"}, ModuleCategory.MISCELLANEOUS);
    }

    @EventListener
    public void onTick(TickEvent event) {
        if (mc.currentScreen instanceof GuiInventory) {
            windowId = mc.thePlayer.openContainer.windowId;
        }
    }

    @EventListener
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof C0DPacketCloseWindow) {
            C0DPacketCloseWindow packet = event.getPacket();
            if (packet.windowId == windowId) {
                event.setCancelled(true);
            }
        }
    }
}

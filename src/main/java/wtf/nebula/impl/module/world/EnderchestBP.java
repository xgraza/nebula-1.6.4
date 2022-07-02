package wtf.nebula.impl.module.world;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.src.GuiChest;
import net.minecraft.src.GuiContainer;
import net.minecraft.src.Packet101CloseWindow;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import wtf.nebula.event.GuiOpenEvent;
import wtf.nebula.event.PacketEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.impl.value.Value;

public class EnderchestBP extends Module {
    public EnderchestBP() {
        super("EnderChestBP", ModuleCategory.WORLD);
    }

    public final Value<Boolean> all = new Value<>("All", false);

    private GuiChest gui;

    @Override
    protected void onActivated() {
        super.onActivated();
        gui = null;
    }

    @Override
    protected void onDeactivated() {
        super.onDeactivated();

        if (!nullCheck()) {
            mc.displayGuiScreen(gui);
        }
    }

    @EventListener
    public void onGuiOpen(GuiOpenEvent event) {
        if (event.getNewScreen() instanceof GuiChest) {
            GuiChest chest = (GuiChest) event.getNewScreen();

            if (!all.getValue() && !chest.lowerChestInventory.getInvName().equals("container.enderchest")) {
                return;
            }

            sendChatMessage("Saved chest GUI! Disable this module to access the container.");
            gui = chest;
        }
    }

    @EventListener
    public void onPacketSend(PacketEvent.Send event) {

        // if we try to close a window
        if (event.getPacket() instanceof Packet101CloseWindow) {

            // our packet
            Packet101CloseWindow packet = event.getPacket();

            // if we saved an enderchest GUI and the window id being closed equates to the one we have saved, do not allow this to be closed.
            if (gui != null && packet.windowId == gui.inventorySlots.windowId) {
                event.setCancelled(true);
            }
        }
    }
}

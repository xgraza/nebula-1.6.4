package wtf.nebula.client.impl.module.active;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.EnumChatFormatting;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.impl.event.base.Era;
import wtf.nebula.client.impl.event.impl.nebula.EventModuleToggle;
import wtf.nebula.client.impl.event.impl.network.EventPacket;
import wtf.nebula.client.impl.event.impl.world.EventEntitySpawn;
import wtf.nebula.client.impl.module.Module;
import wtf.nebula.client.impl.module.ToggleableModule;
import wtf.nebula.client.impl.module.visuals.LogoutSpots;
import wtf.nebula.client.utils.io.SystemTrayUtils;

public class Notifications extends Module {
    public static final Property<Boolean> trayIcon = new Property<>(true, "System Tray", "trayicon", "systemtray", "systray");
    private final Property<Boolean> modules = new Property<>(true, "Modules", "mods");
    private final Property<Boolean> visualRange = new Property<>(true, "Visual Range", "visualrange", "playerspawn");
    private final Property<Boolean> messages = new Property<>(true, "Messages", "privatemessages", "pms");

    public Notifications() {
        super("Notifications", new String[]{"notis", "alerts"});
        offerProperties(trayIcon, modules, visualRange, messages);
    }

    @EventListener
    public void onPacket(EventPacket event) {
        if (event.getEra().equals(Era.PRE) && event.getPacket() instanceof S02PacketChat) {
            S02PacketChat packet = event.getPacket();

            String unformatted = packet.func_148915_c().getUnformattedText();
            if (unformatted.contains("whispered") || unformatted.contains("whispers")) {

                if (shouldUseTray() && !(mc.currentScreen instanceof GuiChat) && messages.getValue()) {
                    SystemTrayUtils.showMessage("You have received a private message.");
                }
            }
        }
    }

    @EventListener
    public void onModuleToggle(EventModuleToggle event) {
        if (!isNull() && modules.getValue()) {
            ToggleableModule module = event.getModule();
            print(module.getLabel() + " toggled " + (module.isRunning() ? (EnumChatFormatting.GREEN + "on") : (EnumChatFormatting.RED + "off")) + EnumChatFormatting.GRAY + ".", hashCode());
        }
    }

    @EventListener
    public void onEntitySpawn(EventEntitySpawn event) {
        if (event.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntity();
            if (player.equals(mc.thePlayer) || player.getCommandSenderName().equals(mc.session.getUsername())) {
                return;
            }

            if (LogoutSpots.isFake(player.getEntityId())) {
                return;
            }

            if (visualRange.getValue()) {
                if (Notifications.shouldUseTray()) {
                    SystemTrayUtils.showMessage("Player \"" + player.getCommandSenderName() + "\" came into your visual range!");
                } else {
                    print("Player " + EnumChatFormatting.RED + player.getCommandSenderName() + EnumChatFormatting.GRAY + " came into your visual range!");
                }
            }
        }
    }

    public static boolean shouldUseTray() {
        return SystemTrayUtils.isCreated() && !mc.inGameHasFocus && trayIcon.getValue();
    }
}

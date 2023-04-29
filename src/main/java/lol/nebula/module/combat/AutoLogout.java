package lol.nebula.module.combat;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.entity.EventUpdate;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.setting.Setting;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import static java.lang.String.format;

/**
 * @author aesthetical
 * @since 04/29/23
 */
public class AutoLogout extends Module {
    private final Setting<Float> health = new Setting<>(6.0f, 0.01f, 1.0f, 19.0f, "Health");

    public AutoLogout() {
        super("Auto Logout", "Automatically logs out", ModuleCategory.COMBAT);
    }

    @Listener
    public void onUpdate(EventUpdate event) {

        // if the health is greater than the minimum health, return
        if (mc.thePlayer.getHealth() > health.getValue()) return;

        // close the channel
        mc.thePlayer.sendQueue.getNetworkManager().closeChannel(
                new ChatComponentText(format("%sauto logout %s> health was less than %.1f hearts",
                        EnumChatFormatting.RED, EnumChatFormatting.GRAY, health.getValue() / 2.0f)));

        // automatically turn off the module
        setState(false);
    }
}

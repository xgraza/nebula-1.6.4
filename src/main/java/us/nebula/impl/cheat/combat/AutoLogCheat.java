package us.nebula.impl.cheat.combat;

import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import us.nebula.api.listener.EventListener;
import us.nebula.api.listener.Subscribe;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.api.value.Setting;
import us.nebula.impl.event.game.EventUpdate;

/**
 * @author xgraza
 * @since 03/30/25
 */
@CheatManifest(name = "AutoLog",
        description = "Automatically logs out for you",
        category = CheatCategory.COMBAT)
public final class AutoLogCheat extends Cheat
{
    private final Setting<Float> healthSetting = new Setting<>(
            "Health", 6.0f, 1.0f, 19.5f, 0.5f);

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        if (MC.thePlayer.getHealth() > healthSetting.getValue())
        {
            return;
        }
        MC.theWorld.sendQuittingDisconnectingPacket();
        MC.thePlayer.sendQueue.getNetworkManager().closeChannel(
                new ChatComponentText("")
                        .appendSibling(new ChatComponentText("[AutoLog] ")
                                .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)))
                        .appendText("Health less than set minimum"));
        toggle();
    };
}

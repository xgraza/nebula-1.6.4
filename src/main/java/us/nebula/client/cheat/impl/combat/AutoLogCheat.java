package us.nebula.client.cheat.impl.combat;

import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import us.nebula.client.listener.EventListener;
import us.nebula.client.listener.Subscribe;
import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.util.value.Setting;
import us.nebula.client.listener.event.game.EventUpdate;

/**
 * @author xgraza
 * @since 03/30/25
 */
@CheatManifest(name = "AutoLog",
        description = "Automatically logs you off a server on specific conditions",
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

    @Override
    public String getMetadata()
    {
        return String.format("%.2f", healthSetting.getValue());
    }
}

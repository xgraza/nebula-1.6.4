package ez.nebula.client.impl.module.combat;

import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.setting.Setting;
import ez.nebula.client.api.listener.event.game.EventUpdate;

/**
 * @author xgraza
 * @since 03/30/25
 */
@ModuleManifest(name = "AutoLog",
        description = "Automatically logs you off a server on specific conditions",
        category = ModuleCategory.COMBAT)
public final class AutoLogModule extends Module
{
    private final Setting<Float> healthSetting = numberBuilder("Health", 6.0f)
            .setMin(1.0f)
            .setMax(19.5f)
            .setScale(0.5f)
            .setDescription("At what health should you automatically be logged off")
            .build();

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

package ez.nebula.client.impl.module.player;

import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.listener.event.network.EventPacket;
import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.setting.Setting;
import ez.nebula.client.core.Nebula;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.ChatAllowedCharacters;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xgraza
 * @since 8/21/26
 */
@ModuleManifest(name = "Parrot",
        description = "Repeat what other players say like a parrot",
        category = ModuleCategory.PLAYER)
public final class ParrotModule extends Module
{
    private static final Pattern PLAYER_TAG_REGEX = Pattern.compile("<(.+)>\\s");

    private final Setting<Boolean> friendsSetting = builder("Friends", false)
            .setDescription("If to repeat your friends")
            .build();

    @Subscribe
    private final EventListener<EventPacket.Inbound> inboundEventListener = event ->
    {
        if (MC.thePlayer == null || MC.theWorld == null)
        {
            return;
        }
        if (event.getPacket() instanceof S02PacketChat)
        {
            final S02PacketChat packet = event.getPacket();
            final String unformatted = packet.getMessage().getUnformattedText()
                    .replaceAll("\u00a7(.)", "").trim();

            final String playerName = getPlayerName(unformatted);
            if (playerName == null || playerName.isEmpty() || playerName.equalsIgnoreCase(MC.getSession().getUsername()))
            {
                return;
            }

            if (!friendsSetting.getValue() && Nebula.INSTANCE.getFriendManager().isFriend(playerName))
            {
                return;
            }

            MC.thePlayer.sendQueue.addToSendQueue(new C01PacketChatMessage(
                    ChatAllowedCharacters.filerAllowedCharacters(
                            unformatted.replaceFirst(PLAYER_TAG_REGEX.pattern(), ""))));
        }
    };

    private String getPlayerName(final String text)
    {
        final Matcher matcher = PLAYER_TAG_REGEX.matcher(text);
        if (matcher.find())
        {
            return matcher.group(1);
        }
        return null;
    }
}

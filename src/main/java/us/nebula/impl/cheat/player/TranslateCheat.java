package us.nebula.impl.cheat.player;

import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import us.nebula.api.listener.EventListener;
import us.nebula.api.listener.Subscribe;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatInstance;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.api.translation.GoogleTranslateService;
import us.nebula.api.translation.Language;
import us.nebula.api.value.Setting;
import us.nebula.impl.event.network.EventPacket;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xgraza
 * @since 03/24/25
 */
@CheatManifest(name = "Translate",
        description = "Translates things in chat",
        category = CheatCategory.PLAYER)
public final class TranslateCheat extends Cheat
{
    @CheatInstance
    public static TranslateCheat INSTANCE;
    private static final Pattern PLAYER_TAG_REGEX = Pattern.compile("<(.+)>\\s");

    private final Setting<Language> targetSetting = new Setting<>(
            "Target", Language.ENGLISH);

    @Subscribe
    private final EventListener<EventPacket.Inbound> inboundEventListener = event ->
    {
        if (event.getPacket() instanceof S02PacketChat)
        {
            final S02PacketChat packet = event.getPacket();
            final IChatComponent component = packet.getMessage();
            if (!component.getUnformattedText().startsWith("<"))
            {
                return;
            }
            final ChatComponentText c = new ChatComponentText(component.getFormattedText());
            c.setChatStyle(new ChatStyle()
                    .setChatClickEvent(new ClickEvent(null, "NEBULA_TRANSLATE"))
                    .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new ChatComponentText("Click to translate")
                                    .setChatStyle(new ChatStyle()
                                            .setColor(EnumChatFormatting.BLUE)))));
            packet.setMessage(c);
        }
    };

    public void handleTranslate(final IChatComponent component)
    {
        final String raw = EnumChatFormatting.getTextWithoutFormattingCodes(component.getUnformattedText());
        final String playerName = getPlayerName(raw);
        final String unformatted = raw.replaceFirst(PLAYER_TAG_REGEX.pattern(), "").trim();
        GoogleTranslateService.INSTANCE.translate(
                targetSetting.getValue(), Language.AUTO, unformatted,
                (source, text) ->
                {
                    final ChatComponentText c = new ChatComponentText("");
                    c.appendSibling(new ChatComponentText("[from " + source.getLocale() + "]")
                            .setChatStyle(new ChatStyle()
                                    .setColor(EnumChatFormatting.BLUE)));
                    c.appendText(" ");
                    c.appendText("<" + playerName + ">");
                    c.appendText(" ");
                    c.appendText(text);
                    MC.ingameGUI.getChatGui().printChatMessage(c);
                });
    }

    private String getPlayerName(final String text)
    {
        final Matcher matcher = PLAYER_TAG_REGEX.matcher(text);
        if (matcher.find())
        {
            return matcher.group(1);
        }
        return "Player";
    }
}

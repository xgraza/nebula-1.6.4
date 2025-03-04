package us.nebula.util.player;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

/**
 * @author xgraza
 * @since 02/15/25
 */
public final class ChatUtil
{
    private static final Minecraft MC = Minecraft.getMinecraft();
    private static final String CHAT_PREFIX = String.format(
            "%s(nebula):%s ",
            EnumChatFormatting.LIGHT_PURPLE,
            EnumChatFormatting.RESET);

    public static void send(String content, final Object... format)
    {
        content = content.replaceAll("(?i)&([0-9A-FK-OR])", "\u00a7$1");
        content = String.format(content, format);
        send(content);
    }

    public static void send(final String content)
    {
        final String[] lines = content.split("\n");
        for (final String line : lines)
        {
            final IChatComponent component = createBaseChatComponent();
            component.appendText(line);
            MC.ingameGUI.getChatGui().printChatMessage(component);
        }
    }

    private static IChatComponent createBaseChatComponent()
    {
        return new ChatComponentText(CHAT_PREFIX)
                .setChatStyle(new ChatStyle()
                        .setColor(EnumChatFormatting.GRAY));
    }
}

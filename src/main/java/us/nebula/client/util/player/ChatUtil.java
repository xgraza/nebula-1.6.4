package us.nebula.client.util.player;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import us.nebula.client.Nebula;

/**
 * @author xgraza
 * @since 02/15/25
 */
public final class ChatUtil
{
    private static final Minecraft MC = Minecraft.getMinecraft();
    private static final String CHAT_PREFIX = String.format(
            "%s(Nebula):%s ",
            EnumChatFormatting.LIGHT_PURPLE,
            EnumChatFormatting.RESET);

    public static final String DEBUG_PREFIX = String.format(
            "%s(DEBUG):%s",
            EnumChatFormatting.YELLOW,
            EnumChatFormatting.RESET);
    public static final String VERBOSE_PREFIX = EnumChatFormatting.BLUE
            + "(%s):"
            + EnumChatFormatting.RESET;

    public static void sendFormatted(final String chatPrefix, String content, final Object... format)
    {
        content = content.replaceAll("(?i)&([0-9A-FK-OR])", "§$1");
        content = String.format(content, format);

        final String[] lines = content.split("\n");
        for (final String line : lines)
        {
            final IChatComponent component = createBaseChatComponent(chatPrefix);
            component.appendText(line);
            if (MC.ingameGUI == null || MC.thePlayer == null)
            {
                Nebula.INSTANCE.getLogger().info(component);
            } else
            {
                MC.ingameGUI.getChatGui().printChatMessage(component);
            }
        }
    }

    public static void sendNebula(final String content, final Object... format)
    {
        sendFormatted(CHAT_PREFIX, content, format);
    }

    private static IChatComponent createBaseChatComponent(final String chatPrefix)
    {
        return new ChatComponentText(chatPrefix)
                .setChatStyle(new ChatStyle()
                        .setColor(EnumChatFormatting.GRAY));
    }
}

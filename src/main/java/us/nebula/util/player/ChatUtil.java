package us.nebula.util.player;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import us.nebula.Nebula;

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

    public static void send(String content, final Object... format)
    {
        content = content.replaceAll("(?i)&([0-9A-FK-OR])", "\u00a7$1");
        content = String.format(content, format);

        final String[] lines = content.split("\n");
        for (final String line : lines)
        {
            final IChatComponent component = createBaseChatComponent();
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

    private static IChatComponent createBaseChatComponent()
    {
        return new ChatComponentText(CHAT_PREFIX)
                .setChatStyle(new ChatStyle()
                        .setColor(EnumChatFormatting.GRAY));
    }
}

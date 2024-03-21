package nebula.client.util.chat;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

/**
 * @author Gavin
 * @since 08/11/23
 */
public class Printer {

  /**
   * The minecraft game instance
   */
  private static final Minecraft mc = Minecraft.getMinecraft();

  /**
   * The prefix of the client chat message
   */
  public static final String PREFIX = ChatUtils.replaceFormatting("[&dN&7] ");

  /**
   * Prints a message to the chat client-sided
   * @param content the custom chat message
   */
  public static void print(String content) {
    mc.ingameGUI.getChatGui().printChatMessage(new ChatComponentText("")
      .appendText(PREFIX)
      .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GRAY))
      .appendText(ChatUtils.replaceFormatting(content)));
  }

  /**
   * Prints a message to the chat client-sided
   * @param component the custom chat component
   */
  public static void print(IChatComponent component) {
    mc.ingameGUI.getChatGui().printChatMessage(new ChatComponentText("")
      .appendText(PREFIX)
      .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GRAY))
      .appendSibling(component));
  }
}

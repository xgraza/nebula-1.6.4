package wtf.nebula.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

/**
 * An interface that includes all the basic utilities a feature should contain
 *
 * @author aesthetical
 * @since 06/27/22
 */
public interface Globals {
    // our minecraft instance
    Minecraft mc = Minecraft.getMinecraft();

    // the client message prefix
    String PREFIX = EnumChatFormatting.GRAY + "<" +
            EnumChatFormatting.LIGHT_PURPLE + "Nebula" +
            EnumChatFormatting.GRAY + ">" +
            EnumChatFormatting.RESET + " ";

    // the irc client message prefix
    String IRC_PREFIX = EnumChatFormatting.GRAY + "<" +
            EnumChatFormatting.RED + "IRC" +
            EnumChatFormatting.GRAY + ">" +
            EnumChatFormatting.RESET + " ";

    /**
     * Checks if necessities needed by the client for modules are null
     * @return if they are null
     */
    default boolean nullCheck() {
        return mc.thePlayer == null || mc.theWorld == null;
    }

    /**
     * Sends a client message
     * @param msg the message to present to the user
     */
    default void sendChatMessage(String msg) {
        mc.ingameGUI.getChatGui().printChatMessage(new ChatComponentText(PREFIX + msg));
    }

    /**
     * Sends a client message with an editable message id
     * @param id the editable message id
     * @param msg the message to present to the user
     */
    default void sendChatMessage(int id, String msg) {
        mc.ingameGUI.getChatGui().printChatMessageWithOptionalDeletion(new ChatComponentText(PREFIX + msg), id);
    }

    default void sendIRCMessage(String message) {
        if (!nullCheck()) {
            mc.ingameGUI.getChatGui().printChatMessage(new ChatComponentText(IRC_PREFIX + message));
        }
    }
}

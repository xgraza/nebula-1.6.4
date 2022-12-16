package wtf.nebula.client.utils.client;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import wtf.nebula.client.core.Nebula;

public interface Wrapper {
    Minecraft mc = Minecraft.getMinecraft();
    String ID = EnumChatFormatting.RED + "[Nebula] " + EnumChatFormatting.RESET;

    default boolean isNull() {
        return mc.thePlayer == null || mc.theWorld == null;
    }

    default void print(String message) {
        mc.ingameGUI.getChatGui().printChatMessage(new ChatComponentText(ID)
                .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GRAY))
                .appendText(message));
    }

    default void print(String message, int id) {
        mc.ingameGUI.getChatGui().printChatMessageWithOptionalDeletion(
                new ChatComponentText(ID)
                        .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GRAY))
                        .appendText(message), id);
    }

    default void print(ChatComponentText comp) {
        mc.ingameGUI.getChatGui().printChatMessage(comp);
    }

    default Nebula getNebula() {
        return Nebula.getInstance();
    }
}

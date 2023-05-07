package lol.nebula.command;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

/**
 * @author aesthetical
 * @since 04/28/23
 */
public abstract class Command {

    /**
     * The minecraft game instance
     */
    protected final Minecraft mc = Minecraft.getMinecraft();

    private final String[] aliases;
    private final String description, syntax;

    /**
     * Creates a new command
     * @param aliases the command aliases
     * @param description the description of this command
     */
    public Command(String[] aliases, String description) {
        this(aliases, description, null);
    }

    /**
     * Creates a new command
     * @param aliases the command aliases
     * @param description the description of this command
     * @param syntax the syntax of this command or null
     */
    public Command(String[] aliases, String description, String syntax) {
        this.aliases = aliases;
        this.description = description;
        this.syntax = syntax;
    }

    /**
     * Dispatches a command
     * @param args the arguments passed
     * @return the message after dispatching, or null
     */
    public abstract String dispatch(String[] args);

    /**
     * Prints a message for this command
     * @param message the message to print to the in game chat
     */
    public void printEditable(String message) {
        mc.ingameGUI.getChatGUI().func_146234_a(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "nebula")
                .appendText(" ")
                .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GRAY))
                .appendText("> ")
                .appendText(message), hashCode());
    }

    /**
     * Prints a message for this command
     * @param message the message to print to the in game chat
     */
    public void print(String message) {
        mc.ingameGUI.getChatGUI().func_146227_a(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "nebula")
                .appendText(" ")
                .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GRAY))
                .appendText("> ")
                .appendText(message));
    }

    /**
     * Gets the command aliases
     * @return a string array of the command aliases
     */
    public String[] getAliases() {
        return aliases;
    }

    /**
     * Gets the command's description
     * @return the command description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the command's syntax
     * @return the command syntax
     */
    public String getSyntax() {
        return syntax;
    }
}

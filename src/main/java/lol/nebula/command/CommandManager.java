package lol.nebula.command;

import lol.nebula.Nebula;
import lol.nebula.command.impl.*;
import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.net.EventPacket;
import net.minecraft.network.play.client.C01PacketChatMessage;

import java.util.*;

/**
 * @author aesthetical
 * @since 04/28/23
 */
public class CommandManager {

    /**
     * A map of all the commands
     */
    private final Map<String, Command> commandNameMap = new LinkedHashMap<>();

    /**
     * A list of all the commands
     */
    private final List<Command> commandList = new LinkedList<>();

    /**
     * The command prefix
     */
    private String commandPrefix = ".";

    /**
     * The prefix config
     */
    private final PrefixConfig prefixConfig;

    public CommandManager() {
        prefixConfig = new PrefixConfig(this);

        register(
                new Drawn(),
                new FakePlayer(),
                new Friend(),
                new Prefix(),
                new SpawnTP(),
                new Toggle(),
                new Tps(),
                new Value(),
                new VClip()
        );
    }

    @Listener(eventPriority = Integer.MAX_VALUE, receiveCanceled = true)
    public void onPacketOutbound(EventPacket.Outbound event) {
        if (event.getPacket() instanceof C01PacketChatMessage) {
            C01PacketChatMessage packet = event.getPacket();
            String message = packet.func_149439_c();

            // do not attempt to parse messages that don't even have the command prefix at the start
            if (message.isEmpty() || !message.startsWith(commandPrefix)) return;

            // do not send this message to the server
            event.cancel();

            // split the message into parts
            String[] args = message.substring(1).trim().split("\\s+");

            // if there are no arguments, return
            if (args.length == 0) return;

            // get the command with this alias
            Command command = getCommand(args[0]);
            if (command != null) {

                // remove the first argument
                args = Arrays.copyOfRange(args, 1, args.length);

                try {
                    String dispatched = command.dispatch(args);
                    if (dispatched == null) dispatched = "Command dispatched successfully.";

                    command.print(dispatched);
                } catch (Exception e) {
                    Nebula.getLogger().error("Failed to run command {}", command.getAliases()[0]);
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Registers all the commands into this manager
     * @param commands the commands
     */
    private void register(Command... commands) {
        for (Command command : commands) {
            commandList.add(command);

            // adds all the command aliases into the command name map
            for (String alias : command.getAliases()) {
                commandNameMap.put(alias, command);
            }
        }
    }

    /**
     * Sets the command execution prefix
     * @param commandPrefix the new prefix
     * @param save if to save this new prefix to the config file
     */
    public void setCommandPrefix(String commandPrefix, boolean save) {
        this.commandPrefix = commandPrefix;

        // save the new prefix if specified
        if (save) prefixConfig.save();
    }

    /**
     * Gets the command prefix
     * @return the command prefix
     */
    public String getCommandPrefix() {
        return commandPrefix;
    }

    /**
     * Gets the prefix config instance
     * @return the prefix config instance
     */
    public PrefixConfig getPrefixConfig() {
        return prefixConfig;
    }

    /**
     * Gets all the commands in a list
     * @return the command list
     */
    public Collection<Command> getCommands() {
        return commandList;
    }

    /**
     * Gets a command by the command alias
     * @param alias the alias of the command
     * @return the command with this alias or null
     * @param <T> the command
     */
    public <T extends Command> T getCommand(String alias) {
        return (T) commandNameMap.getOrDefault(alias.toLowerCase(), null);
    }
}

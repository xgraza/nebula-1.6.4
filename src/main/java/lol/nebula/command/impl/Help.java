package lol.nebula.command.impl;

import lol.nebula.Nebula;
import lol.nebula.command.Command;

import java.util.Collection;
import java.util.StringJoiner;

/**
 * @author aesthetical
 * @since 05/07/23
 */
public class Help extends Command {
    public Help() {
        super(new String[]{"help", "h", "commands", "cmds"}, "Displays help on commands", "<command name>");
    }

    @Override
    public String dispatch(String[] args) {
        if (args.length == 0) {
            Collection<Command> commands = Nebula.getInstance().getCommands().getCommands();
            StringBuilder builder = new StringBuilder("Commands (")
                    .append(commands.size())
                    .append(")")
                    .append(":")
                    .append(" ");

            StringJoiner joiner = new StringJoiner(", ");
            for (Command command : commands) {
                joiner.add(command.getAliases()[0]);
            }

            return builder.append(joiner).toString();
        }

        Command command = Nebula.getInstance().getCommands().getCommand(args[0]);
        if (command == null) return "Invalid command name";

        print("Aliases: " + String.join(", ", command.getAliases()));
        print("Description: " + command.getDescription());

        return "Syntax: " + (command.getSyntax() == null ? "None" : command.getSyntax());
    }
}

package wtf.nebula.client.impl.command.impl;

import wtf.nebula.client.core.Nebula;
import wtf.nebula.client.impl.command.Command;
import wtf.nebula.client.impl.command.arg.Argument;
import wtf.nebula.client.impl.command.arg.CommandContext;
import wtf.nebula.client.impl.command.arg.impl.StringArgument;

import java.util.List;
import java.util.stream.Collectors;

public class Help extends Command {
    public Help() {
        super(new String[]{"help", "h", "commands"}, new StringArgument("commandName").setRequired(false));
    }

    @Override
    public String dispatch(CommandContext ctx) {
        String name = (String) ctx.get("commandName").getValue();
        if (name != null && !name.isEmpty()) {
            Command command = Nebula.getInstance().getCommandManager().commandNameMap.getOrDefault(name.trim().toLowerCase(), null);
            if (command != null) {
                print("Aliases: " + String.join(", ", command.getAliases()));
                if (command.getArgs().length > 0) {
                    StringBuilder builder = new StringBuilder("Arguments (" + command.getArgs().length + "): ");
                    for (Argument argument : command.getArgs()) {
                        if (argument.isRequired()) {
                            builder.append("[");
                        } else {
                            builder.append("(");
                        }

                        builder.append(argument.getName());

                        if (argument.isRequired()) {
                            builder.append("]");
                        } else {
                            builder.append(")");
                        }

                        builder.append(" ");
                    }
                    print(builder.toString());
                }
                return "";
            }
        }

        List<Command> commandList = Nebula.getInstance().getCommandManager().getRegistry();
        return "Total of " + commandList.size() + " commands: " + commandList.stream().map((s) -> s.getAliases()[0]).collect(Collectors.joining(", "));
    }
}

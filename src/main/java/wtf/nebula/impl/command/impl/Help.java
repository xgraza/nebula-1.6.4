package wtf.nebula.impl.command.impl;

import net.minecraft.src.EnumChatFormatting;
import wtf.nebula.impl.command.Command;
import wtf.nebula.repository.impl.CommandRepository;
import wtf.nebula.util.feature.Feature;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Help extends Command {
    public Help() {
        super(Arrays.asList("help", "h", "halp", "commands", "cmds"), "Displays all runnable commands");
    }

    @Override
    public void execute(List<String> args) {
        if (args.isEmpty()) {
            doDefault(0);
        }

        else {

            Command command = CommandRepository.get()
                    .commandByTrigger
                    .getOrDefault(args.get(0).toLowerCase(), null);

            if (command == null) {
                int page = 0;
                try {
                    page = Integer.parseInt(args.get(0)) - 1;
                } catch (NumberFormatException ignored) {

                }

                doDefault(page);
            }

            else {
                sendChatMessage(command.getDescription() + "\nTriggers: " + String.join(", ", command.getTriggers()));
            }
        }
    }

    private void doDefault(int page) {
        List<Command> commands = CommandRepository.get().getChildren();

        int perPage = 5;
        int max = (int) Math.ceil(commands.size() / (double) perPage);

        if (page < 0) {
            page = 0;
        }

        if (page >= max) {
            page = max - 1;
        }

        sendChatMessage("Commands" +
                " " +
                EnumChatFormatting.GREEN +
                "(" +
                commands.size() +
                ")" +
                EnumChatFormatting.RESET +
                " " +
                "Page" +
                " " +
                (page + 1) +
                "/" +
                max +
                "\n" +
                commands
                        .subList(page * perPage, Math.min(commands.size(), (page + 1) * perPage))
                        .stream().map(Feature::getName)
                        .collect(Collectors.joining(", ")));
    }
}

package wtf.nebula.impl.command.impl;

import net.minecraft.util.EnumChatFormatting;
import wtf.nebula.impl.command.Command;
import wtf.nebula.repository.impl.CommandRepository;

import java.util.Arrays;
import java.util.List;

public class Prefix extends Command {
    public Prefix() {
        super(Arrays.asList("prefix", "pfx", "cmdpfx", "commandprefix"), "Sets the command prefix");
    }

    @Override
    public void execute(List<String> args) {
        if (args.isEmpty()) {
            sendChatMessage("Please provide a new prefix");
            return;
        }

        String prefix = args.get(0).toLowerCase();
        if (prefix.equals("@")) {
            sendChatMessage("You cannot use that prefix. That is the IRC prefix.");
            return;
        }

        // if youre retarded and set ur prefix to "fhakgjfhakjfhkhkjhak" that not my problem if you forget it lol
        CommandRepository.get().setPrefix(prefix);
        sendChatMessage("Set prefix to " + EnumChatFormatting.GREEN + prefix);
    }
}

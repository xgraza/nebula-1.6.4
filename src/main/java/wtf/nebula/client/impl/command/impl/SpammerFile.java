package wtf.nebula.client.impl.command.impl;

import net.minecraft.util.EnumChatFormatting;
import wtf.nebula.client.impl.command.Command;
import wtf.nebula.client.impl.command.arg.CommandContext;
import wtf.nebula.client.impl.command.arg.impl.StringArgument;
import wtf.nebula.client.impl.module.miscellaneous.Spammer;

import java.io.File;

public class SpammerFile extends Command {
    public SpammerFile() {
        super(new String[]{"spammer", "spammerfile", "setspammer"}, new StringArgument("fileName"));
    }

    @Override
    public String dispatch(CommandContext ctx) {
        String f = (String) ctx.get("fileName").getValue();
        File file = new File(Spammer.SPAMMER_FOLDER, f + ".txt");
        if (f.isEmpty() || !file.exists()) {
            return "File does not exist.";
        }

        if (file.isDirectory()) {
            return "This file is a directory.";
        }

        Spammer.setFile(file);
        return "Set file to " + EnumChatFormatting.YELLOW + file.getName() + EnumChatFormatting.GRAY + ".";
    }
}

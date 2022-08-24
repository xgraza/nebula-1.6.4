package wtf.nebula.impl.command.impl;

import net.minecraft.util.EnumChatFormatting;
import wtf.nebula.impl.command.Command;
import wtf.nebula.impl.module.misc.Spammer;
import wtf.nebula.repository.impl.ModuleRepository;
import wtf.nebula.util.FileUtil;

import java.awt.*;
import java.awt.Desktop.Action;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class SpammerFile extends Command {
    public SpammerFile() {
        super(Arrays.asList("spammer", "spammerfile", "spam"), "Spams text from files");
    }

    @Override
    public void execute(List<String> args) {
        if (!Files.exists(FileUtil.SPAMMER)) {
            try {
                Files.createDirectory(FileUtil.SPAMMER);

                sendChatMessage("Created spammer folder. Opened in your file explorer");
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Action.OPEN)) {
                    Desktop.getDesktop().open(FileUtil.SPAMMER.toFile());
                }

                else {
                    sendChatMessage("bro wtf ur de/wm is not supporting my file explorer opening wtf mane");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return;
        }

        if (args.isEmpty()) {
            sendChatMessage("Please provide a file name without the extension or \"start\" or \"stop\"");
            return;
        }

        Spammer spammer = ModuleRepository.get().getModule(Spammer.class);

        String arg = args.get(0);
        if (arg.equalsIgnoreCase("start")) {
            if (!spammer.getState()) {
                spammer.setState(true);
            }

            spammer.start(spammer.fileName.getValue());
            return;
        }

        else if (arg.equalsIgnoreCase("stop")) {
            if (spammer.getState()) {
                spammer.setState(false);
            }

            return;
        }

        if (!arg.endsWith(".txt")) {
            arg += ".txt";
        }

        Path path = FileUtil.SPAMMER.resolve(arg);
        if (!Files.exists(path) || !Files.isReadable(path)) {
            sendChatMessage("Unreadable file.");
            return;
        }

        spammer.fileName.setValue(arg);
        sendChatMessage("Set the spammer file to " + EnumChatFormatting.GREEN + path);
    }
}

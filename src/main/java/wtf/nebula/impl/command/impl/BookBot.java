package wtf.nebula.impl.command.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import net.minecraft.src.*;
import wtf.nebula.impl.command.Command;
import wtf.nebula.util.FileUtil;

import java.awt.*;
import java.awt.Desktop.Action;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class BookBot extends Command {
    public BookBot() {
        super(Arrays.asList("bookbot", "writefile"), "Writes text off of a file");
    }

    @Override
    public void execute(List<String> args) {
        ItemStack held = mc.thePlayer.getHeldItem();
        if (held == null || !held.getItem().equals(Item.writableBook)) {
            sendChatMessage("Please hold a book and quill in your hand");
            return;
        }

        if (!Files.exists(FileUtil.BOOKBOT)) {
            sendChatMessage("Creating bookbot directory...");

            try {
                Files.createDirectory(FileUtil.BOOKBOT);
                sendChatMessage("Created bookbot directory! Opened it in your file explorer. Create a text file with any of the book contents you'd like");

                if (!Desktop.isDesktopSupported()) {
                    sendChatMessage("Desktop is not supported for your PC! Open this folder manually");
                    return;
                }

                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Action.OPEN)) {
                    desktop.open(FileUtil.BOOKBOT.toFile());
                }

                else {
                    sendChatMessage("Action OPEN is not supported in your WM/DE");
                }
            } catch (IOException e) {
                e.printStackTrace();

                sendChatMessage("Couldn't create bookbot folder, check logs");
            }

            return;
        }

        if (args.isEmpty()) {
            sendChatMessage("Please provide the file name WITHOUT the .txt at the end");
            return;
        }

        Path file = FileUtil.BOOKBOT.resolve(args.get(0) + ".txt");
        if (!Files.exists(file) || !Files.isReadable(file)) {
            sendChatMessage("Cannot read that file");
            return;
        }

        String content = FileUtil.read(file);
        if (content == null || content.isEmpty()) {
            sendChatMessage("No content found in this file.");
            return;
        }

        args = args.subList(1, args.size());

        String title = "Nebula on top!";
        if (!args.isEmpty()) {
            title = String.join(" ", args);
        }

        // limit the title text length to 16
        title = title.substring(0, Math.min(title.length(), 16));

        List<String> lines = ImmutableList.copyOf(Splitter.fixedLength(256).limit(50).split(content));

        // out nbt list
        NBTTagList list = new NBTTagList();

        for (int i = 0; i < lines.size(); ++i) {
            String line = lines.get(i);
            line = line.substring(0, Math.min(256, line.length()));

            list.appendTag(new NBTTagString(String.valueOf(i), line));
        }

        Write.signBook(held, list, title);
        sendChatMessage("Signed book successfully from file " + EnumChatFormatting.GREEN + file);
    }
}

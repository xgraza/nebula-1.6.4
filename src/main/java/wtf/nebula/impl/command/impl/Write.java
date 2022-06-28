package wtf.nebula.impl.command.impl;

import net.minecraft.src.*;
import wtf.nebula.impl.command.Command;
import wtf.nebula.util.MathUtil;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class Write extends Command {
    public Write() {
        super(Arrays.asList("write", "w", "writebook", "bookbot"), "Writes randomized text into a book for the chunk dupe");
    }

    @Override
    public void execute(List<String> args) {
        ItemStack held = mc.thePlayer.getHeldItem();
        if (held == null || !held.getItem().equals(Item.writableBook)) {
            sendChatMessage("Please hold a book and quill in your hand");
            return;
        }

        String title = "Nebula client on top!";
        if (!args.isEmpty()) {
            title = String.join(" ", args);
        }

        // max title length is 16
        title = title.substring(0, 16);

        // create a NBT compound to put our book text in
        NBTTagList pages = new NBTTagList();

        for (int i = 0; i < 50; ++i) {
            StringBuilder builder = new StringBuilder();

            // this part is from medmex
            IntStream stream = MathUtil.RNG.ints(256, 0x0800, 0x10FFFF);
            for (int j : stream.toArray()) {
                builder.append((char) j);
            }

            pages.appendTag(new NBTTagString(String.valueOf(i), builder.toString()));
        }

        signBook(held, pages, title);
        sendChatMessage("Hey hey hey, the book got successfully written in!");
    }

    private void signBook(ItemStack stack, NBTTagList nbtTagList, String title) {
        String CHANNEL_NAME = "MC|BSign";

        stack.setTagInfo("pages", nbtTagList);
        stack.setTagInfo("author", new NBTTagString("author", mc.thePlayer.getCommandSenderName()));
        stack.setTagInfo("title", new NBTTagString("title", title.trim()));

        stack.itemID = Item.writtenBook.itemID;

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        try {
            Packet.writeItemStack(stack, dataOutputStream);
            mc.thePlayer.sendQueue.addToSendQueue(new Packet250CustomPayload(CHANNEL_NAME, byteArrayOutputStream.toByteArray()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

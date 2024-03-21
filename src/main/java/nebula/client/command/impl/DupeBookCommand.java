package nebula.client.command.impl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import nebula.client.command.Command;
import nebula.client.command.CommandMeta;
import nebula.client.command.CommandResults;
import nebula.client.util.chat.Printer;
import nebula.client.util.math.MathUtils;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;

import java.util.stream.IntStream;

/**
 * @author Gavin
 * @since 08/25/23
 */
@SuppressWarnings("unused")
@CommandMeta(aliases = {"dupebook", "dbook"},
  description = "yes")
public class DupeBookCommand extends Command {

  private static final int MAX_PAGE_TEXT_SIZE = 256;
  private static final int START_BYTE = 0x0800;
  private static final int END_BYTE = 0x10FFFF;

  @Override
  public int execute(String[] args) throws Exception {

    ItemStack stack = mc.thePlayer.getHeldItem();
    if (stack == null || stack.getItem() != Items.writable_book) {
      Printer.print("Hold a writable book in your hand");
      return CommandResults.SUCCESS_NO_RESPONSE;
    }

    NBTTagList tagList = new NBTTagList();
    for (int page = 0; page < 50; ++page) {
      IntStream stream = MathUtils.RNG.ints(
        MAX_PAGE_TEXT_SIZE, START_BYTE, END_BYTE);

      StringBuilder builder = new StringBuilder();
      for (int i : stream.toArray()) {
        builder.append((char) i);
      }

      stream.close();

      tagList.appendTag(new NBTTagString(builder.toString()));
    }

    stack.setTagInfo("author", new NBTTagString(mc.thePlayer.getCommandSenderName()));
    stack.setTagInfo("title", new NBTTagString("Doop Book 69420"));
    stack.func_150996_a(Items.written_book);

    if (stack.hasTagCompound()) {
      NBTTagCompound compound = stack.getTagCompound();
      compound.setTag("pages", tagList);
    } else {
      stack.setTagInfo("pages", tagList);
    }

    ByteBuf buffer = Unpooled.buffer();
    boolean wrote = true;

    try {
      (new PacketBuffer(buffer)).writeItemStackToBuffer(stack);
      mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("MC|BSign", buffer));
    } catch (Exception e) {
      wrote = false;
      e.printStackTrace();
    }
    buffer.release();

    if (!wrote) {
      Printer.print("Could not write to book");
      return CommandResults.SUCCESS_NO_RESPONSE;
    }

    return CommandResults.SUCCESS;
  }
}

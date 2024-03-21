package net.minecraft.client.gui;

import nebula.client.util.render.animation.Animation;
import nebula.client.util.render.animation.Easing;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class ChatLine
{

    private static final DateFormat FORMAT = new SimpleDateFormat("hh:mm");

    /** GUI Update Counter value this Line was created at */
    private final int updateCounterCreated;
    private final IChatComponent lineString;
    private IChatComponent formatted;

    /**
     * int value to refer to existing Chat Lines, can be 0 which means unreferrable
     */
    private final int chatLineID;

    private final long creation;

    private final Animation animation = new Animation(
      Easing.CUBIC_IN_OUT, 250, true);

    public ChatLine(int p_i45000_1_, IChatComponent p_i45000_2_, int p_i45000_3_)
    {
        this.updateCounterCreated = p_i45000_1_;
        this.lineString = p_i45000_2_;
        this.chatLineID = p_i45000_3_;

        creation = System.currentTimeMillis();

        formatted = new ChatComponentText(
          EnumChatFormatting.GRAY + "[" + FORMAT.format(creation) + "] " + EnumChatFormatting.RESET
        ).appendSibling(p_i45000_2_);
    }

    public IChatComponent getLineString()
    {
        return this.lineString;
    }

    public IChatComponent getFormatted() {
        return formatted;
    }

    public int getUpdatedCounter()
    {
        return this.updateCounterCreated;
    }

    public int getChatLineID()
    {
        return this.chatLineID;
    }

    public long getCreation() {
        return creation;
    }

    public Animation getAnimation() {
        return animation;
    }
}

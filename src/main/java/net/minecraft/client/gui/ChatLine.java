package net.minecraft.client.gui;

import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import us.nebula.api.gui.animation.Animation;
import us.nebula.api.gui.animation.AnimationEasing;
import us.nebula.impl.cheat.render.ChatModifierCheat;

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

    private Animation animation;
    private long creationTimeMS;

    public ChatLine(int counter, IChatComponent component, int id)
    {
        this.updateCounterCreated = counter;
        this.lineString = component;
        this.chatLineID = id;

        if (ChatModifierCheat.INSTANCE.isToggled())
        {
            animation = new Animation(AnimationEasing.CUBIC_IN_OUT,
                    200 * ChatModifierCheat.INSTANCE.animateSpeed.getValue());
            creationTimeMS = System.currentTimeMillis();
            formatted = new ChatComponentText(EnumChatFormatting.GRAY
                    + "[" + FORMAT.format(creationTimeMS) + "] "
                    + EnumChatFormatting.RESET)
                        .appendSibling(component);
        }
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

    public long getCreationTimeMS() {
        return creationTimeMS;
    }

    public Animation getAnimation()
    {
        return animation;
    }
}

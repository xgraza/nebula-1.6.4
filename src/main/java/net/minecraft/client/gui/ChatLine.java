/*
 * Copyright (c) xgraza 2025
 */

package net.minecraft.client.gui;

import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StringUtils;
import us.nebula.client.api.gui.animation.Animation;
import us.nebula.client.api.gui.animation.AnimationEasing;
import us.nebula.client.impl.cheat.render.ChatModifierCheat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatLine
{
    private static final DateFormat FORMAT = new SimpleDateFormat("hh:mm");
    public static final Pattern PLAYER_TAG_REGEX = Pattern.compile("<(.+)>\\s");

    /**
     * GUI Update Counter value this Line was created at
     */
    private final int updateCounterCreated;
    private final IChatComponent lineString;

    /**
     * int value to refer to existing Chat Lines, can be 0 which means unreferrable
     */
    private final int chatLineID;

    private String parsedUsername;
    private final Animation animation = new Animation(AnimationEasing.CUBIC_IN_OUT, 0);;

    public ChatLine(int counter, IChatComponent component, int id)
    {
        parseUsername(component);
        final long creationTimeMS = System.currentTimeMillis();
        if (ChatModifierCheat.INSTANCE.isToggled())
        {
            if (ChatModifierCheat.INSTANCE.timestampSetting.getValue())
            {
                component = new ChatComponentText(EnumChatFormatting.GRAY
                        + "[" + FORMAT.format(creationTimeMS) + "] "
                        + EnumChatFormatting.RESET)
                        .appendSibling(component);
            }
        }
        this.updateCounterCreated = counter;
        this.lineString = component;
        this.chatLineID = id;
    }

    private void parseUsername(final IChatComponent component)
    {
        final String raw = StringUtils.stripControlCodes(
                component.getUnformattedText());
        if (!raw.startsWith("<"))
        {
            return;
        }
        final Matcher matcher = PLAYER_TAG_REGEX.matcher(raw);
        if (matcher.find())
        {
            parsedUsername = matcher.group(1);
        }
    }

    public IChatComponent getLineString()
    {
        return this.lineString;
    }

    public int getUpdatedCounter()
    {
        return this.updateCounterCreated;
    }

    public int getChatLineID()
    {
        return this.chatLineID;
    }

    public String getParsedUsername()
    {
        return parsedUsername;
    }

    public Animation getAnimation()
    {
        return animation;
    }
}

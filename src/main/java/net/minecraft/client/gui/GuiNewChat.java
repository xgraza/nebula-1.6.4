/*
 * Copyright (c) xgraza 2025
 */

package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import us.nebula.client.Nebula;
import us.nebula.client.cheat.impl.render.ChatModifierCheat;
import us.nebula.client.util.render.HeadDownloader;
import us.nebula.client.util.render.RenderUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class GuiNewChat extends Gui
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int ELEMENT_HEIGHT = 9;

    private final Minecraft mc;
    private final List<String> sentMessages = new ArrayList<>();
    private final List<ChatLine> chatLines = new ArrayList<>();
    private final List<ChatLine> chatLineList = new ArrayList<>();
    private int scrollOffset;
    private boolean field_146251_k;

    public GuiNewChat(Minecraft par1Minecraft)
    {
        this.mc = par1Minecraft;
    }

    public void drawChat(int updateCounter)
    {
        if (ChatModifierCheat.INSTANCE.isToggled())
        {
            drawNebulaChat(updateCounter);
            return;
        }
        if (this.mc.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN)
        {
            int var2 = this.getHeightPerElement();
            boolean chatOpen = false;
            int var4 = 0;
            int size = this.chatLineList.size();
            float chatAlpha = this.mc.gameSettings.chatOpacity * 0.9F + 0.1F;

            if (size > 0)
            {
                if (isChatOpen())
                {
                    chatOpen = true;
                }

                float var7 = this.getChatScale();
                int var8 = MathHelper.ceiling_float_int((float) this.getChatWidth() / var7);
                GL11.glPushMatrix();
                GL11.glTranslatef(2.0F, 20.0F, 0.0F);
                GL11.glScalef(var7, var7, 1.0F);
                int i;
                int var11;
                int var14;

                for (i = 0; i + this.scrollOffset < this.chatLineList.size() && i < var2; ++i)
                {
                    ChatLine chatLine = this.chatLineList.get(i + this.scrollOffset);

                    if (chatLine != null)
                    {
                        var11 = updateCounter - chatLine.getUpdatedCounter();

                        if (var11 < 200 || chatOpen)
                        {
                            var14 = getOpacity(var11, chatAlpha);
                            ++var4;

                            if (var14 > 3)
                            {
                                int y = -i * mc.fontRenderer.FONT_HEIGHT;
                                drawRect(0, y - mc.fontRenderer.FONT_HEIGHT, var8 + 4, y, var14 / 2 << 24);
                                String var17 = chatLine.getLineString().getFormattedText();
                                this.mc.fontRenderer.drawStringWithShadow(var17, 0, y - 8, 16777215 + (var14 << 24));
                                GL11.glDisable(GL11.GL_ALPHA_TEST);
                            }
                        }
                    }
                }

                if (chatOpen)
                {
                    i = this.mc.fontRenderer.FONT_HEIGHT;
                    GL11.glTranslatef(-3.0F, 0.0F, 0.0F);
                    int var18 = size * i + size;
                    var11 = var4 * i + var4;
                    int var19 = this.scrollOffset * var11 / size;
                    int var13 = var11 * var11 / var18;

                    if (var18 != var11)
                    {
                        var14 = var19 > 0 ? 170 : 96;
                        int var20 = this.field_146251_k ? 13382451 : 3355562;
                        drawRect(0, -var19, 2, -var19 - var13, var20 + (var14 << 24));
                        drawRect(2, -var19, 1, -var19 - var13, 13421772 + (var14 << 24));
                    }
                }

                GL11.glPopMatrix();
            }
        }
    }

    private void drawNebulaChat(int updateCounter)
    {
        if (mc.gameSettings.chatVisibility == EntityPlayer.EnumChatVisibility.HIDDEN || chatLineList.isEmpty())
        {
            return;
        }

        final ChatModifierCheat cm = ChatModifierCheat.INSTANCE;

        glPushMatrix();

        glTranslated(2, 20, 0);

        final float chatAlpha = this.mc.gameSettings.chatOpacity * 0.9F + 0.1F;
        final int lineWidth = MathHelper.ceiling_float_int((float) this.getChatWidth() / getChatScale());

        double posY = 0.0;
        for (int i = 0; i + scrollOffset < chatLineList.size() && i < getHeightPerElement(); ++i)
        {
            final ChatLine chatLine = chatLineList.get(i + scrollOffset);
            if (chatLine == null)
            {
                continue;
            }

            int var11 = updateCounter - chatLine.getUpdatedCounter();
            chatLine.getAnimation().setAnimationTimeMS(cm.animateSpeed.getValue());
            chatLine.getAnimation().setState(var11 < 200);
            double factor = chatLine.getAnimation().getEasedFactor();
            if (!isChatOpen() && factor <= 0.0)
            {
                continue;
            }

            final boolean usesAnimation = cm.animateSpeed.getValue() > 0.0;
            final int opacity = getOpacity(var11, chatAlpha);

            String text = chatLine.getLineString().getFormattedText();

            if (cm.highlightSelfSetting.getValue())
            {
                text = text.replaceAll(mc.thePlayer.getCommandSenderName(),
                        EnumChatFormatting.NEBULA_CLIENT_COLOR
                                + mc.thePlayer.getCommandSenderName()
                                + EnumChatFormatting.RESET);
            }

            final String username = chatLine.getParsedUsername();
            if (cm.highlightFriendsSetting.getValue() && Nebula.INSTANCE.getFriendManager().isFriend(username))
            {
                text = text.replaceAll(username,
                        EnumChatFormatting.AQUA
                                + username
                                + EnumChatFormatting.RESET);
            }

            final List<String> textLines = mc.fontRenderer.listFormattedStringToWidth(text, lineWidth + 6);

            double posX = 0.0;
            if (usesAnimation && factor != 0.0)
            {
                posX = -(lineWidth + 4) * (1 - factor);
            }

            if (!cm.transparentSetting.getValue())
            {
                RenderUtil.rectangle2D(posX,
                        posY - (mc.fontRenderer.FONT_HEIGHT * textLines.size()) - 1,
                        lineWidth + 6,
                        mc.fontRenderer.FONT_HEIGHT * textLines.size(),
                        usesAnimation ? ((int) (255 * chatAlpha) / 2 << 24) : opacity / 2 << 24);
            }

            for (int j = textLines.size() - 1; j >= 0; --j)
            {
                if (j == 0 && cm.playerHeadsSetting.getValue() && username != null)
                {
                    posX += drawPlayerHead(username, posX, posY);
                }
                posY -= mc.fontRenderer.FONT_HEIGHT;
                mc.fontRenderer.drawStringWithShadow(textLines.get(j), (int) posX, (int) posY, 16777215);
            }
        }

        glPopMatrix();
    }

    private int getOpacity(int updates, float chatAlpha)
    {
        double var12 = (double) updates / 200.0D;
        var12 = 1.0D - var12;
        var12 *= 10.0D;

        if (var12 < 0.0D)
        {
            var12 = 0.0D;
        }

        if (var12 > 1.0D)
        {
            var12 = 1.0D;
        }

        var12 *= var12;
        int var14 = (int) (255.0D * var12);

        if (isChatOpen())
        {
            var14 = 255;
        }

        return (int) ((float) var14 * chatAlpha);
    }

    private int drawPlayerHead(final String username, final double x, final double y)
    {
        final int headSize = ELEMENT_HEIGHT - 2;
        final DynamicTexture texture = HeadDownloader.getOrDownloadTexture(username, headSize);
        if (texture == null)
        {
            return 0;
        }

        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        glBindTexture(GL_TEXTURE_2D, texture.getGlTextureId());
        glPushMatrix();
        glBegin(GL_QUADS);
        {
            glTexCoord2d(0, 0);
            glVertex2d(x + 1, y - ELEMENT_HEIGHT + 0.5);

            glTexCoord2d(0, 1);
            glVertex2d(x + 1, y - ELEMENT_HEIGHT + headSize + 0.5);

            glTexCoord2d(1, 1);
            glVertex2d(x + headSize + 1, y - ELEMENT_HEIGHT + headSize + 0.5);

            glTexCoord2d(1, 0);
            glVertex2d(x + headSize + 1, y - ELEMENT_HEIGHT + 0.5);
        }
        glEnd();
        glPopMatrix();
        return headSize + 3;
    }

    public void clearChatMessages()
    {
        if (ChatModifierCheat.INSTANCE.isToggled()
                && ChatModifierCheat.INSTANCE.infiniteChatSetting.getValue())
        {
            return;
        }
        this.chatLineList.clear();
        this.chatLines.clear();
        this.sentMessages.clear();
    }

    public void printChatMessage(IChatComponent component)
    {
        this.printChatMessageWithOptionalDeletion(component, 0);
    }

    public void printChatMessageWithOptionalDeletion(IChatComponent component, int id)
    {
        fuckMojang_addChatLine(component, id, mc.ingameGUI.getUpdateCounter(), false);
        LOGGER.info("[CHAT] {}", component.getUnformattedText());
    }

    private String getTextWithSettingsChatColors(String text)
    {
        return Minecraft.getMinecraft().gameSettings.chatColours
                ? text
                : EnumChatFormatting.getTextWithoutFormattingCodes(text);
    }

    private void fuckMojang_addChatLine(final IChatComponent component,
                                        final int id,
                                        final int updateCounter,
                                        final boolean bl)
    {
        if (id != 0)
        {
            deleteChatLine(id);
        }
        chatLineList.add(0, new ChatLine(updateCounter, component, id));
        while (this.chatLineList.size() > 100)
        {
            this.chatLineList.remove(this.chatLineList.size() - 1);
        }
        while (this.chatLines.size() > 100)
        {
            this.chatLines.remove(this.chatLines.size() - 1);
        }
    }

    private void addChatLine(IChatComponent original, int id, int updateCounter, boolean p_146237_4_)
    {
        if (id != 0)
        {
            this.deleteChatLine(id);
        }

        int scaledChatWidth = MathHelper.floor_float((float) this.getChatWidth() / this.getChatScale());
        int var6 = 0;
        ChatComponentText base = new ChatComponentText("");
        ArrayList var8 = Lists.newArrayList();
        List<IChatComponent> components = Lists.newArrayList(original);

        for (int i = 0; i < components.size(); ++i)
        {
            IChatComponent c = components.get(i);
            String text = this.getTextWithSettingsChatColors(
                    c.getChatStyle().getFormattingCode() + c.getUnformattedTextForChat());
            int textWidth = this.mc.fontRenderer.getStringWidth(text);
            ChatComponentText component = new ChatComponentText(text);
            component.setChatStyle(c.getChatStyle().createShallowCopy());
            boolean var15 = false;

            if (var6 + textWidth > scaledChatWidth)
            {
                String var16 = this.mc.fontRenderer.trimStringToWidth(text, scaledChatWidth - var6, false);
                String var17 = var16.length() < text.length() ? text.substring(var16.length()) : null;

                if (var17 != null && !var17.isEmpty())
                {
                    int var18 = var16.lastIndexOf(" ");

                    if (var18 >= 0 && this.mc.fontRenderer.getStringWidth(text.substring(0, var18)) > 0)
                    {
                        var16 = text.substring(0, var18);
                        var17 = text.substring(var18);
                    }

                    ChatComponentText var19 = new ChatComponentText(var17);
                    var19.setChatStyle(c.getChatStyle().createShallowCopy());
                    components.add(i + 1, var19);
                }

                textWidth = this.mc.fontRenderer.getStringWidth(var16);
                component = new ChatComponentText(var16);
                component.setChatStyle(c.getChatStyle().createShallowCopy());
                var15 = true;
            }

            if (var6 + textWidth <= scaledChatWidth)
            {
                var6 += textWidth;
                base.appendSibling(component);
            } else
            {
                var15 = true;
            }

            if (var15)
            {
                var8.add(base);
                var6 = 0;
                base = new ChatComponentText("");
            }
        }

        var8.add(base);
        boolean var20 = this.isChatOpen();
        IChatComponent var22;

        for (Iterator var21 = var8.iterator(); var21.hasNext(); this.chatLineList.add(0, new ChatLine(updateCounter, var22, id)))
        {
            var22 = (IChatComponent) var21.next();

            if (var20 && this.scrollOffset > 0)
            {
                this.field_146251_k = true;
                this.scroll(1);
            }
        }

        while (this.chatLineList.size() > 100)
        {
            this.chatLineList.remove(this.chatLineList.size() - 1);
        }

        if (!p_146237_4_)
        {
            this.chatLines.add(0, new ChatLine(updateCounter, original, id));

            while (this.chatLines.size() > 100)
            {
                this.chatLines.remove(this.chatLines.size() - 1);
            }
        }
    }

    public void refreshChat()
    {
        this.chatLineList.clear();
        this.resetScroll();

        for (int var1 = this.chatLines.size() - 1; var1 >= 0; --var1)
        {
            ChatLine var2 = (ChatLine) this.chatLines.get(var1);
            this.fuckMojang_addChatLine(var2.getLineString(), var2.getChatLineID(), var2.getUpdatedCounter(), true);
        }
    }

    public List getSentMessages()
    {
        return this.sentMessages;
    }

    public void addToSentMessages(String p_146239_1_)
    {
        if (this.sentMessages.isEmpty() || !this.sentMessages.get(this.sentMessages.size() - 1).equals(p_146239_1_))
        {
            this.sentMessages.add(p_146239_1_);
        }
    }

    public void resetScroll()
    {
        this.scrollOffset = 0;
        this.field_146251_k = false;
    }

    public void scroll(int amount)
    {
        this.scrollOffset += amount;
        int var2 = this.chatLineList.size();

        if (this.scrollOffset > var2 - this.getHeightPerElement())
        {
            this.scrollOffset = var2 - this.getHeightPerElement();
        }

        if (this.scrollOffset <= 0)
        {
            this.scrollOffset = 0;
            this.field_146251_k = false;
        }
    }

    public IChatComponent getComponentAt(int x, int y)
    {
        if (!this.isChatOpen())
        {
            return null;
        } else
        {
            ScaledResolution res = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
            int factor = res.getScaleFactor();
            float scale = this.getChatScale();
            int var6 = x / factor - 3;
            int var7 = y / factor - 27;
            var6 = MathHelper.floor_float((float) var6 / scale);
            var7 = MathHelper.floor_float((float) var7 / scale);

            if (var6 >= 0 && var7 >= 0)
            {
                int var8 = Math.min(this.getHeightPerElement(), this.chatLineList.size());

                if (var6 <= MathHelper.floor_float((float) this.getChatWidth() / this.getChatScale()) && var7 < this.mc.fontRenderer.FONT_HEIGHT * var8 + var8)
                {
                    int var9 = var7 / this.mc.fontRenderer.FONT_HEIGHT + this.scrollOffset;

                    if (var9 >= 0 && var9 < this.chatLineList.size())
                    {
                        ChatLine var10 = this.chatLineList.get(var9);
                        int var11 = 0;

                        for (IChatComponent var13 : (Iterable<IChatComponent>) var10.getLineString()) {
                            if (var13 instanceof ChatComponentText) {
                                var11 += this.mc.fontRenderer.getStringWidth(this.getTextWithSettingsChatColors(((ChatComponentText) var13).getChatComponentText_TextValue()));

                                if (var11 > var6) {
                                    return var13;
                                }
                            }
                        }
                    }

                    return null;
                } else
                {
                    return null;
                }
            } else
            {
                return null;
            }
        }
    }

    public boolean isChatOpen()
    {
        return mc.currentScreen instanceof GuiChat;
    }

    public void deleteChatLine(int p_146242_1_)
    {
        Iterator var2 = this.chatLineList.iterator();
        ChatLine var3;

        do
        {
            if (!var2.hasNext())
            {
                var2 = this.chatLines.iterator();

                do
                {
                    if (!var2.hasNext())
                    {
                        return;
                    }

                    var3 = (ChatLine) var2.next();
                }
                while (var3.getChatLineID() != p_146242_1_);

                var2.remove();
                return;
            }

            var3 = (ChatLine) var2.next();
        }
        while (var3.getChatLineID() != p_146242_1_);

        var2.remove();
    }

    public int getChatWidth()
    {
        return func_146233_a(mc.gameSettings.chatWidth);
    }

    public int getChatHeight()
    {
        return func_146243_b(isChatOpen() ? mc.gameSettings.chatHeightFocused : mc.gameSettings.chatHeightUnfocused);
    }

    public float getChatScale()
    {
        return mc.gameSettings.chatScale;
    }

    public static int func_146233_a(float chatWidth)
    {
        short var1 = 320;
        byte var2 = 40;
        return MathHelper.floor_float(chatWidth * (float) (var1 - var2) + (float) var2);
    }

    public static int func_146243_b(float chatHeight)
    {
        short var1 = 180;
        byte var2 = 20;
        return MathHelper.floor_float(chatHeight * (float) (var1 - var2) + (float) var2);
    }

    public int getHeightPerElement()
    {
        return getChatHeight() / ELEMENT_HEIGHT;
    }
}

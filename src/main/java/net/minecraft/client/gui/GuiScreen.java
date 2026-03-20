/*
 * Copyright (c) xgraza 2025
 */

package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.lwjgl.input.Keyboard.KEY_ESCAPE;

public class GuiScreen extends Gui
{
    /**
     * Holds a instance of RenderItem, used to draw the achievement icons on screen (is based on ItemStack)
     */
    protected static final RenderItem RENDER_ITEM = new RenderItem();

    /**
     * Reference to the Minecraft object.
     */
    protected Minecraft mc;

    /**
     * The width of the screen object.
     */
    public int width;

    /**
     * The height of the screen object.
     */
    public int height;

    /**
     * A list of all the buttons in this container.
     */
    protected List<GuiButton> buttonList = new CopyOnWriteArrayList<>();

    /**
     * A list of all the labels in this container.
     */
    protected List<GuiLabel> labelList = new ArrayList<>();
    public boolean allowUserInput;

    /**
     * The FontRenderer used by GuiScreen
     */
    protected FontRenderer fontRenderer;

    /**
     * The button that was just pressed.
     */
    private GuiButton selectedButton;
    private int eventButton;
    private long lastMouseEvent;
    private int field_146298_h;

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        int var4;

        for (var4 = 0; var4 < this.buttonList.size(); ++var4)
        {
            this.buttonList.get(var4).drawButton(this.mc, par1, par2);
        }

        for (var4 = 0; var4 < this.labelList.size(); ++var4)
        {
            this.labelList.get(var4).drawLabel(this.mc, par1, par2);
        }
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char typedChar, int keyCode)
    {
        if (keyCode == KEY_ESCAPE)
        {
            this.mc.displayGuiScreen(null);
            this.mc.setIngameFocus();
        }
    }

    /**
     * Returns a string stored in the system clipboard.
     */
    public static String getClipboardString()
    {
        try
        {
            final Transferable contents = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
            if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor))
            {
                return (String) contents.getTransferData(DataFlavor.stringFlavor);
            }
        } catch (Exception ignored)
        {

        }
        return "";
    }

    /**
     * Stores the given string in the system clipboard
     */
    public static void setClipboardString(final String text)
    {
        try
        {
            final StringSelection selection = new StringSelection(text);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
        } catch (Exception ignored)
        {

        }
    }

    protected void renderItem(ItemStack itemStack, int x, int y)
    {
        final List<String> tooltipList = itemStack.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips);
        tooltipList.add("Size: " + itemStack.getNBTSize());

        for (int i = 0; i < tooltipList.size(); ++i)
        {
            if (i == 0)
            {
                tooltipList.set(i, itemStack.getRarity().rarityColor + tooltipList.get(i));
            } else
            {
                tooltipList.set(i, EnumChatFormatting.GRAY + tooltipList.get(i));
            }
        }

        this.renderTextList(tooltipList, x, y);
    }

    protected void renderText(String p_146279_1_, int p_146279_2_, int p_146279_3_)
    {
        this.renderTextList(Collections.singletonList(p_146279_1_), p_146279_2_, p_146279_3_);
    }

    protected void renderTextList(List<String> textList, int x, int y)
    {
        if (!textList.isEmpty())
        {
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);

            int maxWidth = 0;
            for (final String var6 : textList)
            {
                int var7 = this.fontRenderer.getStringWidth(var6);

                if (var7 > maxWidth)
                {
                    maxWidth = var7;
                }
            }

            int var14 = x + 12;
            int var15 = y - 12;
            int var8 = 8;

            if (textList.size() > 1)
            {
                var8 += 2 + (textList.size() - 1) * 10;
            }

            if (var14 + maxWidth > this.width)
            {
                var14 -= 28 + maxWidth;
            }

            if (var15 + var8 + 6 > this.height)
            {
                var15 = this.height - var8 - 6;
            }

            this.zLevel = 300.0F;
            RENDER_ITEM.zLevel = 300.0F;
            int var9 = -267386864;
            this.drawGradientRect(var14 - 3, var15 - 4, var14 + maxWidth + 3, var15 - 3, var9, var9);
            this.drawGradientRect(var14 - 3, var15 + var8 + 3, var14 + maxWidth + 3, var15 + var8 + 4, var9, var9);
            this.drawGradientRect(var14 - 3, var15 - 3, var14 + maxWidth + 3, var15 + var8 + 3, var9, var9);
            this.drawGradientRect(var14 - 4, var15 - 3, var14 - 3, var15 + var8 + 3, var9, var9);
            this.drawGradientRect(var14 + maxWidth + 3, var15 - 3, var14 + maxWidth + 4, var15 + var8 + 3, var9, var9);
            int var10 = 1347420415;
            int var11 = (var10 & 16711422) >> 1 | var10 & -16777216;
            this.drawGradientRect(var14 - 3, var15 - 3 + 1, var14 - 3 + 1, var15 + var8 + 3 - 1, var10, var11);
            this.drawGradientRect(var14 + maxWidth + 2, var15 - 3 + 1, var14 + maxWidth + 3, var15 + var8 + 3 - 1, var10, var11);
            this.drawGradientRect(var14 - 3, var15 - 3, var14 + maxWidth + 3, var15 - 3 + 1, var10, var10);
            this.drawGradientRect(var14 - 3, var15 + var8 + 2, var14 + maxWidth + 3, var15 + var8 + 3, var11, var11);

            for (int var12 = 0; var12 < textList.size(); ++var12)
            {
                String var13 = textList.get(var12);
                this.fontRenderer.drawStringWithShadow(var13, var14, var15, -1);

                if (var12 == 0)
                {
                    var15 += 2;
                }

                var15 += 10;
            }

            this.zLevel = 0.0F;
            RENDER_ITEM.zLevel = 0.0F;
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            RenderHelper.enableStandardItemLighting();
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        }
    }

    /**
     * Called when the mouse is clicked.
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (mouseButton == 0)
        {
            for (GuiButton button : buttonList)
            {
                if (button.mousePressed(mc, mouseX, mouseY))
                {
                    this.selectedButton = button;
                    button.playClickSound(mc.getSoundHandler());
                    this.actionPerformed(button);
                }
            }
        }
    }

    protected void mouseMovedOrUp(int p_146286_1_, int p_146286_2_, int p_146286_3_)
    {
        if (this.selectedButton != null && p_146286_3_ == 0)
        {
            this.selectedButton.mouseReleased(p_146286_1_, p_146286_2_);
            this.selectedButton = null;
        }
    }

    protected void mouseClickMove(int p_146273_1_, int p_146273_2_, int p_146273_3_, long p_146273_4_)
    {
    }

    protected void actionPerformed(GuiButton p_146284_1_)
    {
    }

    /**
     * Causes the screen to lay out its subcomponents again. This is the equivalent of the Java call
     * Container.validate()
     */
    public void setWorldAndResolution(Minecraft p_146280_1_, int p_146280_2_, int p_146280_3_)
    {
        this.mc = p_146280_1_;
        this.fontRenderer = p_146280_1_.fontRenderer;
        this.width = p_146280_2_;
        this.height = p_146280_3_;
        this.buttonList.clear();
        this.initGui();
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
    }

    /**
     * Delegates mouse and keyboard input.
     */
    public void handleInput()
    {
        if (Mouse.isCreated())
        {
            while (Mouse.next())
            {
                this.handleMouseInput();
            }
        }

        if (Keyboard.isCreated())
        {
            while (Keyboard.next())
            {
                this.handleKeyboardInput();
            }
        }
    }

    /**
     * Handles mouse input.
     */
    public void handleMouseInput()
    {
        int var1 = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int var2 = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        int var3 = Mouse.getEventButton();

        if (Minecraft.IS_ON_MAC && var3 == 0 && (Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157)))
        {
            var3 = 1;
        }

        if (Mouse.getEventButtonState())
        {
            if (this.mc.gameSettings.touchscreen && this.field_146298_h++ > 0)
            {
                return;
            }

            this.eventButton = var3;
            this.lastMouseEvent = Minecraft.getSystemTime();
            this.mouseClicked(var1, var2, this.eventButton);
        } else if (var3 != -1)
        {
            if (this.mc.gameSettings.touchscreen && --this.field_146298_h > 0)
            {
                return;
            }

            this.eventButton = -1;
            this.mouseMovedOrUp(var1, var2, var3);
        } else if (this.eventButton != -1 && this.lastMouseEvent > 0L)
        {
            long var4 = Minecraft.getSystemTime() - this.lastMouseEvent;
            this.mouseClickMove(var1, var2, this.eventButton, var4);
        }
    }

    /**
     * Handles keyboard input.
     */
    public void handleKeyboardInput()
    {
        if (Keyboard.getEventKeyState())
        {
            int var1 = Keyboard.getEventKey();
            char var2 = Keyboard.getEventCharacter();

            if (var1 == 87)
            {
                this.mc.toggleFullscreen();
                return;
            }

            this.keyTyped(var2, var1);
        }
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
    }

    /**
     * "Called when the screen is unloaded. Used to disable keyboard repeat events."
     */
    public void onGuiClosed()
    {
    }

    public void drawDefaultBackground()
    {
        this.drawWorldBackground(0);
    }

    public void drawWorldBackground(int p_146270_1_)
    {
        if (this.mc.theWorld != null)
        {
            this.drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);
        } else
        {
            this.drawBackground(p_146270_1_);
        }
    }

    public void drawBackground(int p_146278_1_)
    {
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_FOG);
        Tessellator var2 = Tessellator.instance;
        this.mc.getTextureManager().bindTexture(optionsBackground);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        float var3 = 32.0F;
        var2.startDrawingQuads();
        var2.setColorOpaque_I(4210752);
        var2.addVertexWithUV(0.0D, this.height, 0.0D, 0.0D, (float) this.height / var3 + (float) p_146278_1_);
        var2.addVertexWithUV(this.width, this.height, 0.0D, (float) this.width / var3, (float) this.height / var3 + (float) p_146278_1_);
        var2.addVertexWithUV(this.width, 0.0D, 0.0D, (float) this.width / var3, p_146278_1_);
        var2.addVertexWithUV(0.0D, 0.0D, 0.0D, 0.0D, p_146278_1_);
        var2.draw();
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    public boolean doesGuiPauseGame()
    {
        return true;
    }

    public void confirmClicked(boolean par1, int par2)
    {
    }

    /**
     * Returns true if either windows ctrl key is down or if either mac meta key is down
     */
    public static boolean isCtrlKeyDown()
    {
        return Minecraft.IS_ON_MAC ? Keyboard.isKeyDown(219) || Keyboard.isKeyDown(220) : Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157);
    }

    /**
     * Returns true if either shift key is down
     */
    public static boolean isShiftKeyDown()
    {
        return Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);
    }
}

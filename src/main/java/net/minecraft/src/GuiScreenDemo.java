package net.minecraft.src;

import java.net.URI;
import org.lwjgl.opengl.GL11;

public class GuiScreenDemo extends GuiScreen
{
    private static final ResourceLocation field_110407_a = new ResourceLocation("textures/gui/demo_background.png");

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        this.buttonList.clear();
        byte var1 = -16;
        this.buttonList.add(new GuiButton(1, this.width / 2 - 116, this.height / 2 + 62 + var1, 114, 20, I18n.getString("demo.help.buy")));
        this.buttonList.add(new GuiButton(2, this.width / 2 + 2, this.height / 2 + 62 + var1, 114, 20, I18n.getString("demo.help.later")));
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        switch (par1GuiButton.id)
        {
            case 1:
                par1GuiButton.enabled = false;

                try
                {
                    Class var2 = Class.forName("java.awt.Desktop");
                    Object var3 = var2.getMethod("getDesktop", new Class[0]).invoke((Object)null, new Object[0]);
                    var2.getMethod("browse", new Class[] {URI.class}).invoke(var3, new Object[] {new URI("http://www.minecraft.net/store?source=demo")});
                }
                catch (Throwable var4)
                {
                    var4.printStackTrace();
                }

                break;

            case 2:
                this.mc.displayGuiScreen((GuiScreen)null);
                this.mc.setIngameFocus();
        }
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        super.updateScreen();
    }

    /**
     * Draws either a gradient over the background screen (when it exists) or a flat gradient over background.png
     */
    public void drawDefaultBackground()
    {
        super.drawDefaultBackground();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(field_110407_a);
        int var1 = (this.width - 248) / 2;
        int var2 = (this.height - 166) / 2;
        this.drawTexturedModalRect(var1, var2, 0, 0, 248, 166);
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        this.drawDefaultBackground();
        int var4 = (this.width - 248) / 2 + 10;
        int var5 = (this.height - 166) / 2 + 8;
        this.fontRenderer.drawString(I18n.getString("demo.help.title"), var4, var5, 2039583);
        var5 += 12;
        GameSettings var6 = this.mc.gameSettings;
        this.fontRenderer.drawString(I18n.getStringParams("demo.help.movementShort", new Object[] {GameSettings.getKeyDisplayString(var6.keyBindForward.keyCode), GameSettings.getKeyDisplayString(var6.keyBindLeft.keyCode), GameSettings.getKeyDisplayString(var6.keyBindBack.keyCode), GameSettings.getKeyDisplayString(var6.keyBindRight.keyCode)}), var4, var5, 5197647);
        this.fontRenderer.drawString(I18n.getString("demo.help.movementMouse"), var4, var5 + 12, 5197647);
        this.fontRenderer.drawString(I18n.getStringParams("demo.help.jump", new Object[] {GameSettings.getKeyDisplayString(var6.keyBindJump.keyCode)}), var4, var5 + 24, 5197647);
        this.fontRenderer.drawString(I18n.getStringParams("demo.help.inventory", new Object[] {GameSettings.getKeyDisplayString(var6.keyBindInventory.keyCode)}), var4, var5 + 36, 5197647);
        this.fontRenderer.drawSplitString(I18n.getString("demo.help.fullWrapped"), var4, var5 + 68, 218, 2039583);
        super.drawScreen(par1, par2, par3);
    }
}

package net.minecraft.src;

import org.lwjgl.input.Keyboard;

public class GuiScreenResetWorld extends ScreenWithCallback
{
    private GuiScreen field_96152_a;
    private McoServer field_96150_b;
    private GuiTextField field_96151_c;
    private final int field_96149_d = 1;
    private final int field_96153_n = 2;
    private static int field_110360_p = 3;
    private WorldTemplate field_110359_q;
    private GuiButton field_96154_o;

    public GuiScreenResetWorld(GuiScreen par1GuiScreen, McoServer par2McoServer)
    {
        this.field_96152_a = par1GuiScreen;
        this.field_96150_b = par2McoServer;
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        this.field_96151_c.updateCursorCounter();
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.buttonList.add(this.field_96154_o = new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + 12, 97, 20, I18n.getString("mco.configure.world.buttons.reset")));
        this.buttonList.add(new GuiButton(2, this.width / 2 + 5, this.height / 4 + 120 + 12, 97, 20, I18n.getString("gui.cancel")));
        this.field_96151_c = new GuiTextField(this.fontRenderer, this.width / 2 - 100, 99, 200, 20);
        this.field_96151_c.setFocused(true);
        this.field_96151_c.setMaxStringLength(32);
        this.field_96151_c.setText("");

        if (this.field_110359_q == null)
        {
            this.buttonList.add(new GuiButton(field_110360_p, this.width / 2 - 100, 125, 200, 20, I18n.getString("mco.template.default.name")));
        }
        else
        {
            this.field_96151_c.setText("");
            this.field_96151_c.setEnabled(false);
            this.field_96151_c.setFocused(false);
            this.buttonList.add(new GuiButton(field_110360_p, this.width / 2 - 100, 125, 200, 20, I18n.getString("mco.template.name") + ": " + this.field_110359_q.field_110732_b));
        }
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char par1, int par2)
    {
        this.field_96151_c.textboxKeyTyped(par1, par2);

        if (par2 == 28 || par2 == 156)
        {
            this.actionPerformed(this.field_96154_o);
        }
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        if (par1GuiButton.enabled)
        {
            if (par1GuiButton.id == 2)
            {
                this.mc.displayGuiScreen(this.field_96152_a);
            }
            else if (par1GuiButton.id == 1)
            {
                String var2 = I18n.getString("mco.configure.world.reset.question.line1");
                String var3 = I18n.getString("mco.configure.world.reset.question.line2");
                this.mc.displayGuiScreen(new GuiScreenConfirmation(this, GuiScreenConfirmationType.Warning, var2, var3, 1));
            }
            else if (par1GuiButton.id == field_110360_p)
            {
                this.mc.displayGuiScreen(new GuiScreenMcoWorldTemplate(this, this.field_110359_q));
            }
        }
    }

    public void confirmClicked(boolean par1, int par2)
    {
        if (par1 && par2 == 1)
        {
            this.func_140006_g();
        }
        else
        {
            this.mc.displayGuiScreen(this);
        }
    }

    private void func_140006_g()
    {
        TaskResetWorld var1 = new TaskResetWorld(this, this.field_96150_b.field_96408_a, this.field_96151_c.getText(), this.field_110359_q);
        GuiScreenLongRunningTask var2 = new GuiScreenLongRunningTask(this.mc, this.field_96152_a, var1);
        var2.func_98117_g();
        this.mc.displayGuiScreen(var2);
    }

    /**
     * Called when the mouse is clicked.
     */
    protected void mouseClicked(int par1, int par2, int par3)
    {
        super.mouseClicked(par1, par2, par3);
        this.field_96151_c.mouseClicked(par1, par2, par3);
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, I18n.getString("mco.reset.world.title"), this.width / 2, 17, 16777215);
        this.drawCenteredString(this.fontRenderer, I18n.getString("mco.reset.world.warning"), this.width / 2, 56, 16711680);
        this.drawString(this.fontRenderer, I18n.getString("mco.reset.world.seed"), this.width / 2 - 100, 86, 10526880);
        this.field_96151_c.drawTextBox();
        super.drawScreen(par1, par2, par3);
    }

    void func_110358_a(WorldTemplate par1WorldTemplate)
    {
        this.field_110359_q = par1WorldTemplate;
    }

    void func_110354_a(Object par1Obj)
    {
        this.func_110358_a((WorldTemplate)par1Obj);
    }

    static GuiScreen func_96148_a(GuiScreenResetWorld par0GuiScreenResetWorld)
    {
        return par0GuiScreenResetWorld.field_96152_a;
    }

    static Minecraft func_96147_b(GuiScreenResetWorld par0GuiScreenResetWorld)
    {
        return par0GuiScreenResetWorld.mc;
    }

    static Minecraft func_130025_c(GuiScreenResetWorld par0GuiScreenResetWorld)
    {
        return par0GuiScreenResetWorld.mc;
    }

    static Minecraft func_130024_d(GuiScreenResetWorld par0GuiScreenResetWorld)
    {
        return par0GuiScreenResetWorld.mc;
    }
}

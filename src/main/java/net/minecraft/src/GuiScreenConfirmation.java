package net.minecraft.src;

public class GuiScreenConfirmation extends GuiScreen
{
    private final GuiScreenConfirmationType field_140045_e;
    private final String field_140049_p;
    private final String field_96288_n;
    protected final GuiScreen field_140048_a;
    protected final String field_140046_b;
    protected final String field_140047_c;
    protected final int field_140044_d;

    public GuiScreenConfirmation(GuiScreen par1GuiScreen, GuiScreenConfirmationType par2GuiScreenConfirmationType, String par3Str, String par4Str, int par5)
    {
        this.field_140048_a = par1GuiScreen;
        this.field_140044_d = par5;
        this.field_140045_e = par2GuiScreenConfirmationType;
        this.field_140049_p = par3Str;
        this.field_96288_n = par4Str;
        this.field_140046_b = I18n.getString("gui.yes");
        this.field_140047_c = I18n.getString("gui.no");
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        this.buttonList.add(new GuiSmallButton(0, this.width / 2 - 155, this.height / 6 + 112, this.field_140046_b));
        this.buttonList.add(new GuiSmallButton(1, this.width / 2 - 155 + 160, this.height / 6 + 112, this.field_140047_c));
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        this.field_140048_a.confirmClicked(par1GuiButton.id == 0, this.field_140044_d);
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, this.field_140045_e.field_140072_d, this.width / 2, 70, this.field_140045_e.field_140075_c);
        this.drawCenteredString(this.fontRenderer, this.field_140049_p, this.width / 2, 90, 16777215);
        this.drawCenteredString(this.fontRenderer, this.field_96288_n, this.width / 2, 110, 16777215);
        super.drawScreen(par1, par2, par3);
    }
}

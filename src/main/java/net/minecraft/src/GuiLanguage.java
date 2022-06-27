package net.minecraft.src;

public class GuiLanguage extends GuiScreen
{
    /** This GUI's parent GUI. */
    protected GuiScreen parentGui;

    /** This GUI's language list. */
    private GuiSlotLanguage languageList;

    /** For saving the user's language selection to disk. */
    private final GameSettings theGameSettings;
    private final LanguageManager field_135014_d;

    /** This GUI's 'Done' button. */
    private GuiSmallButton doneButton;

    public GuiLanguage(GuiScreen par1GuiScreen, GameSettings par2GameSettings, LanguageManager par3LanguageManager)
    {
        this.parentGui = par1GuiScreen;
        this.theGameSettings = par2GameSettings;
        this.field_135014_d = par3LanguageManager;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        this.buttonList.add(this.doneButton = new GuiSmallButton(6, this.width / 2 - 75, this.height - 38, I18n.getString("gui.done")));
        this.languageList = new GuiSlotLanguage(this);
        this.languageList.registerScrollButtons(7, 8);
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        if (par1GuiButton.enabled)
        {
            switch (par1GuiButton.id)
            {
                case 5:
                    break;

                case 6:
                    this.mc.displayGuiScreen(this.parentGui);
                    break;

                default:
                    this.languageList.actionPerformed(par1GuiButton);
            }
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        this.languageList.drawScreen(par1, par2, par3);
        this.drawCenteredString(this.fontRenderer, I18n.getString("options.language"), this.width / 2, 16, 16777215);
        this.drawCenteredString(this.fontRenderer, "(" + I18n.getString("options.languageWarning") + ")", this.width / 2, this.height - 56, 8421504);
        super.drawScreen(par1, par2, par3);
    }

    static LanguageManager func_135011_a(GuiLanguage par0GuiLanguage)
    {
        return par0GuiLanguage.field_135014_d;
    }

    /**
     * Gets the relevant instance of GameSettings. Synthetic method for use in GuiSlotLanguage
     */
    static GameSettings getGameSettings(GuiLanguage par0GuiLanguage)
    {
        return par0GuiLanguage.theGameSettings;
    }

    /**
     * Returns the private doneButton field.
     */
    static GuiSmallButton getDoneButton(GuiLanguage par0GuiLanguage)
    {
        return par0GuiLanguage.doneButton;
    }
}

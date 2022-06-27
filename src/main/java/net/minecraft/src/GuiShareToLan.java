package net.minecraft.src;

public class GuiShareToLan extends GuiScreen
{
    /**
     * A reference to the screen object that created this. Used for navigating between screens.
     */
    private final GuiScreen parentScreen;
    private GuiButton buttonAllowCommandsToggle;
    private GuiButton buttonGameMode;

    /**
     * The currently selected game mode. One of 'survival', 'creative', or 'adventure'
     */
    private String gameMode = "survival";

    /** True if 'Allow Cheats' is currently enabled */
    private boolean allowCommands;

    public GuiShareToLan(GuiScreen par1GuiScreen)
    {
        this.parentScreen = par1GuiScreen;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(101, this.width / 2 - 155, this.height - 28, 150, 20, I18n.getString("lanServer.start")));
        this.buttonList.add(new GuiButton(102, this.width / 2 + 5, this.height - 28, 150, 20, I18n.getString("gui.cancel")));
        this.buttonList.add(this.buttonGameMode = new GuiButton(104, this.width / 2 - 155, 100, 150, 20, I18n.getString("selectWorld.gameMode")));
        this.buttonList.add(this.buttonAllowCommandsToggle = new GuiButton(103, this.width / 2 + 5, 100, 150, 20, I18n.getString("selectWorld.allowCommands")));
        this.func_74088_g();
    }

    private void func_74088_g()
    {
        this.buttonGameMode.displayString = I18n.getString("selectWorld.gameMode") + " " + I18n.getString("selectWorld.gameMode." + this.gameMode);
        this.buttonAllowCommandsToggle.displayString = I18n.getString("selectWorld.allowCommands") + " ";

        if (this.allowCommands)
        {
            this.buttonAllowCommandsToggle.displayString = this.buttonAllowCommandsToggle.displayString + I18n.getString("options.on");
        }
        else
        {
            this.buttonAllowCommandsToggle.displayString = this.buttonAllowCommandsToggle.displayString + I18n.getString("options.off");
        }
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        if (par1GuiButton.id == 102)
        {
            this.mc.displayGuiScreen(this.parentScreen);
        }
        else if (par1GuiButton.id == 104)
        {
            if (this.gameMode.equals("survival"))
            {
                this.gameMode = "creative";
            }
            else if (this.gameMode.equals("creative"))
            {
                this.gameMode = "adventure";
            }
            else
            {
                this.gameMode = "survival";
            }

            this.func_74088_g();
        }
        else if (par1GuiButton.id == 103)
        {
            this.allowCommands = !this.allowCommands;
            this.func_74088_g();
        }
        else if (par1GuiButton.id == 101)
        {
            this.mc.displayGuiScreen((GuiScreen)null);
            String var2 = this.mc.getIntegratedServer().shareToLAN(EnumGameType.getByName(this.gameMode), this.allowCommands);
            ChatMessageComponent var3;

            if (var2 != null)
            {
                var3 = ChatMessageComponent.createFromTranslationWithSubstitutions("commands.publish.started", new Object[] {var2});
            }
            else
            {
                var3 = ChatMessageComponent.createFromText("commands.publish.failed");
            }

            this.mc.ingameGUI.getChatGUI().printChatMessage(var3.toStringWithFormatting(true));
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, I18n.getString("lanServer.title"), this.width / 2, 50, 16777215);
        this.drawCenteredString(this.fontRenderer, I18n.getString("lanServer.otherPlayers"), this.width / 2, 82, 16777215);
        super.drawScreen(par1, par2, par3);
    }
}

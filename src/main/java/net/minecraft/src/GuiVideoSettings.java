package net.minecraft.src;

public class GuiVideoSettings extends GuiScreen
{
    private GuiScreen parentGuiScreen;

    /** The title string that is displayed in the top-center of the screen. */
    protected String screenTitle = "Video Settings";

    /** GUI game settings */
    private GameSettings guiGameSettings;

    /**
     * True if the system is 64-bit (using a simple indexOf test on a system property)
     */
    private boolean is64bit;

    /** An array of all of EnumOption's video options. */
    private static EnumOptions[] videoOptions = new EnumOptions[] {EnumOptions.GRAPHICS, EnumOptions.RENDER_DISTANCE, EnumOptions.AMBIENT_OCCLUSION, EnumOptions.FRAMERATE_LIMIT, EnumOptions.ANAGLYPH, EnumOptions.VIEW_BOBBING, EnumOptions.GUI_SCALE, EnumOptions.ADVANCED_OPENGL, EnumOptions.GAMMA, EnumOptions.RENDER_CLOUDS, EnumOptions.PARTICLES, EnumOptions.USE_SERVER_TEXTURES, EnumOptions.USE_FULLSCREEN, EnumOptions.ENABLE_VSYNC};

    public GuiVideoSettings(GuiScreen par1GuiScreen, GameSettings par2GameSettings)
    {
        this.parentGuiScreen = par1GuiScreen;
        this.guiGameSettings = par2GameSettings;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        this.screenTitle = I18n.getString("options.videoTitle");
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168, I18n.getString("gui.done")));
        this.is64bit = false;
        String[] var1 = new String[] {"sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch"};
        String[] var2 = var1;
        int var3 = var1.length;

        for (int var4 = 0; var4 < var3; ++var4)
        {
            String var5 = var2[var4];
            String var6 = System.getProperty(var5);

            if (var6 != null && var6.contains("64"))
            {
                this.is64bit = true;
                break;
            }
        }

        int var8 = 0;
        var3 = this.is64bit ? 0 : -15;
        EnumOptions[] var9 = videoOptions;
        int var10 = var9.length;

        for (int var11 = 0; var11 < var10; ++var11)
        {
            EnumOptions var7 = var9[var11];

            if (var7.getEnumFloat())
            {
                this.buttonList.add(new GuiSlider(var7.returnEnumOrdinal(), this.width / 2 - 155 + var8 % 2 * 160, this.height / 7 + var3 + 24 * (var8 >> 1), var7, this.guiGameSettings.getKeyBinding(var7), this.guiGameSettings.getOptionFloatValue(var7)));
            }
            else
            {
                this.buttonList.add(new GuiSmallButton(var7.returnEnumOrdinal(), this.width / 2 - 155 + var8 % 2 * 160, this.height / 7 + var3 + 24 * (var8 >> 1), var7, this.guiGameSettings.getKeyBinding(var7)));
            }

            ++var8;
        }
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        if (par1GuiButton.enabled)
        {
            int var2 = this.guiGameSettings.guiScale;

            if (par1GuiButton.id < 100 && par1GuiButton instanceof GuiSmallButton)
            {
                this.guiGameSettings.setOptionValue(((GuiSmallButton)par1GuiButton).returnEnumOptions(), 1);
                par1GuiButton.displayString = this.guiGameSettings.getKeyBinding(EnumOptions.getEnumOptions(par1GuiButton.id));
            }

            if (par1GuiButton.id == 200)
            {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(this.parentGuiScreen);
            }

            if (this.guiGameSettings.guiScale != var2)
            {
                ScaledResolution var3 = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
                int var4 = var3.getScaledWidth();
                int var5 = var3.getScaledHeight();
                this.setWorldAndResolution(this.mc, var4, var5);
            }
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, this.screenTitle, this.width / 2, this.is64bit ? 20 : 5, 16777215);

        if (!this.is64bit && this.guiGameSettings.renderDistance == 0)
        {
            this.drawCenteredString(this.fontRenderer, I18n.getString("options.farWarning1"), this.width / 2, this.height / 6 + 144 + 1, 11468800);
            this.drawCenteredString(this.fontRenderer, I18n.getString("options.farWarning2"), this.width / 2, this.height / 6 + 144 + 13, 11468800);
        }

        super.drawScreen(par1, par2, par3);
    }
}

package net.minecraft.src;

public class GuiAnimationSettingsOF extends GuiScreen
{
    private GuiScreen prevScreen;
    protected String title = "Animation Settings";
    private GameSettings settings;
    private static EnumOptions[] enumOptions = new EnumOptions[] {EnumOptions.ANIMATED_WATER, EnumOptions.ANIMATED_LAVA, EnumOptions.ANIMATED_FIRE, EnumOptions.ANIMATED_PORTAL, EnumOptions.ANIMATED_REDSTONE, EnumOptions.ANIMATED_EXPLOSION, EnumOptions.ANIMATED_FLAME, EnumOptions.ANIMATED_SMOKE, EnumOptions.VOID_PARTICLES, EnumOptions.WATER_PARTICLES, EnumOptions.RAIN_SPLASH, EnumOptions.PORTAL_PARTICLES, EnumOptions.POTION_PARTICLES, EnumOptions.DRIPPING_WATER_LAVA, EnumOptions.ANIMATED_TERRAIN, EnumOptions.ANIMATED_ITEMS, EnumOptions.ANIMATED_TEXTURES, EnumOptions.PARTICLES};

    public GuiAnimationSettingsOF(GuiScreen guiscreen, GameSettings gamesettings)
    {
        this.prevScreen = guiscreen;
        this.settings = gamesettings;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        StringTranslate stringtranslate = StringTranslate.getInstance();
        int i = 0;
        EnumOptions[] aenumoptions = enumOptions;
        int j = aenumoptions.length;

        for (int k = 0; k < j; ++k)
        {
            EnumOptions enumoptions = aenumoptions[k];
            int x = this.width / 2 - 155 + i % 2 * 160;
            int y = this.height / 6 + 21 * (i / 2) - 10;

            if (!enumoptions.getEnumFloat())
            {
                this.buttonList.add(new GuiSmallButton(enumoptions.returnEnumOrdinal(), x, y, enumoptions, this.settings.getKeyBinding(enumoptions)));
            }
            else
            {
                this.buttonList.add(new GuiSlider(enumoptions.returnEnumOrdinal(), x, y, enumoptions, this.settings.getKeyBinding(enumoptions), this.settings.getOptionFloatValue(enumoptions)));
            }

            ++i;
        }

        this.buttonList.add(new GuiButton(210, this.width / 2 - 155, this.height / 6 + 168 + 11, 70, 20, "All ON"));
        this.buttonList.add(new GuiButton(211, this.width / 2 - 155 + 80, this.height / 6 + 168 + 11, 70, 20, "All OFF"));
        this.buttonList.add(new GuiSmallButton(200, this.width / 2 + 5, this.height / 6 + 168 + 11, stringtranslate.translateKey("gui.done")));
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(GuiButton guibutton)
    {
        if (guibutton.enabled)
        {
            if (guibutton.id < 100 && guibutton instanceof GuiSmallButton)
            {
                this.settings.setOptionValue(((GuiSmallButton)guibutton).returnEnumOptions(), 1);
                guibutton.displayString = this.settings.getKeyBinding(EnumOptions.getEnumOptions(guibutton.id));
            }

            if (guibutton.id == 200)
            {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(this.prevScreen);
            }

            if (guibutton.id == 210)
            {
                this.mc.gameSettings.setAllAnimations(true);
            }

            if (guibutton.id == 211)
            {
                this.mc.gameSettings.setAllAnimations(false);
            }

            if (guibutton.id != EnumOptions.CLOUD_HEIGHT.ordinal())
            {
                ScaledResolution scaledresolution = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
                int i = scaledresolution.getScaledWidth();
                int j = scaledresolution.getScaledHeight();
                this.setWorldAndResolution(this.mc, i, j);
            }
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int i, int j, float f)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 20, 16777215);
        super.drawScreen(i, j, f);
    }
}

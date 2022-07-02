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
    private static EnumOptions[] videoOptions = new EnumOptions[] {EnumOptions.GRAPHICS, EnumOptions.RENDER_DISTANCE_FINE, EnumOptions.AMBIENT_OCCLUSION, EnumOptions.FRAMERATE_LIMIT_FINE, EnumOptions.AO_LEVEL, EnumOptions.VIEW_BOBBING, EnumOptions.GUI_SCALE, EnumOptions.ADVANCED_OPENGL, EnumOptions.GAMMA, EnumOptions.RENDER_CLOUDS, EnumOptions.FOG_FANCY, EnumOptions.FOG_START, EnumOptions.USE_SERVER_TEXTURES};
    private int lastMouseX = 0;
    private int lastMouseY = 0;
    private long mouseStillTime = 0L;

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

        boolean var12 = false;
        boolean var11 = !this.is64bit;
        EnumOptions[] var13 = videoOptions;
        int var14 = var13.length;
        boolean var7 = false;
        int var9;
        int var15;

        for (var15 = 0; var15 < var14; ++var15)
        {
            EnumOptions var8 = var13[var15];
            var9 = this.width / 2 - 155 + var15 % 2 * 160;
            int var10 = this.height / 6 + 21 * (var15 / 2) - 10;

            if (var8.getEnumFloat())
            {
                this.buttonList.add(new GuiSlider(var8.returnEnumOrdinal(), var9, var10, var8, this.guiGameSettings.getKeyBinding(var8), this.guiGameSettings.getOptionFloatValue(var8)));
            }
            else
            {
                this.buttonList.add(new GuiSmallButton(var8.returnEnumOrdinal(), var9, var10, var8, this.guiGameSettings.getKeyBinding(var8)));
            }
        }

        int var16 = this.height / 6 + 21 * (var15 / 2) - 10;
        boolean var17 = false;
        var9 = this.width / 2 - 155 + 160;
        this.buttonList.add(new GuiSmallButton(102, var9, var16, "Quality..."));
        var16 += 21;
        var9 = this.width / 2 - 155 + 0;
        this.buttonList.add(new GuiSmallButton(101, var9, var16, "Details..."));
        var9 = this.width / 2 - 155 + 160;
        this.buttonList.add(new GuiSmallButton(112, var9, var16, "Performance..."));
        var16 += 21;
        var9 = this.width / 2 - 155 + 0;
        this.buttonList.add(new GuiSmallButton(111, var9, var16, "Animations..."));
        var9 = this.width / 2 - 155 + 160;
        this.buttonList.add(new GuiSmallButton(122, var9, var16, "Other..."));
        this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168 + 11, I18n.getString("gui.done")));
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

            if (par1GuiButton.id == 101)
            {
                this.mc.gameSettings.saveOptions();
                GuiDetailSettingsOF var6 = new GuiDetailSettingsOF(this, this.guiGameSettings);
                this.mc.displayGuiScreen(var6);
            }

            if (par1GuiButton.id == 102)
            {
                this.mc.gameSettings.saveOptions();
                GuiQualitySettingsOF var7 = new GuiQualitySettingsOF(this, this.guiGameSettings);
                this.mc.displayGuiScreen(var7);
            }

            if (par1GuiButton.id == 111)
            {
                this.mc.gameSettings.saveOptions();
                GuiAnimationSettingsOF var8 = new GuiAnimationSettingsOF(this, this.guiGameSettings);
                this.mc.displayGuiScreen(var8);
            }

            if (par1GuiButton.id == 112)
            {
                this.mc.gameSettings.saveOptions();
                GuiPerformanceSettingsOF var9 = new GuiPerformanceSettingsOF(this, this.guiGameSettings);
                this.mc.displayGuiScreen(var9);
            }

            if (par1GuiButton.id == 122)
            {
                this.mc.gameSettings.saveOptions();
                GuiOtherSettingsOF var10 = new GuiOtherSettingsOF(this, this.guiGameSettings);
                this.mc.displayGuiScreen(var10);
            }

            if (par1GuiButton.id == EnumOptions.AO_LEVEL.ordinal())
            {
                return;
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
            ;
        }

        super.drawScreen(par1, par2, par3);

        if (Math.abs(par1 - this.lastMouseX) <= 5 && Math.abs(par2 - this.lastMouseY) <= 5)
        {
            short var4 = 700;

            if (System.currentTimeMillis() >= this.mouseStillTime + (long)var4)
            {
                int var5 = this.width / 2 - 150;
                int var6 = this.height / 6 - 5;

                if (par2 <= var6 + 98)
                {
                    var6 += 105;
                }

                int var7 = var5 + 150 + 150;
                int var8 = var6 + 84 + 10;
                GuiButton var9 = this.getSelectedButton(par1, par2);

                if (var9 != null)
                {
                    String var10 = this.getButtonName(var9.displayString);
                    String[] var11 = this.getTooltipLines(var10);

                    if (var11 == null)
                    {
                        return;
                    }

                    this.drawGradientRect(var5, var6, var7, var8, -536870912, -536870912);

                    for (int var12 = 0; var12 < var11.length; ++var12)
                    {
                        String var13 = var11[var12];
                        this.fontRenderer.drawStringWithShadow(var13, var5 + 5, var6 + 5 + var12 * 11, 14540253);
                    }
                }
            }
        }
        else
        {
            this.lastMouseX = par1;
            this.lastMouseY = par2;
            this.mouseStillTime = System.currentTimeMillis();
        }
    }

    private String[] getTooltipLines(String btnName)
    {
        return btnName.equals("Graphics") ? new String[] {"Visual quality", "  Fast  - lower quality, faster", "  Fancy - higher quality, slower", "Changes the appearance of clouds, leaves, water,", "shadows and grass sides."}: (btnName.equals("Render Distance") ? new String[] {"Visible distance", "  Tiny - 32m (fastest)", "  Short - 64m (faster)", "  Normal - 128m", "  Far - 256m (slower)", "  Extreme - 512m (slowest!)", "The Extreme view distance is very resource demanding!"}: (btnName.equals("Smooth Lighting") ? new String[] {"Smooth lighting", "  OFF - no smooth lighting (faster)", "  1% - light smooth lighting (slower)", "  100% - dark smooth lighting (slower)"}: (btnName.equals("Performance") ? new String[] {"FPS Limit", "  Max FPS - no limit (fastest)", "  Balanced - limit 120 FPS (slower)", "  Power saver - limit 40 FPS (slowest)", "  VSync - limit to monitor framerate (60, 30, 20)", "Balanced and Power saver decrease the FPS even if", "the limit value is not reached."}: (btnName.equals("3D Anaglyph") ? new String[] {"3D mode used with red-cyan 3D glasses."}: (btnName.equals("View Bobbing") ? new String[] {"More realistic movement.", "When using mipmaps set it to OFF for best results."}: (btnName.equals("GUI Scale") ? new String[] {"GUI Scale", "Smaller GUI might be faster"}: (btnName.equals("Advanced OpenGL") ? new String[] {"Detect and render only visible geometry", "  OFF - all geometry is rendered (slower)", "  Fast - only visible geometry is rendered (fastest)", "  Fancy - conservative, avoids visual artifacts (faster)", "The option is available only if it is supported by the ", "graphic card."}: (btnName.equals("Fog") ? new String[] {"Fog type", "  Fast - faster fog", "  Fancy - slower fog, looks better", "  OFF - no fog, fastest", "The fancy fog is available only if it is supported by the ", "graphic card."}: (btnName.equals("Fog Start") ? new String[] {"Fog start", "  0.2 - the fog starts near the player", "  0.8 - the fog starts far from the player", "This option usually does not affect the performance."}: (btnName.equals("Brightness") ? new String[] {"Increases the brightness of darker objects", "  OFF - standard brightness", "  100% - maximum brightness for darker objects", "This options does not change the brightness of ", "fully black objects"}: (btnName.equals("Chunk Loading") ? new String[] {"Chunk Loading", "  Default - unstable FPS when loading chunks", "  Smooth - stable FPS", "  Multi-Core - stable FPS, 3x faster world loading", "Smooth and Multi-Core remove the stuttering and freezes", "caused by chunk loading.", "Multi-Core can speed up 3x the world loading and", "increase FPS by using a second CPU core."}: null)))))))))));
    }

    private String getButtonName(String displayString)
    {
        int pos = displayString.indexOf(58);
        return pos < 0 ? displayString : displayString.substring(0, pos);
    }

    private GuiButton getSelectedButton(int i, int j)
    {
        for (int k = 0; k < this.buttonList.size(); ++k)
        {
            GuiButton btn = (GuiButton)this.buttonList.get(k);
            boolean flag = i >= btn.xPosition && j >= btn.yPosition && i < btn.xPosition + btn.width && j < btn.yPosition + btn.height;

            if (flag)
            {
                return btn;
            }
        }

        return null;
    }
}

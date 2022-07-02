package net.minecraft.src;

public class GuiQualitySettingsOF extends GuiScreen
{
    private GuiScreen prevScreen;
    protected String title = "Quality Settings";
    private GameSettings settings;
    private static EnumOptions[] enumOptions = new EnumOptions[] {EnumOptions.MIPMAP_LEVEL, EnumOptions.MIPMAP_TYPE, EnumOptions.CLEAR_WATER, EnumOptions.RANDOM_MOBS, EnumOptions.BETTER_GRASS, EnumOptions.BETTER_SNOW, EnumOptions.CUSTOM_FONTS, EnumOptions.CUSTOM_COLORS, EnumOptions.SWAMP_COLORS, EnumOptions.SMOOTH_BIOMES, EnumOptions.CONNECTED_TEXTURES, EnumOptions.NATURAL_TEXTURES, EnumOptions.CUSTOM_SKY};
    private int lastMouseX = 0;
    private int lastMouseY = 0;
    private long mouseStillTime = 0L;

    public GuiQualitySettingsOF(GuiScreen guiscreen, GameSettings gamesettings)
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

        this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168 + 11, stringtranslate.translateKey("gui.done")));
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
    public void drawScreen(int x, int y, float f)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 20, 16777215);
        super.drawScreen(x, y, f);

        if (Math.abs(x - this.lastMouseX) <= 5 && Math.abs(y - this.lastMouseY) <= 5)
        {
            short activateDelay = 700;

            if (System.currentTimeMillis() >= this.mouseStillTime + (long)activateDelay)
            {
                int x1 = this.width / 2 - 150;
                int y1 = this.height / 6 - 5;

                if (y <= y1 + 98)
                {
                    y1 += 105;
                }

                int x2 = x1 + 150 + 150;
                int y2 = y1 + 84 + 10;
                GuiButton btn = this.getSelectedButton(x, y);

                if (btn != null)
                {
                    String s = this.getButtonName(btn.displayString);
                    String[] lines = this.getTooltipLines(s);

                    if (lines == null)
                    {
                        return;
                    }

                    this.drawGradientRect(x1, y1, x2, y2, -536870912, -536870912);

                    for (int i = 0; i < lines.length; ++i)
                    {
                        String line = lines[i];
                        int col = 14540253;

                        if (line.endsWith("!"))
                        {
                            col = 16719904;
                        }

                        this.fontRenderer.drawStringWithShadow(line, x1 + 5, y1 + 5 + i * 11, col);
                    }
                }
            }
        }
        else
        {
            this.lastMouseX = x;
            this.lastMouseY = y;
            this.mouseStillTime = System.currentTimeMillis();
        }
    }

    private String[] getTooltipLines(String btnName)
    {
        return btnName.equals("Mipmap Level") ? new String[] {"Visual effect which makes distant objects look better", "by smoothing the texture details", "  OFF - no smoothing", "  1 - minimum smoothing", "  4 - maximum smoothing", "This option usually does not affect the performance."}: (btnName.equals("Mipmap Type") ? new String[] {"Visual effect which makes distant objects look better", "by smoothing the texture details", "  Nearest - rough smoothing", "  Linear - fine smoothing", "This option usually does not affect the performance."}: (btnName.equals("Anisotropic Filtering") ? new String[] {"Anisotropic Filtering", " OFF - (default) standard texture detail (faster)", " 2-16 - finer details in mipmapped textures (slower)", "The Anisotropic Filtering restores details in mipmapped", "textures. Higher values may decrease the FPS."}: (btnName.equals("Antialiasing") ? new String[] {"Antialiasing", " OFF - (default) no antialiasing (faster)", " 2-16 - antialiased lines and edges (slower)", "The Antialiasing smooths jagged lines and ", "sharp color transitions.", "Higher values may substantially decrease the FPS.", "Not all levels are supported by all graphics cards.", "Effective after a RESTART!"}: (btnName.equals("Clear Water") ? new String[] {"Clear Water", "  ON - clear, transparent water", "  OFF - default water"}: (btnName.equals("Better Grass") ? new String[] {"Better Grass", "  OFF - default side grass texture, fastest", "  Fast - full side grass texture, slower", "  Fancy - dynamic side grass texture, slowest"}: (btnName.equals("Better Snow") ? new String[] {"Better Snow", "  OFF - default snow, faster", "  ON - better snow, slower", "Shows snow under transparent blocks (fence, tall grass)", "when bordering with snow blocks"}: (btnName.equals("Random Mobs") ? new String[] {"Random Mobs", "  OFF - no random mobs, faster", "  ON - random mobs, slower", "Random mobs uses random textures for the game creatures.", "It needs a texture pack which has multiple mob textures."}: (btnName.equals("Swamp Colors") ? new String[] {"Swamp Colors", "  ON - use swamp colors (default), slower", "  OFF - do not use swamp colors, faster", "The swamp colors affect grass, leaves, vines and water."}: (btnName.equals("Smooth Biomes") ? new String[] {"Smooth Biomes", "  ON - smoothing of biome borders (default), slower", "  OFF - no smoothing of biome borders, faster", "The smoothing of biome borders is done by sampling and", "averaging the color of all surrounding blocks.", "Affected are grass, leaves, vines and water."}: (btnName.equals("Custom Fonts") ? new String[] {"Custom Fonts", "  ON - uses custom fonts (default), slower", "  OFF - uses default font, faster", "The custom fonts are supplied by the current", "texture pack"}: (btnName.equals("Custom Colors") ? new String[] {"Custom Colors", "  ON - uses custom colors (default), slower", "  OFF - uses default colors, faster", "The custom colors are supplied by the current", "texture pack"}: (btnName.equals("Show Capes") ? new String[] {"Show Capes", "  ON - show player capes (default)", "  OFF - do not show player capes"}: (btnName.equals("Connected Textures") ? new String[] {"Connected Textures", "  OFF - no connected textures (default)", "  Fast - fast connected textures", "  Fancy - fancy connected textures", "Connected textures joins the textures of glass,", "sandstone and bookshelves when placed next to", "each other. The connected textures are supplied", "by the current texture pack."}: (btnName.equals("Far View") ? new String[] {"Far View", " OFF - (default) standard view distance", " ON - 3x view distance", "Far View is very resource demanding!", "3x view distance => 9x chunks to be loaded => FPS / 9", "Standard view distances: 32, 64, 128, 256", "Far view distances: 96, 192, 384, 512"}: (btnName.equals("Natural Textures") ? new String[] {"Natural Textures", "  OFF - no natural textures (default)", "  ON - use natural textures", "Natural textures remove the gridlike pattern", "created by repeating blocks of the same type.", "It uses rotated and flipped variants of the base", "block texture. The configuration for the natural", "textures is supplied by the current texture pack"}: (btnName.equals("Custom Sky") ? new String[] {"Custom Sky", "  ON - custom sky textures (default), slow", "  OFF - default sky, faster", "The custom sky textures are supplied by the current", "texture pack"}: null))))))))))))))));
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

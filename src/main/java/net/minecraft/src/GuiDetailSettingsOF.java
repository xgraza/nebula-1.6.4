package net.minecraft.src;

public class GuiDetailSettingsOF extends GuiScreen
{
    private GuiScreen prevScreen;
    protected String title = "Detail Settings";
    private GameSettings settings;
    private static EnumOptions[] enumOptions = new EnumOptions[] {EnumOptions.CLOUDS, EnumOptions.CLOUD_HEIGHT, EnumOptions.TREES, EnumOptions.GRASS, EnumOptions.WATER, EnumOptions.RAIN, EnumOptions.SKY, EnumOptions.STARS, EnumOptions.SUN_MOON, EnumOptions.SHOW_CAPES, EnumOptions.DEPTH_FOG, EnumOptions.HELD_ITEM_TOOLTIPS, EnumOptions.DROPPED_ITEMS};
    private int lastMouseX = 0;
    private int lastMouseY = 0;
    private long mouseStillTime = 0L;

    public GuiDetailSettingsOF(GuiScreen guiscreen, GameSettings gamesettings)
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
                        this.fontRenderer.drawStringWithShadow(line, x1 + 5, y1 + 5 + i * 11, 14540253);
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
        return btnName.equals("Clouds") ? new String[] {"Clouds", "  Default - as set by setting Graphics", "  Fast - lower quality, faster", "  Fancy - higher quality, slower", "  OFF - no clouds, fastest", "Fast clouds are rendered 2D.", "Fancy clouds are rendered 3D."}: (btnName.equals("Cloud Height") ? new String[] {"Cloud Height", "  OFF - default height", "  100% - above world height limit"}: (btnName.equals("Trees") ? new String[] {"Trees", "  Default - as set by setting Graphics", "  Fast - lower quality, faster", "  Fancy - higher quality, slower", "Fast trees have opaque leaves.", "Fancy trees have transparent leaves."}: (btnName.equals("Grass") ? new String[] {"Grass", "  Default - as set by setting Graphics", "  Fast - lower quality, faster", "  Fancy - higher quality, slower", "Fast grass uses default side texture.", "Fancy grass uses biome side texture."}: (btnName.equals("Dropped Items") ? new String[] {"Dropped Items", "  Default - as set by setting Graphics", "  Fast - 2D dropped items, faster", "  Fancy - 3D dropped items, slower"}: (btnName.equals("Water") ? new String[] {"Water", "  Default - as set by setting Graphics", "  Fast  - lower quality, faster", "  Fancy - higher quality, slower", "Fast water (1 pass) has some visual artifacts", "Fancy water (2 pass) has no visual artifacts"}: (btnName.equals("Rain & Snow") ? new String[] {"Rain & Snow", "  Default - as set by setting Graphics", "  Fast  - light rain/snow, faster", "  Fancy - heavy rain/snow, slower", "  OFF - no rain/snow, fastest", "When rain is OFF the splashes and rain sounds", "are still active."}: (btnName.equals("Sky") ? new String[] {"Sky", "  ON - sky is visible, slower", "  OFF  - sky is not visible, faster", "When sky is OFF the moon and sun are still visible."}: (btnName.equals("Stars") ? new String[] {"Stars", "  ON - stars are visible, slower", "  OFF  - stars are not visible, faster"}: (btnName.equals("Depth Fog") ? new String[] {"Depth Fog", "  ON - fog moves closer at bedrock levels (default)", "  OFF - same fog at all levels"}: (btnName.equals("Show Capes") ? new String[] {"Show Capes", "  ON - show player capes (default)", "  OFF - do not show player capes"}: null))))))))));
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

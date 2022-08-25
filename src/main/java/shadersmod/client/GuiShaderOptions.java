package shadersmod.client;

import java.util.Iterator;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.src.Config;
import net.minecraft.src.GuiScreenOF;
import net.minecraft.src.Lang;
import net.minecraft.src.TooltipManager;
import net.minecraft.src.TooltipProviderShaderOptions;
import net.minecraft.util.MathHelper;

public class GuiShaderOptions extends GuiScreenOF
{
    private GuiScreen prevScreen;
    protected String title;
    private GameSettings settings;
    private TooltipManager tooltipManager;
    private String screenName;
    private String screenText;
    private boolean changed;
    public static final String OPTION_PROFILE = "<profile>";
    public static final String OPTION_EMPTY = "<empty>";
    public static final String OPTION_REST = "*";
    private FontRenderer fontRendererObj;

    public GuiShaderOptions(GuiScreen guiscreen, GameSettings gamesettings)
    {
        this.tooltipManager = new TooltipManager(this, new TooltipProviderShaderOptions());
        this.screenName = null;
        this.screenText = null;
        this.changed = false;
        this.title = "Shader Options";
        this.prevScreen = guiscreen;
        this.settings = gamesettings;
    }

    public GuiShaderOptions(GuiScreen guiscreen, GameSettings gamesettings, String screenName)
    {
        this(guiscreen, gamesettings);
        this.screenName = screenName;

        if (screenName != null)
        {
            this.screenText = Shaders.translate("screen." + screenName, screenName);
        }
    }

    public void initGui()
    {
        this.fontRendererObj = this.fontRenderer;
        this.title = I18n.format("of.options.shaderOptionsTitle", new Object[0]);
        byte baseId = 100;
        boolean baseX = false;
        byte baseY = 30;
        byte stepY = 20;
        byte btnWidth = 120;
        byte btnHeight = 20;
        int columns = Shaders.getShaderPackColumns(this.screenName, 2);
        ShaderOption[] ops = Shaders.getShaderPackOptions(this.screenName);

        if (ops != null)
        {
            int colsMin = MathHelper.ceiling_double_int((double)ops.length / 9.0D);

            if (columns < colsMin)
            {
                columns = colsMin;
            }

            for (int i = 0; i < ops.length; ++i)
            {
                ShaderOption so = ops[i];

                if (so != null && so.isVisible())
                {
                    int col = i % columns;
                    int row = i / columns;
                    int colWidth = Math.min(this.width / columns, 200);
                    int var21 = (this.width - colWidth * columns) / 2;
                    int x = col * colWidth + 5 + var21;
                    int y = baseY + row * stepY;
                    int w = colWidth - 10;
                    String text = getButtonText(so, w);
                    Object btn;

                    if (Shaders.isShaderPackOptionSlider(so.getName()))
                    {
                        btn = new GuiSliderShaderOption(baseId + i, x, y, w, btnHeight, so, text);
                    }
                    else
                    {
                        btn = new GuiButtonShaderOption(baseId + i, x, y, w, btnHeight, so, text);
                    }

                    ((GuiButtonShaderOption)btn).enabled = so.isEnabled();
                    this.buttonList.add(btn);
                }
            }
        }

        this.buttonList.add(new GuiButton(201, this.width / 2 - btnWidth - 20, this.height / 6 + 168 + 11, btnWidth, btnHeight, I18n.format("controls.reset", new Object[0])));
        this.buttonList.add(new GuiButton(200, this.width / 2 + 20, this.height / 6 + 168 + 11, btnWidth, btnHeight, I18n.format("gui.done", new Object[0])));
    }

    public static String getButtonText(ShaderOption so, int btnWidth)
    {
        String labelName = so.getNameText();

        if (so instanceof ShaderOptionScreen)
        {
            ShaderOptionScreen fr1 = (ShaderOptionScreen)so;
            return labelName + "...";
        }
        else
        {
            FontRenderer fr = Config.getMinecraft().fontRenderer;

            for (int lenSuffix = fr.getStringWidth(": " + Lang.getOff()) + 5; fr.getStringWidth(labelName) + lenSuffix >= btnWidth && labelName.length() > 0; labelName = labelName.substring(0, labelName.length() - 1))
            {
                ;
            }

            String col = so.isChanged() ? so.getValueColor(so.getValue()) : "";
            String labelValue = so.getValueText(so.getValue());
            return labelName + ": " + col + labelValue;
        }
    }

    protected void actionPerformed(GuiButton guibutton)
    {
        if (guibutton.enabled)
        {
            if (guibutton.id < 200 && guibutton instanceof GuiButtonShaderOption)
            {
                GuiButtonShaderOption opts = (GuiButtonShaderOption)guibutton;
                ShaderOption i = opts.getShaderOption();

                if (i instanceof ShaderOptionScreen)
                {
                    String var8 = i.getName();
                    GuiShaderOptions scr = new GuiShaderOptions(this, this.settings, var8);
                    this.mc.displayGuiScreen(scr);
                    return;
                }

                if (isShiftKeyDown())
                {
                    i.resetValue();
                }
                else
                {
                    i.nextValue();
                }

                this.updateAllButtons();
                this.changed = true;
            }

            if (guibutton.id == 201)
            {
                ShaderOption[] var6 = Shaders.getChangedOptions(Shaders.getShaderPackOptions());

                for (int var7 = 0; var7 < var6.length; ++var7)
                {
                    ShaderOption opt = var6[var7];
                    opt.resetValue();
                    this.changed = true;
                }

                this.updateAllButtons();
            }

            if (guibutton.id == 200)
            {
                if (this.changed)
                {
                    Shaders.saveShaderPackOptions();
                    this.changed = false;
                    Shaders.uninit();
                }

                this.mc.displayGuiScreen(this.prevScreen);
            }
        }
    }

    protected void actionPerformedRightClick(GuiButton btn)
    {
        if (btn instanceof GuiButtonShaderOption)
        {
            GuiButtonShaderOption btnSo = (GuiButtonShaderOption)btn;
            ShaderOption so = btnSo.getShaderOption();

            if (isShiftKeyDown())
            {
                so.resetValue();
            }
            else
            {
                so.prevValue();
            }

            this.updateAllButtons();
            this.changed = true;
        }
    }

    public void onGuiClosed()
    {
        super.onGuiClosed();

        if (this.changed)
        {
            Shaders.saveShaderPackOptions();
            this.changed = false;
            Shaders.uninit();
        }
    }

    private void updateAllButtons()
    {
        Iterator it = this.buttonList.iterator();

        while (it.hasNext())
        {
            GuiButton btn = (GuiButton)it.next();

            if (btn instanceof GuiButtonShaderOption)
            {
                GuiButtonShaderOption gbso = (GuiButtonShaderOption)btn;
                ShaderOption opt = gbso.getShaderOption();

                if (opt instanceof ShaderOptionProfile)
                {
                    ShaderOptionProfile optProf = (ShaderOptionProfile)opt;
                    optProf.updateProfile();
                }

                gbso.displayString = getButtonText(opt, gbso.getButtonWidth());
                gbso.valueChanged();
            }
        }
    }

    public void drawScreen(int x, int y, float f)
    {
        this.drawDefaultBackground();

        if (this.screenText != null)
        {
            this.drawCenteredString(this.fontRendererObj, this.screenText, this.width / 2, 15, 16777215);
        }
        else
        {
            this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 15, 16777215);
        }

        super.drawScreen(x, y, f);
        this.tooltipManager.drawTooltips(x, y, this.buttonList);
    }
}

package net.minecraft.src;

import java.util.Collections;
import java.util.List;
import org.lwjgl.input.Keyboard;

public class GuiScreenMcoWorldTemplate extends GuiScreen
{
    private final ScreenWithCallback field_110401_a;
    private WorldTemplate field_110398_b;
    private List field_110399_c = Collections.emptyList();
    private GuiScreenMcoWorldTemplateSelectionList field_110396_d;
    private int field_110397_e = -1;
    private GuiButton field_110400_p;

    public GuiScreenMcoWorldTemplate(ScreenWithCallback par1ScreenWithCallback, WorldTemplate par2WorldTemplate)
    {
        this.field_110401_a = par1ScreenWithCallback;
        this.field_110398_b = par2WorldTemplate;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.field_110396_d = new GuiScreenMcoWorldTemplateSelectionList(this);
        (new GuiScreenMcoWorldTemplateDownloadThread(this)).start();
        this.func_110385_g();
    }

    private void func_110385_g()
    {
        this.buttonList.add(new GuiButton(0, this.width / 2 + 6, this.height - 52, 153, 20, I18n.getString("gui.cancel")));
        this.buttonList.add(this.field_110400_p = new GuiButton(1, this.width / 2 - 154, this.height - 52, 153, 20, I18n.getString("mco.template.button.select")));
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        super.updateScreen();
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        if (par1GuiButton.enabled)
        {
            if (par1GuiButton.id == 1)
            {
                this.func_110394_h();
            }
            else if (par1GuiButton.id == 0)
            {
                this.field_110401_a.func_110354_a((Object)null);
                this.mc.displayGuiScreen(this.field_110401_a);
            }
            else
            {
                this.field_110396_d.actionPerformed(par1GuiButton);
            }
        }
    }

    private void func_110394_h()
    {
        if (this.field_110397_e >= 0 && this.field_110397_e < this.field_110399_c.size())
        {
            this.field_110401_a.func_110354_a(this.field_110399_c.get(this.field_110397_e));
            this.mc.displayGuiScreen(this.field_110401_a);
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        this.drawDefaultBackground();
        this.field_110396_d.drawScreen(par1, par2, par3);
        this.drawCenteredString(this.fontRenderer, I18n.getString("mco.template.title"), this.width / 2, 20, 16777215);
        super.drawScreen(par1, par2, par3);
    }

    static Minecraft func_110382_a(GuiScreenMcoWorldTemplate par0GuiScreenMcoWorldTemplate)
    {
        return par0GuiScreenMcoWorldTemplate.mc;
    }

    static List func_110388_a(GuiScreenMcoWorldTemplate par0GuiScreenMcoWorldTemplate, List par1List)
    {
        return par0GuiScreenMcoWorldTemplate.field_110399_c = par1List;
    }

    static Minecraft func_110392_b(GuiScreenMcoWorldTemplate par0GuiScreenMcoWorldTemplate)
    {
        return par0GuiScreenMcoWorldTemplate.mc;
    }

    static Minecraft func_130066_c(GuiScreenMcoWorldTemplate par0GuiScreenMcoWorldTemplate)
    {
        return par0GuiScreenMcoWorldTemplate.mc;
    }

    static List func_110395_c(GuiScreenMcoWorldTemplate par0GuiScreenMcoWorldTemplate)
    {
        return par0GuiScreenMcoWorldTemplate.field_110399_c;
    }

    static int func_130064_a(GuiScreenMcoWorldTemplate par0GuiScreenMcoWorldTemplate, int par1)
    {
        return par0GuiScreenMcoWorldTemplate.field_110397_e = par1;
    }

    static WorldTemplate func_130065_a(GuiScreenMcoWorldTemplate par0GuiScreenMcoWorldTemplate, WorldTemplate par1WorldTemplate)
    {
        return par0GuiScreenMcoWorldTemplate.field_110398_b = par1WorldTemplate;
    }

    static WorldTemplate func_130067_e(GuiScreenMcoWorldTemplate par0GuiScreenMcoWorldTemplate)
    {
        return par0GuiScreenMcoWorldTemplate.field_110398_b;
    }

    static int func_130062_f(GuiScreenMcoWorldTemplate par0GuiScreenMcoWorldTemplate)
    {
        return par0GuiScreenMcoWorldTemplate.field_110397_e;
    }

    static FontRenderer func_110389_g(GuiScreenMcoWorldTemplate par0GuiScreenMcoWorldTemplate)
    {
        return par0GuiScreenMcoWorldTemplate.fontRenderer;
    }

    static FontRenderer func_110387_h(GuiScreenMcoWorldTemplate par0GuiScreenMcoWorldTemplate)
    {
        return par0GuiScreenMcoWorldTemplate.fontRenderer;
    }

    static FontRenderer func_110384_i(GuiScreenMcoWorldTemplate par0GuiScreenMcoWorldTemplate)
    {
        return par0GuiScreenMcoWorldTemplate.fontRenderer;
    }

    static FontRenderer func_130063_j(GuiScreenMcoWorldTemplate par0GuiScreenMcoWorldTemplate)
    {
        return par0GuiScreenMcoWorldTemplate.fontRenderer;
    }
}

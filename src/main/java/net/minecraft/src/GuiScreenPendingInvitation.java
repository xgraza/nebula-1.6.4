package net.minecraft.src;

import com.google.common.collect.Lists;
import java.util.List;
import org.lwjgl.input.Keyboard;

public class GuiScreenPendingInvitation extends GuiScreen
{
    private final GuiScreen field_130061_a;
    private GuiScreenPendingInvitationList field_130059_b;
    private List field_130060_c = Lists.newArrayList();
    private int field_130058_d = -1;

    public GuiScreenPendingInvitation(GuiScreen par1GuiScreen)
    {
        this.field_130061_a = par1GuiScreen;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.field_130059_b = new GuiScreenPendingInvitationList(this);
        (new GuiScreenPendingInvitationINNER1(this)).start();
        this.func_130050_g();
    }

    private void func_130050_g()
    {
        this.buttonList.add(new GuiButton(1, this.width / 2 - 154, this.height - 52, 153, 20, I18n.getString("mco.invites.button.accept")));
        this.buttonList.add(new GuiButton(2, this.width / 2 + 6, this.height - 52, 153, 20, I18n.getString("mco.invites.button.reject")));
        this.buttonList.add(new GuiButton(0, this.width / 2 - 75, this.height - 28, 153, 20, I18n.getString("gui.back")));
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
                this.func_130051_i();
            }
            else if (par1GuiButton.id == 0)
            {
                this.mc.displayGuiScreen(this.field_130061_a);
            }
            else if (par1GuiButton.id == 2)
            {
                this.func_130057_h();
            }
            else
            {
                this.field_130059_b.actionPerformed(par1GuiButton);
            }
        }
    }

    private void func_130057_h()
    {
        if (this.field_130058_d >= 0 && this.field_130058_d < this.field_130060_c.size())
        {
            (new GuiScreenPendingInvitationINNER2(this)).start();
        }
    }

    private void func_130051_i()
    {
        if (this.field_130058_d >= 0 && this.field_130058_d < this.field_130060_c.size())
        {
            (new GuiScreenPendingInvitationINNER3(this)).start();
        }
    }

    private void func_130047_j()
    {
        int var1 = this.field_130058_d;

        if (this.field_130060_c.size() - 1 == this.field_130058_d)
        {
            --this.field_130058_d;
        }

        this.field_130060_c.remove(var1);

        if (this.field_130060_c.size() == 0)
        {
            this.field_130058_d = -1;
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        this.drawDefaultBackground();
        this.field_130059_b.drawScreen(par1, par2, par3);
        this.drawCenteredString(this.fontRenderer, I18n.getString("mco.invites.title"), this.width / 2, 20, 16777215);
        super.drawScreen(par1, par2, par3);
    }

    static Minecraft func_130048_a(GuiScreenPendingInvitation par0GuiScreenPendingInvitation)
    {
        return par0GuiScreenPendingInvitation.mc;
    }

    static List func_130043_a(GuiScreenPendingInvitation par0GuiScreenPendingInvitation, List par1List)
    {
        return par0GuiScreenPendingInvitation.field_130060_c = par1List;
    }

    static Minecraft func_130044_b(GuiScreenPendingInvitation par0GuiScreenPendingInvitation)
    {
        return par0GuiScreenPendingInvitation.mc;
    }

    static Minecraft func_130041_c(GuiScreenPendingInvitation par0GuiScreenPendingInvitation)
    {
        return par0GuiScreenPendingInvitation.mc;
    }

    static int func_130049_d(GuiScreenPendingInvitation par0GuiScreenPendingInvitation)
    {
        return par0GuiScreenPendingInvitation.field_130058_d;
    }

    static List func_130042_e(GuiScreenPendingInvitation par0GuiScreenPendingInvitation)
    {
        return par0GuiScreenPendingInvitation.field_130060_c;
    }

    static void func_130040_f(GuiScreenPendingInvitation par0GuiScreenPendingInvitation)
    {
        par0GuiScreenPendingInvitation.func_130047_j();
    }

    static Minecraft func_130056_g(GuiScreenPendingInvitation par0GuiScreenPendingInvitation)
    {
        return par0GuiScreenPendingInvitation.mc;
    }

    static Minecraft func_130046_h(GuiScreenPendingInvitation par0GuiScreenPendingInvitation)
    {
        return par0GuiScreenPendingInvitation.mc;
    }

    static Minecraft func_130055_i(GuiScreenPendingInvitation par0GuiScreenPendingInvitation)
    {
        return par0GuiScreenPendingInvitation.mc;
    }

    static Minecraft func_130054_j(GuiScreenPendingInvitation par0GuiScreenPendingInvitation)
    {
        return par0GuiScreenPendingInvitation.mc;
    }

    static int func_130053_a(GuiScreenPendingInvitation par0GuiScreenPendingInvitation, int par1)
    {
        return par0GuiScreenPendingInvitation.field_130058_d = par1;
    }

    static FontRenderer func_130045_k(GuiScreenPendingInvitation par0GuiScreenPendingInvitation)
    {
        return par0GuiScreenPendingInvitation.fontRenderer;
    }

    static FontRenderer func_130052_l(GuiScreenPendingInvitation par0GuiScreenPendingInvitation)
    {
        return par0GuiScreenPendingInvitation.fontRenderer;
    }
}

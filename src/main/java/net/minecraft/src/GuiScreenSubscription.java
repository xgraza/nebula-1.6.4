package net.minecraft.src;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import org.lwjgl.input.Keyboard;

public class GuiScreenSubscription extends GuiScreen
{
    private final GuiScreen field_98067_a;
    private final McoServer field_98065_b;
    private final int field_98066_c = 0;
    private final int field_98064_d = 1;
    private int field_98068_n;
    private String field_98069_o;

    public GuiScreenSubscription(GuiScreen par1GuiScreen, McoServer par2McoServer)
    {
        this.field_98067_a = par1GuiScreen;
        this.field_98065_b = par2McoServer;
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen() {}

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        this.func_98063_a(this.field_98065_b.field_96408_a);
        Keyboard.enableRepeatEvents(true);
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.getString("gui.cancel")));
    }

    private void func_98063_a(long par1)
    {
        McoClient var3 = new McoClient(this.mc.getSession());

        try
        {
            ValueObjectSubscription var4 = var3.func_98177_f(par1);
            this.field_98068_n = var4.field_98170_b;
            this.field_98069_o = this.func_98062_b(var4.field_98171_a);
        }
        catch (ExceptionMcoService var5)
        {
            Minecraft.getMinecraft().getLogAgent().logSevere(var5.toString());
        }
        catch (IOException var6)
        {
            Minecraft.getMinecraft().getLogAgent().logWarning("Realms: could not parse response");
        }
    }

    private String func_98062_b(long par1)
    {
        GregorianCalendar var3 = new GregorianCalendar(TimeZone.getDefault());
        var3.setTimeInMillis(par1);
        return SimpleDateFormat.getDateTimeInstance().format(var3.getTime());
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        if (par1GuiButton.enabled)
        {
            if (par1GuiButton.id == 0)
            {
                this.mc.displayGuiScreen(this.field_98067_a);
            }
            else if (par1GuiButton.id == 1)
            {
                ;
            }
        }
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char par1, int par2) {}

    /**
     * Called when the mouse is clicked.
     */
    protected void mouseClicked(int par1, int par2, int par3)
    {
        super.mouseClicked(par1, par2, par3);
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, I18n.getString("mco.configure.world.subscription.title"), this.width / 2, 17, 16777215);
        this.drawString(this.fontRenderer, I18n.getString("mco.configure.world.subscription.start"), this.width / 2 - 100, 53, 10526880);
        this.drawString(this.fontRenderer, this.field_98069_o, this.width / 2 - 100, 66, 16777215);
        this.drawString(this.fontRenderer, I18n.getString("mco.configure.world.subscription.daysleft"), this.width / 2 - 100, 85, 10526880);
        this.drawString(this.fontRenderer, String.valueOf(this.field_98068_n), this.width / 2 - 100, 98, 16777215);
        super.drawScreen(par1, par2, par3);
    }
}

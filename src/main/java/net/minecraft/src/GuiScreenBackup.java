package net.minecraft.src;

import java.util.Collections;
import java.util.List;
import org.lwjgl.input.Keyboard;

public class GuiScreenBackup extends GuiScreen
{
    private final GuiScreenConfigureWorld field_110380_a;
    private final long field_110377_b;
    private List field_110378_c = Collections.emptyList();
    private GuiScreenBackupSelectionList field_110375_d;
    private int field_110376_e = -1;
    private GuiButton field_110379_p;

    public GuiScreenBackup(GuiScreenConfigureWorld par1GuiScreenConfigureWorld, long par2)
    {
        this.field_110380_a = par1GuiScreenConfigureWorld;
        this.field_110377_b = par2;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.field_110375_d = new GuiScreenBackupSelectionList(this);
        (new GuiScreenBackupDownloadThread(this)).start();
        this.func_110369_g();
    }

    private void func_110369_g()
    {
        this.buttonList.add(new GuiButton(0, this.width / 2 + 6, this.height - 52, 153, 20, I18n.getString("gui.back")));
        this.buttonList.add(this.field_110379_p = new GuiButton(1, this.width / 2 - 154, this.height - 52, 153, 20, I18n.getString("mco.backup.button.restore")));
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
                String var2 = I18n.getString("mco.configure.world.restore.question.line1");
                String var3 = I18n.getString("mco.configure.world.restore.question.line2");
                this.mc.displayGuiScreen(new GuiScreenConfirmation(this, GuiScreenConfirmationType.Warning, var2, var3, 1));
            }
            else if (par1GuiButton.id == 0)
            {
                this.mc.displayGuiScreen(this.field_110380_a);
            }
            else
            {
                this.field_110375_d.actionPerformed(par1GuiButton);
            }
        }
    }

    public void confirmClicked(boolean par1, int par2)
    {
        if (par1 && par2 == 1)
        {
            this.func_110374_h();
        }
        else
        {
            this.mc.displayGuiScreen(this);
        }
    }

    private void func_110374_h()
    {
        if (this.field_110376_e >= 0 && this.field_110376_e < this.field_110378_c.size())
        {
            Backup var1 = (Backup)this.field_110378_c.get(this.field_110376_e);
            GuiScreenBackupRestoreTask var2 = new GuiScreenBackupRestoreTask(this, var1, (GuiScreenBackupDownloadThread)null);
            GuiScreenLongRunningTask var3 = new GuiScreenLongRunningTask(this.mc, this.field_110380_a, var2);
            var3.func_98117_g();
            this.mc.displayGuiScreen(var3);
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        this.drawDefaultBackground();
        this.field_110375_d.drawScreen(par1, par2, par3);
        this.drawCenteredString(this.fontRenderer, I18n.getString("mco.backup.title"), this.width / 2, 20, 16777215);
        super.drawScreen(par1, par2, par3);
    }

    static Minecraft func_110366_a(GuiScreenBackup par0GuiScreenBackup)
    {
        return par0GuiScreenBackup.mc;
    }

    static List func_110373_a(GuiScreenBackup par0GuiScreenBackup, List par1List)
    {
        return par0GuiScreenBackup.field_110378_c = par1List;
    }

    static long func_110367_b(GuiScreenBackup par0GuiScreenBackup)
    {
        return par0GuiScreenBackup.field_110377_b;
    }

    static Minecraft func_130030_c(GuiScreenBackup par0GuiScreenBackup)
    {
        return par0GuiScreenBackup.mc;
    }

    static GuiScreenConfigureWorld func_130031_d(GuiScreenBackup par0GuiScreenBackup)
    {
        return par0GuiScreenBackup.field_110380_a;
    }

    static Minecraft func_130035_e(GuiScreenBackup par0GuiScreenBackup)
    {
        return par0GuiScreenBackup.mc;
    }

    static Minecraft func_130036_f(GuiScreenBackup par0GuiScreenBackup)
    {
        return par0GuiScreenBackup.mc;
    }

    static List func_110370_e(GuiScreenBackup par0GuiScreenBackup)
    {
        return par0GuiScreenBackup.field_110378_c;
    }

    static int func_130029_a(GuiScreenBackup par0GuiScreenBackup, int par1)
    {
        return par0GuiScreenBackup.field_110376_e = par1;
    }

    static int func_130034_h(GuiScreenBackup par0GuiScreenBackup)
    {
        return par0GuiScreenBackup.field_110376_e;
    }

    static FontRenderer func_130032_i(GuiScreenBackup par0GuiScreenBackup)
    {
        return par0GuiScreenBackup.fontRenderer;
    }

    static FontRenderer func_130033_j(GuiScreenBackup par0GuiScreenBackup)
    {
        return par0GuiScreenBackup.fontRenderer;
    }
}

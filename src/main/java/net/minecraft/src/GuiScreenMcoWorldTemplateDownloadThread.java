package net.minecraft.src;

class GuiScreenMcoWorldTemplateDownloadThread extends Thread
{
    final GuiScreenMcoWorldTemplate field_111256_a;

    GuiScreenMcoWorldTemplateDownloadThread(GuiScreenMcoWorldTemplate par1GuiScreenMcoWorldTemplate)
    {
        this.field_111256_a = par1GuiScreenMcoWorldTemplate;
    }

    public void run()
    {
        McoClient var1 = new McoClient(GuiScreenMcoWorldTemplate.func_110382_a(this.field_111256_a).getSession());

        try
        {
            GuiScreenMcoWorldTemplate.func_110388_a(this.field_111256_a, var1.func_111231_d().field_110736_a);
        }
        catch (ExceptionMcoService var3)
        {
            GuiScreenMcoWorldTemplate.func_110392_b(this.field_111256_a).getLogAgent().logSevere(var3.toString());
        }
    }
}

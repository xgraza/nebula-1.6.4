package net.minecraft.src;

class TaskResetWorld extends TaskLongRunning
{
    private final long field_96591_c;
    private final String field_104066_d;
    private final WorldTemplate field_111252_e;

    final GuiScreenResetWorld field_96592_a;

    public TaskResetWorld(GuiScreenResetWorld par1GuiScreenResetWorld, long par2, String par4Str, WorldTemplate par5WorldTemplate)
    {
        this.field_96592_a = par1GuiScreenResetWorld;
        this.field_96591_c = par2;
        this.field_104066_d = par4Str;
        this.field_111252_e = par5WorldTemplate;
    }

    public void run()
    {
        McoClient var1 = new McoClient(this.getMinecraft().getSession());
        String var2 = I18n.getString("mco.reset.world.resetting.screen.title");
        this.setMessage(var2);

        try
        {
            if (this.field_111252_e != null)
            {
                var1.func_111233_e(this.field_96591_c, this.field_111252_e.field_110734_a);
            }
            else
            {
                var1.func_96376_d(this.field_96591_c, this.field_104066_d);
            }

            GuiScreenResetWorld.func_96147_b(this.field_96592_a).displayGuiScreen(GuiScreenResetWorld.func_96148_a(this.field_96592_a));
        }
        catch (ExceptionMcoService var4)
        {
            GuiScreenResetWorld.func_130025_c(this.field_96592_a).getLogAgent().logSevere(var4.toString());
            this.setFailedMessage(var4.toString());
        }
        catch (Exception var5)
        {
            GuiScreenResetWorld.func_130024_d(this.field_96592_a).getLogAgent().logWarning("Realms: ");
            this.setFailedMessage(var5.toString());
        }
    }
}

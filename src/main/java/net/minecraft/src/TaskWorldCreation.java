package net.minecraft.src;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

class TaskWorldCreation extends TaskLongRunning
{
    private final String field_96589_c;
    private final String field_96587_d;
    private final String field_104065_f;
    private final WorldTemplate field_111253_f;

    final GuiScreenCreateOnlineWorld field_96590_a;

    public TaskWorldCreation(GuiScreenCreateOnlineWorld par1GuiScreenCreateOnlineWorld, String par2Str, String par3Str, String par4Str, WorldTemplate par5WorldTemplate)
    {
        this.field_96590_a = par1GuiScreenCreateOnlineWorld;
        this.field_96589_c = par2Str;
        this.field_96587_d = par3Str;
        this.field_104065_f = par4Str;
        this.field_111253_f = par5WorldTemplate;
    }

    public void run()
    {
        String var1 = I18n.getString("mco.create.world.wait");
        this.setMessage(var1);
        McoClient var2 = new McoClient(GuiScreenCreateOnlineWorld.func_96248_a(this.field_96590_a).getSession());

        try
        {
            if (this.field_111253_f != null)
            {
                var2.func_96386_a(this.field_96589_c, this.field_96587_d, this.field_104065_f, this.field_111253_f.field_110734_a);
            }
            else
            {
                var2.func_96386_a(this.field_96589_c, this.field_96587_d, this.field_104065_f, "-1");
            }

            GuiScreenCreateOnlineWorld.func_96246_c(this.field_96590_a).displayGuiScreen(GuiScreenCreateOnlineWorld.func_96247_b(this.field_96590_a));
        }
        catch (ExceptionMcoService var4)
        {
            GuiScreenCreateOnlineWorld.func_130026_d(this.field_96590_a).getLogAgent().logSevere(var4.toString());
            this.setFailedMessage(var4.toString());
        }
        catch (UnsupportedEncodingException var5)
        {
            GuiScreenCreateOnlineWorld.func_130027_e(this.field_96590_a).getLogAgent().logWarning("Realms: " + var5.getLocalizedMessage());
            this.setFailedMessage(var5.getLocalizedMessage());
        }
        catch (IOException var6)
        {
            GuiScreenCreateOnlineWorld.func_130028_f(this.field_96590_a).getLogAgent().logWarning("Realms: could not parse response");
            this.setFailedMessage(var6.getLocalizedMessage());
        }
        catch (Exception var7)
        {
            this.setFailedMessage(var7.getLocalizedMessage());
        }
    }
}

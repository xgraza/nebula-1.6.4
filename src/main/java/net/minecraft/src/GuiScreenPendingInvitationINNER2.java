package net.minecraft.src;

class GuiScreenPendingInvitationINNER2 extends Thread
{
    final GuiScreenPendingInvitation field_130132_a;

    GuiScreenPendingInvitationINNER2(GuiScreenPendingInvitation par1GuiScreenPendingInvitation)
    {
        this.field_130132_a = par1GuiScreenPendingInvitation;
    }

    public void run()
    {
        try
        {
            McoClient var1 = new McoClient(GuiScreenPendingInvitation.func_130041_c(this.field_130132_a).getSession());
            var1.func_130109_b(((PendingInvite)GuiScreenPendingInvitation.func_130042_e(this.field_130132_a).get(GuiScreenPendingInvitation.func_130049_d(this.field_130132_a))).field_130094_a);
            GuiScreenPendingInvitation.func_130040_f(this.field_130132_a);
        }
        catch (ExceptionMcoService var2)
        {
            GuiScreenPendingInvitation.func_130056_g(this.field_130132_a).getLogAgent().logSevere(var2.toString());
        }
    }
}

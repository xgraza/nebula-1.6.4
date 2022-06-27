package net.minecraft.src;

class GuiScreenPendingInvitationINNER3 extends Thread
{
    final GuiScreenPendingInvitation field_130131_a;

    GuiScreenPendingInvitationINNER3(GuiScreenPendingInvitation par1GuiScreenPendingInvitation)
    {
        this.field_130131_a = par1GuiScreenPendingInvitation;
    }

    public void run()
    {
        try
        {
            McoClient var1 = new McoClient(GuiScreenPendingInvitation.func_130046_h(this.field_130131_a).getSession());
            var1.func_130107_a(((PendingInvite)GuiScreenPendingInvitation.func_130042_e(this.field_130131_a).get(GuiScreenPendingInvitation.func_130049_d(this.field_130131_a))).field_130094_a);
            GuiScreenPendingInvitation.func_130040_f(this.field_130131_a);
        }
        catch (ExceptionMcoService var2)
        {
            GuiScreenPendingInvitation.func_130055_i(this.field_130131_a).getLogAgent().logSevere(var2.toString());
        }
    }
}

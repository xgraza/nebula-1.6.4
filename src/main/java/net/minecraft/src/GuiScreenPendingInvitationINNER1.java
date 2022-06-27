package net.minecraft.src;

class GuiScreenPendingInvitationINNER1 extends Thread
{
    final GuiScreenPendingInvitation field_130121_a;

    GuiScreenPendingInvitationINNER1(GuiScreenPendingInvitation par1GuiScreenPendingInvitation)
    {
        this.field_130121_a = par1GuiScreenPendingInvitation;
    }

    public void run()
    {
        McoClient var1 = new McoClient(GuiScreenPendingInvitation.func_130048_a(this.field_130121_a).getSession());

        try
        {
            GuiScreenPendingInvitation.func_130043_a(this.field_130121_a, var1.func_130108_f().field_130096_a);
        }
        catch (ExceptionMcoService var3)
        {
            GuiScreenPendingInvitation.func_130044_b(this.field_130121_a).getLogAgent().logSevere(var3.toString());
        }
    }
}

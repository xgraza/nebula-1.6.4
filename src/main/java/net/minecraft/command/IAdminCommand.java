package net.minecraft.command;

public interface IAdminCommand
{
    void notifyAdmins(ICommandSender var1, int var2, String var3, Object ... var4);
}

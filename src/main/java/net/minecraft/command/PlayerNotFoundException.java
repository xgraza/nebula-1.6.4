package net.minecraft.command;

public class PlayerNotFoundException extends CommandException
{
    private static final String __OBFID = "CL_00001190";

    public PlayerNotFoundException()
    {
        this("commands.generic.player.notFound");
    }

    public PlayerNotFoundException(String par1Str, Object... par2ArrayOfObj)
    {
        super(par1Str, par2ArrayOfObj);
    }
}

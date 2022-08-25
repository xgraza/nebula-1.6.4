package net.minecraft.command;

import com.google.common.primitives.Doubles;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public abstract class CommandBase implements ICommand
{
    private static IAdminCommand theAdmin;
    private static final String __OBFID = "CL_00001739";

    public int getRequiredPermissionLevel()
    {
        return 4;
    }

    public List getCommandAliases()
    {
        return null;
    }

    public boolean canCommandSenderUseCommand(ICommandSender par1ICommandSender)
    {
        return par1ICommandSender.canCommandSenderUseCommand(this.getRequiredPermissionLevel(), this.getCommandName());
    }

    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        return null;
    }

    public static int parseInt(ICommandSender par0ICommandSender, String par1Str)
    {
        try
        {
            return Integer.parseInt(par1Str);
        }
        catch (NumberFormatException var3)
        {
            throw new NumberInvalidException("commands.generic.num.invalid", new Object[] {par1Str});
        }
    }

    public static int parseIntWithMin(ICommandSender par0ICommandSender, String par1Str, int par2)
    {
        return parseIntBounded(par0ICommandSender, par1Str, par2, Integer.MAX_VALUE);
    }

    public static int parseIntBounded(ICommandSender par0ICommandSender, String par1Str, int par2, int par3)
    {
        int var4 = parseInt(par0ICommandSender, par1Str);

        if (var4 < par2)
        {
            throw new NumberInvalidException("commands.generic.num.tooSmall", new Object[] {Integer.valueOf(var4), Integer.valueOf(par2)});
        }
        else if (var4 > par3)
        {
            throw new NumberInvalidException("commands.generic.num.tooBig", new Object[] {Integer.valueOf(var4), Integer.valueOf(par3)});
        }
        else
        {
            return var4;
        }
    }

    public static double parseDouble(ICommandSender par0ICommandSender, String par1Str)
    {
        try
        {
            double var2 = Double.parseDouble(par1Str);

            if (!Doubles.isFinite(var2))
            {
                throw new NumberInvalidException("commands.generic.num.invalid", new Object[] {par1Str});
            }
            else
            {
                return var2;
            }
        }
        catch (NumberFormatException var4)
        {
            throw new NumberInvalidException("commands.generic.num.invalid", new Object[] {par1Str});
        }
    }

    public static double parseDoubleWithMin(ICommandSender par0ICommandSender, String par1Str, double par2)
    {
        return parseDoubleBounded(par0ICommandSender, par1Str, par2, Double.MAX_VALUE);
    }

    public static double parseDoubleBounded(ICommandSender par0ICommandSender, String par1Str, double par2, double par4)
    {
        double var6 = parseDouble(par0ICommandSender, par1Str);

        if (var6 < par2)
        {
            throw new NumberInvalidException("commands.generic.double.tooSmall", new Object[] {Double.valueOf(var6), Double.valueOf(par2)});
        }
        else if (var6 > par4)
        {
            throw new NumberInvalidException("commands.generic.double.tooBig", new Object[] {Double.valueOf(var6), Double.valueOf(par4)});
        }
        else
        {
            return var6;
        }
    }

    public static boolean parseBoolean(ICommandSender par0ICommandSender, String par1Str)
    {
        if (!par1Str.equals("true") && !par1Str.equals("1"))
        {
            if (!par1Str.equals("false") && !par1Str.equals("0"))
            {
                throw new CommandException("commands.generic.boolean.invalid", new Object[] {par1Str});
            }
            else
            {
                return false;
            }
        }
        else
        {
            return true;
        }
    }

    public static EntityPlayerMP getCommandSenderAsPlayer(ICommandSender par0ICommandSender)
    {
        if (par0ICommandSender instanceof EntityPlayerMP)
        {
            return (EntityPlayerMP)par0ICommandSender;
        }
        else
        {
            throw new PlayerNotFoundException("You must specify which player you wish to perform this action on.", new Object[0]);
        }
    }

    public static EntityPlayerMP getPlayer(ICommandSender par0ICommandSender, String par1Str)
    {
        EntityPlayerMP var2 = PlayerSelector.matchOnePlayer(par0ICommandSender, par1Str);

        if (var2 != null)
        {
            return var2;
        }
        else
        {
            var2 = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(par1Str);

            if (var2 == null)
            {
                throw new PlayerNotFoundException();
            }
            else
            {
                return var2;
            }
        }
    }

    public static String func_96332_d(ICommandSender par0ICommandSender, String par1Str)
    {
        EntityPlayerMP var2 = PlayerSelector.matchOnePlayer(par0ICommandSender, par1Str);

        if (var2 != null)
        {
            return var2.getCommandSenderName();
        }
        else if (PlayerSelector.hasArguments(par1Str))
        {
            throw new PlayerNotFoundException();
        }
        else
        {
            return par1Str;
        }
    }

    public static IChatComponent func_147178_a(ICommandSender p_147178_0_, String[] p_147178_1_, int p_147178_2_)
    {
        return func_147176_a(p_147178_0_, p_147178_1_, p_147178_2_, false);
    }

    public static IChatComponent func_147176_a(ICommandSender p_147176_0_, String[] p_147176_1_, int p_147176_2_, boolean p_147176_3_)
    {
        ChatComponentText var4 = new ChatComponentText("");

        for (int var5 = p_147176_2_; var5 < p_147176_1_.length; ++var5)
        {
            if (var5 > p_147176_2_)
            {
                var4.appendText(" ");
            }

            Object var6 = new ChatComponentText(p_147176_1_[var5]);

            if (p_147176_3_)
            {
                IChatComponent var7 = PlayerSelector.func_150869_b(p_147176_0_, p_147176_1_[var5]);

                if (var7 != null)
                {
                    var6 = var7;
                }
                else if (PlayerSelector.hasArguments(p_147176_1_[var5]))
                {
                    throw new PlayerNotFoundException();
                }
            }

            var4.appendSibling((IChatComponent)var6);
        }

        return var4;
    }

    public static String func_82360_a(ICommandSender par0ICommandSender, String[] par1ArrayOfStr, int par2)
    {
        StringBuilder var3 = new StringBuilder();

        for (int var4 = par2; var4 < par1ArrayOfStr.length; ++var4)
        {
            if (var4 > par2)
            {
                var3.append(" ");
            }

            String var5 = par1ArrayOfStr[var4];
            var3.append(var5);
        }

        return var3.toString();
    }

    public static double func_110666_a(ICommandSender par0ICommandSender, double par1, String par3Str)
    {
        return func_110665_a(par0ICommandSender, par1, par3Str, -30000000, 30000000);
    }

    public static double func_110665_a(ICommandSender par0ICommandSender, double par1, String par3Str, int par4, int par5)
    {
        boolean var6 = par3Str.startsWith("~");

        if (var6 && Double.isNaN(par1))
        {
            throw new NumberInvalidException("commands.generic.num.invalid", new Object[] {Double.valueOf(par1)});
        }
        else
        {
            double var7 = var6 ? par1 : 0.0D;

            if (!var6 || par3Str.length() > 1)
            {
                boolean var9 = par3Str.contains(".");

                if (var6)
                {
                    par3Str = par3Str.substring(1);
                }

                var7 += parseDouble(par0ICommandSender, par3Str);

                if (!var9 && !var6)
                {
                    var7 += 0.5D;
                }
            }

            if (par4 != 0 || par5 != 0)
            {
                if (var7 < (double)par4)
                {
                    throw new NumberInvalidException("commands.generic.double.tooSmall", new Object[] {Double.valueOf(var7), Integer.valueOf(par4)});
                }

                if (var7 > (double)par5)
                {
                    throw new NumberInvalidException("commands.generic.double.tooBig", new Object[] {Double.valueOf(var7), Integer.valueOf(par5)});
                }
            }

            return var7;
        }
    }

    public static Item getItemByText(ICommandSender p_147179_0_, String p_147179_1_)
    {
        Item var2 = (Item)Item.itemRegistry.getObject(p_147179_1_);

        if (var2 == null)
        {
            try
            {
                Item var3 = Item.getItemById(Integer.parseInt(p_147179_1_));

                if (var3 != null)
                {
                    ChatComponentTranslation var4 = new ChatComponentTranslation("commands.generic.deprecatedId", new Object[] {Item.itemRegistry.getNameForObject(var3)});
                    var4.getChatStyle().setColor(EnumChatFormatting.GRAY);
                    p_147179_0_.addChatMessage(var4);
                }

                var2 = var3;
            }
            catch (NumberFormatException var5)
            {
                ;
            }
        }

        if (var2 == null)
        {
            throw new NumberInvalidException("commands.give.notFound", new Object[] {p_147179_1_});
        }
        else
        {
            return var2;
        }
    }

    public static Block getBlockByText(ICommandSender p_147180_0_, String p_147180_1_)
    {
        if (Block.blockRegistry.containsKey(p_147180_1_))
        {
            return (Block)Block.blockRegistry.getObject(p_147180_1_);
        }
        else
        {
            try
            {
                int var2 = Integer.parseInt(p_147180_1_);

                if (Block.blockRegistry.containsID(var2))
                {
                    Block var3 = Block.getBlockById(var2);
                    ChatComponentTranslation var4 = new ChatComponentTranslation("commands.generic.deprecatedId", new Object[] {Block.blockRegistry.getNameForObject(var3)});
                    var4.getChatStyle().setColor(EnumChatFormatting.GRAY);
                    p_147180_0_.addChatMessage(var4);
                    return var3;
                }
            }
            catch (NumberFormatException var5)
            {
                ;
            }

            throw new NumberInvalidException("commands.give.notFound", new Object[] {p_147180_1_});
        }
    }

    public static String joinNiceString(Object[] par0ArrayOfObj)
    {
        StringBuilder var1 = new StringBuilder();

        for (int var2 = 0; var2 < par0ArrayOfObj.length; ++var2)
        {
            String var3 = par0ArrayOfObj[var2].toString();

            if (var2 > 0)
            {
                if (var2 == par0ArrayOfObj.length - 1)
                {
                    var1.append(" and ");
                }
                else
                {
                    var1.append(", ");
                }
            }

            var1.append(var3);
        }

        return var1.toString();
    }

    public static IChatComponent joinNiceString(IChatComponent[] p_147177_0_)
    {
        ChatComponentText var1 = new ChatComponentText("");

        for (int var2 = 0; var2 < p_147177_0_.length; ++var2)
        {
            if (var2 > 0)
            {
                if (var2 == p_147177_0_.length - 1)
                {
                    var1.appendText(" and ");
                }
                else if (var2 > 0)
                {
                    var1.appendText(", ");
                }
            }

            var1.appendSibling(p_147177_0_[var2]);
        }

        return var1;
    }

    public static String joinNiceStringFromCollection(Collection par0Collection)
    {
        return joinNiceString(par0Collection.toArray(new String[par0Collection.size()]));
    }

    public static boolean doesStringStartWith(String par0Str, String par1Str)
    {
        return par1Str.regionMatches(true, 0, par0Str, 0, par0Str.length());
    }

    public static List getListOfStringsMatchingLastWord(String[] par0ArrayOfStr, String ... par1ArrayOfStr)
    {
        String var2 = par0ArrayOfStr[par0ArrayOfStr.length - 1];
        ArrayList var3 = new ArrayList();
        String[] var4 = par1ArrayOfStr;
        int var5 = par1ArrayOfStr.length;

        for (int var6 = 0; var6 < var5; ++var6)
        {
            String var7 = var4[var6];

            if (doesStringStartWith(var2, var7))
            {
                var3.add(var7);
            }
        }

        return var3;
    }

    public static List getListOfStringsFromIterableMatchingLastWord(String[] par0ArrayOfStr, Iterable par1Iterable)
    {
        String var2 = par0ArrayOfStr[par0ArrayOfStr.length - 1];
        ArrayList var3 = new ArrayList();
        Iterator var4 = par1Iterable.iterator();

        while (var4.hasNext())
        {
            String var5 = (String)var4.next();

            if (doesStringStartWith(var2, var5))
            {
                var3.add(var5);
            }
        }

        return var3;
    }

    public boolean isUsernameIndex(String[] par1ArrayOfStr, int par2)
    {
        return false;
    }

    public static void notifyAdmins(ICommandSender par0ICommandSender, String par1Str, Object ... par2ArrayOfObj)
    {
        notifyAdmins(par0ICommandSender, 0, par1Str, par2ArrayOfObj);
    }

    public static void notifyAdmins(ICommandSender par0ICommandSender, int par1, String par2Str, Object ... par3ArrayOfObj)
    {
        if (theAdmin != null)
        {
            theAdmin.notifyAdmins(par0ICommandSender, par1, par2Str, par3ArrayOfObj);
        }
    }

    public static void setAdminCommander(IAdminCommand par0IAdminCommand)
    {
        theAdmin = par0IAdminCommand;
    }

    public int compareTo(ICommand par1ICommand)
    {
        return this.getCommandName().compareTo(par1ICommand.getCommandName());
    }

    public int compareTo(Object par1Obj)
    {
        return this.compareTo((ICommand)par1Obj);
    }
}

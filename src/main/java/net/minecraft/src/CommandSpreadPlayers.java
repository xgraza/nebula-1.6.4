package net.minecraft.src;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.server.MinecraftServer;

public class CommandSpreadPlayers extends CommandBase
{
    public String getCommandName()
    {
        return "spreadplayers";
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    public String getCommandUsage(ICommandSender par1ICommandSender)
    {
        return "commands.spreadplayers.usage";
    }

    public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        if (par2ArrayOfStr.length < 6)
        {
            throw new WrongUsageException("commands.spreadplayers.usage", new Object[0]);
        }
        else
        {
            byte var3 = 0;
            int var16 = var3 + 1;
            double var4 = func_110666_a(par1ICommandSender, Double.NaN, par2ArrayOfStr[var3]);
            double var6 = func_110666_a(par1ICommandSender, Double.NaN, par2ArrayOfStr[var16++]);
            double var8 = func_110664_a(par1ICommandSender, par2ArrayOfStr[var16++], 0.0D);
            double var10 = func_110664_a(par1ICommandSender, par2ArrayOfStr[var16++], var8 + 1.0D);
            boolean var12 = func_110662_c(par1ICommandSender, par2ArrayOfStr[var16++]);
            ArrayList var13 = Lists.newArrayList();

            while (true)
            {
                while (var16 < par2ArrayOfStr.length)
                {
                    String var14 = par2ArrayOfStr[var16++];

                    if (PlayerSelector.hasArguments(var14))
                    {
                        EntityPlayerMP[] var17 = PlayerSelector.matchPlayers(par1ICommandSender, var14);

                        if (var17 == null || var17.length == 0)
                        {
                            throw new PlayerNotFoundException();
                        }

                        Collections.addAll(var13, var17);
                    }
                    else
                    {
                        EntityPlayerMP var15 = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(var14);

                        if (var15 == null)
                        {
                            throw new PlayerNotFoundException();
                        }

                        var13.add(var15);
                    }
                }

                if (var13.isEmpty())
                {
                    throw new PlayerNotFoundException();
                }

                par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions("commands.spreadplayers.spreading." + (var12 ? "teams" : "players"), new Object[] {func_110663_b(var13), Double.valueOf(var4), Double.valueOf(var6), Double.valueOf(var8), Double.valueOf(var10)}));
                this.func_110669_a(par1ICommandSender, var13, new CommandSpreadPlayersPosition(var4, var6), var8, var10, ((EntityLivingBase)var13.get(0)).worldObj, var12);
                return;
            }
        }
    }

    private void func_110669_a(ICommandSender par1ICommandSender, List par2List, CommandSpreadPlayersPosition par3CommandSpreadPlayersPosition, double par4, double par6, World par8World, boolean par9)
    {
        Random var10 = new Random();
        double var11 = par3CommandSpreadPlayersPosition.field_111101_a - par6;
        double var13 = par3CommandSpreadPlayersPosition.field_111100_b - par6;
        double var15 = par3CommandSpreadPlayersPosition.field_111101_a + par6;
        double var17 = par3CommandSpreadPlayersPosition.field_111100_b + par6;
        CommandSpreadPlayersPosition[] var19 = this.func_110670_a(var10, par9 ? this.func_110667_a(par2List) : par2List.size(), var11, var13, var15, var17);
        int var20 = this.func_110668_a(par3CommandSpreadPlayersPosition, par4, par8World, var10, var11, var13, var15, var17, var19, par9);
        double var21 = this.func_110671_a(par2List, par8World, var19, par9);
        notifyAdmins(par1ICommandSender, "commands.spreadplayers.success." + (par9 ? "teams" : "players"), new Object[] {Integer.valueOf(var19.length), Double.valueOf(par3CommandSpreadPlayersPosition.field_111101_a), Double.valueOf(par3CommandSpreadPlayersPosition.field_111100_b)});

        if (var19.length > 1)
        {
            par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions("commands.spreadplayers.info." + (par9 ? "teams" : "players"), new Object[] {String.format("%.2f", new Object[]{Double.valueOf(var21)}), Integer.valueOf(var20)}));
        }
    }

    private int func_110667_a(List par1List)
    {
        HashSet var2 = Sets.newHashSet();
        Iterator var3 = par1List.iterator();

        while (var3.hasNext())
        {
            EntityLivingBase var4 = (EntityLivingBase)var3.next();

            if (var4 instanceof EntityPlayer)
            {
                var2.add(((EntityPlayer)var4).getTeam());
            }
            else
            {
                var2.add((Object)null);
            }
        }

        return var2.size();
    }

    private int func_110668_a(CommandSpreadPlayersPosition par1CommandSpreadPlayersPosition, double par2, World par4World, Random par5Random, double par6, double par8, double par10, double par12, CommandSpreadPlayersPosition[] par14ArrayOfCommandSpreadPlayersPosition, boolean par15)
    {
        boolean var16 = true;
        double var18 = 3.4028234663852886E38D;
        int var17;

        for (var17 = 0; var17 < 10000 && var16; ++var17)
        {
            var16 = false;
            var18 = 3.4028234663852886E38D;
            int var22;
            CommandSpreadPlayersPosition var23;

            for (int var20 = 0; var20 < par14ArrayOfCommandSpreadPlayersPosition.length; ++var20)
            {
                CommandSpreadPlayersPosition var21 = par14ArrayOfCommandSpreadPlayersPosition[var20];
                var22 = 0;
                var23 = new CommandSpreadPlayersPosition();

                for (int var24 = 0; var24 < par14ArrayOfCommandSpreadPlayersPosition.length; ++var24)
                {
                    if (var20 != var24)
                    {
                        CommandSpreadPlayersPosition var25 = par14ArrayOfCommandSpreadPlayersPosition[var24];
                        double var26 = var21.func_111099_a(var25);
                        var18 = Math.min(var26, var18);

                        if (var26 < par2)
                        {
                            ++var22;
                            var23.field_111101_a += var25.field_111101_a - var21.field_111101_a;
                            var23.field_111100_b += var25.field_111100_b - var21.field_111100_b;
                        }
                    }
                }

                if (var22 > 0)
                {
                    var23.field_111101_a /= (double)var22;
                    var23.field_111100_b /= (double)var22;
                    double var30 = (double)var23.func_111096_b();

                    if (var30 > 0.0D)
                    {
                        var23.func_111095_a();
                        var21.func_111094_b(var23);
                    }
                    else
                    {
                        var21.func_111097_a(par5Random, par6, par8, par10, par12);
                    }

                    var16 = true;
                }

                if (var21.func_111093_a(par6, par8, par10, par12))
                {
                    var16 = true;
                }
            }

            if (!var16)
            {
                CommandSpreadPlayersPosition[] var28 = par14ArrayOfCommandSpreadPlayersPosition;
                int var29 = par14ArrayOfCommandSpreadPlayersPosition.length;

                for (var22 = 0; var22 < var29; ++var22)
                {
                    var23 = var28[var22];

                    if (!var23.func_111098_b(par4World))
                    {
                        var23.func_111097_a(par5Random, par6, par8, par10, par12);
                        var16 = true;
                    }
                }
            }
        }

        if (var17 >= 10000)
        {
            throw new CommandException("commands.spreadplayers.failure." + (par15 ? "teams" : "players"), new Object[] {Integer.valueOf(par14ArrayOfCommandSpreadPlayersPosition.length), Double.valueOf(par1CommandSpreadPlayersPosition.field_111101_a), Double.valueOf(par1CommandSpreadPlayersPosition.field_111100_b), String.format("%.2f", new Object[]{Double.valueOf(var18)})});
        }
        else
        {
            return var17;
        }
    }

    private double func_110671_a(List par1List, World par2World, CommandSpreadPlayersPosition[] par3ArrayOfCommandSpreadPlayersPosition, boolean par4)
    {
        double var5 = 0.0D;
        int var7 = 0;
        HashMap var8 = Maps.newHashMap();

        for (int var9 = 0; var9 < par1List.size(); ++var9)
        {
            EntityLivingBase var10 = (EntityLivingBase)par1List.get(var9);
            CommandSpreadPlayersPosition var11;

            if (par4)
            {
                Team var12 = var10 instanceof EntityPlayer ? ((EntityPlayer)var10).getTeam() : null;

                if (!var8.containsKey(var12))
                {
                    var8.put(var12, par3ArrayOfCommandSpreadPlayersPosition[var7++]);
                }

                var11 = (CommandSpreadPlayersPosition)var8.get(var12);
            }
            else
            {
                var11 = par3ArrayOfCommandSpreadPlayersPosition[var7++];
            }

            var10.setPositionAndUpdate((double)((float)MathHelper.floor_double(var11.field_111101_a) + 0.5F), (double)var11.func_111092_a(par2World), (double)MathHelper.floor_double(var11.field_111100_b) + 0.5D);
            double var17 = Double.MAX_VALUE;

            for (int var14 = 0; var14 < par3ArrayOfCommandSpreadPlayersPosition.length; ++var14)
            {
                if (var11 != par3ArrayOfCommandSpreadPlayersPosition[var14])
                {
                    double var15 = var11.func_111099_a(par3ArrayOfCommandSpreadPlayersPosition[var14]);
                    var17 = Math.min(var15, var17);
                }
            }

            var5 += var17;
        }

        var5 /= (double)par1List.size();
        return var5;
    }

    private CommandSpreadPlayersPosition[] func_110670_a(Random par1Random, int par2, double par3, double par5, double par7, double par9)
    {
        CommandSpreadPlayersPosition[] var11 = new CommandSpreadPlayersPosition[par2];

        for (int var12 = 0; var12 < var11.length; ++var12)
        {
            CommandSpreadPlayersPosition var13 = new CommandSpreadPlayersPosition();
            var13.func_111097_a(par1Random, par3, par5, par7, par9);
            var11[var12] = var13;
        }

        return var11;
    }
}

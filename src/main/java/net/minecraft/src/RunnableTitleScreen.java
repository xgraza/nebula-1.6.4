package net.minecraft.src;

import java.io.IOException;

class RunnableTitleScreen extends Thread
{
    final GuiMainMenu theMainMenu;

    RunnableTitleScreen(GuiMainMenu par1GuiMainMenu)
    {
        this.theMainMenu = par1GuiMainMenu;
    }

    public void run()
    {
        McoClient var1 = new McoClient(GuiMainMenu.func_110348_a(this.theMainMenu).getSession());
        boolean var2 = false;

        for (int var3 = 0; var3 < 3; ++var3)
        {
            try
            {
                Boolean var4 = var1.func_96375_b();

                if (var4.booleanValue())
                {
                    GuiMainMenu.func_130021_b(this.theMainMenu);
                }

                GuiMainMenu.func_110349_a(var4.booleanValue());
            }
            catch (ExceptionRetryCall var6)
            {
                var2 = true;
            }
            catch (ExceptionMcoService var7)
            {
                GuiMainMenu.func_130018_c(this.theMainMenu).getLogAgent().logSevere(var7.toString());
            }
            catch (IOException var8)
            {
                GuiMainMenu.func_130019_d(this.theMainMenu).getLogAgent().logWarning("Realms: could not parse response");
            }

            if (!var2)
            {
                break;
            }

            try
            {
                Thread.sleep(10000L);
            }
            catch (InterruptedException var5)
            {
                Thread.currentThread().interrupt();
            }
        }
    }
}

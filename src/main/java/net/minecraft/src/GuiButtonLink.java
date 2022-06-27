package net.minecraft.src;

import java.net.URI;

public class GuiButtonLink extends GuiButton
{
    public GuiButtonLink(int par1, int par2, int par3, int par4, int par5, String par6Str)
    {
        super(par1, par2, par3, par4, par5, par6Str);
    }

    public void func_96135_a(String par1Str)
    {
        try
        {
            URI var2 = new URI(par1Str);
            Class var3 = Class.forName("java.awt.Desktop");
            Object var4 = var3.getMethod("getDesktop", new Class[0]).invoke((Object)null, new Object[0]);
            var3.getMethod("browse", new Class[] {URI.class}).invoke(var4, new Object[] {var2});
        }
        catch (Throwable var5)
        {
            var5.printStackTrace();
        }
    }
}

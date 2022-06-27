package net.minecraft.src;

import java.util.concurrent.Callable;

class MinecraftINNER13 implements Callable
{
    final Minecraft field_142056_a;

    MinecraftINNER13(Minecraft par1Minecraft)
    {
        this.field_142056_a = par1Minecraft;
    }

    public String func_142055_a()
    {
        int var1 = this.field_142056_a.theWorld.getWorldVec3Pool().getPoolSize();
        int var2 = 56 * var1;
        int var3 = var2 / 1024 / 1024;
        int var4 = this.field_142056_a.theWorld.getWorldVec3Pool().func_82590_d();
        int var5 = 56 * var4;
        int var6 = var5 / 1024 / 1024;
        return var1 + " (" + var2 + " bytes; " + var3 + " MB) allocated, " + var4 + " (" + var5 + " bytes; " + var6 + " MB) used";
    }

    public Object call()
    {
        return this.func_142055_a();
    }
}

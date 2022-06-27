package net.minecraft.src;

import java.util.concurrent.Callable;

class WorldClientINNER3 implements Callable
{
    final WorldClient theWorldClient;

    WorldClientINNER3(WorldClient par1WorldClient)
    {
        this.theWorldClient = par1WorldClient;
    }

    public String func_142026_a()
    {
        return WorldClient.func_142030_c(this.theWorldClient).thePlayer.func_142021_k();
    }

    public Object call()
    {
        return this.func_142026_a();
    }
}

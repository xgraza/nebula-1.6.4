package net.minecraft.src;

import java.util.concurrent.Callable;

class WorldClientINNER4 implements Callable
{
    final WorldClient theWorldClient;

    WorldClientINNER4(WorldClient par1WorldClient)
    {
        this.theWorldClient = par1WorldClient;
    }

    public String func_142028_a()
    {
        return WorldClient.func_142030_c(this.theWorldClient).getIntegratedServer() == null ? "Non-integrated multiplayer server" : "Integrated singleplayer server";
    }

    public Object call()
    {
        return this.func_142028_a();
    }
}

package net.minecraft.src;

import java.util.concurrent.Callable;

class CallableConnectionName implements Callable
{
    final NetServerHandler field_111201_a;

    final NetworkListenThread field_111200_b;

    CallableConnectionName(NetworkListenThread par1NetworkListenThread, NetServerHandler par2NetServerHandler)
    {
        this.field_111200_b = par1NetworkListenThread;
        this.field_111201_a = par2NetServerHandler;
    }

    public String func_111199_a()
    {
        return this.field_111201_a.toString();
    }

    public Object call()
    {
        return this.func_111199_a();
    }
}

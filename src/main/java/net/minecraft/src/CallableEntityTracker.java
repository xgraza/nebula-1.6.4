package net.minecraft.src;

import java.util.concurrent.Callable;

class CallableEntityTracker implements Callable
{
    final int field_96570_a;

    final EntityTracker theEntityTracker;

    CallableEntityTracker(EntityTracker par1EntityTracker, int par2)
    {
        this.theEntityTracker = par1EntityTracker;
        this.field_96570_a = par2;
    }

    public String func_96568_a()
    {
        String var1 = "Once per " + this.field_96570_a + " ticks";

        if (this.field_96570_a == Integer.MAX_VALUE)
        {
            var1 = "Maximum (" + var1 + ")";
        }

        return var1;
    }

    public Object call()
    {
        return this.func_96568_a();
    }
}

package net.minecraft.src;

import java.util.concurrent.Callable;

final class CallableBlockDataValue implements Callable
{
    final int field_85063_a;

    CallableBlockDataValue(int par1)
    {
        this.field_85063_a = par1;
    }

    public String callBlockDataValue()
    {
        if (this.field_85063_a < 0)
        {
            return "Unknown? (Got " + this.field_85063_a + ")";
        }
        else
        {
            String var1 = String.format("%4s", new Object[] {Integer.toBinaryString(this.field_85063_a)}).replace(" ", "0");
            return String.format("%1$d / 0x%1$X / 0b%2$s", new Object[] {Integer.valueOf(this.field_85063_a), var1});
        }
    }

    public Object call()
    {
        return this.callBlockDataValue();
    }
}

package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagLong extends NBTBase.NBTPrimitive
{
    private long data;
    private static final String __OBFID = "CL_00001225";

    NBTTagLong() {}

    public NBTTagLong(long p_i45134_1_)
    {
        this.data = p_i45134_1_;
    }

    void write(DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeLong(this.data);
    }

    void load(DataInput par1DataInput, int par2) throws IOException
    {
        this.data = par1DataInput.readLong();
    }

    public byte getId()
    {
        return (byte)4;
    }

    public String toString()
    {
        return "" + this.data + "L";
    }

    public NBTBase copy()
    {
        return new NBTTagLong(this.data);
    }

    public boolean equals(Object par1Obj)
    {
        if (super.equals(par1Obj))
        {
            NBTTagLong var2 = (NBTTagLong)par1Obj;
            return this.data == var2.data;
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {
        return super.hashCode() ^ (int)(this.data ^ this.data >>> 32);
    }

    public long func_150291_c()
    {
        return this.data;
    }

    public int func_150287_d()
    {
        return (int)(this.data & -1L);
    }

    public short func_150289_e()
    {
        return (short)((int)(this.data & 65535L));
    }

    public byte func_150290_f()
    {
        return (byte)((int)(this.data & 255L));
    }

    public double func_150286_g()
    {
        return (double)this.data;
    }

    public float func_150288_h()
    {
        return (float)this.data;
    }
}
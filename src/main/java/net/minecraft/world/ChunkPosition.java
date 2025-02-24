package net.minecraft.world;

import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class ChunkPosition
{
    public final int xCoord;
    public final int field_151327_b;
    public final int yCoord;
    private static final String __OBFID = "CL_00000132";

    public ChunkPosition(int xCoord, int yCoord, int zCoord)
    {
        this.xCoord = xCoord;
        this.field_151327_b = yCoord;
        this.yCoord = zCoord;
    }

    public ChunkPosition(Vec3 p_i45364_1_)
    {
        this(MathHelper.floor_double(p_i45364_1_.xCoord), MathHelper.floor_double(p_i45364_1_.yCoord), MathHelper.floor_double(p_i45364_1_.zCoord));
    }

    public boolean equals(Object par1Obj)
    {
        if (!(par1Obj instanceof ChunkPosition))
        {
            return false;
        }
        else
        {
            ChunkPosition var2 = (ChunkPosition)par1Obj;
            return var2.xCoord == this.xCoord && var2.field_151327_b == this.field_151327_b && var2.yCoord == this.yCoord;
        }
    }

    public int hashCode()
    {
        return this.xCoord * 8976890 + this.field_151327_b * 981131 + this.yCoord;
    }
}

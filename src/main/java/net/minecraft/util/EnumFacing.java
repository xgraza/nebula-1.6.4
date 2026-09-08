package net.minecraft.util;

import net.minecraft.src.BlockPos;

public enum EnumFacing
{
    DOWN(0, 1, 0, -1, 0),
    UP(1, 0, 0, 1, 0),
    NORTH(2, 3, 0, 0, -1),
    SOUTH(3, 2, 0, 0, 1),
    WEST(4, 5, -1, 0, 0),
    EAST(5, 4, 1, 0, 0);

    /**
     * Face order for D-U-N-S-E-W.
     */
    public final int order_a;

    /**
     * Face order for U-D-S-N-W-E.
     */
    public final int order_b;
    private final int frontOffsetX;
    private final int frontOffsetY;
    private final int frontOffsetZ;

    private final BlockPos offsetPos;

    /**
     * List of all values in EnumFacing. Order is D-U-N-S-E-W.
     */
    public static final EnumFacing[] faceList = new EnumFacing[6];

    EnumFacing(int par3, int par4, int frontOffsetX, int frontOffsetY, int frontOffsetZ)
    {
        this.order_a = par3;
        this.order_b = par4;
        this.frontOffsetX = frontOffsetX;
        this.frontOffsetY = frontOffsetY;
        this.frontOffsetZ = frontOffsetZ;
        this.offsetPos = new BlockPos(frontOffsetX, frontOffsetY, frontOffsetZ);
    }

    /**
     * Returns a offset that addresses the block in front of this facing.
     */
    public int getFaceX()
    {
        return this.frontOffsetX;
    }

    public int getFaceY()
    {
        return this.frontOffsetY;
    }

    /**
     * Returns a offset that addresses the block in front of this facing.
     */
    public int getFaceZ()
    {
        return this.frontOffsetZ;
    }

    public BlockPos getFaceOffset()
    {
        return offsetPos;
    }

    /**
     * Returns the opposite face
     */
    public EnumFacing getOpposite()
    {
        return values()[order_b];
    }

    /**
     * Returns the facing that represents the block in front of it.
     */
    public static EnumFacing getFront(int par0)
    {
        return faceList[par0 % faceList.length];
    }

    static
    {
        EnumFacing[] var0 = values();
        int var1 = var0.length;

        for (int var2 = 0; var2 < var1; ++var2)
        {
            EnumFacing var3 = var0[var2];
            faceList[var3.order_a] = var3;
        }
    }
}

package us.nebula.client.util.render;

import net.minecraft.util.EnumFacing;

public final class QuadMask
{
    public static final int UP = 0x02;
    public static final int DOWN = 0x01;
    public static final int NORTH = 0x04;
    public static final int SOUTH = 0x08;
    public static final int EAST = 0x20;
    public static final int WEST = 0x10;

    private static final int[] values = { UP, DOWN, NORTH, SOUTH, EAST, WEST };
    public static final int ALL_FACES;

    static
    {
        int allMask = 0;
        for (int value : values)
        {
            allMask |= value;
        }
        ALL_FACES = allMask;
    }

    public static int getMask(final EnumFacing facing)
    {
        return values[facing.order_a];
    }
}

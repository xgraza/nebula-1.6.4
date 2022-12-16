package net.minecraft.util;

public enum EnumFacing
{
    DOWN(0, 1, 0, -1, 0),
    UP(1, 0, 0, 1, 0),
    NORTH(2, 3, 0, 0, -1, 2),
    SOUTH(3, 2, 0, 0, 1, 0),
    EAST(4, 5, -1, 0, 0, 1),
    WEST(5, 4, 1, 0, 0, 3);

    public final int hIndex;
    public final int order_a;
    public final int order_b;
    private final int frontOffsetX;
    private final int frontOffsetY;
    private final int frontOffsetZ;

    public static final EnumFacing[] faceList = new EnumFacing[6];
    private static final String __OBFID = "CL_00001201";

    public static final EnumFacing[] HORIZONTALS = new EnumFacing[4];

    private EnumFacing(int order_a, int order_b, int fX, int fY, int fZ, int hIndex) {
        this.order_a = order_a;
        this.order_b = order_b;
        this.frontOffsetX = fX;
        this.frontOffsetY = fY;
        this.frontOffsetZ = fZ;
        this.hIndex = hIndex;
    }

    private EnumFacing(int par3, int par4, int par5, int par6, int par7)
    {
        this(par3, par4, par5, par6, par7, -1);
    }

    public int getFrontOffsetX()
    {
        return this.frontOffsetX;
    }

    public int getFrontOffsetY()
    {
        return this.frontOffsetY;
    }

    public int getFrontOffsetZ()
    {
        return this.frontOffsetZ;
    }

    public static EnumFacing getFront(int par0)
    {
        return faceList[par0 % faceList.length];
    }

    public static EnumFacing getHorizontal(int f) {
        return HORIZONTALS[Math.abs(f % HORIZONTALS.length)];
    }

    public EnumFacing getOpposite() {
        for (EnumFacing facing : values()) {
            if (facing.order_a == order_b) {
                return facing;
            }
        }

        return null;
    }

    static {
        EnumFacing[] var0 = values();
        int var1 = var0.length;

        for (int var2 = 0; var2 < var1; ++var2)
        {
            EnumFacing var3 = var0[var2];
            faceList[var3.order_a] = var3;
        }

        for (int i = 0; i < var1; ++i) {
            EnumFacing facing = var0[i];
            if (facing.equals(EnumFacing.UP) || facing.equals(EnumFacing.DOWN)) {
                continue;
            }

            HORIZONTALS[facing.hIndex] = facing;
        }
    }
}

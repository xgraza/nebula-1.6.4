package net.minecraft.util;

public class Facing
{
    public static final int[] oppositeSide = new int[] {1, 0, 3, 2, 5, 4};
    public static final int[] offsetsXForSide = new int[] {0, 0, 0, 0, -1, 1};
    public static final int[] offsetsYForSide = new int[] { -1, 1, 0, 0, 0, 0};
    public static final int[] offsetsZForSide = new int[] {0, 0, -1, 1, 0, 0};
    public static final String[] facings = new String[] {"DOWN", "UP", "NORTH", "SOUTH", "WEST", "EAST"};
    private static final String __OBFID = "CL_00001532";
}

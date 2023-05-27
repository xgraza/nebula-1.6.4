package lol.nebula.util.world;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

/**
 * @author aesthetical
 * @since 05/02/23
 */
public class WorldUtils {
    /**
     * The minecraft game instance
     */
    private static final Minecraft mc = Minecraft.getMinecraft();

    /**
     * Checks if a block is replaceable
     * @param pos the vector the block is at
     * @return if that block can be replaced at that position
     */
    public static boolean isReplaceable(Vec3 pos) {
        Block block = mc.theWorld.getBlock(
                (int) pos.xCoord,
                (int) pos.yCoord,
                (int) pos.zCoord);
        return block != null && block.getMaterial().isReplaceable();
    }

    /**
     * Checks if a block is replaceable
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @return if that block can be replaced at that position
     */
    public static boolean isReplaceable(int x, int y, int z) {
        Block block = mc.theWorld.getBlock(x, y, z);
        return block != null && block.getMaterial().isReplaceable();
    }

    /**
     * Gets the block at these coordinates
     * @param pos the coordinate vector the block is at
     * @return the block or Blocks.air
     */
    public static Block getBlock(Vec3 pos) {
        return mc.theWorld.getBlock(
                (int) pos.xCoord,
                (int) pos.yCoord,
                (int) pos.zCoord);
    }

    /**
     * Gets the block at these coordinates
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @return the block or Blocks.air
     */
    public static Block getBlock(int x, int y, int z) {
        return mc.theWorld.getBlock(x, y, z);
    }

    /**
     * Gets the opposite facing enum for the inputted face
     * @param facing the face
     * @return the opposite face enum constant
     */
    public static EnumFacing getOpposite(EnumFacing facing) {
        return EnumFacing.faceList[facing.getOrder_b()];
    }

    /**
     * Gets a hit vector for a block placement
     * @param vec the xyz vector
     * @param facing the facing value
     * @return the hit vector for this placement
     */
    public static Vec3 getHitVec(Vec3 vec, EnumFacing facing) {
        return getHitVec((int) vec.xCoord, (int) vec.yCoord, (int) vec.zCoord, facing);
    }

    /**
     * Gets a hit vector for a block placement
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @param facing the facing value
     * @return the hit vector for this placement
     */
    public static Vec3 getHitVec(int x, int y, int z, EnumFacing facing) {
        return new Vec3(Vec3.fakePool, x, y, z)
                .addVector(0.5, 0.5, 0.5)
                .addVector(facing.getFrontOffsetX() * 0.5,
                        facing.getFrontOffsetY() * 0.5,
                        facing.getFrontOffsetZ() * 0.5);
    }
}

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
     * Gets the opposite facing enum for the inputted face
     * @param facing the face
     * @return the opposite face enum constant
     */
    public static EnumFacing getOpposite(EnumFacing facing) {
        return EnumFacing.faceList[facing.getOrder_b()];
    }

}

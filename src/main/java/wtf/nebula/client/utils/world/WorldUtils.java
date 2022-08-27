package wtf.nebula.client.utils.world;

import net.minecraft.block.Block;
import net.minecraft.util.Vec3;
import wtf.nebula.client.utils.client.Wrapper;

public class WorldUtils implements Wrapper {
    public static Block getBlock(Vec3 vec) {
        return mc.theWorld.getBlock((int) vec.xCoord, (int) vec.yCoord, (int) vec.zCoord);
    }

    public static boolean isReplaceable(Vec3 vec) {
        return getBlock(vec).getMaterial().isReplaceable();
    }
}

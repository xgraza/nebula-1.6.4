package wtf.nebula.util.world;

import net.minecraft.src.Block;
import net.minecraft.src.Vec3;
import wtf.nebula.util.Globals;

public class BlockUtil implements Globals {
    public static Block getBlockFromVec(Vec3 vec) {
        return Block.blocksList[mc.theWorld.getBlockId((int) vec.xCoord, (int) vec.yCoord, (int) vec.zCoord)];
    }
}

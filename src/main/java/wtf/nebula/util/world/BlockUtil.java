package wtf.nebula.util.world;

import com.google.common.collect.Lists;
import net.minecraft.src.Block;
import net.minecraft.src.Vec3;
import wtf.nebula.util.Globals;

import java.util.List;

public class BlockUtil implements Globals {
    public static final List<Block> REPLACEABLE = Lists.newArrayList(
            Block.waterStill,
            Block.waterMoving,
            Block.lavaMoving,
            Block.lavaStill
    );

    public static Block getBlockFromVec(Vec3 vec) {
        return Block.blocksList[mc.theWorld.getBlockId((int) vec.xCoord, (int) vec.yCoord, (int) vec.zCoord)];
    }

    public static boolean isReplaceable(Vec3 vec) {
        Block block = getBlockFromVec(vec);
        return block == null || REPLACEABLE.contains(block);
    }
}

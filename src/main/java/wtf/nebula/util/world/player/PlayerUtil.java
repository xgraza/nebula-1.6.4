package wtf.nebula.util.world.player;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.init.Blocks;
import net.minecraft.util.Vec3;
import wtf.nebula.util.Globals;
import wtf.nebula.util.world.BlockUtil;

public class PlayerUtil implements Globals {
    public static boolean isOnLiquid(boolean lava) {
        if (mc.thePlayer.isInWater()) {
            return false;
        }

        Vec3 pos = Vec3.createVectorHelper(
                Math.floor(mc.thePlayer.posX),
                mc.thePlayer.boundingBox.minY - 1.0,
                Math.floor(mc.thePlayer.posZ));

        Block block = BlockUtil.getBlockFromVec(pos);
        if (!lava && (block == Blocks.flowing_lava || block == Blocks.lava)) {
            return false;
        }

        return block instanceof BlockLiquid;
    }
}

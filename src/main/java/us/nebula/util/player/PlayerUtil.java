package us.nebula.util.player;

import net.minecraft.client.Minecraft;
import net.minecraft.src.BlockPos;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;

/**
 * @author xgraza
 * @since 03/24/25
 */
public final class PlayerUtil
{
    private static final Minecraft MC = Minecraft.getMinecraft();

    public static BlockPos getOrigin()
    {
        return new BlockPos(MathHelper.floor_double(MC.thePlayer.posX),
                MathHelper.floor_double(MC.thePlayer.boundingBox.minY),
                MathHelper.floor_double(MC.thePlayer.posZ));
    }

    public static boolean isPlayerCollided(final BlockPos pos)
    {
        return isPlayerCollided(pos.getX(), pos.getY(), pos.getZ());
    }

    public static boolean isPlayerCollided(final int x, final int y, final int z)
    {
        final AxisAlignedBB aabb = MC.thePlayer.boundingBox.copy().expand(0.0625, 0, 0.0625);
        final AxisAlignedBB aabb2 = new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1);
        return aabb.intersectsWith(aabb2);
    }
}

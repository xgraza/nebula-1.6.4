package us.nebula.api.interaction;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.src.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import us.nebula.Nebula;
import us.nebula.impl.cheat.world.PacketMineCheat;
import us.nebula.util.player.ChatUtil;
import us.nebula.util.world.BlockUtil;

/**
 * @author xgraza
 * @since 04/29/25
 */
public final class InteractionManager
{
    private static final Minecraft MC = Minecraft.getMinecraft();

    public static final InteractionManager INSTANCE = new InteractionManager();

    public boolean rightClickBlock(final MovingObjectPosition raycast)
    {
        return rightClickBlock(new BlockPos(raycast.blockX, raycast.blockY, raycast.blockZ),
                EnumFacing.values()[raycast.sideHit]);
    }

    public boolean rightClickBlock(final BlockPos pos,
                                   final EnumFacing facing,
                                   final boolean sneak)
    {
        MC.rightClickDelayTimer = 4;

        final boolean sneakPacket = sneak
                && BlockUtil.INTERACTABLE_BLOCK_LIST.contains(MC.theWorld.getBlock(pos))
                && (!MC.thePlayer.isSneaking() || !MC.gameSettings.keyBindSneak.pressed);
        if (sneakPacket)
        {
            MC.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(
                    MC.thePlayer, 1));
        }

        final boolean result = MC.playerController.onPlayerRightClick(MC.thePlayer,
                MC.theWorld,
                Nebula.INSTANCE.getInventoryManager().getStack(),
                pos.getX(), pos.getY(), pos.getZ(), facing.order_a,
                createHitVec(pos, facing));
        if (result)
        {
            MC.thePlayer.swingItem();
        }
        if (sneakPacket)
        {
            MC.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(
                    MC.thePlayer, 2));
        }
        return result;
    }

    public boolean rightClickBlock(final BlockPos pos, final EnumFacing facing)
    {
        return rightClickBlock(pos, facing, false);
    }

    public boolean breakBlock(final int x, final int y, final int z, final int face)
    {
        if (MC.theWorld.isAirBlock(x, y, z))
        {
            PlayerControllerMP.ALLOW_BREAK_OVERRIDE = false;
            return true;
        }

        MC.playerController.blockHitDelay = 0;

        if (!MC.playerController.sameToolAndBlock(x, y, z))
        {
            PlayerControllerMP.ALLOW_BREAK_OVERRIDE = true;
            MC.playerController.resetBlockRemoving();
            MC.playerController.clickBlock(x, y, z, face);
            MC.thePlayer.swingItem();
        }

        if (PacketMineCheat.INSTANCE.isToggled())
        {
            PlayerControllerMP.ALLOW_BREAK_OVERRIDE = false;
            return false;
        }

        PlayerControllerMP.ALLOW_BREAK_OVERRIDE = true;
        MC.playerController.onPlayerDamageBlock(x, y, z, face);
        if (MC.thePlayer.isCurrentToolAdventureModeExempt(x, y, z))
        {
            MC.effectRenderer.addBlockHitEffects(x, y, z, face);
            MC.thePlayer.swingItem();
        }
        boolean brokeBlock = MC.playerController.curBlockDamageMP >= 1.0f;
        if (brokeBlock)
        {
            PlayerControllerMP.ALLOW_BREAK_OVERRIDE = false;
        }
        return brokeBlock;
    }

    public boolean breakBlock(final BlockPos pos, final EnumFacing facing)
    {
        return breakBlock(pos.getX(), pos.getY(), pos.getZ(), facing.order_a);
    }

    private Vec3 createHitVec(final BlockPos pos, final EnumFacing facing)
    {
        final double faceX = facing.getFrontOffsetX() / 2.0;
        final double faceY = facing.getFrontOffsetY() / 2.0;
        final double faceZ = facing.getFrontOffsetZ() / 2.0;
        return Vec3.createVectorHelper(pos.getX() + faceX,
                pos.getY() + faceY,
                pos.getZ() + faceZ);
    }
}

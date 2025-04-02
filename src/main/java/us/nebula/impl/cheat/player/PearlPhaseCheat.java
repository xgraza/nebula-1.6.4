package us.nebula.impl.cheat.player;

import net.minecraft.item.ItemEnderPearl;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.src.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import us.nebula.Nebula;
import us.nebula.api.listener.EventListener;
import us.nebula.api.listener.Subscribe;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.api.manager.rotate.RotationConfirmation;
import us.nebula.impl.event.game.EventUpdate;
import us.nebula.impl.event.player.EventPushFromBlocks;
import us.nebula.util.player.InventoryUtil;
import us.nebula.util.player.PlayerUtil;
import us.nebula.util.world.BlockUtil;

/**
 * @author xgraza
 * @since 03/24/25
 */
@CheatManifest(name = "PearlPhase",
        description = "Phases into a block using pearls",
        category = CheatCategory.PLAYER)
public final class PearlPhaseCheat extends Cheat implements RotationConfirmation
{
    private static final int PEARL_PHASE_ROTATION_PRIORITY = 90;

    private float[] angles = null;
    private int slot = -1;

    @Override
    protected void onDisable()
    {
        super.onDisable();
        slot = -1;
        angles = null;
    }

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        if (!MC.thePlayer.isCollidedHorizontally
                || MC.thePlayer.phased
                || angles != null)
        {
            return;
        }
        slot = InventoryUtil.getHotbarItem(ItemEnderPearl.class);
        if (slot == -1)
        {
            toggle();
            return;
        }
        calcBlockTargetAngles();
        if (angles == null)
        {
            toggle();
            return;
        }
        Nebula.INSTANCE.getRotationManager().spoofAndConfirm(
                angles[0], angles[1], PEARL_PHASE_ROTATION_PRIORITY, this);
    };

    @Subscribe
    private final EventListener<EventPushFromBlocks> pushFromBlocksEventListener = event ->
    {
        event.setCanceled(true);
    };

    @Override
    public void onServerRotateConfirm(final float yaw, final float pitch)
    {
        if (slot != -1)
        {
            Nebula.INSTANCE.getInventoryManager().setSlot(slot);
            MC.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(
                    Nebula.INSTANCE.getInventoryManager().getStack()));
            MC.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation(MC.thePlayer, 1));
            Nebula.INSTANCE.getInventoryManager().syncSlot();
        }
        slot = -1;
        toggle();
    }

    private void calcBlockTargetAngles()
    {
        final BlockPos pos = PlayerUtil.getOrigin().add(0, 1, 0);
        for (final EnumFacing facing : BlockUtil.HORIZONTALS)
        {
            final BlockPos neighbor = BlockUtil.offset(pos, facing);
            if (PlayerUtil.isPlayerCollided(neighbor))
            {
                angles = new float[] {
                        MathHelper.wrapAngleTo180_float(
                                BlockUtil.getHorizontalFacing(facing) * 90.0f),
                        85.0f };
                break;
            }
        }
    }
}

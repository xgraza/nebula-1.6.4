package ez.nebula.client.impl.module.player;

import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.util.minecraft.network.PacketUtil;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.src.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import ez.nebula.client.core.Nebula;
import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.player.server.rotate.RotationConfirmation;
import ez.nebula.client.api.listener.event.game.EventUpdate;
import ez.nebula.client.api.listener.event.player.EventPushFromBlocks;
import ez.nebula.client.util.minecraft.player.InventoryUtil;
import ez.nebula.client.util.minecraft.player.PlayerUtil;
import ez.nebula.client.util.minecraft.world.BlockUtil;

/**
 * @author xgraza
 * @since 03/24/25
 */
@ModuleManifest(name = "PearlPhase",
        description = "Phases into a block using pearls",
        category = ModuleCategory.PLAYER)
public final class PearlPhaseModule extends Module implements RotationConfirmation
{
    private static final int PEARL_PHASE_ROTATION_PRIORITY = 90;

    private float[] angles = null;
    private int slot = -1;

    @Override
    public void onDisable()
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
            notifyError("You need an enderpearl in your hotbar to phase.", 5000L);
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
            event.setCanceled(true);

    @Override
    public void onServerRotateConfirm(final float yaw, final float pitch)
    {
        if (slot != -1)
        {
            Nebula.INSTANCE.getInventoryManager().setSlot(slot);
            PacketUtil.send(new C08PacketPlayerBlockPlacement(
                    Nebula.INSTANCE.getInventoryManager().getStack()));
            PacketUtil.send(new C0APacketAnimation(MC.thePlayer, 1));
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
                angles = new float[]{
                        MathHelper.wrapAngleTo180_float(
                                BlockUtil.getHorizontalFacing(facing) * 90.0f),
                        85.0f };
                break;
            }
        }
    }
}

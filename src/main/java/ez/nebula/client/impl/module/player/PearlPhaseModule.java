package ez.nebula.client.impl.module.player;

import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.util.minecraft.network.PacketUtil;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.network.play.client.C03PacketPlayer;
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
public final class PearlPhaseModule extends Module
{
    private static final int PEARL_PHASE_ROTATION_PRIORITY = 90;
    private static final float PITCH = 87.0f;

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        if (!MC.thePlayer.isCollidedHorizontally || MC.thePlayer.phased)
        {
            return;
        }
        final int slot = InventoryUtil.getHotbarItem(ItemEnderPearl.class);
        if (slot == InventoryUtil.INVALID_SLOT)
        {
            notifyError("You need an ender pearl in your hotbar to phase.", 5000L);
            toggle();
            return;
        }

        final float[] angles = calcBlockTargetAngles();
        if (angles == null || !Nebula.INSTANCE.getRotationManager().canTakePrecedent(PEARL_PHASE_ROTATION_PRIORITY))
        {
            return;
        }

        PacketUtil.send(new C03PacketPlayer.C06PacketPlayerPosLook(
                MC.thePlayer.posX,
                MC.thePlayer.boundingBox.minY,
                MC.thePlayer.posY,
                MC.thePlayer.posZ,
                angles[0],
                angles[1],
                MC.thePlayer.onGround));

        Nebula.INSTANCE.getInventoryManager().setSlot(slot);
        PacketUtil.send(new C08PacketPlayerBlockPlacement(Nebula.INSTANCE.getInventoryManager().getStack()));
        PacketUtil.send(new C0APacketAnimation(MC.thePlayer, 1));
        Nebula.INSTANCE.getInventoryManager().syncSlot();
        toggle();
    };

    @Subscribe
    private final EventListener<EventPushFromBlocks> pushFromBlocksEventListener = event ->
            event.setCanceled(true);

    private float[] calcBlockTargetAngles()
    {
        final BlockPos pos = PlayerUtil.getOrigin().up();
        for (final EnumFacing facing : BlockUtil.HORIZONTALS)
        {
            final BlockPos neighbor = BlockUtil.offset(pos, facing);
            if (PlayerUtil.isPlayerCollided(neighbor))
            {
                return new float[] { MathHelper.wrapAngleTo180_float(BlockUtil.getHorizontalFacing(facing) * 90.0f), PITCH };
            }
        }
        return null;
    }
}

package ez.nebula.client.impl.module.player;

import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.listener.event.game.EventUpdate;
import ez.nebula.client.api.listener.event.player.EventPushFromBlocks;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.manager.module.type.InteractionModule;
import ez.nebula.client.api.manager.module.type.RotationPriority;
import ez.nebula.client.util.minecraft.player.InventoryUtil;
import ez.nebula.client.util.minecraft.player.PlayerUtil;
import ez.nebula.client.util.minecraft.world.BlockUtil;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.src.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

/**
 * @author xgraza
 * @since 03/24/25
 */
@ModuleManifest(name = "PearlPhase",
        description = "Phases into a block using pearls",
        category = ModuleCategory.PLAYER)
@RotationPriority(100)
public final class PearlPhaseModule extends InteractionModule
{
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

        if (rotateAndWait(calcBlockTargetAngles()))
        {
            use(slot);
            toggle();
        }
    };

    @Subscribe
    private final EventListener<EventPushFromBlocks> pushFromBlocksEventListener = event ->
            event.setCanceled(true);

    private float[] calcBlockTargetAngles()
    {
        final BlockPos pos = PlayerUtil.getOrigin().up();
        for (final EnumFacing facing : BlockUtil.HORIZONTALS)
        {
            final BlockPos neighbor = pos.offset(facing);
            if (PlayerUtil.isPlayerCollided(neighbor))
            {
                return new float[] { MathHelper.wrapAngleTo180_float(BlockUtil.getHorizontalFacing(facing) * 90.0f), PITCH };
            }
        }
        return null;
    }
}

package us.nebula.client.cheat.impl.world;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEndPortal;
import net.minecraft.init.Items;
import net.minecraft.src.BlockPos;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import us.nebula.client.Nebula;
import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.interaction.InteractionManager;
import us.nebula.client.listener.EventListener;
import us.nebula.client.listener.Subscribe;
import us.nebula.client.listener.event.game.EventPostUpdate;
import us.nebula.client.listener.event.player.EventAttackBlock;
import us.nebula.client.listener.event.player.EventMoveUpdate;
import us.nebula.client.listener.event.world.EventModifySelectedBoundBox;
import us.nebula.client.util.math.AngleUtil;
import us.nebula.client.util.player.InventoryUtil;
import us.nebula.client.util.world.BlockUtil;

/**
 * @author xgraza
 * @since 05/24/26
 */
@CheatManifest(name = "PortalBreaker",
        description = "Attempts to breaks placed end portal blocks (not frames) when you try to break them",
        category = CheatCategory.WORLD)
public final class PortalBreakerCheat extends Cheat
{
    private static final int PORTAL_BREAKER_ROTATION_PRIORITY = 80;

    private MovingObjectPosition result;
    private int slot = -1;
    private BlockPos pos;
    private boolean tryAfterRotate;

    @Override
    public void onDisable()
    {
        super.onDisable();
        if (MC.thePlayer != null && slot != -1)
        {
            Nebula.INSTANCE.getInventoryManager().syncSlot();
        }
        slot = -1;
        pos = null;
        tryAfterRotate = false;
    }

    @Subscribe
    private final EventListener<EventMoveUpdate> moveUpdateEventListener = event ->
    {
        if (slot == -1 || pos == null || tryAfterRotate)
        {
            return;
        }

        result = null;
        float[] angles = null;
        for (final EnumFacing facing : EnumFacing.values())
        {
            if (facing == EnumFacing.UP || facing == EnumFacing.DOWN)
            {
                continue;
            }
            final BlockPos neighborPos = pos.offset(facing);
            if (!BlockUtil.isReplaceable(neighborPos))
            {
                angles = AngleUtil.anglesToBlock(neighborPos, BlockUtil.getOpposite(facing));
                result = AngleUtil.raytrace(5, angles[0], angles[1]);
                if (result == null
                        || (result.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK
                            || MC.theWorld.getBlock(result.blockX, result.blockY, result.blockZ) instanceof BlockEndPortal)
                            || result.sideHit < 2) // if sideHit is UP/DOWN
                {
                    result = null;
                }
                break;
            }
        }
        if (result == null || angles == null)
        {
            notifyError("Could not find stable supporting block to place water on", 7500L);
            pos = null;
            slot = -1;
            return;
        }

        if (tryAfterRotate)
        {
            return;
        }
        tryAfterRotate = Nebula.INSTANCE.getRotationManager().spoof(
                angles[0], angles[1], PORTAL_BREAKER_ROTATION_PRIORITY);
    };

    @Subscribe
    private final EventListener<EventPostUpdate> postUpdateEventListener = event ->
    {
        if (!tryAfterRotate)
        {
            return;
        }

        Nebula.INSTANCE.getInventoryManager().setSlot(slot);
        InteractionManager.INSTANCE.rightClickBlock(result); // click w/ water bucket
        InteractionManager.INSTANCE.rightClickBlock(result); // collect water
        Nebula.INSTANCE.getInventoryManager().syncSlot();

        slot = -1;
        pos = null;
        tryAfterRotate = false;
    };

    @Subscribe
    private final EventListener<EventModifySelectedBoundBox> modifySelectedBoundBoxEventListener = event ->
    {
        final Block block = event.getWorld().getBlock(event.getX(), event.getY(), event.getZ());
        if (!(block instanceof BlockEndPortal))
        {
            return;
        }
        event.setAabb(new AxisAlignedBB(new BlockPos(event.getX(), event.getY(), event.getZ())));
    };

    @Subscribe
    private final EventListener<EventAttackBlock> attackBlockEventListener = event ->
    {
        final BlockPos pos = new BlockPos(event.getX(), event.getY(), event.getZ());
        if (!(MC.theWorld.getBlock(pos) instanceof BlockEndPortal))
        {
            return;
        }

        slot = InventoryUtil.getSlot(0, 9,
                (stack) -> stack.getItem() == Items.water_bucket);
        if (slot == -1)
        {
            notifyInfo("You need a water bucket in your hotbar", 7500L);
            return;
        }
        this.pos = pos;
        event.cancel();
    };
}

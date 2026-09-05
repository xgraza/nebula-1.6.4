package ez.nebula.client.api.manager.module.type;

import ez.nebula.client.api.player.InteractionManager;
import ez.nebula.client.core.Nebula;
import ez.nebula.client.util.minecraft.network.PacketUtil;
import ez.nebula.client.util.minecraft.player.InventoryUtil;
import ez.nebula.client.util.minecraft.world.BlockInfo;
import ez.nebula.client.util.minecraft.world.BlockUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.src.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author xgraza
 * @since 9/4/26
 */
public abstract class InteractionModule extends RotationModule
{
    public InteractionModule()
    {
        this(-1);
    }

    public InteractionModule(final int rotationPriority)
    {
        super(rotationPriority);
    }

    protected void use()
    {
        final ItemStack stack = Nebula.INSTANCE.getInventoryManager().getStack();
        if (stack == null)
        {
            PacketUtil.send(new C08PacketPlayerBlockPlacement(null));
            MC.entityRenderer.itemRenderer.resetEquippedProgress();
            return;
        }
        if (MC.playerController.sendUseItem(MC.thePlayer, MC.theWorld, stack))
        {
            MC.entityRenderer.itemRenderer.resetEquippedProgress();
        }
    }

    protected void use(final int slot)
    {
        if (slot != InventoryUtil.INVALID_SLOT)
        {
            Nebula.INSTANCE.getInventoryManager().setSlot(slot);
        }
        use();
        if (slot != InventoryUtil.INVALID_SLOT)
        {
            Nebula.INSTANCE.getInventoryManager().syncSlot();
        }
    }

    protected void click(final BlockPos pos, final EnumFacing face)
    {
        swing();
        MC.playerController.clickBlock(pos.getX(), pos.getY(), pos.getZ(), face.order_a);
    }

    protected void swing()
    {
        InteractionManager.INSTANCE.swingItem();
    }

    protected boolean place(final BlockPos pos)
    {
        final BlockInfo info = BlockUtil.getPlacement(pos);
        return info != null && place(info);
    }

    protected boolean place(final BlockInfo info)
    {
        return place(info.getPos(), info.getFacing());
    }

    protected boolean place(final BlockInfo info, final int slot)
    {
        if (slot != InventoryUtil.INVALID_SLOT)
        {
            Nebula.INSTANCE.getInventoryManager().setSlot(slot);
        }
        final boolean result = place(info);
        if (slot != InventoryUtil.INVALID_SLOT)
        {
            Nebula.INSTANCE.getInventoryManager().syncSlot();
        }
        return result;
    }

    protected boolean place(final BlockPos pos, final EnumFacing face)
    {
        return place(pos, face, true);
    }

    protected boolean place(final BlockPos pos, final EnumFacing face, final int slot)
    {
        if (slot != InventoryUtil.INVALID_SLOT)
        {
            Nebula.INSTANCE.getInventoryManager().setSlot(slot);
        }
        final boolean result = place(pos, face, true);
        if (slot != InventoryUtil.INVALID_SLOT)
        {
            Nebula.INSTANCE.getInventoryManager().syncSlot();
        }
        return result;
    }

    protected boolean place(final MovingObjectPosition result)
    {
        return place(new BlockPos(result.blockX, result.blockY, result.blockZ), EnumFacing.faceList[result.sideHit], true);
    }

    /**
     * Attempts to place a block
     * @param pos
     * @param face
     * @param sneak
     * @return
     */
    protected boolean place(final BlockPos pos, final EnumFacing face, final boolean sneak)
    {
        return InteractionManager.INSTANCE.rightClickBlock(pos, face, sneak);
    }

    protected int placeMultiPos(final int maxBlocks, final int slot, final boolean countFailures, final Collection<BlockPos> positions)
    {
        if (positions.isEmpty())
        {
            return 0;
        }
        final List<BlockInfo> infoList = new ArrayList<>();
        for (final BlockPos pos : positions)
        {
            if (!BlockUtil.isReplaceable(pos))
            {
                continue;
            }
            final BlockInfo info = BlockUtil.getPlacement(pos);
            if (info != null)
            {
                infoList.add(info);
            }
        }
        return infoList.isEmpty() ? 0 : placeMultiInfo(maxBlocks, slot, countFailures, infoList);
    }

    /**
     * Places multiple blocks in a single tick without rotations
     * @param maxBlocks the maximum amount of successful block placements
     * @param slot the slot to switch to when placing (or -1)
     * @param countFailures if to count a failed block place attempt towards the place counter
     * @param placements a {@link Collection} of {@link BlockInfo} to place
     * @return the amount of block placements done
     */
    protected int placeMultiInfo(final int maxBlocks, final int slot, final boolean countFailures, final Collection<BlockInfo> placements)
    {
        if (placements.isEmpty())
        {
            return 0;
        }
        int placed = 0;
        for (final BlockInfo info : placements)
        {
            if (placed >= maxBlocks)
            {
                if (slot != InventoryUtil.INVALID_SLOT)
                {
                    Nebula.INSTANCE.getInventoryManager().syncSlot();
                }
                break;
            }
            if (info == null)
            {
                continue;
            }
            if (place(info, slot))
            {
                ++placed;
            } else
            {
                if (countFailures)
                {
                    ++placed;
                }
            }
        }
        return placed;
    }
}

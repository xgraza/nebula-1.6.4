package us.nebula.client.impl.cheat.world;

import net.minecraft.block.Block;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.src.BlockPos;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import us.nebula.client.Nebula;
import us.nebula.client.api.interaction.InteractionManager;
import us.nebula.client.api.listener.EventListener;
import us.nebula.client.api.listener.Subscribe;
import us.nebula.client.api.manager.cheat.Cheat;
import us.nebula.client.api.manager.cheat.CheatCategory;
import us.nebula.client.api.manager.cheat.CheatManifest;
import us.nebula.client.api.value.Setting;
import us.nebula.client.impl.event.game.EventUpdate;
import us.nebula.client.impl.event.render.EventRender3D;
import us.nebula.client.util.player.InventoryUtil;
import us.nebula.client.util.player.PlayerUtil;
import us.nebula.client.util.render.RenderUtil;
import us.nebula.client.util.world.BlockInfo;
import us.nebula.client.util.world.BlockUtil;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author xgraza
 * @since 04/29/25
 */
@CheatManifest(name = "AutoHighway",
        description = "Automatically builds a highway",
        category = CheatCategory.WORLD)
public final class AutoHighwayCheat extends Cheat
{
    private final Setting<Integer> blocksSetting = new Setting<>(
            "Blocks", 2, 1, 4, 1);
    private final Setting<Integer> blockPlacesSetting = new Setting<>(
            "Blocks/Tick", 5, 1, 10, 1);
    private final Setting<Boolean> supportingBlocksSetting = new Setting<>(
            "Support Blocks", false);
    private final Setting<Boolean> breakSetting = new Setting<>(
            "Break Blocks", true);

    private final Queue<BlockPos> positionQueue = new ConcurrentLinkedQueue<>();
    private final Queue<BlockInfo> breakPositionQueue = new ConcurrentLinkedQueue<>();
    private BlockPos currentBlockPos;
    private BlockInfo breakInfo;

    @Override
    protected void onDisable()
    {
        super.onDisable();
        positionQueue.clear();
        breakPositionQueue.clear();
        currentBlockPos = null;

        if (breakInfo != null && MC.playerController.isHittingBlock)
        {
            Nebula.INSTANCE.getInventoryManager().syncSlot();
            MC.playerController.resetBlockRemoving();
        }
        breakInfo = null;
        PlayerControllerMP.ALLOW_BREAK_OVERRIDE = false;
    }

    @Subscribe
    private final EventListener<EventRender3D> render3DEventListener = event ->
    {
        if (currentBlockPos != null)
        {
            RenderUtil.filledBox3D(new AxisAlignedBB(currentBlockPos), 0, 0x80FF0000);
        }
        if (breakInfo != null)
        {
            RenderUtil.filledBox3D(new AxisAlignedBB(breakInfo.getPos()), 0, 0x800000FF);
        }
    };

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        if (breakSetting.getValue() && !breakPositionQueue.isEmpty())
        {
            if (breakInfo == null)
            {
                breakInfo = breakPositionQueue.poll();
                return;
            }
            final int slot = InventoryUtil.getBestToolSlotFor(MC.theWorld.getBlock(breakInfo.getPos()));
            if (slot != -1)
            {
                Nebula.INSTANCE.getInventoryManager().setSlot(slot);
            }
            if (InteractionManager.INSTANCE.breakBlock(breakInfo.getPos(), breakInfo.getFacing()))
            {
                Nebula.INSTANCE.getInventoryManager().syncSlot();
                breakInfo = null;
            }
            return;
        }

        if (positionQueue.size() <= 5)
        {
            queuePositions();
        }

        for (int i = 0; i < blockPlacesSetting.getValue(); ++i)
        {
            // find first block slot
            final int slot = getBlockSlot();
            if (slot == -1)
            {
                break;
            }

            final BlockPos pos = positionQueue.poll();
            // early return if null, meeans queue is empty
            if (pos == null)
            {
                break;
            }
            if (!BlockUtil.isReplaceable(pos))
            {
                final Block blockType = ((ItemBlock) MC.thePlayer.inventory.getStackInSlot(slot).getItem()).getBlock();
                final Block block = MC.theWorld.getBlock(pos);
                if (breakSetting.getValue() && !block.equals(blockType))
                {
                    addBlocksToBreak(pos);
                    break;
                }
                continue;
            }
            placeBlock(pos, slot);
        }
    };

    private void addBlocksToBreak(final BlockPos origin)
    {
        final Set<BlockInfo> breakInfoSet = new LinkedHashSet<>();
        final EnumFacing opposite = BlockUtil.getOpposite(PlayerUtil.getFacing());
        if (isValidBlock(origin))
        {
            breakInfoSet.add(new BlockInfo(origin, opposite));
        }

        // check at least 2 blocks above
        for (int y = origin.getY(); y <= MathHelper.floor_double(MC.thePlayer.boundingBox.minY) + 2; y++)
        {
            final BlockPos pos = new BlockPos(origin.getX(), y, origin.getZ());
            if (isValidBlock(pos))
            {
                breakInfoSet.add(new BlockInfo(pos, opposite));
            }
        }

        for (final BlockInfo blockInfo : breakInfoSet)
        {
            if (!breakPositionQueue.contains(blockInfo))
            {
                breakPositionQueue.add(blockInfo);
            }
        }
    }

    private boolean isValidBlock(final BlockPos pos)
    {
        final Block block = MC.theWorld.getBlock(pos);
        return block != null && !block.getMaterial().isReplaceable() && block.blockHardness != -1;
    }

    private void placeBlock(final BlockPos pos, final int slot)
    {
        final BlockInfo info = getBlockInfo(pos);
        if (info == null)
        {
            return;
        }
        Nebula.INSTANCE.getInventoryManager().setSlot(slot);
        InteractionManager.INSTANCE.rightClickBlock(
                info.getPos(), info.getFacing(), true);
        Nebula.INSTANCE.getInventoryManager().syncSlot();
    }

    private BlockInfo getBlockInfo(final BlockPos pos)
    {
        for (final EnumFacing facing : EnumFacing.values())
        {
            final BlockPos neighbor = BlockUtil.offset(pos, facing);
            if (!BlockUtil.isReplaceable(neighbor))
            {
                return new BlockInfo(neighbor, BlockUtil.getOpposite(facing));
            }
        }

        for (final EnumFacing facing : EnumFacing.values())
        {
            final BlockPos neighbor = BlockUtil.offset(pos, facing);
            if (BlockUtil.isReplaceable(neighbor))
            {
                for (final EnumFacing side : EnumFacing.values())
                {
                    final BlockPos n = BlockUtil.offset(neighbor, side);
                    if (!BlockUtil.isReplaceable(n))
                    {
                        return new BlockInfo(n, BlockUtil.getOpposite(side));
                    }
                }
            }
        }
        return null;
    }

    private int getBlockSlot()
    {
        final ItemStack itemStack = MC.thePlayer.getHeldItem();
        if (itemStack != null && itemStack.getItem() instanceof ItemBlock)
        {
            return MC.thePlayer.inventory.currentItem;
        }
        return InventoryUtil.getHotbarSlot(
                (stack) -> stack.getItem() instanceof ItemBlock);
    }

    private void queuePositions()
    {
        final BlockPos origin = PlayerUtil.getOrigin().down();
        final EnumFacing facing = PlayerUtil.getFacing();
        final Set<BlockPos> positionSet = new LinkedHashSet<>();
        final BlockPos[] adjacent = BlockUtil.getAdjacent(facing);
        if (adjacent == null)
        {
            return;
        }

        final BlockPos vec = BlockUtil.getFacingVec(facing);

        for (int offset = 0; offset < blocksSetting.getValue(); ++offset)
        {
            final BlockPos pos = origin.add(vec.getX() * (offset + 1),
                    0, vec.getZ() * (offset + 1));
            positionSet.add(pos);
            positionSet.add(pos.add(adjacent[0].getX(), 0, adjacent[0].getZ()));
            positionSet.add(pos.add(adjacent[1].getX(), 0, adjacent[1].getZ()));

            if (supportingBlocksSetting.getValue())
            {
                positionSet.add(pos.add(adjacent[0].getX() * 2, 0, adjacent[0].getZ() * 2));
                positionSet.add(pos.add(adjacent[1].getX() * 2, 0, adjacent[1].getZ() * 2));
            }
            positionSet.add(pos.add(adjacent[0].getX() * 2, 1, adjacent[0].getZ() * 2));
            positionSet.add(pos.add(adjacent[1].getX() * 2, 1, adjacent[1].getZ() * 2));
        }

        if (positionSet.isEmpty())
        {
            return;
        }
        for (final BlockPos pos : positionSet)
        {
            if (!positionQueue.contains(pos))
            {
                positionQueue.add(pos);
            }
        }
    }
}

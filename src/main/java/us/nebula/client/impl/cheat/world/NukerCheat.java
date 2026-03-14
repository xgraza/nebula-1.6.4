package us.nebula.client.impl.cheat.world;

import net.minecraft.block.Block;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.src.BlockPos;
import net.minecraft.util.EnumFacing;
import us.nebula.client.api.interaction.InteractionManager;
import us.nebula.client.api.listener.EventListener;
import us.nebula.client.api.listener.Subscribe;
import us.nebula.client.api.manager.cheat.Cheat;
import us.nebula.client.api.manager.cheat.CheatCategory;
import us.nebula.client.api.manager.cheat.CheatManifest;
import us.nebula.client.api.value.Setting;
import us.nebula.client.impl.event.game.EventUpdate;
import us.nebula.client.util.player.InventoryUtil;
import us.nebula.client.util.player.PlayerUtil;
import us.nebula.client.util.world.BlockUtil;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/**
 * @author xgraza
 * @since 06/24/25
 */
@CheatManifest(name = "Nuker",
        description = "Breaks blocks in the surrounding area",
        category = CheatCategory.WORLD)
public final class NukerCheat extends Cheat
{
    private final Setting<Double> rangeSetting = new Setting<>(
            "Range", 4.5, 1.0, 6.0, 0.5);
    private final Setting<Integer> yRangeSetting = new Setting<>(
            "Y-Range", 3, 1, 6, 1);
    private final Setting<Boolean> swapToToolSetting = new Setting<>(
            "Swap To Tool", true);
    private final Setting<Boolean> instantSetting = new Setting<>(
            "Instant", false);

    private final Queue<BlockPos> breakPosQueue = new ConcurrentLinkedQueue<>();
    private BlockPos currentBlock;
    private int oldSlot = -1;

    @Override
    protected void onDisable()
    {
        super.onDisable();
        PlayerControllerMP.ALLOW_BREAK_OVERRIDE = false;
    }

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = evnet ->
    {
        if (breakPosQueue.isEmpty())
        {
            searchBlocks();
            return;
        }

        breakPosQueue.removeIf(
                (pos) -> MC.thePlayer.getDistance(
                        pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5) > rangeSetting.getValue());

        if (currentBlock == null || BlockUtil.isReplaceable(currentBlock))
        {
            currentBlock = breakPosQueue.poll();
            if (currentBlock == null)
            {
                return;
            }
        }

        final Block block = MC.theWorld.getBlock(currentBlock);
        if (block.getMaterial().isReplaceable() || block.blockHardness == -1.0f)
        {
            currentBlock = null;
            return;
        }

        if (swapToToolSetting.getValue())
        {
            final int slot = InventoryUtil.getBestToolSlotFor(block);
            if (slot != -1)
            {
                if (oldSlot == -1)
                {
                    oldSlot = MC.thePlayer.inventory.currentItem;
                }
                MC.thePlayer.inventory.currentItem = slot;
            }
        }

        final EnumFacing face = BlockUtil.getOpposite(PlayerUtil.getFacing());
        if (InteractionManager.INSTANCE.breakBlock(currentBlock, face))
        {
            if (instantSetting.getValue())
            {
                MC.theWorld.setBlockToAir(currentBlock.getX(), currentBlock.getY(), currentBlock.getZ());
            }
            if (oldSlot != -1)
            {
                MC.thePlayer.inventory.currentItem = oldSlot;
                oldSlot = -1;
            }
            currentBlock = null;
        }
    };

    private void searchBlocks()
    {
        breakPosQueue.clear();
        Set<BlockPos> breakPosSet = new LinkedHashSet<>();

        final BlockPos origin = PlayerUtil.getOrigin();
        final int r = rangeSetting.getValue().intValue();
        for (int y = 0; y <= yRangeSetting.getValue(); ++y)
        {
            for (int x = -r; x <= r; ++x)
            {
                for (int z = -r; z <= r; ++z)
                {
                    final BlockPos pos = origin.add(x, y, z);
                    if (MC.thePlayer.getDistance(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5)
                            > rangeSetting.getValue())
                    {
                        continue;
                    }
                    final Block block = MC.theWorld.getBlock(pos);
                    if (block.blockHardness == -1.0f || block.getMaterial().isReplaceable())
                    {
                        continue;
                    }
                    breakPosSet.add(pos);
                }
            }
        }

        breakPosSet = breakPosSet.stream().sorted(Comparator.comparingDouble(
                        (pos) -> MC.thePlayer.getDistance(pos.getX(), pos.getY(), pos.getZ())))
                .collect(Collectors.toCollection(LinkedHashSet::new));
        breakPosQueue.addAll(breakPosSet);
    }
}

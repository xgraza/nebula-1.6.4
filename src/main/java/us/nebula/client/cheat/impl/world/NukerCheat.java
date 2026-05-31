package us.nebula.client.cheat.impl.world;

import net.minecraft.block.Block;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.src.BlockPos;
import net.minecraft.util.EnumFacing;
import us.nebula.client.interaction.InteractionManager;
import us.nebula.client.listener.EventListener;
import us.nebula.client.listener.Subscribe;
import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.listener.event.game.EventUpdate;
import us.nebula.client.setting.Setting;
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
        description = "Automatically breaks blocks around you to clear an area",
        category = CheatCategory.WORLD)
public final class NukerCheat extends Cheat
{
    private final Setting<Float> rangeSetting = numberBuilder("Range", 4.2f)
            .setMin(1.0f)
            .setMax(6.0f)
            .setScale(0.1f)
            .setDescription("The range to nuke blocks in")
            .build();
    private final Setting<Integer> yRangeSetting = numberBuilder("Y-Range", 3)
            .setMin(1)
            .setMax(6)
            .setScale(1)
            .setDescription("The y range to look for nuker blocks")
            .build();
    private final Setting<Boolean> swapToToolSetting = builder("Swap To Tool", true)
            .setDescription("If to automatically swap to the best tool")
            .build();
    private final Setting<Boolean> instantSetting = builder("Instant", false)
            .setDescription("If to instantly set a block to air if it can break quickly")
            .build();

    private final Queue<BlockPos> breakPosQueue = new ConcurrentLinkedQueue<>();
    private BlockPos currentBlock;
    private int oldSlot = -1;

    @Override
    public void onDisable()
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

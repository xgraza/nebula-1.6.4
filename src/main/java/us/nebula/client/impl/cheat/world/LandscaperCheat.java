package us.nebula.client.impl.cheat.world;

import net.minecraft.block.*;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.src.BlockPos;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
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

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/**
 * @author xgraza
 * @since 05/03/25
 */
@CheatManifest(name = "Landscaper",
        description = "Breaks all foliage blocks at your Y level",
        category = CheatCategory.WORLD)
public final class LandscaperCheat extends Cheat
{
    private final Setting<Double> rangeSetting = new Setting<>(
            "Range", 4.2, 1.0, 6.0, 0.5);
    private final Setting<Boolean> snowSetting = new Setting<>(
            "Shovel Snow", true);

    private final Queue<BlockPos> breakQueue = new ConcurrentLinkedQueue<>();
    private BlockPos breakingBlockPos;

    @Override
    public void onDisable()
    {
        super.onDisable();
        breakQueue.clear();
        breakingBlockPos = null;
        if (MC.thePlayer != null)
        {
            Nebula.INSTANCE.getInventoryManager().syncSlot();
        }
        PlayerControllerMP.ALLOW_BREAK_OVERRIDE = false;
    }

    @Subscribe
    private final EventListener<EventRender3D> render3DEventListener = event ->
    {
        if (breakingBlockPos == null)
        {
            return;
        }
        RenderUtil.filledBox3D(new AxisAlignedBB(breakingBlockPos), 0, 0x8000FF00);
    };

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        populateBreakQueue();
        if (breakQueue.isEmpty())
        {
            PlayerControllerMP.ALLOW_BREAK_OVERRIDE = false;
            return;
        }
        if (breakingBlockPos == null)
        {
            PlayerControllerMP.ALLOW_BREAK_OVERRIDE = false;
            Nebula.INSTANCE.getInventoryManager().syncSlot();
            breakingBlockPos = breakQueue.poll();
            return;
        }
        final Block block = MC.theWorld.getBlock(breakingBlockPos);
        if (block instanceof BlockSnow || block instanceof BlockSnowBlock)
        {
            final int slot = InventoryUtil.getBestToolSlotFor(block);
            if (slot != -1)
            {
                Nebula.INSTANCE.getInventoryManager().setSlot(slot);
            }
        }
        if (InteractionManager.INSTANCE.breakBlock(breakingBlockPos, EnumFacing.UP))
        {
            breakingBlockPos = null;
            Nebula.INSTANCE.getInventoryManager().syncSlot();
        }
    };

    private boolean isBlockValid(final BlockPos blockPos)
    {
        final Block block = MC.theWorld.getBlock(blockPos);
        if (!snowSetting.getValue() && (block instanceof BlockSnow || block instanceof BlockSnowBlock))
        {
            return false;
        }
        return block instanceof BlockFlower
                || block instanceof BlockDoublePlant
                || block instanceof BlockGrass
                || block instanceof BlockTallGrass
                || block instanceof BlockMushroom
                || block instanceof BlockDeadBush
                || block instanceof BlockSnow
                || block instanceof BlockSnowBlock;
    }

    private void populateBreakQueue()
    {
        breakQueue.clear();
        Set<BlockPos> breakPositionSet = new HashSet<>();
        final BlockPos origin = PlayerUtil.getOrigin();
        final int range = rangeSetting.getValue().intValue();
        for (int x = -range; x <= range; ++x)
        {
            for (int z = -range; z <= range; ++z)
            {
                final BlockPos pos = origin.add(x, 0, z);
                if (isBlockValid(pos) && !breakQueue.contains(pos))
                {
                    breakPositionSet.add(pos);
                }
            }
        }
        breakPositionSet = breakPositionSet.stream().sorted(Comparator.comparingDouble((x) ->
                        MC.thePlayer.getDistance(x.getX(), x.getY(), x.getZ())))
                .collect(Collectors.toCollection(LinkedHashSet::new));
        breakQueue.addAll(breakPositionSet);
    }
}

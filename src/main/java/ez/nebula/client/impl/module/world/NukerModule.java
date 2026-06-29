package ez.nebula.client.impl.module.world;

import ez.nebula.client.api.listener.event.game.EventPostUpdate;
import ez.nebula.client.api.listener.event.render.EventRender3D;
import ez.nebula.client.api.setting.NumberSetting;
import ez.nebula.client.core.Nebula;
import ez.nebula.client.util.math.AngleUtil;
import ez.nebula.client.util.minecraft.world.BlockInfo;
import ez.nebula.client.util.render.RenderUtil;
import net.minecraft.block.Block;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.src.BlockPos;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import ez.nebula.client.api.player.InteractionManager;
import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.listener.event.game.EventUpdate;
import ez.nebula.client.api.setting.Setting;
import ez.nebula.client.util.minecraft.player.InventoryUtil;
import ez.nebula.client.util.minecraft.player.PlayerUtil;
import ez.nebula.client.util.minecraft.world.BlockUtil;
import net.minecraft.util.Vec3;

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
@ModuleManifest(name = "Nuker",
        description = "Automatically breaks blocks around you to clear an area",
        category = ModuleCategory.WORLD)
public final class NukerModule extends Module
{
    private static final int NUKER_ROTATION_PRIORITY = 80;

    private final NumberSetting<Float> rangeSetting = numberBuilder("Range", 4.2f)
            .setMin(1.0f)
            .setMax(6.0f)
            .setScale(0.1f)
            .setDescription("The range to nuke blocks in")
            .build();
    private final NumberSetting<Integer> yRangeSetting = numberBuilder("Y-Range", 3)
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
    private final Setting<Boolean> rotateSetting = builder("Rotate", false)
            .setDescription("If to rotate to the block you're breaking")
            .build();
    private final Setting<Boolean> renderSetting = builder("Render", true)
            .setDescription("If to render what block is being broken")
            .build();

    private final Queue<BlockPos> breakPosQueue = new ConcurrentLinkedQueue<>();
    private BlockInfo currentBlock;
    private int oldSlot = -1;
    private float[] angles;

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

        BlockPos pos = currentBlock == null ? null : currentBlock.getPos();
        if (pos == null || BlockUtil.isReplaceable(pos))
        {
            pos = breakPosQueue.poll();
            if (pos == null)
            {
                return;
            }
        }

        final Block block = MC.theWorld.getBlock(pos);
        if (block.getMaterial().isReplaceable() || block.blockHardness == -1.0f)
        {
            currentBlock = null;
            return;
        }

        final EnumFacing face = AngleUtil.getVisibleFace(pos, rangeSetting.getValue().doubleValue() + 0.5);
        if (face == null)
        {
            return;
        }
        currentBlock = new BlockInfo(pos, face);

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

        if (rotateSetting.getValue())
        {
            if (angles == null)
            {
                return;
            }
            if (!Nebula.INSTANCE.getRotationManager().spoof(angles[0], angles[1], NUKER_ROTATION_PRIORITY))
            {
                angles = null;
            }
        }
    };

    @Subscribe
    private final EventListener<EventPostUpdate> postUpdateEventListener = event ->
    {
        if ((rotateSetting.getValue() && angles == null) || currentBlock == null)
        {
            return;
        }
        if (InteractionManager.INSTANCE.breakBlock(currentBlock.getPos(), currentBlock.getFacing()))
        {
            if (instantSetting.getValue())
            {
                final BlockPos pos = currentBlock.getPos();
                MC.theWorld.setBlockToAir(pos.getX(), pos.getY(), pos.getZ());
            }
            if (oldSlot != -1)
            {
                MC.thePlayer.inventory.currentItem = oldSlot;
                oldSlot = -1;
            }
            currentBlock = null;
        }
    };

    @Subscribe
    private final EventListener<EventRender3D> render3DEventListener = event ->
    {
        if (currentBlock == null)
        {
            return;
        }
        angles = AngleUtil.anglesToBlock(currentBlock.getPos(), currentBlock.getFacing(), event.getPartialTicks());
        if (!renderSetting.getValue())
        {
            return;
        }

        final AxisAlignedBB aabb = new AxisAlignedBB(Vec3.createVectorHelper(
                currentBlock.getPos().getX(), currentBlock.getPos().getY(), currentBlock.getPos().getZ()), 1);

        RenderUtil.renderFilledAABB(aabb, RenderUtil.calculateFaceMask(currentBlock.getFacing()), 0x80FF0000);
        RenderUtil.renderOutlinedAABB(aabb, 1.5f, RenderUtil.calculateFaceMask(currentBlock.getFacing()), 0xFFFF0000);
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

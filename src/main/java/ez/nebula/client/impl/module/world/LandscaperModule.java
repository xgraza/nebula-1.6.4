package ez.nebula.client.impl.module.world;

import ez.nebula.client.Nebula;
import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.setting.NumberSetting;
import ez.nebula.client.util.math.AngleUtil;
import ez.nebula.client.util.minecraft.world.BlockUtil;
import ez.nebula.client.util.render.world.Render3D;
import net.minecraft.block.*;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.src.BlockPos;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import ez.nebula.client.api.player.InteractionManager;
import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.setting.Setting;
import ez.nebula.client.util.render.world.QuadMask;
import ez.nebula.client.api.listener.event.game.EventUpdate;
import ez.nebula.client.api.listener.event.render.EventRender3D;
import ez.nebula.client.util.minecraft.player.InventoryUtil;
import ez.nebula.client.util.minecraft.player.PlayerUtil;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/**
 * @author xgraza
 * @since 05/03/25
 */
@ModuleManifest(name = "Landscaper",
        description = "Automatically breaks all foliage (i.e. grass, flowers, snow) blocks at your Y level",
        category = ModuleCategory.WORLD)
public final class LandscaperModule extends Module
{
    private static final int LANDSCAPER_ROTATION_PRIORITY = 10;

    private final NumberSetting<Float> rangeSetting = numberBuilder("Range", 4.2f)
            .setMin(1.0f)
            .setMax(6.0f)
            .setScale(0.1f)
            .setDescription("The range to break foliage blocks in")
            .build();
    private final Setting<Boolean> rotateSetting = builder("Rotate", false)
            .setDescription("If to rotate to the block to break")
            .build();
    private final Setting<Boolean> snowSetting = builder("Shovel Snow", true)
            .setDescription("If to clear snow")
            .build();
    private final Setting<Boolean> saplingsSetting = builder("Saplings", false)
            .setDescription("If to destroy saplings")
            .build();

    private final Queue<BlockPos> breakQueue = new ConcurrentLinkedQueue<>();
    private BlockPos breakingBlockPos;
    private float[] angles;

    @Override
    public void onDisable()
    {
        super.onDisable();
        breakQueue.clear();
        breakingBlockPos = null;
        if (MC.thePlayer != null)
        {
            Nebula.INVENTORY.syncSlot();
        }
        PlayerControllerMP.ALLOW_BREAK_OVERRIDE = false;
        angles = null;
    }

    @Subscribe
    private final EventListener<EventRender3D> render3DEventListener = event ->
    {
        if (breakingBlockPos == null)
        {
            return;
        }
        if (rotateSetting.getValue())
        {
            angles = AngleUtil.anglesToBlock(breakingBlockPos, EnumFacing.UP, event.getPartialTicks());
        }
        Render3D.filledAABB(new AxisAlignedBB(breakingBlockPos), QuadMask.ALL_FACES, 0x8000FF00);
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
            Nebula.INVENTORY.syncSlot();
            breakingBlockPos = breakQueue.poll();
            return;
        }
        if (rotateSetting.getValue())
        {
            if (angles == null)
            {
                return;
            }
            if (!Nebula.ROTATIONS.spoof(angles[0], angles[1], LANDSCAPER_ROTATION_PRIORITY))
            {
                return;
            }
        }
        final Block block = MC.theWorld.getBlock(breakingBlockPos);
        if (block instanceof BlockSnow || block instanceof BlockSnowBlock)
        {
            final int slot = InventoryUtil.getBestToolSlotFor(block);
            if (slot != InventoryUtil.INVALID_SLOT)
            {
                Nebula.INVENTORY.setSlot(slot);
            }
        }
        if (InteractionManager.INSTANCE.breakBlock(breakingBlockPos, EnumFacing.UP))
        {
            breakingBlockPos = null;
            Nebula.INVENTORY.syncSlot();
        }
    };

    private boolean isBlockValid(final BlockPos blockPos)
    {
        final Block block = MC.theWorld.getBlock(blockPos);
        if (!snowSetting.getValue() && (block instanceof BlockSnow || block instanceof BlockSnowBlock))
        {
            return false;
        }
        if (!saplingsSetting.getValue() && block instanceof BlockSapling)
        {
            return false;
        }
        return block instanceof BlockFlower
                || block instanceof BlockDoublePlant
                || block instanceof BlockTallGrass
                || block instanceof BlockMushroom
                || block instanceof BlockDeadBush
                || block instanceof BlockSnow
                || block instanceof BlockSnowBlock
                || block instanceof BlockSapling;
    }

    private void populateBreakQueue()
    {
        breakQueue.clear();
        Set<BlockPos> breakPositionSet = new HashSet<>();
        final BlockPos origin = PlayerUtil.getOrigin();
        for (final BlockPos offset : BlockUtil.RADIAL_BLOCK_MAP.get(rangeSetting.getValue().intValue()))
        {
            final BlockPos pos = origin.add(offset);
            if (isBlockValid(pos) && !breakQueue.contains(pos))
            {
                breakPositionSet.add(pos);
            }
        }
        breakPositionSet = breakPositionSet.stream().sorted(Comparator.comparingDouble((x) ->
                        MC.thePlayer.getDistance(x.getX(), x.getY(), x.getZ())))
                .collect(Collectors.toCollection(LinkedHashSet::new));
        breakQueue.addAll(breakPositionSet);
    }
}

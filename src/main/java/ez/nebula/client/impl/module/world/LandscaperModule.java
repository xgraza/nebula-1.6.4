package ez.nebula.client.impl.module.world;

import ez.nebula.client.api.manager.module.Module;
import net.minecraft.block.*;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.src.BlockPos;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import ez.nebula.client.core.Nebula;
import ez.nebula.client.api.player.InteractionManager;
import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.setting.Setting;
import ez.nebula.client.util.render.QuadMask;
import ez.nebula.client.api.listener.event.game.EventUpdate;
import ez.nebula.client.api.listener.event.render.EventRender3D;
import ez.nebula.client.util.minecraft.player.InventoryUtil;
import ez.nebula.client.util.minecraft.player.PlayerUtil;
import ez.nebula.client.util.render.RenderUtil;

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
    private final Setting<Float> rangeSetting = numberBuilder("Range", 4.2f)
            .setMin(1.0f)
            .setMax(6.0f)
            .setScale(0.1f)
            .setDescription("The range to break foliage blocks in")
            .build();
    private final Setting<Boolean> snowSetting = builder("Shovel Snow", true)
            .setDescription("If to clear snow")
            .build();

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
        RenderUtil.renderFilledAABB(new AxisAlignedBB(breakingBlockPos), QuadMask.ALL_FACES, 0x8000FF00);
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

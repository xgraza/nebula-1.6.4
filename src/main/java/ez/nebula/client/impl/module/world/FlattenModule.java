package ez.nebula.client.impl.module.world;

import ez.nebula.client.api.DebugFeature;
import ez.nebula.client.api.listener.event.game.EventPostUpdate;
import ez.nebula.client.api.setting.NumberSetting;
import ez.nebula.client.api.setting.block.BlockSetting;
import ez.nebula.client.api.setting.block.BlockValue;
import ez.nebula.client.core.Nebula;
import ez.nebula.client.util.math.AngleUtil;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.src.BlockPos;
import ez.nebula.client.api.player.InteractionManager;
import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.listener.event.game.EventUpdate;
import ez.nebula.client.api.setting.Setting;
import ez.nebula.client.util.minecraft.player.PlayerUtil;
import ez.nebula.client.util.minecraft.world.BlockInfo;
import ez.nebula.client.util.minecraft.world.BlockUtil;

import java.util.*;

/**
 * @author xgraza
 * @since 06/24/25
 */
@ModuleManifest(name = "Flatten",
        description = "Places the currently held block in a radial pattern to flatten the area around you",
        category = ModuleCategory.WORLD)
public final class FlattenModule extends Module
{
    private static final int FLATTEN_ROTATION_PRIORITY = 60;

    private final NumberSetting<Double> rangeSetting = numberBuilder("Range", 4.5)
            .setMin(1.5)
            .setMax(6.0)
            .setScale(0.5)
            .setDescription("The range to place blocks at")
            .build();
    @DebugFeature
    private final BlockSetting blockSetting = blockBuilder("Block")
            .setBlock(Blocks.obsidian)
            .setDescription("The type of block to use with flatten")
            .build();
    private final Setting<Boolean> radialSetting = builder("Radial", true)
            .setDescription("If to place the blocks in a radial pattern")
            .build();
    private final Setting<Boolean> stopOnSneakSetting = builder("Stop on Sneak", false)
            .setDescription("If to stop placing blocks when sneaking")
            .build();
//    private final Setting<Boolean> roateSetting = builder("Rotate", false)
//            .setDescription("If to rotate towards the block you're placing")
//            .build();
    private final Setting<Integer> blocksSetting = numberBuilder("Blocks", 4)
            .setMin(1)
            .setMax(20)
            .setScale(1)
            .setDescription("How many blocks to place per tick")
            //.setVisibility((value) -> !roateSetting.getValue())
            .build();
    private final Setting<Integer> yOffsetSetting = numberBuilder("Y-Offset", 0)
            .setMin(0)
            .setMax(2)
            .setScale(1)
            .setDescription("The y-offset to place blocks at")
            .build();

    private BlockInfo placeInfo;
    private float[] angles;

    @Override
    public void onDisable()
    {
        super.onDisable();
        if (MC.thePlayer != null)
        {
           // Nebula.INSTANCE.getInventoryManager().syncSlot();
        }
        angles = null;
        placeInfo = null;
    }

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
//        final int slot = InventoryUtil.getSlot(0, 9, (stack) -> ((BlockSetting)blockSetting).isBlock(stack));
//        if (slot == -1)
//        {
//            return;
//        }
//
//        Nebula.INSTANCE.getInventoryManager().setSlot(slot);

        final ItemStack heldStack = MC.thePlayer.getHeldItem();
        if (heldStack == null || !(heldStack.getItem() instanceof ItemBlock))
        {
            return;
        }

        if (stopOnSneakSetting.getValue() && MC.thePlayer.isSneaking())
        {
            return;
        }

        final List<BlockInfo> placementInfoList = getPlacements();
        if (placementInfoList.isEmpty())
        {
            return;
        }
        for (int i = 0; i < blocksSetting.getValue(); ++i)
        {
            if (i > placementInfoList.size() - 1)
            {
                break;
            }
            final BlockInfo info = placementInfoList.get(i);
            InteractionManager.INSTANCE.rightClickBlock(info.getPos(), info.getFacing(), true);
        }
    };

    private List<BlockInfo> getPlacements()
    {
        final Set<BlockPos> positions = new HashSet<>();
        final BlockPos origin = PlayerUtil.getOrigin();

        for (final BlockPos offset : BlockUtil.RADIAL_BLOCK_MAP.get(rangeSetting.getValue().intValue()))
        {
            if (offset.getY() != 0)
            {
                continue;
            }
            final BlockPos pos = origin.add(offset.getX(), -(1 + yOffsetSetting.getValue()), offset.getZ());
            if (radialSetting.getValue() && MC.thePlayer.getDistance(
                    pos.getX() + 0.5,
                    pos.getY() + 1.5,
                    pos.getZ() + 0.5) > rangeSetting.getValue())
            {
                continue;
            }

            if (BlockUtil.isReplaceable(pos))
            {
                positions.add(pos);
            }
        }

        final List<BlockInfo> infoList = new LinkedList<>();
        for (final BlockPos pos : positions)
        {
            final BlockInfo info = BlockUtil.getPlacement(pos);
            if (info != null)
            {
                infoList.add(info);
            }
        }

        infoList.sort(Comparator.comparingDouble((info) ->
        {
            final BlockPos p = info.getPos();
            return -MC.thePlayer.getDistance(p.getX(), p.getY(), p.getZ());
        }));
        return infoList;
    }
}

package ez.nebula.client.impl.module.world;

import ez.nebula.client.api.setting.block.BlockSetting;
import ez.nebula.client.api.setting.block.BlockValue;
import ez.nebula.client.core.Nebula;
import ez.nebula.client.util.minecraft.player.InventoryUtil;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.src.BlockPos;
import net.minecraft.util.EnumFacing;
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
    private final Setting<Integer> rangeSetting = numberBuilder("Range", 4)
            .setMin(1)
            .setMax(6)
            .setScale(1)
            .setDescription("The range to place blocks at")
            .build();
    private final Setting<BlockValue> blockSetting = blockBuilder("Block")
            .setBlock(Blocks.obsidian)
            .setDescription("The type of block to use with flatten")
            .build();
    private final Setting<Boolean> radialSetting = builder("Radial", true)
            .setDescription("If to place the blocks in a radial pattern")
            .build();
    private final Setting<Boolean> stopOnSneakSetting = builder("Stop on Sneak", false)
            .setDescription("If to stop placing blocks when sneaking")
            .build();
    private final Setting<Integer> blocksSetting = numberBuilder("Blocks", 4)
            .setMin(1)
            .setMax(20)
            .setScale(1)
            .setDescription("How many blocks to place per tick")
            .build();
    private final Setting<Integer> yOffsetSetting = numberBuilder("Y-Offset", 0)
            .setMin(0)
            .setMax(2)
            .setScale(1)
            .setDescription("The y-offset to place blocks at")
            .build();

    @Override
    public void onDisable()
    {
        super.onDisable();
        if (MC.thePlayer != null)
        {
           // Nebula.INSTANCE.getInventoryManager().syncSlot();
        }
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
        for (final BlockInfo info : placementInfoList)
        {
            InteractionManager.INSTANCE.rightClickBlock(info.getPos(), info.getFacing(), true);
        }
    };

    private List<BlockInfo> getPlacements()
    {
        final Set<BlockPos> positions = new HashSet<>();
        final int range = rangeSetting.getValue();
        final BlockPos origin = PlayerUtil.getOrigin();

        for (int x = -range; x <= range; ++x)
        {
            for (int z = -range; z <= range; ++z)
            {
                final BlockPos pos = origin.add(x, -(1 + yOffsetSetting.getValue()), z);
                if (radialSetting.getValue() && MC.thePlayer.getDistance(
                        pos.getX() + 0.5,
                        pos.getY(),
                        pos.getZ() + 0.5) > rangeSetting.getValue())
                {
                    continue;
                }

                if (BlockUtil.isReplaceable(pos))
                {
                    positions.add(pos);
                }
            }
        }

        final List<BlockInfo> infoList = new LinkedList<>();
        for (final BlockPos pos : positions)
        {
            for (final EnumFacing facing : EnumFacing.values())
            {
                final BlockPos n = pos.offset(facing);
                if (!BlockUtil.isReplaceable(n))
                {
                    infoList.add(new BlockInfo(n, BlockUtil.getOpposite(facing)));
                }
                if (infoList.size() > blocksSetting.getValue())
                {
                    break;
                }
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

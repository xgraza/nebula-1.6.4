package us.nebula.client.cheat.impl.world;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.src.BlockPos;
import net.minecraft.util.EnumFacing;
import us.nebula.client.interaction.InteractionManager;
import us.nebula.client.listener.EventListener;
import us.nebula.client.listener.Subscribe;
import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.util.value.Setting;
import us.nebula.client.listener.event.game.EventUpdate;
import us.nebula.client.util.player.PlayerUtil;
import us.nebula.client.util.world.BlockInfo;
import us.nebula.client.util.world.BlockUtil;

import java.util.*;

/**
 * @author xgraza
 * @since 06/24/25
 */
@CheatManifest(name = "Flatten",
        description = "Places the currently held block in a radial pattern to flatten the area around you",
        category = CheatCategory.WORLD)
public final class FlattenCheat extends Cheat
{
    private final Setting<Integer> rangeSetting = new Setting<>(
            "Range", 4, 1, 6, 1);
    private final Setting<Boolean> radialSetting = new Setting<>(
            "Radial", true);
    private final Setting<Boolean> stopOnSneakSetting = new Setting<>(
            "Stop on Sneak", false);
    private final Setting<Integer> blocksSetting = new Setting<>(
            "Blocks", 4, 1, 20, 1);
    private final Setting<Integer> yOffsetSetting = new Setting<>(
            "Y-Offset", 0, 0, 2, 1);

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
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

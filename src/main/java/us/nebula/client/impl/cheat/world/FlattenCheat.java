package us.nebula.client.impl.cheat.world;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
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
import us.nebula.client.util.player.PlayerUtil;
import us.nebula.client.util.world.BlockInfo;
import us.nebula.client.util.world.BlockUtil;

import java.util.*;

/**
 * @author xgraza
 * @since 06/24/25
 */
@CheatManifest(name = "Flatten",
        description = "Flattens the area around you",
        category = CheatCategory.WORLD)
public final class FlattenCheat extends Cheat
{
    private final Setting<Integer> rangeSetting = new Setting<>(
            "Range", 4, 1, 6, 1);
    private final Setting<Boolean> radialSetting = new Setting<>(
            "Radial", true);
    private final Setting<Integer> blocksSetting = new Setting<>(
            "Blocks", 4, 1, 20, 1);
    private final Setting<Boolean> breakAboveSetting = new Setting<>(
            "Break Above", false);

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        final ItemStack heldStack = MC.thePlayer.getHeldItem();
        if (heldStack == null || !(heldStack.getItem() instanceof ItemBlock))
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
                final BlockPos pos = origin.add(x, -1, z);
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

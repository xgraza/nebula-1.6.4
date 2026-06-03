package ez.nebula.client.impl.module.world;

import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.listener.event.game.EventUpdate;
import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.player.InteractionManager;
import ez.nebula.client.api.setting.Setting;
import ez.nebula.client.core.Nebula;
import ez.nebula.client.util.math.MathUtil;
import ez.nebula.client.util.minecraft.player.InventoryUtil;
import ez.nebula.client.util.minecraft.player.PlayerUtil;
import ez.nebula.client.util.minecraft.world.BlockUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.BlockWood;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemDye;
import net.minecraft.src.BlockPos;
import net.minecraft.util.EnumFacing;

import java.util.List;
import java.util.TreeMap;

/**
 * @author xgraza
 * @since 6/2/26
 */
@ModuleManifest(name = "AutoTree",
        description = "Automatically plants and/or bonemeals saplings",
        category = ModuleCategory.WORLD)
public final class AutoTreeModule extends Module
{
    private final Setting<Double> rangeSetting = numberBuilder("Range", 4.5)
            .setMin(1.0)
            .setMax(6.0)
            .setScale(0.5)
            .setDescription("How far to plant or bonemeal saplings")
            .build();

    private final Setting<Boolean> plantSetting = builder("Plant", true)
            .setDescription("If to automatically plant saplings")
            .build();
    private final Setting<Integer> spacingSetting = numberBuilder("Space", 3)
            .setMin(1)
            .setMax(20)
            .setScale(1)
            .setDescription("The spacing in blocks between placing saplings")
            .setVisibility((value) -> plantSetting.getValue())
            .build();
    private final Setting<Boolean> oakSaplingSetting = builder("Oak Saplings", true)
            .setDescription("If to plant oak saplings")
            .setVisibility((value) -> plantSetting.getValue())
            .build();
    private final Setting<Boolean> darkOakSaplingSetting = builder("Dark Oak Saplings", true)
            .setDescription("If to plant dark oak saplings")
            .setVisibility((value) -> plantSetting.getValue())
            .build();
    private final Setting<Boolean> birchSaplingSetting = builder("Birch Saplings", true)
            .setDescription("If to plant birch saplings")
            .setVisibility((value) -> plantSetting.getValue())
            .build();
    private final Setting<Boolean> spruceSaplingSetting = builder("Spruce Saplings", true)
            .setDescription("If to plant spruce saplings")
            .setVisibility((value) -> plantSetting.getValue())
            .build();
    private final Setting<Boolean> acaciaSaplingSetting = builder("Acacia Saplings", true)
            .setDescription("If to plant acacia saplings")
            .setVisibility((value) -> plantSetting.getValue())
            .build();
    private final Setting<Boolean> jungleSaplingSetting = builder("Jungle Saplings", true)
            .setDescription("If to plant jungle saplings")
            .setVisibility((value) -> plantSetting.getValue())
            .build();

    private final Setting<Boolean> bonemealSetting = builder("Bonemeal", false)
            .setDescription("If to automatically bonemeal saplings")
            .build();
    private final Setting<Integer> packetsSetting = numberBuilder("Packets", 5)
            .setMin(1)
            .setMax(20)
            .setScale(1)
            .setDescription("How many times to bonemeal a sapling in a tick")
            .setVisibility((value) -> bonemealSetting.getValue())
            .build();

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        if (bonemealSetting.getValue())
        {
            handleBonemeal();
        }
        if (plantSetting.getValue())
        {
            handlePlanting();
        }
    };

    private void handlePlanting()
    {
        final BlockPos placePos = getSaplingPlacePos();
        if (placePos == null)
        {
            return;
        }
        final int slot = getSapplingSlot();
        if (slot == -1)
        {
            return;
        }

        Nebula.INSTANCE.getInventoryManager().setSlot(slot);
        InteractionManager.INSTANCE.rightClickBlock(placePos.down(), EnumFacing.UP, false);
        Nebula.INSTANCE.getInventoryManager().syncSlot();
    }

    private void handleBonemeal()
    {
        final BlockPos saplingPos = getNearestTree(
                rangeSetting.getValue().intValue(), false, PlayerUtil.getOrigin());
        if (saplingPos == null)
        {
            return;
        }
        final int slot = getBonemealSlot();
        if (slot == -1)
        {
            return;
        }

        Nebula.INSTANCE.getInventoryManager().setSlot(slot);
        for (int i = 0; i < packetsSetting.getValue(); ++i)
        {
            InteractionManager.INSTANCE.rightClickBlock(saplingPos, BlockUtil.getOpposite(PlayerUtil.getFacing()), false);
        }
        Nebula.INSTANCE.getInventoryManager().syncSlot();
    }

    private BlockPos getNearestTree(final int range, final boolean includeWood, final BlockPos origin)
    {
        final TreeMap<Double, BlockPos> blockDistances = new TreeMap<>(); // haha, get it?
        final List<BlockPos> surroundingList = BlockUtil.RADIAL_BLOCK_MAP.get(range);
        for (final BlockPos offset : surroundingList)
        {
            final BlockPos pos = origin.add(offset);
            final Block block = MC.theWorld.getBlock(pos);
            if (block instanceof BlockSapling || (includeWood && (block instanceof BlockWood || block instanceof BlockLeaves)))
            {
                blockDistances.put(MathUtil.getDistanceSq(pos, origin), pos);
            }
        }
        if (blockDistances.isEmpty())
        {
            return null;
        }
        return blockDistances.firstEntry().getValue();
    }

    private BlockPos getSaplingPlacePos()
    {
        final int r = rangeSetting.getValue().intValue();
        final List<BlockPos> surroundingList = BlockUtil.RADIAL_BLOCK_MAP.get(r);
        final BlockPos origin = PlayerUtil.getOrigin();
        for (final BlockPos offset : surroundingList)
        {
            if (offset.getY() != 0)
            {
                continue;
            }
            final BlockPos pos = origin.add(offset);
            if (!BlockUtil.isReplaceable(pos))
            {
                continue;
            }
            final Block placeOnBlock = MC.theWorld.getBlock(pos.down());
            if (placeOnBlock == Blocks.grass)
            {
                // make sure we don't place too close!
                final BlockPos nearestSaplingPos = getNearestTree(spacingSetting.getValue() + 1, true, pos);
                if (nearestSaplingPos != null && MathUtil.getDistance(nearestSaplingPos, pos) <= spacingSetting.getValue())
                {
                    continue;
                }
                return pos;
            }
        }
        return null;
    }

    private int getBonemealSlot()
    {
        return InventoryUtil.getSlot(0, 9, (stack) ->
        {
            if (!(stack.getItem() instanceof ItemDye))
            {
                return false;
            }
            return stack.getItemDamage() == 15; // bonemeal
        });
    }

    private int getSapplingSlot()
    {
        return InventoryUtil.getSlot(0, 9, (stack) ->
        {
            if (!(stack.getItem() instanceof ItemBlock))
            {
                return false;
            }
            final Block block = ((ItemBlock) stack.getItem()).getBlock();
            if (!(block instanceof BlockSapling))
            {
                return false;
            }
            final int subType = stack.getItemDamage();
            switch (subType)
            {
                case 0:
                {
                    return oakSaplingSetting.getValue();
                }
                case 1:
                {
                    return spruceSaplingSetting.getValue();
                }
                case 2:
                {
                    return birchSaplingSetting.getValue();
                }
                case 3:
                {
                    return jungleSaplingSetting.getValue();
                }
                case 4:
                {
                    return acaciaSaplingSetting.getValue();
                }
                case 5:
                {
                    return darkOakSaplingSetting.getValue();
                }
                default:
                {
                    return false;
                }
            }
        });
    }
}

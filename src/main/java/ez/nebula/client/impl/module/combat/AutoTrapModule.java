package ez.nebula.client.impl.module.combat;

import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.listener.event.game.EventUpdate;
import ez.nebula.client.api.listener.event.render.EventRender3D;
import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.player.InteractionManager;
import ez.nebula.client.api.setting.ColorSetting;
import ez.nebula.client.api.setting.NumberSetting;
import ez.nebula.client.api.setting.Setting;
import ez.nebula.client.api.setting.block.BlockSetting;
import ez.nebula.client.core.Nebula;
import ez.nebula.client.impl.module.player.FreecamModule;
import ez.nebula.client.impl.module.render.HUDModule;
import ez.nebula.client.util.math.MathUtil;
import ez.nebula.client.util.minecraft.player.InventoryUtil;
import ez.nebula.client.util.minecraft.player.PlayerUtil;
import ez.nebula.client.util.minecraft.world.BlockInfo;
import ez.nebula.client.util.minecraft.world.BlockUtil;
import ez.nebula.client.util.render.world.QuadMask;
import ez.nebula.client.util.render.world.Render3D;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.src.BlockPos;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author xgraza
 * @since 9/1/26
 */
@ModuleManifest(name = "AutoTrap",
        description = "Automatically traps someone in an enclosure",
        category = ModuleCategory.COMBAT)
public final class AutoTrapModule extends Module
{
    private final BlockSetting blockSetting = blockBuilder("Block")
            .setBlock(Blocks.obsidian)
            .setDescription("The kind of block to surround someone with")
            .build();
    private final NumberSetting<Double> rangeSetting = numberBuilder("Range", 4.5)
            .setMin(1.0)
            .setMax(6.0)
            .setScale(0.5)
            .setDescription("The place range")
            .build();
    private final NumberSetting<Integer> blocksSetting = numberBuilder("Blocks", 4)
            .setMin(1)
            .setMax(20)
            .setScale(1)
            .setDescription("How many blocks to place per tick")
            .build();
    private final Setting<Boolean> renderSetting = builder("Render", true)
            .setDescription("If to render the blocks to place around a target")
            .build();

    private final List<BlockPos> placementList = new CopyOnWriteArrayList<>();
    private EntityPlayer target;

    @Override
    public void onDisable()
    {
        super.onDisable();
        target = null;
        placementList.clear();
        if (MC.thePlayer != null)
        {
            Nebula.INSTANCE.getInventoryManager().syncSlot();
        }
    }

    @Subscribe
    private final EventListener<EventRender3D> render3DEventListener = event ->
    {
        if (!renderSetting.getValue())
        {
            return;
        }
        final ColorSetting color = HUDModule.INSTANCE.primaryColorSetting;
        for (final BlockPos pos : placementList)
        {
            final AxisAlignedBB bb = new AxisAlignedBB(pos);
            Render3D.filledAABB(bb, QuadMask.ALL_FACES, color.getValueInt(80));
            Render3D.outlinedAABB(bb, 1.5f, QuadMask.ALL_FACES, color.getValueInt());
        }
    };

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        target = getTarget();
        if (target == null)
        {
            if (!placementList.isEmpty())
            {
                Nebula.INSTANCE.getInventoryManager().syncSlot();
            }
            placementList.clear();
            return;
        }

        final boolean hadItems = !placementList.isEmpty();
        queueBlocks(target);
        if (placementList.isEmpty())
        {
            if (hadItems)
            {
                Nebula.INSTANCE.getInventoryManager().syncSlot();
            }
            return;
        }

        final int slot = InventoryUtil.getHotbarSlot(blockSetting::isBlock);
        if (slot == -1)
        {
            Nebula.INSTANCE.getInventoryManager().syncSlot();
            return;
        }

        int placed = 0;
        for (final BlockPos pos : placementList)
        {
            if (placed >= blocksSetting.getValue())
            {
                Nebula.INSTANCE.getInventoryManager().syncSlot();
                return;
            }
            final BlockInfo info = BlockUtil.getPlacement(pos);
            if (info == null)
            {
                continue;
            }
            Nebula.INSTANCE.getInventoryManager().setSlot(slot);
            if (InteractionManager.INSTANCE.rightClickBlock(info.getPos(), info.getFacing(), true))
            {
                ++placed;
            }
        }
    };

    private EntityPlayer getTarget()
    {
        return MC.theWorld.playerEntities.stream()
                .filter((player) -> !player.equals(MC.thePlayer)
                        && !player.isDead
                        && player.getHealth() > 0.0f
                        && player.getEntityId() != FreecamModule.CAMERA_ENTITY_ID
                        && player.getDistanceToEntity(MC.thePlayer) <= rangeSetting.getValue())
                .min(Comparator.comparingDouble((player) -> MC.thePlayer.getDistanceToEntity(player)))
                .orElse(null);
    }

    private void queueBlocks(final EntityPlayer player)
    {
        placementList.clear();

        final Queue<BlockPos> collidingPositions = new ArrayDeque<>();
        collidingPositions.add(PlayerUtil.getOrigin(player));
        final Set<BlockPos> exploredSet = new HashSet<>(collidingPositions);

        while (!collidingPositions.isEmpty())
        {
            final BlockPos collidingPos = collidingPositions.poll();
            for (final EnumFacing facing : EnumFacing.values())
            {
                final BlockPos neighbor = collidingPos.offset(facing);
                if (MathUtil.getDistanceFromPlayer(
                        neighbor.getX() + 0.5, neighbor.getY() + 0.5, neighbor.getZ() + 0.5) > rangeSetting.getValue())
                {
                    continue;
                }

                if (player.boundingBox.intersectsWith(new AxisAlignedBB(neighbor)))
                {
                    if (exploredSet.add(neighbor))
                    {
                        collidingPositions.add(neighbor);
                    }
                } else
                {
                    if (!placementList.contains(neighbor))
                    {
                        placementList.add(neighbor);
                    }
                }
            }
        }

        placementList.removeIf((pos) -> !BlockUtil.isReplaceable(pos));
        placementList.sort(Comparator.comparingDouble(MathUtil::getDistanceFromPlayer));
    }
}

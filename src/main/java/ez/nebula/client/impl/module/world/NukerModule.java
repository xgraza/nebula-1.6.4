package ez.nebula.client.impl.module.world;

import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.listener.event.game.EventUpdate;
import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.player.InteractionManager;
import ez.nebula.client.api.setting.NumberSetting;
import ez.nebula.client.core.Nebula;
import ez.nebula.client.util.math.AngleUtil;
import ez.nebula.client.util.minecraft.player.ChatUtil;
import ez.nebula.client.util.minecraft.player.InventoryUtil;
import ez.nebula.client.util.minecraft.player.PlayerUtil;
import ez.nebula.client.util.minecraft.world.BlockInfo;
import ez.nebula.client.util.minecraft.world.BlockUtil;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.src.BlockPos;
import net.minecraft.util.EnumFacing;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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

    private final NumberSetting<Double> rangeSetting = numberBuilder("Range", 4.5)
            .setMin(1.0)
            .setMax(6.0)
            .setScale(0.5)
            .setDescription("The range to break at")
            .build();
    private final NumberSetting<Integer> blocksSetting = numberBuilder("Blocks", 10)
            .setMin(1)
            .setMax(20)
            .setScale(1)
            .setDescription("How many blocks to break in a tick")
            .build();

    private BlockInfo info;

    @Override
    public void onDisable()
    {
        super.onDisable();
        info = null;
        if (MC.thePlayer != null)
        {
            Nebula.INSTANCE.getInventoryManager().syncSlot();
        }
    }

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        if (info != null)
        {
            if (MC.thePlayer.getDistance(info.getPos().getX() + 0.5, info.getPos().getY() + 1, info.getPos().getZ() + 0.5) > rangeSetting.getValue())
            {
                Nebula.INSTANCE.getInventoryManager().syncSlot();
                info = null;
            } else
            {
                final int slot = InventoryUtil.getBestToolSlotFor(MC.theWorld.getBlock(info.getPos()));
                if (slot != -1 && Nebula.INSTANCE.getInventoryManager().getSlot() != slot)
                {
                    Nebula.INSTANCE.getInventoryManager().setSlot(slot);
                }
                if (!InteractionManager.INSTANCE.breakBlock(info.getPos(), info.getFacing()))
                {
                    return;
                }
                info = null;
                Nebula.INSTANCE.getInventoryManager().syncSlot();
            }
        }

        final List<BlockPos> breakList = getBreakList();
        if (breakList.isEmpty())
        {
            if (info != null)
            {
                Nebula.INSTANCE.getInventoryManager().syncSlot();
            }
            info = null;
            return;
        }
        int blocks = 0;
        for (final BlockPos pos : breakList)
        {
            EnumFacing face = AngleUtil.getVisibleFace(pos, 6.0);
            if (face == null)
            {
                face = EnumFacing.UP;
            }
            final int slot = InventoryUtil.getBestToolSlotFor(MC.theWorld.getBlock(pos));
            if (slot != -1 && Nebula.INSTANCE.getInventoryManager().getSlot() != slot)
            {
                Nebula.INSTANCE.getInventoryManager().setSlot(slot);
            }
            if (InteractionManager.INSTANCE.breakBlock(pos, face))
            {
                ++blocks;
            } else
            {
                info = new BlockInfo(pos, face);
                break;
            }
            if (blocks >= blocksSetting.getValue())
            {
                break;
            }
        }
        Nebula.INSTANCE.getInventoryManager().syncSlot();
    };

    private List<BlockPos> getBreakList()
    {
        final List<BlockPos> breakList = new ArrayList<>();
        final BlockPos origin = PlayerUtil.getOrigin();
        for (final BlockPos offset : BlockUtil.RADIAL_BLOCK_MAP.get(rangeSetting.getValue().intValue()))
        {
            final BlockPos pos = origin.add(offset);
            if (MC.thePlayer.getDistance(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5) > rangeSetting.getValue())
            {
                continue;
            }

            final Block block = MC.theWorld.getBlock(pos);
            if (block.blockHardness == -1.0f || block.getMaterial() == Material.air)
            {
                continue;
            }

            if (block != Blocks.obsidian)
            {
                continue;
            }

            breakList.add(pos);
        }

        if (!breakList.isEmpty())
        {
            breakList.sort(Comparator.comparingDouble((pos) -> MC.thePlayer.getDistance(pos)));
        }
        return breakList;
    }
}

package ez.nebula.client.impl.module.world;

import ez.nebula.client.Nebula;
import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.listener.event.game.EventUpdate;
import ez.nebula.client.api.listener.event.render.EventRender3D;
import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.player.InteractionManager;
import ez.nebula.client.api.setting.NumberSetting;
import ez.nebula.client.api.setting.Setting;
import ez.nebula.client.impl.module.combat.AutoBedModule;
import ez.nebula.client.impl.module.combat.KillAuraModule;
import ez.nebula.client.impl.module.render.HUDModule;
import ez.nebula.client.util.math.AngleUtil;
import ez.nebula.client.util.math.MathUtil;
import ez.nebula.client.util.minecraft.player.InventoryUtil;
import ez.nebula.client.util.minecraft.player.PlayerUtil;
import ez.nebula.client.util.minecraft.world.BlockInfo;
import ez.nebula.client.util.minecraft.world.BlockUtil;
import ez.nebula.client.util.render.world.QuadMask;
import ez.nebula.client.util.render.world.Render3D;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.src.BlockPos;
import net.minecraft.util.AxisAlignedBB;
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
    private final Setting<Boolean> underSetting = builder("Break Under", false)
            .setDescription("If to break underneath you")
            .build();
    private final Setting<Boolean> keepYSetting = builder("Keep Y", true)
            .setDescription("If to keep your y position for what blocks to break")
            .build();

    private List<BlockPos> breakList;
    private BlockInfo info;
    private double posY = -1;

    @Override
    public void onDisable()
    {
        super.onDisable();
        info = null;
        breakList = null;
        if (MC.thePlayer != null)
        {
            Nebula.INVENTORY.sync();
        }
        posY = -1;
        PlayerControllerMP.ALLOW_BREAK_OVERRIDE = false;
    }

    @Subscribe
    private final EventListener<EventRender3D> render3DEventListener = event ->
    {
        if (breakList == null || breakList.isEmpty())
        {
            return;
        }
        for (final BlockPos pos : breakList)
        {
            if (info != null && info.getPos().equals(pos))
            {
                continue;
            }
            final AxisAlignedBB bb = new AxisAlignedBB(pos);
            Render3D.filledAABB(bb, QuadMask.ALL_FACES, HUDModule.INSTANCE.primaryColorSetting.getValueInt(80));
            Render3D.outlinedAABB(bb, 1.5f, QuadMask.ALL_FACES, HUDModule.INSTANCE.primaryColorSetting.getValueInt());
        }
        if (info != null)
        {
            final AxisAlignedBB bb = new AxisAlignedBB(info.getPos());
            final int faceMask = QuadMask.mask(info.getFacing());
            Render3D.filledAABB(bb, faceMask, 0x80FF0000);
            Render3D.outlinedAABB(bb, 1.5f, faceMask, 0xFFFFFFFF);
        }
    };

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        if (AutoBedModule.INSTANCE.isActive() || KillAuraModule.INSTANCE.isAttacking())
        {
            if (info != null)
            {
                Nebula.INVENTORY.sync();
            }
            info = null;
            return;
        }

        if (info != null)
        {
            PlayerControllerMP.ALLOW_BREAK_OVERRIDE = true;

            if (MathUtil.getDistanceFromPlayer(info.getPos().getX() + 0.5, info.getPos().getY() + 0.5, info.getPos().getZ() + 0.5) > rangeSetting.getValue())
            {
                Nebula.INVENTORY.sync();
                info = null;
            } else
            {
                swapToBestBlockSlot(info.getPos());
                if (!InteractionManager.INSTANCE.breakBlock(info.getPos(), info.getFacing()))
                {
                    return;
                }
                info = null;
                Nebula.INVENTORY.sync();
                PlayerControllerMP.ALLOW_BREAK_OVERRIDE = false;
            }
        }

        breakList = getBreakList();
        if (breakList.isEmpty())
        {
            PlayerControllerMP.ALLOW_BREAK_OVERRIDE = false;
            Nebula.INVENTORY.sync();
            info = null;
            return;
        }
        PlayerControllerMP.ALLOW_BREAK_OVERRIDE = true;
        int blocks = 0;
        for (final BlockPos pos : breakList)
        {
            EnumFacing face = AngleUtil.getVisibleFace(pos, 6.0);
            if (face == null)
            {
                face = EnumFacing.UP;
            }
            swapToBestBlockSlot(pos);
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
        PlayerControllerMP.ALLOW_BREAK_OVERRIDE = false;
        Nebula.INVENTORY.sync();
    };

    private void swapToBestBlockSlot(final BlockPos pos)
    {
        final int slot = InventoryUtil.getBestToolSlotFor(MC.theWorld.getBlock(pos));
        if (slot != InventoryUtil.INVALID_SLOT)
        {
            Nebula.INVENTORY.spoof(slot);
        }
    }

    private List<BlockPos> getBreakList()
    {
        final List<BlockPos> breakList = new ArrayList<>();

        if (!keepYSetting.getValue() || posY == -1)
        {
            posY = Math.round(MC.thePlayer.boundingBox.minY);
        }
        
        final BlockPos origin = PlayerUtil.getOrigin(posY);
        for (final BlockPos offset : BlockUtil.RADIAL_BLOCK_MAP.get(rangeSetting.getValue().intValue()))
        {
            if (!underSetting.getValue() && offset.getY() < 0)
            {
                continue;
            }
            final BlockPos pos = origin.add(offset);
            if (MathUtil.getDistanceFromPlayer(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > rangeSetting.getValue())
            {
                continue;
            }

            final Block block = MC.theWorld.getBlock(pos);
            if (block.blockHardness == -1.0f || block.blockHardness == 100.0f || block.getMaterial() == Material.air)
            {
                continue;
            }

            breakList.add(pos);
        }

        if (!breakList.isEmpty())
        {
            breakList.sort(Comparator.comparingDouble(MathUtil::getDistanceFromPlayer));
        }
        return breakList;
    }
}

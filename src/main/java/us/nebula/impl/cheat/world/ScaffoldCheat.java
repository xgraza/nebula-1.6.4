package us.nebula.impl.cheat.world;

import net.minecraft.src.BlockPos;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import us.nebula.Nebula;
import us.nebula.api.interaction.InteractionManager;
import us.nebula.api.listener.EventListener;
import us.nebula.api.listener.Subscribe;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.api.value.Setting;
import us.nebula.impl.event.game.EventUpdate;
import us.nebula.impl.event.render.EventRender3D;
import us.nebula.util.player.InventoryUtil;
import us.nebula.util.player.PlayerUtil;
import us.nebula.util.render.RenderUtil;
import us.nebula.util.world.BlockUtil;

/**
 * @author xgraza
 * @since 02/16/25
 */
@CheatManifest(name = "Scaffold",
        description = "Rapidly places blocks under you",
        category = CheatCategory.WORLD)
public final class ScaffoldCheat extends Cheat
{
    private final Setting<Double> extend = new Setting<>(
            "Extend", 0.0, 0.0, 6.0, 0.5);
    private final Setting<Boolean> towerSetting = new Setting<>(
            "Tower", true);
    private final Setting<Boolean> renderSetting = new Setting<>(
            "Render", false);

    private BlockData blockData;

    @Override
    protected void onDisable()
    {
        super.onDisable();
        blockData = null;
    }

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        final int slot = InventoryUtil.getHotbarSlot(
                InventoryUtil.BLOCK_FILTER);
        if (slot == -1)
        {
            return;
        }

        blockData = getBlockData();
        if (blockData == null)
        {
            return;
        }

        Nebula.INSTANCE.getInventoryManager().setSlot(slot);

        final boolean result = InteractionManager.INSTANCE.rightClickBlock(blockData.pos, blockData.facing);
        if (result)
        {
            if (MC.gameSettings.keyBindJump.pressed && towerSetting.getValue())
            {
                if (MC.thePlayer.onGround || (MC.thePlayer.motionY == 0.16477328182606651))
                {
                    MC.thePlayer.motionY = 0.42f;
                }
            }
        }

        Nebula.INSTANCE.getInventoryManager().syncSlot();
    };

    @Subscribe
    private final EventListener<EventRender3D> render3DEventListener = event ->
    {
        if (!renderSetting.getValue() || blockData == null)
        {
            return;
        }

        final AxisAlignedBB aabb = new AxisAlignedBB(Vec3.createVectorHelper(
                blockData.pos.getX(), blockData.pos.getY(), blockData.pos.getZ()), 1);

        RenderUtil.filledBox3D(aabb, 0, 0x80FF0000);
        RenderUtil.outlinedBox3D(aabb, 1.5f, 0xFFFF0000);
    };

    private BlockData getBlockData()
    {
        BlockPos pos = PlayerUtil.getOrigin().add(0, -1, 0);
        if (extend.getValue() > 0.0 && !MC.gameSettings.keyBindJump.pressed)
        {
            final float yaw = MC.thePlayer.rotationYaw * 0.017453292f;

            double distance = 0.0;
            while (distance <= extend.getValue())
            {
                distance += extend.getScale().doubleValue();
                final BlockPos extendedPos = pos.add(new BlockPos(
                        (int) (-Math.sin(yaw) * distance),
                        0, (int) (Math.cos(yaw) * distance)));
                if (BlockUtil.isReplaceable(extendedPos))
                {
                    pos = extendedPos;
                    break;
                }
            }
        }

        for (final EnumFacing facing : EnumFacing.values())
        {
            final BlockPos neighbor = BlockUtil.offset(pos, facing);
            if (!BlockUtil.isReplaceable(neighbor))
            {
                return new BlockData(neighbor, BlockUtil.getOpposite(facing));
            }
        }

        for (final EnumFacing facing : EnumFacing.values())
        {
            final BlockPos neighbor = BlockUtil.offset(pos, facing);
            if (BlockUtil.isReplaceable(neighbor))
            {
                for (final EnumFacing side : EnumFacing.values())
                {
                    final BlockPos n = BlockUtil.offset(neighbor, side);
                    if (!BlockUtil.isReplaceable(n))
                    {
                        return new BlockData(n, BlockUtil.getOpposite(side));
                    }
                }
            }
        }
        return null;
    }

    private static final class BlockData
    {
        private final BlockPos pos;
        private final EnumFacing facing;

        public BlockData(BlockPos pos, EnumFacing facing)
        {
            this.pos = pos;
            this.facing = facing;
        }
    }
}

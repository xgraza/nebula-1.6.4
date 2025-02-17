package us.nebula.impl.cheat.player;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.src.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import us.nebula.api.listener.EventListener;
import us.nebula.api.listener.Subscribe;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.impl.event.game.EventUpdate;

import static org.lwjgl.input.Keyboard.KEY_G;

/**
 * @author xgraza
 * @since 02/16/25
 */
@CheatManifest(name = "Scaffold", category = CheatCategory.PLAYER)
public final class ScaffoldCheat extends Cheat
{
    private int towerTicks;

    @Override
    protected void onDisable()
    {
        super.onDisable();
        towerTicks = 0;
    }

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        final int slot = getBlockSlot();
        if (slot == -1)
        {
            return;
        }

        final BlockData blockData = getBlockData();
        if (blockData == null)
        {
            return;
        }

        final int prevSlot = MC.thePlayer.inventory.currentItem;
        MC.thePlayer.inventory.currentItem = slot;

        final int x = blockData.pos.getX();
        final int y = blockData.pos.getY();
        final int z = blockData.pos.getZ();

        final boolean result = MC.playerController.onPlayerRightClick(MC.thePlayer, MC.theWorld,
                MC.thePlayer.inventory.getStackInSlot(slot),
                x, y, z,
                blockData.facing.order_a,
                Vec3.createVectorHelper(x + 0.5, y + 0.5, z + 0.5));
        if (result)
        {
            MC.thePlayer.swingItem();

            if (MC.gameSettings.keyBindJump.pressed)
            {
                if (towerTicks >= 9)
                {
                    towerTicks = 0;
                    //MC.thePlayer.motionY -= 0.08;
                } else if (MC.thePlayer.onGround || MC.thePlayer.motionY == 0.16477328182606651)
                {
                    MC.thePlayer.motionY = 0.42f;
                }
                ++towerTicks;
            } else
            {
                towerTicks = 0;
            }
        }

        MC.thePlayer.inventory.currentItem = prevSlot;
    };

    private BlockData getBlockData()
    {
        final BlockPos pos = new BlockPos(MathHelper.floor_double(MC.thePlayer.posX),
                MathHelper.floor_double(MC.thePlayer.boundingBox.minY) - 1,
                MathHelper.floor_double(MC.thePlayer.posZ));
        for (final EnumFacing facing : EnumFacing.values())
        {
            final BlockPos neighbor = offset(pos, facing);
            if (!isReplaceable(neighbor))
            {
                return new BlockData(neighbor, opposite(facing));
            }
        }

        for (final EnumFacing facing : EnumFacing.values())
        {
            final BlockPos neighbor = offset(pos, facing);
            if (isReplaceable(neighbor))
            {
                for (final EnumFacing side : EnumFacing.values())
                {
                    final BlockPos n = offset(neighbor, side);
                    if (!isReplaceable(n))
                    {
                        return new BlockData(n, opposite(side));
                    }
                }
            }
        }
        return null;
    }

    private boolean isReplaceable(final BlockPos pos)
    {
        return MC.theWorld.getBlock(pos.getX(), pos.getY(), pos.getZ()).getMaterial().isReplaceable();
    }

    private EnumFacing opposite(final EnumFacing facing)
    {
        return EnumFacing.values()[facing.order_b];
    }

    private BlockPos offset(final BlockPos pos, final EnumFacing facing)
    {
        return new BlockPos(pos.getX() + facing.getFrontOffsetX(),
                pos.getY() + facing.getFrontOffsetY(),
                pos.getZ() + facing.getFrontOffsetZ());
    }

    private int getBlockSlot()
    {
        for (int i = 0; i < 9; ++i)
        {
            final ItemStack itemStack = MC.thePlayer.inventory.getStackInSlot(i);
            if (itemStack != null && itemStack.getItem() instanceof ItemBlock)
            {
                return i;
            }
        }
        return -1;
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

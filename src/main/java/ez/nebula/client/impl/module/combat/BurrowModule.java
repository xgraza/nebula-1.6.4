package ez.nebula.client.impl.module.combat;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.src.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import ez.nebula.client.core.Nebula;
import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.listener.event.game.EventUpdate;
import ez.nebula.client.api.listener.event.network.EventPacket;
import ez.nebula.client.api.setting.Setting;
import ez.nebula.client.util.minecraft.player.PlayerUtil;
import ez.nebula.client.util.minecraft.world.BlockUtil;

/**
 * @author xgraza
 * @since 03/25/25
 */
@ModuleManifest(name = "Burrow",
        description = "Places a blast resistant block at your feet to prevent explosion damage",
        category = ModuleCategory.COMBAT)
public final class BurrowModule extends Module
{
    private final Setting<Boolean> instantSetting = builder("Instant", false)
            .setDescription("If to use packets to observe when the burrow block is replaced to instantly replace it")
            .build();

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        // SWAP IZZO
        // SEEYUH!
        if (!isBurrowed())
        {
            burrow();
            return;
        }
        if (!instantSetting.getValue())
        {
            toggle();
        }
    };

    @Subscribe
    private final EventListener<EventPacket.Inbound> inboundEventListener = event ->
    {
        if (MC.thePlayer == null || MC.theWorld == null || !instantSetting.getValue())
        {
            return;
        }
        if (event.getPacket() instanceof S23PacketBlockChange)
        {
            final S23PacketBlockChange packet = event.getPacket();
            final BlockPos pos = PlayerUtil.getOrigin().add(0, 1, 0);
            if (packet.getX() == pos.getX()
                    && packet.getY() == pos.getY()
                    && packet.getZ() == pos.getZ()
                    && packet.getType().getMaterial().isReplaceable())
            {
                burrow();
            }
        }
    };

    private void burrow()
    {
        final int slot = getBlockSlot();
        if (slot == -1)
        {
            notifyError("Obsidian/EnderChest/Anvil required in hotbar for burrow.", 7500L);
            toggle();
            return;
        }
        final BlockData blockData = getBlockData();
        if (blockData == null)
        {
            notifyError("No position found to place at!", 7500L);
            toggle();
            return;
        }

        final boolean sneak = isBlockUnderInteractable() && !MC.thePlayer.isSneaking();
        if (sneak)
        {
            MC.thePlayer.sendQueue.addToSendQueue(
                    new C0BPacketEntityAction(MC.thePlayer, 1));
        }

        Nebula.INSTANCE.getInventoryManager().setSlot(slot);

        final Vec3 hitVec = Vec3.createVectorHelper(
                blockData.pos.getX() + 0.5,
                blockData.pos.getY() + 0.5,
                blockData.pos.getZ() + 0.5);
        MC.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(
                blockData.pos.getX(),
                blockData.pos.getY(),
                blockData.pos.getZ(),
                blockData.facing.order_a,
                MC.thePlayer.getHeldItem(),
                (float) (hitVec.xCoord - blockData.pos.getX()),
                (float) (hitVec.yCoord - blockData.pos.getY()),
                (float) (hitVec.zCoord - blockData.pos.getZ())
        ));
        MC.thePlayer.swingItem();
        Nebula.INSTANCE.getInventoryManager().syncSlot();

        if (sneak)
        {
            MC.thePlayer.sendQueue.addToSendQueue(
                    new C0BPacketEntityAction(MC.thePlayer, 2));
        }
    }

    private boolean isBurrowed()
    {
        return !BlockUtil.isReplaceable(PlayerUtil.getOrigin());
    }

    private boolean isBlockUnderInteractable()
    {
        final int posX = MathHelper.floor_double(MC.thePlayer.posX);
        final int posY = (int) (Math.round(MC.thePlayer.boundingBox.minY) - 1);
        final int posZ = MathHelper.floor_double(MC.thePlayer.posZ);
        return BlockUtil.INTERACTABLE_BLOCK_LIST.contains(MC.theWorld.getBlock(posX, posY, posZ));
    }

    private int getBlockSlot()
    {
        for (int i = 0; i < 9; ++i)
        {
            final ItemStack itemStack = MC.thePlayer.inventory.getStackInSlot(i);
            if (itemStack == null || !(itemStack.getItem() instanceof ItemBlock))
            {
                continue;
            }
            final Block block = ((ItemBlock) itemStack.getItem()).getBlock();
            if (block instanceof BlockObsidian
                    || block instanceof BlockEnderChest
                    || block instanceof BlockAnvil)
            {
                return i;
            }
        }
        return -1;
    }

    private BlockData getBlockData()
    {
        final BlockPos origin = PlayerUtil.getOrigin();
        for (final EnumFacing facing : EnumFacing.values())
        {
            final BlockPos n = BlockUtil.offset(origin, facing);
            if (!BlockUtil.isReplaceable(n))
            {
                return new BlockData(n, BlockUtil.getOpposite(facing));
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

package us.nebula.client.cheat.impl.world;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.src.BlockPos;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import us.nebula.client.Nebula;
import us.nebula.client.listener.EventListener;
import us.nebula.client.listener.Subscribe;
import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.util.render.QuadMask;
import us.nebula.client.util.value.Setting;
import us.nebula.client.listener.event.game.EventUpdate;
import us.nebula.client.listener.event.network.EventPacket;
import us.nebula.client.listener.event.render.EventRender3D;
import us.nebula.client.util.render.RenderUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xgraza
 * @since 04/02/25
 */
@CheatManifest(name = "AntiGhostBlock",
        description = "Prevents ghost blocks from spawning from placing or breaking",
        category = CheatCategory.WORLD)
public final class AntiGhostBlockCheat extends Cheat
{
    private final Setting<Boolean> placeSetting = new Setting<>(
            "Place", true);
    private final Setting<Boolean> breakSetting = new Setting<>(
            "Break", true);

    private final Setting<Boolean> packetSetting = new Setting<>(
            "Packet", true);
    private final Setting<Double> confirmTimeSetting = new Setting<>(
            "Confirm Time", 0.5, 0.1, 5.0, 0.1);
    private final Setting<Boolean> debugRenderSetting = new Setting<>(
            "Debug Render", false);

    private final Map<BlockPos, Long> placeConfirmBlockPosMap = new ConcurrentHashMap<>();
    private final Map<BlockPos, OriginalBlockData> breakConfirmBlockPosMap = new ConcurrentHashMap<>();

    @Override
    public void onDisable()
    {
        super.onDisable();
        placeConfirmBlockPosMap.clear();
        breakConfirmBlockPosMap.clear();
    }

    @Subscribe
    private final EventListener<EventRender3D> render3DEventListener = event ->
    {
        if (placeConfirmBlockPosMap.isEmpty() || !debugRenderSetting.getValue())
        {
            return;
        }
        for (final BlockPos pos : placeConfirmBlockPosMap.keySet())
        {
            final AxisAlignedBB aabb = new AxisAlignedBB(pos);
            RenderUtil.renderFilledAABB(aabb, QuadMask.ALL_FACES, 0xAB00FF00);
            RenderUtil.renderOutlinedAABB(aabb, 1.5f, QuadMask.ALL_FACES, 0xAB00FF00);
        }
        for (final BlockPos pos : breakConfirmBlockPosMap.keySet())
        {
            final AxisAlignedBB aabb = new AxisAlignedBB(pos);
            RenderUtil.renderFilledAABB(aabb, QuadMask.ALL_FACES, 0xAB00FF00);
            RenderUtil.renderOutlinedAABB(aabb, 1.5f, QuadMask.ALL_FACES, 0xAB00FF00);
        }
    };

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        if (placeSetting.getValue() && !placeConfirmBlockPosMap.isEmpty())
        {
            for (final BlockPos pos : placeConfirmBlockPosMap.keySet())
            {
                // fix random NPE thrown (inconsistency between Netty/Main thread?)
                final Long confirmTime = placeConfirmBlockPosMap.get(pos);
                if (confirmTime == null)
                {
                    continue;
                }

                final long timeMS = confirmTime + (long) (confirmTimeSetting.getValue() * 1000.0);
                if (System.currentTimeMillis() > timeMS)
                {
                    placeConfirmBlockPosMap.remove(pos);
                    MC.theWorld.setBlockToAir(pos.getX(), pos.getY(), pos.getZ());
                }
            }
        }

        if (breakSetting.getValue() && !breakConfirmBlockPosMap.isEmpty())
        {
            for (final BlockPos pos : breakConfirmBlockPosMap.keySet())
            {
                final OriginalBlockData data = breakConfirmBlockPosMap.get(pos);
                if (data == null)
                {
                    continue;
                }
                final long timeMS = data.getTime() + (long) (confirmTimeSetting.getValue() * 1000.0);
                if (System.currentTimeMillis() > timeMS)
                {
                    breakConfirmBlockPosMap.remove(pos);
                    MC.theWorld.setBlock(pos.getX(), pos.getY(), pos.getZ(), data.getBlock());
                }
            }
        }
    };

    @Subscribe
    private final EventListener<EventPacket.Inbound> inboundEventListener = event ->
    {
        if (event.getPacket() instanceof S22PacketMultiBlockChange)
        {
            // TODO
        } else if (event.getPacket() instanceof S23PacketBlockChange)
        {
            final S23PacketBlockChange packet = event.getPacket();
            final BlockPos pos = new BlockPos(packet.getX(), packet.getY(), packet.getZ());

            if (placeSetting.getValue())
            {
                // TODO: check if the block matches server-side?
                placeConfirmBlockPosMap.remove(pos);
            }

            if (breakSetting.getValue() && breakConfirmBlockPosMap.containsKey(pos))
            {
                final Block block = packet.getType();
                if (block == null || block instanceof BlockAir)
                {
                    breakConfirmBlockPosMap.remove(pos);
                }
            }
        }
    };

    @Subscribe
    private final EventListener<EventPacket.Outbound> outboundEventListener = event ->
    {
        if (event.getPacket() instanceof C08PacketPlayerBlockPlacement && placeSetting.getValue())
        {
            final C08PacketPlayerBlockPlacement packet = event.getPacket();
            if (!isBlockPlacePacket(packet))
            {
                return;
            }

            final BlockPos pos = new BlockPos(packet.getPosX(), packet.getPosY(), packet.getPosZ());
            final EnumFacing side = EnumFacing.faceList[packet.getSide()];

            if (packetSetting.getValue())
            {
                MC.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(
                        1, pos, side.order_a));
            }
            placeConfirmBlockPosMap.put(pos.offset(side), System.currentTimeMillis());
        }

        if (event.getPacket() instanceof C07PacketPlayerDigging && breakSetting.getValue())
        {
            final C07PacketPlayerDigging packet = event.getPacket();
            if (packet.getAction() != 2) // STOP_BREAKING / FINISH
            {
                return;
            }
            final BlockPos pos = new BlockPos(packet.getX(), packet.getY(), packet.getZ());
            final Block block = MC.theWorld.getBlock(pos);
            if (block == null || block instanceof BlockAir)
            {
                return;
            }
            // TODO: retain metadata?
            breakConfirmBlockPosMap.put(pos, new OriginalBlockData(block, System.currentTimeMillis()));
        }
    };

    private boolean isBlockPlacePacket(final C08PacketPlayerBlockPlacement packet)
    {
        // ignore interact block packets
        if (packet.getPosX() == -1
                && packet.getPosY() == -1
                && packet.getPosZ() == -1
                && packet.getSide() == 255)
        {
            return false;
        }

        ItemStack itemStack = packet.getItemStack();
        // resort to the item stack in the server hand
        if (itemStack == null)
        {
            itemStack = Nebula.INSTANCE.getInventoryManager().getStack();
        }

        return itemStack != null && itemStack.getItem() instanceof ItemBlock;
    }

    private static final class OriginalBlockData
    {
        private final Block block;
        private final long time;

        public OriginalBlockData(Block block, long time)
        {
            this.block = block;
            this.time = time;
        }

        public Block getBlock()
        {
            return block;
        }

        public long getTime()
        {
            return time;
        }
    }
}

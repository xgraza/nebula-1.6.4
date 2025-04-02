package us.nebula.impl.cheat.miscellaneous;

import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.src.BlockPos;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import us.nebula.ClientSettings;
import us.nebula.api.DebugFeature;
import us.nebula.api.listener.EventListener;
import us.nebula.api.listener.Subscribe;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.api.value.Setting;
import us.nebula.impl.event.game.EventUpdate;
import us.nebula.impl.event.network.EventPacket;
import us.nebula.impl.event.render.EventRender3D;
import us.nebula.util.player.ChatUtil;
import us.nebula.util.render.RenderUtil;
import us.nebula.util.world.BlockUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xgraza
 * @since 04/02/25
 */
@CheatManifest(name = "AntiGhostBlock",
        description = "Prevents ghost blocks from spawning",
        category = CheatCategory.MISCELLANEOUS)
public final class AntiGhostBlock extends Cheat
{
    private final Setting<Boolean> packetSetting = new Setting<>(
            "Packet", true);
    private final Setting<Double> confirmTimeSetting = new Setting<>(
            "Confirm Time", 0.5, 0.1, 5.0, 0.1);
    @DebugFeature
    private final Setting<Boolean> debugRenderSetting = new Setting<>(
            "Debug Render", false);

    private final Map<BlockPos, Long> confirmBlockPosMap = new ConcurrentHashMap<>();

    @Override
    protected void onDisable()
    {
        super.onDisable();
        confirmBlockPosMap.clear();
    }

    @Subscribe
    private final EventListener<EventRender3D> render3DEventListener = event ->
    {
        if (confirmBlockPosMap.isEmpty() || !debugRenderSetting.getValue())
        {
            return;
        }
        for (final BlockPos pos : confirmBlockPosMap.keySet())
        {
            final AxisAlignedBB aabb = new AxisAlignedBB(pos);
            RenderUtil.filledBox3D(aabb, 0, 0xAB00FF00);
            RenderUtil.outlinedBox3D(aabb, 1.5f, 0xAB00FF00);
        }
    };

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        if (confirmBlockPosMap.isEmpty() || packetSetting.getValue())
        {
            return;
        }
        for (final BlockPos blockPos : confirmBlockPosMap.keySet())
        {
            final long timeMS = confirmBlockPosMap.get(blockPos)
                    + (long) (confirmTimeSetting.getValue() * 1000.0);
            if (System.currentTimeMillis() > timeMS)
            {
                if (ClientSettings.VERBOSE_LOGGING)
                {
                    ChatUtil.send("Removing ghost block @{" + blockPos + "}");
                }
                confirmBlockPosMap.remove(blockPos);
                MC.theWorld.setBlockToAir(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            }
        }
    };

    @Subscribe
    private final EventListener<EventPacket.Inbound> inboundEventListener = event ->
    {
        if (event.getPacket() instanceof S23PacketBlockChange)
        {
            final S23PacketBlockChange packet = event.getPacket();
            confirmBlockPosMap.remove(new BlockPos(packet.getX(), packet.getY(), packet.getZ()));
        }
    };

    @Subscribe
    private final EventListener<EventPacket.Outbound> outboundEventListener = event ->
    {
        if (event.getPacket() instanceof C08PacketPlayerBlockPlacement)
        {
            final C08PacketPlayerBlockPlacement packet = event.getPacket();

            final int x = packet.getPosX();
            final int y = packet.getPosY();
            final int z = packet.getPosZ();
            final int side = packet.getSide();

            // ignore interact block packets
            if (x == -1 && y == -1 && z == -1 && side == 255)
            {
                return;
            }

            if (packetSetting.getValue())
            {
                MC.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(
                        1, x, y, z, side));
            }
            confirmBlockPosMap.put(BlockUtil.offset(
                    new BlockPos(x, y, z),
                    EnumFacing.faceList[side]), System.currentTimeMillis());
        }
    };
}

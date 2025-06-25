package us.nebula.impl.cheat.movement;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import us.nebula.api.listener.EventListener;
import us.nebula.api.listener.Subscribe;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.api.value.Setting;
import us.nebula.impl.event.game.EventUpdate;
import us.nebula.impl.event.network.EventPacket;
import us.nebula.util.math.Timer;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author xgraza
 * @since 06/24/25
 */
@CheatManifest(name = "Blink",
        description = "Suspends packets to look like lag",
        category = CheatCategory.MOVEMENT)
public final class BlinkCheat extends Cheat
{
    private final Setting<Boolean> manualSetting = new Setting<>(
            "Manual", false);
    private final Setting<Double> delaySetting = new Setting<>(
            "Delay", 1.0, 0.1, 20.0, 0.1)
            .setVisibility(() -> !manualSetting.getValue());

    private final Queue<Packet> packetQueue = new ConcurrentLinkedQueue<>();
    private final Timer timer = new Timer();

    @Override
    protected void onDisable()
    {
        super.onDisable();
        if (MC.thePlayer != null)
        {
            busPackets();
        }
        packetQueue.clear();
    }

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        if (!manualSetting.getValue() && timer.hasElapsed((long) (delaySetting.getValue() * 1000.0)))
        {
            timer.resetTime();
            busPackets();
        }
    };

    @Subscribe
    private final EventListener<EventPacket.Outbound> outboundEventListener = event ->
    {
        if (event.getPacket() instanceof C03PacketPlayer
                || event.getPacket() instanceof C03PacketPlayer.C04PacketPlayerPosition
                || event.getPacket() instanceof C03PacketPlayer.C05PacketPlayerLook
                || event.getPacket() instanceof C03PacketPlayer.C06PacketPlayerPosLook
                || event.getPacket() instanceof C00PacketKeepAlive
                || event.getPacket() instanceof C0FPacketConfirmTransaction)
        {
            packetQueue.add(event.getPacket());
            event.setCanceled(true);
        }
    };

    private void busPackets()
    {
        while (!packetQueue.isEmpty())
        {
            final Packet packet = packetQueue.poll();
            if (packet == null)
            {
                break;
            }
            MC.thePlayer.sendQueue.getNetworkManager().sendPacketInstantly(packet);
        }
        packetQueue.clear();
    }

    @Override
    public String getMetadata()
    {
        return String.valueOf(packetQueue.size());
    }
}

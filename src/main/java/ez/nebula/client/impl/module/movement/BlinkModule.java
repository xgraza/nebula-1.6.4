package ez.nebula.client.impl.module.movement;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.listener.event.game.EventUpdate;
import ez.nebula.client.api.listener.event.network.EventPacket;
import ez.nebula.client.api.setting.Setting;
import ez.nebula.client.util.math.Timer;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author xgraza
 * @since 06/24/25
 */
@ModuleManifest(name = "Blink",
        description = "Suspends packets for a period of time, and the burst sends it all at once to make it appear as a laggy connection",
        category = ModuleCategory.MOVEMENT)
public final class BlinkModule extends Module
{
    private final Setting<Boolean> manualSetting = builder("Manual", false)
            .setDescription("If to manually hold packets until Blink is disabled")
            .build();
    private final Setting<Double> delaySetting = numberBuilder("Delay", 1.0)
            .setMin(0.1)
            .setMax(20.0)
            .setScale(0.1)
            .setDescription("The delay in milliseconds to release packets at")
            .setVisibility((value) -> !manualSetting.getValue())
            .build();

    private final Queue<Packet> packetQueue = new ConcurrentLinkedQueue<>();
    private final Timer timer = new Timer();

    @Override
    public void onDisable()
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

package ez.nebula.client.api.player.server;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import ez.nebula.client.api.listener.EventBus;
import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.manager.IManager;
import ez.nebula.client.api.listener.event.network.EventPacket;
import ez.nebula.client.util.io.NetworkUtil;

import java.util.Arrays;

/**
 * @author xgraza
 * @since 5/5/26
 */
public final class ServerManager implements IManager
{
    private static final Minecraft MC = Minecraft.getMinecraft();

    private final double[] packetResponseTimes = new double[20];
    private long lastPacketMS = -1L;
    private int index = 0;

    @Subscribe
    private final EventListener<EventPacket.Inbound> inboundEventListener = event ->
    {
        if (!(event.getPacket() instanceof S03PacketTimeUpdate))
        {
            return;
        }
        final long time = System.currentTimeMillis();
        if (lastPacketMS == -1L)
        {
            lastPacketMS = time;
            return;
        }
        final double difference = time - lastPacketMS;
        lastPacketMS = time;
        packetResponseTimes[index++ % packetResponseTimes.length] = Math.max(0.0, difference / 50.0);
    };

    @Override
    public void init()
    {
        EventBus.subscribe(this);
        Arrays.fill(packetResponseTimes, 0);
    }

    public String getServerIP()
    {
        final ServerData serverData = MC.getCurrentServerData();
        if (serverData == null || serverData.serverIP == null)
        {
            if (MC.isSingleplayer())
            {
                return "SP";
            }
            return "Unknown";
        }
        return serverData.serverIP.replace(":", "_");
    }

    public long getLastPacketMS()
    {
        return lastPacketMS;
    }

    public double getAverageTPS()
    {
        double sum = 0.0;
        int amount = 0;
        for (final double timeDifference : packetResponseTimes)
        {
            if (timeDifference > 0.0)
            {
                sum += timeDifference;
                ++amount;
            }
        }
        return amount == 0 ? 20.0 : sum / amount;
    }

    public double getCurrentTPS()
    {
        return packetResponseTimes[Math.max(0, index - 1) % packetResponseTimes.length];
    }

    public double getScaledLatency()
    {
        return (50L + NetworkUtil.getLatency(MC.thePlayer)) * (1.0 / Math.min(20.0, getCurrentTPS()));
    }
}

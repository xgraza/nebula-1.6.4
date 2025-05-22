package us.nebula.impl.cheat.miscellaneous;

import net.minecraft.client.gui.GuiPlayerInfo;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.network.play.server.S3APacketTabComplete;
import us.nebula.api.listener.EventListener;
import us.nebula.api.listener.Subscribe;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.api.value.Setting;
import us.nebula.impl.event.game.EventUpdate;
import us.nebula.impl.event.network.EventPacket;

/**
 * @author xgraza
 * @since 05/22/25
 */
@CheatManifest(name = "FastLatency", 
        description = "Calculates latency faster", 
        category = CheatCategory.MISCELLANEOUS)
public final class FastLatencyCheat extends Cheat
{
    private final Setting<Integer> frequencySetting = new Setting<>(
            "Frequency", 20, 2, 20, 1);

    private long sentAt;

    @Override
    protected void onDisable()
    {
        super.onDisable();
        sentAt = -1L;
    }

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        if (MC.thePlayer.ticksExisted % frequencySetting.getValue() == 0 && sentAt == -1L)
        {
            sentAt = System.currentTimeMillis();
            MC.thePlayer.sendQueue.addToSendQueue(new C14PacketTabComplete(""));
        }
    };

    @Subscribe
    private final EventListener<EventPacket.Inbound> inboundEventListener = event ->
    {
        final long timeNowMS = System.currentTimeMillis();
        if (event.getPacket() instanceof S3APacketTabComplete && sentAt != -1L)
        {
            final GuiPlayerInfo info = getPlayerInfo();
            if (info != null)
            {
                info.responseTime = (int) (timeNowMS - sentAt);
                sentAt = -1L;
            }
        }
    };

    @SuppressWarnings("unchecked")
    private GuiPlayerInfo getPlayerInfo()
    {
        return (GuiPlayerInfo) MC.thePlayer.sendQueue.playerInfoMap.getOrDefault(
                MC.thePlayer.getCommandSenderName(), null);
    }
}

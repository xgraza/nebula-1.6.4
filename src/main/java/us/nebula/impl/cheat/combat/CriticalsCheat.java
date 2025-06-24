package us.nebula.impl.cheat.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import us.nebula.api.listener.EventListener;
import us.nebula.api.listener.Subscribe;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.api.value.Setting;
import us.nebula.impl.event.network.EventPacket;
import us.nebula.impl.gui.client.component.cheat.value.EnumSettingComponent;

/**
 * @author xgraza
 * @since 03/06/25
 */
@CheatManifest(name = "Criticals",
        description = "Automatically applies critical hits",
        category = CheatCategory.COMBAT)
public final class CriticalsCheat extends Cheat
{
    private final Setting<Mode> modeSetting = new Setting<>(
            "Mode", Mode.MOTION);

    @Subscribe
    private final EventListener<EventPacket.Outbound> outboundEventListener = event ->
    {
        if (event.getPacket() instanceof C02PacketUseEntity)
        {
            final C02PacketUseEntity packet = event.getPacket();
            if (!packet.func_149565_c().equals(C02PacketUseEntity.Action.ATTACK))
            {
                return;
            }
            final Entity entity = packet.func_149564_a(MC.theWorld);
            if (!(entity instanceof EntityLivingBase))
            {
                return;
            }

            if (!MC.thePlayer.onGround
                    || MC.thePlayer.isOnLadder()
                    || MC.thePlayer.isInWater()
                    || MC.thePlayer.isInWeb)
            {
                return;
            }

            switch (modeSetting.getValue())
            {
                case MOTION:
                {
                    MC.thePlayer.motionX *= 1.2;
                    MC.thePlayer.motionZ *= 1.2;
                    MC.thePlayer.motionY = 0.1;
                    break;
                }
                case PACKET:
                {
                    MC.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(
                            MC.thePlayer.posX,
                            MC.thePlayer.boundingBox.minY + 0.1,
                            MC.thePlayer.posY + 0.100000004768371,
                            MC.thePlayer.posZ,
                            false));
                    MC.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(
                            MC.thePlayer.posX,
                            MC.thePlayer.boundingBox.minY,
                            MC.thePlayer.posY,
                            MC.thePlayer.posZ,
                            false));
                    break;
                }
            }
        }
    };

    @Override
    public String getMetadata()
    {
        return EnumSettingComponent.formatEnum(modeSetting.getValue());
    }

    private enum Mode
    {
        MOTION, PACKET
    }
}

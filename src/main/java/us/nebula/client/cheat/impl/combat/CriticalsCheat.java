/*
 * Copyright (c) xgraza 2025
 */

package us.nebula.client.cheat.impl.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;
import us.nebula.client.listener.EventListener;
import us.nebula.client.listener.Subscribe;
import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.util.value.Setting;
import us.nebula.client.cheat.impl.movement.SpeedCheat;
import us.nebula.client.cheat.impl.world.FakePlayerCheat;
import us.nebula.client.listener.event.network.EventPacket;
import us.nebula.client.listener.event.player.EventMoveUpdate;
import us.nebula.client.cheat.gui.component.cheat.value.EnumSettingComponent;
import us.nebula.client.util.math.Timer;

/**
 * @author xgraza
 * @since 03/06/25
 */
@CheatManifest(name = "Criticals",
        description = "Automatically makes attacks critical hits",
        category = CheatCategory.COMBAT)
public final class CriticalsCheat extends Cheat
{
    private final Setting<Mode> modeSetting = new Setting<>(
            "Mode", Mode.MOTION);
    private final Setting<Boolean> efficentSetting = new Setting<>(
            "Efficient", false);
    private final Setting<Double> delaySetting = new Setting<>(
            "Delay", 0.5, 0.0, 5.0, 0.5)
            .setVisibility(() -> !efficentSetting.getValue());
    private final Setting<Boolean> pauseWithLagbackSetting = new Setting<>(
            "Pause with Lagback", true);

    private final Timer lagbackTimer = new Timer();
    private final Timer timer = new Timer();
    private int modifyStage = -1;

    @Override
    public void onDisable()
    {
        super.onDisable();
        modifyStage = -1;
    }

    @Subscribe
    private final EventListener<EventMoveUpdate> moveUpdateEventListener = event ->
    {
        if (modifyStage == -1)
        {
            return;
        }

        if (!MC.thePlayer.onGround || SpeedCheat.INSTANCE.isActive())
        {
            event.setY(MC.thePlayer.boundingBox.minY);
            event.setStance(MC.thePlayer.posY);
            event.setOnGround(MC.thePlayer.onGround);

            modifyStage = -1;
            timer.resetTime();
            return;
        }
        event.setOnGround(false);
        switch (modifyStage)
        {
            case 0:
            {
                event.setY(event.getY() + 0.1);
                event.setStance(event.getStance() + 0.100000004768371);
                break;
            }
            case 1:
            case 2:
            {
                break;
            }
            case 3:
            {
                event.setOnGround(true);
                modifyStage = -1;
                timer.resetTime();
                //FakePlayerCheat.INSTANCE.critFake();
                return;
            }
        }
        ++modifyStage;
    };

    @Subscribe
    private final EventListener<EventPacket.Inbound> inboundEventListener = event ->
    {
        if (event.getPacket() instanceof S08PacketPlayerPosLook)
        {
            lagbackTimer.resetTime();
        }
    };

    @Subscribe
    private final EventListener<EventPacket.Outbound> outboundEventListener = event ->
    {
        if (event.getPacket() instanceof C02PacketUseEntity)
        {
            final C02PacketUseEntity packet = event.getPacket();
            if (!packet.getAction().equals(C02PacketUseEntity.Action.ATTACK))
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
                    || MC.thePlayer.isInWeb
                    || MC.thePlayer.isPotionActive(Potion.blindness))
            {
                return;
            }

            if (efficentSetting.getValue())
            {
                final EntityLivingBase living = (EntityLivingBase) entity;
                if (living.hurtResistantTime < living.maxHurtResistantTime / 2.0f)
                {
                    return;
                }
            } else
            {
                if (!timer.hasElapsed((long) (delaySetting.getValue() * 1000.0)))
                {
                    return;
                }
                timer.resetTime();
            }

            if (!lagbackTimer.hasElapsed(20) && pauseWithLagbackSetting.getValue())
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
                    FakePlayerCheat.INSTANCE.critFake();
                    break;
                }
                case PACKET_2:
                {
                    if (modifyStage == -1)
                    {
                        modifyStage = 0;
                    }
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
        MOTION, PACKET, PACKET_2
    }
}

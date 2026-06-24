/*
 * Copyright (c) xgraza 2025
 */

package ez.nebula.client.impl.module.combat;

import ez.nebula.client.api.listener.IEventPriorities;
import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.impl.module.movement.SpeedModule;
import ez.nebula.client.impl.module.world.FakePlayerModule;
import ez.nebula.client.util.minecraft.player.PlayerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;
import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.listener.event.network.EventPacket;
import ez.nebula.client.api.listener.event.player.EventMoveUpdate;
import ez.nebula.client.impl.gui.module.component.module.value.EnumSettingComponent;
import ez.nebula.client.api.setting.Setting;
import ez.nebula.client.util.math.Timer;

/**
 * @author xgraza
 * @since 03/06/25
 */
@ModuleManifest(name = "Criticals",
        description = "Automatically makes attacks critical hits",
        category = ModuleCategory.COMBAT)
public final class CriticalsModule extends Module
{
    private final Setting<Mode> modeSetting = enumBuilder("Mode", Mode.MOTION)
            .setDescription("How critical hits should be handled")
            .build();
    private final Setting<Boolean> efficentSetting = builder("Efficient", false)
            .setDescription("If to only try to critical hit when it will do the most damage")
            .build();
    private final Setting<Double> delaySetting = numberBuilder("Delay", 0.5)
            .setMin(0.0)
            .setMax(5.0)
            .setScale(0.5)
            .setDescription("How long to wait before attacking")
            .setVisibility((value) -> !efficentSetting.getValue())
            .build();
    private final Setting<Boolean> pauseWithLagbackSetting = builder("Pause with Lagback", true)
            .setDescription("If to prevent trying to make critical hits after the AntiCheat lags you back")
            .build();

    private final Timer lagbackTimer = new Timer();
    private final Timer timer = new Timer();
    private int modifyStage = -1;

    @Override
    public void onDisable()
    {
        super.onDisable();
        modifyStage = -1;
    }

    @Subscribe(priority = IEventPriorities.MEDIUM)
    private final EventListener<EventMoveUpdate> moveUpdateEventListener = event ->
    {
        if (modifyStage == -1)
        {
            return;
        }

        if (!MC.thePlayer.onGround || SpeedModule.INSTANCE.isActive())
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
                FakePlayerModule.INSTANCE.critFake();
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
                    || MC.thePlayer.isPotionActive(Potion.blindness)
                    || PlayerUtil.isAboveWater())
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
                    MC.thePlayer.sendQueue.getNetworkManager().sendPacketInstantly(
                            new C03PacketPlayer.C04PacketPlayerPosition(
                                MC.thePlayer.posX,
                                MC.thePlayer.boundingBox.minY + 0.1,
                                MC.thePlayer.posY + 0.100000004768371,
                                MC.thePlayer.posZ,
                                false));
                    MC.thePlayer.sendQueue.getNetworkManager().sendPacketInstantly(
                            new C03PacketPlayer.C04PacketPlayerPosition(
                                    MC.thePlayer.posX,
                                    MC.thePlayer.boundingBox.minY,
                                    MC.thePlayer.posY,
                                    MC.thePlayer.posZ,
                                    false));
                    FakePlayerModule.INSTANCE.critFake();
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

/*
 * Copyright (c) xgraza 2025
 */

package us.nebula.impl.cheat.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import us.nebula.api.listener.EventListener;
import us.nebula.api.listener.Subscribe;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.api.value.Setting;
import us.nebula.impl.event.network.EventPacket;
import us.nebula.impl.event.player.EventMoveUpdate;
import us.nebula.impl.gui.client.component.cheat.value.EnumSettingComponent;
import us.nebula.util.player.ChatUtil;

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
    private final Setting<Boolean> efficentSetting = new Setting<>(
            "Efficient", false);

    private boolean crit;
    private int modifyStage = -1;

    @Override
    protected void onDisable()
    {
        super.onDisable();
        crit = false;
        modifyStage = -1;
    }

    @Subscribe
    private final EventListener<EventMoveUpdate> moveUpdateEventListener = event ->
    {
        if (modifyStage == -1 || !MC.thePlayer.onGround)
        {
            modifyStage = -1;
            return;
        }
        ChatUtil.send("Crit Stage: " + modifyStage);
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
                return;
            }
        }
        ++modifyStage;
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

            if (efficentSetting.getValue())
            {
                final EntityLivingBase living = (EntityLivingBase) entity;
                if (living.hurtResistantTime < living.maxHurtResistantTime / 2.0f)
                {
                    return;
                }
            }

            if (!MC.thePlayer.onGround
                    || MC.thePlayer.isOnLadder()
                    || MC.thePlayer.isInWater()
                    || MC.thePlayer.isInWeb
                    || MC.thePlayer.isPotionActive(Potion.blindness))
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
                    if (crit)
                    {
                        return;
                    }
                    crit = true;
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
                    crit = false;
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

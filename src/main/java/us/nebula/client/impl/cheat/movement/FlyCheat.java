package us.nebula.client.impl.cheat.movement;

import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.world.WorldSettings;
import us.nebula.client.api.listener.EventListener;
import us.nebula.client.api.listener.Subscribe;
import us.nebula.client.api.manager.cheat.Cheat;
import us.nebula.client.api.manager.cheat.CheatCategory;
import us.nebula.client.api.manager.cheat.CheatManifest;
import us.nebula.client.api.value.Setting;
import us.nebula.client.impl.event.game.EventUpdate;
import us.nebula.client.impl.event.network.EventPacket;
import us.nebula.client.impl.event.player.EventMove;
import us.nebula.client.impl.gui.client.component.cheat.value.EnumSettingComponent;
import us.nebula.client.util.player.MoveUtil;

/**
 * @author xgraza
 * @since 3/14/26
 */
@CheatManifest(name = "Fly",
        description = "Buzz buzz, I'm flying to infinity and beyond!",
        category = CheatCategory.MOVEMENT)
public final class FlyCheat extends Cheat
{
    private final Setting<Mode> modeSetting = new Setting<>(
            "Mode", Mode.VANILLA);
    private final Setting<Double> speedSetting = new Setting<>(
            "Speed", 1.0, 0.1, 7.0, 0.05);
    private final Setting<Boolean> antiKickSetting = new Setting<>(
            "Anti-Kick", false);
    private final Setting<Boolean> doubleTapSpaceSetting = new Setting<>(
            "Double Tap Space", false)
            .setVisibility(() -> modeSetting.getValue() == Mode.CREATIVE);

    @Override
    public void onDisable()
    {
        super.onDisable();
        if (MC.thePlayer != null && MC.thePlayer.capabilities != null)
        {
            MC.thePlayer.capabilities.isFlying = false;
            MC.thePlayer.capabilities.allowFlying = false;
            MC.thePlayer.capabilities.setFlySpeed(0.05f);
        }
    }

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        if (modeSetting.getValue() == Mode.CREATIVE)
        {
            MC.thePlayer.capabilities.allowFlying = true;
            MC.thePlayer.capabilities.setFlySpeed(speedSetting.getValue().floatValue() / 10.0f);
            if (!doubleTapSpaceSetting.getValue())
            {
                MC.thePlayer.capabilities.isFlying = true;
            }

            if (antiKickSetting.getValue())
            {
                MC.thePlayer.motionY = -0.0313;
            }
        }
    };

    @Subscribe
    private final EventListener<EventMove> moveEventListener = event ->
    {
        if (modeSetting.getValue() == Mode.VANILLA)
        {
            if (MoveUtil.isMoving())
            {
                MoveUtil.setSpeed(event, speedSetting.getValue());
            }

            if (MC.gameSettings.keyBindJump.pressed)
            {
                MC.thePlayer.motionY = speedSetting.getValue();
            } else if (MC.gameSettings.keyBindSneak.pressed)
            {
                MC.thePlayer.motionY = -speedSetting.getValue();
            } else
            {
                MC.thePlayer.motionY = antiKickSetting.getValue() ? -0.0313 : 0.0;
            }

            event.setY(MC.thePlayer.motionY);
        }
    };

    @Subscribe
    private final EventListener<EventPacket.Outbound> outboundEventListener = event ->
    {
        if (event.getPacket() instanceof C13PacketPlayerAbilities && modeSetting.getValue() == Mode.CREATIVE)
        {
            final C13PacketPlayerAbilities packet = event.getPacket();
            if (MC.playerController.currentGameType != WorldSettings.GameType.CREATIVE)
            {
                packet.setCreativeMode(false);
                packet.setFlying(false);
                packet.setAllowFlying(false);
            }
            packet.setFlySpeed(0.05f);
            packet.setWalkSpeed(0.1f);
        }
    };

    @Override
    public String getMetadata()
    {
        return EnumSettingComponent.formatEnum(modeSetting.getValue());
    }

    private enum Mode
    {
        VANILLA, CREATIVE
    }
}

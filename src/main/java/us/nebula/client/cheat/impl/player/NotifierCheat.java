package us.nebula.client.cheat.impl.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S0EPacketSpawnObject;
import net.minecraft.util.EnumChatFormatting;
import us.nebula.client.Nebula;
import us.nebula.client.listener.EventListener;
import us.nebula.client.listener.Subscribe;
import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.util.value.Setting;
import us.nebula.client.listener.event.game.EventUpdate;
import us.nebula.client.listener.event.network.EventPacket;
import us.nebula.client.listener.event.world.EventAddEntity;
import us.nebula.client.util.math.Timer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xgraza
 * @since 3/16/26
 */
@CheatManifest(name = "Notifier",
        description = "Notifies you of things happening in game around you",
        category = CheatCategory.PLAYER)
public final class NotifierCheat extends Cheat
{
    private static final int ENDER_PEARL_SPAWN_TYPE = 65;

    private final Setting<Boolean> visualRangeSetting = new Setting<>(
            "Visual Range", false);
    private final Setting<Boolean> takeDamageSetting = new Setting<>(
            "Take Damage", false);
    private final Setting<Boolean> pearlsSetting = new Setting<>(
            "Pearls", false);
    private final Setting<Double> pearlDelaySetting = new Setting<>(
            "Pearl Delay", 1.5, 0.0, 5.0, 0.05)
            .setVisibility(pearlsSetting::getValue);

    private final Map<Integer, Timer> playerPearlTimerMap = new ConcurrentHashMap<>();
    private float previousHealth;

    @Override
    public void onDisable()
    {
        super.onDisable();
        previousHealth = 0.0f;
        playerPearlTimerMap.clear();
    }

    @Subscribe
    private final EventListener<EventAddEntity> addEntityEventListener = event ->
    {
        if (event.getEntity() instanceof EntityPlayer
                && visualRangeSetting.getValue()
                && !event.isPreviousExisting())
        {
            final EntityPlayer player = (EntityPlayer) event.getEntity();
            if (Nebula.INSTANCE.getFriendManager().isFriend(player))
            {
                return;
            }
            notify(String.format("Player %s entered your visual range", player.getCommandSenderName()));
        }
    };

    @Subscribe
    private final EventListener<EventPacket.Inbound> inboundEventListener = event ->
    {
        if (event.getPacket() instanceof S0EPacketSpawnObject && pearlsSetting.getValue())
        {
            final S0EPacketSpawnObject packet = event.getPacket();
            if (packet.getType() != ENDER_PEARL_SPAWN_TYPE)
            {
                return;
            }

            double x = packet.getX() / 32.0;
            double y = packet.getY() / 32.0;
            double z = packet.getZ() / 32.0;

            // find closest entity to pearl
            final EntityPlayer thrownByPlayer = MC.theWorld.getClosestPlayer(x, y, z, -1);

            if (thrownByPlayer != null)
            {
                if (pearlDelaySetting.getValue() > 0.0)
                {
                    final Timer timer = playerPearlTimerMap.computeIfAbsent(
                            thrownByPlayer.getEntityId(),
                            (__) -> new Timer());
                    if (!timer.hasElapsed((long) (pearlDelaySetting.getValue() * 1000.0)))
                    {
                        return;
                    }
                    timer.resetTime();
                }

                notify(String.format("A pearl was thrown by %s at XYZ: %.1f, %.1f, %.1f",
                        thrownByPlayer.getCommandSenderName(), x, y, z));
            } else
            {
                notify(String.format("A pearl was thrown at XYZ: %.1f, %.1f, %.1f", x, y, z));
            }
        }
    };

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        if (previousHealth == 0.0f)
        {
            previousHealth = MC.thePlayer.getHealth();
        }
        if (takeDamageSetting.getValue() && previousHealth > MC.thePlayer.getHealth() && !MC.inGameHasFocus)
        {
            previousHealth = MC.thePlayer.getHealth();
            notify(String.format("You have taken damage! Health: %.1f", MC.thePlayer.getHealth()));
        }
    };

    private void notify(final String text)
    {
        if (!MC.inGameHasFocus && Nebula.INSTANCE.getSystemTray().isActive())
        {
            Nebula.INSTANCE.getSystemTray().notify(
                    EnumChatFormatting.getTextWithoutFormattingCodes(text));
        } else
        {
            notifyWarn(text, 7500L);
        }
    }
}

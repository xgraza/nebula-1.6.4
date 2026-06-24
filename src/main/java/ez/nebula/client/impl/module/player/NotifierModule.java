package ez.nebula.client.impl.module.player;

import ez.nebula.client.api.listener.event.world.EventChangeWorld;
import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.tray.SystemNotifications;
import ez.nebula.client.impl.module.world.FakePlayerModule;
import ez.nebula.client.util.minecraft.player.EntityUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S0EPacketSpawnObject;
import net.minecraft.util.EnumChatFormatting;
import ez.nebula.client.core.Nebula;
import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.listener.event.game.EventUpdate;
import ez.nebula.client.api.listener.event.network.EventPacket;
import ez.nebula.client.api.listener.event.world.EventAddEntity;
import ez.nebula.client.api.setting.Setting;
import ez.nebula.client.util.math.Timer;
import org.lwjgl.opengl.Display;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xgraza
 * @since 3/16/26
 */
@ModuleManifest(name = "Notifier",
        description = "Notifies you of things happening in game around you",
        category = ModuleCategory.PLAYER)
public final class NotifierModule extends Module
{
    private static final int ENDER_PEARL_SPAWN_TYPE = 65;

    private final Setting<Boolean> visualRangeSetting = builder("Visual Range", false)
            .setDescription("If to notify you when another player enters your render distance")
            .build();
    private final Setting<Boolean> takeDamageSetting = builder("Take Damage", false)
            .setDescription("If to notify you when you have taken damage")
            .build();
    private final Setting<Boolean> pearlsSetting = builder("Pearls", false)
            .setDescription("If to notify you when a pearl is thrown")
            .build();
    private final Setting<Double> pearlDelaySetting = numberBuilder("Pearl Delay", 1.5)
            .setMin(0.0)
            .setMax(5.0)
            .setScale(0.05)
            .setDescription("How long in seconds before notifying about another pearl thrown from the same player")
            .setVisibility((value) -> pearlsSetting.getValue())
            .build();

    private final Map<Integer, Timer> playerPearlTimerMap = new ConcurrentHashMap<>();
    private float previousHealth = -1.0f;

    @Override
    public void onDisable()
    {
        super.onDisable();
        previousHealth = -1.0f;
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
            if (Nebula.INSTANCE.getFriendManager().isFriend(player)
                    || player.getEntityId() == FreecamModule.CAMERA_ENTITY_ID
                    || player.getEntityId() == FakePlayerModule.INSTANCE.getFakePlayerEntityID())
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
                    final Timer timer = playerPearlTimerMap.get(thrownByPlayer.getEntityId());
                    if (timer != null && !timer.hasElapsed((long) (pearlDelaySetting.getValue() * 1000.0)))
                    {
                        return;
                    }
                    playerPearlTimerMap.computeIfAbsent(
                            thrownByPlayer.getEntityId(), (__) -> new Timer());
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
        if (MC.thePlayer.getHealth() > 0.0f)
        {
            final float playerHealth = EntityUtil.getHealth(MC.thePlayer);
            if (takeDamageSetting.getValue() && previousHealth != -1.0f && previousHealth > playerHealth && !Display.isActive())
            {
                notify(String.format("You have taken damage! Health: %.1f", playerHealth));
            }
            previousHealth = playerHealth;
        }
    };

    @Subscribe
    private final EventListener<EventChangeWorld> changeWorldEventListener = event ->
    {
        playerPearlTimerMap.clear();
        previousHealth = -1.0f;
    };

    private void notify(final String text)
    {
        if (!Display.isActive())
        {
            SystemNotifications.info("Nebula Notifier", EnumChatFormatting.getTextWithoutFormattingCodes(text));
        } else
        {
            notifyWarn(text, 7500L);
        }
    }
}

package us.nebula.client.impl.cheat.player;

import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.Display;
import us.nebula.client.Nebula;
import us.nebula.client.api.listener.EventListener;
import us.nebula.client.api.listener.Subscribe;
import us.nebula.client.api.manager.cheat.Cheat;
import us.nebula.client.api.manager.cheat.CheatCategory;
import us.nebula.client.api.manager.cheat.CheatManifest;
import us.nebula.client.api.value.Setting;
import us.nebula.client.impl.event.game.EventUpdate;
import us.nebula.client.util.player.ChatUtil;

/**
 * @author xgraza
 * @since 3/16/26
 */
@CheatManifest(name = "Notifier",
        description = "Notifies you of things happening in game around you",
        category = CheatCategory.PLAYER)
public final class NotifierCheat extends Cheat
{
    private final Setting<Boolean> visualRangeSetting = new Setting<>(
            "Visual Range", false);
    private final Setting<Boolean> takeDamageSetting = new Setting<>(
            "Take Damage", false);
    private final Setting<Boolean> pearlsSetting = new Setting<>(
            "Pearls", false);

    private float previousHealth;

    @Override
    protected void onDisable()
    {
        super.onDisable();
        previousHealth = 0.0f;
    }

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        if (takeDamageSetting.getValue() && previousHealth > MC.thePlayer.getHealth() && !Display.isActive())
        {
            previousHealth = MC.thePlayer.getHealth();
            notify(String.format("You have taken damage! Health: %.1f", MC.thePlayer.getHealth()));
        }
    };

    private void notify(final String text)
    {
        if (!Display.isActive() && Nebula.INSTANCE.getSystemTray().isActive())
        {
            Nebula.INSTANCE.getSystemTray().notify(
                    EnumChatFormatting.getTextWithoutFormattingCodes(text));
        } else
        {
            ChatUtil.send(text);
        }
    }
}

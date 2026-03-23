package us.nebula.client.impl.cheat.movement;

import org.lwjgl.input.Keyboard;
import us.nebula.client.api.listener.EventListener;
import us.nebula.client.api.listener.Subscribe;
import us.nebula.client.api.manager.cheat.Cheat;
import us.nebula.client.api.manager.cheat.CheatCategory;
import us.nebula.client.api.manager.cheat.CheatManifest;
import us.nebula.client.api.value.Setting;
import us.nebula.client.impl.event.game.EventUpdate;
import us.nebula.client.impl.event.player.EventSprint;

/**
 * @author xgraza
 * @since 02/14/25
 */
@CheatManifest(name = "Sprint",
        description = "Force holds the sprint key for you",
        category = CheatCategory.MOVEMENT)
public final class SprintCheat extends Cheat
{
    final Setting<Boolean> omniSprintSetting = new Setting<>("Omni-Sprint", false);

    @Override
    public void onDisable()
    {
        super.onDisable();
        if (!Keyboard.isKeyDown(MC.gameSettings.keyBindSprint.getKeyCode()))
        {
            MC.gameSettings.keyBindSprint.setPressed(false);
        }
    }

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
            MC.gameSettings.keyBindSprint.setPressed(true);

    @Subscribe
    private final EventListener<EventSprint> sprintEventListener = event ->
    {
        if (omniSprintSetting.getValue() && MC.thePlayer.movementInput.moveForward != 0.0f)
        {
            event.setCanceled(true);
        }
    };
}

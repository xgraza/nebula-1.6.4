package ez.nebula.client.impl.module.movement;

import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.listener.event.game.EventUpdate;
import ez.nebula.client.api.listener.event.player.EventSprint;
import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.setting.Setting;
import org.lwjgl.input.Keyboard;

/**
 * @author xgraza
 * @since 02/14/25
 */
@ModuleManifest(name = "Sprint",
        description = "Force holds the sprint key for you",
        category = ModuleCategory.MOVEMENT)
public final class SprintModule extends Module
{
    private final Setting<Boolean> omniSprintSetting = builder("Omni-Sprint", false)
            .setDescription("If to allow full-speed sprint in all directions")
            .build();

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

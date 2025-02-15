package us.nebula.impl.cheat.movement;

import org.lwjgl.input.Keyboard;
import us.nebula.api.listener.EventListener;
import us.nebula.api.listener.Subscribe;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.impl.event.game.EventUpdate;

/**
 * @author xgraza
 * @since 02/14/25
 */
@CheatManifest(name = "Sprint", category = CheatCategory.MOVEMENT)
public final class SprintCheat extends Cheat
{
    @Override
    protected void onDisable()
    {
        super.onDisable();
        if (!Keyboard.isKeyDown(MC.gameSettings.keyBindSprint.getKeyCode()))
        {
            MC.gameSettings.keyBindSprint.setPressed(false);
        }
    }

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        MC.gameSettings.keyBindSprint.setPressed(true);
    };
}

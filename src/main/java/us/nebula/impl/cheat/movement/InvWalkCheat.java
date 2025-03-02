package us.nebula.impl.cheat.movement;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import us.nebula.api.listener.EventListener;
import us.nebula.api.listener.Subscribe;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.impl.event.game.EventUpdate;

import java.util.List;

/**
 * @author xgraza
 * @since 03/01/25
 */
@SuppressWarnings("unchecked")
@CheatManifest(name = "InvWalk",
        description = "Allows you to freely walk in GUIs",
        category = CheatCategory.MOVEMENT)
public final class InvWalkCheat extends Cheat
{
    // preset GUI screens that require keyboard input that may be annoying to use with inv walk
    private static final List<Class<? extends GuiScreen>> SCREEN_BLACKLIST = Lists.newArrayList(
            GuiChat.class,
            GuiContainerCreative.class,
            GuiCommandBlock.class,
            GuiEditSign.class,
            GuiScreenBook.class,
            GuiRepair.class);

    private final KeyBinding[] moveKeyBindings = new KeyBinding[6];

    @Override
    protected void onEnable()
    {
        super.onEnable();
        moveKeyBindings[0] = MC.gameSettings.keyBindForward;
        moveKeyBindings[1] = MC.gameSettings.keyBindBack;
        moveKeyBindings[2] = MC.gameSettings.keyBindRight;
        moveKeyBindings[3] = MC.gameSettings.keyBindLeft;
        moveKeyBindings[4] = MC.gameSettings.keyBindJump;
        moveKeyBindings[5] = MC.gameSettings.keyBindSprint;
    }

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        if (MC.currentScreen == null || isScreenBlacklisted())
        {
            return;
        }
        MC.currentScreen.allowUserInput = true;
        for (final KeyBinding keyBinding : moveKeyBindings)
        {
            final int keyCode = keyBinding.getKeyCode();
            boolean keyState;
            if (keyCode < -100)
            {
                keyState = Mouse.isButtonDown(keyCode - 100);
            } else
            {
                keyState = Keyboard.isKeyDown(keyCode);
            }
            keyBinding.pressed = keyState;
        }
    };

    private boolean isScreenBlacklisted()
    {
        return MC.currentScreen != null && SCREEN_BLACKLIST.contains(MC.currentScreen.getClass());
    }
}

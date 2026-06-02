package ez.nebula.client.impl.module.movement;

import com.google.common.collect.Lists;
import ez.nebula.client.api.manager.module.Module;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.listener.event.game.EventUpdate;
import ez.nebula.client.impl.gui.hud.HUDEditorScreen;
import ez.nebula.client.api.setting.Setting;

import java.util.List;

/**
 * @author xgraza
 * @since 03/01/25
 */
@SuppressWarnings("unchecked")
@ModuleManifest(name = "InvWalk",
        description = "Allows you to freely walk in GUIs",
        category = ModuleCategory.MOVEMENT)
public final class InvWalkModule extends Module
{
    // preset GUI screens that require keyboard input that may be annoying to use with inv walk
    private static final List<Class<? extends GuiScreen>> SCREEN_BLACKLIST = Lists.newArrayList(
            GuiChat.class,
            HUDEditorScreen.class,
            GuiContainerCreative.class,
            GuiCommandBlock.class,
            GuiEditSign.class,
            GuiScreenBook.class,
            GuiRepair.class);

    private final KeyBinding[] moveKeyBindings = new KeyBinding[6];

    private final Setting<Boolean> allowBlacklistedGUIsSetting = builder("Allow Blacklisted GUIs", false)
            .setDescription("If to allow movement in all GUIs regardless of their issues")
            .build();

    @Override
    public void onEnable()
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
        return MC.currentScreen != null
                && SCREEN_BLACKLIST.contains(MC.currentScreen.getClass())
                && !allowBlacklistedGUIsSetting.getValue();
    }
}

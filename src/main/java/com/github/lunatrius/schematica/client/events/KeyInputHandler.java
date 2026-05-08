package com.github.lunatrius.schematica.client.events;

import com.github.lunatrius.schematica.client.gui.GuiSchematicControl;
import com.github.lunatrius.schematica.client.gui.GuiSchematicLoad;
import com.github.lunatrius.schematica.client.gui.GuiSchematicSave;
import com.github.lunatrius.schematica.lib.Strings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.input.Keyboard;
import us.nebula.client.listener.EventListener;
import us.nebula.client.listener.Subscribe;
import us.nebula.client.listener.event.input.EventKey;

public class KeyInputHandler
{
    public static final KeyBinding KEY_BINDING_LOAD = new KeyBinding(Strings.KEY_LOAD, Keyboard.KEY_DIVIDE, Strings.KEY_CATEGORY);
    public static final KeyBinding KEY_BINDING_SAVE = new KeyBinding(Strings.KEY_SAVE, Keyboard.KEY_MULTIPLY, Strings.KEY_CATEGORY);
    public static final KeyBinding KEY_BINDING_CONTROL = new KeyBinding(Strings.KEY_CONTROL, Keyboard.KEY_SUBTRACT, Strings.KEY_CATEGORY);

    public static final KeyBinding[] KEY_BINDINGS = new KeyBinding[]{
            KEY_BINDING_LOAD, KEY_BINDING_SAVE, KEY_BINDING_CONTROL
    };

    public static void initKeyBindings(final GameSettings settings)
    {
        settings.keyBindLoadSchematic = KEY_BINDING_LOAD;
        settings.keyBindSaveSchematic = KEY_BINDING_SAVE;
        settings.keyBindControlSchematic = KEY_BINDING_CONTROL;
        // merge new key bindings
        settings.keyBindings = ArrayUtils.addAll(settings.keyBindings,
                KEY_BINDING_LOAD, KEY_BINDING_SAVE, KEY_BINDING_CONTROL);
    }

    private final Minecraft minecraft = Minecraft.getMinecraft();

    @Subscribe
    private final EventListener<EventKey> keyEventListener = event ->
    {
        if (minecraft.currentScreen != null)
        {
            return;
        }
        for (KeyBinding keyBinding : KEY_BINDINGS)
        {
            if (keyBinding.getIsKeyPressed())
            {
                GuiScreen guiScreen = null;
                if (keyBinding == KEY_BINDING_LOAD)
                {
                    guiScreen = new GuiSchematicLoad(null);
                } else if (keyBinding == KEY_BINDING_SAVE)
                {
                    guiScreen = new GuiSchematicSave(null);
                } else if (keyBinding == KEY_BINDING_CONTROL)
                {
                    guiScreen = new GuiSchematicControl(null);
                }

                if (guiScreen != null)
                {
                    this.minecraft.displayGuiScreen(guiScreen);
                }
            }
        }
    };

//	@SubscribeEvent
//	public void keyInput(KeyInputEvent event) {
//		for (KeyBinding keyBinding : KEY_BINDINGS) {
//			if (keyBinding.isPressed()) {
//				if (this.minecraft.currentScreen == null) {
//					GuiScreen guiScreen = null;
//					if (keyBinding == KEY_BINDING_LOAD) {
//						guiScreen = new GuiSchematicLoad(this.minecraft.currentScreen);
//					} else if (keyBinding == KEY_BINDING_SAVE) {
//						guiScreen = new GuiSchematicSave(this.minecraft.currentScreen);
//					} else if (keyBinding == KEY_BINDING_CONTROL) {
//						guiScreen = new GuiSchematicControl(this.minecraft.currentScreen);
//					}
//
//					if (guiScreen != null) {
//						this.minecraft.displayGuiScreen(guiScreen);
//					}
//				}
//			}
//		}
//	}
}

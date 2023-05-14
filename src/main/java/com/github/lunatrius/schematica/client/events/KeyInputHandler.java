//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Gavin\Documents\schematica\mappings"!

//Decompiled by Procyon!

package com.github.lunatrius.schematica.client.events;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.input.EventKeyInput;
import net.minecraft.client.settings.*;
import net.minecraft.client.*;
import com.github.lunatrius.schematica.client.gui.*;
import net.minecraft.client.gui.*;

public class KeyInputHandler
{
    private static KeyBinding KEY_BINDING_LOAD;
    private static KeyBinding KEY_BINDING_SAVE;
    private static KeyBinding KEY_BINDING_CONTROL;
    public static KeyBinding[] KEY_BINDINGS;
    private final Minecraft minecraft;
    
    public KeyInputHandler() {
        this.minecraft = Minecraft.getMinecraft();
    }
    
//    @SubscribeEvent
//    public void keyInput(final InputEvent.KeyInputEvent event) {
    @Listener
    public void keyInput(final EventKeyInput event) {
        int keyCode = event.getKeyCode();

        if (this.minecraft.currentScreen == null) {
            GuiScreen guiScreen = null;
            if (keyCode == KeyInputHandler.KEY_BINDING_LOAD.getKeyCode()) {
                guiScreen = new GuiSchematicLoad(this.minecraft.currentScreen);
            } else if (keyCode == KeyInputHandler.KEY_BINDING_SAVE.getKeyCode()) {
                guiScreen = new GuiSchematicSave(this.minecraft.currentScreen);
            } else if (keyCode == KeyInputHandler.KEY_BINDING_CONTROL.getKeyCode()) {
                guiScreen = new GuiSchematicControl(this.minecraft.currentScreen);
            }
            if (guiScreen != null) {
                this.minecraft.displayGuiScreen(guiScreen);
            }
        }
    }
    
    static {
        KEY_BINDING_LOAD = new KeyBinding("schematica.key.load", 181, "schematica.key.category");
        KEY_BINDING_SAVE = new KeyBinding("schematica.key.save", 55, "schematica.key.category");
        KEY_BINDING_CONTROL = new KeyBinding("schematica.key.control", 74, "schematica.key.category");
        KEY_BINDINGS = new KeyBinding[] { KeyInputHandler.KEY_BINDING_LOAD, KeyInputHandler.KEY_BINDING_SAVE, KeyInputHandler.KEY_BINDING_CONTROL };
    }
}

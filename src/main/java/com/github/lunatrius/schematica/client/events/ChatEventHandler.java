//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Gavin\Documents\schematica\mappings"!

//Decompiled by Procyon!

package com.github.lunatrius.schematica.client.events;

import com.github.lunatrius.schematica.lib.*;
import com.github.lunatrius.schematica.*;
import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.net.EventPacket;
import net.minecraft.network.play.server.S02PacketChat;

public class ChatEventHandler
{
    private final Settings settings;
    
    public ChatEventHandler() {
        this.settings = Settings.instance;
    }
    
//    @SubscribeEvent
//    public void onClientChatReceivedEvent(final ClientChatReceivedEvent event) {
    @Listener
    public void onClientChatReceivedEvent(final EventPacket.Inbound event) {
        if (!(event.getPacket() instanceof S02PacketChat)) return;

        final Settings settings = this.settings;
        ++settings.chatLines;
        if (this.settings.chatLines < 10) {
            final String message = ((S02PacketChat) event.getPacket()).func_148915_c().getFormattedText();
            if (message.contains("�0�2�0�0�e�f")) {
                Reference.logger.info("Printer is disabled on this server.");
                SchematicPrinter.INSTANCE.setEnabled(false);
            }
            if (message.contains("�0�2�1�0�e�f")) {
                Reference.logger.info("Saving is disabled on this server.");
                this.settings.isSaveEnabled = false;
            }
            if (message.contains("�0�2�1�1�e�f")) {
                Reference.logger.info("Loading is disabled on this server.");
                this.settings.isLoadEnabled = false;
            }
        }
    }
}

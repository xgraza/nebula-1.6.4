//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Gavin\Documents\schematica\mappings"!

//Decompiled by Procyon!

package com.github.lunatrius.schematica.client.events;

import com.github.lunatrius.schematica.SchematicPrinter;
import com.github.lunatrius.schematica.Schematica;
import com.github.lunatrius.schematica.Settings;
import com.github.lunatrius.schematica.client.renderer.RendererSchematicChunk;
import com.github.lunatrius.schematica.lib.Reference;
import com.github.lunatrius.schematica.world.SchematicWorld;
import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.entity.EventUpdate;
import lol.nebula.listener.events.world.EventServerChange;
import lol.nebula.listener.events.world.EventServerDisconnect;
import lol.nebula.listener.events.world.EventTick;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.AxisAlignedBB;

public class TickHandler
{
    private final Minecraft minecraft;
    private int ticks;
    private final WorldRenderer[] sortedWorldRenderers;
    
    public TickHandler() {
        this.minecraft = Minecraft.getMinecraft();
        this.ticks = -1;
        this.sortedWorldRenderers = minecraft.renderGlobal.sortedWorldRenderers;
    }
    
//    @SubscrilbeEvent
//    public void clientConnect(final FMLNetworkEvent.ClientConnectedToServerEvent event) {
    @Listener
    public void clientConnect(final EventServerChange event) {
        Reference.logger.info("Scheduling client settings reset.");
        Settings.instance.isPendingReset = true;
    }
    
//    @SubscribeEvent
//    public void clientDisconnect(final FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
    @Listener
    public void clientDisconnect(final EventServerDisconnect event) {
        Reference.logger.info("Scheduling client settings reset.");
        Settings.instance.isPendingReset = true;
    }
    
//    @SubscribeEvent
//    public void clientTick(final TickEvent.ClientTickEvent event) {
    @Listener
    public void clientTick(final EventTick event) {
        this.minecraft.mcProfiler.startSection("schematica");
        final SchematicWorld schematic = Schematica.proxy.getActiveSchematic();
        if (this.minecraft.thePlayer != null && schematic != null && schematic.isRendering()) {
            this.minecraft.mcProfiler.startSection("printer");
            final SchematicPrinter printer = SchematicPrinter.INSTANCE;
            if (printer.isEnabled() && printer.isPrinting() && this.ticks-- < 0) {
                this.ticks = Reference.config.placeDelay;
                printer.print();
            }
            this.minecraft.mcProfiler.endStartSection("checkDirty");
            this.checkDirty();
            this.minecraft.mcProfiler.endStartSection("canUpdate");
            RendererSchematicChunk.setCanUpdate(true);
            this.minecraft.mcProfiler.endSection();
        }
        if (Settings.instance.isPendingReset) {
            Settings.instance.reset();
            Settings.instance.isPendingReset = false;
        }
        this.minecraft.mcProfiler.endSection();
    }
    
    private void checkDirty() {
        if (this.sortedWorldRenderers != null) {
            try {
                final WorldRenderer[] renderers = this.sortedWorldRenderers;
                if (renderers != null) {
                    int count = 0;
                    for (final WorldRenderer worldRenderer : renderers) {
                        if (worldRenderer != null && worldRenderer.needsUpdate && count++ < 125) {
                            final AxisAlignedBB worldRendererBoundingBox = worldRenderer.rendererBoundingBox.getOffsetBoundingBox((double)(-Settings.instance.offset.x), (double)(-Settings.instance.offset.y), (double)(-Settings.instance.offset.z));
                            for (final RendererSchematicChunk renderer : Settings.instance.sortedRendererSchematicChunk) {
                                if (!renderer.getDirty() && renderer.getBoundingBox().intersectsWith(worldRendererBoundingBox)) {
                                    renderer.setDirty();
                                }
                            }
                        }
                    }
                }
            }
            catch (Exception e) {
                Reference.logger.error("Dirty check failed!", (Throwable)e);
            }
        }
    }
}

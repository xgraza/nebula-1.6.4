//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Gavin\Documents\schematica\mappings"!

//Decompiled by Procyon!

package com.github.lunatrius.schematica.client.renderer;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.render.world.EventRender3D;
import net.minecraft.client.*;
import net.minecraft.profiler.*;
import net.minecraft.client.renderer.culling.*;
import net.minecraft.util.*;
import net.minecraft.entity.player.*;
import com.github.lunatrius.schematica.*;
import net.minecraft.client.entity.*;
import com.github.lunatrius.schematica.world.*;
import org.lwjgl.opengl.*;
import com.github.lunatrius.core.util.vector.*;
import java.util.*;

public class RendererSchematicGlobal
{
    private final Minecraft minecraft;
    private final Settings settings;
    private final Profiler profiler;
    private final Frustrum frustrum;
    private final RendererSchematicChunkSorter rendererSchematicChunkSorter;
    
    public RendererSchematicGlobal() {
        this.minecraft = Minecraft.getMinecraft();
        this.settings = Settings.instance;
        this.profiler = this.minecraft.mcProfiler;
        this.frustrum = new Frustrum();
        this.rendererSchematicChunkSorter = new RendererSchematicChunkSorter();
    }
    
//    @SubscribeEvent
//    public void onRender(final RenderWorldLastEvent event) {
    @Listener
    public void onRender(final EventRender3D event) {
        final EntityPlayerSP player = (EntityPlayerSP)this.minecraft.thePlayer;
        if (player != null) {
            this.settings.playerPosition.x = (float)(player.lastTickPosX + (player.posX - player.lastTickPosX) * event.getPartialTicks());
            this.settings.playerPosition.y = (float)(player.lastTickPosY + (player.posY - player.lastTickPosY) * event.getPartialTicks());
            this.settings.playerPosition.z = (float)(player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.getPartialTicks());
            this.settings.rotationRender = (MathHelper.floor_double((double)(player.rotationYaw / 90.0f)) & 0x3);
            this.settings.orientation = this.getOrientation((EntityPlayer)player);
            this.profiler.startSection("schematica");
            final SchematicWorld schematic = Schematica.proxy.getActiveSchematic();
            if ((schematic != null && schematic.isRendering()) || this.settings.isRenderingGuide) {
                this.render(schematic);
            }
            this.profiler.endSection();
        }
    }
    
    private EnumFacing getOrientation(final EntityPlayer player) {
        if (player.rotationPitch > 45.0f) {
            return EnumFacing.DOWN;
        }
        if (player.rotationPitch < -45.0f) {
            return EnumFacing.UP;
        }
        switch (MathHelper.floor_double(player.rotationYaw / 90.0 + 0.5) & 0x3) {
            case 0: {
                return EnumFacing.SOUTH;
            }
            case 1: {
                return EnumFacing.WEST;
            }
            case 2: {
                return EnumFacing.NORTH;
            }
            case 3: {
                return EnumFacing.EAST;
            }
            default: {
                return null;
            }
        }
    }
    
    public void render(final SchematicWorld schematic) {
        GL11.glPushMatrix();
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3042);
        GL11.glTranslatef(-this.settings.getTranslationX(), -this.settings.getTranslationY(), -this.settings.getTranslationZ());
        this.profiler.startSection("schematic");
        if (schematic != null && schematic.isRendering()) {
            this.profiler.startSection("updateFrustrum");
            this.updateFrustrum();
            this.profiler.endStartSection("sortAndUpdate");
            if (RendererSchematicChunk.getCanUpdate()) {
                this.sortAndUpdate();
            }
            this.profiler.endStartSection("render");
            for (int pass = 0; pass < 3; ++pass) {
                for (final RendererSchematicChunk renderer : this.settings.sortedRendererSchematicChunk) {
                    renderer.render(pass);
                }
            }
            this.profiler.endSection();
        }
        this.profiler.endStartSection("guide");
        RenderHelper.createBuffers();
        this.profiler.startSection("dataPrep");
        if (schematic != null && schematic.isRendering()) {
            RenderHelper.drawCuboidOutline(RenderHelper.VEC_ZERO, Schematica.proxy.getActiveSchematic().dimensions(), 63, 0.75f, 0.0f, 0.75f, 0.25f);
        }
        if (this.settings.isRenderingGuide) {
            Vector3f start = this.settings.pointMin.clone().sub(this.settings.offset);
            Vector3f end = this.settings.pointMax.clone().sub(this.settings.offset);
            end.add(1.0f, 1.0f, 1.0f);
            RenderHelper.drawCuboidOutline(start, end, 63, 0.0f, 0.75f, 0.0f, 0.25f);
            start = this.settings.pointA.clone().sub(this.settings.offset);
            end = start.clone().add(1.0f, 1.0f, 1.0f);
            RenderHelper.drawCuboidOutline(start, end, 63, 0.75f, 0.0f, 0.0f, 0.25f);
            RenderHelper.drawCuboidSurface(start, end, 63, 0.75f, 0.0f, 0.0f, 0.25f);
            start = this.settings.pointB.clone().sub(this.settings.offset);
            end = start.clone().add(1.0f, 1.0f, 1.0f);
            RenderHelper.drawCuboidOutline(start, end, 63, 0.0f, 0.0f, 0.75f, 0.25f);
            RenderHelper.drawCuboidSurface(start, end, 63, 0.0f, 0.0f, 0.75f, 0.25f);
        }
        final int quadCount = RenderHelper.getQuadCount();
        final int lineCount = RenderHelper.getLineCount();
        if (quadCount > 0 || lineCount > 0) {
            GL11.glDisable(3553);
            GL11.glLineWidth(1.5f);
            GL11.glEnableClientState(32884);
            GL11.glEnableClientState(32886);
            this.profiler.endStartSection("quad");
            if (quadCount > 0) {
                GL11.glVertexPointer(3, 0, RenderHelper.getQuadVertexBuffer());
                GL11.glColorPointer(4, 0, RenderHelper.getQuadColorBuffer());
                GL11.glDrawArrays(7, 0, quadCount);
            }
            this.profiler.endStartSection("line");
            if (lineCount > 0) {
                GL11.glVertexPointer(3, 0, RenderHelper.getLineVertexBuffer());
                GL11.glColorPointer(4, 0, RenderHelper.getLineColorBuffer());
                GL11.glDrawArrays(1, 0, lineCount);
            }
            this.profiler.endSection();
            GL11.glDisableClientState(32886);
            GL11.glDisableClientState(32884);
            GL11.glEnable(3553);
        }
        this.profiler.endSection();
        GL11.glDisable(3042);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glPopMatrix();
    }
    
    private void updateFrustrum() {
        this.frustrum.setPosition((double)this.settings.getTranslationX(), (double)this.settings.getTranslationY(), (double)this.settings.getTranslationZ());
        for (final RendererSchematicChunk rendererSchematicChunk : this.settings.sortedRendererSchematicChunk) {
            rendererSchematicChunk.isInFrustrum = this.frustrum.isBoundingBoxInFrustum(rendererSchematicChunk.getBoundingBox());
        }
    }
    
    private void sortAndUpdate() {
        Collections.sort(this.settings.sortedRendererSchematicChunk, (Comparator<? super RendererSchematicChunk>)this.rendererSchematicChunkSorter);
        for (final RendererSchematicChunk rendererSchematicChunk : this.settings.sortedRendererSchematicChunk) {
            if (rendererSchematicChunk.getDirty()) {
                rendererSchematicChunk.updateRenderer();
                break;
            }
        }
    }
}

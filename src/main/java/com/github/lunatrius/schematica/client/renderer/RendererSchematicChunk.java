//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Gavin\Documents\schematica\mappings"!

//Decompiled by Procyon!

package com.github.lunatrius.schematica.client.renderer;

import com.github.lunatrius.core.util.vector.*;
import com.github.lunatrius.schematica.*;
import net.minecraft.client.*;
import net.minecraft.profiler.*;
import com.github.lunatrius.schematica.world.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import java.lang.reflect.*;
import org.lwjgl.opengl.*;
import net.minecraft.client.renderer.texture.*;
import com.github.lunatrius.schematica.lib.*;
import net.minecraft.world.*;
import net.minecraft.init.*;
import net.minecraft.block.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.tileentity.*;
import java.util.*;

public class RendererSchematicChunk
{
    public static final int CHUNK_WIDTH = 16;
    public static final int CHUNK_HEIGHT = 16;
    public static final int CHUNK_LENGTH = 16;
    private static boolean canUpdate;
    public boolean isInFrustrum;
    public final Vector3f centerPosition;
    private final Settings settings;
    private final Minecraft minecraft;
    private final Profiler profiler;
    private final SchematicWorld schematic;
    private final List<TileEntity> tileEntities;
    private final AxisAlignedBB boundingBox;
    private static final Map<String, ResourceLocation> resourcePacks;
    private Field fieldMapTexturesStiched;
    private boolean needsUpdate;
    private int glList;
    
    public RendererSchematicChunk(final SchematicWorld schematicWorld, final int baseX, final int baseY, final int baseZ) {
        this.isInFrustrum = false;
        this.centerPosition = new Vector3f();
        this.settings = Settings.instance;
        this.minecraft = this.settings.minecraft;
        this.profiler = this.minecraft.mcProfiler;
        this.tileEntities = new ArrayList<TileEntity>();
        this.boundingBox = AxisAlignedBB.getBoundingBox(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
        this.needsUpdate = true;
        this.glList = -1;
        this.schematic = schematicWorld;
        this.boundingBox.setBounds((double)(baseX * 16), (double)(baseY * 16), (double)(baseZ * 16), (double)((baseX + 1) * 16), (double)((baseY + 1) * 16), (double)((baseZ + 1) * 16));
        this.centerPosition.x = (float)(int)((baseX + 0.5) * 16.0);
        this.centerPosition.y = (float)(int)((baseY + 0.5) * 16.0);
        this.centerPosition.z = (float)(int)((baseZ + 0.5) * 16.0);
        for (final TileEntity tileEntity : this.schematic.getTileEntities()) {
            final int x = tileEntity.xCoord;
            final int y = tileEntity.yCoord;
            final int z = tileEntity.zCoord;
            if (x >= this.boundingBox.minX) {
                if (x >= this.boundingBox.maxX) {
                    continue;
                }
                if (z < this.boundingBox.minZ) {
                    continue;
                }
                if (z >= this.boundingBox.maxZ) {
                    continue;
                }
                if (y < this.boundingBox.minY) {
                    continue;
                }
                if (y >= this.boundingBox.maxY) {
                    continue;
                }
                this.tileEntities.add(tileEntity);
            }
        }
        this.glList = GL11.glGenLists(3);
//        try {
//            this.fieldMapTexturesStiched = //ReflectionHelper.findField((Class)TextureMap.class, new String[] { "f", "mapUploadedSprites", "mapUploadedSprites" });
//        }
//        catch (Exception ex) {
//            Reference.logger.fatal("Failed to initialize mapTexturesStiched!", (Throwable)ex);
//            this.fieldMapTexturesStiched = null;
//        }
    }
    
    public void delete() {
        if (this.glList != -1) {
            GL11.glDeleteLists(this.glList, 3);
        }
    }
    
    public AxisAlignedBB getBoundingBox() {
        return this.boundingBox;
    }
    
    public static void setCanUpdate(final boolean parCanUpdate) {
        RendererSchematicChunk.canUpdate = parCanUpdate;
    }
    
    public static boolean getCanUpdate() {
        return RendererSchematicChunk.canUpdate;
    }
    
    public void setDirty() {
        this.needsUpdate = true;
    }
    
    public boolean getDirty() {
        return this.needsUpdate;
    }
    
    public float distanceToPoint(final Vector3f vector) {
        final float x = vector.x - this.centerPosition.x;
        final float y = vector.y - this.centerPosition.y;
        final float z = vector.z - this.centerPosition.z;
        return x * x + y * y + z * z;
    }
    
    public void updateRenderer() {
        if (this.needsUpdate) {
            setCanUpdate(this.needsUpdate = false);
            RenderHelper.createBuffers();
            for (int pass = 0; pass < 3; ++pass) {
                RenderHelper.initBuffers();
                final int minX = (int)this.boundingBox.minX;
                final int maxX = Math.min((int)this.boundingBox.maxX, this.schematic.getWidth());
                int minY = (int)this.boundingBox.minY;
                int maxY = Math.min((int)this.boundingBox.maxY, this.schematic.getHeight());
                final int minZ = (int)this.boundingBox.minZ;
                final int maxZ = Math.min((int)this.boundingBox.maxZ, this.schematic.getLength());
                final int renderingLayer = this.schematic.getRenderingLayer();
                if (renderingLayer >= 0) {
                    if (renderingLayer >= minY && renderingLayer < maxY) {
                        minY = renderingLayer;
                        maxY = renderingLayer + 1;
                    }
                    else {
                        maxY = (minY = 0);
                    }
                }
                GL11.glNewList(this.glList + pass, 4864);
                this.renderBlocks(pass, minX, minY, minZ, maxX, maxY, maxZ);
                final int quadCount = RenderHelper.getQuadCount();
                final int lineCount = RenderHelper.getLineCount();
                if (quadCount > 0 || lineCount > 0) {
                    GL11.glDisable(3553);
                    GL11.glLineWidth(1.5f);
                    GL11.glEnableClientState(32884);
                    GL11.glEnableClientState(32886);
                    if (quadCount > 0) {
                        GL11.glVertexPointer(3, 0, RenderHelper.getQuadVertexBuffer());
                        GL11.glColorPointer(4, 0, RenderHelper.getQuadColorBuffer());
                        GL11.glDrawArrays(7, 0, quadCount);
                    }
                    if (lineCount > 0) {
                        GL11.glVertexPointer(3, 0, RenderHelper.getLineVertexBuffer());
                        GL11.glColorPointer(4, 0, RenderHelper.getLineColorBuffer());
                        GL11.glDrawArrays(1, 0, lineCount);
                    }
                    GL11.glDisableClientState(32886);
                    GL11.glDisableClientState(32884);
                    GL11.glEnable(3553);
                }
                GL11.glEndList();
            }
            RenderHelper.destroyBuffers();
        }
    }
    
    public void render(final int renderPass) {
        if (!this.isInFrustrum) {
            return;
        }
        if (this.distanceToPoint(this.settings.getTranslationVector()) > 25600.0f) {
            return;
        }
        GL11.glDisable(2896);
        this.profiler.startSection("blocks");
        this.bindTexture();
        GL11.glCallList(this.glList + renderPass);
        this.profiler.endStartSection("tileEntities");
        this.renderTileEntities(renderPass);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glAlphaFunc(516, 0.1f);
        this.profiler.endSection();
    }
    
    public void renderBlocks(final int renderPass, final int minX, final int minY, final int minZ, final int maxX, final int maxY, final int maxZ) {
        final IBlockAccess mcWorld = (IBlockAccess)this.minecraft.theWorld;
        final RenderBlocks renderBlocks = this.settings.renderBlocks;
        final Vector3f zero = new Vector3f();
        final Vector3f size = new Vector3f();
        final int ambientOcclusion = this.minecraft.gameSettings.ambientOcclusion;
        this.minecraft.gameSettings.ambientOcclusion = 0;
        Tessellator.instance.startDrawingQuads();
        for (int y = minY; y < maxY; ++y) {
            for (int z = minZ; z < maxZ; ++z) {
                for (int x = minX; x < maxX; ++x) {
                    try {
                        final Block block = this.schematic.getBlock(x, y, z);
                        final int wx = (int)this.settings.offset.x + x;
                        final int wy = (int)this.settings.offset.y + y;
                        final int wz = (int)this.settings.offset.z + z;
                        final Block mcBlock = mcWorld.getBlock(wx, wy, wz);
                        int sides = 0;
                        if (block != null) {
                            if (block.shouldSideBeRendered((IBlockAccess)this.schematic, x, y - 1, z, 0)) {
                                sides |= 0x1;
                            }
                            if (block.shouldSideBeRendered((IBlockAccess)this.schematic, x, y + 1, z, 1)) {
                                sides |= 0x2;
                            }
                            if (block.shouldSideBeRendered((IBlockAccess)this.schematic, x, y, z - 1, 2)) {
                                sides |= 0x4;
                            }
                            if (block.shouldSideBeRendered((IBlockAccess)this.schematic, x, y, z + 1, 3)) {
                                sides |= 0x8;
                            }
                            if (block.shouldSideBeRendered((IBlockAccess)this.schematic, x - 1, y, z, 4)) {
                                sides |= 0x10;
                            }
                            if (block.shouldSideBeRendered((IBlockAccess)this.schematic, x + 1, y, z, 5)) {
                                sides |= 0x20;
                            }
                        }
                        final boolean isAirBlock = mcWorld.isAirBlock(wx, wy, wz);
                        if (!isAirBlock) {
                            if (Reference.config.highlight && renderPass == 2) {
                                if (block == Blocks.air && Reference.config.highlightAir) {
                                    zero.set((float)x, (float)y, (float)z);
                                    size.set((float)(x + 1), (float)(y + 1), (float)(z + 1));
                                    if (Reference.config.drawQuads) {
                                        RenderHelper.drawCuboidSurface(zero, size, 63, 0.75f, 0.0f, 0.75f, 0.25f);
                                    }
                                    if (Reference.config.drawLines) {
                                        RenderHelper.drawCuboidOutline(zero, size, 63, 0.75f, 0.0f, 0.75f, 0.25f);
                                    }
                                }
                                else if (block != mcBlock) {
                                    zero.set((float)x, (float)y, (float)z);
                                    size.set((float)(x + 1), (float)(y + 1), (float)(z + 1));
                                    if (Reference.config.drawQuads) {
                                        RenderHelper.drawCuboidSurface(zero, size, sides, 1.0f, 0.0f, 0.0f, 0.25f);
                                    }
                                    if (Reference.config.drawLines) {
                                        RenderHelper.drawCuboidOutline(zero, size, sides, 1.0f, 0.0f, 0.0f, 0.25f);
                                    }
                                }
                                else if (this.schematic.getBlockMetadata(x, y, z) != mcWorld.getBlockMetadata(wx, wy, wz)) {
                                    zero.set((float)x, (float)y, (float)z);
                                    size.set((float)(x + 1), (float)(y + 1), (float)(z + 1));
                                    if (Reference.config.drawQuads) {
                                        RenderHelper.drawCuboidSurface(zero, size, sides, 0.75f, 0.35f, 0.0f, 0.25f);
                                    }
                                    if (Reference.config.drawLines) {
                                        RenderHelper.drawCuboidOutline(zero, size, sides, 0.75f, 0.35f, 0.0f, 0.25f);
                                    }
                                }
                            }
                        }
                        else if (block != Blocks.air) {
                            if (Reference.config.highlight && renderPass == 2) {
                                zero.set((float)x, (float)y, (float)z);
                                size.set((float)(x + 1), (float)(y + 1), (float)(z + 1));
                                if (Reference.config.drawQuads) {
                                    RenderHelper.drawCuboidSurface(zero, size, sides, 0.0f, 0.75f, 1.0f, 0.25f);
                                }
                                if (Reference.config.drawLines) {
                                    RenderHelper.drawCuboidOutline(zero, size, sides, 0.0f, 0.75f, 1.0f, 0.25f);
                                }
                            }
                            if (block != null && block.getRenderBlockPass() == renderPass /*block.canRenderInPass(renderPass)*/) {
                                renderBlocks.renderBlockByRenderType(block, x, y, z);
                            }
                        }
                    }
                    catch (Exception e) {
                        Reference.logger.error("Failed to render block!", (Throwable)e);
                    }
                }
            }
        }
        Tessellator.instance.draw();
        this.minecraft.gameSettings.ambientOcclusion = ambientOcclusion;
    }
    
    public void renderTileEntities(final int renderPass) {
        if (renderPass != 0) {
            return;
        }
        final IBlockAccess mcWorld = (IBlockAccess)this.minecraft.theWorld;
        GL11.glColor4f(1.0f, 1.0f, 1.0f, Reference.config.alpha);
        try {
            for (final TileEntity tileEntity : this.tileEntities) {
                final int x = tileEntity.xCoord;
                final int y = tileEntity.yCoord;
                final int z = tileEntity.zCoord;
                final int renderingLayer = this.schematic.getRenderingLayer();
                if (renderingLayer >= 0 && renderingLayer != y) {
                    continue;
                }
                final Block mcBlock = mcWorld.getBlock(x + (int)this.settings.offset.x, y + (int)this.settings.offset.y, z + (int)this.settings.offset.z);
                if (mcBlock != Blocks.air) {
                    continue;
                }
                final TileEntitySpecialRenderer tileEntitySpecialRenderer = TileEntityRendererDispatcher.instance.getSpecialRenderer(tileEntity);
                if (tileEntitySpecialRenderer == null) {
                    continue;
                }
                try {
                    tileEntitySpecialRenderer.renderTileEntityAt(tileEntity, (double)x, (double)y, (double)z, 0.0f);
                    OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
                    GL11.glDisable(3553);
                    OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
                }
                catch (Exception e) {
                    Reference.logger.error("Failed to render a tile entity!", (Throwable)e);
                }
                GL11.glColor4f(1.0f, 1.0f, 1.0f, Reference.config.alpha);
            }
        }
        catch (Exception ex) {
            Reference.logger.error("Failed to render tile entities!", (Throwable)ex);
        }
    }
    
    private void bindTexture() {
        if (!Reference.config.enableAlpha) {
            this.minecraft.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            return;
        }
        this.minecraft.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
    }
    
    static {
        RendererSchematicChunk.canUpdate = false;
        resourcePacks = new HashMap<String, ResourceLocation>();
    }
}

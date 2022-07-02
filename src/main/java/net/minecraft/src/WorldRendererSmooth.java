package net.minecraft.src;

import java.util.HashSet;
import java.util.List;
import org.lwjgl.opengl.GL11;

public class WorldRendererSmooth extends WorldRenderer
{
    private WrUpdateState updateState = new WrUpdateState();
    public int activeSet = 0;
    public int[] activeListIndex = new int[] {0, 0};
    public int[][][] glWorkLists = new int[2][2][16];
    public boolean[] tempSkipRenderPass = new boolean[2];

    public WorldRendererSmooth(World par1World, List par2List, int par3, int par4, int par5, int par6)
    {
        super(par1World, par2List, par3, par4, par5, par6);
        int glWorkBase = 393216 + 64 * (this.glRenderList / 3);

        for (int set = 0; set < 2; ++set)
        {
            int setBase = glWorkBase + set * 2 * 16;

            for (int pass = 0; pass < 2; ++pass)
            {
                int passBase = setBase + pass * 16;

                for (int t = 0; t < 16; ++t)
                {
                    this.glWorkLists[set][pass][t] = passBase + t;
                }
            }
        }
    }

    /**
     * Sets a new position for the renderer and setting it up so it can be reloaded with the new data for that position
     */
    public void setPosition(int px, int py, int pz)
    {
        if (this.isUpdating)
        {
            this.updateRenderer();
        }

        super.setPosition(px, py, pz);
    }

    /**
     * Will update this chunk renderer
     */
    public void updateRenderer()
    {
        if (this.worldObj != null)
        {
            this.updateRenderer(0L);
            this.finishUpdate();
        }
    }

    public boolean updateRenderer(long finishTime)
    {
        if (this.worldObj == null)
        {
            return true;
        }
        else
        {
            this.needsUpdate = false;

            if (!this.isUpdating)
            {
                if (this.needsBoxUpdate)
                {
                    float xMin = 0.0F;
                    GL11.glNewList(this.glRenderList + 2, GL11.GL_COMPILE);
                    RenderItem.renderAABB(AxisAlignedBB.getAABBPool().getAABB((double)((float)this.posXClip - xMin), (double)((float)this.posYClip - xMin), (double)((float)this.posZClip - xMin), (double)((float)(this.posXClip + 16) + xMin), (double)((float)(this.posYClip + 16) + xMin), (double)((float)(this.posZClip + 16) + xMin)));
                    GL11.glEndList();
                    this.needsBoxUpdate = false;
                }

                if (Reflector.LightCache.exists())
                {
                    Object var25 = Reflector.getFieldValue(Reflector.LightCache_cache);
                    Reflector.callVoid(var25, Reflector.LightCache_clear, new Object[0]);
                    Reflector.callVoid(Reflector.BlockCoord_resetPool, new Object[0]);
                }

                Chunk.isLit = false;
            }

            int var26 = this.posX;
            int yMin = this.posY;
            int zMin = this.posZ;
            int xMax = this.posX + 16;
            int yMax = this.posY + 16;
            int zMax = this.posZ + 16;
            ChunkCache chunkcache = null;
            RenderBlocks renderblocks = null;
            HashSet setOldEntityRenders = null;

            if (!this.isUpdating)
            {
                for (int setNewEntityRenderers = 0; setNewEntityRenderers < 2; ++setNewEntityRenderers)
                {
                    this.tempSkipRenderPass[setNewEntityRenderers] = true;
                }

                byte var27 = 1;
                chunkcache = new ChunkCache(this.worldObj, var26 - var27, yMin - var27, zMin - var27, xMax + var27, yMax + var27, zMax + var27, var27);
                renderblocks = new RenderBlocks(chunkcache);
                setOldEntityRenders = new HashSet();
                setOldEntityRenders.addAll(this.tileEntityRenderers);
                this.tileEntityRenderers.clear();
            }

            if (this.isUpdating || !chunkcache.extendedLevelsInChunkCache())
            {
                this.bytesDrawn = 0;
                Tessellator var28 = Tessellator.instance;
                boolean hasForge = Reflector.ForgeHooksClient.exists();

                for (int renderPass = 0; renderPass < 2; ++renderPass)
                {
                    boolean renderNextPass = false;
                    boolean hasRenderedBlocks = false;
                    boolean hasGlList = false;

                    for (int y = yMin; y < yMax; ++y)
                    {
                        if (this.isUpdating)
                        {
                            this.isUpdating = false;
                            chunkcache = this.updateState.chunkcache;
                            renderblocks = this.updateState.renderblocks;
                            setOldEntityRenders = this.updateState.setOldEntityRenders;
                            renderPass = this.updateState.renderPass;
                            y = this.updateState.y;
                            renderNextPass = this.updateState.flag;
                            hasRenderedBlocks = this.updateState.hasRenderedBlocks;
                            hasGlList = this.updateState.hasGlList;

                            if (hasGlList)
                            {
                                GL11.glNewList(this.glWorkLists[this.activeSet][renderPass][this.activeListIndex[renderPass]], GL11.GL_COMPILE);
                                var28.setRenderingChunk(true);
                                var28.startDrawingQuads();
                                var28.setTranslation((double)(-globalChunkOffsetX), 0.0D, (double)(-globalChunkOffsetZ));
                            }
                        }
                        else if (hasGlList && finishTime != 0L && System.nanoTime() - finishTime > 0L && this.activeListIndex[renderPass] < 15)
                        {
                            var28.draw();
                            GL11.glEndList();
                            var28.setRenderingChunk(false);
                            var28.setTranslation(0.0D, 0.0D, 0.0D);
                            ++this.activeListIndex[renderPass];
                            this.updateState.chunkcache = chunkcache;
                            this.updateState.renderblocks = renderblocks;
                            this.updateState.setOldEntityRenders = setOldEntityRenders;
                            this.updateState.renderPass = renderPass;
                            this.updateState.y = y;
                            this.updateState.flag = renderNextPass;
                            this.updateState.hasRenderedBlocks = hasRenderedBlocks;
                            this.updateState.hasGlList = hasGlList;
                            this.isUpdating = true;
                            return false;
                        }

                        for (int z = zMin; z < zMax; ++z)
                        {
                            for (int x = var26; x < xMax; ++x)
                            {
                                int i3 = chunkcache.getBlockId(x, y, z);

                                if (i3 > 0)
                                {
                                    if (!hasGlList)
                                    {
                                        hasGlList = true;
                                        GL11.glNewList(this.glWorkLists[this.activeSet][renderPass][this.activeListIndex[renderPass]], GL11.GL_COMPILE);
                                        var28.setRenderingChunk(true);
                                        var28.startDrawingQuads();
                                        var28.setTranslation((double)(-globalChunkOffsetX), 0.0D, (double)(-globalChunkOffsetZ));
                                    }

                                    Block block = Block.blocksList[i3];

                                    if (renderPass == 0 && block.hasTileEntity())
                                    {
                                        TileEntity blockPass = chunkcache.getBlockTileEntity(x, y, z);

                                        if (TileEntityRenderer.instance.hasSpecialRenderer(blockPass))
                                        {
                                            this.tileEntityRenderers.add(blockPass);
                                        }
                                    }

                                    int var30 = block.getRenderBlockPass();
                                    boolean canRender = true;

                                    if (var30 != renderPass)
                                    {
                                        renderNextPass = true;
                                        canRender = false;
                                    }

                                    if (hasForge)
                                    {
                                        canRender = Reflector.callBoolean(block, Reflector.ForgeBlock_canRenderInPass, new Object[] {Integer.valueOf(renderPass)});
                                    }

                                    if (canRender)
                                    {
                                        hasRenderedBlocks |= renderblocks.renderBlockByRenderType(block, x, y, z);
                                    }
                                }
                            }
                        }
                    }

                    if (hasGlList)
                    {
                        this.bytesDrawn += var28.draw();
                        GL11.glEndList();
                        var28.setRenderingChunk(false);
                        var28.setTranslation(0.0D, 0.0D, 0.0D);
                    }
                    else
                    {
                        hasRenderedBlocks = false;
                    }

                    if (hasRenderedBlocks)
                    {
                        this.tempSkipRenderPass[renderPass] = false;
                    }

                    if (!renderNextPass)
                    {
                        break;
                    }
                }
            }

            HashSet var29 = new HashSet();
            var29.addAll(this.tileEntityRenderers);
            var29.removeAll(setOldEntityRenders);
            this.tileEntities.addAll(var29);
            setOldEntityRenders.removeAll(this.tileEntityRenderers);
            this.tileEntities.removeAll(setOldEntityRenders);
            this.isChunkLit = Chunk.isLit;
            this.isInitialized = true;
            ++chunksUpdated;
            this.isVisible = true;
            this.isVisibleFromPosition = false;
            this.skipRenderPass[0] = this.tempSkipRenderPass[0];
            this.skipRenderPass[1] = this.tempSkipRenderPass[1];
            this.isUpdating = false;
            return true;
        }
    }

    public void finishUpdate()
    {
        int pass;
        int i;
        int list;

        for (pass = 0; pass < 2; ++pass)
        {
            if (!this.skipRenderPass[pass])
            {
                GL11.glNewList(this.glRenderList + pass, GL11.GL_COMPILE);

                for (i = 0; i <= this.activeListIndex[pass]; ++i)
                {
                    list = this.glWorkLists[this.activeSet][pass][i];
                    GL11.glCallList(list);
                }

                GL11.glEndList();
            }
        }

        if (this.activeSet == 0)
        {
            this.activeSet = 1;
        }
        else
        {
            this.activeSet = 0;
        }

        for (pass = 0; pass < 2; ++pass)
        {
            if (!this.skipRenderPass[pass])
            {
                for (i = 0; i <= this.activeListIndex[pass]; ++i)
                {
                    list = this.glWorkLists[this.activeSet][pass][i];
                    GL11.glNewList(list, GL11.GL_COMPILE);
                    GL11.glEndList();
                }
            }
        }

        for (pass = 0; pass < 2; ++pass)
        {
            this.activeListIndex[pass] = 0;
        }
    }
}

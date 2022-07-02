package net.minecraft.src;

import java.util.HashSet;
import java.util.List;
import org.lwjgl.opengl.GL11;

public class WorldRendererThreaded extends WorldRenderer
{
    private int glRenderListStable;
    private int glRenderListBoundingBox;

    public WorldRendererThreaded(World par1World, List par2List, int par3, int par4, int par5, int par6)
    {
        super(par1World, par2List, par3, par4, par5, par6);
        this.glRenderListStable = this.glRenderList + 393216;
        this.glRenderListBoundingBox = this.glRenderList + 2;
    }

    /**
     * Will update this chunk renderer
     */
    public void updateRenderer()
    {
        if (this.worldObj != null)
        {
            this.updateRenderer((IWrUpdateListener)null);
            this.finishUpdate();
        }
    }

    public void updateRenderer(IWrUpdateListener updateListener)
    {
        if (this.worldObj != null)
        {
            this.needsUpdate = false;
            int xMin = this.posX;
            int yMin = this.posY;
            int zMin = this.posZ;
            int xMax = this.posX + 16;
            int yMax = this.posY + 16;
            int zMax = this.posZ + 16;
            boolean[] tempSkipRenderPass = new boolean[2];

            for (int hashset = 0; hashset < tempSkipRenderPass.length; ++hashset)
            {
                tempSkipRenderPass[hashset] = true;
            }

            if (Reflector.LightCache.exists())
            {
                Object var27 = Reflector.getFieldValue(Reflector.LightCache_cache);
                Reflector.callVoid(var27, Reflector.LightCache_clear, new Object[0]);
                Reflector.callVoid(Reflector.BlockCoord_resetPool, new Object[0]);
            }

            Chunk.isLit = false;
            HashSet var28 = new HashSet();
            var28.addAll(this.tileEntityRenderers);
            this.tileEntityRenderers.clear();
            byte one = 1;
            ChunkCache chunkcache = new ChunkCache(this.worldObj, xMin - one, yMin - one, zMin - one, xMax + one, yMax + one, zMax + one, one);

            if (!chunkcache.extendedLevelsInChunkCache())
            {
                ++chunksUpdated;
                RenderBlocks hashset1 = new RenderBlocks(chunkcache);
                this.bytesDrawn = 0;
                Tessellator tessellator = Tessellator.instance;
                boolean hasForge = Reflector.ForgeHooksClient.exists();
                WrUpdateControl uc = new WrUpdateControl();

                for (int renderPass = 0; renderPass < 2; ++renderPass)
                {
                    uc.setRenderPass(renderPass);
                    boolean renderNextPass = false;
                    boolean hasRenderedBlocks = false;
                    boolean hasGlList = false;

                    for (int y = yMin; y < yMax; ++y)
                    {
                        if (hasRenderedBlocks && updateListener != null)
                        {
                            updateListener.updating(uc);
                        }

                        for (int z = zMin; z < zMax; ++z)
                        {
                            for (int x = xMin; x < xMax; ++x)
                            {
                                int i3 = chunkcache.getBlockId(x, y, z);

                                if (i3 > 0)
                                {
                                    if (!hasGlList)
                                    {
                                        hasGlList = true;
                                        GL11.glNewList(this.glRenderList + renderPass, GL11.GL_COMPILE);
                                        tessellator.setRenderingChunk(true);
                                        tessellator.startDrawingQuads();
                                        tessellator.setTranslation((double)(-globalChunkOffsetX), 0.0D, (double)(-globalChunkOffsetZ));
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

                                    int var31 = block.getRenderBlockPass();
                                    boolean canRender = true;

                                    if (var31 != renderPass)
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
                                        hasRenderedBlocks |= hashset1.renderBlockByRenderType(block, x, y, z);
                                    }
                                }
                            }
                        }
                    }

                    if (hasGlList)
                    {
                        if (updateListener != null)
                        {
                            updateListener.updating(uc);
                        }

                        this.bytesDrawn += tessellator.draw();
                        GL11.glEndList();
                        tessellator.setRenderingChunk(false);
                        tessellator.setTranslation(0.0D, 0.0D, 0.0D);
                    }
                    else
                    {
                        hasRenderedBlocks = false;
                    }

                    if (hasRenderedBlocks)
                    {
                        tempSkipRenderPass[renderPass] = false;
                    }

                    if (!renderNextPass)
                    {
                        break;
                    }
                }
            }

            for (int var29 = 0; var29 < 2; ++var29)
            {
                this.skipRenderPass[var29] = tempSkipRenderPass[var29];
            }

            HashSet var30 = new HashSet();
            var30.addAll(this.tileEntityRenderers);
            var30.removeAll(var28);
            this.tileEntities.addAll(var30);
            var28.removeAll(this.tileEntityRenderers);
            this.tileEntities.removeAll(var28);
            this.isChunkLit = Chunk.isLit;
            this.isInitialized = true;
            this.isVisible = true;
            this.isVisibleFromPosition = false;
        }
    }

    public void finishUpdate()
    {
        int temp = this.glRenderList;
        this.glRenderList = this.glRenderListStable;
        this.glRenderListStable = temp;

        for (int f = 0; f < 2; ++f)
        {
            if (!this.skipRenderPass[f])
            {
                GL11.glNewList(this.glRenderList + f, GL11.GL_COMPILE);
                GL11.glEndList();
            }
        }

        if (this.needsBoxUpdate && !this.skipAllRenderPasses())
        {
            float var3 = 0.0F;
            GL11.glNewList(this.glRenderListBoundingBox, GL11.GL_COMPILE);
            RenderItem.renderAABB(AxisAlignedBB.getAABBPool().getAABB((double)((float)this.posXClip - var3), (double)((float)this.posYClip - var3), (double)((float)this.posZClip - var3), (double)((float)(this.posXClip + 16) + var3), (double)((float)(this.posYClip + 16) + var3), (double)((float)(this.posZClip + 16) + var3)));
            GL11.glEndList();
            this.needsBoxUpdate = false;
        }
    }

    /**
     * Takes in the pass the call list is being requested for. Args: renderPass
     */
    public int getGLCallListForPass(int par1)
    {
        return !this.isInFrustum ? -1 : (!this.skipRenderPass[par1] ? this.glRenderListStable + par1 : -1);
    }

    /**
     * Renders the occlusion query GL List
     */
    public void callOcclusionQueryList()
    {
        GL11.glCallList(this.glRenderListBoundingBox);
    }
}

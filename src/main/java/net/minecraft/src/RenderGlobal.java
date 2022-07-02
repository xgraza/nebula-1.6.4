package net.minecraft.src;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ARBOcclusionQuery;
import org.lwjgl.opengl.GL11;

public class RenderGlobal implements IWorldAccess
{
    private static final ResourceLocation locationMoonPhasesPng = new ResourceLocation("textures/environment/moon_phases.png");
    private static final ResourceLocation locationSunPng = new ResourceLocation("textures/environment/sun.png");
    private static final ResourceLocation locationCloudsPng = new ResourceLocation("textures/environment/clouds.png");
    private static final ResourceLocation locationEndSkyPng = new ResourceLocation("textures/environment/end_sky.png");
    public List tileEntities = new ArrayList();
    public WorldClient theWorld;

    /** The RenderEngine instance used by RenderGlobal */
    public final TextureManager renderEngine;
    public CompactArrayList worldRenderersToUpdate = new CompactArrayList(100, 0.8F);
    private WorldRenderer[] sortedWorldRenderers;
    private WorldRenderer[] worldRenderers;
    private int renderChunksWide;
    private int renderChunksTall;
    private int renderChunksDeep;

    /** OpenGL render lists base */
    private int glRenderListBase;

    /** A reference to the Minecraft object. */
    public Minecraft mc;

    /** Global render blocks */
    public RenderBlocks globalRenderBlocks;

    /** OpenGL occlusion query base */
    private IntBuffer glOcclusionQueryBase;

    /** Is occlusion testing enabled */
    private boolean occlusionEnabled;

    /**
     * counts the cloud render updates. Used with mod to stagger some updates
     */
    private int cloudTickCounter;

    /** The star GL Call list */
    private int starGLCallList;

    /** OpenGL sky list */
    private int glSkyList;

    /** OpenGL sky list 2 */
    private int glSkyList2;

    /** Minimum block X */
    private int minBlockX;

    /** Minimum block Y */
    private int minBlockY;

    /** Minimum block Z */
    private int minBlockZ;

    /** Maximum block X */
    private int maxBlockX;

    /** Maximum block Y */
    private int maxBlockY;

    /** Maximum block Z */
    private int maxBlockZ;

    /**
     * Stores blocks currently being broken. Key is entity ID of the thing doing the breaking. Value is a
     * DestroyBlockProgress
     */
    public Map damagedBlocks = new HashMap();
    private Icon[] destroyBlockIcons;
    private int renderDistance = -1;

    /** Render entities startup counter (init value=2) */
    private int renderEntitiesStartupCounter = 2;

    /** Count entities total */
    private int countEntitiesTotal;

    /** Count entities rendered */
    private int countEntitiesRendered;

    /** Count entities hidden */
    private int countEntitiesHidden;

    /** Occlusion query result */
    IntBuffer occlusionResult = GLAllocation.createDirectIntBuffer(64);

    /** How many renderers are loaded this frame that try to be rendered */
    private int renderersLoaded;

    /** How many renderers are being clipped by the frustrum this frame */
    private int renderersBeingClipped;

    /** How many renderers are being occluded this frame */
    private int renderersBeingOccluded;

    /** How many renderers are actually being rendered this frame */
    private int renderersBeingRendered;

    /**
     * How many renderers are skipping rendering due to not having a render pass this frame
     */
    private int renderersSkippingRenderPass;

    /** Dummy render int */
    private int dummyRenderInt;

    /** World renderers check index */
    private int worldRenderersCheckIndex;
    private IntBuffer glListBuffer = BufferUtils.createIntBuffer(65536);

    /**
     * Previous x position when the renderers were sorted. (Once the distance moves more than 4 units they will be
     * resorted)
     */
    double prevSortX = -9999.0D;

    /**
     * Previous y position when the renderers were sorted. (Once the distance moves more than 4 units they will be
     * resorted)
     */
    double prevSortY = -9999.0D;

    /**
     * Previous Z position when the renderers were sorted. (Once the distance moves more than 4 units they will be
     * resorted)
     */
    double prevSortZ = -9999.0D;

    /**
     * The offset used to determine if a renderer is one of the sixteenth that are being updated this frame
     */
    int frustumCheckOffset;
    double prevReposX;
    double prevReposY;
    double prevReposZ;
    public Entity renderedEntity;
    private long lastMovedTime = System.currentTimeMillis();
    private long lastActionTime = System.currentTimeMillis();
    private static AxisAlignedBB AABB_INFINITE = AxisAlignedBB.getBoundingBox(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);

    public RenderGlobal(Minecraft par1Minecraft)
    {
        this.mc = par1Minecraft;
        this.renderEngine = par1Minecraft.getTextureManager();
        byte var2 = 65;
        byte var3 = 16;
        this.glRenderListBase = GLAllocation.generateDisplayLists(var2 * var2 * var3 * 3);
        this.occlusionEnabled = OpenGlCapsChecker.checkARBOcclusion();

        if (this.occlusionEnabled)
        {
            this.occlusionResult.clear();
            this.glOcclusionQueryBase = GLAllocation.createDirectIntBuffer(var2 * var2 * var3);
            this.glOcclusionQueryBase.clear();
            this.glOcclusionQueryBase.position(0);
            this.glOcclusionQueryBase.limit(var2 * var2 * var3);
            ARBOcclusionQuery.glGenQueriesARB(this.glOcclusionQueryBase);
        }

        this.starGLCallList = GLAllocation.generateDisplayLists(3);
        GL11.glPushMatrix();
        GL11.glNewList(this.starGLCallList, GL11.GL_COMPILE);
        this.renderStars();
        GL11.glEndList();
        GL11.glPopMatrix();
        Tessellator var4 = Tessellator.instance;
        this.glSkyList = this.starGLCallList + 1;
        GL11.glNewList(this.glSkyList, GL11.GL_COMPILE);
        byte var5 = 64;
        int var6 = 256 / var5 + 2;
        float var7 = 16.0F;
        int var8;
        int var9;

        for (var8 = -var5 * var6; var8 <= var5 * var6; var8 += var5)
        {
            for (var9 = -var5 * var6; var9 <= var5 * var6; var9 += var5)
            {
                var4.startDrawingQuads();
                var4.addVertex((double)(var8 + 0), (double)var7, (double)(var9 + 0));
                var4.addVertex((double)(var8 + var5), (double)var7, (double)(var9 + 0));
                var4.addVertex((double)(var8 + var5), (double)var7, (double)(var9 + var5));
                var4.addVertex((double)(var8 + 0), (double)var7, (double)(var9 + var5));
                var4.draw();
            }
        }

        GL11.glEndList();
        this.glSkyList2 = this.starGLCallList + 2;
        GL11.glNewList(this.glSkyList2, GL11.GL_COMPILE);
        var7 = -16.0F;
        var4.startDrawingQuads();

        for (var8 = -var5 * var6; var8 <= var5 * var6; var8 += var5)
        {
            for (var9 = -var5 * var6; var9 <= var5 * var6; var9 += var5)
            {
                var4.addVertex((double)(var8 + var5), (double)var7, (double)(var9 + 0));
                var4.addVertex((double)(var8 + 0), (double)var7, (double)(var9 + 0));
                var4.addVertex((double)(var8 + 0), (double)var7, (double)(var9 + var5));
                var4.addVertex((double)(var8 + var5), (double)var7, (double)(var9 + var5));
            }
        }

        var4.draw();
        GL11.glEndList();
    }

    private void renderStars()
    {
        Random var1 = new Random(10842L);
        Tessellator var2 = Tessellator.instance;
        var2.startDrawingQuads();

        for (int var3 = 0; var3 < 1500; ++var3)
        {
            double var4 = (double)(var1.nextFloat() * 2.0F - 1.0F);
            double var6 = (double)(var1.nextFloat() * 2.0F - 1.0F);
            double var8 = (double)(var1.nextFloat() * 2.0F - 1.0F);
            double var10 = (double)(0.15F + var1.nextFloat() * 0.1F);
            double var12 = var4 * var4 + var6 * var6 + var8 * var8;

            if (var12 < 1.0D && var12 > 0.01D)
            {
                var12 = 1.0D / Math.sqrt(var12);
                var4 *= var12;
                var6 *= var12;
                var8 *= var12;
                double var14 = var4 * 100.0D;
                double var16 = var6 * 100.0D;
                double var18 = var8 * 100.0D;
                double var20 = Math.atan2(var4, var8);
                double var22 = Math.sin(var20);
                double var24 = Math.cos(var20);
                double var26 = Math.atan2(Math.sqrt(var4 * var4 + var8 * var8), var6);
                double var28 = Math.sin(var26);
                double var30 = Math.cos(var26);
                double var32 = var1.nextDouble() * Math.PI * 2.0D;
                double var34 = Math.sin(var32);
                double var36 = Math.cos(var32);

                for (int var38 = 0; var38 < 4; ++var38)
                {
                    double var39 = 0.0D;
                    double var41 = (double)((var38 & 2) - 1) * var10;
                    double var43 = (double)((var38 + 1 & 2) - 1) * var10;
                    double var45 = var41 * var36 - var43 * var34;
                    double var47 = var43 * var36 + var41 * var34;
                    double var49 = var45 * var28 + var39 * var30;
                    double var51 = var39 * var28 - var45 * var30;
                    double var53 = var51 * var22 - var47 * var24;
                    double var55 = var47 * var22 + var51 * var24;
                    var2.addVertex(var14 + var53, var16 + var49, var18 + var55);
                }
            }
        }

        var2.draw();
    }

    /**
     * set null to clear
     */
    public void setWorldAndLoadRenderers(WorldClient par1WorldClient)
    {
        if (this.theWorld != null)
        {
            this.theWorld.removeWorldAccess(this);
        }

        this.prevSortX = -9999.0D;
        this.prevSortY = -9999.0D;
        this.prevSortZ = -9999.0D;
        RenderManager.instance.set(par1WorldClient);
        this.theWorld = par1WorldClient;
        this.globalRenderBlocks = new RenderBlocks(par1WorldClient);

        if (par1WorldClient != null)
        {
            par1WorldClient.addWorldAccess(this);
            this.loadRenderers();
        }
    }

    /**
     * Loads all the renderers and sets up the basic settings usage
     */
    public void loadRenderers()
    {
        if (this.theWorld != null)
        {
            Block.leaves.setGraphicsLevel(Config.isTreesFancy());
            this.renderDistance = this.mc.gameSettings.renderDistance;
            int var1;

            if (this.worldRenderers != null)
            {
                for (var1 = 0; var1 < this.worldRenderers.length; ++var1)
                {
                    this.worldRenderers[var1].stopRendering();
                }
            }

            var1 = 64 << 3 - this.renderDistance;
            short var2 = 512;
            var1 = 2 * this.mc.gameSettings.ofRenderDistanceFine;

            if (Config.isLoadChunksFar() && var1 < var2)
            {
                var1 = var2;
            }

            var1 += Config.getPreloadedChunks() * 2 * 16;
            short var3 = 400;

            if (this.mc.gameSettings.ofRenderDistanceFine > 256)
            {
                var3 = 1024;
            }

            if (var1 > var3)
            {
                var1 = var3;
            }

            this.prevReposX = -9999.0D;
            this.prevReposY = -9999.0D;
            this.prevReposZ = -9999.0D;
            this.renderChunksWide = var1 / 16 + 1;
            this.renderChunksTall = 16;
            this.renderChunksDeep = var1 / 16 + 1;
            this.worldRenderers = new WorldRenderer[this.renderChunksWide * this.renderChunksTall * this.renderChunksDeep];
            this.sortedWorldRenderers = new WorldRenderer[this.renderChunksWide * this.renderChunksTall * this.renderChunksDeep];
            int var4 = 0;
            int var5 = 0;
            this.minBlockX = 0;
            this.minBlockY = 0;
            this.minBlockZ = 0;
            this.maxBlockX = this.renderChunksWide;
            this.maxBlockY = this.renderChunksTall;
            this.maxBlockZ = this.renderChunksDeep;
            int var7;

            for (var7 = 0; var7 < this.worldRenderersToUpdate.size(); ++var7)
            {
                WorldRenderer var8 = (WorldRenderer)this.worldRenderersToUpdate.get(var7);

                if (var8 != null)
                {
                    var8.needsUpdate = false;
                }
            }

            this.worldRenderersToUpdate.clear();
            this.tileEntities.clear();

            for (var7 = 0; var7 < this.renderChunksWide; ++var7)
            {
                for (int var11 = 0; var11 < this.renderChunksTall; ++var11)
                {
                    for (int var9 = 0; var9 < this.renderChunksDeep; ++var9)
                    {
                        int var10 = (var9 * this.renderChunksTall + var11) * this.renderChunksWide + var7;
                        this.worldRenderers[var10] = new WorldRenderer(this.theWorld, this.tileEntities, var7 * 16, var11 * 16, var9 * 16, this.glRenderListBase + var4);

                        if (this.occlusionEnabled)
                        {
                            this.worldRenderers[var10].glOcclusionQuery = this.glOcclusionQueryBase.get(var5);
                        }

                        this.worldRenderers[var10].isWaitingOnOcclusionQuery = false;
                        this.worldRenderers[var10].isVisible = true;
                        this.worldRenderers[var10].isInFrustum = false;
                        this.worldRenderers[var10].chunkIndex = var5++;
                        this.sortedWorldRenderers[var10] = this.worldRenderers[var10];

                        if (this.theWorld.chunkExists(var7, var9))
                        {
                            this.worldRenderers[var10].markDirty();
                            this.worldRenderersToUpdate.add(this.worldRenderers[var10]);
                        }

                        var4 += 3;
                    }
                }
            }

            if (this.theWorld != null)
            {
                Object var12 = this.mc.renderViewEntity;

                if (var12 == null)
                {
                    var12 = this.mc.thePlayer;
                }

                if (var12 != null)
                {
                    this.markRenderersForNewPosition(MathHelper.floor_double(((EntityLivingBase)var12).posX), MathHelper.floor_double(((EntityLivingBase)var12).posY), MathHelper.floor_double(((EntityLivingBase)var12).posZ));
                    Arrays.sort(this.sortedWorldRenderers, new EntitySorter((Entity)var12));
                }
            }

            this.renderEntitiesStartupCounter = 2;
        }
    }

    /**
     * Renders all entities within range and within the frustrum. Args: pos, frustrum, partialTickTime
     */
    public void renderEntities(Vec3 par1Vec3, ICamera par2ICamera, float par3)
    {
        int var4 = 0;

        if (Reflector.MinecraftForgeClient_getRenderPass.exists())
        {
            var4 = Reflector.callInt(Reflector.MinecraftForgeClient_getRenderPass, new Object[0]);
        }

        boolean var5 = Reflector.ForgeEntity_shouldRenderInPass.exists();
        boolean var6 = Reflector.ForgeTileEntity_shouldRenderInPass.exists();

        if (this.renderEntitiesStartupCounter > 0)
        {
            if (var4 > 0)
            {
                return;
            }

            --this.renderEntitiesStartupCounter;
        }
        else
        {
            this.theWorld.theProfiler.startSection("prepare");
            TileEntityRenderer.instance.cacheActiveRenderInfo(this.theWorld, this.mc.getTextureManager(), this.mc.fontRenderer, this.mc.renderViewEntity, par3);
            RenderManager.instance.cacheActiveRenderInfo(this.theWorld, this.mc.getTextureManager(), this.mc.fontRenderer, this.mc.renderViewEntity, this.mc.pointedEntityLiving, this.mc.gameSettings, par3);

            if (var4 == 0)
            {
                this.countEntitiesTotal = 0;
                this.countEntitiesRendered = 0;
                this.countEntitiesHidden = 0;
                EntityLivingBase var7 = this.mc.renderViewEntity;
                RenderManager.renderPosX = var7.lastTickPosX + (var7.posX - var7.lastTickPosX) * (double)par3;
                RenderManager.renderPosY = var7.lastTickPosY + (var7.posY - var7.lastTickPosY) * (double)par3;
                RenderManager.renderPosZ = var7.lastTickPosZ + (var7.posZ - var7.lastTickPosZ) * (double)par3;
                TileEntityRenderer.staticPlayerX = var7.lastTickPosX + (var7.posX - var7.lastTickPosX) * (double)par3;
                TileEntityRenderer.staticPlayerY = var7.lastTickPosY + (var7.posY - var7.lastTickPosY) * (double)par3;
                TileEntityRenderer.staticPlayerZ = var7.lastTickPosZ + (var7.posZ - var7.lastTickPosZ) * (double)par3;
            }

            this.mc.entityRenderer.enableLightmap((double)par3);
            this.theWorld.theProfiler.endStartSection("global");
            List var18 = this.theWorld.getLoadedEntityList();

            if (var4 == 0)
            {
                this.countEntitiesTotal = var18.size();
            }

            if (Config.isFogOff() && this.mc.entityRenderer.fogStandard)
            {
                GL11.glDisable(GL11.GL_FOG);
            }

            int var8;
            Entity var9;

            for (var8 = 0; var8 < this.theWorld.weatherEffects.size(); ++var8)
            {
                var9 = (Entity)this.theWorld.weatherEffects.get(var8);

                if (!var5 || Reflector.callBoolean(var9, Reflector.ForgeEntity_shouldRenderInPass, new Object[] {Integer.valueOf(var4)}))
                {
                    ++this.countEntitiesRendered;

                    if (var9.isInRangeToRenderVec3D(par1Vec3))
                    {
                        RenderManager.instance.renderEntity(var9, par3);
                    }
                }
            }

            this.theWorld.theProfiler.endStartSection("entities");
            boolean var10 = this.mc.gameSettings.fancyGraphics;
            this.mc.gameSettings.fancyGraphics = Config.isDroppedItemsFancy();

            for (var8 = 0; var8 < var18.size(); ++var8)
            {
                var9 = (Entity)var18.get(var8);

                if (!var5 || Reflector.callBoolean(var9, Reflector.ForgeEntity_shouldRenderInPass, new Object[] {Integer.valueOf(var4)}))
                {
                    boolean var11 = var9.isInRangeToRenderVec3D(par1Vec3) && (var9.ignoreFrustumCheck || par2ICamera.isBoundingBoxInFrustum(var9.boundingBox) || var9.riddenByEntity == this.mc.thePlayer);

                    if (!var11 && var9 instanceof EntityLiving)
                    {
                        EntityLiving var12 = (EntityLiving)var9;

                        if (var12.getLeashed() && var12.getLeashedToEntity() != null)
                        {
                            Entity var13 = var12.getLeashedToEntity();
                            var11 = par2ICamera.isBoundingBoxInFrustum(var13.boundingBox);
                        }
                    }

                    if (var11 && (var9 != this.mc.renderViewEntity || this.mc.gameSettings.thirdPersonView != 0 || this.mc.renderViewEntity.isPlayerSleeping()) && this.theWorld.blockExists(MathHelper.floor_double(var9.posX), 0, MathHelper.floor_double(var9.posZ)))
                    {
                        ++this.countEntitiesRendered;

                        if (var9.getClass() == EntityItemFrame.class)
                        {
                            var9.renderDistanceWeight = 0.06D;
                        }

                        this.renderedEntity = var9;
                        RenderManager.instance.renderEntity(var9, par3);
                        this.renderedEntity = null;
                    }
                }
            }

            this.mc.gameSettings.fancyGraphics = var10;
            this.theWorld.theProfiler.endStartSection("tileentities");
            RenderHelper.enableStandardItemLighting();

            for (var8 = 0; var8 < this.tileEntities.size(); ++var8)
            {
                TileEntity var19 = (TileEntity)this.tileEntities.get(var8);

                if (!var6 || Reflector.callBoolean(var19, Reflector.ForgeTileEntity_shouldRenderInPass, new Object[] {Integer.valueOf(var4)}))
                {
                    AxisAlignedBB var20 = this.getTileEntityBoundingBox(var19);

                    if (par2ICamera.isBoundingBoxInFrustum(var20))
                    {
                        Class var21 = var19.getClass();

                        if (var21 == TileEntitySign.class && !Config.zoomMode)
                        {
                            EntityClientPlayerMP var14 = this.mc.thePlayer;
                            double var15 = var19.getDistanceFrom(var14.posX, var14.posY, var14.posZ);

                            if (var15 > 256.0D)
                            {
                                FontRenderer var17 = TileEntityRenderer.instance.getFontRenderer();
                                var17.enabled = false;
                                TileEntityRenderer.instance.renderTileEntity(var19, par3);
                                var17.enabled = true;
                                continue;
                            }
                        }

                        if (var21 == TileEntityChest.class)
                        {
                            int var22 = this.theWorld.getBlockId(var19.xCoord, var19.yCoord, var19.zCoord);
                            Block var23 = Block.blocksList[var22];

                            if (!(var23 instanceof BlockChest))
                            {
                                continue;
                            }
                        }

                        TileEntityRenderer.instance.renderTileEntity(var19, par3);
                    }
                }
            }

            this.mc.entityRenderer.disableLightmap((double)par3);
            this.theWorld.theProfiler.endSection();
        }
    }

    /**
     * Gets the render info for use on the Debug screen
     */
    public String getDebugInfoRenders()
    {
        return "C: " + this.renderersBeingRendered + "/" + this.renderersLoaded + ". F: " + this.renderersBeingClipped + ", O: " + this.renderersBeingOccluded + ", E: " + this.renderersSkippingRenderPass;
    }

    /**
     * Gets the entities info for use on the Debug screen
     */
    public String getDebugInfoEntities()
    {
        return "E: " + this.countEntitiesRendered + "/" + this.countEntitiesTotal + ". B: " + this.countEntitiesHidden + ", I: " + (this.countEntitiesTotal - this.countEntitiesHidden - this.countEntitiesRendered) + ", " + Config.getVersion();
    }

    /**
     * Goes through all the renderers setting new positions on them and those that have their position changed are
     * adding to be updated
     */
    private void markRenderersForNewPosition(int par1, int par2, int par3)
    {
        par1 -= 8;
        par2 -= 8;
        par3 -= 8;
        this.minBlockX = Integer.MAX_VALUE;
        this.minBlockY = Integer.MAX_VALUE;
        this.minBlockZ = Integer.MAX_VALUE;
        this.maxBlockX = Integer.MIN_VALUE;
        this.maxBlockY = Integer.MIN_VALUE;
        this.maxBlockZ = Integer.MIN_VALUE;
        int var4 = this.renderChunksWide * 16;
        int var5 = var4 / 2;

        for (int var6 = 0; var6 < this.renderChunksWide; ++var6)
        {
            int var7 = var6 * 16;
            int var8 = var7 + var5 - par1;

            if (var8 < 0)
            {
                var8 -= var4 - 1;
            }

            var8 /= var4;
            var7 -= var8 * var4;

            if (var7 < this.minBlockX)
            {
                this.minBlockX = var7;
            }

            if (var7 > this.maxBlockX)
            {
                this.maxBlockX = var7;
            }

            for (int var9 = 0; var9 < this.renderChunksDeep; ++var9)
            {
                int var10 = var9 * 16;
                int var11 = var10 + var5 - par3;

                if (var11 < 0)
                {
                    var11 -= var4 - 1;
                }

                var11 /= var4;
                var10 -= var11 * var4;

                if (var10 < this.minBlockZ)
                {
                    this.minBlockZ = var10;
                }

                if (var10 > this.maxBlockZ)
                {
                    this.maxBlockZ = var10;
                }

                for (int var12 = 0; var12 < this.renderChunksTall; ++var12)
                {
                    int var13 = var12 * 16;

                    if (var13 < this.minBlockY)
                    {
                        this.minBlockY = var13;
                    }

                    if (var13 > this.maxBlockY)
                    {
                        this.maxBlockY = var13;
                    }

                    WorldRenderer var14 = this.worldRenderers[(var9 * this.renderChunksTall + var12) * this.renderChunksWide + var6];
                    boolean var15 = var14.needsUpdate;
                    var14.setPosition(var7, var13, var10);

                    if (!var15 && var14.needsUpdate)
                    {
                        this.worldRenderersToUpdate.add(var14);
                    }
                }
            }
        }
    }

    /**
     * Sorts all renderers based on the passed in entity. Args: entityLiving, renderPass, partialTickTime
     */
    public int sortAndRender(EntityLivingBase par1EntityLivingBase, int par2, double par3)
    {
        Profiler var5 = this.theWorld.theProfiler;
        var5.startSection("sortchunks");

        if (this.worldRenderersToUpdate.size() < 10)
        {
            byte var6 = 10;

            for (int var7 = 0; var7 < var6; ++var7)
            {
                this.worldRenderersCheckIndex = (this.worldRenderersCheckIndex + 1) % this.worldRenderers.length;
                WorldRenderer var8 = this.worldRenderers[this.worldRenderersCheckIndex];

                if (var8.needsUpdate && !this.worldRenderersToUpdate.contains(var8))
                {
                    this.worldRenderersToUpdate.add(var8);
                }
            }
        }

        if (this.mc.gameSettings.renderDistance != this.renderDistance && !Config.isLoadChunksFar())
        {
            this.loadRenderers();
        }

        if (par2 == 0)
        {
            this.renderersLoaded = 0;
            this.dummyRenderInt = 0;
            this.renderersBeingClipped = 0;
            this.renderersBeingOccluded = 0;
            this.renderersBeingRendered = 0;
            this.renderersSkippingRenderPass = 0;
        }

        double var40 = par1EntityLivingBase.lastTickPosX + (par1EntityLivingBase.posX - par1EntityLivingBase.lastTickPosX) * par3;
        double var41 = par1EntityLivingBase.lastTickPosY + (par1EntityLivingBase.posY - par1EntityLivingBase.lastTickPosY) * par3;
        double var10 = par1EntityLivingBase.lastTickPosZ + (par1EntityLivingBase.posZ - par1EntityLivingBase.lastTickPosZ) * par3;
        double var12 = par1EntityLivingBase.posX - this.prevSortX;
        double var14 = par1EntityLivingBase.posY - this.prevSortY;
        double var16 = par1EntityLivingBase.posZ - this.prevSortZ;
        double var18 = var12 * var12 + var14 * var14 + var16 * var16;
        int var20;

        if (var18 > 16.0D)
        {
            this.prevSortX = par1EntityLivingBase.posX;
            this.prevSortY = par1EntityLivingBase.posY;
            this.prevSortZ = par1EntityLivingBase.posZ;
            var20 = Config.getPreloadedChunks() * 16;
            double var21 = par1EntityLivingBase.posX - this.prevReposX;
            double var23 = par1EntityLivingBase.posY - this.prevReposY;
            double var25 = par1EntityLivingBase.posZ - this.prevReposZ;
            double var27 = var21 * var21 + var23 * var23 + var25 * var25;

            if (var27 > (double)(var20 * var20) + 16.0D)
            {
                this.prevReposX = par1EntityLivingBase.posX;
                this.prevReposY = par1EntityLivingBase.posY;
                this.prevReposZ = par1EntityLivingBase.posZ;
                this.markRenderersForNewPosition(MathHelper.floor_double(par1EntityLivingBase.posX), MathHelper.floor_double(par1EntityLivingBase.posY), MathHelper.floor_double(par1EntityLivingBase.posZ));
            }

            Arrays.sort(this.sortedWorldRenderers, new EntitySorter(par1EntityLivingBase));
            int var29 = (int)par1EntityLivingBase.posX;
            int var30 = (int)par1EntityLivingBase.posZ;
            short var31 = 2000;

            if (Math.abs(var29 - WorldRenderer.globalChunkOffsetX) > var31 || Math.abs(var30 - WorldRenderer.globalChunkOffsetZ) > var31)
            {
                WorldRenderer.globalChunkOffsetX = var29;
                WorldRenderer.globalChunkOffsetZ = var30;
                this.loadRenderers();
            }
        }

        RenderHelper.disableStandardItemLighting();

        if (this.mc.gameSettings.ofSmoothFps && par2 == 0)
        {
            GL11.glFinish();
        }

        byte var42 = 0;
        int var43 = 0;

        if (this.occlusionEnabled && this.mc.gameSettings.advancedOpengl && !this.mc.gameSettings.anaglyph && par2 == 0)
        {
            byte var22 = 0;
            byte var44 = 20;
            this.checkOcclusionQueryResult(var22, var44, par1EntityLivingBase.posX, par1EntityLivingBase.posY, par1EntityLivingBase.posZ);
            int var24;

            for (var24 = var22; var24 < var44; ++var24)
            {
                this.sortedWorldRenderers[var24].isVisible = true;
            }

            var5.endStartSection("render");
            var20 = var42 + this.renderSortedRenderers(var22, var44, par2, par3);
            var24 = var44;
            int var45 = 0;
            byte var26 = 40;
            int var28;

            for (int var46 = this.renderChunksWide; var24 < this.sortedWorldRenderers.length; var20 += this.renderSortedRenderers(var28, var24, par2, par3))
            {
                var5.endStartSection("occ");
                var28 = var24;

                if (var45 < var46)
                {
                    ++var45;
                }
                else
                {
                    --var45;
                }

                var24 += var45 * var26;

                if (var24 <= var28)
                {
                    var24 = var28 + 10;
                }

                if (var24 > this.sortedWorldRenderers.length)
                {
                    var24 = this.sortedWorldRenderers.length;
                }

                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glDisable(GL11.GL_ALPHA_TEST);
                GL11.glDisable(GL11.GL_FOG);
                GL11.glColorMask(false, false, false, false);
                GL11.glDepthMask(false);
                var5.startSection("check");
                this.checkOcclusionQueryResult(var28, var24, par1EntityLivingBase.posX, par1EntityLivingBase.posY, par1EntityLivingBase.posZ);
                var5.endSection();
                GL11.glPushMatrix();
                float var47 = 0.0F;
                float var48 = 0.0F;
                float var49 = 0.0F;

                for (int var32 = var28; var32 < var24; ++var32)
                {
                    WorldRenderer var33 = this.sortedWorldRenderers[var32];

                    if (var33.skipAllRenderPasses())
                    {
                        var33.isInFrustum = false;
                    }
                    else if (var33.isUpdating)
                    {
                        var33.isVisible = true;
                    }
                    else if (var33.isInFrustum)
                    {
                        if (Config.isOcclusionFancy() && !var33.isInFrustrumFully)
                        {
                            var33.isVisible = true;
                        }
                        else if (var33.isInFrustum && !var33.isWaitingOnOcclusionQuery)
                        {
                            float var34;
                            float var35;
                            float var36;
                            float var37;

                            if (var33.isVisibleFromPosition)
                            {
                                var34 = Math.abs((float)(var33.visibleFromX - par1EntityLivingBase.posX));
                                var35 = Math.abs((float)(var33.visibleFromY - par1EntityLivingBase.posY));
                                var36 = Math.abs((float)(var33.visibleFromZ - par1EntityLivingBase.posZ));
                                var37 = var34 + var35 + var36;

                                if ((double)var37 < 10.0D + (double)var32 / 1000.0D)
                                {
                                    var33.isVisible = true;
                                    continue;
                                }

                                var33.isVisibleFromPosition = false;
                            }

                            var34 = (float)((double)var33.posXMinus - var40);
                            var35 = (float)((double)var33.posYMinus - var41);
                            var36 = (float)((double)var33.posZMinus - var10);
                            var37 = var34 - var47;
                            float var38 = var35 - var48;
                            float var39 = var36 - var49;

                            if (var37 != 0.0F || var38 != 0.0F || var39 != 0.0F)
                            {
                                GL11.glTranslatef(var37, var38, var39);
                                var47 += var37;
                                var48 += var38;
                                var49 += var39;
                            }

                            var5.startSection("bb");
                            ARBOcclusionQuery.glBeginQueryARB(ARBOcclusionQuery.GL_SAMPLES_PASSED_ARB, var33.glOcclusionQuery);
                            var33.callOcclusionQueryList();
                            ARBOcclusionQuery.glEndQueryARB(ARBOcclusionQuery.GL_SAMPLES_PASSED_ARB);
                            var5.endSection();
                            var33.isWaitingOnOcclusionQuery = true;
                            ++var43;
                        }
                    }
                }

                GL11.glPopMatrix();

                if (this.mc.gameSettings.anaglyph)
                {
                    if (EntityRenderer.anaglyphField == 0)
                    {
                        GL11.glColorMask(false, true, true, true);
                    }
                    else
                    {
                        GL11.glColorMask(true, false, false, true);
                    }
                }
                else
                {
                    GL11.glColorMask(true, true, true, true);
                }

                GL11.glDepthMask(true);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glEnable(GL11.GL_ALPHA_TEST);
                GL11.glEnable(GL11.GL_FOG);
                var5.endStartSection("render");
            }
        }
        else
        {
            var5.endStartSection("render");
            var20 = var42 + this.renderSortedRenderers(0, this.sortedWorldRenderers.length, par2, par3);
        }

        var5.endSection();
        return var20;
    }

    private void checkOcclusionQueryResult(int startIndex, int endIndex, double px, double py, double pz)
    {
        for (int k = startIndex; k < endIndex; ++k)
        {
            WorldRenderer wr = this.sortedWorldRenderers[k];

            if (wr.isWaitingOnOcclusionQuery)
            {
                this.occlusionResult.clear();
                ARBOcclusionQuery.glGetQueryObjectuARB(wr.glOcclusionQuery, ARBOcclusionQuery.GL_QUERY_RESULT_AVAILABLE_ARB, this.occlusionResult);

                if (this.occlusionResult.get(0) != 0)
                {
                    wr.isWaitingOnOcclusionQuery = false;
                    this.occlusionResult.clear();
                    ARBOcclusionQuery.glGetQueryObjectuARB(wr.glOcclusionQuery, ARBOcclusionQuery.GL_QUERY_RESULT_ARB, this.occlusionResult);
                    boolean wasVisible = wr.isVisible;
                    wr.isVisible = this.occlusionResult.get(0) > 0;

                    if (wasVisible && wr.isVisible)
                    {
                        wr.isVisibleFromPosition = true;
                        wr.visibleFromX = px;
                        wr.visibleFromY = py;
                        wr.visibleFromZ = pz;
                    }
                }
            }
        }
    }

    /**
     * Renders the sorted renders for the specified render pass. Args: startRenderer, numRenderers, renderPass,
     * partialTickTime
     */
    private int renderSortedRenderers(int par1, int par2, int par3, double par4)
    {
        this.glListBuffer.clear();
        int var6 = 0;
        boolean var7 = this.mc.gameSettings.showDebugInfo;

        for (int var8 = par1; var8 < par2; ++var8)
        {
            WorldRenderer var9 = this.sortedWorldRenderers[var8];

            if (var7 && par3 == 0)
            {
                ++this.renderersLoaded;

                if (var9.skipRenderPass[par3])
                {
                    ++this.renderersSkippingRenderPass;
                }
                else if (!var9.isInFrustum)
                {
                    ++this.renderersBeingClipped;
                }
                else if (this.occlusionEnabled && !var9.isVisible)
                {
                    ++this.renderersBeingOccluded;
                }
                else
                {
                    ++this.renderersBeingRendered;
                }
            }

            if (var9.isInFrustum && !var9.skipRenderPass[par3] && (!this.occlusionEnabled || var9.isVisible))
            {
                int var10 = var9.getGLCallListForPass(par3);

                if (var10 >= 0)
                {
                    this.glListBuffer.put(var10);
                    ++var6;
                }
            }
        }

        if (var6 == 0)
        {
            return 0;
        }
        else
        {
            if (Config.isFogOff() && this.mc.entityRenderer.fogStandard)
            {
                GL11.glDisable(GL11.GL_FOG);
            }

            this.glListBuffer.flip();
            EntityLivingBase var15 = this.mc.renderViewEntity;
            double var16 = var15.lastTickPosX + (var15.posX - var15.lastTickPosX) * par4 - (double)WorldRenderer.globalChunkOffsetX;
            double var11 = var15.lastTickPosY + (var15.posY - var15.lastTickPosY) * par4;
            double var13 = var15.lastTickPosZ + (var15.posZ - var15.lastTickPosZ) * par4 - (double)WorldRenderer.globalChunkOffsetZ;
            this.mc.entityRenderer.enableLightmap(par4);
            GL11.glTranslatef((float)(-var16), (float)(-var11), (float)(-var13));
            GL11.glCallLists(this.glListBuffer);
            GL11.glTranslatef((float)var16, (float)var11, (float)var13);
            this.mc.entityRenderer.disableLightmap(par4);
            return var6;
        }
    }

    /**
     * Render all render lists
     */
    public void renderAllRenderLists(int par1, double par2) {}

    public void updateClouds()
    {
        ++this.cloudTickCounter;

        if (this.cloudTickCounter % 20 == 0)
        {
            Iterator var1 = this.damagedBlocks.values().iterator();

            while (var1.hasNext())
            {
                DestroyBlockProgress var2 = (DestroyBlockProgress)var1.next();
                int var3 = var2.getCreationCloudUpdateTick();

                if (this.cloudTickCounter - var3 > 400)
                {
                    var1.remove();
                }
            }
        }
    }

    /**
     * Renders the sky with the partial tick time. Args: partialTickTime
     */
    public void renderSky(float par1)
    {
        if (Reflector.ForgeWorldProvider_getSkyRenderer.exists())
        {
            WorldProvider var2 = this.mc.theWorld.provider;
            Object var3 = Reflector.call(var2, Reflector.ForgeWorldProvider_getSkyRenderer, new Object[0]);

            if (var3 != null)
            {
                Reflector.callVoid(var3, Reflector.IRenderHandler_render, new Object[] {Float.valueOf(par1), this.theWorld, this.mc});
                return;
            }
        }

        if (this.mc.theWorld.provider.dimensionId == 1)
        {
            if (!Config.isSkyEnabled())
            {
                return;
            }

            GL11.glDisable(GL11.GL_FOG);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            RenderHelper.disableStandardItemLighting();
            GL11.glDepthMask(false);
            this.renderEngine.bindTexture(locationEndSkyPng);
            Tessellator var20 = Tessellator.instance;

            for (int var22 = 0; var22 < 6; ++var22)
            {
                GL11.glPushMatrix();

                if (var22 == 1)
                {
                    GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
                }

                if (var22 == 2)
                {
                    GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                }

                if (var22 == 3)
                {
                    GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
                }

                if (var22 == 4)
                {
                    GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
                }

                if (var22 == 5)
                {
                    GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
                }

                var20.startDrawingQuads();
                var20.setColorOpaque_I(2631720);
                var20.addVertexWithUV(-100.0D, -100.0D, -100.0D, 0.0D, 0.0D);
                var20.addVertexWithUV(-100.0D, -100.0D, 100.0D, 0.0D, 16.0D);
                var20.addVertexWithUV(100.0D, -100.0D, 100.0D, 16.0D, 16.0D);
                var20.addVertexWithUV(100.0D, -100.0D, -100.0D, 16.0D, 0.0D);
                var20.draw();
                GL11.glPopMatrix();
            }

            GL11.glDepthMask(true);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
        }
        else if (this.mc.theWorld.provider.isSurfaceWorld())
        {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            Vec3 var21 = this.theWorld.getSkyColor(this.mc.renderViewEntity, par1);
            var21 = CustomColorizer.getSkyColor(var21, this.mc.theWorld, this.mc.renderViewEntity.posX, this.mc.renderViewEntity.posY + 1.0D, this.mc.renderViewEntity.posZ);
            float var23 = (float)var21.xCoord;
            float var4 = (float)var21.yCoord;
            float var5 = (float)var21.zCoord;
            float var6;

            if (this.mc.gameSettings.anaglyph)
            {
                float var7 = (var23 * 30.0F + var4 * 59.0F + var5 * 11.0F) / 100.0F;
                float var8 = (var23 * 30.0F + var4 * 70.0F) / 100.0F;
                var6 = (var23 * 30.0F + var5 * 70.0F) / 100.0F;
                var23 = var7;
                var4 = var8;
                var5 = var6;
            }

            GL11.glColor3f(var23, var4, var5);
            Tessellator var24 = Tessellator.instance;
            GL11.glDepthMask(false);
            GL11.glEnable(GL11.GL_FOG);
            GL11.glColor3f(var23, var4, var5);

            if (Config.isSkyEnabled())
            {
                GL11.glCallList(this.glSkyList);
            }

            GL11.glDisable(GL11.GL_FOG);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            RenderHelper.disableStandardItemLighting();
            float[] var25 = this.theWorld.provider.calcSunriseSunsetColors(this.theWorld.getCelestialAngle(par1), par1);
            float var9;
            float var10;
            float var11;
            float var12;
            float var13;
            int var15;
            float var16;
            float var17;

            if (var25 != null && Config.isSunMoonEnabled())
            {
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glShadeModel(GL11.GL_SMOOTH);
                GL11.glPushMatrix();
                GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(MathHelper.sin(this.theWorld.getCelestialAngleRadians(par1)) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
                var6 = var25[0];
                var9 = var25[1];
                var10 = var25[2];

                if (this.mc.gameSettings.anaglyph)
                {
                    var11 = (var6 * 30.0F + var9 * 59.0F + var10 * 11.0F) / 100.0F;
                    var12 = (var6 * 30.0F + var9 * 70.0F) / 100.0F;
                    var13 = (var6 * 30.0F + var10 * 70.0F) / 100.0F;
                    var6 = var11;
                    var9 = var12;
                    var10 = var13;
                }

                var24.startDrawing(6);
                var24.setColorRGBA_F(var6, var9, var10, var25[3]);
                var24.addVertex(0.0D, 100.0D, 0.0D);
                byte var14 = 16;
                var24.setColorRGBA_F(var25[0], var25[1], var25[2], 0.0F);

                for (var15 = 0; var15 <= var14; ++var15)
                {
                    var13 = (float)var15 * (float)Math.PI * 2.0F / (float)var14;
                    var16 = MathHelper.sin(var13);
                    var17 = MathHelper.cos(var13);
                    var24.addVertex((double)(var16 * 120.0F), (double)(var17 * 120.0F), (double)(-var17 * 40.0F * var25[3]));
                }

                var24.draw();
                GL11.glPopMatrix();
                GL11.glShadeModel(GL11.GL_FLAT);
            }

            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
            GL11.glPushMatrix();
            var6 = 1.0F - this.theWorld.getRainStrength(par1);
            var9 = 0.0F;
            var10 = 0.0F;
            var11 = 0.0F;
            GL11.glColor4f(1.0F, 1.0F, 1.0F, var6);
            GL11.glTranslatef(var9, var10, var11);
            GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
            CustomSky.renderSky(this.theWorld, this.renderEngine, this.theWorld.getCelestialAngle(par1), var6);
            GL11.glRotatef(this.theWorld.getCelestialAngle(par1) * 360.0F, 1.0F, 0.0F, 0.0F);

            if (Config.isSunMoonEnabled())
            {
                var12 = 30.0F;
                this.renderEngine.bindTexture(locationSunPng);
                var24.startDrawingQuads();
                var24.addVertexWithUV((double)(-var12), 100.0D, (double)(-var12), 0.0D, 0.0D);
                var24.addVertexWithUV((double)var12, 100.0D, (double)(-var12), 1.0D, 0.0D);
                var24.addVertexWithUV((double)var12, 100.0D, (double)var12, 1.0D, 1.0D);
                var24.addVertexWithUV((double)(-var12), 100.0D, (double)var12, 0.0D, 1.0D);
                var24.draw();
                var12 = 20.0F;
                this.renderEngine.bindTexture(locationMoonPhasesPng);
                int var26 = this.theWorld.getMoonPhase();
                int var27 = var26 % 4;
                var15 = var26 / 4 % 2;
                var16 = (float)(var27 + 0) / 4.0F;
                var17 = (float)(var15 + 0) / 2.0F;
                float var18 = (float)(var27 + 1) / 4.0F;
                float var19 = (float)(var15 + 1) / 2.0F;
                var24.startDrawingQuads();
                var24.addVertexWithUV((double)(-var12), -100.0D, (double)var12, (double)var18, (double)var19);
                var24.addVertexWithUV((double)var12, -100.0D, (double)var12, (double)var16, (double)var19);
                var24.addVertexWithUV((double)var12, -100.0D, (double)(-var12), (double)var16, (double)var17);
                var24.addVertexWithUV((double)(-var12), -100.0D, (double)(-var12), (double)var18, (double)var17);
                var24.draw();
            }

            GL11.glDisable(GL11.GL_TEXTURE_2D);
            var13 = this.theWorld.getStarBrightness(par1) * var6;

            if (var13 > 0.0F && Config.isStarsEnabled() && !CustomSky.hasSkyLayers(this.theWorld))
            {
                GL11.glColor4f(var13, var13, var13, var13);
                GL11.glCallList(this.starGLCallList);
            }

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_FOG);
            GL11.glPopMatrix();
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glColor3f(0.0F, 0.0F, 0.0F);
            double var28 = this.mc.thePlayer.getPosition(par1).yCoord - this.theWorld.getHorizon();

            if (var28 < 0.0D)
            {
                GL11.glPushMatrix();
                GL11.glTranslatef(0.0F, 12.0F, 0.0F);
                GL11.glCallList(this.glSkyList2);
                GL11.glPopMatrix();
                var10 = 1.0F;
                var11 = -((float)(var28 + 65.0D));
                var12 = -var10;
                var24.startDrawingQuads();
                var24.setColorRGBA_I(0, 255);
                var24.addVertex((double)(-var10), (double)var11, (double)var10);
                var24.addVertex((double)var10, (double)var11, (double)var10);
                var24.addVertex((double)var10, (double)var12, (double)var10);
                var24.addVertex((double)(-var10), (double)var12, (double)var10);
                var24.addVertex((double)(-var10), (double)var12, (double)(-var10));
                var24.addVertex((double)var10, (double)var12, (double)(-var10));
                var24.addVertex((double)var10, (double)var11, (double)(-var10));
                var24.addVertex((double)(-var10), (double)var11, (double)(-var10));
                var24.addVertex((double)var10, (double)var12, (double)(-var10));
                var24.addVertex((double)var10, (double)var12, (double)var10);
                var24.addVertex((double)var10, (double)var11, (double)var10);
                var24.addVertex((double)var10, (double)var11, (double)(-var10));
                var24.addVertex((double)(-var10), (double)var11, (double)(-var10));
                var24.addVertex((double)(-var10), (double)var11, (double)var10);
                var24.addVertex((double)(-var10), (double)var12, (double)var10);
                var24.addVertex((double)(-var10), (double)var12, (double)(-var10));
                var24.addVertex((double)(-var10), (double)var12, (double)(-var10));
                var24.addVertex((double)(-var10), (double)var12, (double)var10);
                var24.addVertex((double)var10, (double)var12, (double)var10);
                var24.addVertex((double)var10, (double)var12, (double)(-var10));
                var24.draw();
            }

            if (this.theWorld.provider.isSkyColored())
            {
                GL11.glColor3f(var23 * 0.2F + 0.04F, var4 * 0.2F + 0.04F, var5 * 0.6F + 0.1F);
            }
            else
            {
                GL11.glColor3f(var23, var4, var5);
            }

            if (this.mc.gameSettings.ofRenderDistanceFine <= 64)
            {
                GL11.glColor3f(this.mc.entityRenderer.fogColorRed, this.mc.entityRenderer.fogColorGreen, this.mc.entityRenderer.fogColorBlue);
            }

            GL11.glPushMatrix();
            GL11.glTranslatef(0.0F, -((float)(var28 - 16.0D)), 0.0F);

            if (Config.isSkyEnabled())
            {
                GL11.glCallList(this.glSkyList2);
            }

            GL11.glPopMatrix();
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDepthMask(true);
        }
    }

    public void renderClouds(float par1)
    {
        if (!Config.isCloudsOff())
        {
            if (Reflector.ForgeWorldProvider_getCloudRenderer.exists())
            {
                WorldProvider var2 = this.mc.theWorld.provider;
                Object var3 = Reflector.call(var2, Reflector.ForgeWorldProvider_getCloudRenderer, new Object[0]);

                if (var3 != null)
                {
                    Reflector.callVoid(var3, Reflector.IRenderHandler_render, new Object[] {Float.valueOf(par1), this.theWorld, this.mc});
                    return;
                }
            }

            if (this.mc.theWorld.provider.isSurfaceWorld())
            {
                if (Config.isCloudsFancy())
                {
                    this.renderCloudsFancy(par1);
                }
                else
                {
                    GL11.glDisable(GL11.GL_CULL_FACE);
                    float var24 = (float)(this.mc.renderViewEntity.lastTickPosY + (this.mc.renderViewEntity.posY - this.mc.renderViewEntity.lastTickPosY) * (double)par1);
                    byte var25 = 32;
                    int var4 = 256 / var25;
                    Tessellator var5 = Tessellator.instance;
                    this.renderEngine.bindTexture(locationCloudsPng);
                    GL11.glEnable(GL11.GL_BLEND);
                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                    Vec3 var6 = this.theWorld.getCloudColour(par1);
                    float var7 = (float)var6.xCoord;
                    float var8 = (float)var6.yCoord;
                    float var9 = (float)var6.zCoord;
                    float var10;

                    if (this.mc.gameSettings.anaglyph)
                    {
                        var10 = (var7 * 30.0F + var8 * 59.0F + var9 * 11.0F) / 100.0F;
                        float var11 = (var7 * 30.0F + var8 * 70.0F) / 100.0F;
                        float var12 = (var7 * 30.0F + var9 * 70.0F) / 100.0F;
                        var7 = var10;
                        var8 = var11;
                        var9 = var12;
                    }

                    var10 = 4.8828125E-4F;
                    double var26 = (double)((float)this.cloudTickCounter + par1);
                    double var13 = this.mc.renderViewEntity.prevPosX + (this.mc.renderViewEntity.posX - this.mc.renderViewEntity.prevPosX) * (double)par1 + var26 * 0.029999999329447746D;
                    double var15 = this.mc.renderViewEntity.prevPosZ + (this.mc.renderViewEntity.posZ - this.mc.renderViewEntity.prevPosZ) * (double)par1;
                    int var17 = MathHelper.floor_double(var13 / 2048.0D);
                    int var18 = MathHelper.floor_double(var15 / 2048.0D);
                    var13 -= (double)(var17 * 2048);
                    var15 -= (double)(var18 * 2048);
                    float var19 = this.theWorld.provider.getCloudHeight() - var24 + 0.33F;
                    var19 += this.mc.gameSettings.ofCloudsHeight * 128.0F;
                    float var20 = (float)(var13 * (double)var10);
                    float var21 = (float)(var15 * (double)var10);
                    var5.startDrawingQuads();
                    var5.setColorRGBA_F(var7, var8, var9, 0.8F);

                    for (int var22 = -var25 * var4; var22 < var25 * var4; var22 += var25)
                    {
                        for (int var23 = -var25 * var4; var23 < var25 * var4; var23 += var25)
                        {
                            var5.addVertexWithUV((double)(var22 + 0), (double)var19, (double)(var23 + var25), (double)((float)(var22 + 0) * var10 + var20), (double)((float)(var23 + var25) * var10 + var21));
                            var5.addVertexWithUV((double)(var22 + var25), (double)var19, (double)(var23 + var25), (double)((float)(var22 + var25) * var10 + var20), (double)((float)(var23 + var25) * var10 + var21));
                            var5.addVertexWithUV((double)(var22 + var25), (double)var19, (double)(var23 + 0), (double)((float)(var22 + var25) * var10 + var20), (double)((float)(var23 + 0) * var10 + var21));
                            var5.addVertexWithUV((double)(var22 + 0), (double)var19, (double)(var23 + 0), (double)((float)(var22 + 0) * var10 + var20), (double)((float)(var23 + 0) * var10 + var21));
                        }
                    }

                    var5.draw();
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    GL11.glDisable(GL11.GL_BLEND);
                    GL11.glEnable(GL11.GL_CULL_FACE);
                }
            }
        }
    }

    /**
     * Checks if the given position is to be rendered with cloud fog
     */
    public boolean hasCloudFog(double par1, double par3, double par5, float par7)
    {
        return false;
    }

    /**
     * Renders the 3d fancy clouds
     */
    public void renderCloudsFancy(float par1)
    {
        GL11.glDisable(GL11.GL_CULL_FACE);
        float var2 = (float)(this.mc.renderViewEntity.lastTickPosY + (this.mc.renderViewEntity.posY - this.mc.renderViewEntity.lastTickPosY) * (double)par1);
        Tessellator var3 = Tessellator.instance;
        float var4 = 12.0F;
        float var5 = 4.0F;
        double var6 = (double)((float)this.cloudTickCounter + par1);
        double var8 = (this.mc.renderViewEntity.prevPosX + (this.mc.renderViewEntity.posX - this.mc.renderViewEntity.prevPosX) * (double)par1 + var6 * 0.029999999329447746D) / (double)var4;
        double var10 = (this.mc.renderViewEntity.prevPosZ + (this.mc.renderViewEntity.posZ - this.mc.renderViewEntity.prevPosZ) * (double)par1) / (double)var4 + 0.33000001311302185D;
        float var12 = this.theWorld.provider.getCloudHeight() - var2 + 0.33F;
        var12 += this.mc.gameSettings.ofCloudsHeight * 128.0F;
        int var13 = MathHelper.floor_double(var8 / 2048.0D);
        int var14 = MathHelper.floor_double(var10 / 2048.0D);
        var8 -= (double)(var13 * 2048);
        var10 -= (double)(var14 * 2048);
        this.renderEngine.bindTexture(locationCloudsPng);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        Vec3 var15 = this.theWorld.getCloudColour(par1);
        float var16 = (float)var15.xCoord;
        float var17 = (float)var15.yCoord;
        float var18 = (float)var15.zCoord;
        float var19;
        float var20;
        float var21;

        if (this.mc.gameSettings.anaglyph)
        {
            var19 = (var16 * 30.0F + var17 * 59.0F + var18 * 11.0F) / 100.0F;
            var21 = (var16 * 30.0F + var17 * 70.0F) / 100.0F;
            var20 = (var16 * 30.0F + var18 * 70.0F) / 100.0F;
            var16 = var19;
            var17 = var21;
            var18 = var20;
        }

        var19 = (float)(var8 * 0.0D);
        var21 = (float)(var10 * 0.0D);
        var20 = 0.00390625F;
        var19 = (float)MathHelper.floor_double(var8) * var20;
        var21 = (float)MathHelper.floor_double(var10) * var20;
        float var22 = (float)(var8 - (double)MathHelper.floor_double(var8));
        float var23 = (float)(var10 - (double)MathHelper.floor_double(var10));
        byte var24 = 8;
        byte var25 = 4;
        float var26 = 9.765625E-4F;
        GL11.glScalef(var4, 1.0F, var4);

        for (int var27 = 0; var27 < 2; ++var27)
        {
            if (var27 == 0)
            {
                GL11.glColorMask(false, false, false, false);
            }
            else if (this.mc.gameSettings.anaglyph)
            {
                if (EntityRenderer.anaglyphField == 0)
                {
                    GL11.glColorMask(false, true, true, true);
                }
                else
                {
                    GL11.glColorMask(true, false, false, true);
                }
            }
            else
            {
                GL11.glColorMask(true, true, true, true);
            }

            for (int var28 = -var25 + 1; var28 <= var25; ++var28)
            {
                for (int var29 = -var25 + 1; var29 <= var25; ++var29)
                {
                    var3.startDrawingQuads();
                    float var30 = (float)(var28 * var24);
                    float var31 = (float)(var29 * var24);
                    float var32 = var30 - var22;
                    float var33 = var31 - var23;

                    if (var12 > -var5 - 1.0F)
                    {
                        var3.setColorRGBA_F(var16 * 0.7F, var17 * 0.7F, var18 * 0.7F, 0.8F);
                        var3.setNormal(0.0F, -1.0F, 0.0F);
                        var3.addVertexWithUV((double)(var32 + 0.0F), (double)(var12 + 0.0F), (double)(var33 + (float)var24), (double)((var30 + 0.0F) * var20 + var19), (double)((var31 + (float)var24) * var20 + var21));
                        var3.addVertexWithUV((double)(var32 + (float)var24), (double)(var12 + 0.0F), (double)(var33 + (float)var24), (double)((var30 + (float)var24) * var20 + var19), (double)((var31 + (float)var24) * var20 + var21));
                        var3.addVertexWithUV((double)(var32 + (float)var24), (double)(var12 + 0.0F), (double)(var33 + 0.0F), (double)((var30 + (float)var24) * var20 + var19), (double)((var31 + 0.0F) * var20 + var21));
                        var3.addVertexWithUV((double)(var32 + 0.0F), (double)(var12 + 0.0F), (double)(var33 + 0.0F), (double)((var30 + 0.0F) * var20 + var19), (double)((var31 + 0.0F) * var20 + var21));
                    }

                    if (var12 <= var5 + 1.0F)
                    {
                        var3.setColorRGBA_F(var16, var17, var18, 0.8F);
                        var3.setNormal(0.0F, 1.0F, 0.0F);
                        var3.addVertexWithUV((double)(var32 + 0.0F), (double)(var12 + var5 - var26), (double)(var33 + (float)var24), (double)((var30 + 0.0F) * var20 + var19), (double)((var31 + (float)var24) * var20 + var21));
                        var3.addVertexWithUV((double)(var32 + (float)var24), (double)(var12 + var5 - var26), (double)(var33 + (float)var24), (double)((var30 + (float)var24) * var20 + var19), (double)((var31 + (float)var24) * var20 + var21));
                        var3.addVertexWithUV((double)(var32 + (float)var24), (double)(var12 + var5 - var26), (double)(var33 + 0.0F), (double)((var30 + (float)var24) * var20 + var19), (double)((var31 + 0.0F) * var20 + var21));
                        var3.addVertexWithUV((double)(var32 + 0.0F), (double)(var12 + var5 - var26), (double)(var33 + 0.0F), (double)((var30 + 0.0F) * var20 + var19), (double)((var31 + 0.0F) * var20 + var21));
                    }

                    var3.setColorRGBA_F(var16 * 0.9F, var17 * 0.9F, var18 * 0.9F, 0.8F);
                    int var34;

                    if (var28 > -1)
                    {
                        var3.setNormal(-1.0F, 0.0F, 0.0F);

                        for (var34 = 0; var34 < var24; ++var34)
                        {
                            var3.addVertexWithUV((double)(var32 + (float)var34 + 0.0F), (double)(var12 + 0.0F), (double)(var33 + (float)var24), (double)((var30 + (float)var34 + 0.5F) * var20 + var19), (double)((var31 + (float)var24) * var20 + var21));
                            var3.addVertexWithUV((double)(var32 + (float)var34 + 0.0F), (double)(var12 + var5), (double)(var33 + (float)var24), (double)((var30 + (float)var34 + 0.5F) * var20 + var19), (double)((var31 + (float)var24) * var20 + var21));
                            var3.addVertexWithUV((double)(var32 + (float)var34 + 0.0F), (double)(var12 + var5), (double)(var33 + 0.0F), (double)((var30 + (float)var34 + 0.5F) * var20 + var19), (double)((var31 + 0.0F) * var20 + var21));
                            var3.addVertexWithUV((double)(var32 + (float)var34 + 0.0F), (double)(var12 + 0.0F), (double)(var33 + 0.0F), (double)((var30 + (float)var34 + 0.5F) * var20 + var19), (double)((var31 + 0.0F) * var20 + var21));
                        }
                    }

                    if (var28 <= 1)
                    {
                        var3.setNormal(1.0F, 0.0F, 0.0F);

                        for (var34 = 0; var34 < var24; ++var34)
                        {
                            var3.addVertexWithUV((double)(var32 + (float)var34 + 1.0F - var26), (double)(var12 + 0.0F), (double)(var33 + (float)var24), (double)((var30 + (float)var34 + 0.5F) * var20 + var19), (double)((var31 + (float)var24) * var20 + var21));
                            var3.addVertexWithUV((double)(var32 + (float)var34 + 1.0F - var26), (double)(var12 + var5), (double)(var33 + (float)var24), (double)((var30 + (float)var34 + 0.5F) * var20 + var19), (double)((var31 + (float)var24) * var20 + var21));
                            var3.addVertexWithUV((double)(var32 + (float)var34 + 1.0F - var26), (double)(var12 + var5), (double)(var33 + 0.0F), (double)((var30 + (float)var34 + 0.5F) * var20 + var19), (double)((var31 + 0.0F) * var20 + var21));
                            var3.addVertexWithUV((double)(var32 + (float)var34 + 1.0F - var26), (double)(var12 + 0.0F), (double)(var33 + 0.0F), (double)((var30 + (float)var34 + 0.5F) * var20 + var19), (double)((var31 + 0.0F) * var20 + var21));
                        }
                    }

                    var3.setColorRGBA_F(var16 * 0.8F, var17 * 0.8F, var18 * 0.8F, 0.8F);

                    if (var29 > -1)
                    {
                        var3.setNormal(0.0F, 0.0F, -1.0F);

                        for (var34 = 0; var34 < var24; ++var34)
                        {
                            var3.addVertexWithUV((double)(var32 + 0.0F), (double)(var12 + var5), (double)(var33 + (float)var34 + 0.0F), (double)((var30 + 0.0F) * var20 + var19), (double)((var31 + (float)var34 + 0.5F) * var20 + var21));
                            var3.addVertexWithUV((double)(var32 + (float)var24), (double)(var12 + var5), (double)(var33 + (float)var34 + 0.0F), (double)((var30 + (float)var24) * var20 + var19), (double)((var31 + (float)var34 + 0.5F) * var20 + var21));
                            var3.addVertexWithUV((double)(var32 + (float)var24), (double)(var12 + 0.0F), (double)(var33 + (float)var34 + 0.0F), (double)((var30 + (float)var24) * var20 + var19), (double)((var31 + (float)var34 + 0.5F) * var20 + var21));
                            var3.addVertexWithUV((double)(var32 + 0.0F), (double)(var12 + 0.0F), (double)(var33 + (float)var34 + 0.0F), (double)((var30 + 0.0F) * var20 + var19), (double)((var31 + (float)var34 + 0.5F) * var20 + var21));
                        }
                    }

                    if (var29 <= 1)
                    {
                        var3.setNormal(0.0F, 0.0F, 1.0F);

                        for (var34 = 0; var34 < var24; ++var34)
                        {
                            var3.addVertexWithUV((double)(var32 + 0.0F), (double)(var12 + var5), (double)(var33 + (float)var34 + 1.0F - var26), (double)((var30 + 0.0F) * var20 + var19), (double)((var31 + (float)var34 + 0.5F) * var20 + var21));
                            var3.addVertexWithUV((double)(var32 + (float)var24), (double)(var12 + var5), (double)(var33 + (float)var34 + 1.0F - var26), (double)((var30 + (float)var24) * var20 + var19), (double)((var31 + (float)var34 + 0.5F) * var20 + var21));
                            var3.addVertexWithUV((double)(var32 + (float)var24), (double)(var12 + 0.0F), (double)(var33 + (float)var34 + 1.0F - var26), (double)((var30 + (float)var24) * var20 + var19), (double)((var31 + (float)var34 + 0.5F) * var20 + var21));
                            var3.addVertexWithUV((double)(var32 + 0.0F), (double)(var12 + 0.0F), (double)(var33 + (float)var34 + 1.0F - var26), (double)((var30 + 0.0F) * var20 + var19), (double)((var31 + (float)var34 + 0.5F) * var20 + var21));
                        }
                    }

                    var3.draw();
                }
            }
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_CULL_FACE);
    }

    /**
     * Updates some of the renderers sorted by distance from the player
     */
    public boolean updateRenderers(EntityLivingBase par1EntityLivingBase, boolean par2)
    {
        if (this.worldRenderersToUpdate.size() <= 0)
        {
            return false;
        }
        else
        {
            int var3 = 0;
            int var4 = Config.getUpdatesPerFrame();

            if (Config.isDynamicUpdates() && !this.isMoving(par1EntityLivingBase))
            {
                var4 *= 3;
            }

            byte var5 = 4;
            int var6 = 0;
            WorldRenderer var7 = null;
            float var8 = Float.MAX_VALUE;
            int var9 = -1;

            for (int var10 = 0; var10 < this.worldRenderersToUpdate.size(); ++var10)
            {
                WorldRenderer var11 = (WorldRenderer)this.worldRenderersToUpdate.get(var10);

                if (var11 != null)
                {
                    ++var6;

                    if (!var11.needsUpdate)
                    {
                        this.worldRenderersToUpdate.set(var10, (Object)null);
                    }
                    else
                    {
                        float var12 = var11.distanceToEntitySquared(par1EntityLivingBase);

                        if (var12 <= 256.0F && this.isActingNow())
                        {
                            var11.updateRenderer();
                            var11.needsUpdate = false;
                            this.worldRenderersToUpdate.set(var10, (Object)null);
                            ++var3;
                        }
                        else
                        {
                            if (var12 > 256.0F && var3 >= var4)
                            {
                                break;
                            }

                            if (!var11.isInFrustum)
                            {
                                var12 *= (float)var5;
                            }

                            if (var7 == null)
                            {
                                var7 = var11;
                                var8 = var12;
                                var9 = var10;
                            }
                            else if (var12 < var8)
                            {
                                var7 = var11;
                                var8 = var12;
                                var9 = var10;
                            }
                        }
                    }
                }
            }

            if (var7 != null)
            {
                var7.updateRenderer();
                var7.needsUpdate = false;
                this.worldRenderersToUpdate.set(var9, (Object)null);
                ++var3;
                float var15 = var8 / 5.0F;

                for (int var16 = 0; var16 < this.worldRenderersToUpdate.size() && var3 < var4; ++var16)
                {
                    WorldRenderer var17 = (WorldRenderer)this.worldRenderersToUpdate.get(var16);

                    if (var17 != null)
                    {
                        float var13 = var17.distanceToEntitySquared(par1EntityLivingBase);

                        if (!var17.isInFrustum)
                        {
                            var13 *= (float)var5;
                        }

                        float var14 = Math.abs(var13 - var8);

                        if (var14 < var15)
                        {
                            var17.updateRenderer();
                            var17.needsUpdate = false;
                            this.worldRenderersToUpdate.set(var16, (Object)null);
                            ++var3;
                        }
                    }
                }
            }

            if (var6 == 0)
            {
                this.worldRenderersToUpdate.clear();
            }

            this.worldRenderersToUpdate.compact();
            return true;
        }
    }

    public void drawBlockDamageTexture(Tessellator par1Tessellator, EntityPlayer par2EntityPlayer, float par3)
    {
        this.drawBlockDamageTexture(par1Tessellator, par2EntityPlayer, par3);
    }

    public void drawBlockDamageTexture(Tessellator par1Tessellator, EntityLivingBase par2EntityPlayer, float par3)
    {
        double var4 = par2EntityPlayer.lastTickPosX + (par2EntityPlayer.posX - par2EntityPlayer.lastTickPosX) * (double)par3;
        double var6 = par2EntityPlayer.lastTickPosY + (par2EntityPlayer.posY - par2EntityPlayer.lastTickPosY) * (double)par3;
        double var8 = par2EntityPlayer.lastTickPosZ + (par2EntityPlayer.posZ - par2EntityPlayer.lastTickPosZ) * (double)par3;

        if (!this.damagedBlocks.isEmpty())
        {
            GL11.glBlendFunc(GL11.GL_DST_COLOR, GL11.GL_SRC_COLOR);
            this.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
            GL11.glPushMatrix();
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glPolygonOffset(-3.0F, -3.0F);
            GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            par1Tessellator.startDrawingQuads();
            par1Tessellator.setTranslation(-var4, -var6, -var8);
            par1Tessellator.disableColor();
            Iterator var10 = this.damagedBlocks.values().iterator();

            while (var10.hasNext())
            {
                DestroyBlockProgress var11 = (DestroyBlockProgress)var10.next();
                double var12 = (double)var11.getPartialBlockX() - var4;
                double var14 = (double)var11.getPartialBlockY() - var6;
                double var16 = (double)var11.getPartialBlockZ() - var8;

                if (var12 * var12 + var14 * var14 + var16 * var16 > 1024.0D)
                {
                    var10.remove();
                }
                else
                {
                    int var18 = this.theWorld.getBlockId(var11.getPartialBlockX(), var11.getPartialBlockY(), var11.getPartialBlockZ());
                    Block var19 = var18 > 0 ? Block.blocksList[var18] : null;

                    if (var19 == null)
                    {
                        var19 = Block.stone;
                    }

                    this.globalRenderBlocks.renderBlockUsingTexture(var19, var11.getPartialBlockX(), var11.getPartialBlockY(), var11.getPartialBlockZ(), this.destroyBlockIcons[var11.getPartialBlockDamage()]);
                }
            }

            par1Tessellator.draw();
            par1Tessellator.setTranslation(0.0D, 0.0D, 0.0D);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glPolygonOffset(0.0F, 0.0F);
            GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glDepthMask(true);
            GL11.glPopMatrix();
        }
    }

    /**
     * Draws the selection box for the player. Args: entityPlayer, rayTraceHit, i, itemStack, partialTickTime
     */
    public void drawSelectionBox(EntityPlayer par1EntityPlayer, MovingObjectPosition par2MovingObjectPosition, int par3, float par4)
    {
        if (par3 == 0 && par2MovingObjectPosition.typeOfHit == EnumMovingObjectType.TILE)
        {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.4F);
            GL11.glLineWidth(2.0F);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDepthMask(false);
            float var5 = 0.002F;
            int var6 = this.theWorld.getBlockId(par2MovingObjectPosition.blockX, par2MovingObjectPosition.blockY, par2MovingObjectPosition.blockZ);

            if (var6 > 0)
            {
                Block.blocksList[var6].setBlockBoundsBasedOnState(this.theWorld, par2MovingObjectPosition.blockX, par2MovingObjectPosition.blockY, par2MovingObjectPosition.blockZ);
                double var7 = par1EntityPlayer.lastTickPosX + (par1EntityPlayer.posX - par1EntityPlayer.lastTickPosX) * (double)par4;
                double var9 = par1EntityPlayer.lastTickPosY + (par1EntityPlayer.posY - par1EntityPlayer.lastTickPosY) * (double)par4;
                double var11 = par1EntityPlayer.lastTickPosZ + (par1EntityPlayer.posZ - par1EntityPlayer.lastTickPosZ) * (double)par4;
                this.drawOutlinedBoundingBox(Block.blocksList[var6].getSelectedBoundingBoxFromPool(this.theWorld, par2MovingObjectPosition.blockX, par2MovingObjectPosition.blockY, par2MovingObjectPosition.blockZ).expand((double)var5, (double)var5, (double)var5).getOffsetBoundingBox(-var7, -var9, -var11));
            }

            GL11.glDepthMask(true);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_BLEND);
        }
    }

    /**
     * Draws lines for the edges of the bounding box.
     */
    public void drawOutlinedBoundingBox(AxisAlignedBB par1AxisAlignedBB)
    {
        Tessellator var2 = Tessellator.instance;
        var2.startDrawing(3);
        var2.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        var2.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        var2.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
        var2.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
        var2.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        var2.draw();
        var2.startDrawing(3);
        var2.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        var2.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        var2.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
        var2.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
        var2.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        var2.draw();
        var2.startDrawing(1);
        var2.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        var2.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        var2.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        var2.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        var2.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
        var2.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
        var2.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
        var2.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
        var2.draw();
    }

    /**
     * Marks the blocks in the given range for update
     */
    public void markBlocksForUpdate(int par1, int par2, int par3, int par4, int par5, int par6)
    {
        int var7 = MathHelper.bucketInt(par1, 16);
        int var8 = MathHelper.bucketInt(par2, 16);
        int var9 = MathHelper.bucketInt(par3, 16);
        int var10 = MathHelper.bucketInt(par4, 16);
        int var11 = MathHelper.bucketInt(par5, 16);
        int var12 = MathHelper.bucketInt(par6, 16);

        for (int var13 = var7; var13 <= var10; ++var13)
        {
            int var14 = var13 % this.renderChunksWide;

            if (var14 < 0)
            {
                var14 += this.renderChunksWide;
            }

            for (int var15 = var8; var15 <= var11; ++var15)
            {
                int var16 = var15 % this.renderChunksTall;

                if (var16 < 0)
                {
                    var16 += this.renderChunksTall;
                }

                for (int var17 = var9; var17 <= var12; ++var17)
                {
                    int var18 = var17 % this.renderChunksDeep;

                    if (var18 < 0)
                    {
                        var18 += this.renderChunksDeep;
                    }

                    int var19 = (var18 * this.renderChunksTall + var16) * this.renderChunksWide + var14;
                    WorldRenderer var20 = this.worldRenderers[var19];

                    if (var20 != null && !var20.needsUpdate)
                    {
                        this.worldRenderersToUpdate.add(var20);
                        var20.markDirty();
                    }
                }
            }
        }
    }

    /**
     * On the client, re-renders the block. On the server, sends the block to the client (which will re-render it),
     * including the tile entity description packet if applicable. Args: x, y, z
     */
    public void markBlockForUpdate(int par1, int par2, int par3)
    {
        this.markBlocksForUpdate(par1 - 1, par2 - 1, par3 - 1, par1 + 1, par2 + 1, par3 + 1);
    }

    /**
     * On the client, re-renders this block. On the server, does nothing. Used for lighting updates.
     */
    public void markBlockForRenderUpdate(int par1, int par2, int par3)
    {
        this.markBlocksForUpdate(par1 - 1, par2 - 1, par3 - 1, par1 + 1, par2 + 1, par3 + 1);
    }

    /**
     * On the client, re-renders all blocks in this range, inclusive. On the server, does nothing. Args: min x, min y,
     * min z, max x, max y, max z
     */
    public void markBlockRangeForRenderUpdate(int par1, int par2, int par3, int par4, int par5, int par6)
    {
        this.markBlocksForUpdate(par1 - 1, par2 - 1, par3 - 1, par4 + 1, par5 + 1, par6 + 1);
    }

    /**
     * Checks all renderers that previously weren't in the frustum and 1/16th of those that previously were in the
     * frustum for frustum clipping Args: frustum, partialTickTime
     */
    public void clipRenderersByFrustum(ICamera par1ICamera, float par2)
    {
        for (int var3 = 0; var3 < this.worldRenderers.length; ++var3)
        {
            if (!this.worldRenderers[var3].skipAllRenderPasses())
            {
                this.worldRenderers[var3].updateInFrustum(par1ICamera);
            }
        }

        ++this.frustumCheckOffset;
    }

    /**
     * Plays the specified record. Arg: recordName, x, y, z
     */
    public void playRecord(String par1Str, int par2, int par3, int par4)
    {
        ItemRecord var5 = ItemRecord.getRecord(par1Str);

        if (par1Str != null && var5 != null)
        {
            this.mc.ingameGUI.setRecordPlayingMessage(var5.getRecordTitle());
        }

        this.mc.sndManager.playStreaming(par1Str, (float)par2, (float)par3, (float)par4);
    }

    /**
     * Plays the specified sound. Arg: soundName, x, y, z, volume, pitch
     */
    public void playSound(String par1Str, double par2, double par4, double par6, float par8, float par9) {}

    /**
     * Plays sound to all near players except the player reference given
     */
    public void playSoundToNearExcept(EntityPlayer par1EntityPlayer, String par2Str, double par3, double par5, double par7, float par9, float par10) {}

    /**
     * Spawns a particle. Arg: particleType, x, y, z, velX, velY, velZ
     */
    public void spawnParticle(String par1Str, double par2, double par4, double par6, double par8, double par10, double par12)
    {
        try
        {
            this.doSpawnParticle(par1Str, par2, par4, par6, par8, par10, par12);
        }
        catch (Throwable var17)
        {
            CrashReport var15 = CrashReport.makeCrashReport(var17, "Exception while adding particle");
            CrashReportCategory var16 = var15.makeCategory("Particle being added");
            var16.addCrashSection("Name", par1Str);
            var16.addCrashSectionCallable("Position", new CallableParticlePositionInfo(this, par2, par4, par6));
            throw new ReportedException(var15);
        }
    }

    /**
     * Spawns a particle. Arg: particleType, x, y, z, velX, velY, velZ
     */
    public EntityFX doSpawnParticle(String par1Str, double par2, double par4, double par6, double par8, double par10, double par12)
    {
        if (this.mc != null && this.mc.renderViewEntity != null && this.mc.effectRenderer != null)
        {
            int var14 = this.mc.gameSettings.particleSetting;

            if (var14 == 1 && this.theWorld.rand.nextInt(3) == 0)
            {
                var14 = 2;
            }

            double var15 = this.mc.renderViewEntity.posX - par2;
            double var17 = this.mc.renderViewEntity.posY - par4;
            double var19 = this.mc.renderViewEntity.posZ - par6;
            EntityFX var21 = null;

            if (par1Str.equals("hugeexplosion"))
            {
                if (Config.isAnimatedExplosion())
                {
                    this.mc.effectRenderer.addEffect(var21 = new EntityHugeExplodeFX(this.theWorld, par2, par4, par6, par8, par10, par12));
                }
            }
            else if (par1Str.equals("largeexplode"))
            {
                if (Config.isAnimatedExplosion())
                {
                    this.mc.effectRenderer.addEffect(var21 = new EntityLargeExplodeFX(this.renderEngine, this.theWorld, par2, par4, par6, par8, par10, par12));
                }
            }
            else if (par1Str.equals("fireworksSpark"))
            {
                this.mc.effectRenderer.addEffect(var21 = new EntityFireworkSparkFX(this.theWorld, par2, par4, par6, par8, par10, par12, this.mc.effectRenderer));
            }

            if (var21 != null)
            {
                return (EntityFX)var21;
            }
            else
            {
                double var22 = 16.0D;
                double var24 = 16.0D;

                if (par1Str.equals("crit"))
                {
                    var22 = 196.0D;
                }

                if (var15 * var15 + var17 * var17 + var19 * var19 > var22 * var22)
                {
                    return null;
                }
                else if (var14 > 1)
                {
                    return null;
                }
                else
                {
                    if (par1Str.equals("bubble"))
                    {
                        var21 = new EntityBubbleFX(this.theWorld, par2, par4, par6, par8, par10, par12);
                        CustomColorizer.updateWaterFX((EntityFX)var21, this.theWorld);
                    }
                    else if (par1Str.equals("suspended"))
                    {
                        if (Config.isWaterParticles())
                        {
                            var21 = new EntitySuspendFX(this.theWorld, par2, par4, par6, par8, par10, par12);
                        }
                    }
                    else if (par1Str.equals("depthsuspend"))
                    {
                        if (Config.isVoidParticles())
                        {
                            var21 = new EntityAuraFX(this.theWorld, par2, par4, par6, par8, par10, par12);
                        }
                    }
                    else if (par1Str.equals("townaura"))
                    {
                        var21 = new EntityAuraFX(this.theWorld, par2, par4, par6, par8, par10, par12);
                        CustomColorizer.updateMyceliumFX((EntityFX)var21);
                    }
                    else if (par1Str.equals("crit"))
                    {
                        var21 = new EntityCritFX(this.theWorld, par2, par4, par6, par8, par10, par12);
                    }
                    else if (par1Str.equals("magicCrit"))
                    {
                        var21 = new EntityCritFX(this.theWorld, par2, par4, par6, par8, par10, par12);
                        ((EntityFX)var21).setRBGColorF(((EntityFX)var21).getRedColorF() * 0.3F, ((EntityFX)var21).getGreenColorF() * 0.8F, ((EntityFX)var21).getBlueColorF());
                        ((EntityFX)var21).nextTextureIndexX();
                    }
                    else if (par1Str.equals("smoke"))
                    {
                        if (Config.isAnimatedSmoke())
                        {
                            var21 = new EntitySmokeFX(this.theWorld, par2, par4, par6, par8, par10, par12);
                        }
                    }
                    else if (par1Str.equals("mobSpell"))
                    {
                        if (Config.isPotionParticles())
                        {
                            var21 = new EntitySpellParticleFX(this.theWorld, par2, par4, par6, 0.0D, 0.0D, 0.0D);
                            ((EntityFX)var21).setRBGColorF((float)par8, (float)par10, (float)par12);
                        }
                    }
                    else if (par1Str.equals("mobSpellAmbient"))
                    {
                        if (Config.isPotionParticles())
                        {
                            var21 = new EntitySpellParticleFX(this.theWorld, par2, par4, par6, 0.0D, 0.0D, 0.0D);
                            ((EntityFX)var21).setAlphaF(0.15F);
                            ((EntityFX)var21).setRBGColorF((float)par8, (float)par10, (float)par12);
                        }
                    }
                    else if (par1Str.equals("spell"))
                    {
                        if (Config.isPotionParticles())
                        {
                            var21 = new EntitySpellParticleFX(this.theWorld, par2, par4, par6, par8, par10, par12);
                        }
                    }
                    else if (par1Str.equals("instantSpell"))
                    {
                        if (Config.isPotionParticles())
                        {
                            var21 = new EntitySpellParticleFX(this.theWorld, par2, par4, par6, par8, par10, par12);
                            ((EntitySpellParticleFX)var21).setBaseSpellTextureIndex(144);
                        }
                    }
                    else if (par1Str.equals("witchMagic"))
                    {
                        if (Config.isPotionParticles())
                        {
                            var21 = new EntitySpellParticleFX(this.theWorld, par2, par4, par6, par8, par10, par12);
                            ((EntitySpellParticleFX)var21).setBaseSpellTextureIndex(144);
                            float var26 = this.theWorld.rand.nextFloat() * 0.5F + 0.35F;
                            ((EntityFX)var21).setRBGColorF(1.0F * var26, 0.0F * var26, 1.0F * var26);
                        }
                    }
                    else if (par1Str.equals("note"))
                    {
                        var21 = new EntityNoteFX(this.theWorld, par2, par4, par6, par8, par10, par12);
                    }
                    else if (par1Str.equals("portal"))
                    {
                        if (Config.isPortalParticles())
                        {
                            var21 = new EntityPortalFX(this.theWorld, par2, par4, par6, par8, par10, par12);
                            CustomColorizer.updatePortalFX((EntityFX)var21);
                        }
                    }
                    else if (par1Str.equals("enchantmenttable"))
                    {
                        var21 = new EntityEnchantmentTableParticleFX(this.theWorld, par2, par4, par6, par8, par10, par12);
                    }
                    else if (par1Str.equals("explode"))
                    {
                        if (Config.isAnimatedExplosion())
                        {
                            var21 = new EntityExplodeFX(this.theWorld, par2, par4, par6, par8, par10, par12);
                        }
                    }
                    else if (par1Str.equals("flame"))
                    {
                        if (Config.isAnimatedFlame())
                        {
                            var21 = new EntityFlameFX(this.theWorld, par2, par4, par6, par8, par10, par12);
                        }
                    }
                    else if (par1Str.equals("lava"))
                    {
                        var21 = new EntityLavaFX(this.theWorld, par2, par4, par6);
                    }
                    else if (par1Str.equals("footstep"))
                    {
                        var21 = new EntityFootStepFX(this.renderEngine, this.theWorld, par2, par4, par6);
                    }
                    else if (par1Str.equals("splash"))
                    {
                        var21 = new EntitySplashFX(this.theWorld, par2, par4, par6, par8, par10, par12);
                        CustomColorizer.updateWaterFX((EntityFX)var21, this.theWorld);
                    }
                    else if (par1Str.equals("largesmoke"))
                    {
                        if (Config.isAnimatedSmoke())
                        {
                            var21 = new EntitySmokeFX(this.theWorld, par2, par4, par6, par8, par10, par12, 2.5F);
                        }
                    }
                    else if (par1Str.equals("cloud"))
                    {
                        var21 = new EntityCloudFX(this.theWorld, par2, par4, par6, par8, par10, par12);
                    }
                    else if (par1Str.equals("reddust"))
                    {
                        if (Config.isAnimatedRedstone())
                        {
                            var21 = new EntityReddustFX(this.theWorld, par2, par4, par6, (float)par8, (float)par10, (float)par12);
                            CustomColorizer.updateReddustFX((EntityFX)var21, this.theWorld, var15, var17, var19);
                        }
                    }
                    else if (par1Str.equals("snowballpoof"))
                    {
                        var21 = new EntityBreakingFX(this.theWorld, par2, par4, par6, Item.snowball);
                    }
                    else if (par1Str.equals("dripWater"))
                    {
                        if (Config.isDrippingWaterLava())
                        {
                            var21 = new EntityDropParticleFX(this.theWorld, par2, par4, par6, Material.water);
                        }
                    }
                    else if (par1Str.equals("dripLava"))
                    {
                        if (Config.isDrippingWaterLava())
                        {
                            var21 = new EntityDropParticleFX(this.theWorld, par2, par4, par6, Material.lava);
                        }
                    }
                    else if (par1Str.equals("snowshovel"))
                    {
                        var21 = new EntitySnowShovelFX(this.theWorld, par2, par4, par6, par8, par10, par12);
                    }
                    else if (par1Str.equals("slime"))
                    {
                        var21 = new EntityBreakingFX(this.theWorld, par2, par4, par6, Item.slimeBall);
                    }
                    else if (par1Str.equals("heart"))
                    {
                        var21 = new EntityHeartFX(this.theWorld, par2, par4, par6, par8, par10, par12);
                    }
                    else if (par1Str.equals("angryVillager"))
                    {
                        var21 = new EntityHeartFX(this.theWorld, par2, par4 + 0.5D, par6, par8, par10, par12);
                        ((EntityFX)var21).setParticleTextureIndex(81);
                        ((EntityFX)var21).setRBGColorF(1.0F, 1.0F, 1.0F);
                    }
                    else if (par1Str.equals("happyVillager"))
                    {
                        var21 = new EntityAuraFX(this.theWorld, par2, par4, par6, par8, par10, par12);
                        ((EntityFX)var21).setParticleTextureIndex(82);
                        ((EntityFX)var21).setRBGColorF(1.0F, 1.0F, 1.0F);
                    }
                    else
                    {
                        String[] var27;
                        int var28;
                        int var29;

                        if (par1Str.startsWith("iconcrack_"))
                        {
                            var27 = par1Str.split("_", 3);
                            var29 = Integer.parseInt(var27[1]);

                            if (var27.length > 2)
                            {
                                var28 = Integer.parseInt(var27[2]);
                                var21 = new EntityBreakingFX(this.theWorld, par2, par4, par6, par8, par10, par12, Item.itemsList[var29], var28);
                            }
                            else
                            {
                                var21 = new EntityBreakingFX(this.theWorld, par2, par4, par6, par8, par10, par12, Item.itemsList[var29], 0);
                            }
                        }
                        else if (par1Str.startsWith("tilecrack_"))
                        {
                            var27 = par1Str.split("_", 3);
                            var29 = Integer.parseInt(var27[1]);
                            var28 = Integer.parseInt(var27[2]);
                            var21 = (new EntityDiggingFX(this.theWorld, par2, par4, par6, par8, par10, par12, Block.blocksList[var29], var28)).applyRenderColor(var28);
                        }
                    }

                    if (var21 != null)
                    {
                        this.mc.effectRenderer.addEffect((EntityFX)var21);
                    }

                    return (EntityFX)var21;
                }
            }
        }
        else
        {
            return null;
        }
    }

    /**
     * Called on all IWorldAccesses when an entity is created or loaded. On client worlds, starts downloading any
     * necessary textures. On server worlds, adds the entity to the entity tracker.
     */
    public void onEntityCreate(Entity par1Entity)
    {
        RandomMobs.entityLoaded(par1Entity);
    }

    /**
     * Called on all IWorldAccesses when an entity is unloaded or destroyed. On client worlds, releases any downloaded
     * textures. On server worlds, removes the entity from the entity tracker.
     */
    public void onEntityDestroy(Entity par1Entity) {}

    /**
     * Deletes all display lists
     */
    public void deleteAllDisplayLists()
    {
        GLAllocation.deleteDisplayLists(this.glRenderListBase);
    }

    public void broadcastSound(int par1, int par2, int par3, int par4, int par5)
    {
        Random var6 = this.theWorld.rand;

        switch (par1)
        {
            case 1013:
            case 1018:
                if (this.mc.renderViewEntity != null)
                {
                    double var7 = (double)par2 - this.mc.renderViewEntity.posX;
                    double var9 = (double)par3 - this.mc.renderViewEntity.posY;
                    double var11 = (double)par4 - this.mc.renderViewEntity.posZ;
                    double var13 = Math.sqrt(var7 * var7 + var9 * var9 + var11 * var11);
                    double var15 = this.mc.renderViewEntity.posX;
                    double var17 = this.mc.renderViewEntity.posY;
                    double var19 = this.mc.renderViewEntity.posZ;

                    if (var13 > 0.0D)
                    {
                        var15 += var7 / var13 * 2.0D;
                        var17 += var9 / var13 * 2.0D;
                        var19 += var11 / var13 * 2.0D;
                    }

                    if (par1 == 1013)
                    {
                        this.theWorld.playSound(var15, var17, var19, "mob.wither.spawn", 1.0F, 1.0F, false);
                    }
                    else if (par1 == 1018)
                    {
                        this.theWorld.playSound(var15, var17, var19, "mob.enderdragon.end", 5.0F, 1.0F, false);
                    }
                }

            default:
        }
    }

    /**
     * Plays a pre-canned sound effect along with potentially auxiliary data-driven one-shot behaviour (particles, etc).
     */
    public void playAuxSFX(EntityPlayer par1EntityPlayer, int par2, int par3, int par4, int par5, int par6)
    {
        Random var7 = this.theWorld.rand;
        double var8;
        double var10;
        double var12;
        String var14;
        int var15;
        int var16;
        double var17;
        double var19;
        double var21;
        double var23;
        double var25;

        switch (par2)
        {
            case 1000:
                this.theWorld.playSound((double)par3, (double)par4, (double)par5, "random.click", 1.0F, 1.0F, false);
                break;

            case 1001:
                this.theWorld.playSound((double)par3, (double)par4, (double)par5, "random.click", 1.0F, 1.2F, false);
                break;

            case 1002:
                this.theWorld.playSound((double)par3, (double)par4, (double)par5, "random.bow", 1.0F, 1.2F, false);
                break;

            case 1003:
                if (Math.random() < 0.5D)
                {
                    this.theWorld.playSound((double)par3 + 0.5D, (double)par4 + 0.5D, (double)par5 + 0.5D, "random.door_open", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
                }
                else
                {
                    this.theWorld.playSound((double)par3 + 0.5D, (double)par4 + 0.5D, (double)par5 + 0.5D, "random.door_close", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
                }

                break;

            case 1004:
                this.theWorld.playSound((double)((float)par3 + 0.5F), (double)((float)par4 + 0.5F), (double)((float)par5 + 0.5F), "random.fizz", 0.5F, 2.6F + (var7.nextFloat() - var7.nextFloat()) * 0.8F, false);
                break;

            case 1005:
                if (Item.itemsList[par6] instanceof ItemRecord)
                {
                    this.theWorld.playRecord(((ItemRecord)Item.itemsList[par6]).recordName, par3, par4, par5);
                }
                else
                {
                    this.theWorld.playRecord((String)null, par3, par4, par5);
                }

                break;

            case 1007:
                this.theWorld.playSound((double)par3 + 0.5D, (double)par4 + 0.5D, (double)par5 + 0.5D, "mob.ghast.charge", 10.0F, (var7.nextFloat() - var7.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1008:
                this.theWorld.playSound((double)par3 + 0.5D, (double)par4 + 0.5D, (double)par5 + 0.5D, "mob.ghast.fireball", 10.0F, (var7.nextFloat() - var7.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1009:
                this.theWorld.playSound((double)par3 + 0.5D, (double)par4 + 0.5D, (double)par5 + 0.5D, "mob.ghast.fireball", 2.0F, (var7.nextFloat() - var7.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1010:
                this.theWorld.playSound((double)par3 + 0.5D, (double)par4 + 0.5D, (double)par5 + 0.5D, "mob.zombie.wood", 2.0F, (var7.nextFloat() - var7.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1011:
                this.theWorld.playSound((double)par3 + 0.5D, (double)par4 + 0.5D, (double)par5 + 0.5D, "mob.zombie.metal", 2.0F, (var7.nextFloat() - var7.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1012:
                this.theWorld.playSound((double)par3 + 0.5D, (double)par4 + 0.5D, (double)par5 + 0.5D, "mob.zombie.woodbreak", 2.0F, (var7.nextFloat() - var7.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1014:
                this.theWorld.playSound((double)par3 + 0.5D, (double)par4 + 0.5D, (double)par5 + 0.5D, "mob.wither.shoot", 2.0F, (var7.nextFloat() - var7.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1015:
                this.theWorld.playSound((double)par3 + 0.5D, (double)par4 + 0.5D, (double)par5 + 0.5D, "mob.bat.takeoff", 0.05F, (var7.nextFloat() - var7.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1016:
                this.theWorld.playSound((double)par3 + 0.5D, (double)par4 + 0.5D, (double)par5 + 0.5D, "mob.zombie.infect", 2.0F, (var7.nextFloat() - var7.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1017:
                this.theWorld.playSound((double)par3 + 0.5D, (double)par4 + 0.5D, (double)par5 + 0.5D, "mob.zombie.unfect", 2.0F, (var7.nextFloat() - var7.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1020:
                this.theWorld.playSound((double)((float)par3 + 0.5F), (double)((float)par4 + 0.5F), (double)((float)par5 + 0.5F), "random.anvil_break", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 1021:
                this.theWorld.playSound((double)((float)par3 + 0.5F), (double)((float)par4 + 0.5F), (double)((float)par5 + 0.5F), "random.anvil_use", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 1022:
                this.theWorld.playSound((double)((float)par3 + 0.5F), (double)((float)par4 + 0.5F), (double)((float)par5 + 0.5F), "random.anvil_land", 0.3F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 2000:
                int var27 = par6 % 3 - 1;
                int var28 = par6 / 3 % 3 - 1;
                var10 = (double)par3 + (double)var27 * 0.6D + 0.5D;
                var12 = (double)par4 + 0.5D;
                double var29 = (double)par5 + (double)var28 * 0.6D + 0.5D;

                for (int var43 = 0; var43 < 10; ++var43)
                {
                    double var44 = var7.nextDouble() * 0.2D + 0.01D;
                    double var45 = var10 + (double)var27 * 0.01D + (var7.nextDouble() - 0.5D) * (double)var28 * 0.5D;
                    var25 = var12 + (var7.nextDouble() - 0.5D) * 0.5D;
                    var17 = var29 + (double)var28 * 0.01D + (var7.nextDouble() - 0.5D) * (double)var27 * 0.5D;
                    var19 = (double)var27 * var44 + var7.nextGaussian() * 0.01D;
                    var21 = -0.03D + var7.nextGaussian() * 0.01D;
                    var23 = (double)var28 * var44 + var7.nextGaussian() * 0.01D;
                    this.spawnParticle("smoke", var45, var25, var17, var19, var21, var23);
                }

                return;

            case 2001:
                var16 = par6 & 4095;

                if (var16 > 0)
                {
                    Block var42 = Block.blocksList[var16];
                    this.mc.sndManager.playSound(var42.stepSound.getBreakSound(), (float)par3 + 0.5F, (float)par4 + 0.5F, (float)par5 + 0.5F, (var42.stepSound.getVolume() + 1.0F) / 2.0F, var42.stepSound.getPitch() * 0.8F);
                }

                this.mc.effectRenderer.addBlockDestroyEffects(par3, par4, par5, par6 & 4095, par6 >> 12 & 255);
                break;

            case 2002:
                var8 = (double)par3;
                var10 = (double)par4;
                var12 = (double)par5;
                var14 = "iconcrack_" + Item.potion.itemID + "_" + par6;

                for (var15 = 0; var15 < 8; ++var15)
                {
                    this.spawnParticle(var14, var8, var10, var12, var7.nextGaussian() * 0.15D, var7.nextDouble() * 0.2D, var7.nextGaussian() * 0.15D);
                }

                var15 = Item.potion.getColorFromDamage(par6);
                float var31 = (float)(var15 >> 16 & 255) / 255.0F;
                float var32 = (float)(var15 >> 8 & 255) / 255.0F;
                float var33 = (float)(var15 >> 0 & 255) / 255.0F;
                String var34 = "spell";

                if (Item.potion.isEffectInstant(par6))
                {
                    var34 = "instantSpell";
                }

                for (var16 = 0; var16 < 100; ++var16)
                {
                    var25 = var7.nextDouble() * 4.0D;
                    var17 = var7.nextDouble() * Math.PI * 2.0D;
                    var19 = Math.cos(var17) * var25;
                    var21 = 0.01D + var7.nextDouble() * 0.5D;
                    var23 = Math.sin(var17) * var25;
                    EntityFX var47 = this.doSpawnParticle(var34, var8 + var19 * 0.1D, var10 + 0.3D, var12 + var23 * 0.1D, var19, var21, var23);

                    if (var47 != null)
                    {
                        float var48 = 0.75F + var7.nextFloat() * 0.25F;
                        var47.setRBGColorF(var31 * var48, var32 * var48, var33 * var48);
                        var47.multiplyVelocity((float)var25);
                    }
                }

                this.theWorld.playSound((double)par3 + 0.5D, (double)par4 + 0.5D, (double)par5 + 0.5D, "random.glass", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 2003:
                var8 = (double)par3 + 0.5D;
                var10 = (double)par4;
                var12 = (double)par5 + 0.5D;
                var14 = "iconcrack_" + Item.eyeOfEnder.itemID;

                for (var15 = 0; var15 < 8; ++var15)
                {
                    this.spawnParticle(var14, var8, var10, var12, var7.nextGaussian() * 0.15D, var7.nextDouble() * 0.2D, var7.nextGaussian() * 0.15D);
                }

                for (double var46 = 0.0D; var46 < (Math.PI * 2D); var46 += 0.15707963267948966D)
                {
                    this.spawnParticle("portal", var8 + Math.cos(var46) * 5.0D, var10 - 0.4D, var12 + Math.sin(var46) * 5.0D, Math.cos(var46) * -5.0D, 0.0D, Math.sin(var46) * -5.0D);
                    this.spawnParticle("portal", var8 + Math.cos(var46) * 5.0D, var10 - 0.4D, var12 + Math.sin(var46) * 5.0D, Math.cos(var46) * -7.0D, 0.0D, Math.sin(var46) * -7.0D);
                }

                return;

            case 2004:
                for (int var35 = 0; var35 < 20; ++var35)
                {
                    double var36 = (double)par3 + 0.5D + ((double)this.theWorld.rand.nextFloat() - 0.5D) * 2.0D;
                    double var38 = (double)par4 + 0.5D + ((double)this.theWorld.rand.nextFloat() - 0.5D) * 2.0D;
                    double var40 = (double)par5 + 0.5D + ((double)this.theWorld.rand.nextFloat() - 0.5D) * 2.0D;
                    this.theWorld.spawnParticle("smoke", var36, var38, var40, 0.0D, 0.0D, 0.0D);
                    this.theWorld.spawnParticle("flame", var36, var38, var40, 0.0D, 0.0D, 0.0D);
                }

                return;

            case 2005:
                ItemDye.func_96603_a(this.theWorld, par3, par4, par5, par6);
        }
    }

    /**
     * Starts (or continues) destroying a block with given ID at the given coordinates for the given partially destroyed
     * value
     */
    public void destroyBlockPartially(int par1, int par2, int par3, int par4, int par5)
    {
        if (par5 >= 0 && par5 < 10)
        {
            DestroyBlockProgress var6 = (DestroyBlockProgress)this.damagedBlocks.get(Integer.valueOf(par1));

            if (var6 == null || var6.getPartialBlockX() != par2 || var6.getPartialBlockY() != par3 || var6.getPartialBlockZ() != par4)
            {
                var6 = new DestroyBlockProgress(par1, par2, par3, par4);
                this.damagedBlocks.put(Integer.valueOf(par1), var6);
            }

            var6.setPartialBlockDamage(par5);
            var6.setCloudUpdateTick(this.cloudTickCounter);
        }
        else
        {
            this.damagedBlocks.remove(Integer.valueOf(par1));
        }
    }

    public void registerDestroyBlockIcons(IconRegister par1IconRegister)
    {
        this.destroyBlockIcons = new Icon[10];

        for (int var2 = 0; var2 < this.destroyBlockIcons.length; ++var2)
        {
            this.destroyBlockIcons[var2] = par1IconRegister.registerIcon("destroy_stage_" + var2);
        }
    }

    public void setAllRenderersVisible()
    {
        if (this.worldRenderers != null)
        {
            for (int i = 0; i < this.worldRenderers.length; ++i)
            {
                this.worldRenderers[i].isVisible = true;
            }
        }
    }

    public boolean isMoving(EntityLivingBase entityliving)
    {
        boolean moving = this.isMovingNow(entityliving);

        if (moving)
        {
            this.lastMovedTime = System.currentTimeMillis();
            return true;
        }
        else
        {
            return System.currentTimeMillis() - this.lastMovedTime < 2000L;
        }
    }

    private boolean isMovingNow(EntityLivingBase entityliving)
    {
        double maxDiff = 0.001D;
        return entityliving.isJumping ? true : (entityliving.isSneaking() ? true : ((double)entityliving.prevSwingProgress > maxDiff ? true : (this.mc.mouseHelper.deltaX != 0 ? true : (this.mc.mouseHelper.deltaY != 0 ? true : (Math.abs(entityliving.posX - entityliving.prevPosX) > maxDiff ? true : (Math.abs(entityliving.posY - entityliving.prevPosY) > maxDiff ? true : Math.abs(entityliving.posZ - entityliving.prevPosZ) > maxDiff))))));
    }

    public boolean isActing()
    {
        boolean acting = this.isActingNow();

        if (acting)
        {
            this.lastActionTime = System.currentTimeMillis();
            return true;
        }
        else
        {
            return System.currentTimeMillis() - this.lastActionTime < 500L;
        }
    }

    public boolean isActingNow()
    {
        return Mouse.isButtonDown(0) ? true : Mouse.isButtonDown(1);
    }

    public int renderAllSortedRenderers(int renderPass, double partialTicks)
    {
        return this.renderSortedRenderers(0, this.sortedWorldRenderers.length, renderPass, partialTicks);
    }

    public void updateCapes()
    {
        if (this.theWorld != null)
        {
            boolean showCapes = Config.isShowCapes();
            List playerList = this.theWorld.playerEntities;

            for (int i = 0; i < playerList.size(); ++i)
            {
                Entity entity = (Entity)playerList.get(i);

                if (entity instanceof AbstractClientPlayer)
                {
                    AbstractClientPlayer player = (AbstractClientPlayer)entity;
                    player.getTextureCape().enabled = showCapes;
                }
            }
        }
    }

    public AxisAlignedBB getTileEntityBoundingBox(TileEntity te)
    {
        Block blockType = te.getBlockType();

        if (blockType == Block.enchantmentTable)
        {
            return AxisAlignedBB.getAABBPool().getAABB((double)te.xCoord, (double)te.yCoord, (double)te.zCoord, (double)(te.xCoord + 1), (double)(te.yCoord + 1), (double)(te.zCoord + 1));
        }
        else if (blockType != Block.chest && blockType != Block.chestTrapped)
        {
            if (blockType != null && blockType != Block.beacon)
            {
                AxisAlignedBB blockAabb = te.getBlockType().getCollisionBoundingBoxFromPool(te.worldObj, te.xCoord, te.yCoord, te.zCoord);

                if (blockAabb != null)
                {
                    return blockAabb;
                }
            }

            return AABB_INFINITE;
        }
        else
        {
            return AxisAlignedBB.getAABBPool().getAABB((double)(te.xCoord - 1), (double)te.yCoord, (double)(te.zCoord - 1), (double)(te.xCoord + 2), (double)(te.yCoord + 2), (double)(te.zCoord + 2));
        }
    }
}

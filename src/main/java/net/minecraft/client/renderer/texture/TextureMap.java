package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import javax.imageio.ImageIO;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.StitcherException;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.item.Item;
import net.minecraft.src.Config;
import net.minecraft.src.ConnectedTextures;
import net.minecraft.src.CustomItems;
import net.minecraft.src.Reflector;
import net.minecraft.src.ReflectorForge;
import net.minecraft.src.TextureUtils;
import net.minecraft.src.WrUpdates;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import shadersmod.client.ShadersTex;

public class TextureMap extends AbstractTexture implements ITickableTextureObject, IIconRegister
{
    private static final boolean ENABLE_SKIP = Boolean.parseBoolean(System.getProperty("fml.skipFirstTextureLoad", "true"));
    private static final Logger logger = LogManager.getLogger();
    public static final ResourceLocation locationBlocksTexture = new ResourceLocation("textures/atlas/blocks.png");
    public static final ResourceLocation locationItemsTexture = new ResourceLocation("textures/atlas/items.png");
    private final List listAnimatedSprites;
    private final Map mapRegisteredSprites;
    private final Map mapUploadedSprites;
    public final int textureType;
    public final String basePath;
    private int mipmapLevels;
    private int anisotropicFiltering;
    private final TextureAtlasSprite missingImage;
    private boolean skipFirst;
    public static TextureMap textureMapBlocks = null;
    public static TextureMap textureMapItems = null;
    private TextureAtlasSprite[] iconGrid;
    private int iconGridSize;
    private int iconGridCountX;
    private int iconGridCountY;
    private double iconGridSizeU;
    private double iconGridSizeV;
    private int counterIndexInMap;
    public int atlasWidth;
    public int atlasHeight;

    public TextureMap(int par1, String par2Str)
    {
        this(par1, par2Str, false);
    }

    public TextureMap(int par1, String par2Str, boolean skipFirst)
    {
        this.listAnimatedSprites = Lists.newArrayList();
        this.mapRegisteredSprites = Maps.newHashMap();
        this.mapUploadedSprites = Maps.newHashMap();
        this.anisotropicFiltering = 1;
        this.missingImage = new TextureAtlasSprite("missingno");
        this.skipFirst = false;
        this.iconGrid = null;
        this.iconGridSize = -1;
        this.iconGridCountX = -1;
        this.iconGridCountY = -1;
        this.iconGridSizeU = -1.0D;
        this.iconGridSizeV = -1.0D;
        this.counterIndexInMap = 0;
        this.atlasWidth = 0;
        this.atlasHeight = 0;
        this.textureType = par1;
        this.basePath = par2Str;

        if (this.textureType == 0)
        {
            textureMapBlocks = this;
        }

        if (this.textureType == 1)
        {
            textureMapItems = this;
        }

        this.registerIcons();
        this.skipFirst = skipFirst && ENABLE_SKIP;
    }

    private void initMissingImage()
    {
        int[] var1;

        if ((float)this.anisotropicFiltering > 1.0F)
        {
            boolean var5 = true;
            boolean var3 = true;
            boolean var4 = true;
            this.missingImage.setIconWidth(32);
            this.missingImage.setIconHeight(32);
            var1 = new int[1024];
            System.arraycopy(TextureUtil.missingTextureData, 0, var1, 0, TextureUtil.missingTextureData.length);
            TextureUtil.prepareAnisotropicData(var1, 16, 16, 8);
        }
        else
        {
            var1 = TextureUtil.missingTextureData;
            this.missingImage.setIconWidth(16);
            this.missingImage.setIconHeight(16);
        }

        int[][] var51 = new int[this.mipmapLevels + 1][];
        var51[0] = var1;
        this.missingImage.setFramesTextureData(Lists.newArrayList(new int[][][] {var51}));
        this.missingImage.setIndexInMap(this.counterIndexInMap++);
    }

    public void loadTexture(IResourceManager par1ResourceManager) throws IOException
    {
        ShadersTex.resManager = par1ResourceManager;
        this.counterIndexInMap = 0;
        this.initMissingImage();
        this.deleteGlTexture();
        this.loadTextureAtlas(par1ResourceManager);
    }

    public void loadTextureAtlas(IResourceManager par1ResourceManager)
    {
        ShadersTex.resManager = par1ResourceManager;
        Config.dbg("Loading texture map: " + this.basePath);
        WrUpdates.finishCurrentUpdate();
        this.registerIcons();
        int var2 = TextureUtils.getGLMaximumTextureSize();
        Stitcher var3 = new Stitcher(var2, var2, true, 0, this.mipmapLevels);
        this.mapUploadedSprites.clear();
        this.listAnimatedSprites.clear();
        int var4 = Integer.MAX_VALUE;
        Reflector.callVoid(Reflector.ForgeHooksClient_onTextureStitchedPre, new Object[] {this});
        Iterator var5 = this.mapRegisteredSprites.entrySet().iterator();
        TextureAtlasSprite var8;

        while (var5.hasNext() && !this.skipFirst)
        {
            Entry var24 = (Entry)var5.next();
            ResourceLocation var26 = new ResourceLocation((String)var24.getKey());
            var8 = (TextureAtlasSprite)var24.getValue();
            ResourceLocation sheetWidth = this.completeResourceLocation(var26, 0);

            if (var8.getIndexInMap() < 0)
            {
                var8.setIndexInMap(this.counterIndexInMap++);
            }

            if (var8.hasCustomLoader(par1ResourceManager, var26))
            {
                if (!var8.load(par1ResourceManager, var26))
                {
                    var4 = Math.min(var4, Math.min(var8.getIconWidth(), var8.getIconHeight()));
                    var3.addSprite(var8);
                }

                Config.dbg("Custom loader: " + var8);
            }
            else
            {
                try
                {
                    IResource sheetHeight = ShadersTex.loadResource(par1ResourceManager, sheetWidth);
                    BufferedImage[] debugImage = new BufferedImage[1 + this.mipmapLevels];
                    debugImage[0] = ImageIO.read(sheetHeight.getInputStream());
                    TextureMetadataSection var25 = (TextureMetadataSection)sheetHeight.getMetadata("texture");

                    if (var25 != null)
                    {
                        List var28 = var25.getListMipmaps();
                        int var30;

                        if (!var28.isEmpty())
                        {
                            int var18 = debugImage[0].getWidth();
                            var30 = debugImage[0].getHeight();

                            if (MathHelper.roundUpToPowerOfTwo(var18) != var18 || MathHelper.roundUpToPowerOfTwo(var30) != var30)
                            {
                                throw new RuntimeException("Unable to load extra miplevels, source-texture is not power of two");
                            }
                        }

                        Iterator var182 = var28.iterator();

                        while (var182.hasNext())
                        {
                            var30 = ((Integer)var182.next()).intValue();

                            if (var30 > 0 && var30 < debugImage.length - 1 && debugImage[var30] == null)
                            {
                                ResourceLocation var31 = this.completeResourceLocation(var26, var30);

                                try
                                {
                                    debugImage[var30] = ImageIO.read(ShadersTex.loadResource(par1ResourceManager, var31).getInputStream());
                                }
                                catch (IOException var20)
                                {
                                    logger.error("Unable to load miplevel {} from: {}", new Object[] {Integer.valueOf(var30), var31, var20});
                                }
                            }
                        }
                    }

                    AnimationMetadataSection var281 = (AnimationMetadataSection)sheetHeight.getMetadata("animation");
                    var8.loadSprite(debugImage, var281, (float)this.anisotropicFiltering > 1.0F);
                }
                catch (RuntimeException var22)
                {
                    logger.error("Unable to parse metadata from " + sheetWidth, var22);
                    ReflectorForge.FMLClientHandler_trackBrokenTexture(sheetWidth, var22.getMessage());
                    continue;
                }
                catch (IOException var23)
                {
                    logger.error("Using missing texture, unable to load " + sheetWidth + ", " + var23.getClass().getName());
                    ReflectorForge.FMLClientHandler_trackMissingTexture(sheetWidth);
                    continue;
                }

                var4 = Math.min(var4, Math.min(var8.getIconWidth(), var8.getIconHeight()));
                var3.addSprite(var8);
            }
        }

        int var241 = MathHelper.calculateLogBaseTwo(var4);

        if (var241 < 0)
        {
            var241 = 0;
        }

        if (var241 < this.mipmapLevels)
        {
            logger.info("{}: dropping miplevel from {} to {}, because of minTexel: {}", new Object[] {this.basePath, Integer.valueOf(this.mipmapLevels), Integer.valueOf(var241), Integer.valueOf(var4)});
            this.mipmapLevels = var241;
        }

        Iterator var261 = this.mapRegisteredSprites.values().iterator();

        while (var261.hasNext() && !this.skipFirst)
        {
            final TextureAtlasSprite sheetWidth1 = (TextureAtlasSprite)var261.next();

            try
            {
                sheetWidth1.generateMipmaps(this.mipmapLevels);
            }
            catch (Throwable var19)
            {
                CrashReport debugImage1 = CrashReport.makeCrashReport(var19, "Applying mipmap");
                CrashReportCategory var251 = debugImage1.makeCategory("Sprite being mipmapped");
                var251.addCrashSectionCallable("Sprite name", new Callable()
                {
                    public String call()
                    {
                        return sheetWidth1.getIconName();
                    }
                });
                var251.addCrashSectionCallable("Sprite size", new Callable()
                {
                    public String call()
                    {
                        return sheetWidth1.getIconWidth() + " x " + sheetWidth1.getIconHeight();
                    }
                });
                var251.addCrashSectionCallable("Sprite frames", new Callable()
                {
                    public String call()
                    {
                        return sheetWidth1.getFrameCount() + " frames";
                    }
                });
                var251.addCrashSection("Mipmap levels", Integer.valueOf(this.mipmapLevels));
                throw new ReportedException(debugImage1);
            }
        }

        this.missingImage.generateMipmaps(this.mipmapLevels);
        var3.addSprite(this.missingImage);
        this.skipFirst = false;

        try
        {
            var3.doStitch();
        }
        catch (StitcherException var181)
        {
            throw var181;
        }

        Config.dbg("Texture size: " + this.basePath + ", " + var3.getCurrentWidth() + "x" + var3.getCurrentHeight());
        int sheetWidth2 = var3.getCurrentWidth();
        int sheetHeight1 = var3.getCurrentHeight();
        BufferedImage debugImage2 = null;

        if (System.getProperty("saveTextureMap", "false").equalsIgnoreCase("true"))
        {
            debugImage2 = this.makeDebugImage(sheetWidth2, sheetHeight1);
        }

        logger.info("Created: {}x{} {}-atlas", new Object[] {Integer.valueOf(var3.getCurrentWidth()), Integer.valueOf(var3.getCurrentHeight()), this.basePath});

        if (Config.isShaders())
        {
            ShadersTex.allocateTextureMap(this.getGlTextureId(), this.mipmapLevels, var3.getCurrentWidth(), var3.getCurrentHeight(), var3, this);
        }
        else
        {
            TextureUtil.allocateTextureImpl(this.getGlTextureId(), this.mipmapLevels, var3.getCurrentWidth(), var3.getCurrentHeight(), (float)this.anisotropicFiltering);
        }

        HashMap var252 = Maps.newHashMap(this.mapRegisteredSprites);
        Iterator var282 = var3.getStichSlots().iterator();

        while (var282.hasNext())
        {
            var8 = (TextureAtlasSprite)var282.next();

            if (Config.isShaders())
            {
                ShadersTex.setIconName(ShadersTex.setSprite(var8).getIconName());
            }

            String var301 = var8.getIconName();
            var252.remove(var301);
            this.mapUploadedSprites.put(var301, var8);

            try
            {
                if (Config.isShaders())
                {
                    ShadersTex.uploadTexSubForLoadAtlas(var8.getFrameTextureData(0), var8.getIconWidth(), var8.getIconHeight(), var8.getOriginX(), var8.getOriginY(), false, false);
                }
                else
                {
                    TextureUtil.uploadTextureMipmap(var8.getFrameTextureData(0), var8.getIconWidth(), var8.getIconHeight(), var8.getOriginX(), var8.getOriginY(), false, false);
                }

                if (debugImage2 != null)
                {
                    this.addDebugSprite(var8, debugImage2);
                }
            }
            catch (Throwable var21)
            {
                CrashReport var311 = CrashReport.makeCrashReport(var21, "Stitching texture atlas");
                CrashReportCategory var33 = var311.makeCategory("Texture being stitched together");
                var33.addCrashSection("Atlas path", this.basePath);
                var33.addCrashSection("Sprite", var8);
                throw new ReportedException(var311);
            }

            if (var8.hasAnimationMetadata())
            {
                this.listAnimatedSprites.add(var8);
            }
            else
            {
                var8.clearFramesTextureData();
            }
        }

        var282 = var252.values().iterator();

        while (var282.hasNext())
        {
            var8 = (TextureAtlasSprite)var282.next();
            var8.copyFrom(this.missingImage);
        }

        if (debugImage2 != null)
        {
            this.writeDebugImage(debugImage2, "debug_" + this.basePath.replace('/', '_') + ".png");
        }

        Reflector.callVoid(Reflector.ForgeHooksClient_onTextureStitchedPost, new Object[] {this});
    }

    public ResourceLocation completeResourceLocation(ResourceLocation p_147634_1_, int p_147634_2_)
    {
        return this.isAbsoluteLocation(p_147634_1_) ? (p_147634_2_ == 0 ? new ResourceLocation(p_147634_1_.getResourceDomain(), p_147634_1_.getResourcePath() + ".png") : new ResourceLocation(p_147634_1_.getResourceDomain(), p_147634_1_.getResourcePath() + "mipmap" + p_147634_2_ + ".png")) : (p_147634_2_ == 0 ? new ResourceLocation(p_147634_1_.getResourceDomain(), String.format("%s/%s%s", new Object[] {this.basePath, p_147634_1_.getResourcePath(), ".png"})): new ResourceLocation(p_147634_1_.getResourceDomain(), String.format("%s/mipmaps/%s.%d%s", new Object[] {this.basePath, p_147634_1_.getResourcePath(), Integer.valueOf(p_147634_2_), ".png"})));
    }

    private void registerIcons()
    {
        this.mapRegisteredSprites.clear();
        Iterator var1;

        if (this.textureType == 0)
        {
            var1 = Block.blockRegistry.iterator();

            while (var1.hasNext())
            {
                Block var3 = (Block)var1.next();

                if (var3.getMaterial() != Material.air)
                {
                    var3.registerIcons(this);
                }
            }

            Minecraft.getMinecraft().renderGlobal.registerDestroyBlockIcons(this);
            RenderManager.instance.updateIcons(this);
            ConnectedTextures.updateIcons(this);
        }

        if (this.textureType == 1)
        {
            CustomItems.updateIcons(this);
        }

        var1 = Item.itemRegistry.iterator();

        while (var1.hasNext())
        {
            Item var31 = (Item)var1.next();

            if (var31 != null && var31.getSpriteNumber() == this.textureType)
            {
                var31.registerIcons(this);
            }
        }
    }

    public TextureAtlasSprite getAtlasSprite(String par1Str)
    {
        TextureAtlasSprite var2 = (TextureAtlasSprite)this.mapUploadedSprites.get(par1Str);

        if (var2 == null)
        {
            var2 = this.missingImage;
        }

        return var2;
    }

    public void updateAnimations()
    {
        if (Config.isShaders())
        {
            ShadersTex.updatingTex = this.getMultiTexID();
        }

        boolean hasNormal = false;
        boolean hasSpecular = false;
        TextureUtil.bindTexture(this.getGlTextureId());
        Iterator var1 = this.listAnimatedSprites.iterator();

        while (var1.hasNext())
        {
            TextureAtlasSprite i$ = (TextureAtlasSprite)var1.next();

            if (this.textureType == 0)
            {
                if (!this.isTerrainAnimationActive(i$))
                {
                    continue;
                }
            }
            else if (this.textureType == 1 && !this.isItemAnimationActive(i$))
            {
                continue;
            }

            i$.updateAnimation();

            if (i$.spriteNormal != null)
            {
                hasNormal = true;
            }

            if (i$.spriteSpecular != null)
            {
                hasSpecular = true;
            }
        }

        if (Config.isShaders())
        {
            TextureAtlasSprite textureatlassprite;
            Iterator i$1;

            if (hasNormal)
            {
                TextureUtil.bindTexture(this.getMultiTexID().norm);
                i$1 = this.listAnimatedSprites.iterator();

                while (i$1.hasNext())
                {
                    textureatlassprite = (TextureAtlasSprite)i$1.next();

                    if (textureatlassprite.spriteNormal != null && this.isTerrainAnimationActive(textureatlassprite))
                    {
                        if (textureatlassprite == TextureUtils.iconClock || textureatlassprite == TextureUtils.iconCompass)
                        {
                            textureatlassprite.spriteNormal.frameCounter = textureatlassprite.frameCounter;
                        }

                        textureatlassprite.spriteNormal.updateAnimation();
                    }
                }
            }

            if (hasSpecular)
            {
                TextureUtil.bindTexture(this.getMultiTexID().spec);
                i$1 = this.listAnimatedSprites.iterator();

                while (i$1.hasNext())
                {
                    textureatlassprite = (TextureAtlasSprite)i$1.next();

                    if (textureatlassprite.spriteSpecular != null && this.isTerrainAnimationActive(textureatlassprite))
                    {
                        if (textureatlassprite == TextureUtils.iconClock || textureatlassprite == TextureUtils.iconCompass)
                        {
                            textureatlassprite.spriteNormal.frameCounter = textureatlassprite.frameCounter;
                        }

                        textureatlassprite.spriteSpecular.updateAnimation();
                    }
                }
            }

            if (hasNormal || hasSpecular)
            {
                TextureUtil.bindTexture(this.getGlTextureId());
            }
        }

        if (Config.isShaders())
        {
            ShadersTex.updatingTex = null;
        }
    }

    private boolean isItemAnimationActive(TextureAtlasSprite ts)
    {
        return ts != TextureUtils.iconClock && ts != TextureUtils.iconCompass ? Config.isAnimatedItems() : true;
    }

    public IIcon registerIcon(String par1Str)
    {
        if (par1Str == null)
        {
            throw new IllegalArgumentException("Name cannot be null!");
        }
        else if (par1Str.indexOf(92) != -1 && !this.isAbsoluteLocationPath(par1Str))
        {
            throw new IllegalArgumentException("Name cannot contain slashes!");
        }
        else
        {
            Object var2 = (TextureAtlasSprite)this.mapRegisteredSprites.get(par1Str);

            if (var2 == null && this.textureType == 1 && Reflector.ModLoader_getCustomAnimationLogic.exists())
            {
                var2 = Reflector.call(Reflector.ModLoader_getCustomAnimationLogic, new Object[] {par1Str});
            }

            if (var2 == null)
            {
                if (this.textureType == 1)
                {
                    if ("clock".equals(par1Str))
                    {
                        var2 = new TextureClock(par1Str);
                    }
                    else if ("compass".equals(par1Str))
                    {
                        var2 = new TextureCompass(par1Str);
                    }
                    else
                    {
                        var2 = new TextureAtlasSprite(par1Str);
                    }
                }
                else
                {
                    var2 = new TextureAtlasSprite(par1Str);
                }

                this.mapRegisteredSprites.put(par1Str, var2);

                if (var2 instanceof TextureAtlasSprite)
                {
                    TextureAtlasSprite tas = (TextureAtlasSprite)var2;

                    if (tas.getIndexInMap() < 0)
                    {
                        tas.setIndexInMap(this.counterIndexInMap++);
                    }
                }
            }

            return (IIcon)var2;
        }
    }

    public int getTextureType()
    {
        return this.textureType;
    }

    public void tick()
    {
        this.updateAnimations();
    }

    public void setMipmapLevels(int p_147633_1_)
    {
        this.mipmapLevels = p_147633_1_;
    }

    public void setAnisotropicFiltering(int p_147632_1_)
    {
        this.anisotropicFiltering = p_147632_1_;
    }

    public TextureAtlasSprite getTextureExtry(String name)
    {
        return (TextureAtlasSprite)this.mapRegisteredSprites.get(name);
    }

    public boolean setTextureEntry(String name, TextureAtlasSprite entry)
    {
        if (!this.mapRegisteredSprites.containsKey(name))
        {
            this.mapRegisteredSprites.put(name, entry);

            if (entry.getIndexInMap() < 0)
            {
                entry.setIndexInMap(this.counterIndexInMap++);
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean setTextureEntry(TextureAtlasSprite entry)
    {
        return this.setTextureEntry(entry.getIconName(), entry);
    }

    public String getBasePath()
    {
        return this.basePath;
    }

    public int getMipmapLevels()
    {
        return this.mipmapLevels;
    }

    private boolean isAbsoluteLocation(ResourceLocation loc)
    {
        String path = loc.getResourcePath();
        return this.isAbsoluteLocationPath(path);
    }

    private boolean isAbsoluteLocationPath(String resPath)
    {
        String path = resPath.toLowerCase();
        return path.startsWith("mcpatcher/") || path.startsWith("optifine/");
    }

    public TextureAtlasSprite getIconSafe(String name)
    {
        return (TextureAtlasSprite)this.mapRegisteredSprites.get(name);
    }

    private int getStandardTileSize(Collection icons)
    {
        int[] sizeCounts = new int[16];
        Iterator mostUsedPo2 = icons.iterator();
        int value;
        int count;

        while (mostUsedPo2.hasNext())
        {
            TextureAtlasSprite mostUsedCount = (TextureAtlasSprite)mostUsedPo2.next();

            if (mostUsedCount != null)
            {
                value = TextureUtils.getPowerOfTwo(mostUsedCount.getWidth());
                count = TextureUtils.getPowerOfTwo(mostUsedCount.getHeight());
                int po2 = Math.max(value, count);

                if (po2 < sizeCounts.length)
                {
                    ++sizeCounts[po2];
                }
            }
        }

        int var8 = 4;
        int var9 = 0;

        for (value = 0; value < sizeCounts.length; ++value)
        {
            count = sizeCounts[value];

            if (count > var9)
            {
                var8 = value;
                var9 = count;
            }
        }

        if (var8 < 4)
        {
            var8 = 4;
        }

        value = TextureUtils.twoToPower(var8);
        return value;
    }

    private void updateIconGrid(int sheetWidth, int sheetHeight)
    {
        this.iconGridCountX = -1;
        this.iconGridCountY = -1;
        this.iconGrid = null;

        if (this.iconGridSize > 0)
        {
            this.iconGridCountX = sheetWidth / this.iconGridSize;
            this.iconGridCountY = sheetHeight / this.iconGridSize;
            this.iconGrid = new TextureAtlasSprite[this.iconGridCountX * this.iconGridCountY];
            this.iconGridSizeU = 1.0D / (double)this.iconGridCountX;
            this.iconGridSizeV = 1.0D / (double)this.iconGridCountY;
            Iterator it = this.mapUploadedSprites.values().iterator();

            while (it.hasNext())
            {
                TextureAtlasSprite ts = (TextureAtlasSprite)it.next();
                double deltaU = 0.5D / (double)sheetWidth;
                double deltaV = 0.5D / (double)sheetHeight;
                double uMin = (double)Math.min(ts.getMinU(), ts.getMaxU()) + deltaU;
                double vMin = (double)Math.min(ts.getMinV(), ts.getMaxV()) + deltaV;
                double uMax = (double)Math.max(ts.getMinU(), ts.getMaxU()) - deltaU;
                double vMax = (double)Math.max(ts.getMinV(), ts.getMaxV()) - deltaV;
                int iuMin = (int)(uMin / this.iconGridSizeU);
                int ivMin = (int)(vMin / this.iconGridSizeV);
                int iuMax = (int)(uMax / this.iconGridSizeU);
                int ivMax = (int)(vMax / this.iconGridSizeV);

                for (int iu = iuMin; iu <= iuMax; ++iu)
                {
                    if (iu >= 0 && iu < this.iconGridCountX)
                    {
                        for (int iv = ivMin; iv <= ivMax; ++iv)
                        {
                            if (iv >= 0 && iv < this.iconGridCountX)
                            {
                                int index = iv * this.iconGridCountX + iu;
                                this.iconGrid[index] = ts;
                            }
                            else
                            {
                                Config.warn("Invalid grid V: " + iv + ", icon: " + ts.getIconName());
                            }
                        }
                    }
                    else
                    {
                        Config.warn("Invalid grid U: " + iu + ", icon: " + ts.getIconName());
                    }
                }
            }
        }
    }

    public TextureAtlasSprite getIconByUV(double u, double v)
    {
        if (this.iconGrid == null)
        {
            return null;
        }
        else
        {
            int iu = (int)(u / this.iconGridSizeU);
            int iv = (int)(v / this.iconGridSizeV);
            int index = iv * this.iconGridCountX + iu;
            return index >= 0 && index <= this.iconGrid.length ? this.iconGrid[index] : null;
        }
    }

    public TextureAtlasSprite getMissingSprite()
    {
        return this.missingImage;
    }

    public int getMaxTextureIndex()
    {
        return this.mapRegisteredSprites.size();
    }

    private boolean isTerrainAnimationActive(TextureAtlasSprite ts)
    {
        return ts != TextureUtils.iconWaterStill && ts != TextureUtils.iconWaterFlow ? (ts != TextureUtils.iconLavaStill && ts != TextureUtils.iconLavaFlow ? (ts != TextureUtils.iconFireLayer0 && ts != TextureUtils.iconFireLayer1 ? (ts == TextureUtils.iconPortal ? Config.isAnimatedPortal() : (ts != TextureUtils.iconClock && ts != TextureUtils.iconCompass ? Config.isAnimatedTerrain() : true)) : Config.isAnimatedFire()) : Config.isAnimatedLava()) : Config.isAnimatedWater();
    }

    public int getCountRegisteredSprites()
    {
        return this.counterIndexInMap;
    }

    public void loadTextureSafe(IResourceManager rm)
    {
        try
        {
            this.loadTexture(rm);
        }
        catch (IOException var3)
        {
            Config.warn("Error loading texture map: " + this.basePath);
            var3.printStackTrace();
        }
    }

    private BufferedImage makeDebugImage(int sheetWidth, int sheetHeight)
    {
        BufferedImage image = new BufferedImage(sheetWidth, sheetHeight, 2);
        Graphics2D g = image.createGraphics();
        g.setPaint(new Color(255, 255, 0));
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        return image;
    }

    private void addDebugSprite(TextureAtlasSprite ts, BufferedImage image)
    {
        if (ts.getFrameCount() < 1)
        {
            Config.warn("Debug sprite has no data: " + ts.getIconName());
        }
        else
        {
            int[] data = ts.getFrameTextureData(0)[0];
            image.setRGB(ts.getOriginX(), ts.getOriginY(), ts.getIconWidth(), ts.getIconHeight(), data, 0, ts.getIconWidth());
        }
    }

    private void writeDebugImage(BufferedImage image, String pngPath)
    {
        try
        {
            ImageIO.write(image, "png", new File(Config.getMinecraft().mcDataDir, pngPath));
        }
        catch (Exception var4)
        {
            var4.printStackTrace();
        }
    }
}

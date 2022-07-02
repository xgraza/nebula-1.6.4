package net.minecraft.src;

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
import javax.imageio.ImageIO;

public class TextureMap extends AbstractTexture implements TickableTextureObject, IconRegister
{
    public static final ResourceLocation locationBlocksTexture = new ResourceLocation("textures/atlas/blocks.png");
    public static final ResourceLocation locationItemsTexture = new ResourceLocation("textures/atlas/items.png");
    private final List listAnimatedSprites = Lists.newArrayList();
    private final Map mapRegisteredSprites = Maps.newHashMap();
    private final Map mapUploadedSprites = Maps.newHashMap();

    /** 0 = terrain.png, 1 = items.png */
    public final int textureType;
    public final String basePath;
    private final TextureAtlasSprite missingImage = new TextureAtlasSprite("missingno");
    public static TextureMap textureMapBlocks = null;
    public static TextureMap textureMapItems = null;
    private TextureAtlasSprite[] iconGrid = null;
    private int iconGridSize = -1;
    private int iconGridCountX = -1;
    private int iconGridCountY = -1;
    private double iconGridSizeU = -1.0D;
    private double iconGridSizeV = -1.0D;

    public TextureMap(int par1, String par2Str)
    {
        this.textureType = par1;
        this.basePath = par2Str;

        if (this.textureType == 0)
        {
            textureMapBlocks = this;
        }

        if (this.textureType == 0)
        {
            textureMapItems = this;
        }

        this.registerIcons();
    }

    private void initMissingImage()
    {
        this.missingImage.setFramesTextureData(Lists.newArrayList(new int[][] {TextureUtil.missingTextureData}));
        this.missingImage.setIconWidth(16);
        this.missingImage.setIconHeight(16);
        this.missingImage.setIndexInMap(0);
    }

    public void loadTexture(ResourceManager par1ResourceManager) throws IOException
    {
        this.initMissingImage();
        this.loadTextureAtlas(par1ResourceManager);
    }

    public void loadTextureAtlas(ResourceManager par1ResourceManager)
    {
        Config.dbg("Loading texture map: " + this.basePath);
        Iterator var2 = this.mapUploadedSprites.values().iterator();

        while (var2.hasNext())
        {
            TextureAtlasSprite var3 = (TextureAtlasSprite)var2.next();
            var3.deleteOwnTexture();
        }

        this.registerIcons();
        Collection var21 = this.mapRegisteredSprites.values();
        this.iconGridSize = this.getStandardTileSize(var21);
        Config.dbg("Icon grid size: " + this.basePath + ", " + this.iconGridSize);
        int var22 = Minecraft.getGLMaximumTextureSize();
        Stitcher var4 = new Stitcher(var22, var22, true);
        var4.minTileDimension = this.iconGridSize;
        this.mapUploadedSprites.clear();
        this.listAnimatedSprites.clear();
        Reflector.callVoid(Reflector.ForgeHooksClient_onTextureStitchedPre, new Object[] {this});
        Iterator var5 = this.mapRegisteredSprites.entrySet().iterator();

        while (var5.hasNext())
        {
            Entry var6 = (Entry)var5.next();
            ResourceLocation var7 = new ResourceLocation((String)var6.getKey());
            TextureAtlasSprite var8 = (TextureAtlasSprite)var6.getValue();
            ResourceLocation var9 = new ResourceLocation(var7.getResourceDomain(), String.format("%s/%s%s", new Object[] {this.basePath, var7.getResourcePath(), ".png"}));

            if (this.isAbsoluteLocation(var7))
            {
                var9 = new ResourceLocation(var7.getResourceDomain(), var7.getResourcePath() + ".png");
            }

            try
            {
                if (!var8.load(par1ResourceManager, var9))
                {
                    continue;
                }
            }
            catch (RuntimeException var19)
            {
                Minecraft.getMinecraft().getLogAgent().logSevere(String.format("Unable to parse animation metadata from %s: %s", new Object[] {var9, var19.getMessage()}));
                continue;
            }
            catch (IOException var20)
            {
                Minecraft.getMinecraft().getLogAgent().logSevere("Using missing texture, unable to load: " + var9);
                continue;
            }

            var4.addSprite(var8);
        }

        var4.addSprite(this.missingImage);

        try
        {
            var4.doStitch();
        }
        catch (StitcherException var17)
        {
            throw var17;
        }

        Config.dbg("Texture size: " + this.basePath + ", " + var4.getCurrentWidth() + "x" + var4.getCurrentHeight());
        int var23 = var4.getCurrentWidth();
        int var24 = var4.getCurrentHeight();
        BufferedImage var25 = null;

        if (System.getProperty("saveTextureMap", "false").equalsIgnoreCase("true"))
        {
            var25 = this.makeDebugImage(var23, var24);
        }

        TextureUtil.allocateTexture(this.getGlTextureId(), var4.getCurrentWidth(), var4.getCurrentHeight());
        boolean var26 = Config.isUseMipmaps() && this.textureType == 0;
        TextureUtils.setupTexture(var23, var24, this.iconGridSize, var26);
        HashMap var10 = Maps.newHashMap(this.mapRegisteredSprites);
        Iterator var11 = var4.getStichSlots().iterator();
        TextureAtlasSprite var12;

        while (var11.hasNext())
        {
            var12 = (TextureAtlasSprite)var11.next();
            String var13 = var12.getIconName();
            var10.remove(var13);
            this.mapUploadedSprites.put(var13, var12);

            try
            {
                var12.sheetWidth = var23;
                var12.sheetHeight = var24;
                boolean var27 = Config.isUseMipmaps() && this.textureType == 0;
                var12.setMipmapActive(var27);
                var12.uploadFrameTexture();

                if (Config.isMultiTexture())
                {
                    var12.bindUploadOwnTexture();
                    TextureUtil.bindTexture(this.getGlTextureId());
                }

                if (var25 != null)
                {
                    this.addDebugSprite(var12, var25);
                }
            }
            catch (Throwable var18)
            {
                CrashReport var15 = CrashReport.makeCrashReport(var18, "Stitching texture atlas");
                CrashReportCategory var16 = var15.makeCategory("Texture being stitched together");
                var16.addCrashSection("Atlas path", this.basePath);
                var16.addCrashSection("Sprite", var12);
                throw new ReportedException(var15);
            }

            if (var12.hasAnimationMetadata())
            {
                this.listAnimatedSprites.add(var12);
            }
            else
            {
                var12.clearFramesTextureData();
            }
        }

        var11 = var10.values().iterator();

        while (var11.hasNext())
        {
            var12 = (TextureAtlasSprite)var11.next();
            var12.copyFrom(this.missingImage);
        }

        this.updateIconGrid(var23, var24);

        if (var25 != null)
        {
            this.writeDebugImage(var25, "debug_" + this.basePath.replace('/', '_') + ".png");
        }

        Reflector.callVoid(Reflector.ForgeHooksClient_onTextureStitchedPost, new Object[] {this});
    }

    private void registerIcons()
    {
        this.mapRegisteredSprites.clear();
        int var1;
        int var2;

        if (this.textureType == 0)
        {
            Block[] var3 = Block.blocksList;
            var1 = var3.length;

            for (var2 = 0; var2 < var1; ++var2)
            {
                Block var4 = var3[var2];

                if (var4 != null)
                {
                    var4.registerIcons(this);
                }
            }

            Minecraft.getMinecraft().renderGlobal.registerDestroyBlockIcons(this);
            RenderManager.instance.updateIcons(this);
            ConnectedTextures.updateIcons(this);
        }

        Item[] var5 = Item.itemsList;
        var1 = var5.length;

        for (var2 = 0; var2 < var1; ++var2)
        {
            Item var6 = var5[var2];

            if (var6 != null && var6.getSpriteNumber() == this.textureType)
            {
                var6.registerIcons(this);
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
        TextureUtil.bindTexture(this.getGlTextureId());
        Iterator var1 = this.listAnimatedSprites.iterator();

        while (var1.hasNext())
        {
            TextureAtlasSprite var2 = (TextureAtlasSprite)var1.next();

            if (this.textureType == 0)
            {
                if (!this.isTerrainAnimationActive(var2))
                {
                    continue;
                }
            }
            else if (this.textureType == 1 && !Config.isAnimatedItems())
            {
                continue;
            }

            var2.updateAnimation();
        }

        if (Config.isMultiTexture())
        {
            for (int var4 = 0; var4 < this.listAnimatedSprites.size(); ++var4)
            {
                TextureAtlasSprite var3 = (TextureAtlasSprite)this.listAnimatedSprites.get(var4);

                if (this.isTerrainAnimationActive(var3))
                {
                    var3.uploadOwnAnimation();
                }
            }
        }
    }

    public Icon registerIcon(String par1Str)
    {
        if (par1Str == null)
        {
            (new RuntimeException("Don\'t register null!")).printStackTrace();
            par1Str = "null";
        }

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
                TextureAtlasSprite var3 = (TextureAtlasSprite)var2;
                var3.setIndexInMap(this.mapRegisteredSprites.size());
            }
        }

        return (Icon)var2;
    }

    public int getTextureType()
    {
        return this.textureType;
    }

    public void tick()
    {
        this.updateAnimations();
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
            entry.setIndexInMap(this.mapRegisteredSprites.size());
            return true;
        }
        else
        {
            return false;
        }
    }

    private boolean isAbsoluteLocation(ResourceLocation loc)
    {
        String name = loc.getResourcePath().toLowerCase();
        return name.startsWith("mcpatcher/") || name.startsWith("optifine/");
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
                double uMin = (double)Math.min(ts.getMinU(), ts.getMaxU());
                double vMin = (double)Math.min(ts.getMinV(), ts.getMaxV());
                double uMax = (double)Math.max(ts.getMinU(), ts.getMaxU());
                double vMax = (double)Math.max(ts.getMinV(), ts.getMaxV());
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
        return ts != TextureUtils.iconWaterStill && ts != TextureUtils.iconWaterFlow ? (ts != TextureUtils.iconLavaStill && ts != TextureUtils.iconLavaFlow ? (ts != TextureUtils.iconFireLayer0 && ts != TextureUtils.iconFireLayer1 ? (ts == TextureUtils.iconPortal ? Config.isAnimatedPortal() : Config.isAnimatedTerrain()) : Config.isAnimatedFire()) : Config.isAnimatedLava()) : Config.isAnimatedWater();
    }

    public void loadTextureSafe(ResourceManager rm)
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
            int[] data = ts.getFrameTextureData(0);
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

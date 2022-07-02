package net.minecraft.src;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.nio.IntBuffer;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.src.TextureUtils$1;
import net.minecraft.src.TextureUtils$2;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GLContext;

public class TextureUtils
{
    public static final String texGrassTop = "grass_top";
    public static final String texStone = "stone";
    public static final String texDirt = "dirt";
    public static final String texGrassSide = "grass_side";
    public static final String texStoneslabSide = "stone_slab_side";
    public static final String texStoneslabTop = "stone_slab_top";
    public static final String texBedrock = "bedrock";
    public static final String texSand = "sand";
    public static final String texGravel = "gravel";
    public static final String texLogOak = "log_oak";
    public static final String texLogOakTop = "log_oak_top";
    public static final String texGoldOre = "gold_ore";
    public static final String texIronOre = "iron_ore";
    public static final String texCoalOre = "coal_ore";
    public static final String texObsidian = "obsidian";
    public static final String texGrassSideOverlay = "grass_side_overlay";
    public static final String texSnow = "snow";
    public static final String texGrassSideSnowed = "grass_side_snowed";
    public static final String texMyceliumSide = "mycelium_side";
    public static final String texMyceliumTop = "mycelium_top";
    public static final String texDiamondOre = "diamond_ore";
    public static final String texRedstoneOre = "redstone_ore";
    public static final String texLapisOre = "lapis_ore";
    public static final String texLeavesOak = "leaves_oak";
    public static final String texLeavesOakOpaque = "leaves_oak_opaque";
    public static final String texLeavesJungle = "leaves_jungle";
    public static final String texLeavesJungleOpaque = "leaves_jungle_opaque";
    public static final String texCactusSide = "cactus_side";
    public static final String texClay = "clay";
    public static final String texFarmlandWet = "farmland_wet";
    public static final String texFarmlandDry = "farmland_dry";
    public static final String texNetherrack = "netherrack";
    public static final String texSoulSand = "soul_sand";
    public static final String texGlowstone = "glowstone";
    public static final String texLogSpruce = "log_spruce";
    public static final String texLogBirch = "log_birch";
    public static final String texLeavesSpruce = "leaves_spruce";
    public static final String texLeavesSpruceOpaque = "leaves_spruce_opaque";
    public static final String texLogJungle = "log_jungle";
    public static final String texEndStone = "end_stone";
    public static final String texSandstoneTop = "sandstone_top";
    public static final String texSandstoneBottom = "sandstone_bottom";
    public static final String texRedstoneLampOff = "redstone_lamp_off";
    public static final String texRedstoneLampOn = "redstone_lamp_on";
    public static final String texWaterStill = "water_still";
    public static final String texWaterFlow = "water_flow";
    public static final String texLavaStill = "lava_still";
    public static final String texLavaFlow = "lava_flow";
    public static final String texFireLayer0 = "fire_layer_0";
    public static final String texFireLayer1 = "fire_layer_1";
    public static final String texPortal = "portal";
    public static Icon iconGrassTop;
    public static Icon iconGrassSide;
    public static Icon iconGrassSideOverlay;
    public static Icon iconSnow;
    public static Icon iconGrassSideSnowed;
    public static Icon iconMyceliumSide;
    public static Icon iconMyceliumTop;
    public static Icon iconWaterStill;
    public static Icon iconWaterFlow;
    public static Icon iconLavaStill;
    public static Icon iconLavaFlow;
    public static Icon iconPortal;
    public static Icon iconFireLayer0;
    public static Icon iconFireLayer1;
    private static IntBuffer staticBuffer = GLAllocation.createDirectIntBuffer(256);

    private static Set makeAtlasNames()
    {
        HashSet set = new HashSet();
        set.add("/terrain.png");
        set.add("/gui/items.png");
        set.add("/ctm.png");
        set.add("/eloraam/world/world1.png");
        set.add("/gfx/buildcraft/blocks/blocks.png");
        return set;
    }

    public static void update()
    {
        TextureMap mapBlocks = TextureMap.textureMapBlocks;

        if (mapBlocks != null)
        {
            iconGrassTop = mapBlocks.getIconSafe("grass_top");
            iconGrassSide = mapBlocks.getIconSafe("grass_side");
            iconGrassSideOverlay = mapBlocks.getIconSafe("grass_side_overlay");
            iconSnow = mapBlocks.getIconSafe("snow");
            iconGrassSideSnowed = mapBlocks.getIconSafe("grass_side_snowed");
            iconMyceliumSide = mapBlocks.getIconSafe("mycelium_side");
            iconMyceliumTop = mapBlocks.getIconSafe("mycelium_top");
            iconWaterStill = mapBlocks.getIconSafe("water_still");
            iconWaterFlow = mapBlocks.getIconSafe("water_flow");
            iconLavaStill = mapBlocks.getIconSafe("lava_still");
            iconLavaFlow = mapBlocks.getIconSafe("lava_flow");
            iconFireLayer0 = mapBlocks.getIconSafe("fire_layer_0");
            iconFireLayer1 = mapBlocks.getIconSafe("fire_layer_1");
            iconPortal = mapBlocks.getIconSafe("portal");
        }
    }

    public static BufferedImage fixTextureDimensions(String name, BufferedImage bi)
    {
        if (name.startsWith("/mob/zombie") || name.startsWith("/mob/pigzombie"))
        {
            int width = bi.getWidth();
            int height = bi.getHeight();

            if (width == height * 2)
            {
                BufferedImage scaledImage = new BufferedImage(width, height * 2, 2);
                Graphics2D gr = scaledImage.createGraphics();
                gr.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                gr.drawImage(bi, 0, 0, width, height, (ImageObserver)null);
                return scaledImage;
            }
        }

        return bi;
    }

    public static TextureAtlasSprite getTextureAtlasSprite(Icon icon)
    {
        return icon instanceof TextureAtlasSprite ? (TextureAtlasSprite)icon : null;
    }

    public static int ceilPowerOfTwo(int val)
    {
        int i;

        for (i = 1; i < val; i *= 2)
        {
            ;
        }

        return i;
    }

    public static int getPowerOfTwo(int val)
    {
        int i = 1;
        int po2;

        for (po2 = 0; i < val; ++po2)
        {
            i *= 2;
        }

        return po2;
    }

    public static int twoToPower(int power)
    {
        int val = 1;

        for (int i = 0; i < power; ++i)
        {
            val *= 2;
        }

        return val;
    }

    public static void refreshBlockTextures()
    {
        Config.dbg("*** Reloading block textures ***");
        TextureMap.textureMapBlocks.loadTextureSafe(Config.getResourceManager());
        update();
        NaturalTextures.update();
        TextureMap.textureMapBlocks.updateAnimations();
    }

    public static TextureObject getTexture(String path)
    {
        return getTexture(new ResourceLocation(path));
    }

    public static TextureObject getTexture(ResourceLocation loc)
    {
        TextureObject tex = Config.getTextureManager().getTexture(loc);

        if (tex != null)
        {
            return tex;
        }
        else if (!Config.hasResource(loc))
        {
            return null;
        }
        else
        {
            SimpleTexture tex1 = new SimpleTexture(loc);
            Config.getTextureManager().loadTexture(loc, tex1);
            return tex1;
        }
    }

    public static void resourcesReloaded(ResourceManager rm)
    {
        if (TextureMap.textureMapBlocks != null)
        {
            Config.dbg("*** Reloading custom textures ***");
            CustomSky.reset();
            TextureAnimations.reset();
            update();
            NaturalTextures.update();
            TextureAnimations.update();
            CustomColorizer.update();
            CustomSky.update();
            RandomMobs.resetTextures();
            Config.updateTexturePackClouds();
            Config.getTextureManager().tick();
        }
    }

    public static void refreshTextureMaps(ResourceManager rm)
    {
        TextureMap.textureMapBlocks.loadTextureSafe(rm);
        TextureMap.textureMapItems.loadTextureSafe(rm);
        update();
        NaturalTextures.update();
    }

    public static void registerResourceListener()
    {
        ResourceManager rm = Config.getResourceManager();

        if (rm instanceof ReloadableResourceManager)
        {
            ReloadableResourceManager tto = (ReloadableResourceManager)rm;
            TextureUtils$1 ttol = new TextureUtils$1();
            tto.registerReloadListener(ttol);
        }

        TextureUtils$2 tto1 = new TextureUtils$2();
        ResourceLocation ttol1 = new ResourceLocation("optifine/TickableTextures");
        Config.getTextureManager().loadTickableTexture(ttol1, tto1);
    }

    public static String fixResourcePath(String path, String basePath)
    {
        String strAssMc = "assets/minecraft/";

        if (path.startsWith(strAssMc))
        {
            path = path.substring(strAssMc.length());
            return path;
        }
        else if (path.startsWith("./"))
        {
            path = path.substring(2);

            if (!basePath.endsWith("/"))
            {
                basePath = basePath + "/";
            }

            path = basePath + path;
            return path;
        }
        else
        {
            String strMcpatcher = "mcpatcher/";

            if (path.startsWith("~/"))
            {
                path = path.substring(2);
                path = strMcpatcher + path;
                return path;
            }
            else if (path.startsWith("/"))
            {
                path = strMcpatcher + path.substring(1);
                return path;
            }
            else
            {
                return path;
            }
        }
    }

    public static String getBasePath(String path)
    {
        int pos = path.lastIndexOf(47);
        return pos < 0 ? "" : path.substring(0, pos);
    }

    public static void setupTexture(int width, int height, int gridSize, boolean useMipmaps)
    {
        int minFilter = 9728;
        short magFilter = 9728;
        boolean mipmapsActive = useMipmaps && Config.isUseMipmaps();

        if (mipmapsActive)
        {
            minFilter = Config.getMipmapType();
        }

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, minFilter);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, magFilter);
        char wrapMode = 33071;
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, wrapMode);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, wrapMode);

        if (mipmapsActive)
        {
            updateMaxMipmapLevel(width, height, gridSize);
            Mipmaps.allocateMipmapTextures(width, height, "");
        }

        updateAnisotropicLevel();
    }

    public static void updateMaxMipmapLevel(int width, int height, int gridSize)
    {
        if (Config.getMipmapLevel() > 0)
        {
            if (GLContext.getCapabilities().OpenGL12)
            {
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_BASE_LEVEL, 0);
                int mipmapLevel = Config.getMipmapLevel();

                if (mipmapLevel >= 4)
                {
                    int minDim = Math.min(width, height);
                    mipmapLevel = getMaxMipmapLevel(minDim);

                    if (gridSize > 1)
                    {
                        int gridSizePo2 = getPowerOfTwo(gridSize);
                        mipmapLevel = gridSizePo2;
                    }

                    if (mipmapLevel < 0)
                    {
                        mipmapLevel = 0;
                    }
                }

                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, mipmapLevel);
            }
        }
    }

    public static void updateAnisotropicLevel()
    {
        if (GLContext.getCapabilities().GL_EXT_texture_filter_anisotropic)
        {
            float maxLevel = GL11.glGetFloat(34047);
            float level = (float)Config.getAnisotropicFilterLevel();
            level = Math.min(level, maxLevel);
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, 34046, level);
        }
    }

    private static int getMaxMipmapLevel(int size)
    {
        int level;

        for (level = 0; size > 0; ++level)
        {
            size /= 2;
        }

        return level - 1;
    }

    public static IntBuffer getStaticBuffer(int w, int h)
    {
        int len = w * h;

        if (staticBuffer == null || staticBuffer.capacity() < len)
        {
            len = ceilPowerOfTwo(len);
            staticBuffer = GLAllocation.createDirectIntBuffer(len);
        }

        return staticBuffer;
    }
}

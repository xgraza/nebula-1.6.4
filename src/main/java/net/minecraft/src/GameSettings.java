package net.minecraft.src;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

public class GameSettings
{
    private static final String[] RENDER_DISTANCES = new String[] {"options.renderDistance.far", "options.renderDistance.normal", "options.renderDistance.short", "options.renderDistance.tiny"};
    private static final String[] DIFFICULTIES = new String[] {"options.difficulty.peaceful", "options.difficulty.easy", "options.difficulty.normal", "options.difficulty.hard"};

    /** GUI scale values */
    private static final String[] GUISCALES = new String[] {"options.guiScale.auto", "options.guiScale.small", "options.guiScale.normal", "options.guiScale.large"};
    private static final String[] CHAT_VISIBILITIES = new String[] {"options.chat.visibility.full", "options.chat.visibility.system", "options.chat.visibility.hidden"};
    private static final String[] PARTICLES = new String[] {"options.particles.all", "options.particles.decreased", "options.particles.minimal"};

    /** Limit framerate labels */
    private static final String[] LIMIT_FRAMERATES = new String[] {"performance.max", "performance.balanced", "performance.powersaver"};
    private static final String[] AMBIENT_OCCLUSIONS = new String[] {"options.ao.off", "options.ao.min", "options.ao.max"};
    public float musicVolume = 1.0F;
    public float soundVolume = 1.0F;
    public float mouseSensitivity = 0.5F;
    public boolean invertMouse;
    public int renderDistance = 1;
    public boolean viewBobbing = true;
    public boolean anaglyph;

    /** Advanced OpenGL */
    public boolean advancedOpengl;
    public int limitFramerate = 1;
    public boolean fancyGraphics = true;

    /** Smooth Lighting */
    public int ambientOcclusion = 2;

    /** Clouds flag */
    public boolean clouds = true;
    public int ofRenderDistanceFine = 128;
    public int ofLimitFramerateFine = 0;
    public int ofFogType = 1;
    public float ofFogStart = 0.8F;
    public int ofMipmapLevel = 0;
    public int ofMipmapType = 0;
    public boolean ofLoadFar = false;
    public int ofPreloadedChunks = 0;
    public boolean ofOcclusionFancy = false;
    public boolean ofSmoothFps = false;
    public boolean ofSmoothWorld = Config.isSingleProcessor();
    public boolean ofLazyChunkLoading = Config.isSingleProcessor();
    public float ofAoLevel = 1.0F;
    public int ofAaLevel = 0;
    public int ofAfLevel = 1;
    public int ofClouds = 0;
    public float ofCloudsHeight = 0.0F;
    public int ofTrees = 0;
    public int ofGrass = 0;
    public int ofRain = 0;
    public int ofWater = 0;
    public int ofDroppedItems = 0;
    public int ofBetterGrass = 3;
    public int ofAutoSaveTicks = 4000;
    public boolean ofLagometer = false;
    public boolean ofProfiler = false;
    public boolean ofWeather = true;
    public boolean ofSky = true;
    public boolean ofStars = true;
    public boolean ofSunMoon = true;
    public int ofChunkUpdates = 1;
    public int ofChunkLoading = 0;
    public boolean ofChunkUpdatesDynamic = false;
    public int ofTime = 0;
    public boolean ofClearWater = false;
    public boolean ofDepthFog = true;
    public boolean ofBetterSnow = false;
    public String ofFullscreenMode = "Default";
    public boolean ofSwampColors = true;
    public boolean ofRandomMobs = true;
    public boolean ofSmoothBiomes = true;
    public boolean ofCustomFonts = true;
    public boolean ofCustomColors = true;
    public boolean ofCustomSky = true;
    public boolean ofShowCapes = true;
    public int ofConnectedTextures = 2;
    public boolean ofNaturalTextures = false;
    public boolean ofFastMath = false;
    public int ofAnimatedWater = 0;
    public int ofAnimatedLava = 0;
    public boolean ofAnimatedFire = true;
    public boolean ofAnimatedPortal = true;
    public boolean ofAnimatedRedstone = true;
    public boolean ofAnimatedExplosion = true;
    public boolean ofAnimatedFlame = true;
    public boolean ofAnimatedSmoke = true;
    public boolean ofVoidParticles = true;
    public boolean ofWaterParticles = true;
    public boolean ofRainSplash = true;
    public boolean ofPortalParticles = true;
    public boolean ofPotionParticles = true;
    public boolean ofDrippingWaterLava = true;
    public boolean ofAnimatedTerrain = true;
    public boolean ofAnimatedItems = true;
    public boolean ofAnimatedTextures = true;
    public static final int DEFAULT = 0;
    public static final int FAST = 1;
    public static final int FANCY = 2;
    public static final int OFF = 3;
    public static final int ANIM_ON = 0;
    public static final int ANIM_GENERATED = 1;
    public static final int ANIM_OFF = 2;
    public static final int CL_DEFAULT = 0;
    public static final int CL_SMOOTH = 1;
    public static final int CL_THREADED = 2;
    public static final String DEFAULT_STR = "Default";
    public KeyBinding ofKeyBindZoom;

    /** The name of the selected texture pack. */
    public String skin = "Default";
    public int chatVisibility;
    public boolean chatColours = true;
    public boolean chatLinks = true;
    public boolean chatLinksPrompt = true;
    public float chatOpacity = 1.0F;
    public boolean serverTextures = true;
    public boolean snooperEnabled = true;
    public boolean fullScreen;
    public boolean enableVsync = true;
    public boolean hideServerAddress;

    /**
     * Whether to show advanced information on item tooltips, toggled by F3+H
     */
    public boolean advancedItemTooltips;

    /** Whether to pause when the game loses focus, toggled by F3+P */
    public boolean pauseOnLostFocus = true;

    /** Whether to show your cape */
    public boolean showCape = true;
    public boolean touchscreen;
    public int overrideWidth;
    public int overrideHeight;
    public boolean heldItemTooltips = true;
    public float chatScale = 1.0F;
    public float chatWidth = 1.0F;
    public float chatHeightUnfocused = 0.44366196F;
    public float chatHeightFocused = 1.0F;
    public KeyBinding keyBindForward = new KeyBinding("key.forward", 17);
    public KeyBinding keyBindLeft = new KeyBinding("key.left", 30);
    public KeyBinding keyBindBack = new KeyBinding("key.back", 31);
    public KeyBinding keyBindRight = new KeyBinding("key.right", 32);
    public KeyBinding keyBindJump = new KeyBinding("key.jump", 57);
    public KeyBinding keyBindInventory = new KeyBinding("key.inventory", 18);
    public KeyBinding keyBindDrop = new KeyBinding("key.drop", 16);
    public KeyBinding keyBindChat = new KeyBinding("key.chat", 20);
    public KeyBinding keyBindSneak = new KeyBinding("key.sneak", 42);
    public KeyBinding keyBindAttack = new KeyBinding("key.attack", -100);
    public KeyBinding keyBindUseItem = new KeyBinding("key.use", -99);
    public KeyBinding keyBindPlayerList = new KeyBinding("key.playerlist", 15);
    public KeyBinding keyBindPickBlock = new KeyBinding("key.pickItem", -98);
    public KeyBinding keyBindCommand = new KeyBinding("key.command", 53);
    public KeyBinding[] keyBindings;
    protected Minecraft mc;
    private File optionsFile;
    public int difficulty;
    public boolean hideGUI;
    public int thirdPersonView;

    /** true if debug info should be displayed instead of version */
    public boolean showDebugInfo;
    public boolean showDebugProfilerChart;

    /** The lastServer string. */
    public String lastServer;

    /** No clipping for singleplayer */
    public boolean noclip;

    /** Smooth Camera Toggle */
    public boolean smoothCamera;
    public boolean debugCamEnable;

    /** No clipping movement rate */
    public float noclipRate;

    /** Change rate for debug camera */
    public float debugCamRate;
    public float fovSetting;
    public float gammaSetting;

    /** GUI scale */
    public int guiScale;

    /** Determines amount of particles. 0 = All, 1 = Decreased, 2 = Minimal */
    public int particleSetting;

    /** Game settings language */
    public String language;
    private File optionsFileOF;

    public GameSettings(Minecraft par1Minecraft, File par2File)
    {
        this.limitFramerate = 0;
        this.ofKeyBindZoom = new KeyBinding("Zoom", 29);
        this.keyBindings = new KeyBinding[] {this.keyBindAttack, this.keyBindUseItem, this.keyBindForward, this.keyBindLeft, this.keyBindBack, this.keyBindRight, this.keyBindJump, this.keyBindSneak, this.keyBindDrop, this.keyBindInventory, this.keyBindChat, this.keyBindPlayerList, this.keyBindPickBlock, this.ofKeyBindZoom, this.keyBindCommand};
        this.difficulty = 2;
        this.lastServer = "";
        this.noclipRate = 1.0F;
        this.debugCamRate = 1.0F;
        this.language = "en_US";
        this.mc = par1Minecraft;
        this.optionsFile = new File(par2File, "options.txt");
        this.optionsFileOF = new File(par2File, "optionsof.txt");
        this.loadOptions();
        Config.initGameSettings(this);
    }

    public GameSettings()
    {
        this.limitFramerate = 0;
        this.ofKeyBindZoom = new KeyBinding("Zoom", 29);
        this.keyBindings = new KeyBinding[] {this.keyBindAttack, this.keyBindUseItem, this.keyBindForward, this.keyBindLeft, this.keyBindBack, this.keyBindRight, this.keyBindJump, this.keyBindSneak, this.keyBindDrop, this.keyBindInventory, this.keyBindChat, this.keyBindPlayerList, this.keyBindPickBlock, this.ofKeyBindZoom, this.keyBindCommand};
        this.difficulty = 2;
        this.lastServer = "";
        this.noclipRate = 1.0F;
        this.debugCamRate = 1.0F;
        this.language = "en_US";
    }

    public String getKeyBindingDescription(int par1)
    {
        return I18n.getString(this.keyBindings[par1].keyDescription);
    }

    /**
     * The string that appears inside the button/slider in the options menu.
     */
    public String getOptionDisplayString(int par1)
    {
        int var2 = this.keyBindings[par1].keyCode;
        return getKeyDisplayString(var2);
    }

    /**
     * Represents a key or mouse button as a string. Args: key
     */
    public static String getKeyDisplayString(int par0)
    {
        return par0 < 0 ? I18n.getStringParams("key.mouseButton", new Object[] {Integer.valueOf(par0 + 101)}): Keyboard.getKeyName(par0);
    }

    /**
     * Returns whether the specified key binding is currently being pressed.
     */
    public static boolean isKeyDown(KeyBinding par0KeyBinding)
    {
        return par0KeyBinding.keyCode < 0 ? Mouse.isButtonDown(par0KeyBinding.keyCode + 100) : Keyboard.isKeyDown(par0KeyBinding.keyCode);
    }

    /**
     * Sets a key binding.
     */
    public void setKeyBinding(int par1, int par2)
    {
        this.keyBindings[par1].keyCode = par2;
        this.saveOptions();
    }

    /**
     * If the specified option is controlled by a slider (float value), this will set the float value.
     */
    public void setOptionFloatValue(EnumOptions par1EnumOptions, float par2)
    {
        if (par1EnumOptions == EnumOptions.MUSIC)
        {
            this.musicVolume = par2;
            this.mc.sndManager.onSoundOptionsChanged();
        }

        if (par1EnumOptions == EnumOptions.SOUND)
        {
            this.soundVolume = par2;
            this.mc.sndManager.onSoundOptionsChanged();
        }

        if (par1EnumOptions == EnumOptions.SENSITIVITY)
        {
            this.mouseSensitivity = par2;
        }

        if (par1EnumOptions == EnumOptions.FOV)
        {
            this.fovSetting = par2;
        }

        if (par1EnumOptions == EnumOptions.GAMMA)
        {
            this.gammaSetting = par2;
        }

        if (par1EnumOptions == EnumOptions.CLOUD_HEIGHT)
        {
            this.ofCloudsHeight = par2;
        }

        if (par1EnumOptions == EnumOptions.AO_LEVEL)
        {
            this.ofAoLevel = par2;
            this.mc.renderGlobal.loadRenderers();
        }

        if (par1EnumOptions == EnumOptions.RENDER_DISTANCE_FINE)
        {
            int var3 = this.ofRenderDistanceFine;
            this.ofRenderDistanceFine = 32 + (int)(par2 * 480.0F);
            this.ofRenderDistanceFine = this.ofRenderDistanceFine >> 4 << 4;
            this.ofRenderDistanceFine = Config.limit(this.ofRenderDistanceFine, 32, 512);
            this.renderDistance = fineToRenderDistance(this.ofRenderDistanceFine);

            if (this.ofRenderDistanceFine != var3)
            {
                this.mc.renderGlobal.loadRenderers();
            }
        }

        if (par1EnumOptions == EnumOptions.FRAMERATE_LIMIT_FINE)
        {
            this.ofLimitFramerateFine = (int)(par2 * 200.0F);
            this.enableVsync = false;

            if (this.ofLimitFramerateFine < 5)
            {
                this.enableVsync = true;
                this.ofLimitFramerateFine = 0;
            }

            if (this.ofLimitFramerateFine > 199)
            {
                this.enableVsync = false;
                this.ofLimitFramerateFine = 0;
            }

            if (this.ofLimitFramerateFine > 30)
            {
                this.ofLimitFramerateFine = this.ofLimitFramerateFine / 5 * 5;
            }

            if (this.ofLimitFramerateFine > 100)
            {
                this.ofLimitFramerateFine = this.ofLimitFramerateFine / 10 * 10;
            }

            this.limitFramerate = fineToLimitFramerate(this.ofLimitFramerateFine);
            this.updateVSync();
        }

        if (par1EnumOptions == EnumOptions.CHAT_OPACITY)
        {
            this.chatOpacity = par2;
            this.mc.ingameGUI.getChatGUI().func_96132_b();
        }

        if (par1EnumOptions == EnumOptions.CHAT_HEIGHT_FOCUSED)
        {
            this.chatHeightFocused = par2;
            this.mc.ingameGUI.getChatGUI().func_96132_b();
        }

        if (par1EnumOptions == EnumOptions.CHAT_HEIGHT_UNFOCUSED)
        {
            this.chatHeightUnfocused = par2;
            this.mc.ingameGUI.getChatGUI().func_96132_b();
        }

        if (par1EnumOptions == EnumOptions.CHAT_WIDTH)
        {
            this.chatWidth = par2;
            this.mc.ingameGUI.getChatGUI().func_96132_b();
        }

        if (par1EnumOptions == EnumOptions.CHAT_SCALE)
        {
            this.chatScale = par2;
            this.mc.ingameGUI.getChatGUI().func_96132_b();
        }
    }

    private void updateWaterOpacity()
    {
        if (this.mc.getIntegratedServer() != null)
        {
            Config.waterOpacityChanged = true;
        }

        byte opacity = 3;

        if (this.ofClearWater)
        {
            opacity = 1;
        }

        Block.waterStill.setLightOpacity(opacity);
        Block.waterMoving.setLightOpacity(opacity);

        if (this.mc.theWorld != null)
        {
            IChunkProvider cp = this.mc.theWorld.chunkProvider;

            if (cp != null)
            {
                for (int x = -512; x < 512; ++x)
                {
                    for (int z = -512; z < 512; ++z)
                    {
                        if (cp.chunkExists(x, z))
                        {
                            Chunk c = cp.provideChunk(x, z);

                            if (c != null && !(c instanceof EmptyChunk))
                            {
                                ExtendedBlockStorage[] ebss = c.getBlockStorageArray();

                                for (int i = 0; i < ebss.length; ++i)
                                {
                                    ExtendedBlockStorage ebs = ebss[i];

                                    if (ebs != null)
                                    {
                                        NibbleArray na = ebs.getSkylightArray();

                                        if (na != null)
                                        {
                                            byte[] data = na.data;

                                            for (int d = 0; d < data.length; ++d)
                                            {
                                                data[d] = 0;
                                            }
                                        }
                                    }
                                }

                                c.generateSkylightMap();
                            }
                        }
                    }
                }

                this.mc.renderGlobal.loadRenderers();
            }
        }
    }

    public void updateChunkLoading()
    {
        if (this.mc.renderGlobal != null)
        {
            this.mc.renderGlobal.loadRenderers();
        }
    }

    public void setAllAnimations(boolean flag)
    {
        int animVal = flag ? 0 : 2;
        this.ofAnimatedWater = animVal;
        this.ofAnimatedLava = animVal;
        this.ofAnimatedFire = flag;
        this.ofAnimatedPortal = flag;
        this.ofAnimatedRedstone = flag;
        this.ofAnimatedExplosion = flag;
        this.ofAnimatedFlame = flag;
        this.ofAnimatedSmoke = flag;
        this.ofVoidParticles = flag;
        this.ofWaterParticles = flag;
        this.ofRainSplash = flag;
        this.ofPortalParticles = flag;
        this.ofPotionParticles = flag;
        this.particleSetting = flag ? 0 : 2;
        this.ofDrippingWaterLava = flag;
        this.ofAnimatedTerrain = flag;
        this.ofAnimatedItems = flag;
        this.ofAnimatedTextures = flag;
    }

    /**
     * For non-float options. Toggles the option on/off, or cycles through the list i.e. render distances.
     */
    public void setOptionValue(EnumOptions par1EnumOptions, int par2)
    {
        if (par1EnumOptions == EnumOptions.INVERT_MOUSE)
        {
            this.invertMouse = !this.invertMouse;
        }

        if (par1EnumOptions == EnumOptions.RENDER_DISTANCE)
        {
            this.renderDistance = this.renderDistance + par2 & 3;
            this.ofRenderDistanceFine = renderDistanceToFine(this.renderDistance);
        }

        if (par1EnumOptions == EnumOptions.GUI_SCALE)
        {
            this.guiScale = this.guiScale + par2 & 3;
        }

        if (par1EnumOptions == EnumOptions.PARTICLES)
        {
            this.particleSetting = (this.particleSetting + par2) % 3;
        }

        if (par1EnumOptions == EnumOptions.VIEW_BOBBING)
        {
            this.viewBobbing = !this.viewBobbing;
        }

        if (par1EnumOptions == EnumOptions.RENDER_CLOUDS)
        {
            this.clouds = !this.clouds;
        }

        if (par1EnumOptions == EnumOptions.ADVANCED_OPENGL)
        {
            if (!Config.isOcclusionAvailable())
            {
                this.ofOcclusionFancy = false;
                this.advancedOpengl = false;
            }
            else if (!this.advancedOpengl)
            {
                this.advancedOpengl = true;
                this.ofOcclusionFancy = false;
            }
            else if (!this.ofOcclusionFancy)
            {
                this.ofOcclusionFancy = true;
            }
            else
            {
                this.ofOcclusionFancy = false;
                this.advancedOpengl = false;
            }

            this.mc.renderGlobal.setAllRenderersVisible();
        }

        if (par1EnumOptions == EnumOptions.ANAGLYPH)
        {
            this.anaglyph = !this.anaglyph;
            this.mc.refreshResources();
        }

        if (par1EnumOptions == EnumOptions.FRAMERATE_LIMIT)
        {
            this.limitFramerate = (this.limitFramerate + par2 + 3) % 3;
            this.ofLimitFramerateFine = limitFramerateToFine(this.limitFramerate);
        }

        if (par1EnumOptions == EnumOptions.DIFFICULTY)
        {
            this.difficulty = this.difficulty + par2 & 3;
        }

        if (par1EnumOptions == EnumOptions.GRAPHICS)
        {
            this.fancyGraphics = !this.fancyGraphics;
            this.mc.renderGlobal.loadRenderers();
        }

        if (par1EnumOptions == EnumOptions.AMBIENT_OCCLUSION)
        {
            this.ambientOcclusion = (this.ambientOcclusion + par2) % 3;
            this.mc.renderGlobal.loadRenderers();
        }

        if (par1EnumOptions == EnumOptions.FOG_FANCY)
        {
            switch (this.ofFogType)
            {
                case 1:
                    this.ofFogType = 2;

                    if (!Config.isFancyFogAvailable())
                    {
                        this.ofFogType = 3;
                    }

                    break;

                case 2:
                    this.ofFogType = 3;
                    break;

                case 3:
                    this.ofFogType = 1;
                    break;

                default:
                    this.ofFogType = 1;
            }
        }

        if (par1EnumOptions == EnumOptions.FOG_START)
        {
            this.ofFogStart += 0.2F;

            if (this.ofFogStart > 0.81F)
            {
                this.ofFogStart = 0.2F;
            }
        }

        if (par1EnumOptions == EnumOptions.MIPMAP_LEVEL)
        {
            ++this.ofMipmapLevel;

            if (this.ofMipmapLevel > 4)
            {
                this.ofMipmapLevel = 0;
            }

            TextureUtils.refreshBlockTextures();
        }

        if (par1EnumOptions == EnumOptions.MIPMAP_TYPE)
        {
            ++this.ofMipmapType;

            if (this.ofMipmapType > 3)
            {
                this.ofMipmapType = 0;
            }

            TextureUtils.refreshBlockTextures();
        }

        if (par1EnumOptions == EnumOptions.LOAD_FAR)
        {
            this.ofLoadFar = !this.ofLoadFar;
            this.mc.renderGlobal.loadRenderers();
        }

        if (par1EnumOptions == EnumOptions.PRELOADED_CHUNKS)
        {
            this.ofPreloadedChunks += 2;

            if (this.ofPreloadedChunks > 8)
            {
                this.ofPreloadedChunks = 0;
            }

            this.mc.renderGlobal.loadRenderers();
        }

        if (par1EnumOptions == EnumOptions.SMOOTH_FPS)
        {
            this.ofSmoothFps = !this.ofSmoothFps;
        }

        if (par1EnumOptions == EnumOptions.SMOOTH_WORLD)
        {
            this.ofSmoothWorld = !this.ofSmoothWorld;
            Config.updateThreadPriorities();
        }

        if (par1EnumOptions == EnumOptions.CLOUDS)
        {
            ++this.ofClouds;

            if (this.ofClouds > 3)
            {
                this.ofClouds = 0;
            }
        }

        if (par1EnumOptions == EnumOptions.TREES)
        {
            ++this.ofTrees;

            if (this.ofTrees > 2)
            {
                this.ofTrees = 0;
            }

            this.mc.renderGlobal.loadRenderers();
        }

        if (par1EnumOptions == EnumOptions.GRASS)
        {
            ++this.ofGrass;

            if (this.ofGrass > 2)
            {
                this.ofGrass = 0;
            }

            RenderBlocks.fancyGrass = Config.isGrassFancy();
            this.mc.renderGlobal.loadRenderers();
        }

        if (par1EnumOptions == EnumOptions.DROPPED_ITEMS)
        {
            ++this.ofDroppedItems;

            if (this.ofDroppedItems > 2)
            {
                this.ofDroppedItems = 0;
            }
        }

        if (par1EnumOptions == EnumOptions.RAIN)
        {
            ++this.ofRain;

            if (this.ofRain > 3)
            {
                this.ofRain = 0;
            }
        }

        if (par1EnumOptions == EnumOptions.WATER)
        {
            ++this.ofWater;

            if (this.ofWater > 2)
            {
                this.ofWater = 0;
            }
        }

        if (par1EnumOptions == EnumOptions.ANIMATED_WATER)
        {
            ++this.ofAnimatedWater;

            if (this.ofAnimatedWater > 2)
            {
                this.ofAnimatedWater = 0;
            }
        }

        if (par1EnumOptions == EnumOptions.ANIMATED_LAVA)
        {
            ++this.ofAnimatedLava;

            if (this.ofAnimatedLava > 2)
            {
                this.ofAnimatedLava = 0;
            }
        }

        if (par1EnumOptions == EnumOptions.ANIMATED_FIRE)
        {
            this.ofAnimatedFire = !this.ofAnimatedFire;
        }

        if (par1EnumOptions == EnumOptions.ANIMATED_PORTAL)
        {
            this.ofAnimatedPortal = !this.ofAnimatedPortal;
        }

        if (par1EnumOptions == EnumOptions.ANIMATED_REDSTONE)
        {
            this.ofAnimatedRedstone = !this.ofAnimatedRedstone;
        }

        if (par1EnumOptions == EnumOptions.ANIMATED_EXPLOSION)
        {
            this.ofAnimatedExplosion = !this.ofAnimatedExplosion;
        }

        if (par1EnumOptions == EnumOptions.ANIMATED_FLAME)
        {
            this.ofAnimatedFlame = !this.ofAnimatedFlame;
        }

        if (par1EnumOptions == EnumOptions.ANIMATED_SMOKE)
        {
            this.ofAnimatedSmoke = !this.ofAnimatedSmoke;
        }

        if (par1EnumOptions == EnumOptions.VOID_PARTICLES)
        {
            this.ofVoidParticles = !this.ofVoidParticles;
        }

        if (par1EnumOptions == EnumOptions.WATER_PARTICLES)
        {
            this.ofWaterParticles = !this.ofWaterParticles;
        }

        if (par1EnumOptions == EnumOptions.PORTAL_PARTICLES)
        {
            this.ofPortalParticles = !this.ofPortalParticles;
        }

        if (par1EnumOptions == EnumOptions.POTION_PARTICLES)
        {
            this.ofPotionParticles = !this.ofPotionParticles;
        }

        if (par1EnumOptions == EnumOptions.DRIPPING_WATER_LAVA)
        {
            this.ofDrippingWaterLava = !this.ofDrippingWaterLava;
        }

        if (par1EnumOptions == EnumOptions.ANIMATED_TERRAIN)
        {
            this.ofAnimatedTerrain = !this.ofAnimatedTerrain;
        }

        if (par1EnumOptions == EnumOptions.ANIMATED_TEXTURES)
        {
            this.ofAnimatedTextures = !this.ofAnimatedTextures;
        }

        if (par1EnumOptions == EnumOptions.ANIMATED_ITEMS)
        {
            this.ofAnimatedItems = !this.ofAnimatedItems;
        }

        if (par1EnumOptions == EnumOptions.RAIN_SPLASH)
        {
            this.ofRainSplash = !this.ofRainSplash;
        }

        if (par1EnumOptions == EnumOptions.LAGOMETER)
        {
            this.ofLagometer = !this.ofLagometer;
        }

        if (par1EnumOptions == EnumOptions.AUTOSAVE_TICKS)
        {
            this.ofAutoSaveTicks *= 10;

            if (this.ofAutoSaveTicks > 40000)
            {
                this.ofAutoSaveTicks = 40;
            }
        }

        if (par1EnumOptions == EnumOptions.BETTER_GRASS)
        {
            ++this.ofBetterGrass;

            if (this.ofBetterGrass > 3)
            {
                this.ofBetterGrass = 1;
            }

            this.mc.renderGlobal.loadRenderers();
        }

        if (par1EnumOptions == EnumOptions.CONNECTED_TEXTURES)
        {
            ++this.ofConnectedTextures;

            if (this.ofConnectedTextures > 3)
            {
                this.ofConnectedTextures = 1;
            }

            this.mc.renderGlobal.loadRenderers();
        }

        if (par1EnumOptions == EnumOptions.WEATHER)
        {
            this.ofWeather = !this.ofWeather;
        }

        if (par1EnumOptions == EnumOptions.SKY)
        {
            this.ofSky = !this.ofSky;
        }

        if (par1EnumOptions == EnumOptions.STARS)
        {
            this.ofStars = !this.ofStars;
        }

        if (par1EnumOptions == EnumOptions.SUN_MOON)
        {
            this.ofSunMoon = !this.ofSunMoon;
        }

        if (par1EnumOptions == EnumOptions.CHUNK_UPDATES)
        {
            ++this.ofChunkUpdates;

            if (this.ofChunkUpdates > 5)
            {
                this.ofChunkUpdates = 1;
            }
        }

        if (par1EnumOptions == EnumOptions.CHUNK_LOADING)
        {
            ++this.ofChunkLoading;

            if (this.ofChunkLoading > 2)
            {
                this.ofChunkLoading = 0;
            }

            this.updateChunkLoading();
        }

        if (par1EnumOptions == EnumOptions.CHUNK_UPDATES_DYNAMIC)
        {
            this.ofChunkUpdatesDynamic = !this.ofChunkUpdatesDynamic;
        }

        if (par1EnumOptions == EnumOptions.TIME)
        {
            ++this.ofTime;

            if (this.ofTime > 3)
            {
                this.ofTime = 0;
            }
        }

        if (par1EnumOptions == EnumOptions.CLEAR_WATER)
        {
            this.ofClearWater = !this.ofClearWater;
            this.updateWaterOpacity();
        }

        if (par1EnumOptions == EnumOptions.DEPTH_FOG)
        {
            this.ofDepthFog = !this.ofDepthFog;
        }

        if (par1EnumOptions == EnumOptions.AA_LEVEL)
        {
            int[] var3 = new int[] {0, 2, 4, 6, 8, 12, 16};
            boolean var4 = false;

            for (int var5 = 0; var5 < var3.length - 1; ++var5)
            {
                if (this.ofAaLevel == var3[var5])
                {
                    this.ofAaLevel = var3[var5 + 1];
                    var4 = true;
                    break;
                }
            }

            if (!var4)
            {
                this.ofAaLevel = 0;
            }
        }

        if (par1EnumOptions == EnumOptions.AF_LEVEL)
        {
            this.ofAfLevel *= 2;

            if (this.ofAfLevel > 16)
            {
                this.ofAfLevel = 1;
            }

            this.ofAfLevel = Config.limit(this.ofAfLevel, 1, 16);
            TextureUtils.refreshBlockTextures();
            this.mc.renderGlobal.loadRenderers();
        }

        if (par1EnumOptions == EnumOptions.PROFILER)
        {
            this.ofProfiler = !this.ofProfiler;
        }

        if (par1EnumOptions == EnumOptions.BETTER_SNOW)
        {
            this.ofBetterSnow = !this.ofBetterSnow;
            this.mc.renderGlobal.loadRenderers();
        }

        if (par1EnumOptions == EnumOptions.SWAMP_COLORS)
        {
            this.ofSwampColors = !this.ofSwampColors;
            CustomColorizer.updateUseDefaultColorMultiplier();
            this.mc.renderGlobal.loadRenderers();
        }

        if (par1EnumOptions == EnumOptions.RANDOM_MOBS)
        {
            this.ofRandomMobs = !this.ofRandomMobs;
            RandomMobs.resetTextures();
        }

        if (par1EnumOptions == EnumOptions.SMOOTH_BIOMES)
        {
            this.ofSmoothBiomes = !this.ofSmoothBiomes;
            CustomColorizer.updateUseDefaultColorMultiplier();
            this.mc.renderGlobal.loadRenderers();
        }

        if (par1EnumOptions == EnumOptions.CUSTOM_FONTS)
        {
            this.ofCustomFonts = !this.ofCustomFonts;
            this.mc.fontRenderer.onResourceManagerReload(Config.getResourceManager());
            this.mc.standardGalacticFontRenderer.onResourceManagerReload(Config.getResourceManager());
        }

        if (par1EnumOptions == EnumOptions.CUSTOM_COLORS)
        {
            this.ofCustomColors = !this.ofCustomColors;
            CustomColorizer.update();
            this.mc.renderGlobal.loadRenderers();
        }

        if (par1EnumOptions == EnumOptions.CUSTOM_SKY)
        {
            this.ofCustomSky = !this.ofCustomSky;
            CustomSky.update();
        }

        if (par1EnumOptions == EnumOptions.SHOW_CAPES)
        {
            this.ofShowCapes = !this.ofShowCapes;
            this.mc.renderGlobal.updateCapes();
        }

        if (par1EnumOptions == EnumOptions.NATURAL_TEXTURES)
        {
            this.ofNaturalTextures = !this.ofNaturalTextures;
            NaturalTextures.update();
            this.mc.renderGlobal.loadRenderers();
        }

        if (par1EnumOptions == EnumOptions.FAST_MATH)
        {
            this.ofFastMath = !this.ofFastMath;
            MathHelper.fastMath = this.ofFastMath;
        }

        if (par1EnumOptions == EnumOptions.LAZY_CHUNK_LOADING)
        {
            this.ofLazyChunkLoading = !this.ofLazyChunkLoading;
            this.mc.renderGlobal.loadRenderers();
        }

        if (par1EnumOptions == EnumOptions.FULLSCREEN_MODE)
        {
            List var6 = Arrays.asList(Config.getFullscreenModes());

            if (this.ofFullscreenMode.equals("Default"))
            {
                this.ofFullscreenMode = (String)var6.get(0);
            }
            else
            {
                int var7 = var6.indexOf(this.ofFullscreenMode);

                if (var7 < 0)
                {
                    this.ofFullscreenMode = "Default";
                }
                else
                {
                    ++var7;

                    if (var7 >= var6.size())
                    {
                        this.ofFullscreenMode = "Default";
                    }
                    else
                    {
                        this.ofFullscreenMode = (String)var6.get(var7);
                    }
                }
            }
        }

        if (par1EnumOptions == EnumOptions.HELD_ITEM_TOOLTIPS)
        {
            this.heldItemTooltips = !this.heldItemTooltips;
        }

        if (par1EnumOptions == EnumOptions.CHAT_VISIBILITY)
        {
            this.chatVisibility = (this.chatVisibility + par2) % 3;
        }

        if (par1EnumOptions == EnumOptions.CHAT_COLOR)
        {
            this.chatColours = !this.chatColours;
        }

        if (par1EnumOptions == EnumOptions.CHAT_LINKS)
        {
            this.chatLinks = !this.chatLinks;
        }

        if (par1EnumOptions == EnumOptions.CHAT_LINKS_PROMPT)
        {
            this.chatLinksPrompt = !this.chatLinksPrompt;
        }

        if (par1EnumOptions == EnumOptions.USE_SERVER_TEXTURES)
        {
            this.serverTextures = !this.serverTextures;
        }

        if (par1EnumOptions == EnumOptions.SNOOPER_ENABLED)
        {
            this.snooperEnabled = !this.snooperEnabled;
        }

        if (par1EnumOptions == EnumOptions.SHOW_CAPE)
        {
            this.showCape = !this.showCape;
        }

        if (par1EnumOptions == EnumOptions.TOUCHSCREEN)
        {
            this.touchscreen = !this.touchscreen;
        }

        if (par1EnumOptions == EnumOptions.USE_FULLSCREEN)
        {
            this.fullScreen = !this.fullScreen;

            if (this.mc.isFullScreen() != this.fullScreen)
            {
                this.mc.toggleFullscreen();
            }
        }

        if (par1EnumOptions == EnumOptions.ENABLE_VSYNC)
        {
            this.enableVsync = !this.enableVsync;
            Display.setVSyncEnabled(this.enableVsync);
        }

        this.saveOptions();
    }

    public float getOptionFloatValue(EnumOptions par1EnumOptions)
    {
        return par1EnumOptions == EnumOptions.CLOUD_HEIGHT ? this.ofCloudsHeight : (par1EnumOptions == EnumOptions.AO_LEVEL ? this.ofAoLevel : (par1EnumOptions == EnumOptions.RENDER_DISTANCE_FINE ? (float)(this.ofRenderDistanceFine - 32) / 480.0F : (par1EnumOptions == EnumOptions.FRAMERATE_LIMIT_FINE ? (this.ofLimitFramerateFine > 0 && this.ofLimitFramerateFine < 200 ? (float)this.ofLimitFramerateFine / 200.0F : (this.enableVsync ? 0.0F : 1.0F)) : (par1EnumOptions == EnumOptions.FOV ? this.fovSetting : (par1EnumOptions == EnumOptions.GAMMA ? this.gammaSetting : (par1EnumOptions == EnumOptions.MUSIC ? this.musicVolume : (par1EnumOptions == EnumOptions.SOUND ? this.soundVolume : (par1EnumOptions == EnumOptions.SENSITIVITY ? this.mouseSensitivity : (par1EnumOptions == EnumOptions.CHAT_OPACITY ? this.chatOpacity : (par1EnumOptions == EnumOptions.CHAT_HEIGHT_FOCUSED ? this.chatHeightFocused : (par1EnumOptions == EnumOptions.CHAT_HEIGHT_UNFOCUSED ? this.chatHeightUnfocused : (par1EnumOptions == EnumOptions.CHAT_SCALE ? this.chatScale : (par1EnumOptions == EnumOptions.CHAT_WIDTH ? this.chatWidth : 0.0F)))))))))))));
    }

    public boolean getOptionOrdinalValue(EnumOptions par1EnumOptions)
    {
        switch (EnumOptionsHelper.enumOptionsMappingHelperArray[par1EnumOptions.ordinal()])
        {
            case 1:
                return this.invertMouse;

            case 2:
                return this.viewBobbing;

            case 3:
                return this.anaglyph;

            case 4:
                return this.advancedOpengl;

            case 5:
                return this.clouds;

            case 6:
                return this.chatColours;

            case 7:
                return this.chatLinks;

            case 8:
                return this.chatLinksPrompt;

            case 9:
                return this.serverTextures;

            case 10:
                return this.snooperEnabled;

            case 11:
                return this.fullScreen;

            case 12:
                return this.enableVsync;

            case 13:
                return this.showCape;

            case 14:
                return this.touchscreen;

            default:
                return false;
        }
    }

    /**
     * Returns the translation of the given index in the given String array. If the index is smaller than 0 or greater
     * than/equal to the length of the String array, it is changed to 0.
     */
    private static String getTranslation(String[] par0ArrayOfStr, int par1)
    {
        if (par1 < 0 || par1 >= par0ArrayOfStr.length)
        {
            par1 = 0;
        }

        return I18n.getString(par0ArrayOfStr[par1]);
    }

    /**
     * Gets a key binding.
     */
    public String getKeyBinding(EnumOptions par1EnumOptions)
    {
        String var2 = I18n.getString(par1EnumOptions.getEnumString());

        if (var2 == null)
        {
            var2 = par1EnumOptions.getEnumString();
        }

        String var3 = var2 + ": ";
        String var5;

        if (par1EnumOptions == EnumOptions.RENDER_DISTANCE_FINE)
        {
            var5 = "Tiny";
            short var10 = 32;

            if (this.ofRenderDistanceFine >= 64)
            {
                var5 = "Short";
                var10 = 64;
            }

            if (this.ofRenderDistanceFine >= 128)
            {
                var5 = "Normal";
                var10 = 128;
            }

            if (this.ofRenderDistanceFine >= 256)
            {
                var5 = "Far";
                var10 = 256;
            }

            if (this.ofRenderDistanceFine >= 512)
            {
                var5 = "Extreme";
                var10 = 512;
            }

            int var7 = this.ofRenderDistanceFine - var10;
            return var7 == 0 ? var3 + var5 : var3 + var5 + " +" + var7;
        }
        else if (par1EnumOptions == EnumOptions.FRAMERATE_LIMIT_FINE)
        {
            return this.ofLimitFramerateFine > 0 && this.ofLimitFramerateFine < 200 ? var3 + " " + this.ofLimitFramerateFine + " FPS" : (this.enableVsync ? var3 + " VSync" : var3 + " MaxFPS");
        }
        else if (par1EnumOptions == EnumOptions.ADVANCED_OPENGL)
        {
            return !this.advancedOpengl ? var3 + "OFF" : (this.ofOcclusionFancy ? var3 + "Fancy" : var3 + "Fast");
        }
        else if (par1EnumOptions == EnumOptions.FOG_FANCY)
        {
            switch (this.ofFogType)
            {
                case 1:
                    return var3 + "Fast";

                case 2:
                    return var3 + "Fancy";

                case 3:
                    return var3 + "OFF";

                default:
                    return var3 + "OFF";
            }
        }
        else if (par1EnumOptions == EnumOptions.FOG_START)
        {
            return var3 + this.ofFogStart;
        }
        else if (par1EnumOptions == EnumOptions.MIPMAP_LEVEL)
        {
            return this.ofMipmapLevel == 0 ? var3 + "OFF" : (this.ofMipmapLevel == 4 ? var3 + "Max" : var3 + this.ofMipmapLevel);
        }
        else if (par1EnumOptions == EnumOptions.MIPMAP_TYPE)
        {
            switch (this.ofMipmapType)
            {
                case 0:
                    return var3 + "Nearest";

                case 1:
                    return var3 + "Linear";

                case 2:
                    return var3 + "Bilinear";

                case 3:
                    return var3 + "Trilinear";

                default:
                    return var3 + "Nearest";
            }
        }
        else if (par1EnumOptions == EnumOptions.LOAD_FAR)
        {
            return this.ofLoadFar ? var3 + "ON" : var3 + "OFF";
        }
        else if (par1EnumOptions == EnumOptions.PRELOADED_CHUNKS)
        {
            return this.ofPreloadedChunks == 0 ? var3 + "OFF" : var3 + this.ofPreloadedChunks;
        }
        else if (par1EnumOptions == EnumOptions.SMOOTH_FPS)
        {
            return this.ofSmoothFps ? var3 + "ON" : var3 + "OFF";
        }
        else if (par1EnumOptions == EnumOptions.SMOOTH_WORLD)
        {
            return this.ofSmoothWorld ? var3 + "ON" : var3 + "OFF";
        }
        else if (par1EnumOptions == EnumOptions.CLOUDS)
        {
            switch (this.ofClouds)
            {
                case 1:
                    return var3 + "Fast";

                case 2:
                    return var3 + "Fancy";

                case 3:
                    return var3 + "OFF";

                default:
                    return var3 + "Default";
            }
        }
        else if (par1EnumOptions == EnumOptions.TREES)
        {
            switch (this.ofTrees)
            {
                case 1:
                    return var3 + "Fast";

                case 2:
                    return var3 + "Fancy";

                default:
                    return var3 + "Default";
            }
        }
        else if (par1EnumOptions == EnumOptions.GRASS)
        {
            switch (this.ofGrass)
            {
                case 1:
                    return var3 + "Fast";

                case 2:
                    return var3 + "Fancy";

                default:
                    return var3 + "Default";
            }
        }
        else if (par1EnumOptions == EnumOptions.DROPPED_ITEMS)
        {
            switch (this.ofDroppedItems)
            {
                case 1:
                    return var3 + "Fast";

                case 2:
                    return var3 + "Fancy";

                default:
                    return var3 + "Default";
            }
        }
        else if (par1EnumOptions == EnumOptions.RAIN)
        {
            switch (this.ofRain)
            {
                case 1:
                    return var3 + "Fast";

                case 2:
                    return var3 + "Fancy";

                case 3:
                    return var3 + "OFF";

                default:
                    return var3 + "Default";
            }
        }
        else if (par1EnumOptions == EnumOptions.WATER)
        {
            switch (this.ofWater)
            {
                case 1:
                    return var3 + "Fast";

                case 2:
                    return var3 + "Fancy";

                case 3:
                    return var3 + "OFF";

                default:
                    return var3 + "Default";
            }
        }
        else if (par1EnumOptions == EnumOptions.ANIMATED_WATER)
        {
            switch (this.ofAnimatedWater)
            {
                case 1:
                    return var3 + "Dynamic";

                case 2:
                    return var3 + "OFF";

                default:
                    return var3 + "ON";
            }
        }
        else if (par1EnumOptions == EnumOptions.ANIMATED_LAVA)
        {
            switch (this.ofAnimatedLava)
            {
                case 1:
                    return var3 + "Dynamic";

                case 2:
                    return var3 + "OFF";

                default:
                    return var3 + "ON";
            }
        }
        else if (par1EnumOptions == EnumOptions.ANIMATED_FIRE)
        {
            return this.ofAnimatedFire ? var3 + "ON" : var3 + "OFF";
        }
        else if (par1EnumOptions == EnumOptions.ANIMATED_PORTAL)
        {
            return this.ofAnimatedPortal ? var3 + "ON" : var3 + "OFF";
        }
        else if (par1EnumOptions == EnumOptions.ANIMATED_REDSTONE)
        {
            return this.ofAnimatedRedstone ? var3 + "ON" : var3 + "OFF";
        }
        else if (par1EnumOptions == EnumOptions.ANIMATED_EXPLOSION)
        {
            return this.ofAnimatedExplosion ? var3 + "ON" : var3 + "OFF";
        }
        else if (par1EnumOptions == EnumOptions.ANIMATED_FLAME)
        {
            return this.ofAnimatedFlame ? var3 + "ON" : var3 + "OFF";
        }
        else if (par1EnumOptions == EnumOptions.ANIMATED_SMOKE)
        {
            return this.ofAnimatedSmoke ? var3 + "ON" : var3 + "OFF";
        }
        else if (par1EnumOptions == EnumOptions.VOID_PARTICLES)
        {
            return this.ofVoidParticles ? var3 + "ON" : var3 + "OFF";
        }
        else if (par1EnumOptions == EnumOptions.WATER_PARTICLES)
        {
            return this.ofWaterParticles ? var3 + "ON" : var3 + "OFF";
        }
        else if (par1EnumOptions == EnumOptions.PORTAL_PARTICLES)
        {
            return this.ofPortalParticles ? var3 + "ON" : var3 + "OFF";
        }
        else if (par1EnumOptions == EnumOptions.POTION_PARTICLES)
        {
            return this.ofPotionParticles ? var3 + "ON" : var3 + "OFF";
        }
        else if (par1EnumOptions == EnumOptions.DRIPPING_WATER_LAVA)
        {
            return this.ofDrippingWaterLava ? var3 + "ON" : var3 + "OFF";
        }
        else if (par1EnumOptions == EnumOptions.ANIMATED_TERRAIN)
        {
            return this.ofAnimatedTerrain ? var3 + "ON" : var3 + "OFF";
        }
        else if (par1EnumOptions == EnumOptions.ANIMATED_TEXTURES)
        {
            return this.ofAnimatedTextures ? var3 + "ON" : var3 + "OFF";
        }
        else if (par1EnumOptions == EnumOptions.ANIMATED_ITEMS)
        {
            return this.ofAnimatedItems ? var3 + "ON" : var3 + "OFF";
        }
        else if (par1EnumOptions == EnumOptions.RAIN_SPLASH)
        {
            return this.ofRainSplash ? var3 + "ON" : var3 + "OFF";
        }
        else if (par1EnumOptions == EnumOptions.LAGOMETER)
        {
            return this.ofLagometer ? var3 + "ON" : var3 + "OFF";
        }
        else if (par1EnumOptions == EnumOptions.AUTOSAVE_TICKS)
        {
            return this.ofAutoSaveTicks <= 40 ? var3 + "Default (2s)" : (this.ofAutoSaveTicks <= 400 ? var3 + "20s" : (this.ofAutoSaveTicks <= 4000 ? var3 + "3min" : var3 + "30min"));
        }
        else if (par1EnumOptions == EnumOptions.BETTER_GRASS)
        {
            switch (this.ofBetterGrass)
            {
                case 1:
                    return var3 + "Fast";

                case 2:
                    return var3 + "Fancy";

                default:
                    return var3 + "OFF";
            }
        }
        else if (par1EnumOptions == EnumOptions.CONNECTED_TEXTURES)
        {
            switch (this.ofConnectedTextures)
            {
                case 1:
                    return var3 + "Fast";

                case 2:
                    return var3 + "Fancy";

                default:
                    return var3 + "OFF";
            }
        }
        else if (par1EnumOptions == EnumOptions.WEATHER)
        {
            return this.ofWeather ? var3 + "ON" : var3 + "OFF";
        }
        else if (par1EnumOptions == EnumOptions.SKY)
        {
            return this.ofSky ? var3 + "ON" : var3 + "OFF";
        }
        else if (par1EnumOptions == EnumOptions.STARS)
        {
            return this.ofStars ? var3 + "ON" : var3 + "OFF";
        }
        else if (par1EnumOptions == EnumOptions.SUN_MOON)
        {
            return this.ofSunMoon ? var3 + "ON" : var3 + "OFF";
        }
        else if (par1EnumOptions == EnumOptions.CHUNK_UPDATES)
        {
            return var3 + this.ofChunkUpdates;
        }
        else if (par1EnumOptions == EnumOptions.CHUNK_LOADING)
        {
            return this.ofChunkLoading == 1 ? var3 + "Smooth" : (this.ofChunkLoading == 2 ? var3 + "Multi-Core" : var3 + "Default");
        }
        else if (par1EnumOptions == EnumOptions.CHUNK_UPDATES_DYNAMIC)
        {
            return this.ofChunkUpdatesDynamic ? var3 + "ON" : var3 + "OFF";
        }
        else if (par1EnumOptions == EnumOptions.TIME)
        {
            return this.ofTime == 1 ? var3 + "Day Only" : (this.ofTime == 3 ? var3 + "Night Only" : var3 + "Default");
        }
        else if (par1EnumOptions == EnumOptions.CLEAR_WATER)
        {
            return this.ofClearWater ? var3 + "ON" : var3 + "OFF";
        }
        else if (par1EnumOptions == EnumOptions.DEPTH_FOG)
        {
            return this.ofDepthFog ? var3 + "ON" : var3 + "OFF";
        }
        else if (par1EnumOptions == EnumOptions.AA_LEVEL)
        {
            return this.ofAaLevel == 0 ? var3 + "OFF" : var3 + this.ofAaLevel;
        }
        else if (par1EnumOptions == EnumOptions.AF_LEVEL)
        {
            return this.ofAfLevel == 1 ? var3 + "OFF" : var3 + this.ofAfLevel;
        }
        else if (par1EnumOptions == EnumOptions.PROFILER)
        {
            return this.ofProfiler ? var3 + "ON" : var3 + "OFF";
        }
        else if (par1EnumOptions == EnumOptions.BETTER_SNOW)
        {
            return this.ofBetterSnow ? var3 + "ON" : var3 + "OFF";
        }
        else if (par1EnumOptions == EnumOptions.SWAMP_COLORS)
        {
            return this.ofSwampColors ? var3 + "ON" : var3 + "OFF";
        }
        else if (par1EnumOptions == EnumOptions.RANDOM_MOBS)
        {
            return this.ofRandomMobs ? var3 + "ON" : var3 + "OFF";
        }
        else if (par1EnumOptions == EnumOptions.SMOOTH_BIOMES)
        {
            return this.ofSmoothBiomes ? var3 + "ON" : var3 + "OFF";
        }
        else if (par1EnumOptions == EnumOptions.CUSTOM_FONTS)
        {
            return this.ofCustomFonts ? var3 + "ON" : var3 + "OFF";
        }
        else if (par1EnumOptions == EnumOptions.CUSTOM_COLORS)
        {
            return this.ofCustomColors ? var3 + "ON" : var3 + "OFF";
        }
        else if (par1EnumOptions == EnumOptions.CUSTOM_SKY)
        {
            return this.ofCustomSky ? var3 + "ON" : var3 + "OFF";
        }
        else if (par1EnumOptions == EnumOptions.SHOW_CAPES)
        {
            return this.ofShowCapes ? var3 + "ON" : var3 + "OFF";
        }
        else if (par1EnumOptions == EnumOptions.NATURAL_TEXTURES)
        {
            return this.ofNaturalTextures ? var3 + "ON" : var3 + "OFF";
        }
        else if (par1EnumOptions == EnumOptions.FAST_MATH)
        {
            return this.ofFastMath ? var3 + "ON" : var3 + "OFF";
        }
        else if (par1EnumOptions == EnumOptions.LAZY_CHUNK_LOADING)
        {
            return this.ofLazyChunkLoading ? var3 + "ON" : var3 + "OFF";
        }
        else if (par1EnumOptions == EnumOptions.FULLSCREEN_MODE)
        {
            return var3 + this.ofFullscreenMode;
        }
        else if (par1EnumOptions == EnumOptions.HELD_ITEM_TOOLTIPS)
        {
            return this.heldItemTooltips ? var3 + "ON" : var3 + "OFF";
        }
        else
        {
            var5 = I18n.getString(par1EnumOptions.getEnumString()) + ": ";

            if (par1EnumOptions.getEnumFloat())
            {
                float var9 = this.getOptionFloatValue(par1EnumOptions);
                return par1EnumOptions == EnumOptions.SENSITIVITY ? (var9 == 0.0F ? var5 + I18n.getString("options.sensitivity.min") : (var9 == 1.0F ? var5 + I18n.getString("options.sensitivity.max") : var5 + (int)(var9 * 200.0F) + "%")) : (par1EnumOptions == EnumOptions.FOV ? (var9 == 0.0F ? var5 + I18n.getString("options.fov.min") : (var9 == 1.0F ? var5 + I18n.getString("options.fov.max") : var5 + (int)(70.0F + var9 * 40.0F))) : (par1EnumOptions == EnumOptions.GAMMA ? (var9 == 0.0F ? var5 + I18n.getString("options.gamma.min") : (var9 == 1.0F ? var5 + I18n.getString("options.gamma.max") : var5 + "+" + (int)(var9 * 100.0F) + "%")) : (par1EnumOptions == EnumOptions.CHAT_OPACITY ? var5 + (int)(var9 * 90.0F + 10.0F) + "%" : (par1EnumOptions == EnumOptions.CHAT_HEIGHT_UNFOCUSED ? var5 + GuiNewChat.func_96130_b(var9) + "px" : (par1EnumOptions == EnumOptions.CHAT_HEIGHT_FOCUSED ? var5 + GuiNewChat.func_96130_b(var9) + "px" : (par1EnumOptions == EnumOptions.CHAT_WIDTH ? var5 + GuiNewChat.func_96128_a(var9) + "px" : (var9 == 0.0F ? var5 + I18n.getString("options.off") : var5 + (int)(var9 * 100.0F) + "%")))))));
            }
            else if (par1EnumOptions.getEnumBoolean())
            {
                boolean var8 = this.getOptionOrdinalValue(par1EnumOptions);
                return var8 ? var5 + I18n.getString("options.on") : var5 + I18n.getString("options.off");
            }
            else if (par1EnumOptions == EnumOptions.RENDER_DISTANCE)
            {
                return var5 + getTranslation(RENDER_DISTANCES, this.renderDistance);
            }
            else if (par1EnumOptions == EnumOptions.DIFFICULTY)
            {
                return var5 + getTranslation(DIFFICULTIES, this.difficulty);
            }
            else if (par1EnumOptions == EnumOptions.GUI_SCALE)
            {
                return var5 + getTranslation(GUISCALES, this.guiScale);
            }
            else if (par1EnumOptions == EnumOptions.CHAT_VISIBILITY)
            {
                return var5 + getTranslation(CHAT_VISIBILITIES, this.chatVisibility);
            }
            else if (par1EnumOptions == EnumOptions.PARTICLES)
            {
                return var5 + getTranslation(PARTICLES, this.particleSetting);
            }
            else if (par1EnumOptions == EnumOptions.FRAMERATE_LIMIT)
            {
                return var5 + getTranslation(LIMIT_FRAMERATES, this.limitFramerate);
            }
            else if (par1EnumOptions == EnumOptions.AMBIENT_OCCLUSION)
            {
                return var5 + getTranslation(AMBIENT_OCCLUSIONS, this.ambientOcclusion);
            }
            else if (par1EnumOptions == EnumOptions.GRAPHICS)
            {
                if (this.fancyGraphics)
                {
                    return var5 + I18n.getString("options.graphics.fancy");
                }
                else
                {
                    String var6 = "options.graphics.fast";
                    return var5 + I18n.getString("options.graphics.fast");
                }
            }
            else
            {
                return var5;
            }
        }
    }

    /**
     * Loads the options from the options file. It appears that this has replaced the previous 'loadOptions'
     */
    public void loadOptions()
    {
        try
        {
            if (!this.optionsFile.exists())
            {
                return;
            }

            BufferedReader var1 = new BufferedReader(new FileReader(this.optionsFile));
            String var2 = "";

            while ((var2 = var1.readLine()) != null)
            {
                try
                {
                    String[] var3 = var2.split(":");

                    if (var3[0].equals("music"))
                    {
                        this.musicVolume = this.parseFloat(var3[1]);
                    }

                    if (var3[0].equals("sound"))
                    {
                        this.soundVolume = this.parseFloat(var3[1]);
                    }

                    if (var3[0].equals("mouseSensitivity"))
                    {
                        this.mouseSensitivity = this.parseFloat(var3[1]);
                    }

                    if (var3[0].equals("fov"))
                    {
                        this.fovSetting = this.parseFloat(var3[1]);
                    }

                    if (var3[0].equals("gamma"))
                    {
                        this.gammaSetting = this.parseFloat(var3[1]);
                    }

                    if (var3[0].equals("invertYMouse"))
                    {
                        this.invertMouse = var3[1].equals("true");
                    }

                    if (var3[0].equals("viewDistance"))
                    {
                        this.renderDistance = Integer.parseInt(var3[1]);
                        this.ofRenderDistanceFine = renderDistanceToFine(this.renderDistance);
                    }

                    if (var3[0].equals("guiScale"))
                    {
                        this.guiScale = Integer.parseInt(var3[1]);
                    }

                    if (var3[0].equals("particles"))
                    {
                        this.particleSetting = Integer.parseInt(var3[1]);
                    }

                    if (var3[0].equals("bobView"))
                    {
                        this.viewBobbing = var3[1].equals("true");
                    }

                    if (var3[0].equals("anaglyph3d"))
                    {
                        this.anaglyph = var3[1].equals("true");
                    }

                    if (var3[0].equals("advancedOpengl"))
                    {
                        this.advancedOpengl = var3[1].equals("true");
                    }

                    if (var3[0].equals("fpsLimit"))
                    {
                        this.limitFramerate = Integer.parseInt(var3[1]);
                        this.ofLimitFramerateFine = limitFramerateToFine(this.limitFramerate);
                    }

                    if (var3[0].equals("difficulty"))
                    {
                        this.difficulty = Integer.parseInt(var3[1]);
                    }

                    if (var3[0].equals("fancyGraphics"))
                    {
                        this.fancyGraphics = var3[1].equals("true");
                    }

                    if (var3[0].equals("ao"))
                    {
                        if (var3[1].equals("true"))
                        {
                            this.ambientOcclusion = 2;
                        }
                        else if (var3[1].equals("false"))
                        {
                            this.ambientOcclusion = 0;
                        }
                        else
                        {
                            this.ambientOcclusion = Integer.parseInt(var3[1]);
                        }
                    }

                    if (var3[0].equals("clouds"))
                    {
                        this.clouds = var3[1].equals("true");
                    }

                    if (var3[0].equals("skin"))
                    {
                        this.skin = var3[1];
                    }

                    if (var3[0].equals("lastServer") && var3.length >= 2)
                    {
                        this.lastServer = var2.substring(var2.indexOf(58) + 1);
                    }

                    if (var3[0].equals("lang") && var3.length >= 2)
                    {
                        this.language = var3[1];
                    }

                    if (var3[0].equals("chatVisibility"))
                    {
                        this.chatVisibility = Integer.parseInt(var3[1]);
                    }

                    if (var3[0].equals("chatColors"))
                    {
                        this.chatColours = var3[1].equals("true");
                    }

                    if (var3[0].equals("chatLinks"))
                    {
                        this.chatLinks = var3[1].equals("true");
                    }

                    if (var3[0].equals("chatLinksPrompt"))
                    {
                        this.chatLinksPrompt = var3[1].equals("true");
                    }

                    if (var3[0].equals("chatOpacity"))
                    {
                        this.chatOpacity = this.parseFloat(var3[1]);
                    }

                    if (var3[0].equals("serverTextures"))
                    {
                        this.serverTextures = var3[1].equals("true");
                    }

                    if (var3[0].equals("snooperEnabled"))
                    {
                        this.snooperEnabled = var3[1].equals("true");
                    }

                    if (var3[0].equals("fullscreen"))
                    {
                        this.fullScreen = var3[1].equals("true");
                    }

                    if (var3[0].equals("enableVsync"))
                    {
                        this.enableVsync = var3[1].equals("true");
                        this.updateVSync();
                    }

                    if (var3[0].equals("hideServerAddress"))
                    {
                        this.hideServerAddress = var3[1].equals("true");
                    }

                    if (var3[0].equals("advancedItemTooltips"))
                    {
                        this.advancedItemTooltips = var3[1].equals("true");
                    }

                    if (var3[0].equals("pauseOnLostFocus"))
                    {
                        this.pauseOnLostFocus = var3[1].equals("true");
                    }

                    if (var3[0].equals("showCape"))
                    {
                        this.showCape = var3[1].equals("true");
                    }

                    if (var3[0].equals("touchscreen"))
                    {
                        this.touchscreen = var3[1].equals("true");
                    }

                    if (var3[0].equals("overrideHeight"))
                    {
                        this.overrideHeight = Integer.parseInt(var3[1]);
                    }

                    if (var3[0].equals("overrideWidth"))
                    {
                        this.overrideWidth = Integer.parseInt(var3[1]);
                    }

                    if (var3[0].equals("heldItemTooltips"))
                    {
                        this.heldItemTooltips = var3[1].equals("true");
                    }

                    if (var3[0].equals("chatHeightFocused"))
                    {
                        this.chatHeightFocused = this.parseFloat(var3[1]);
                    }

                    if (var3[0].equals("chatHeightUnfocused"))
                    {
                        this.chatHeightUnfocused = this.parseFloat(var3[1]);
                    }

                    if (var3[0].equals("chatScale"))
                    {
                        this.chatScale = this.parseFloat(var3[1]);
                    }

                    if (var3[0].equals("chatWidth"))
                    {
                        this.chatWidth = this.parseFloat(var3[1]);
                    }

                    for (int var4 = 0; var4 < this.keyBindings.length; ++var4)
                    {
                        if (var3[0].equals("key_" + this.keyBindings[var4].keyDescription))
                        {
                            this.keyBindings[var4].keyCode = Integer.parseInt(var3[1]);
                        }
                    }
                }
                catch (Exception var7)
                {
                    this.mc.getLogAgent().logWarning("Skipping bad option: " + var2);
                    var7.printStackTrace();
                }
            }

            KeyBinding.resetKeyBindingArrayAndHash();
            var1.close();
        }
        catch (Exception var8)
        {
            this.mc.getLogAgent().logWarning("Failed to load options");
            var8.printStackTrace();
        }

        try
        {
            File var9 = this.optionsFileOF;

            if (!var9.exists())
            {
                var9 = this.optionsFile;
            }

            if (!var9.exists())
            {
                return;
            }

            BufferedReader var10 = new BufferedReader(new FileReader(var9));
            String var11 = "";

            while ((var11 = var10.readLine()) != null)
            {
                try
                {
                    String[] var12 = var11.split(":");

                    if (var12[0].equals("ofRenderDistanceFine") && var12.length >= 2)
                    {
                        this.ofRenderDistanceFine = Integer.valueOf(var12[1]).intValue();
                        this.ofRenderDistanceFine = Config.limit(this.ofRenderDistanceFine, 32, 512);
                        this.renderDistance = fineToRenderDistance(this.ofRenderDistanceFine);
                    }

                    if (var12[0].equals("ofLimitFramerateFine") && var12.length >= 2)
                    {
                        this.ofLimitFramerateFine = Integer.valueOf(var12[1]).intValue();
                        this.ofLimitFramerateFine = Config.limit(this.ofLimitFramerateFine, 0, 199);
                        this.limitFramerate = fineToLimitFramerate(this.ofLimitFramerateFine);
                    }

                    if (var12[0].equals("ofFogType") && var12.length >= 2)
                    {
                        this.ofFogType = Integer.valueOf(var12[1]).intValue();
                        this.ofFogType = Config.limit(this.ofFogType, 1, 3);
                    }

                    if (var12[0].equals("ofFogStart") && var12.length >= 2)
                    {
                        this.ofFogStart = Float.valueOf(var12[1]).floatValue();

                        if (this.ofFogStart < 0.2F)
                        {
                            this.ofFogStart = 0.2F;
                        }

                        if (this.ofFogStart > 0.81F)
                        {
                            this.ofFogStart = 0.8F;
                        }
                    }

                    if (var12[0].equals("ofMipmapLevel") && var12.length >= 2)
                    {
                        this.ofMipmapLevel = Integer.valueOf(var12[1]).intValue();

                        if (this.ofMipmapLevel < 0)
                        {
                            this.ofMipmapLevel = 0;
                        }

                        if (this.ofMipmapLevel > 4)
                        {
                            this.ofMipmapLevel = 4;
                        }
                    }

                    if (var12[0].equals("ofMipmapType") && var12.length >= 2)
                    {
                        this.ofMipmapType = Integer.valueOf(var12[1]).intValue();
                        this.ofMipmapType = Config.limit(this.ofMipmapType, 0, 3);
                    }

                    if (var12[0].equals("ofLoadFar") && var12.length >= 2)
                    {
                        this.ofLoadFar = Boolean.valueOf(var12[1]).booleanValue();
                    }

                    if (var12[0].equals("ofPreloadedChunks") && var12.length >= 2)
                    {
                        this.ofPreloadedChunks = Integer.valueOf(var12[1]).intValue();

                        if (this.ofPreloadedChunks < 0)
                        {
                            this.ofPreloadedChunks = 0;
                        }

                        if (this.ofPreloadedChunks > 8)
                        {
                            this.ofPreloadedChunks = 8;
                        }
                    }

                    if (var12[0].equals("ofOcclusionFancy") && var12.length >= 2)
                    {
                        this.ofOcclusionFancy = Boolean.valueOf(var12[1]).booleanValue();
                    }

                    if (var12[0].equals("ofSmoothFps") && var12.length >= 2)
                    {
                        this.ofSmoothFps = Boolean.valueOf(var12[1]).booleanValue();
                    }

                    if (var12[0].equals("ofSmoothWorld") && var12.length >= 2)
                    {
                        this.ofSmoothWorld = Boolean.valueOf(var12[1]).booleanValue();
                    }

                    if (var12[0].equals("ofAoLevel") && var12.length >= 2)
                    {
                        this.ofAoLevel = Float.valueOf(var12[1]).floatValue();
                        this.ofAoLevel = Config.limit(this.ofAoLevel, 0.0F, 1.0F);
                    }

                    if (var12[0].equals("ofClouds") && var12.length >= 2)
                    {
                        this.ofClouds = Integer.valueOf(var12[1]).intValue();
                        this.ofClouds = Config.limit(this.ofClouds, 0, 3);
                    }

                    if (var12[0].equals("ofCloudsHeight") && var12.length >= 2)
                    {
                        this.ofCloudsHeight = Float.valueOf(var12[1]).floatValue();
                        this.ofCloudsHeight = Config.limit(this.ofCloudsHeight, 0.0F, 1.0F);
                    }

                    if (var12[0].equals("ofTrees") && var12.length >= 2)
                    {
                        this.ofTrees = Integer.valueOf(var12[1]).intValue();
                        this.ofTrees = Config.limit(this.ofTrees, 0, 2);
                    }

                    if (var12[0].equals("ofGrass") && var12.length >= 2)
                    {
                        this.ofGrass = Integer.valueOf(var12[1]).intValue();
                        this.ofGrass = Config.limit(this.ofGrass, 0, 2);
                    }

                    if (var12[0].equals("ofDroppedItems") && var12.length >= 2)
                    {
                        this.ofDroppedItems = Integer.valueOf(var12[1]).intValue();
                        this.ofDroppedItems = Config.limit(this.ofDroppedItems, 0, 2);
                    }

                    if (var12[0].equals("ofRain") && var12.length >= 2)
                    {
                        this.ofRain = Integer.valueOf(var12[1]).intValue();
                        this.ofRain = Config.limit(this.ofRain, 0, 3);
                    }

                    if (var12[0].equals("ofWater") && var12.length >= 2)
                    {
                        this.ofWater = Integer.valueOf(var12[1]).intValue();
                        this.ofWater = Config.limit(this.ofWater, 0, 3);
                    }

                    if (var12[0].equals("ofAnimatedWater") && var12.length >= 2)
                    {
                        this.ofAnimatedWater = Integer.valueOf(var12[1]).intValue();
                        this.ofAnimatedWater = Config.limit(this.ofAnimatedWater, 0, 2);
                    }

                    if (var12[0].equals("ofAnimatedLava") && var12.length >= 2)
                    {
                        this.ofAnimatedLava = Integer.valueOf(var12[1]).intValue();
                        this.ofAnimatedLava = Config.limit(this.ofAnimatedLava, 0, 2);
                    }

                    if (var12[0].equals("ofAnimatedFire") && var12.length >= 2)
                    {
                        this.ofAnimatedFire = Boolean.valueOf(var12[1]).booleanValue();
                    }

                    if (var12[0].equals("ofAnimatedPortal") && var12.length >= 2)
                    {
                        this.ofAnimatedPortal = Boolean.valueOf(var12[1]).booleanValue();
                    }

                    if (var12[0].equals("ofAnimatedRedstone") && var12.length >= 2)
                    {
                        this.ofAnimatedRedstone = Boolean.valueOf(var12[1]).booleanValue();
                    }

                    if (var12[0].equals("ofAnimatedExplosion") && var12.length >= 2)
                    {
                        this.ofAnimatedExplosion = Boolean.valueOf(var12[1]).booleanValue();
                    }

                    if (var12[0].equals("ofAnimatedFlame") && var12.length >= 2)
                    {
                        this.ofAnimatedFlame = Boolean.valueOf(var12[1]).booleanValue();
                    }

                    if (var12[0].equals("ofAnimatedSmoke") && var12.length >= 2)
                    {
                        this.ofAnimatedSmoke = Boolean.valueOf(var12[1]).booleanValue();
                    }

                    if (var12[0].equals("ofVoidParticles") && var12.length >= 2)
                    {
                        this.ofVoidParticles = Boolean.valueOf(var12[1]).booleanValue();
                    }

                    if (var12[0].equals("ofWaterParticles") && var12.length >= 2)
                    {
                        this.ofWaterParticles = Boolean.valueOf(var12[1]).booleanValue();
                    }

                    if (var12[0].equals("ofPortalParticles") && var12.length >= 2)
                    {
                        this.ofPortalParticles = Boolean.valueOf(var12[1]).booleanValue();
                    }

                    if (var12[0].equals("ofPotionParticles") && var12.length >= 2)
                    {
                        this.ofPotionParticles = Boolean.valueOf(var12[1]).booleanValue();
                    }

                    if (var12[0].equals("ofDrippingWaterLava") && var12.length >= 2)
                    {
                        this.ofDrippingWaterLava = Boolean.valueOf(var12[1]).booleanValue();
                    }

                    if (var12[0].equals("ofAnimatedTerrain") && var12.length >= 2)
                    {
                        this.ofAnimatedTerrain = Boolean.valueOf(var12[1]).booleanValue();
                    }

                    if (var12[0].equals("ofAnimatedTextures") && var12.length >= 2)
                    {
                        this.ofAnimatedTextures = Boolean.valueOf(var12[1]).booleanValue();
                    }

                    if (var12[0].equals("ofAnimatedItems") && var12.length >= 2)
                    {
                        this.ofAnimatedItems = Boolean.valueOf(var12[1]).booleanValue();
                    }

                    if (var12[0].equals("ofRainSplash") && var12.length >= 2)
                    {
                        this.ofRainSplash = Boolean.valueOf(var12[1]).booleanValue();
                    }

                    if (var12[0].equals("ofLagometer") && var12.length >= 2)
                    {
                        this.ofLagometer = Boolean.valueOf(var12[1]).booleanValue();
                    }

                    if (var12[0].equals("ofAutoSaveTicks") && var12.length >= 2)
                    {
                        this.ofAutoSaveTicks = Integer.valueOf(var12[1]).intValue();
                        this.ofAutoSaveTicks = Config.limit(this.ofAutoSaveTicks, 40, 40000);
                    }

                    if (var12[0].equals("ofBetterGrass") && var12.length >= 2)
                    {
                        this.ofBetterGrass = Integer.valueOf(var12[1]).intValue();
                        this.ofBetterGrass = Config.limit(this.ofBetterGrass, 1, 3);
                    }

                    if (var12[0].equals("ofConnectedTextures") && var12.length >= 2)
                    {
                        this.ofConnectedTextures = Integer.valueOf(var12[1]).intValue();
                        this.ofConnectedTextures = Config.limit(this.ofConnectedTextures, 1, 3);
                    }

                    if (var12[0].equals("ofWeather") && var12.length >= 2)
                    {
                        this.ofWeather = Boolean.valueOf(var12[1]).booleanValue();
                    }

                    if (var12[0].equals("ofSky") && var12.length >= 2)
                    {
                        this.ofSky = Boolean.valueOf(var12[1]).booleanValue();
                    }

                    if (var12[0].equals("ofStars") && var12.length >= 2)
                    {
                        this.ofStars = Boolean.valueOf(var12[1]).booleanValue();
                    }

                    if (var12[0].equals("ofSunMoon") && var12.length >= 2)
                    {
                        this.ofSunMoon = Boolean.valueOf(var12[1]).booleanValue();
                    }

                    if (var12[0].equals("ofChunkUpdates") && var12.length >= 2)
                    {
                        this.ofChunkUpdates = Integer.valueOf(var12[1]).intValue();
                        this.ofChunkUpdates = Config.limit(this.ofChunkUpdates, 1, 5);
                    }

                    if (var12[0].equals("ofChunkLoading") && var12.length >= 2)
                    {
                        this.ofChunkLoading = Integer.valueOf(var12[1]).intValue();
                        this.ofChunkLoading = Config.limit(this.ofChunkLoading, 0, 2);
                        this.updateChunkLoading();
                    }

                    if (var12[0].equals("ofChunkUpdatesDynamic") && var12.length >= 2)
                    {
                        this.ofChunkUpdatesDynamic = Boolean.valueOf(var12[1]).booleanValue();
                    }

                    if (var12[0].equals("ofTime") && var12.length >= 2)
                    {
                        this.ofTime = Integer.valueOf(var12[1]).intValue();
                        this.ofTime = Config.limit(this.ofTime, 0, 3);
                    }

                    if (var12[0].equals("ofClearWater") && var12.length >= 2)
                    {
                        this.ofClearWater = Boolean.valueOf(var12[1]).booleanValue();
                        this.updateWaterOpacity();
                    }

                    if (var12[0].equals("ofDepthFog") && var12.length >= 2)
                    {
                        this.ofDepthFog = Boolean.valueOf(var12[1]).booleanValue();
                    }

                    if (var12[0].equals("ofAaLevel") && var12.length >= 2)
                    {
                        this.ofAaLevel = Integer.valueOf(var12[1]).intValue();
                        this.ofAaLevel = Config.limit(this.ofAaLevel, 0, 16);
                    }

                    if (var12[0].equals("ofAfLevel") && var12.length >= 2)
                    {
                        this.ofAfLevel = Integer.valueOf(var12[1]).intValue();
                        this.ofAfLevel = Config.limit(this.ofAfLevel, 1, 16);
                    }

                    if (var12[0].equals("ofProfiler") && var12.length >= 2)
                    {
                        this.ofProfiler = Boolean.valueOf(var12[1]).booleanValue();
                    }

                    if (var12[0].equals("ofBetterSnow") && var12.length >= 2)
                    {
                        this.ofBetterSnow = Boolean.valueOf(var12[1]).booleanValue();
                    }

                    if (var12[0].equals("ofSwampColors") && var12.length >= 2)
                    {
                        this.ofSwampColors = Boolean.valueOf(var12[1]).booleanValue();
                    }

                    if (var12[0].equals("ofRandomMobs") && var12.length >= 2)
                    {
                        this.ofRandomMobs = Boolean.valueOf(var12[1]).booleanValue();
                    }

                    if (var12[0].equals("ofSmoothBiomes") && var12.length >= 2)
                    {
                        this.ofSmoothBiomes = Boolean.valueOf(var12[1]).booleanValue();
                    }

                    if (var12[0].equals("ofCustomFonts") && var12.length >= 2)
                    {
                        this.ofCustomFonts = Boolean.valueOf(var12[1]).booleanValue();
                    }

                    if (var12[0].equals("ofCustomColors") && var12.length >= 2)
                    {
                        this.ofCustomColors = Boolean.valueOf(var12[1]).booleanValue();
                    }

                    if (var12[0].equals("ofCustomSky") && var12.length >= 2)
                    {
                        this.ofCustomSky = Boolean.valueOf(var12[1]).booleanValue();
                    }

                    if (var12[0].equals("ofShowCapes") && var12.length >= 2)
                    {
                        this.ofShowCapes = Boolean.valueOf(var12[1]).booleanValue();
                    }

                    if (var12[0].equals("ofNaturalTextures") && var12.length >= 2)
                    {
                        this.ofNaturalTextures = Boolean.valueOf(var12[1]).booleanValue();
                    }

                    if (var12[0].equals("ofLazyChunkLoading") && var12.length >= 2)
                    {
                        this.ofLazyChunkLoading = Boolean.valueOf(var12[1]).booleanValue();
                    }

                    if (var12[0].equals("ofFullscreenMode") && var12.length >= 2)
                    {
                        this.ofFullscreenMode = var12[1];
                    }

                    if (var12[0].equals("ofFastMath") && var12.length >= 2)
                    {
                        this.ofFastMath = Boolean.valueOf(var12[1]).booleanValue();
                        MathHelper.fastMath = this.ofFastMath;
                    }
                }
                catch (Exception var5)
                {
                    Config.dbg("Skipping bad option: " + var11);
                    var5.printStackTrace();
                }
            }

            KeyBinding.resetKeyBindingArrayAndHash();
            var10.close();
        }
        catch (Exception var6)
        {
            Config.warn("Failed to load options");
            var6.printStackTrace();
        }
    }

    /**
     * Parses a string into a float.
     */
    private float parseFloat(String par1Str)
    {
        return par1Str.equals("true") ? 1.0F : (par1Str.equals("false") ? 0.0F : Float.parseFloat(par1Str));
    }

    /**
     * Saves the options to the options file.
     */
    public void saveOptions()
    {
        if (Reflector.FMLClientHandler.exists())
        {
            Object var1 = Reflector.call(Reflector.FMLClientHandler_instance, new Object[0]);

            if (var1 != null && Reflector.callBoolean(var1, Reflector.FMLClientHandler_isLoading, new Object[0]))
            {
                return;
            }
        }

        PrintWriter var5;

        try
        {
            var5 = new PrintWriter(new FileWriter(this.optionsFile));
            var5.println("music:" + this.musicVolume);
            var5.println("sound:" + this.soundVolume);
            var5.println("invertYMouse:" + this.invertMouse);
            var5.println("mouseSensitivity:" + this.mouseSensitivity);
            var5.println("fov:" + this.fovSetting);
            var5.println("gamma:" + this.gammaSetting);
            var5.println("viewDistance:" + this.renderDistance);
            var5.println("guiScale:" + this.guiScale);
            var5.println("particles:" + this.particleSetting);
            var5.println("bobView:" + this.viewBobbing);
            var5.println("anaglyph3d:" + this.anaglyph);
            var5.println("advancedOpengl:" + this.advancedOpengl);
            var5.println("fpsLimit:" + this.limitFramerate);
            var5.println("difficulty:" + this.difficulty);
            var5.println("fancyGraphics:" + this.fancyGraphics);
            var5.println("ao:" + this.ambientOcclusion);
            var5.println("clouds:" + this.clouds);
            var5.println("skin:" + this.skin);
            var5.println("lastServer:" + this.lastServer);
            var5.println("lang:" + this.language);
            var5.println("chatVisibility:" + this.chatVisibility);
            var5.println("chatColors:" + this.chatColours);
            var5.println("chatLinks:" + this.chatLinks);
            var5.println("chatLinksPrompt:" + this.chatLinksPrompt);
            var5.println("chatOpacity:" + this.chatOpacity);
            var5.println("serverTextures:" + this.serverTextures);
            var5.println("snooperEnabled:" + this.snooperEnabled);
            var5.println("fullscreen:" + this.fullScreen);
            var5.println("enableVsync:" + this.enableVsync);
            var5.println("hideServerAddress:" + this.hideServerAddress);
            var5.println("advancedItemTooltips:" + this.advancedItemTooltips);
            var5.println("pauseOnLostFocus:" + this.pauseOnLostFocus);
            var5.println("showCape:" + this.showCape);
            var5.println("touchscreen:" + this.touchscreen);
            var5.println("overrideWidth:" + this.overrideWidth);
            var5.println("overrideHeight:" + this.overrideHeight);
            var5.println("heldItemTooltips:" + this.heldItemTooltips);
            var5.println("chatHeightFocused:" + this.chatHeightFocused);
            var5.println("chatHeightUnfocused:" + this.chatHeightUnfocused);
            var5.println("chatScale:" + this.chatScale);
            var5.println("chatWidth:" + this.chatWidth);

            for (int var2 = 0; var2 < this.keyBindings.length; ++var2)
            {
                var5.println("key_" + this.keyBindings[var2].keyDescription + ":" + this.keyBindings[var2].keyCode);
            }

            var5.close();
        }
        catch (Exception var4)
        {
            this.mc.getLogAgent().logWarning("Failed to save options");
            var4.printStackTrace();
        }

        try
        {
            var5 = new PrintWriter(new FileWriter(this.optionsFileOF));
            var5.println("ofRenderDistanceFine:" + this.ofRenderDistanceFine);
            var5.println("ofLimitFramerateFine:" + this.ofLimitFramerateFine);
            var5.println("ofFogType:" + this.ofFogType);
            var5.println("ofFogStart:" + this.ofFogStart);
            var5.println("ofMipmapLevel:" + this.ofMipmapLevel);
            var5.println("ofMipmapType:" + this.ofMipmapType);
            var5.println("ofLoadFar:" + this.ofLoadFar);
            var5.println("ofPreloadedChunks:" + this.ofPreloadedChunks);
            var5.println("ofOcclusionFancy:" + this.ofOcclusionFancy);
            var5.println("ofSmoothFps:" + this.ofSmoothFps);
            var5.println("ofSmoothWorld:" + this.ofSmoothWorld);
            var5.println("ofAoLevel:" + this.ofAoLevel);
            var5.println("ofClouds:" + this.ofClouds);
            var5.println("ofCloudsHeight:" + this.ofCloudsHeight);
            var5.println("ofTrees:" + this.ofTrees);
            var5.println("ofGrass:" + this.ofGrass);
            var5.println("ofDroppedItems:" + this.ofDroppedItems);
            var5.println("ofRain:" + this.ofRain);
            var5.println("ofWater:" + this.ofWater);
            var5.println("ofAnimatedWater:" + this.ofAnimatedWater);
            var5.println("ofAnimatedLava:" + this.ofAnimatedLava);
            var5.println("ofAnimatedFire:" + this.ofAnimatedFire);
            var5.println("ofAnimatedPortal:" + this.ofAnimatedPortal);
            var5.println("ofAnimatedRedstone:" + this.ofAnimatedRedstone);
            var5.println("ofAnimatedExplosion:" + this.ofAnimatedExplosion);
            var5.println("ofAnimatedFlame:" + this.ofAnimatedFlame);
            var5.println("ofAnimatedSmoke:" + this.ofAnimatedSmoke);
            var5.println("ofVoidParticles:" + this.ofVoidParticles);
            var5.println("ofWaterParticles:" + this.ofWaterParticles);
            var5.println("ofPortalParticles:" + this.ofPortalParticles);
            var5.println("ofPotionParticles:" + this.ofPotionParticles);
            var5.println("ofDrippingWaterLava:" + this.ofDrippingWaterLava);
            var5.println("ofAnimatedTerrain:" + this.ofAnimatedTerrain);
            var5.println("ofAnimatedTextures:" + this.ofAnimatedTextures);
            var5.println("ofAnimatedItems:" + this.ofAnimatedItems);
            var5.println("ofRainSplash:" + this.ofRainSplash);
            var5.println("ofLagometer:" + this.ofLagometer);
            var5.println("ofAutoSaveTicks:" + this.ofAutoSaveTicks);
            var5.println("ofBetterGrass:" + this.ofBetterGrass);
            var5.println("ofConnectedTextures:" + this.ofConnectedTextures);
            var5.println("ofWeather:" + this.ofWeather);
            var5.println("ofSky:" + this.ofSky);
            var5.println("ofStars:" + this.ofStars);
            var5.println("ofSunMoon:" + this.ofSunMoon);
            var5.println("ofChunkUpdates:" + this.ofChunkUpdates);
            var5.println("ofChunkLoading:" + this.ofChunkLoading);
            var5.println("ofChunkUpdatesDynamic:" + this.ofChunkUpdatesDynamic);
            var5.println("ofTime:" + this.ofTime);
            var5.println("ofClearWater:" + this.ofClearWater);
            var5.println("ofDepthFog:" + this.ofDepthFog);
            var5.println("ofAaLevel:" + this.ofAaLevel);
            var5.println("ofAfLevel:" + this.ofAfLevel);
            var5.println("ofProfiler:" + this.ofProfiler);
            var5.println("ofBetterSnow:" + this.ofBetterSnow);
            var5.println("ofSwampColors:" + this.ofSwampColors);
            var5.println("ofRandomMobs:" + this.ofRandomMobs);
            var5.println("ofSmoothBiomes:" + this.ofSmoothBiomes);
            var5.println("ofCustomFonts:" + this.ofCustomFonts);
            var5.println("ofCustomColors:" + this.ofCustomColors);
            var5.println("ofCustomSky:" + this.ofCustomSky);
            var5.println("ofShowCapes:" + this.ofShowCapes);
            var5.println("ofNaturalTextures:" + this.ofNaturalTextures);
            var5.println("ofLazyChunkLoading:" + this.ofLazyChunkLoading);
            var5.println("ofFullscreenMode:" + this.ofFullscreenMode);
            var5.println("ofFastMath:" + this.ofFastMath);
            var5.close();
        }
        catch (Exception var3)
        {
            Config.warn("Failed to save options");
            var3.printStackTrace();
        }

        this.sendSettingsToServer();
    }

    /**
     * Send a client info packet with settings information to the server
     */
    public void sendSettingsToServer()
    {
        if (this.mc.thePlayer != null)
        {
            this.mc.thePlayer.sendQueue.addToSendQueue(new Packet204ClientInfo(this.language, this.renderDistance, this.chatVisibility, this.chatColours, this.difficulty, this.showCape));
        }
    }

    public void resetSettings()
    {
        this.renderDistance = 1;
        this.ofRenderDistanceFine = renderDistanceToFine(this.renderDistance);
        this.viewBobbing = true;
        this.anaglyph = false;
        this.advancedOpengl = false;
        this.limitFramerate = 0;
        this.enableVsync = false;
        this.updateVSync();
        this.ofLimitFramerateFine = 0;
        this.fancyGraphics = true;
        this.ambientOcclusion = 2;
        this.clouds = true;
        this.fovSetting = 0.0F;
        this.gammaSetting = 0.0F;
        this.guiScale = 0;
        this.particleSetting = 0;
        this.heldItemTooltips = true;
        this.ofFogType = 1;
        this.ofFogStart = 0.8F;
        this.ofMipmapLevel = 0;
        this.ofMipmapType = 0;
        this.ofLoadFar = false;
        this.ofPreloadedChunks = 0;
        this.ofOcclusionFancy = false;
        this.ofSmoothFps = false;
        this.ofSmoothWorld = Config.isSingleProcessor();
        this.ofLazyChunkLoading = Config.isSingleProcessor();
        this.ofFastMath = false;
        this.ofAoLevel = 1.0F;
        this.ofAaLevel = 0;
        this.ofAfLevel = 1;
        this.ofClouds = 0;
        this.ofCloudsHeight = 0.0F;
        this.ofTrees = 0;
        this.ofGrass = 0;
        this.ofRain = 0;
        this.ofWater = 0;
        this.ofBetterGrass = 3;
        this.ofAutoSaveTicks = 4000;
        this.ofLagometer = false;
        this.ofProfiler = false;
        this.ofWeather = true;
        this.ofSky = true;
        this.ofStars = true;
        this.ofSunMoon = true;
        this.ofChunkUpdates = 1;
        this.ofChunkLoading = 0;
        this.ofChunkUpdatesDynamic = false;
        this.ofTime = 0;
        this.ofClearWater = false;
        this.ofDepthFog = true;
        this.ofBetterSnow = false;
        this.ofFullscreenMode = "Default";
        this.ofSwampColors = true;
        this.ofRandomMobs = true;
        this.ofSmoothBiomes = true;
        this.ofCustomFonts = true;
        this.ofCustomColors = true;
        this.ofCustomSky = true;
        this.ofShowCapes = true;
        this.ofConnectedTextures = 2;
        this.ofNaturalTextures = false;
        this.ofAnimatedWater = 0;
        this.ofAnimatedLava = 0;
        this.ofAnimatedFire = true;
        this.ofAnimatedPortal = true;
        this.ofAnimatedRedstone = true;
        this.ofAnimatedExplosion = true;
        this.ofAnimatedFlame = true;
        this.ofAnimatedSmoke = true;
        this.ofVoidParticles = true;
        this.ofWaterParticles = true;
        this.ofRainSplash = true;
        this.ofPortalParticles = true;
        this.ofPotionParticles = true;
        this.ofDrippingWaterLava = true;
        this.ofAnimatedTerrain = true;
        this.ofAnimatedItems = true;
        this.ofAnimatedTextures = true;
        this.mc.renderGlobal.updateCapes();
        this.updateWaterOpacity();
        this.mc.renderGlobal.setAllRenderersVisible();
        this.mc.refreshResources();
        this.saveOptions();
    }

    public void updateVSync()
    {
        Display.setVSyncEnabled(this.enableVsync);
    }

    private static int fineToRenderDistance(int rdFine)
    {
        byte rd = 3;

        if (rdFine > 32)
        {
            rd = 2;
        }

        if (rdFine > 64)
        {
            rd = 1;
        }

        if (rdFine > 128)
        {
            rd = 0;
        }

        return rd;
    }

    private static int renderDistanceToFine(int rd)
    {
        return 32 << 3 - rd;
    }

    private static int fineToLimitFramerate(int fine)
    {
        byte limit = 2;

        if (fine > 35)
        {
            limit = 1;
        }

        if (fine >= 200)
        {
            limit = 0;
        }

        if (fine <= 0)
        {
            limit = 0;
        }

        return limit;
    }

    private static int limitFramerateToFine(int limit)
    {
        switch (limit)
        {
            case 0:
                return 0;

            case 1:
                return 120;

            case 2:
                return 35;

            default:
                return 0;
        }
    }

    /**
     * Should render clouds
     */
    public boolean shouldRenderClouds()
    {
        return this.ofRenderDistanceFine > 64 && this.clouds;
    }
}

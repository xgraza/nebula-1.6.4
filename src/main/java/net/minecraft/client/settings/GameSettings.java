package net.minecraft.client.settings;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.src.ClearWater;
import net.minecraft.src.Config;
import net.minecraft.src.CustomColorizer;
import net.minecraft.src.CustomSky;
import net.minecraft.src.DynamicLights;
import net.minecraft.src.IWrUpdater;
import net.minecraft.src.Lang;
import net.minecraft.src.NaturalTextures;
import net.minecraft.src.RandomMobs;
import net.minecraft.src.Reflector;
import net.minecraft.src.TextureUtils;
import net.minecraft.src.WrUpdaterSmooth;
import net.minecraft.src.WrUpdaterThreaded;
import net.minecraft.src.WrUpdates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import shadersmod.client.Shaders;

public class GameSettings
{
    private static final Logger logger = LogManager.getLogger();
    private static final Gson gson = new Gson();
    private static final ParameterizedType typeListString = new ParameterizedType()
    {
        public Type[] getActualTypeArguments()
        {
            return new Type[] {String.class};
        }
        public Type getRawType()
        {
            return List.class;
        }
        public Type getOwnerType()
        {
            return null;
        }
    };
    private static final String[] GUISCALES = new String[] {"options.guiScale.auto", "options.guiScale.small", "options.guiScale.normal", "options.guiScale.large"};
    private static final String[] PARTICLES = new String[] {"options.particles.all", "options.particles.decreased", "options.particles.minimal"};
    private static final String[] AMBIENT_OCCLUSIONS = new String[] {"options.ao.off", "options.ao.min", "options.ao.max"};
    public float mouseSensitivity = 0.5F;
    public boolean invertMouse;
    public int renderDistanceChunks = -1;
    public boolean viewBobbing = true;
    public boolean anaglyph;
    public boolean advancedOpengl;
    public boolean fboEnable = true;
    public int limitFramerate = 120;
    public boolean fancyGraphics = true;
    public int ambientOcclusion = 2;
    public int ofFogType = 1;
    public float ofFogStart = 0.8F;
    public int ofMipmapType = 0;
    public boolean ofLoadFar = false;
    public int ofPreloadedChunks = 0;
    public boolean ofOcclusionFancy = false;
    public boolean ofSmoothFps = false;
    public boolean ofSmoothWorld = Config.isSingleProcessor();
    public boolean ofLazyChunkLoading = Config.isSingleProcessor();
    public float ofAoLevel = 1.0F;
    public int ofAaLevel = 0;
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
    public boolean ofShowFps = false;
    public boolean ofWeather = true;
    public boolean ofSky = true;
    public boolean ofStars = true;
    public boolean ofSunMoon = true;
    public int ofVignette = 0;
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
    public boolean ofFastRender = false;
    public int ofTranslucentBlocks = 2;
    public boolean ofDynamicFov = true;
    public int ofDynamicLights = 3;
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
    private static final int[] OF_DYNAMIC_LIGHTS = new int[] {3, 1, 2};
    private static final String[] KEYS_DYNAMIC_LIGHTS = new String[] {"options.off", "options.graphics.fast", "options.graphics.fancy"};
    public KeyBinding ofKeyBindZoom;
    private File optionsFileOF;
    public boolean clouds = true;
    public List resourcePacks = new ArrayList();
    public EntityPlayer.EnumChatVisibility chatVisibility;
    public boolean chatColours;
    public boolean chatLinks;
    public boolean chatLinksPrompt;
    public float chatOpacity;
    public boolean serverTextures;
    public boolean snooperEnabled;
    public boolean fullScreen;
    public boolean enableVsync;
    public boolean hideServerAddress;
    public boolean advancedItemTooltips;
    public boolean pauseOnLostFocus;
    public boolean showCape;
    public boolean touchscreen;
    public int overrideWidth;
    public int overrideHeight;
    public boolean heldItemTooltips;
    public float chatScale;
    public float chatWidth;
    public float chatHeightUnfocused;
    public float chatHeightFocused;
    public boolean showInventoryAchievementHint;
    public int mipmapLevels;
    public int anisotropicFiltering;
    private Map mapSoundLevels;
    public KeyBinding keyBindForward;
    public KeyBinding keyBindLeft;
    public KeyBinding keyBindBack;
    public KeyBinding keyBindRight;
    public KeyBinding keyBindJump;
    public KeyBinding keyBindSneak;
    public KeyBinding keyBindInventory;
    public KeyBinding keyBindUseItem;
    public KeyBinding keyBindDrop;
    public KeyBinding keyBindAttack;
    public KeyBinding keyBindPickBlock;
    public KeyBinding keyBindSprint;
    public KeyBinding keyBindChat;
    public KeyBinding keyBindPlayerList;
    public KeyBinding keyBindCommand;
    public KeyBinding keyBindScreenshot;
    public KeyBinding keyBindTogglePerspective;
    public KeyBinding keyBindSmoothCamera;
    public KeyBinding[] keyBindsHotbar;
    public KeyBinding[] keyBindings;
    protected Minecraft mc;
    private File optionsFile;
    public EnumDifficulty difficulty;
    public boolean hideGUI;
    public int thirdPersonView;
    public boolean showDebugInfo;
    public boolean showDebugProfilerChart;
    public String lastServer;
    public boolean noclip;
    public boolean smoothCamera;
    public boolean debugCamEnable;
    public float noclipRate;
    public float debugCamRate;
    public float fovSetting;
    public float gammaSetting;
    public float saturation;
    public int guiScale;
    public int particleSetting;
    public String language;
    public boolean forceUnicodeFont;

    public GameSettings(Minecraft par1Minecraft, File par2File)
    {
        this.chatVisibility = EntityPlayer.EnumChatVisibility.FULL;
        this.chatColours = true;
        this.chatLinks = true;
        this.chatLinksPrompt = true;
        this.chatOpacity = 1.0F;
        this.serverTextures = true;
        this.snooperEnabled = true;
        this.enableVsync = true;
        this.pauseOnLostFocus = true;
        this.showCape = true;
        this.heldItemTooltips = true;
        this.chatScale = 1.0F;
        this.chatWidth = 1.0F;
        this.chatHeightUnfocused = 0.44366196F;
        this.chatHeightFocused = 1.0F;
        this.showInventoryAchievementHint = true;
        this.mipmapLevels = 4;
        this.anisotropicFiltering = 1;
        this.mapSoundLevels = Maps.newEnumMap(SoundCategory.class);
        this.keyBindForward = new KeyBinding("key.forward", 17, "key.categories.movement");
        this.keyBindLeft = new KeyBinding("key.left", 30, "key.categories.movement");
        this.keyBindBack = new KeyBinding("key.back", 31, "key.categories.movement");
        this.keyBindRight = new KeyBinding("key.right", 32, "key.categories.movement");
        this.keyBindJump = new KeyBinding("key.jump", 57, "key.categories.movement");
        this.keyBindSneak = new KeyBinding("key.sneak", 42, "key.categories.movement");
        this.keyBindInventory = new KeyBinding("key.inventory", 18, "key.categories.inventory");
        this.keyBindUseItem = new KeyBinding("key.use", -99, "key.categories.gameplay");
        this.keyBindDrop = new KeyBinding("key.drop", 16, "key.categories.gameplay");
        this.keyBindAttack = new KeyBinding("key.attack", -100, "key.categories.gameplay");
        this.keyBindPickBlock = new KeyBinding("key.pickItem", -98, "key.categories.gameplay");
        this.keyBindSprint = new KeyBinding("key.sprint", 29, "key.categories.gameplay");
        this.keyBindChat = new KeyBinding("key.chat", 20, "key.categories.multiplayer");
        this.keyBindPlayerList = new KeyBinding("key.playerlist", 15, "key.categories.multiplayer");
        this.keyBindCommand = new KeyBinding("key.command", 53, "key.categories.multiplayer");
        this.keyBindScreenshot = new KeyBinding("key.screenshot", 60, "key.categories.misc");
        this.keyBindTogglePerspective = new KeyBinding("key.togglePerspective", 63, "key.categories.misc");
        this.keyBindSmoothCamera = new KeyBinding("key.smoothCamera", 0, "key.categories.misc");
        this.keyBindsHotbar = new KeyBinding[] {new KeyBinding("key.hotbar.1", 2, "key.categories.inventory"), new KeyBinding("key.hotbar.2", 3, "key.categories.inventory"), new KeyBinding("key.hotbar.3", 4, "key.categories.inventory"), new KeyBinding("key.hotbar.4", 5, "key.categories.inventory"), new KeyBinding("key.hotbar.5", 6, "key.categories.inventory"), new KeyBinding("key.hotbar.6", 7, "key.categories.inventory"), new KeyBinding("key.hotbar.7", 8, "key.categories.inventory"), new KeyBinding("key.hotbar.8", 9, "key.categories.inventory"), new KeyBinding("key.hotbar.9", 10, "key.categories.inventory")};
        this.keyBindings = (KeyBinding[])((KeyBinding[])ArrayUtils.addAll(new KeyBinding[] {this.keyBindAttack, this.keyBindUseItem, this.keyBindForward, this.keyBindLeft, this.keyBindBack, this.keyBindRight, this.keyBindJump, this.keyBindSneak, this.keyBindDrop, this.keyBindInventory, this.keyBindChat, this.keyBindPlayerList, this.keyBindPickBlock, this.keyBindCommand, this.keyBindScreenshot, this.keyBindTogglePerspective, this.keyBindSmoothCamera, this.keyBindSprint}, this.keyBindsHotbar));
        this.difficulty = EnumDifficulty.NORMAL;
        this.lastServer = "";
        this.noclipRate = 1.0F;
        this.debugCamRate = 1.0F;
        this.language = "en_US";
        this.forceUnicodeFont = false;
        this.mc = par1Minecraft;
        this.optionsFile = new File(par2File, "options.txt");
        this.optionsFileOF = new File(par2File, "optionsof.txt");
        this.limitFramerate = (int)GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
        this.ofKeyBindZoom = new KeyBinding("of.key.zoom", 29, "key.categories.misc");
        this.keyBindings = (KeyBinding[])((KeyBinding[])ArrayUtils.add(this.keyBindings, this.ofKeyBindZoom));
        GameSettings.Options.RENDER_DISTANCE.setValueMax(32.0F);
        this.renderDistanceChunks = par1Minecraft.isJava64bit() ? 12 : 8;
        this.loadOptions();
        Config.initGameSettings(this);
    }

    public GameSettings()
    {
        this.chatVisibility = EntityPlayer.EnumChatVisibility.FULL;
        this.chatColours = true;
        this.chatLinks = true;
        this.chatLinksPrompt = true;
        this.chatOpacity = 1.0F;
        this.serverTextures = true;
        this.snooperEnabled = true;
        this.enableVsync = true;
        this.pauseOnLostFocus = true;
        this.showCape = true;
        this.heldItemTooltips = true;
        this.chatScale = 1.0F;
        this.chatWidth = 1.0F;
        this.chatHeightUnfocused = 0.44366196F;
        this.chatHeightFocused = 1.0F;
        this.showInventoryAchievementHint = true;
        this.mipmapLevels = 4;
        this.anisotropicFiltering = 1;
        this.mapSoundLevels = Maps.newEnumMap(SoundCategory.class);
        this.keyBindForward = new KeyBinding("key.forward", 17, "key.categories.movement");
        this.keyBindLeft = new KeyBinding("key.left", 30, "key.categories.movement");
        this.keyBindBack = new KeyBinding("key.back", 31, "key.categories.movement");
        this.keyBindRight = new KeyBinding("key.right", 32, "key.categories.movement");
        this.keyBindJump = new KeyBinding("key.jump", 57, "key.categories.movement");
        this.keyBindSneak = new KeyBinding("key.sneak", 42, "key.categories.movement");
        this.keyBindInventory = new KeyBinding("key.inventory", 18, "key.categories.inventory");
        this.keyBindUseItem = new KeyBinding("key.use", -99, "key.categories.gameplay");
        this.keyBindDrop = new KeyBinding("key.drop", 16, "key.categories.gameplay");
        this.keyBindAttack = new KeyBinding("key.attack", -100, "key.categories.gameplay");
        this.keyBindPickBlock = new KeyBinding("key.pickItem", -98, "key.categories.gameplay");
        this.keyBindSprint = new KeyBinding("key.sprint", 29, "key.categories.gameplay");
        this.keyBindChat = new KeyBinding("key.chat", 20, "key.categories.multiplayer");
        this.keyBindPlayerList = new KeyBinding("key.playerlist", 15, "key.categories.multiplayer");
        this.keyBindCommand = new KeyBinding("key.command", 53, "key.categories.multiplayer");
        this.keyBindScreenshot = new KeyBinding("key.screenshot", 60, "key.categories.misc");
        this.keyBindTogglePerspective = new KeyBinding("key.togglePerspective", 63, "key.categories.misc");
        this.keyBindSmoothCamera = new KeyBinding("key.smoothCamera", 0, "key.categories.misc");
        this.keyBindsHotbar = new KeyBinding[] {new KeyBinding("key.hotbar.1", 2, "key.categories.inventory"), new KeyBinding("key.hotbar.2", 3, "key.categories.inventory"), new KeyBinding("key.hotbar.3", 4, "key.categories.inventory"), new KeyBinding("key.hotbar.4", 5, "key.categories.inventory"), new KeyBinding("key.hotbar.5", 6, "key.categories.inventory"), new KeyBinding("key.hotbar.6", 7, "key.categories.inventory"), new KeyBinding("key.hotbar.7", 8, "key.categories.inventory"), new KeyBinding("key.hotbar.8", 9, "key.categories.inventory"), new KeyBinding("key.hotbar.9", 10, "key.categories.inventory")};
        this.keyBindings = (KeyBinding[])((KeyBinding[])ArrayUtils.addAll(new KeyBinding[] {this.keyBindAttack, this.keyBindUseItem, this.keyBindForward, this.keyBindLeft, this.keyBindBack, this.keyBindRight, this.keyBindJump, this.keyBindSneak, this.keyBindDrop, this.keyBindInventory, this.keyBindChat, this.keyBindPlayerList, this.keyBindPickBlock, this.keyBindCommand, this.keyBindScreenshot, this.keyBindTogglePerspective, this.keyBindSmoothCamera, this.keyBindSprint}, this.keyBindsHotbar));
        this.difficulty = EnumDifficulty.NORMAL;
        this.lastServer = "";
        this.noclipRate = 1.0F;
        this.debugCamRate = 1.0F;
        this.language = "en_US";
        this.forceUnicodeFont = false;
        this.limitFramerate = (int)GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
        this.ofKeyBindZoom = new KeyBinding("of.key.zoom", 29, "key.categories.misc");
        this.keyBindings = (KeyBinding[])((KeyBinding[])ArrayUtils.add(this.keyBindings, this.ofKeyBindZoom));
    }

    public static String getKeyDisplayString(int par0)
    {
        return par0 < 0 ? I18n.format("key.mouseButton", new Object[] {Integer.valueOf(par0 + 101)}): Keyboard.getKeyName(par0);
    }

    public static boolean isKeyDown(KeyBinding par0KeyBinding)
    {
        return par0KeyBinding.getKeyCode() == 0 ? false : (par0KeyBinding.getKeyCode() < 0 ? Mouse.isButtonDown(par0KeyBinding.getKeyCode() + 100) : Keyboard.isKeyDown(par0KeyBinding.getKeyCode()));
    }

    public void setKeyCodeSave(KeyBinding p_151440_1_, int p_151440_2_)
    {
        p_151440_1_.setKeyCode(p_151440_2_);
        this.saveOptions();
    }

    public void setOptionFloatValue(GameSettings.Options par1EnumOptions, float par2)
    {
        if (par1EnumOptions == GameSettings.Options.CLOUD_HEIGHT)
        {
            this.ofCloudsHeight = par2;
        }

        if (par1EnumOptions == GameSettings.Options.AO_LEVEL)
        {
            this.ofAoLevel = par2;
            this.mc.renderGlobal.loadRenderers();
        }

        int var3;

        if (par1EnumOptions == GameSettings.Options.MIPMAP_TYPE)
        {
            var3 = (int)par2;
            this.ofMipmapType = Config.limit(var3, 0, 3);
            TextureUtils.refreshBlockTextures();
        }

        if (par1EnumOptions == GameSettings.Options.SENSITIVITY)
        {
            this.mouseSensitivity = par2;
        }

        if (par1EnumOptions == GameSettings.Options.FOV)
        {
            this.fovSetting = par2;
        }

        if (par1EnumOptions == GameSettings.Options.GAMMA)
        {
            this.gammaSetting = par2;
        }

        if (par1EnumOptions == GameSettings.Options.FRAMERATE_LIMIT)
        {
            this.limitFramerate = (int)par2;
            this.enableVsync = false;

            if (this.limitFramerate <= 0)
            {
                this.limitFramerate = (int)GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
                this.enableVsync = true;
            }

            this.updateVSync();
        }

        if (par1EnumOptions == GameSettings.Options.CHAT_OPACITY)
        {
            this.chatOpacity = par2;
            this.mc.ingameGUI.getChatGui().refreshChat();
        }

        if (par1EnumOptions == GameSettings.Options.CHAT_HEIGHT_FOCUSED)
        {
            this.chatHeightFocused = par2;
            this.mc.ingameGUI.getChatGui().refreshChat();
        }

        if (par1EnumOptions == GameSettings.Options.CHAT_HEIGHT_UNFOCUSED)
        {
            this.chatHeightUnfocused = par2;
            this.mc.ingameGUI.getChatGui().refreshChat();
        }

        if (par1EnumOptions == GameSettings.Options.CHAT_WIDTH)
        {
            this.chatWidth = par2;
            this.mc.ingameGUI.getChatGui().refreshChat();
        }

        if (par1EnumOptions == GameSettings.Options.CHAT_SCALE)
        {
            this.chatScale = par2;
            this.mc.ingameGUI.getChatGui().refreshChat();
        }

        if (par1EnumOptions == GameSettings.Options.ANISOTROPIC_FILTERING)
        {
            var3 = this.anisotropicFiltering;

            if (var3 > 1 && Config.isShaders())
            {
                Config.showGuiMessage(Lang.get("of.message.af.shaders1"), Lang.get("of.message.af.shaders2"));
                return;
            }

            this.anisotropicFiltering = (int)par2;

            if ((float)var3 != par2)
            {
                this.mc.getTextureMapBlocks().setAnisotropicFiltering(this.anisotropicFiltering);
                this.mc.scheduleResourcesRefresh();
            }
        }

        if (par1EnumOptions == GameSettings.Options.MIPMAP_LEVELS)
        {
            var3 = this.mipmapLevels;
            this.mipmapLevels = (int)par2;

            if ((float)var3 != par2)
            {
                this.mc.getTextureMapBlocks().setMipmapLevels(this.mipmapLevels);
                this.mc.scheduleResourcesRefresh();
            }
        }

        if (par1EnumOptions == GameSettings.Options.RENDER_DISTANCE)
        {
            this.renderDistanceChunks = (int)par2;
        }
    }

    public void setOptionValue(GameSettings.Options par1EnumOptions, int par2)
    {
        if (par1EnumOptions == GameSettings.Options.FOG_FANCY)
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

        if (par1EnumOptions == GameSettings.Options.FOG_START)
        {
            this.ofFogStart += 0.2F;

            if (this.ofFogStart > 0.81F)
            {
                this.ofFogStart = 0.2F;
            }
        }

        if (par1EnumOptions == GameSettings.Options.LOAD_FAR)
        {
            this.ofLoadFar = !this.ofLoadFar;
            this.mc.renderGlobal.loadRenderers();
        }

        if (par1EnumOptions == GameSettings.Options.PRELOADED_CHUNKS)
        {
            this.ofPreloadedChunks += 2;

            if (this.ofPreloadedChunks > 8)
            {
                this.ofPreloadedChunks = 0;
            }

            this.mc.renderGlobal.loadRenderers();
        }

        if (par1EnumOptions == GameSettings.Options.SMOOTH_FPS)
        {
            this.ofSmoothFps = !this.ofSmoothFps;
        }

        if (par1EnumOptions == GameSettings.Options.SMOOTH_WORLD)
        {
            this.ofSmoothWorld = !this.ofSmoothWorld;
            Config.updateAvailableProcessors();

            if (!Config.isSingleProcessor())
            {
                this.ofSmoothWorld = false;
            }

            Config.updateThreadPriorities();
        }

        if (par1EnumOptions == GameSettings.Options.CLOUDS)
        {
            ++this.ofClouds;

            if (this.ofClouds > 3)
            {
                this.ofClouds = 0;
            }
        }

        if (par1EnumOptions == GameSettings.Options.TREES)
        {
            ++this.ofTrees;

            if (this.ofTrees > 2)
            {
                this.ofTrees = 0;
            }

            this.mc.renderGlobal.loadRenderers();
        }

        if (par1EnumOptions == GameSettings.Options.GRASS)
        {
            ++this.ofGrass;

            if (this.ofGrass > 2)
            {
                this.ofGrass = 0;
            }

            RenderBlocks.fancyGrass = Config.isGrassFancy();
            this.mc.renderGlobal.loadRenderers();
        }

        if (par1EnumOptions == GameSettings.Options.DROPPED_ITEMS)
        {
            ++this.ofDroppedItems;

            if (this.ofDroppedItems > 2)
            {
                this.ofDroppedItems = 0;
            }
        }

        if (par1EnumOptions == GameSettings.Options.RAIN)
        {
            ++this.ofRain;

            if (this.ofRain > 3)
            {
                this.ofRain = 0;
            }
        }

        if (par1EnumOptions == GameSettings.Options.WATER)
        {
            ++this.ofWater;

            if (this.ofWater > 2)
            {
                this.ofWater = 0;
            }
        }

        if (par1EnumOptions == GameSettings.Options.ANIMATED_WATER)
        {
            ++this.ofAnimatedWater;

            if (this.ofAnimatedWater == 1)
            {
                ++this.ofAnimatedWater;
            }

            if (this.ofAnimatedWater > 2)
            {
                this.ofAnimatedWater = 0;
            }
        }

        if (par1EnumOptions == GameSettings.Options.ANIMATED_LAVA)
        {
            ++this.ofAnimatedLava;

            if (this.ofAnimatedLava == 1)
            {
                ++this.ofAnimatedLava;
            }

            if (this.ofAnimatedLava > 2)
            {
                this.ofAnimatedLava = 0;
            }
        }

        if (par1EnumOptions == GameSettings.Options.ANIMATED_FIRE)
        {
            this.ofAnimatedFire = !this.ofAnimatedFire;
        }

        if (par1EnumOptions == GameSettings.Options.ANIMATED_PORTAL)
        {
            this.ofAnimatedPortal = !this.ofAnimatedPortal;
        }

        if (par1EnumOptions == GameSettings.Options.ANIMATED_REDSTONE)
        {
            this.ofAnimatedRedstone = !this.ofAnimatedRedstone;
        }

        if (par1EnumOptions == GameSettings.Options.ANIMATED_EXPLOSION)
        {
            this.ofAnimatedExplosion = !this.ofAnimatedExplosion;
        }

        if (par1EnumOptions == GameSettings.Options.ANIMATED_FLAME)
        {
            this.ofAnimatedFlame = !this.ofAnimatedFlame;
        }

        if (par1EnumOptions == GameSettings.Options.ANIMATED_SMOKE)
        {
            this.ofAnimatedSmoke = !this.ofAnimatedSmoke;
        }

        if (par1EnumOptions == GameSettings.Options.VOID_PARTICLES)
        {
            this.ofVoidParticles = !this.ofVoidParticles;
        }

        if (par1EnumOptions == GameSettings.Options.WATER_PARTICLES)
        {
            this.ofWaterParticles = !this.ofWaterParticles;
        }

        if (par1EnumOptions == GameSettings.Options.PORTAL_PARTICLES)
        {
            this.ofPortalParticles = !this.ofPortalParticles;
        }

        if (par1EnumOptions == GameSettings.Options.POTION_PARTICLES)
        {
            this.ofPotionParticles = !this.ofPotionParticles;
        }

        if (par1EnumOptions == GameSettings.Options.DRIPPING_WATER_LAVA)
        {
            this.ofDrippingWaterLava = !this.ofDrippingWaterLava;
        }

        if (par1EnumOptions == GameSettings.Options.ANIMATED_TERRAIN)
        {
            this.ofAnimatedTerrain = !this.ofAnimatedTerrain;
        }

        if (par1EnumOptions == GameSettings.Options.ANIMATED_TEXTURES)
        {
            this.ofAnimatedTextures = !this.ofAnimatedTextures;
        }

        if (par1EnumOptions == GameSettings.Options.ANIMATED_ITEMS)
        {
            this.ofAnimatedItems = !this.ofAnimatedItems;
        }

        if (par1EnumOptions == GameSettings.Options.RAIN_SPLASH)
        {
            this.ofRainSplash = !this.ofRainSplash;
        }

        if (par1EnumOptions == GameSettings.Options.LAGOMETER)
        {
            this.ofLagometer = !this.ofLagometer;
        }

        if (par1EnumOptions == GameSettings.Options.SHOW_FPS)
        {
            this.ofShowFps = !this.ofShowFps;
        }

        if (par1EnumOptions == GameSettings.Options.AUTOSAVE_TICKS)
        {
            this.ofAutoSaveTicks *= 10;

            if (this.ofAutoSaveTicks > 40000)
            {
                this.ofAutoSaveTicks = 40;
            }
        }

        if (par1EnumOptions == GameSettings.Options.BETTER_GRASS)
        {
            ++this.ofBetterGrass;

            if (this.ofBetterGrass > 3)
            {
                this.ofBetterGrass = 1;
            }

            this.mc.renderGlobal.loadRenderers();
        }

        if (par1EnumOptions == GameSettings.Options.CONNECTED_TEXTURES)
        {
            ++this.ofConnectedTextures;

            if (this.ofConnectedTextures > 3)
            {
                this.ofConnectedTextures = 1;
            }

            this.mc.renderGlobal.loadRenderers();
        }

        if (par1EnumOptions == GameSettings.Options.WEATHER)
        {
            this.ofWeather = !this.ofWeather;
        }

        if (par1EnumOptions == GameSettings.Options.SKY)
        {
            this.ofSky = !this.ofSky;
        }

        if (par1EnumOptions == GameSettings.Options.STARS)
        {
            this.ofStars = !this.ofStars;
        }

        if (par1EnumOptions == GameSettings.Options.SUN_MOON)
        {
            this.ofSunMoon = !this.ofSunMoon;
        }

        if (par1EnumOptions == GameSettings.Options.VIGNETTE)
        {
            ++this.ofVignette;

            if (this.ofVignette > 2)
            {
                this.ofVignette = 0;
            }
        }

        if (par1EnumOptions == GameSettings.Options.CHUNK_UPDATES)
        {
            ++this.ofChunkUpdates;

            if (this.ofChunkUpdates > 5)
            {
                this.ofChunkUpdates = 1;
            }
        }

        if (par1EnumOptions == GameSettings.Options.CHUNK_LOADING)
        {
            ++this.ofChunkLoading;

            if (this.ofChunkLoading > 2)
            {
                this.ofChunkLoading = 0;
            }

            this.updateChunkLoading();
        }

        if (par1EnumOptions == GameSettings.Options.CHUNK_UPDATES_DYNAMIC)
        {
            this.ofChunkUpdatesDynamic = !this.ofChunkUpdatesDynamic;
        }

        if (par1EnumOptions == GameSettings.Options.TIME)
        {
            ++this.ofTime;

            if (this.ofTime > 3)
            {
                this.ofTime = 0;
            }
        }

        if (par1EnumOptions == GameSettings.Options.CLEAR_WATER)
        {
            this.ofClearWater = !this.ofClearWater;
            this.updateWaterOpacity();
        }

        if (par1EnumOptions == GameSettings.Options.DEPTH_FOG)
        {
            this.ofDepthFog = !this.ofDepthFog;
        }

        if (par1EnumOptions == GameSettings.Options.AA_LEVEL)
        {
            this.ofAaLevel = 0;
        }

        if (par1EnumOptions == GameSettings.Options.PROFILER)
        {
            this.ofProfiler = !this.ofProfiler;
        }

        if (par1EnumOptions == GameSettings.Options.BETTER_SNOW)
        {
            this.ofBetterSnow = !this.ofBetterSnow;
            this.mc.renderGlobal.loadRenderers();
        }

        if (par1EnumOptions == GameSettings.Options.SWAMP_COLORS)
        {
            this.ofSwampColors = !this.ofSwampColors;
            CustomColorizer.updateUseDefaultColorMultiplier();
            this.mc.renderGlobal.loadRenderers();
        }

        if (par1EnumOptions == GameSettings.Options.RANDOM_MOBS)
        {
            this.ofRandomMobs = !this.ofRandomMobs;
            RandomMobs.resetTextures();
        }

        if (par1EnumOptions == GameSettings.Options.SMOOTH_BIOMES)
        {
            this.ofSmoothBiomes = !this.ofSmoothBiomes;
            CustomColorizer.updateUseDefaultColorMultiplier();
            this.mc.renderGlobal.loadRenderers();
        }

        if (par1EnumOptions == GameSettings.Options.CUSTOM_FONTS)
        {
            this.ofCustomFonts = !this.ofCustomFonts;
            this.mc.fontRenderer.onResourceManagerReload(Config.getResourceManager());
            this.mc.standardGalacticFontRenderer.onResourceManagerReload(Config.getResourceManager());
        }

        if (par1EnumOptions == GameSettings.Options.CUSTOM_COLORS)
        {
            this.ofCustomColors = !this.ofCustomColors;
            CustomColorizer.update();
            this.mc.renderGlobal.loadRenderers();
        }

        if (par1EnumOptions == GameSettings.Options.CUSTOM_SKY)
        {
            this.ofCustomSky = !this.ofCustomSky;
            CustomSky.update();
        }

        if (par1EnumOptions == GameSettings.Options.SHOW_CAPES)
        {
            this.ofShowCapes = !this.ofShowCapes;
            this.mc.renderGlobal.updateCapes();
        }

        if (par1EnumOptions == GameSettings.Options.NATURAL_TEXTURES)
        {
            this.ofNaturalTextures = !this.ofNaturalTextures;
            NaturalTextures.update();
            this.mc.renderGlobal.loadRenderers();
        }

        if (par1EnumOptions == GameSettings.Options.FAST_MATH)
        {
            this.ofFastMath = !this.ofFastMath;
            MathHelper.fastMath = this.ofFastMath;
        }

        if (par1EnumOptions == GameSettings.Options.FAST_RENDER)
        {
            if (!this.ofFastRender && Config.isShaders())
            {
                Config.showGuiMessage(Lang.get("of.message.fr.shaders1"), Lang.get("of.message.fr.shaders2"));
                return;
            }

            this.ofFastRender = !this.ofFastRender;

            if (this.ofFastRender)
            {
                this.mc.entityRenderer.stopUseShader();
            }

            Config.updateFramebufferSize();
            this.mc.renderGlobal.loadRenderers();
        }

        if (par1EnumOptions == GameSettings.Options.TRANSLUCENT_BLOCKS)
        {
            if (this.ofTranslucentBlocks == 1)
            {
                this.ofTranslucentBlocks = 2;
            }
            else
            {
                this.ofTranslucentBlocks = 1;
            }

            this.mc.renderGlobal.loadRenderers();
        }

        if (par1EnumOptions == GameSettings.Options.LAZY_CHUNK_LOADING)
        {
            this.ofLazyChunkLoading = !this.ofLazyChunkLoading;
            Config.updateAvailableProcessors();

            if (!Config.isSingleProcessor())
            {
                this.ofLazyChunkLoading = false;
            }

            this.mc.renderGlobal.loadRenderers();
        }

        if (par1EnumOptions == GameSettings.Options.FULLSCREEN_MODE)
        {
            List modeList = Arrays.asList(Config.getDisplayModeNames());

            if (this.ofFullscreenMode.equals("Default"))
            {
                this.ofFullscreenMode = (String)modeList.get(0);
            }
            else
            {
                int index = modeList.indexOf(this.ofFullscreenMode);

                if (index < 0)
                {
                    this.ofFullscreenMode = "Default";
                }
                else
                {
                    ++index;

                    if (index >= modeList.size())
                    {
                        this.ofFullscreenMode = "Default";
                    }
                    else
                    {
                        this.ofFullscreenMode = (String)modeList.get(index);
                    }
                }
            }
        }

        if (par1EnumOptions == GameSettings.Options.DYNAMIC_FOV)
        {
            this.ofDynamicFov = !this.ofDynamicFov;
        }

        if (par1EnumOptions == GameSettings.Options.DYNAMIC_LIGHTS)
        {
            this.ofDynamicLights = nextValue(this.ofDynamicLights, OF_DYNAMIC_LIGHTS);
            DynamicLights.removeLights(this.mc.renderGlobal);
        }

        if (par1EnumOptions == GameSettings.Options.HELD_ITEM_TOOLTIPS)
        {
            this.heldItemTooltips = !this.heldItemTooltips;
        }

        if (par1EnumOptions == GameSettings.Options.INVERT_MOUSE)
        {
            this.invertMouse = !this.invertMouse;
        }

        if (par1EnumOptions == GameSettings.Options.GUI_SCALE)
        {
            this.guiScale = this.guiScale + par2 & 3;
        }

        if (par1EnumOptions == GameSettings.Options.PARTICLES)
        {
            this.particleSetting = (this.particleSetting + par2) % 3;
        }

        if (par1EnumOptions == GameSettings.Options.VIEW_BOBBING)
        {
            this.viewBobbing = !this.viewBobbing;
        }

        if (par1EnumOptions == GameSettings.Options.RENDER_CLOUDS)
        {
            this.clouds = !this.clouds;
        }

        if (par1EnumOptions == GameSettings.Options.FORCE_UNICODE_FONT)
        {
            this.forceUnicodeFont = !this.forceUnicodeFont;
            this.mc.fontRenderer.setUnicodeFlag(this.mc.getLanguageManager().isCurrentLocaleUnicode() || this.forceUnicodeFont);
        }

        if (par1EnumOptions == GameSettings.Options.ADVANCED_OPENGL)
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

        if (par1EnumOptions == GameSettings.Options.FBO_ENABLE)
        {
            this.fboEnable = !this.fboEnable;
        }

        if (par1EnumOptions == GameSettings.Options.ANAGLYPH)
        {
            if (!this.anaglyph && Config.isShaders())
            {
                Config.showGuiMessage(Lang.get("of.message.an.shaders1"), Lang.get("of.message.an.shaders2"));
                return;
            }

            this.anaglyph = !this.anaglyph;
            this.mc.refreshResources();
        }

        if (par1EnumOptions == GameSettings.Options.DIFFICULTY)
        {
            this.difficulty = EnumDifficulty.getDifficultyEnum(this.difficulty.getDifficultyId() + par2 & 3);
        }

        if (par1EnumOptions == GameSettings.Options.GRAPHICS)
        {
            this.fancyGraphics = !this.fancyGraphics;
            this.mc.renderGlobal.loadRenderers();
        }

        if (par1EnumOptions == GameSettings.Options.AMBIENT_OCCLUSION)
        {
            this.ambientOcclusion = (this.ambientOcclusion + par2) % 3;
            this.mc.renderGlobal.loadRenderers();
        }

        if (par1EnumOptions == GameSettings.Options.CHAT_VISIBILITY)
        {
            this.chatVisibility = EntityPlayer.EnumChatVisibility.getEnumChatVisibility((this.chatVisibility.getChatVisibility() + par2) % 3);
        }

        if (par1EnumOptions == GameSettings.Options.CHAT_COLOR)
        {
            this.chatColours = !this.chatColours;
        }

        if (par1EnumOptions == GameSettings.Options.CHAT_LINKS)
        {
            this.chatLinks = !this.chatLinks;
        }

        if (par1EnumOptions == GameSettings.Options.CHAT_LINKS_PROMPT)
        {
            this.chatLinksPrompt = !this.chatLinksPrompt;
        }

        if (par1EnumOptions == GameSettings.Options.USE_SERVER_TEXTURES)
        {
            this.serverTextures = !this.serverTextures;
        }

        if (par1EnumOptions == GameSettings.Options.SNOOPER_ENABLED)
        {
            this.snooperEnabled = !this.snooperEnabled;
        }

        if (par1EnumOptions == GameSettings.Options.SHOW_CAPE)
        {
            this.showCape = !this.showCape;
        }

        if (par1EnumOptions == GameSettings.Options.TOUCHSCREEN)
        {
            this.touchscreen = !this.touchscreen;
        }

        if (par1EnumOptions == GameSettings.Options.USE_FULLSCREEN)
        {
            this.fullScreen = !this.fullScreen;

            if (this.mc.isFullScreen() != this.fullScreen)
            {
                this.mc.toggleFullscreen();
            }
        }

        if (par1EnumOptions == GameSettings.Options.ENABLE_VSYNC)
        {
            this.enableVsync = !this.enableVsync;
            Display.setVSyncEnabled(this.enableVsync);
        }

        this.saveOptions();
    }

    public float getOptionFloatValue(GameSettings.Options par1EnumOptions)
    {
        return par1EnumOptions == GameSettings.Options.CLOUD_HEIGHT ? this.ofCloudsHeight : (par1EnumOptions == GameSettings.Options.AO_LEVEL ? this.ofAoLevel : (par1EnumOptions == GameSettings.Options.FRAMERATE_LIMIT ? ((float)this.limitFramerate == GameSettings.Options.FRAMERATE_LIMIT.getValueMax() && this.enableVsync ? 0.0F : (float)this.limitFramerate) : (par1EnumOptions == GameSettings.Options.MIPMAP_TYPE ? (float)this.ofMipmapType : (par1EnumOptions == GameSettings.Options.FOV ? this.fovSetting : (par1EnumOptions == GameSettings.Options.GAMMA ? this.gammaSetting : (par1EnumOptions == GameSettings.Options.SATURATION ? this.saturation : (par1EnumOptions == GameSettings.Options.SENSITIVITY ? this.mouseSensitivity : (par1EnumOptions == GameSettings.Options.CHAT_OPACITY ? this.chatOpacity : (par1EnumOptions == GameSettings.Options.CHAT_HEIGHT_FOCUSED ? this.chatHeightFocused : (par1EnumOptions == GameSettings.Options.CHAT_HEIGHT_UNFOCUSED ? this.chatHeightUnfocused : (par1EnumOptions == GameSettings.Options.CHAT_SCALE ? this.chatScale : (par1EnumOptions == GameSettings.Options.CHAT_WIDTH ? this.chatWidth : (par1EnumOptions == GameSettings.Options.FRAMERATE_LIMIT ? (float)this.limitFramerate : (par1EnumOptions == GameSettings.Options.ANISOTROPIC_FILTERING ? (float)this.anisotropicFiltering : (par1EnumOptions == GameSettings.Options.MIPMAP_LEVELS ? (float)this.mipmapLevels : (par1EnumOptions == GameSettings.Options.RENDER_DISTANCE ? (float)this.renderDistanceChunks : 0.0F))))))))))))))));
    }

    public boolean getOptionOrdinalValue(GameSettings.Options par1EnumOptions)
    {
        switch (GameSettings.SwitchOptions.optionIds[par1EnumOptions.ordinal()])
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
                return this.fboEnable;

            case 6:
                return this.clouds;

            case 7:
                return this.chatColours;

            case 8:
                return this.chatLinks;

            case 9:
                return this.chatLinksPrompt;

            case 10:
                return this.serverTextures;

            case 11:
                return this.snooperEnabled;

            case 12:
                return this.fullScreen;

            case 13:
                return this.enableVsync;

            case 14:
                return this.showCape;

            case 15:
                return this.touchscreen;

            case 16:
                return this.forceUnicodeFont;

            default:
                return false;
        }
    }

    private static String getTranslation(String[] par0ArrayOfStr, int par1)
    {
        if (par1 < 0 || par1 >= par0ArrayOfStr.length)
        {
            par1 = 0;
        }

        return I18n.format(par0ArrayOfStr[par1], new Object[0]);
    }

    public String getKeyBinding(GameSettings.Options par1EnumOptions)
    {
        String var2 = I18n.format(par1EnumOptions.getEnumString(), new Object[0]) + ": ";

        if (var2 == null)
        {
            var2 = par1EnumOptions.getEnumString();
        }

        int var33;

        if (par1EnumOptions == GameSettings.Options.RENDER_DISTANCE)
        {
            var33 = (int)this.getOptionFloatValue(par1EnumOptions);
            String var41 = I18n.format("options.renderDistance.tiny", new Object[0]);
            byte baseDist = 2;

            if (var33 >= 4)
            {
                var41 = I18n.format("options.renderDistance.short", new Object[0]);
                baseDist = 4;
            }

            if (var33 >= 8)
            {
                var41 = I18n.format("options.renderDistance.normal", new Object[0]);
                baseDist = 8;
            }

            if (var33 >= 16)
            {
                var41 = I18n.format("options.renderDistance.far", new Object[0]);
                baseDist = 16;
            }

            if (var33 >= 32)
            {
                var41 = Lang.get("of.options.renderDistance.extreme");
                baseDist = 32;
            }

            int diff = this.renderDistanceChunks - baseDist;
            String descr = var41;

            if (diff > 0)
            {
                descr = var41 + "+";
            }

            return var2 + var33 + " " + descr + "";
        }
        else if (par1EnumOptions == GameSettings.Options.ADVANCED_OPENGL)
        {
            return !this.advancedOpengl ? var2 + Lang.getOff() : (this.ofOcclusionFancy ? var2 + Lang.getFancy() : var2 + Lang.getFast());
        }
        else if (par1EnumOptions == GameSettings.Options.FOG_FANCY)
        {
            switch (this.ofFogType)
            {
                case 1:
                    return var2 + Lang.getFast();

                case 2:
                    return var2 + Lang.getFancy();

                case 3:
                    return var2 + Lang.getOff();

                default:
                    return var2 + Lang.getOff();
            }
        }
        else if (par1EnumOptions == GameSettings.Options.FOG_START)
        {
            return var2 + this.ofFogStart;
        }
        else if (par1EnumOptions == GameSettings.Options.MIPMAP_TYPE)
        {
            switch (this.ofMipmapType)
            {
                case 0:
                    return var2 + Lang.get("of.options.mipmap.nearest");

                case 1:
                    return var2 + Lang.get("of.options.mipmap.linear");

                case 2:
                    return var2 + Lang.get("of.options.mipmap.bilinear");

                case 3:
                    return var2 + Lang.get("of.options.mipmap.trilinear");

                default:
                    return var2 + Lang.get("of.options.mipmap.nearest");
            }
        }
        else if (par1EnumOptions == GameSettings.Options.LOAD_FAR)
        {
            return this.ofLoadFar ? var2 + Lang.getOn() : var2 + Lang.getOff();
        }
        else if (par1EnumOptions == GameSettings.Options.PRELOADED_CHUNKS)
        {
            return this.ofPreloadedChunks == 0 ? var2 + Lang.getOff() : var2 + this.ofPreloadedChunks;
        }
        else if (par1EnumOptions == GameSettings.Options.SMOOTH_FPS)
        {
            return this.ofSmoothFps ? var2 + Lang.getOn() : var2 + Lang.getOff();
        }
        else if (par1EnumOptions == GameSettings.Options.SMOOTH_WORLD)
        {
            return this.ofSmoothWorld ? var2 + Lang.getOn() : var2 + Lang.getOff();
        }
        else if (par1EnumOptions == GameSettings.Options.CLOUDS)
        {
            switch (this.ofClouds)
            {
                case 1:
                    return var2 + Lang.getFast();

                case 2:
                    return var2 + Lang.getFancy();

                case 3:
                    return var2 + Lang.getOff();

                default:
                    return var2 + Lang.getDefault();
            }
        }
        else if (par1EnumOptions == GameSettings.Options.TREES)
        {
            switch (this.ofTrees)
            {
                case 1:
                    return var2 + Lang.getFast();

                case 2:
                    return var2 + Lang.getFancy();

                default:
                    return var2 + Lang.getDefault();
            }
        }
        else if (par1EnumOptions == GameSettings.Options.GRASS)
        {
            switch (this.ofGrass)
            {
                case 1:
                    return var2 + Lang.getFast();

                case 2:
                    return var2 + Lang.getFancy();

                default:
                    return var2 + Lang.getDefault();
            }
        }
        else if (par1EnumOptions == GameSettings.Options.DROPPED_ITEMS)
        {
            switch (this.ofDroppedItems)
            {
                case 1:
                    return var2 + Lang.getFast();

                case 2:
                    return var2 + Lang.getFancy();

                default:
                    return var2 + Lang.getDefault();
            }
        }
        else if (par1EnumOptions == GameSettings.Options.RAIN)
        {
            switch (this.ofRain)
            {
                case 1:
                    return var2 + Lang.getFast();

                case 2:
                    return var2 + Lang.getFancy();

                case 3:
                    return var2 + Lang.getOff();

                default:
                    return var2 + Lang.getDefault();
            }
        }
        else if (par1EnumOptions == GameSettings.Options.WATER)
        {
            switch (this.ofWater)
            {
                case 1:
                    return var2 + Lang.getFast();

                case 2:
                    return var2 + Lang.getFancy();

                case 3:
                    return var2 + Lang.getOff();

                default:
                    return var2 + Lang.getDefault();
            }
        }
        else if (par1EnumOptions == GameSettings.Options.ANIMATED_WATER)
        {
            switch (this.ofAnimatedWater)
            {
                case 1:
                    return var2 + Lang.get("of.options.animation.dynamic");

                case 2:
                    return var2 + Lang.getOff();

                default:
                    return var2 + Lang.getOn();
            }
        }
        else if (par1EnumOptions == GameSettings.Options.ANIMATED_LAVA)
        {
            switch (this.ofAnimatedLava)
            {
                case 1:
                    return var2 + Lang.get("of.options.animation.dynamic");

                case 2:
                    return var2 + Lang.getOff();

                default:
                    return var2 + Lang.getOn();
            }
        }
        else if (par1EnumOptions == GameSettings.Options.ANIMATED_FIRE)
        {
            return this.ofAnimatedFire ? var2 + Lang.getOn() : var2 + Lang.getOff();
        }
        else if (par1EnumOptions == GameSettings.Options.ANIMATED_PORTAL)
        {
            return this.ofAnimatedPortal ? var2 + Lang.getOn() : var2 + Lang.getOff();
        }
        else if (par1EnumOptions == GameSettings.Options.ANIMATED_REDSTONE)
        {
            return this.ofAnimatedRedstone ? var2 + Lang.getOn() : var2 + Lang.getOff();
        }
        else if (par1EnumOptions == GameSettings.Options.ANIMATED_EXPLOSION)
        {
            return this.ofAnimatedExplosion ? var2 + Lang.getOn() : var2 + Lang.getOff();
        }
        else if (par1EnumOptions == GameSettings.Options.ANIMATED_FLAME)
        {
            return this.ofAnimatedFlame ? var2 + Lang.getOn() : var2 + Lang.getOff();
        }
        else if (par1EnumOptions == GameSettings.Options.ANIMATED_SMOKE)
        {
            return this.ofAnimatedSmoke ? var2 + Lang.getOn() : var2 + Lang.getOff();
        }
        else if (par1EnumOptions == GameSettings.Options.VOID_PARTICLES)
        {
            return this.ofVoidParticles ? var2 + Lang.getOn() : var2 + Lang.getOff();
        }
        else if (par1EnumOptions == GameSettings.Options.WATER_PARTICLES)
        {
            return this.ofWaterParticles ? var2 + Lang.getOn() : var2 + Lang.getOff();
        }
        else if (par1EnumOptions == GameSettings.Options.PORTAL_PARTICLES)
        {
            return this.ofPortalParticles ? var2 + Lang.getOn() : var2 + Lang.getOff();
        }
        else if (par1EnumOptions == GameSettings.Options.POTION_PARTICLES)
        {
            return this.ofPotionParticles ? var2 + Lang.getOn() : var2 + Lang.getOff();
        }
        else if (par1EnumOptions == GameSettings.Options.DRIPPING_WATER_LAVA)
        {
            return this.ofDrippingWaterLava ? var2 + Lang.getOn() : var2 + Lang.getOff();
        }
        else if (par1EnumOptions == GameSettings.Options.ANIMATED_TERRAIN)
        {
            return this.ofAnimatedTerrain ? var2 + Lang.getOn() : var2 + Lang.getOff();
        }
        else if (par1EnumOptions == GameSettings.Options.ANIMATED_TEXTURES)
        {
            return this.ofAnimatedTextures ? var2 + Lang.getOn() : var2 + Lang.getOff();
        }
        else if (par1EnumOptions == GameSettings.Options.ANIMATED_ITEMS)
        {
            return this.ofAnimatedItems ? var2 + Lang.getOn() : var2 + Lang.getOff();
        }
        else if (par1EnumOptions == GameSettings.Options.RAIN_SPLASH)
        {
            return this.ofRainSplash ? var2 + Lang.getOn() : var2 + Lang.getOff();
        }
        else if (par1EnumOptions == GameSettings.Options.LAGOMETER)
        {
            return this.ofLagometer ? var2 + Lang.getOn() : var2 + Lang.getOff();
        }
        else if (par1EnumOptions == GameSettings.Options.SHOW_FPS)
        {
            return this.ofShowFps ? var2 + Lang.getOn() : var2 + Lang.getOff();
        }
        else if (par1EnumOptions == GameSettings.Options.AUTOSAVE_TICKS)
        {
            return this.ofAutoSaveTicks <= 40 ? var2 + Lang.get("of.options.save.default") : (this.ofAutoSaveTicks <= 400 ? var2 + Lang.get("of.options.save.20s") : (this.ofAutoSaveTicks <= 4000 ? var2 + Lang.get("of.options.save.3min") : var2 + Lang.get("of.options.save.30min")));
        }
        else if (par1EnumOptions == GameSettings.Options.BETTER_GRASS)
        {
            switch (this.ofBetterGrass)
            {
                case 1:
                    return var2 + Lang.getFast();

                case 2:
                    return var2 + Lang.getFancy();

                default:
                    return var2 + Lang.getOff();
            }
        }
        else if (par1EnumOptions == GameSettings.Options.CONNECTED_TEXTURES)
        {
            switch (this.ofConnectedTextures)
            {
                case 1:
                    return var2 + Lang.getFast();

                case 2:
                    return var2 + Lang.getFancy();

                default:
                    return var2 + Lang.getOff();
            }
        }
        else if (par1EnumOptions == GameSettings.Options.WEATHER)
        {
            return this.ofWeather ? var2 + Lang.getOn() : var2 + Lang.getOff();
        }
        else if (par1EnumOptions == GameSettings.Options.SKY)
        {
            return this.ofSky ? var2 + Lang.getOn() : var2 + Lang.getOff();
        }
        else if (par1EnumOptions == GameSettings.Options.STARS)
        {
            return this.ofStars ? var2 + Lang.getOn() : var2 + Lang.getOff();
        }
        else if (par1EnumOptions == GameSettings.Options.SUN_MOON)
        {
            return this.ofSunMoon ? var2 + Lang.getOn() : var2 + Lang.getOff();
        }
        else if (par1EnumOptions == GameSettings.Options.VIGNETTE)
        {
            switch (this.ofVignette)
            {
                case 1:
                    return var2 + Lang.getFast();

                case 2:
                    return var2 + Lang.getFancy();

                default:
                    return var2 + Lang.getDefault();
            }
        }
        else if (par1EnumOptions == GameSettings.Options.CHUNK_UPDATES)
        {
            return var2 + this.ofChunkUpdates;
        }
        else if (par1EnumOptions == GameSettings.Options.CHUNK_LOADING)
        {
            return this.ofChunkLoading == 1 ? var2 + "Smooth" : (this.ofChunkLoading == 2 ? var2 + "Multi-Core" : var2 + "Default");
        }
        else if (par1EnumOptions == GameSettings.Options.CHUNK_UPDATES_DYNAMIC)
        {
            return this.ofChunkUpdatesDynamic ? var2 + Lang.getOn() : var2 + Lang.getOff();
        }
        else if (par1EnumOptions == GameSettings.Options.TIME)
        {
            return this.ofTime == 1 ? var2 + Lang.get("of.options.time.dayOnly") : (this.ofTime == 3 ? var2 + Lang.get("of.options.time.nightOnly") : var2 + Lang.getDefault());
        }
        else if (par1EnumOptions == GameSettings.Options.CLEAR_WATER)
        {
            return this.ofClearWater ? var2 + Lang.getOn() : var2 + Lang.getOff();
        }
        else if (par1EnumOptions == GameSettings.Options.DEPTH_FOG)
        {
            return this.ofDepthFog ? var2 + Lang.getOn() : var2 + Lang.getOff();
        }
        else if (par1EnumOptions == GameSettings.Options.AA_LEVEL)
        {
            return this.ofAaLevel == 0 ? var2 + Lang.getOff() : var2 + this.ofAaLevel;
        }
        else if (par1EnumOptions == GameSettings.Options.PROFILER)
        {
            return this.ofProfiler ? var2 + Lang.getOn() : var2 + Lang.getOff();
        }
        else if (par1EnumOptions == GameSettings.Options.BETTER_SNOW)
        {
            return this.ofBetterSnow ? var2 + Lang.getOn() : var2 + Lang.getOff();
        }
        else if (par1EnumOptions == GameSettings.Options.SWAMP_COLORS)
        {
            return this.ofSwampColors ? var2 + Lang.getOn() : var2 + Lang.getOff();
        }
        else if (par1EnumOptions == GameSettings.Options.RANDOM_MOBS)
        {
            return this.ofRandomMobs ? var2 + Lang.getOn() : var2 + Lang.getOff();
        }
        else if (par1EnumOptions == GameSettings.Options.SMOOTH_BIOMES)
        {
            return this.ofSmoothBiomes ? var2 + Lang.getOn() : var2 + Lang.getOff();
        }
        else if (par1EnumOptions == GameSettings.Options.CUSTOM_FONTS)
        {
            return this.ofCustomFonts ? var2 + Lang.getOn() : var2 + Lang.getOff();
        }
        else if (par1EnumOptions == GameSettings.Options.CUSTOM_COLORS)
        {
            return this.ofCustomColors ? var2 + Lang.getOn() : var2 + Lang.getOff();
        }
        else if (par1EnumOptions == GameSettings.Options.CUSTOM_SKY)
        {
            return this.ofCustomSky ? var2 + Lang.getOn() : var2 + Lang.getOff();
        }
        else if (par1EnumOptions == GameSettings.Options.SHOW_CAPES)
        {
            return this.ofShowCapes ? var2 + Lang.getOn() : var2 + Lang.getOff();
        }
        else if (par1EnumOptions == GameSettings.Options.NATURAL_TEXTURES)
        {
            return this.ofNaturalTextures ? var2 + Lang.getOn() : var2 + Lang.getOff();
        }
        else if (par1EnumOptions == GameSettings.Options.FAST_MATH)
        {
            return this.ofFastMath ? var2 + Lang.getOn() : var2 + Lang.getOff();
        }
        else if (par1EnumOptions == GameSettings.Options.FAST_RENDER)
        {
            return this.ofFastRender ? var2 + Lang.getOn() : var2 + Lang.getOff();
        }
        else if (par1EnumOptions == GameSettings.Options.TRANSLUCENT_BLOCKS)
        {
            return this.ofTranslucentBlocks == 1 ? var2 + Lang.getFast() : var2 + Lang.getFancy();
        }
        else if (par1EnumOptions == GameSettings.Options.LAZY_CHUNK_LOADING)
        {
            return this.ofLazyChunkLoading ? var2 + Lang.getOn() : var2 + Lang.getOff();
        }
        else if (par1EnumOptions == GameSettings.Options.DYNAMIC_FOV)
        {
            return this.ofDynamicFov ? var2 + Lang.getOn() : var2 + Lang.getOff();
        }
        else if (par1EnumOptions == GameSettings.Options.DYNAMIC_LIGHTS)
        {
            var33 = indexOf(this.ofDynamicLights, OF_DYNAMIC_LIGHTS);
            return var2 + getTranslation(KEYS_DYNAMIC_LIGHTS, var33);
        }
        else if (par1EnumOptions == GameSettings.Options.FULLSCREEN_MODE)
        {
            return this.ofFullscreenMode.equals("Default") ? var2 + Lang.getDefault() : var2 + this.ofFullscreenMode;
        }
        else if (par1EnumOptions == GameSettings.Options.HELD_ITEM_TOOLTIPS)
        {
            return this.heldItemTooltips ? var2 + Lang.getOn() : var2 + Lang.getOff();
        }
        else
        {
            float var32;

            if (par1EnumOptions == GameSettings.Options.FRAMERATE_LIMIT)
            {
                var32 = this.getOptionFloatValue(par1EnumOptions);
                return var32 == 0.0F ? var2 + Lang.get("of.options.framerateLimit.vsync") : (var32 == par1EnumOptions.valueMax ? var2 + I18n.format("options.framerateLimit.max", new Object[0]) : var2 + (int)var32 + " fps");
            }
            else if (par1EnumOptions.getEnumFloat())
            {
                var32 = this.getOptionFloatValue(par1EnumOptions);
                float var4 = par1EnumOptions.normalizeValue(var32);
                return par1EnumOptions == GameSettings.Options.SENSITIVITY ? (var4 == 0.0F ? var2 + I18n.format("options.sensitivity.min", new Object[0]) : (var4 == 1.0F ? var2 + I18n.format("options.sensitivity.max", new Object[0]) : var2 + (int)(var4 * 200.0F) + "%")) : (par1EnumOptions == GameSettings.Options.FOV ? (var4 == 0.0F ? var2 + I18n.format("options.fov.min", new Object[0]) : (var4 == 1.0F ? var2 + I18n.format("options.fov.max", new Object[0]) : var2 + (int)(70.0F + var4 * 40.0F))) : (par1EnumOptions == GameSettings.Options.FRAMERATE_LIMIT ? (var32 == par1EnumOptions.valueMax ? var2 + I18n.format("options.framerateLimit.max", new Object[0]) : var2 + (int)var32 + " fps") : (par1EnumOptions == GameSettings.Options.GAMMA ? (var4 == 0.0F ? var2 + I18n.format("options.gamma.min", new Object[0]) : (var4 == 1.0F ? var2 + I18n.format("options.gamma.max", new Object[0]) : var2 + "+" + (int)(var4 * 100.0F) + "%")) : (par1EnumOptions == GameSettings.Options.SATURATION ? var2 + (int)(var4 * 400.0F) + "%" : (par1EnumOptions == GameSettings.Options.CHAT_OPACITY ? var2 + (int)(var4 * 90.0F + 10.0F) + "%" : (par1EnumOptions == GameSettings.Options.CHAT_HEIGHT_UNFOCUSED ? var2 + GuiNewChat.func_146243_b(var4) + "px" : (par1EnumOptions == GameSettings.Options.CHAT_HEIGHT_FOCUSED ? var2 + GuiNewChat.func_146243_b(var4) + "px" : (par1EnumOptions == GameSettings.Options.CHAT_WIDTH ? var2 + GuiNewChat.func_146233_a(var4) + "px" : (par1EnumOptions == GameSettings.Options.RENDER_DISTANCE ? var2 + (int)var32 + " chunks" : (par1EnumOptions == GameSettings.Options.ANISOTROPIC_FILTERING ? (var32 == 1.0F ? var2 + I18n.format("options.off", new Object[0]) : var2 + (int)var32) : (par1EnumOptions == GameSettings.Options.MIPMAP_LEVELS ? (var32 == 0.0F ? var2 + I18n.format("options.off", new Object[0]) : var2 + (int)var32) : (var4 == 0.0F ? var2 + I18n.format("options.off", new Object[0]) : var2 + (int)(var4 * 100.0F) + "%"))))))))))));
            }
            else if (par1EnumOptions.getEnumBoolean())
            {
                boolean var31 = this.getOptionOrdinalValue(par1EnumOptions);
                return var31 ? var2 + I18n.format("options.on", new Object[0]) : var2 + I18n.format("options.off", new Object[0]);
            }
            else if (par1EnumOptions == GameSettings.Options.DIFFICULTY)
            {
                return var2 + I18n.format(this.difficulty.getDifficultyResourceKey(), new Object[0]);
            }
            else if (par1EnumOptions == GameSettings.Options.GUI_SCALE)
            {
                return var2 + getTranslation(GUISCALES, this.guiScale);
            }
            else if (par1EnumOptions == GameSettings.Options.CHAT_VISIBILITY)
            {
                return var2 + I18n.format(this.chatVisibility.getResourceKey(), new Object[0]);
            }
            else if (par1EnumOptions == GameSettings.Options.PARTICLES)
            {
                return var2 + getTranslation(PARTICLES, this.particleSetting);
            }
            else if (par1EnumOptions == GameSettings.Options.AMBIENT_OCCLUSION)
            {
                return var2 + getTranslation(AMBIENT_OCCLUSIONS, this.ambientOcclusion);
            }
            else if (par1EnumOptions == GameSettings.Options.GRAPHICS)
            {
                if (this.fancyGraphics)
                {
                    return var2 + I18n.format("options.graphics.fancy", new Object[0]);
                }
                else
                {
                    String var3 = "options.graphics.fast";
                    return var2 + I18n.format("options.graphics.fast", new Object[0]);
                }
            }
            else
            {
                return var2;
            }
        }
    }

    public void loadOptions()
    {
        try
        {
            if (!this.optionsFile.exists())
            {
                return;
            }

            BufferedReader var9 = new BufferedReader(new FileReader(this.optionsFile));
            String var2 = "";
            this.mapSoundLevels.clear();

            while ((var2 = var9.readLine()) != null)
            {
                try
                {
                    String[] var8 = var2.split(":");

                    if (var8[0].equals("mouseSensitivity"))
                    {
                        this.mouseSensitivity = this.parseFloat(var8[1]);
                    }

                    if (var8[0].equals("fov"))
                    {
                        this.fovSetting = this.parseFloat(var8[1]);
                    }

                    if (var8[0].equals("gamma"))
                    {
                        this.gammaSetting = this.parseFloat(var8[1]);
                    }

                    if (var8[0].equals("saturation"))
                    {
                        this.saturation = this.parseFloat(var8[1]);
                    }

                    if (var8[0].equals("invertYMouse"))
                    {
                        this.invertMouse = var8[1].equals("true");
                    }

                    if (var8[0].equals("renderDistance"))
                    {
                        this.renderDistanceChunks = Integer.parseInt(var8[1]);
                    }

                    if (var8[0].equals("guiScale"))
                    {
                        this.guiScale = Integer.parseInt(var8[1]);
                    }

                    if (var8[0].equals("particles"))
                    {
                        this.particleSetting = Integer.parseInt(var8[1]);
                    }

                    if (var8[0].equals("bobView"))
                    {
                        this.viewBobbing = var8[1].equals("true");
                    }

                    if (var8[0].equals("anaglyph3d"))
                    {
                        this.anaglyph = var8[1].equals("true");
                    }

                    if (var8[0].equals("advancedOpengl"))
                    {
                        this.advancedOpengl = var8[1].equals("true");
                    }

                    if (var8[0].equals("maxFps"))
                    {
                        this.limitFramerate = Integer.parseInt(var8[1]);
                        this.enableVsync = false;

                        if (this.limitFramerate <= 0)
                        {
                            this.limitFramerate = (int)GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
                            this.enableVsync = true;
                        }

                        this.updateVSync();
                    }

                    if (var8[0].equals("fboEnable"))
                    {
                        this.fboEnable = var8[1].equals("true");
                    }

                    if (var8[0].equals("difficulty"))
                    {
                        this.difficulty = EnumDifficulty.getDifficultyEnum(Integer.parseInt(var8[1]));
                    }

                    if (var8[0].equals("fancyGraphics"))
                    {
                        this.fancyGraphics = var8[1].equals("true");
                    }

                    if (var8[0].equals("ao"))
                    {
                        if (var8[1].equals("true"))
                        {
                            this.ambientOcclusion = 2;
                        }
                        else if (var8[1].equals("false"))
                        {
                            this.ambientOcclusion = 0;
                        }
                        else
                        {
                            this.ambientOcclusion = Integer.parseInt(var8[1]);
                        }
                    }

                    if (var8[0].equals("clouds"))
                    {
                        this.clouds = var8[1].equals("true");
                    }

                    if (var8[0].equals("resourcePacks"))
                    {
                        this.resourcePacks = (List)gson.fromJson(var2.substring(var2.indexOf(58) + 1), typeListString);

                        if (this.resourcePacks == null)
                        {
                            this.resourcePacks = new ArrayList();
                        }
                    }

                    if (var8[0].equals("lastServer") && var8.length >= 2)
                    {
                        this.lastServer = var2.substring(var2.indexOf(58) + 1);
                    }

                    if (var8[0].equals("lang") && var8.length >= 2)
                    {
                        this.language = var8[1];
                    }

                    if (var8[0].equals("chatVisibility"))
                    {
                        this.chatVisibility = EntityPlayer.EnumChatVisibility.getEnumChatVisibility(Integer.parseInt(var8[1]));
                    }

                    if (var8[0].equals("chatColors"))
                    {
                        this.chatColours = var8[1].equals("true");
                    }

                    if (var8[0].equals("chatLinks"))
                    {
                        this.chatLinks = var8[1].equals("true");
                    }

                    if (var8[0].equals("chatLinksPrompt"))
                    {
                        this.chatLinksPrompt = var8[1].equals("true");
                    }

                    if (var8[0].equals("chatOpacity"))
                    {
                        this.chatOpacity = this.parseFloat(var8[1]);
                    }

                    if (var8[0].equals("serverTextures"))
                    {
                        this.serverTextures = var8[1].equals("true");
                    }

                    if (var8[0].equals("snooperEnabled"))
                    {
                        this.snooperEnabled = var8[1].equals("true");
                    }

                    if (var8[0].equals("fullscreen"))
                    {
                        this.fullScreen = var8[1].equals("true");
                    }

                    if (var8[0].equals("enableVsync"))
                    {
                        this.enableVsync = var8[1].equals("true");
                        this.updateVSync();
                    }

                    if (var8[0].equals("hideServerAddress"))
                    {
                        this.hideServerAddress = var8[1].equals("true");
                    }

                    if (var8[0].equals("advancedItemTooltips"))
                    {
                        this.advancedItemTooltips = var8[1].equals("true");
                    }

                    if (var8[0].equals("pauseOnLostFocus"))
                    {
                        this.pauseOnLostFocus = var8[1].equals("true");
                    }

                    if (var8[0].equals("showCape"))
                    {
                        this.showCape = var8[1].equals("true");
                    }

                    if (var8[0].equals("touchscreen"))
                    {
                        this.touchscreen = var8[1].equals("true");
                    }

                    if (var8[0].equals("overrideHeight"))
                    {
                        this.overrideHeight = Integer.parseInt(var8[1]);
                    }

                    if (var8[0].equals("overrideWidth"))
                    {
                        this.overrideWidth = Integer.parseInt(var8[1]);
                    }

                    if (var8[0].equals("heldItemTooltips"))
                    {
                        this.heldItemTooltips = var8[1].equals("true");
                    }

                    if (var8[0].equals("chatHeightFocused"))
                    {
                        this.chatHeightFocused = this.parseFloat(var8[1]);
                    }

                    if (var8[0].equals("chatHeightUnfocused"))
                    {
                        this.chatHeightUnfocused = this.parseFloat(var8[1]);
                    }

                    if (var8[0].equals("chatScale"))
                    {
                        this.chatScale = this.parseFloat(var8[1]);
                    }

                    if (var8[0].equals("chatWidth"))
                    {
                        this.chatWidth = this.parseFloat(var8[1]);
                    }

                    if (var8[0].equals("showInventoryAchievementHint"))
                    {
                        this.showInventoryAchievementHint = var8[1].equals("true");
                    }

                    if (var8[0].equals("mipmapLevels"))
                    {
                        this.mipmapLevels = Integer.parseInt(var8[1]);
                    }

                    if (var8[0].equals("anisotropicFiltering"))
                    {
                        this.anisotropicFiltering = Integer.parseInt(var8[1]);
                    }

                    if (var8[0].equals("forceUnicodeFont"))
                    {
                        this.forceUnicodeFont = var8[1].equals("true");
                    }

                    KeyBinding[] var4 = this.keyBindings;
                    int var5 = var4.length;
                    int var6;

                    for (var6 = 0; var6 < var5; ++var6)
                    {
                        KeyBinding var10 = var4[var6];

                        if (var8[0].equals("key_" + var10.getKeyDescription()))
                        {
                            var10.setKeyCode(Integer.parseInt(var8[1]));
                        }
                    }

                    SoundCategory[] var111 = SoundCategory.values();
                    var5 = var111.length;

                    for (var6 = 0; var6 < var5; ++var6)
                    {
                        SoundCategory var11 = var111[var6];

                        if (var8[0].equals("soundCategory_" + var11.getCategoryName()))
                        {
                            this.mapSoundLevels.put(var11, Float.valueOf(this.parseFloat(var8[1])));
                        }
                    }
                }
                catch (Exception var91)
                {
                    logger.warn("Skipping bad option: " + var2);
                    var91.printStackTrace();
                }
            }

            KeyBinding.resetKeyBindingArrayAndHash();
            var9.close();
        }
        catch (Exception var101)
        {
            logger.error("Failed to load options", var101);
        }

        this.loadOfOptions();
    }

    private float parseFloat(String par1Str)
    {
        return par1Str.equals("true") ? 1.0F : (par1Str.equals("false") ? 0.0F : Float.parseFloat(par1Str));
    }

    public void saveOptions()
    {
        if (Reflector.FMLClientHandler.exists())
        {
            Object var6 = Reflector.call(Reflector.FMLClientHandler_instance, new Object[0]);

            if (var6 != null && Reflector.callBoolean(var6, Reflector.FMLClientHandler_isLoading, new Object[0]))
            {
                return;
            }
        }

        try
        {
            PrintWriter var81 = new PrintWriter(new FileWriter(this.optionsFile));
            var81.println("invertYMouse:" + this.invertMouse);
            var81.println("mouseSensitivity:" + this.mouseSensitivity);
            var81.println("fov:" + this.fovSetting);
            var81.println("gamma:" + this.gammaSetting);
            var81.println("saturation:" + this.saturation);
            var81.println("renderDistance:" + Config.limit(this.renderDistanceChunks, 2, 16));
            var81.println("guiScale:" + this.guiScale);
            var81.println("particles:" + this.particleSetting);
            var81.println("bobView:" + this.viewBobbing);
            var81.println("anaglyph3d:" + this.anaglyph);
            var81.println("advancedOpengl:" + this.advancedOpengl);
            var81.println("maxFps:" + this.limitFramerate);
            var81.println("fboEnable:" + this.fboEnable);
            var81.println("difficulty:" + this.difficulty.getDifficultyId());
            var81.println("fancyGraphics:" + this.fancyGraphics);
            var81.println("ao:" + this.ambientOcclusion);
            var81.println("clouds:" + this.clouds);
            var81.println("resourcePacks:" + gson.toJson(this.resourcePacks));
            var81.println("lastServer:" + this.lastServer);
            var81.println("lang:" + this.language);
            var81.println("chatVisibility:" + this.chatVisibility.getChatVisibility());
            var81.println("chatColors:" + this.chatColours);
            var81.println("chatLinks:" + this.chatLinks);
            var81.println("chatLinksPrompt:" + this.chatLinksPrompt);
            var81.println("chatOpacity:" + this.chatOpacity);
            var81.println("serverTextures:" + this.serverTextures);
            var81.println("snooperEnabled:" + this.snooperEnabled);
            var81.println("fullscreen:" + this.fullScreen);
            var81.println("enableVsync:" + this.enableVsync);
            var81.println("hideServerAddress:" + this.hideServerAddress);
            var81.println("advancedItemTooltips:" + this.advancedItemTooltips);
            var81.println("pauseOnLostFocus:" + this.pauseOnLostFocus);
            var81.println("showCape:" + this.showCape);
            var81.println("touchscreen:" + this.touchscreen);
            var81.println("overrideWidth:" + this.overrideWidth);
            var81.println("overrideHeight:" + this.overrideHeight);
            var81.println("heldItemTooltips:" + this.heldItemTooltips);
            var81.println("chatHeightFocused:" + this.chatHeightFocused);
            var81.println("chatHeightUnfocused:" + this.chatHeightUnfocused);
            var81.println("chatScale:" + this.chatScale);
            var81.println("chatWidth:" + this.chatWidth);
            var81.println("showInventoryAchievementHint:" + this.showInventoryAchievementHint);
            var81.println("mipmapLevels:" + this.mipmapLevels);
            var81.println("anisotropicFiltering:" + this.anisotropicFiltering);
            var81.println("forceUnicodeFont:" + this.forceUnicodeFont);
            KeyBinding[] var2 = this.keyBindings;
            int var3 = var2.length;
            int var4;

            for (var4 = 0; var4 < var3; ++var4)
            {
                KeyBinding var7 = var2[var4];
                var81.println("key_" + var7.getKeyDescription() + ":" + var7.getKeyCode());
            }

            SoundCategory[] var9 = SoundCategory.values();
            var3 = var9.length;

            for (var4 = 0; var4 < var3; ++var4)
            {
                SoundCategory var8 = var9[var4];
                var81.println("soundCategory_" + var8.getCategoryName() + ":" + this.getSoundLevel(var8));
            }

            var81.close();
        }
        catch (Exception var71)
        {
            logger.error("Failed to save options", var71);
        }

        this.saveOfOptions();
        this.sendSettingsToServer();
    }

    public float getSoundLevel(SoundCategory p_151438_1_)
    {
        return this.mapSoundLevels.containsKey(p_151438_1_) ? ((Float)this.mapSoundLevels.get(p_151438_1_)).floatValue() : 1.0F;
    }

    public void setSoundLevel(SoundCategory p_151439_1_, float p_151439_2_)
    {
        this.mc.getSoundHandler().setSoundLevel(p_151439_1_, p_151439_2_);
        this.mapSoundLevels.put(p_151439_1_, Float.valueOf(p_151439_2_));
    }

    public void sendSettingsToServer()
    {
        if (this.mc.thePlayer != null)
        {
            this.mc.thePlayer.sendQueue.addToSendQueue(new C15PacketClientSettings(this.language, this.renderDistanceChunks, this.chatVisibility, this.chatColours, this.difficulty, this.showCape));
        }
    }

    public boolean shouldRenderClouds()
    {
        return this.renderDistanceChunks >= 4 && this.clouds;
    }

    public void loadOfOptions()
    {
        try
        {
            File exception = this.optionsFileOF;

            if (!exception.exists())
            {
                exception = this.optionsFile;
            }

            if (!exception.exists())
            {
                return;
            }

            BufferedReader bufferedreader = new BufferedReader(new FileReader(exception));
            String s = "";

            while ((s = bufferedreader.readLine()) != null)
            {
                try
                {
                    String[] exception1 = s.split(":");

                    if (exception1[0].equals("ofRenderDistanceChunks") && exception1.length >= 2)
                    {
                        this.renderDistanceChunks = Integer.valueOf(exception1[1]).intValue();
                        this.renderDistanceChunks = Config.limit(this.renderDistanceChunks, 2, 32);
                    }

                    if (exception1[0].equals("ofFogType") && exception1.length >= 2)
                    {
                        this.ofFogType = Integer.valueOf(exception1[1]).intValue();
                        this.ofFogType = Config.limit(this.ofFogType, 1, 3);
                    }

                    if (exception1[0].equals("ofFogStart") && exception1.length >= 2)
                    {
                        this.ofFogStart = Float.valueOf(exception1[1]).floatValue();

                        if (this.ofFogStart < 0.2F)
                        {
                            this.ofFogStart = 0.2F;
                        }

                        if (this.ofFogStart > 0.81F)
                        {
                            this.ofFogStart = 0.8F;
                        }
                    }

                    if (exception1[0].equals("ofMipmapType") && exception1.length >= 2)
                    {
                        this.ofMipmapType = Integer.valueOf(exception1[1]).intValue();
                        this.ofMipmapType = Config.limit(this.ofMipmapType, 0, 3);
                    }

                    if (exception1[0].equals("ofLoadFar") && exception1.length >= 2)
                    {
                        this.ofLoadFar = Boolean.valueOf(exception1[1]).booleanValue();
                    }

                    if (exception1[0].equals("ofPreloadedChunks") && exception1.length >= 2)
                    {
                        this.ofPreloadedChunks = Integer.valueOf(exception1[1]).intValue();

                        if (this.ofPreloadedChunks < 0)
                        {
                            this.ofPreloadedChunks = 0;
                        }

                        if (this.ofPreloadedChunks > 8)
                        {
                            this.ofPreloadedChunks = 8;
                        }
                    }

                    if (exception1[0].equals("ofOcclusionFancy") && exception1.length >= 2)
                    {
                        this.ofOcclusionFancy = Boolean.valueOf(exception1[1]).booleanValue();
                    }

                    if (exception1[0].equals("ofSmoothFps") && exception1.length >= 2)
                    {
                        this.ofSmoothFps = Boolean.valueOf(exception1[1]).booleanValue();
                    }

                    if (exception1[0].equals("ofSmoothWorld") && exception1.length >= 2)
                    {
                        this.ofSmoothWorld = Boolean.valueOf(exception1[1]).booleanValue();
                    }

                    if (exception1[0].equals("ofAoLevel") && exception1.length >= 2)
                    {
                        this.ofAoLevel = Float.valueOf(exception1[1]).floatValue();
                        this.ofAoLevel = Config.limit(this.ofAoLevel, 0.0F, 1.0F);
                    }

                    if (exception1[0].equals("ofClouds") && exception1.length >= 2)
                    {
                        this.ofClouds = Integer.valueOf(exception1[1]).intValue();
                        this.ofClouds = Config.limit(this.ofClouds, 0, 3);
                    }

                    if (exception1[0].equals("ofCloudsHeight") && exception1.length >= 2)
                    {
                        this.ofCloudsHeight = Float.valueOf(exception1[1]).floatValue();
                        this.ofCloudsHeight = Config.limit(this.ofCloudsHeight, 0.0F, 1.0F);
                    }

                    if (exception1[0].equals("ofTrees") && exception1.length >= 2)
                    {
                        this.ofTrees = Integer.valueOf(exception1[1]).intValue();
                        this.ofTrees = Config.limit(this.ofTrees, 0, 2);
                    }

                    if (exception1[0].equals("ofGrass") && exception1.length >= 2)
                    {
                        this.ofGrass = Integer.valueOf(exception1[1]).intValue();
                        this.ofGrass = Config.limit(this.ofGrass, 0, 2);
                    }

                    if (exception1[0].equals("ofDroppedItems") && exception1.length >= 2)
                    {
                        this.ofDroppedItems = Integer.valueOf(exception1[1]).intValue();
                        this.ofDroppedItems = Config.limit(this.ofDroppedItems, 0, 2);
                    }

                    if (exception1[0].equals("ofRain") && exception1.length >= 2)
                    {
                        this.ofRain = Integer.valueOf(exception1[1]).intValue();
                        this.ofRain = Config.limit(this.ofRain, 0, 3);
                    }

                    if (exception1[0].equals("ofWater") && exception1.length >= 2)
                    {
                        this.ofWater = Integer.valueOf(exception1[1]).intValue();
                        this.ofWater = Config.limit(this.ofWater, 0, 3);
                    }

                    if (exception1[0].equals("ofAnimatedWater") && exception1.length >= 2)
                    {
                        this.ofAnimatedWater = Integer.valueOf(exception1[1]).intValue();
                        this.ofAnimatedWater = Config.limit(this.ofAnimatedWater, 0, 2);
                    }

                    if (exception1[0].equals("ofAnimatedLava") && exception1.length >= 2)
                    {
                        this.ofAnimatedLava = Integer.valueOf(exception1[1]).intValue();
                        this.ofAnimatedLava = Config.limit(this.ofAnimatedLava, 0, 2);
                    }

                    if (exception1[0].equals("ofAnimatedFire") && exception1.length >= 2)
                    {
                        this.ofAnimatedFire = Boolean.valueOf(exception1[1]).booleanValue();
                    }

                    if (exception1[0].equals("ofAnimatedPortal") && exception1.length >= 2)
                    {
                        this.ofAnimatedPortal = Boolean.valueOf(exception1[1]).booleanValue();
                    }

                    if (exception1[0].equals("ofAnimatedRedstone") && exception1.length >= 2)
                    {
                        this.ofAnimatedRedstone = Boolean.valueOf(exception1[1]).booleanValue();
                    }

                    if (exception1[0].equals("ofAnimatedExplosion") && exception1.length >= 2)
                    {
                        this.ofAnimatedExplosion = Boolean.valueOf(exception1[1]).booleanValue();
                    }

                    if (exception1[0].equals("ofAnimatedFlame") && exception1.length >= 2)
                    {
                        this.ofAnimatedFlame = Boolean.valueOf(exception1[1]).booleanValue();
                    }

                    if (exception1[0].equals("ofAnimatedSmoke") && exception1.length >= 2)
                    {
                        this.ofAnimatedSmoke = Boolean.valueOf(exception1[1]).booleanValue();
                    }

                    if (exception1[0].equals("ofVoidParticles") && exception1.length >= 2)
                    {
                        this.ofVoidParticles = Boolean.valueOf(exception1[1]).booleanValue();
                    }

                    if (exception1[0].equals("ofWaterParticles") && exception1.length >= 2)
                    {
                        this.ofWaterParticles = Boolean.valueOf(exception1[1]).booleanValue();
                    }

                    if (exception1[0].equals("ofPortalParticles") && exception1.length >= 2)
                    {
                        this.ofPortalParticles = Boolean.valueOf(exception1[1]).booleanValue();
                    }

                    if (exception1[0].equals("ofPotionParticles") && exception1.length >= 2)
                    {
                        this.ofPotionParticles = Boolean.valueOf(exception1[1]).booleanValue();
                    }

                    if (exception1[0].equals("ofDrippingWaterLava") && exception1.length >= 2)
                    {
                        this.ofDrippingWaterLava = Boolean.valueOf(exception1[1]).booleanValue();
                    }

                    if (exception1[0].equals("ofAnimatedTerrain") && exception1.length >= 2)
                    {
                        this.ofAnimatedTerrain = Boolean.valueOf(exception1[1]).booleanValue();
                    }

                    if (exception1[0].equals("ofAnimatedTextures") && exception1.length >= 2)
                    {
                        this.ofAnimatedTextures = Boolean.valueOf(exception1[1]).booleanValue();
                    }

                    if (exception1[0].equals("ofAnimatedItems") && exception1.length >= 2)
                    {
                        this.ofAnimatedItems = Boolean.valueOf(exception1[1]).booleanValue();
                    }

                    if (exception1[0].equals("ofRainSplash") && exception1.length >= 2)
                    {
                        this.ofRainSplash = Boolean.valueOf(exception1[1]).booleanValue();
                    }

                    if (exception1[0].equals("ofLagometer") && exception1.length >= 2)
                    {
                        this.ofLagometer = Boolean.valueOf(exception1[1]).booleanValue();
                    }

                    if (exception1[0].equals("ofShowFps") && exception1.length >= 2)
                    {
                        this.ofShowFps = Boolean.valueOf(exception1[1]).booleanValue();
                    }

                    if (exception1[0].equals("ofAutoSaveTicks") && exception1.length >= 2)
                    {
                        this.ofAutoSaveTicks = Integer.valueOf(exception1[1]).intValue();
                        this.ofAutoSaveTicks = Config.limit(this.ofAutoSaveTicks, 40, 40000);
                    }

                    if (exception1[0].equals("ofBetterGrass") && exception1.length >= 2)
                    {
                        this.ofBetterGrass = Integer.valueOf(exception1[1]).intValue();
                        this.ofBetterGrass = Config.limit(this.ofBetterGrass, 1, 3);
                    }

                    if (exception1[0].equals("ofConnectedTextures") && exception1.length >= 2)
                    {
                        this.ofConnectedTextures = Integer.valueOf(exception1[1]).intValue();
                        this.ofConnectedTextures = Config.limit(this.ofConnectedTextures, 1, 3);
                    }

                    if (exception1[0].equals("ofWeather") && exception1.length >= 2)
                    {
                        this.ofWeather = Boolean.valueOf(exception1[1]).booleanValue();
                    }

                    if (exception1[0].equals("ofSky") && exception1.length >= 2)
                    {
                        this.ofSky = Boolean.valueOf(exception1[1]).booleanValue();
                    }

                    if (exception1[0].equals("ofStars") && exception1.length >= 2)
                    {
                        this.ofStars = Boolean.valueOf(exception1[1]).booleanValue();
                    }

                    if (exception1[0].equals("ofSunMoon") && exception1.length >= 2)
                    {
                        this.ofSunMoon = Boolean.valueOf(exception1[1]).booleanValue();
                    }

                    if (exception1[0].equals("ofVignette") && exception1.length >= 2)
                    {
                        this.ofVignette = Integer.valueOf(exception1[1]).intValue();
                        this.ofVignette = Config.limit(this.ofVignette, 0, 2);
                    }

                    if (exception1[0].equals("ofChunkUpdates") && exception1.length >= 2)
                    {
                        this.ofChunkUpdates = Integer.valueOf(exception1[1]).intValue();
                        this.ofChunkUpdates = Config.limit(this.ofChunkUpdates, 1, 5);
                    }

                    if (exception1[0].equals("ofChunkLoading") && exception1.length >= 2)
                    {
                        this.ofChunkLoading = Integer.valueOf(exception1[1]).intValue();
                        this.ofChunkLoading = Config.limit(this.ofChunkLoading, 0, 2);
                        this.updateChunkLoading();
                    }

                    if (exception1[0].equals("ofChunkUpdatesDynamic") && exception1.length >= 2)
                    {
                        this.ofChunkUpdatesDynamic = Boolean.valueOf(exception1[1]).booleanValue();
                    }

                    if (exception1[0].equals("ofTime") && exception1.length >= 2)
                    {
                        this.ofTime = Integer.valueOf(exception1[1]).intValue();
                        this.ofTime = Config.limit(this.ofTime, 0, 3);
                    }

                    if (exception1[0].equals("ofClearWater") && exception1.length >= 2)
                    {
                        this.ofClearWater = Boolean.valueOf(exception1[1]).booleanValue();
                        this.updateWaterOpacity();
                    }

                    if (exception1[0].equals("ofDepthFog") && exception1.length >= 2)
                    {
                        this.ofDepthFog = Boolean.valueOf(exception1[1]).booleanValue();
                    }

                    if (exception1[0].equals("ofAaLevel") && exception1.length >= 2)
                    {
                        this.ofAaLevel = Integer.valueOf(exception1[1]).intValue();
                        this.ofAaLevel = Config.limit(this.ofAaLevel, 0, 16);
                    }

                    if (exception1[0].equals("ofProfiler") && exception1.length >= 2)
                    {
                        this.ofProfiler = Boolean.valueOf(exception1[1]).booleanValue();
                    }

                    if (exception1[0].equals("ofBetterSnow") && exception1.length >= 2)
                    {
                        this.ofBetterSnow = Boolean.valueOf(exception1[1]).booleanValue();
                    }

                    if (exception1[0].equals("ofSwampColors") && exception1.length >= 2)
                    {
                        this.ofSwampColors = Boolean.valueOf(exception1[1]).booleanValue();
                    }

                    if (exception1[0].equals("ofRandomMobs") && exception1.length >= 2)
                    {
                        this.ofRandomMobs = Boolean.valueOf(exception1[1]).booleanValue();
                    }

                    if (exception1[0].equals("ofSmoothBiomes") && exception1.length >= 2)
                    {
                        this.ofSmoothBiomes = Boolean.valueOf(exception1[1]).booleanValue();
                    }

                    if (exception1[0].equals("ofCustomFonts") && exception1.length >= 2)
                    {
                        this.ofCustomFonts = Boolean.valueOf(exception1[1]).booleanValue();
                    }

                    if (exception1[0].equals("ofCustomColors") && exception1.length >= 2)
                    {
                        this.ofCustomColors = Boolean.valueOf(exception1[1]).booleanValue();
                    }

                    if (exception1[0].equals("ofCustomSky") && exception1.length >= 2)
                    {
                        this.ofCustomSky = Boolean.valueOf(exception1[1]).booleanValue();
                    }

                    if (exception1[0].equals("ofShowCapes") && exception1.length >= 2)
                    {
                        this.ofShowCapes = Boolean.valueOf(exception1[1]).booleanValue();
                    }

                    if (exception1[0].equals("ofNaturalTextures") && exception1.length >= 2)
                    {
                        this.ofNaturalTextures = Boolean.valueOf(exception1[1]).booleanValue();
                    }

                    if (exception1[0].equals("ofLazyChunkLoading") && exception1.length >= 2)
                    {
                        this.ofLazyChunkLoading = Boolean.valueOf(exception1[1]).booleanValue();
                    }

                    if (exception1[0].equals("ofDynamicFov") && exception1.length >= 2)
                    {
                        this.ofDynamicFov = Boolean.valueOf(exception1[1]).booleanValue();
                    }

                    if (exception1[0].equals("ofDynamicLights") && exception1.length >= 2)
                    {
                        this.ofDynamicLights = Integer.valueOf(exception1[1]).intValue();
                        this.ofDynamicLights = Config.limit(this.ofDynamicLights, 1, 3);
                    }

                    if (exception1[0].equals("ofFullscreenMode") && exception1.length >= 2)
                    {
                        this.ofFullscreenMode = exception1[1];
                    }

                    if (exception1[0].equals("ofFastMath") && exception1.length >= 2)
                    {
                        this.ofFastMath = Boolean.valueOf(exception1[1]).booleanValue();
                        MathHelper.fastMath = this.ofFastMath;
                    }

                    if (exception1[0].equals("ofFastRender") && exception1.length >= 2)
                    {
                        this.ofFastRender = Boolean.valueOf(exception1[1]).booleanValue();
                    }

                    if (exception1[0].equals("ofTranslucentBlocks") && exception1.length >= 2)
                    {
                        this.ofTranslucentBlocks = Integer.valueOf(exception1[1]).intValue();
                        this.ofTranslucentBlocks = Config.limit(this.ofTranslucentBlocks, 1, 2);
                    }
                }
                catch (Exception var5)
                {
                    Config.dbg("Skipping bad option: " + s);
                    var5.printStackTrace();
                }
            }

            KeyBinding.resetKeyBindingArrayAndHash();
            bufferedreader.close();
        }
        catch (Exception var6)
        {
            Config.warn("Failed to load options");
            var6.printStackTrace();
        }
    }

    public void saveOfOptions()
    {
        try
        {
            PrintWriter exception = new PrintWriter(new FileWriter(this.optionsFileOF));
            exception.println("ofRenderDistanceChunks:" + this.renderDistanceChunks);
            exception.println("ofFogType:" + this.ofFogType);
            exception.println("ofFogStart:" + this.ofFogStart);
            exception.println("ofMipmapType:" + this.ofMipmapType);
            exception.println("ofLoadFar:" + this.ofLoadFar);
            exception.println("ofPreloadedChunks:" + this.ofPreloadedChunks);
            exception.println("ofOcclusionFancy:" + this.ofOcclusionFancy);
            exception.println("ofSmoothFps:" + this.ofSmoothFps);
            exception.println("ofSmoothWorld:" + this.ofSmoothWorld);
            exception.println("ofAoLevel:" + this.ofAoLevel);
            exception.println("ofClouds:" + this.ofClouds);
            exception.println("ofCloudsHeight:" + this.ofCloudsHeight);
            exception.println("ofTrees:" + this.ofTrees);
            exception.println("ofGrass:" + this.ofGrass);
            exception.println("ofDroppedItems:" + this.ofDroppedItems);
            exception.println("ofRain:" + this.ofRain);
            exception.println("ofWater:" + this.ofWater);
            exception.println("ofAnimatedWater:" + this.ofAnimatedWater);
            exception.println("ofAnimatedLava:" + this.ofAnimatedLava);
            exception.println("ofAnimatedFire:" + this.ofAnimatedFire);
            exception.println("ofAnimatedPortal:" + this.ofAnimatedPortal);
            exception.println("ofAnimatedRedstone:" + this.ofAnimatedRedstone);
            exception.println("ofAnimatedExplosion:" + this.ofAnimatedExplosion);
            exception.println("ofAnimatedFlame:" + this.ofAnimatedFlame);
            exception.println("ofAnimatedSmoke:" + this.ofAnimatedSmoke);
            exception.println("ofVoidParticles:" + this.ofVoidParticles);
            exception.println("ofWaterParticles:" + this.ofWaterParticles);
            exception.println("ofPortalParticles:" + this.ofPortalParticles);
            exception.println("ofPotionParticles:" + this.ofPotionParticles);
            exception.println("ofDrippingWaterLava:" + this.ofDrippingWaterLava);
            exception.println("ofAnimatedTerrain:" + this.ofAnimatedTerrain);
            exception.println("ofAnimatedTextures:" + this.ofAnimatedTextures);
            exception.println("ofAnimatedItems:" + this.ofAnimatedItems);
            exception.println("ofRainSplash:" + this.ofRainSplash);
            exception.println("ofLagometer:" + this.ofLagometer);
            exception.println("ofShowFps:" + this.ofShowFps);
            exception.println("ofAutoSaveTicks:" + this.ofAutoSaveTicks);
            exception.println("ofBetterGrass:" + this.ofBetterGrass);
            exception.println("ofConnectedTextures:" + this.ofConnectedTextures);
            exception.println("ofWeather:" + this.ofWeather);
            exception.println("ofSky:" + this.ofSky);
            exception.println("ofStars:" + this.ofStars);
            exception.println("ofSunMoon:" + this.ofSunMoon);
            exception.println("ofVignette:" + this.ofVignette);
            exception.println("ofChunkUpdates:" + this.ofChunkUpdates);
            exception.println("ofChunkLoading:" + this.ofChunkLoading);
            exception.println("ofChunkUpdatesDynamic:" + this.ofChunkUpdatesDynamic);
            exception.println("ofTime:" + this.ofTime);
            exception.println("ofClearWater:" + this.ofClearWater);
            exception.println("ofDepthFog:" + this.ofDepthFog);
            exception.println("ofAaLevel:" + this.ofAaLevel);
            exception.println("ofProfiler:" + this.ofProfiler);
            exception.println("ofBetterSnow:" + this.ofBetterSnow);
            exception.println("ofSwampColors:" + this.ofSwampColors);
            exception.println("ofRandomMobs:" + this.ofRandomMobs);
            exception.println("ofSmoothBiomes:" + this.ofSmoothBiomes);
            exception.println("ofCustomFonts:" + this.ofCustomFonts);
            exception.println("ofCustomColors:" + this.ofCustomColors);
            exception.println("ofCustomSky:" + this.ofCustomSky);
            exception.println("ofShowCapes:" + this.ofShowCapes);
            exception.println("ofNaturalTextures:" + this.ofNaturalTextures);
            exception.println("ofLazyChunkLoading:" + this.ofLazyChunkLoading);
            exception.println("ofDynamicFov:" + this.ofDynamicFov);
            exception.println("ofDynamicLights:" + this.ofDynamicLights);
            exception.println("ofFullscreenMode:" + this.ofFullscreenMode);
            exception.println("ofFastMath:" + this.ofFastMath);
            exception.println("ofFastRender:" + this.ofFastRender);
            exception.println("ofTranslucentBlocks:" + this.ofTranslucentBlocks);
            exception.close();
        }
        catch (Exception var2)
        {
            Config.warn("Failed to save options");
            var2.printStackTrace();
        }
    }

    public void resetSettings()
    {
        this.renderDistanceChunks = 8;
        this.viewBobbing = true;
        this.anaglyph = false;
        this.advancedOpengl = false;
        this.limitFramerate = (int)GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
        this.enableVsync = false;
        this.updateVSync();
        this.mipmapLevels = 4;
        this.anisotropicFiltering = 1;
        this.fancyGraphics = true;
        this.ambientOcclusion = 2;
        this.clouds = true;
        this.fovSetting = 70.0F;
        this.gammaSetting = 0.0F;
        this.guiScale = 0;
        this.particleSetting = 0;
        this.heldItemTooltips = true;
        this.ofFogType = 1;
        this.ofFogStart = 0.8F;
        this.ofMipmapType = 0;
        this.ofLoadFar = false;
        this.ofPreloadedChunks = 0;
        this.ofOcclusionFancy = false;
        this.ofSmoothFps = false;
        Config.updateAvailableProcessors();
        this.ofSmoothWorld = Config.isSingleProcessor();
        this.ofLazyChunkLoading = Config.isSingleProcessor();
        this.ofFastMath = false;
        this.ofFastRender = false;
        this.ofTranslucentBlocks = 2;
        this.ofDynamicFov = true;
        this.ofDynamicLights = 3;
        this.ofAoLevel = 1.0F;
        this.ofAaLevel = 0;
        this.ofClouds = 0;
        this.ofCloudsHeight = 0.0F;
        this.ofTrees = 0;
        this.ofGrass = 0;
        this.ofRain = 0;
        this.ofWater = 0;
        this.ofBetterGrass = 3;
        this.ofAutoSaveTicks = 4000;
        this.ofLagometer = false;
        this.ofShowFps = false;
        this.ofProfiler = false;
        this.ofWeather = true;
        this.ofSky = true;
        this.ofStars = true;
        this.ofSunMoon = true;
        this.ofVignette = 0;
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
        Shaders.setShaderPack(Shaders.packNameNone);
        Shaders.configAntialiasingLevel = 0;
        Shaders.uninit();
        Shaders.storeConfig();
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

    private void updateWaterOpacity()
    {
        if (this.mc.isIntegratedServerRunning() && this.mc.getIntegratedServer() != null)
        {
            Config.waterOpacityChanged = true;
        }

        ClearWater.updateWaterOpacity(this, this.mc.theWorld);
    }

    public void updateChunkLoading()
    {
        switch (this.ofChunkLoading)
        {
            case 1:
                WrUpdates.setWrUpdater(new WrUpdaterSmooth());
                break;

            case 2:
                WrUpdates.setWrUpdater(new WrUpdaterThreaded());
                break;

            default:
                WrUpdates.setWrUpdater((IWrUpdater)null);
        }

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

    private static int nextValue(int val, int[] vals)
    {
        int index = indexOf(val, vals);

        if (index < 0)
        {
            return vals[0];
        }
        else
        {
            ++index;

            if (index >= vals.length)
            {
                index = 0;
            }

            return vals[index];
        }
    }

    private static int limit(int val, int[] vals)
    {
        int index = indexOf(val, vals);
        return index < 0 ? vals[0] : val;
    }

    private static int indexOf(int val, int[] vals)
    {
        for (int i = 0; i < vals.length; ++i)
        {
            if (vals[i] == val)
            {
                return i;
            }
        }

        return -1;
    }

    public static enum Options
    {
        INVERT_MOUSE("INVERT_MOUSE", 0, "INVERT_MOUSE", 0, "options.invertMouse", false, true),
        SENSITIVITY("SENSITIVITY", 1, "SENSITIVITY", 1, "options.sensitivity", true, false),
        FOV("FOV", 2, "FOV", 2, "options.fov", true, false),
        GAMMA("GAMMA", 3, "GAMMA", 3, "options.gamma", true, false),
        SATURATION("SATURATION", 4, "SATURATION", 4, "options.saturation", true, false),
        RENDER_DISTANCE("RENDER_DISTANCE", 5, "RENDER_DISTANCE", 5, "options.renderDistance", true, false, 2.0F, 16.0F, 1.0F),
        VIEW_BOBBING("VIEW_BOBBING", 6, "VIEW_BOBBING", 6, "options.viewBobbing", false, true),
        ANAGLYPH("ANAGLYPH", 7, "ANAGLYPH", 7, "options.anaglyph", false, true),
        ADVANCED_OPENGL("ADVANCED_OPENGL", 8, "ADVANCED_OPENGL", 8, "options.advancedOpengl", false, true),
        FRAMERATE_LIMIT("FRAMERATE_LIMIT", 9, "FRAMERATE_LIMIT", 9, "options.framerateLimit", true, false, 0.0F, 260.0F, 5.0F),
        FBO_ENABLE("FBO_ENABLE", 10, "FBO_ENABLE", 10, "options.fboEnable", false, true),
        DIFFICULTY("DIFFICULTY", 11, "DIFFICULTY", 11, "options.difficulty", false, false),
        GRAPHICS("GRAPHICS", 12, "GRAPHICS", 12, "options.graphics", false, false),
        AMBIENT_OCCLUSION("AMBIENT_OCCLUSION", 13, "AMBIENT_OCCLUSION", 13, "options.ao", false, false),
        GUI_SCALE("GUI_SCALE", 14, "GUI_SCALE", 14, "options.guiScale", false, false),
        RENDER_CLOUDS("RENDER_CLOUDS", 15, "RENDER_CLOUDS", 15, "options.renderClouds", false, true),
        PARTICLES("PARTICLES", 16, "PARTICLES", 16, "options.particles", false, false),
        CHAT_VISIBILITY("CHAT_VISIBILITY", 17, "CHAT_VISIBILITY", 17, "options.chat.visibility", false, false),
        CHAT_COLOR("CHAT_COLOR", 18, "CHAT_COLOR", 18, "options.chat.color", false, true),
        CHAT_LINKS("CHAT_LINKS", 19, "CHAT_LINKS", 19, "options.chat.links", false, true),
        CHAT_OPACITY("CHAT_OPACITY", 20, "CHAT_OPACITY", 20, "options.chat.opacity", true, false),
        CHAT_LINKS_PROMPT("CHAT_LINKS_PROMPT", 21, "CHAT_LINKS_PROMPT", 21, "options.chat.links.prompt", false, true),
        USE_SERVER_TEXTURES("USE_SERVER_TEXTURES", 22, "USE_SERVER_TEXTURES", 22, "options.serverTextures", false, true),
        SNOOPER_ENABLED("SNOOPER_ENABLED", 23, "SNOOPER_ENABLED", 23, "options.snooper", false, true),
        USE_FULLSCREEN("USE_FULLSCREEN", 24, "USE_FULLSCREEN", 24, "options.fullscreen", false, true),
        ENABLE_VSYNC("ENABLE_VSYNC", 25, "ENABLE_VSYNC", 25, "options.vsync", false, true),
        SHOW_CAPE("SHOW_CAPE", 26, "SHOW_CAPE", 26, "options.showCape", false, true),
        TOUCHSCREEN("TOUCHSCREEN", 27, "TOUCHSCREEN", 27, "options.touchscreen", false, true),
        CHAT_SCALE("CHAT_SCALE", 28, "CHAT_SCALE", 28, "options.chat.scale", true, false),
        CHAT_WIDTH("CHAT_WIDTH", 29, "CHAT_WIDTH", 29, "options.chat.width", true, false),
        CHAT_HEIGHT_FOCUSED("CHAT_HEIGHT_FOCUSED", 30, "CHAT_HEIGHT_FOCUSED", 30, "options.chat.height.focused", true, false),
        CHAT_HEIGHT_UNFOCUSED("CHAT_HEIGHT_UNFOCUSED", 31, "CHAT_HEIGHT_UNFOCUSED", 31, "options.chat.height.unfocused", true, false),
        MIPMAP_LEVELS("MIPMAP_LEVELS", 32, "MIPMAP_LEVELS", 32, "options.mipmapLevels", true, false, 0.0F, 4.0F, 1.0F),
        ANISOTROPIC_FILTERING("ANISOTROPIC_FILTERING", 33, "ANISOTROPIC_FILTERING", 33, "options.anisotropicFiltering", true, false, 1.0F, 16.0F, 0.0F, (Object)null, null)
        {
            protected float snapToStep(float p_148264_1_)
            {
                return (float)MathHelper.roundUpToPowerOfTwo((int)p_148264_1_);
            }
        },
        FORCE_UNICODE_FONT("FORCE_UNICODE_FONT", 34, "FORCE_UNICODE_FONT", 34, "options.forceUnicodeFont", false, true),
        FOG_FANCY("FOG_FANCY", 35, "", 999, "of.options.FOG_FANCY", false, false),
        FOG_START("FOG_START", 36, "", 999, "of.options.FOG_START", false, false),
        MIPMAP_TYPE("MIPMAP_TYPE", 37, "", 999, "of.options.MIPMAP_TYPE", true, false, 0.0F, 3.0F, 1.0F),
        LOAD_FAR("LOAD_FAR", 38, "", 999, "Load Far", false, false),
        PRELOADED_CHUNKS("PRELOADED_CHUNKS", 39, "", 999, "Preloaded Chunks", false, false),
        SMOOTH_FPS("SMOOTH_FPS", 40, "", 999, "of.options.SMOOTH_FPS", false, false),
        CLOUDS("CLOUDS", 41, "", 999, "of.options.CLOUDS", false, false),
        CLOUD_HEIGHT("CLOUD_HEIGHT", 42, "", 999, "of.options.CLOUD_HEIGHT", true, false),
        TREES("TREES", 43, "", 999, "of.options.TREES", false, false),
        GRASS("GRASS", 44, "", 999, "Grass", false, false),
        RAIN("RAIN", 45, "", 999, "of.options.RAIN", false, false),
        WATER("WATER", 46, "", 999, "Water", false, false),
        ANIMATED_WATER("ANIMATED_WATER", 47, "", 999, "of.options.ANIMATED_WATER", false, false),
        ANIMATED_LAVA("ANIMATED_LAVA", 48, "", 999, "of.options.ANIMATED_LAVA", false, false),
        ANIMATED_FIRE("ANIMATED_FIRE", 49, "", 999, "of.options.ANIMATED_FIRE", false, false),
        ANIMATED_PORTAL("ANIMATED_PORTAL", 50, "", 999, "of.options.ANIMATED_PORTAL", false, false),
        AO_LEVEL("AO_LEVEL", 51, "", 999, "of.options.AO_LEVEL", true, false),
        LAGOMETER("LAGOMETER", 52, "", 999, "of.options.LAGOMETER", false, false),
        SHOW_FPS("SHOW_FPS", 53, "", 999, "of.options.SHOW_FPS", false, false),
        AUTOSAVE_TICKS("AUTOSAVE_TICKS", 54, "", 999, "of.options.AUTOSAVE_TICKS", false, false),
        BETTER_GRASS("BETTER_GRASS", 55, "", 999, "of.options.BETTER_GRASS", false, false),
        ANIMATED_REDSTONE("ANIMATED_REDSTONE", 56, "", 999, "of.options.ANIMATED_REDSTONE", false, false),
        ANIMATED_EXPLOSION("ANIMATED_EXPLOSION", 57, "", 999, "of.options.ANIMATED_EXPLOSION", false, false),
        ANIMATED_FLAME("ANIMATED_FLAME", 58, "", 999, "of.options.ANIMATED_FLAME", false, false),
        ANIMATED_SMOKE("ANIMATED_SMOKE", 59, "", 999, "of.options.ANIMATED_SMOKE", false, false),
        WEATHER("WEATHER", 60, "", 999, "of.options.WEATHER", false, false),
        SKY("SKY", 61, "", 999, "of.options.SKY", false, false),
        STARS("STARS", 62, "", 999, "of.options.STARS", false, false),
        SUN_MOON("SUN_MOON", 63, "", 999, "of.options.SUN_MOON", false, false),
        VIGNETTE("VIGNETTE", 64, "", 999, "of.options.VIGNETTE", false, false),
        CHUNK_UPDATES("CHUNK_UPDATES", 65, "", 999, "of.options.CHUNK_UPDATES", false, false),
        CHUNK_UPDATES_DYNAMIC("CHUNK_UPDATES_DYNAMIC", 66, "", 999, "of.options.CHUNK_UPDATES_DYNAMIC", false, false),
        TIME("TIME", 67, "", 999, "of.options.TIME", false, false),
        CLEAR_WATER("CLEAR_WATER", 68, "", 999, "of.options.CLEAR_WATER", false, false),
        SMOOTH_WORLD("SMOOTH_WORLD", 69, "", 999, "of.options.SMOOTH_WORLD", false, false),
        DEPTH_FOG("DEPTH_FOG", 70, "", 999, "Depth Fog", false, false),
        VOID_PARTICLES("VOID_PARTICLES", 71, "", 999, "of.options.VOID_PARTICLES", false, false),
        WATER_PARTICLES("WATER_PARTICLES", 72, "", 999, "of.options.WATER_PARTICLES", false, false),
        RAIN_SPLASH("RAIN_SPLASH", 73, "", 999, "of.options.RAIN_SPLASH", false, false),
        PORTAL_PARTICLES("PORTAL_PARTICLES", 74, "", 999, "of.options.PORTAL_PARTICLES", false, false),
        POTION_PARTICLES("POTION_PARTICLES", 75, "", 999, "of.options.POTION_PARTICLES", false, false),
        PROFILER("PROFILER", 76, "", 999, "of.options.PROFILER", false, false),
        DRIPPING_WATER_LAVA("DRIPPING_WATER_LAVA", 77, "", 999, "of.options.DRIPPING_WATER_LAVA", false, false),
        BETTER_SNOW("BETTER_SNOW", 78, "", 999, "of.options.BETTER_SNOW", false, false),
        FULLSCREEN_MODE("FULLSCREEN_MODE", 79, "", 999, "of.options.FULLSCREEN_MODE", false, false),
        ANIMATED_TERRAIN("ANIMATED_TERRAIN", 80, "", 999, "of.options.ANIMATED_TERRAIN", false, false),
        ANIMATED_ITEMS("ANIMATED_ITEMS", 81, "", 999, "Items Animated", false, false),
        SWAMP_COLORS("SWAMP_COLORS", 82, "", 999, "of.options.SWAMP_COLORS", false, false),
        RANDOM_MOBS("RANDOM_MOBS", 83, "", 999, "of.options.RANDOM_MOBS", false, false),
        SMOOTH_BIOMES("SMOOTH_BIOMES", 84, "", 999, "of.options.SMOOTH_BIOMES", false, false),
        CUSTOM_FONTS("CUSTOM_FONTS", 85, "", 999, "of.options.CUSTOM_FONTS", false, false),
        CUSTOM_COLORS("CUSTOM_COLORS", 86, "", 999, "of.options.CUSTOM_COLORS", false, false),
        SHOW_CAPES("SHOW_CAPES", 87, "", 999, "of.options.SHOW_CAPES", false, false),
        CONNECTED_TEXTURES("CONNECTED_TEXTURES", 88, "", 999, "of.options.CONNECTED_TEXTURES", false, false),
        AA_LEVEL("AA_LEVEL", 89, "", 999, "of.options.AA_LEVEL", true, false, 0.0F, 16.0F, 1.0F),
        ANIMATED_TEXTURES("ANIMATED_TEXTURES", 90, "", 999, "of.options.ANIMATED_TEXTURES", false, false),
        NATURAL_TEXTURES("NATURAL_TEXTURES", 91, "", 999, "of.options.NATURAL_TEXTURES", false, false),
        CHUNK_LOADING("CHUNK_LOADING", 92, "", 999, "Chunk Loading", false, false),
        HELD_ITEM_TOOLTIPS("HELD_ITEM_TOOLTIPS", 93, "", 999, "of.options.HELD_ITEM_TOOLTIPS", false, false),
        DROPPED_ITEMS("DROPPED_ITEMS", 94, "", 999, "of.options.DROPPED_ITEMS", false, false),
        LAZY_CHUNK_LOADING("LAZY_CHUNK_LOADING", 95, "", 999, "of.options.LAZY_CHUNK_LOADING", false, false),
        CUSTOM_SKY("CUSTOM_SKY", 96, "", 999, "of.options.CUSTOM_SKY", false, false),
        FAST_MATH("FAST_MATH", 97, "", 999, "of.options.FAST_MATH", false, false),
        FAST_RENDER("FAST_RENDER", 98, "", 999, "of.options.FAST_RENDER", false, false),
        TRANSLUCENT_BLOCKS("TRANSLUCENT_BLOCKS", 99, "", 999, "of.options.TRANSLUCENT_BLOCKS", false, false),
        DYNAMIC_FOV("DYNAMIC_FOV", 100, "", 999, "of.options.DYNAMIC_FOV", false, false),
        DYNAMIC_LIGHTS("DYNAMIC_LIGHTS", 101, "", 999, "of.options.DYNAMIC_LIGHTS", false, false);
        private final boolean enumFloat;
        private final boolean enumBoolean;
        private final String enumString;
        private final float valueStep;
        private float valueMin;
        private float valueMax;
        private static final GameSettings.Options[] $VALUES = new GameSettings.Options[]{INVERT_MOUSE, SENSITIVITY, FOV, GAMMA, SATURATION, RENDER_DISTANCE, VIEW_BOBBING, ANAGLYPH, ADVANCED_OPENGL, FRAMERATE_LIMIT, FBO_ENABLE, DIFFICULTY, GRAPHICS, AMBIENT_OCCLUSION, GUI_SCALE, RENDER_CLOUDS, PARTICLES, CHAT_VISIBILITY, CHAT_COLOR, CHAT_LINKS, CHAT_OPACITY, CHAT_LINKS_PROMPT, USE_SERVER_TEXTURES, SNOOPER_ENABLED, USE_FULLSCREEN, ENABLE_VSYNC, SHOW_CAPE, TOUCHSCREEN, CHAT_SCALE, CHAT_WIDTH, CHAT_HEIGHT_FOCUSED, CHAT_HEIGHT_UNFOCUSED, MIPMAP_LEVELS, ANISOTROPIC_FILTERING, FORCE_UNICODE_FONT};
        private static final GameSettings.Options[] $VALUES$ = new GameSettings.Options[]{INVERT_MOUSE, SENSITIVITY, FOV, GAMMA, SATURATION, RENDER_DISTANCE, VIEW_BOBBING, ANAGLYPH, ADVANCED_OPENGL, FRAMERATE_LIMIT, FBO_ENABLE, DIFFICULTY, GRAPHICS, AMBIENT_OCCLUSION, GUI_SCALE, RENDER_CLOUDS, PARTICLES, CHAT_VISIBILITY, CHAT_COLOR, CHAT_LINKS, CHAT_OPACITY, CHAT_LINKS_PROMPT, USE_SERVER_TEXTURES, SNOOPER_ENABLED, USE_FULLSCREEN, ENABLE_VSYNC, SHOW_CAPE, TOUCHSCREEN, CHAT_SCALE, CHAT_WIDTH, CHAT_HEIGHT_FOCUSED, CHAT_HEIGHT_UNFOCUSED, MIPMAP_LEVELS, ANISOTROPIC_FILTERING, FORCE_UNICODE_FONT, FOG_FANCY, FOG_START, MIPMAP_TYPE, LOAD_FAR, PRELOADED_CHUNKS, SMOOTH_FPS, CLOUDS, CLOUD_HEIGHT, TREES, GRASS, RAIN, WATER, ANIMATED_WATER, ANIMATED_LAVA, ANIMATED_FIRE, ANIMATED_PORTAL, AO_LEVEL, LAGOMETER, SHOW_FPS, AUTOSAVE_TICKS, BETTER_GRASS, ANIMATED_REDSTONE, ANIMATED_EXPLOSION, ANIMATED_FLAME, ANIMATED_SMOKE, WEATHER, SKY, STARS, SUN_MOON, VIGNETTE, CHUNK_UPDATES, CHUNK_UPDATES_DYNAMIC, TIME, CLEAR_WATER, SMOOTH_WORLD, DEPTH_FOG, VOID_PARTICLES, WATER_PARTICLES, RAIN_SPLASH, PORTAL_PARTICLES, POTION_PARTICLES, PROFILER, DRIPPING_WATER_LAVA, BETTER_SNOW, FULLSCREEN_MODE, ANIMATED_TERRAIN, ANIMATED_ITEMS, SWAMP_COLORS, RANDOM_MOBS, SMOOTH_BIOMES, CUSTOM_FONTS, CUSTOM_COLORS, SHOW_CAPES, CONNECTED_TEXTURES, AA_LEVEL, ANIMATED_TEXTURES, NATURAL_TEXTURES, CHUNK_LOADING, HELD_ITEM_TOOLTIPS, DROPPED_ITEMS, LAZY_CHUNK_LOADING, CUSTOM_SKY, FAST_MATH, FAST_RENDER, TRANSLUCENT_BLOCKS, DYNAMIC_FOV, DYNAMIC_LIGHTS};

        public static GameSettings.Options getEnumOptions(int par0)
        {
            GameSettings.Options[] var1 = values();
            int var2 = var1.length;

            for (int var3 = 0; var3 < var2; ++var3)
            {
                GameSettings.Options var4 = var1[var3];

                if (var4.returnEnumOrdinal() == par0)
                {
                    return var4;
                }
            }

            return null;
        }

        private Options(String var1, int var2, String par1Str, int par2, String par3Str, boolean par4, boolean par5)
        {
            this(var1, var2, par1Str, par2, par3Str, par4, par5, 0.0F, 1.0F, 0.0F);
        }

        private Options(String var1, int var2, String p_i45004_1_, int p_i45004_2_, String p_i45004_3_, boolean p_i45004_4_, boolean p_i45004_5_, float p_i45004_6_, float p_i45004_7_, float p_i45004_8_)
        {
            this.enumString = p_i45004_3_;
            this.enumFloat = p_i45004_4_;
            this.enumBoolean = p_i45004_5_;
            this.valueMin = p_i45004_6_;
            this.valueMax = p_i45004_7_;
            this.valueStep = p_i45004_8_;
        }

        public boolean getEnumFloat()
        {
            return this.enumFloat;
        }

        public boolean getEnumBoolean()
        {
            return this.enumBoolean;
        }

        public int returnEnumOrdinal()
        {
            return this.ordinal();
        }

        public String getEnumString()
        {
            return this.enumString;
        }

        public float getValueMax()
        {
            return this.valueMax;
        }

        public void setValueMax(float p_148263_1_)
        {
            this.valueMax = p_148263_1_;
        }

        public float normalizeValue(float p_148266_1_)
        {
            return MathHelper.clamp_float((this.snapToStepClamp(p_148266_1_) - this.valueMin) / (this.valueMax - this.valueMin), 0.0F, 1.0F);
        }

        public float denormalizeValue(float p_148262_1_)
        {
            return this.snapToStepClamp(this.valueMin + (this.valueMax - this.valueMin) * MathHelper.clamp_float(p_148262_1_, 0.0F, 1.0F));
        }

        public float snapToStepClamp(float p_148268_1_)
        {
            p_148268_1_ = this.snapToStep(p_148268_1_);
            return MathHelper.clamp_float(p_148268_1_, this.valueMin, this.valueMax);
        }

        protected float snapToStep(float p_148264_1_)
        {
            if (this.valueStep > 0.0F)
            {
                p_148264_1_ = this.valueStep * (float)Math.round(p_148264_1_ / this.valueStep);
            }

            return p_148264_1_;
        }

        private Options(String var1, int var2, String p_i45005_1_, int p_i45005_2_, String p_i45005_3_, boolean p_i45005_4_, boolean p_i45005_5_, float p_i45005_6_, float p_i45005_7_, float p_i45005_8_, Object p_i45005_9_)
        {
            this(var1, var2, p_i45005_1_, p_i45005_2_, p_i45005_3_, p_i45005_4_, p_i45005_5_, p_i45005_6_, p_i45005_7_, p_i45005_8_);
        }

        Options(String x0, int x1, String x2, int x3, String x4, boolean x5, boolean x6, float x7, float x8, float x9, Object x10, Object x11)
        {
            this(x0, x1, x2, x3, x4, x5, x6, x7, x8, x9, x10);
        }
    }

    static final class SwitchOptions
    {
        static final int[] optionIds = new int[GameSettings.Options.values().length];

        static
        {
            try
            {
                optionIds[GameSettings.Options.INVERT_MOUSE.ordinal()] = 1;
            }
            catch (NoSuchFieldError var16)
            {
                ;
            }

            try
            {
                optionIds[GameSettings.Options.VIEW_BOBBING.ordinal()] = 2;
            }
            catch (NoSuchFieldError var15)
            {
                ;
            }

            try
            {
                optionIds[GameSettings.Options.ANAGLYPH.ordinal()] = 3;
            }
            catch (NoSuchFieldError var14)
            {
                ;
            }

            try
            {
                optionIds[GameSettings.Options.ADVANCED_OPENGL.ordinal()] = 4;
            }
            catch (NoSuchFieldError var13)
            {
                ;
            }

            try
            {
                optionIds[GameSettings.Options.FBO_ENABLE.ordinal()] = 5;
            }
            catch (NoSuchFieldError var12)
            {
                ;
            }

            try
            {
                optionIds[GameSettings.Options.RENDER_CLOUDS.ordinal()] = 6;
            }
            catch (NoSuchFieldError var11)
            {
                ;
            }

            try
            {
                optionIds[GameSettings.Options.CHAT_COLOR.ordinal()] = 7;
            }
            catch (NoSuchFieldError var10)
            {
                ;
            }

            try
            {
                optionIds[GameSettings.Options.CHAT_LINKS.ordinal()] = 8;
            }
            catch (NoSuchFieldError var9)
            {
                ;
            }

            try
            {
                optionIds[GameSettings.Options.CHAT_LINKS_PROMPT.ordinal()] = 9;
            }
            catch (NoSuchFieldError var8)
            {
                ;
            }

            try
            {
                optionIds[GameSettings.Options.USE_SERVER_TEXTURES.ordinal()] = 10;
            }
            catch (NoSuchFieldError var7)
            {
                ;
            }

            try
            {
                optionIds[GameSettings.Options.SNOOPER_ENABLED.ordinal()] = 11;
            }
            catch (NoSuchFieldError var6)
            {
                ;
            }

            try
            {
                optionIds[GameSettings.Options.USE_FULLSCREEN.ordinal()] = 12;
            }
            catch (NoSuchFieldError var5)
            {
                ;
            }

            try
            {
                optionIds[GameSettings.Options.ENABLE_VSYNC.ordinal()] = 13;
            }
            catch (NoSuchFieldError var4)
            {
                ;
            }

            try
            {
                optionIds[GameSettings.Options.SHOW_CAPE.ordinal()] = 14;
            }
            catch (NoSuchFieldError var3)
            {
                ;
            }

            try
            {
                optionIds[GameSettings.Options.TOUCHSCREEN.ordinal()] = 15;
            }
            catch (NoSuchFieldError var2)
            {
                ;
            }

            try
            {
                optionIds[GameSettings.Options.FORCE_UNICODE_FONT.ordinal()] = 16;
            }
            catch (NoSuchFieldError var1)
            {
                ;
            }
        }
    }
}

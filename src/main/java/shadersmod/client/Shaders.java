package shadersmod.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.src.Config;
import net.minecraft.src.CustomColorizer;
import net.minecraft.src.GlStateManager;
import net.minecraft.src.Lang;
import net.minecraft.src.PropertiesOrdered;
import net.minecraft.src.Reflector;
import net.minecraft.src.StrUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import org.apache.commons.io.IOUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.GLU;
import shadersmod.common.SMCLog;
import shadersmod.uniform.CustomUniforms;
import shadersmod.uniform.LegacyUniforms;
import shadersmod.uniform.ShaderUniformFloat4;
import shadersmod.uniform.ShaderUniformInt;
import shadersmod.uniform.Smoother;

public class Shaders
{
    static Minecraft mc = Minecraft.getMinecraft();
    static EntityRenderer entityRenderer;
    public static boolean isInitializedOnce = false;
    public static boolean isShaderPackInitialized = false;
    public static ContextCapabilities capabilities;
    public static String glVersionString;
    public static String glVendorString;
    public static String glRendererString;
    public static boolean hasGlGenMipmap = false;
    public static boolean hasForge = false;
    public static int numberResetDisplayList = 0;
    static boolean needResetModels = false;
    private static int renderDisplayWidth = 0;
    private static int renderDisplayHeight = 0;
    public static int renderWidth = 0;
    public static int renderHeight = 0;
    public static boolean isRenderingWorld = false;
    public static boolean isRenderingSky = false;
    public static boolean isCompositeRendered = false;
    public static boolean isRenderingDfb = false;
    public static boolean isShadowPass = false;
    public static boolean isSleeping;
    private static boolean isRenderingFirstPersonHand;
    private static boolean isHandRenderedMain;
    public static boolean renderItemKeepDepthMask = false;
    public static boolean itemToRenderMainTranslucent = false;
    static float[] sunPosition = new float[4];
    static float[] moonPosition = new float[4];
    static float[] shadowLightPosition = new float[4];
    static float[] upPosition = new float[4];
    static float[] shadowLightPositionVector = new float[4];
    static float[] upPosModelView = new float[] {0.0F, 100.0F, 0.0F, 0.0F};
    static float[] sunPosModelView = new float[] {0.0F, 100.0F, 0.0F, 0.0F};
    static float[] moonPosModelView = new float[] {0.0F, -100.0F, 0.0F, 0.0F};
    private static float[] tempMat = new float[16];
    static float clearColorR;
    static float clearColorG;
    static float clearColorB;
    static float skyColorR;
    static float skyColorG;
    static float skyColorB;
    static long worldTime = 0L;
    static long lastWorldTime = 0L;
    static long diffWorldTime = 0L;
    static float celestialAngle = 0.0F;
    static float sunAngle = 0.0F;
    static float shadowAngle = 0.0F;
    static int moonPhase = 0;
    static long systemTime = 0L;
    static long lastSystemTime = 0L;
    static long diffSystemTime = 0L;
    static int frameCounter = 0;
    static float frameTime = 0.0F;
    static float frameTimeCounter = 0.0F;
    static int systemTimeInt32 = 0;
    static float rainStrength = 0.0F;
    static float wetness = 0.0F;
    public static float wetnessHalfLife = 600.0F;
    public static float drynessHalfLife = 200.0F;
    public static float eyeBrightnessHalflife = 10.0F;
    static boolean usewetness = false;
    static int isEyeInWater = 0;
    static int eyeBrightness = 0;
    static float eyeBrightnessFadeX = 0.0F;
    static float eyeBrightnessFadeY = 0.0F;
    static float eyePosY = 0.0F;
    static float centerDepth = 0.0F;
    static float centerDepthSmooth = 0.0F;
    static float centerDepthSmoothHalflife = 1.0F;
    static boolean centerDepthSmoothEnabled = false;
    static int superSamplingLevel = 1;
    static float nightVision = 0.0F;
    static float blindness = 0.0F;
    static boolean updateChunksErrorRecorded = false;
    static boolean lightmapEnabled = false;
    static boolean fogEnabled = true;
    public static int entityAttrib = 10;
    public static int midTexCoordAttrib = 11;
    public static int tangentAttrib = 12;
    public static boolean useEntityAttrib = false;
    public static boolean useMidTexCoordAttrib = false;
    public static boolean useMultiTexCoord3Attrib = false;
    public static boolean useTangentAttrib = false;
    public static boolean progUseEntityAttrib = false;
    public static boolean progUseMidTexCoordAttrib = false;
    public static boolean progUseTangentAttrib = false;
    public static int atlasSizeX = 0;
    public static int atlasSizeY = 0;
    public static boolean useEntityColor = true;
    public static ShaderUniformFloat4 uniformEntityColor = new ShaderUniformFloat4("entityColor");
    public static ShaderUniformInt uniformEntityId = new ShaderUniformInt("entityId");
    public static ShaderUniformInt uniformBlockEntityId = new ShaderUniformInt("blockEntityId");
    static double previousCameraPositionX;
    static double previousCameraPositionY;
    static double previousCameraPositionZ;
    static double cameraPositionX;
    static double cameraPositionY;
    static double cameraPositionZ;
    static int shadowPassInterval = 0;
    public static boolean needResizeShadow = false;
    static int shadowMapWidth = 1024;
    static int shadowMapHeight = 1024;
    static int spShadowMapWidth = 1024;
    static int spShadowMapHeight = 1024;
    static float shadowMapFOV = 90.0F;
    static float shadowMapHalfPlane = 160.0F;
    static boolean shadowMapIsOrtho = true;
    static float shadowDistanceRenderMul = -1.0F;
    static int shadowPassCounter = 0;
    static int preShadowPassThirdPersonView;
    public static boolean shouldSkipDefaultShadow = false;
    static boolean waterShadowEnabled = false;
    static final int MaxDrawBuffers = 8;
    static final int MaxColorBuffers = 8;
    static final int MaxDepthBuffers = 3;
    static final int MaxShadowColorBuffers = 8;
    static final int MaxShadowDepthBuffers = 2;
    static int usedColorBuffers = 0;
    static int usedDepthBuffers = 0;
    static int usedShadowColorBuffers = 0;
    static int usedShadowDepthBuffers = 0;
    static int usedColorAttachs = 0;
    static int usedDrawBuffers = 0;
    static int dfb = 0;
    static int sfb = 0;
    private static int[] gbuffersFormat = new int[8];
    public static boolean[] gbuffersClear = new boolean[8];
    public static int activeProgram = 0;
    public static final int ProgramNone = 0;
    public static final int ProgramBasic = 1;
    public static final int ProgramTextured = 2;
    public static final int ProgramTexturedLit = 3;
    public static final int ProgramSkyBasic = 4;
    public static final int ProgramSkyTextured = 5;
    public static final int ProgramClouds = 6;
    public static final int ProgramTerrain = 7;
    public static final int ProgramTerrainSolid = 8;
    public static final int ProgramTerrainCutoutMip = 9;
    public static final int ProgramTerrainCutout = 10;
    public static final int ProgramDamagedBlock = 11;
    public static final int ProgramWater = 12;
    public static final int ProgramBlock = 13;
    public static final int ProgramBeaconBeam = 14;
    public static final int ProgramItem = 15;
    public static final int ProgramEntities = 16;
    public static final int ProgramArmorGlint = 17;
    public static final int ProgramSpiderEyes = 18;
    public static final int ProgramHand = 19;
    public static final int ProgramWeather = 20;
    public static final int ProgramComposite = 21;
    public static final int ProgramComposite1 = 22;
    public static final int ProgramComposite2 = 23;
    public static final int ProgramComposite3 = 24;
    public static final int ProgramComposite4 = 25;
    public static final int ProgramComposite5 = 26;
    public static final int ProgramComposite6 = 27;
    public static final int ProgramComposite7 = 28;
    public static final int ProgramFinal = 29;
    public static final int ProgramShadow = 30;
    public static final int ProgramShadowSolid = 31;
    public static final int ProgramShadowCutout = 32;
    public static final int ProgramDeferred = 33;
    public static final int ProgramDeferred1 = 34;
    public static final int ProgramDeferred2 = 35;
    public static final int ProgramDeferred3 = 36;
    public static final int ProgramDeferred4 = 37;
    public static final int ProgramDeferred5 = 38;
    public static final int ProgramDeferred6 = 39;
    public static final int ProgramDeferred7 = 40;
    public static final int ProgramHandWater = 41;
    public static final int ProgramDeferredLast = 42;
    public static final int ProgramCompositeLast = 43;
    public static final int ProgramCount = 44;
    public static final int MaxCompositePasses = 8;
    public static final int MaxDeferredPasses = 8;
    private static final String[] programNames = new String[] {"", "gbuffers_basic", "gbuffers_textured", "gbuffers_textured_lit", "gbuffers_skybasic", "gbuffers_skytextured", "gbuffers_clouds", "gbuffers_terrain", "gbuffers_terrain_solid", "gbuffers_terrain_cutout_mip", "gbuffers_terrain_cutout", "gbuffers_damagedblock", "gbuffers_water", "gbuffers_block", "gbuffers_beaconbeam", "gbuffers_item", "gbuffers_entities", "gbuffers_armor_glint", "gbuffers_spidereyes", "gbuffers_hand", "gbuffers_weather", "composite", "composite1", "composite2", "composite3", "composite4", "composite5", "composite6", "composite7", "final", "shadow", "shadow_solid", "shadow_cutout", "deferred", "deferred1", "deferred2", "deferred3", "deferred4", "deferred5", "deferred6", "deferred7", "gbuffers_hand_water", "deferred_last", "composite_last"};
    private static final int[] programBackups = new int[] {0, 0, 1, 2, 1, 2, 2, 3, 7, 7, 7, 7, 7, 7, 2, 3, 3, 2, 2, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 30, 30, 0, 0, 0, 0, 0, 0, 0, 0, 19, 0, 0};
    static int[] programsID = new int[44];
    private static int[] programsRef = new int[44];
    private static int programIDCopyDepth = 0;
    private static boolean hasDeferredPrograms = false;
    public static String[] programsDrawBufSettings = new String[44];
    private static String newDrawBufSetting = null;
    static IntBuffer[] programsDrawBuffers = new IntBuffer[44];
    static IntBuffer activeDrawBuffers = null;
    private static String[] programsColorAtmSettings = new String[44];
    private static String newColorAtmSetting = null;
    private static String activeColorAtmSettings = null;
    private static int[] programsCompositeMipmapSetting = new int[44];
    private static int newCompositeMipmapSetting = 0;
    private static int activeCompositeMipmapSetting = 0;
    public static Properties loadedShaders = null;
    public static Properties shadersConfig = null;
    public static ITextureObject defaultTexture = null;
    public static boolean normalMapEnabled = false;
    public static boolean[] shadowHardwareFilteringEnabled = new boolean[2];
    public static boolean[] shadowMipmapEnabled = new boolean[2];
    public static boolean[] shadowFilterNearest = new boolean[2];
    public static boolean[] shadowColorMipmapEnabled = new boolean[8];
    public static boolean[] shadowColorFilterNearest = new boolean[8];
    public static boolean configTweakBlockDamage = false;
    public static boolean configCloudShadow = false;
    public static float configHandDepthMul = 0.125F;
    public static float configRenderResMul = 1.0F;
    public static float configShadowResMul = 1.0F;
    public static int configTexMinFilB = 0;
    public static int configTexMinFilN = 0;
    public static int configTexMinFilS = 0;
    public static int configTexMagFilB = 0;
    public static int configTexMagFilN = 0;
    public static int configTexMagFilS = 0;
    public static boolean configShadowClipFrustrum = true;
    public static boolean configNormalMap = true;
    public static boolean configSpecularMap = true;
    public static PropertyDefaultTrueFalse configOldLighting = new PropertyDefaultTrueFalse("oldLighting", "Classic Lighting", 0);
    public static PropertyDefaultTrueFalse configOldHandLight = new PropertyDefaultTrueFalse("oldHandLight", "Old Hand Light", 0);
    public static int configAntialiasingLevel = 0;
    public static final int texMinFilRange = 3;
    public static final int texMagFilRange = 2;
    public static final String[] texMinFilDesc = new String[] {"Nearest", "Nearest-Nearest", "Nearest-Linear"};
    public static final String[] texMagFilDesc = new String[] {"Nearest", "Linear"};
    public static final int[] texMinFilValue = new int[] {9728, 9984, 9986};
    public static final int[] texMagFilValue = new int[] {9728, 9729};
    static IShaderPack shaderPack = null;
    public static boolean shaderPackLoaded = false;
    static File currentshader;
    static String currentshadername;
    public static String packNameNone = "OFF";
    static String packNameDefault = "(internal)";
    static String shaderpacksdirname = "shaderpacks";
    static String optionsfilename = "optionsshaders.txt";
    static File shadersdir = new File(Minecraft.getMinecraft().mcDataDir, "shaders");
    static File shaderpacksdir = new File(Minecraft.getMinecraft().mcDataDir, shaderpacksdirname);
    static File configFile = new File(Minecraft.getMinecraft().mcDataDir, optionsfilename);
    static ShaderOption[] shaderPackOptions = null;
    static Set<String> shaderPackOptionSliders = null;
    static ShaderProfile[] shaderPackProfiles = null;
    static Map<String, ScreenShaderOptions> shaderPackGuiScreens = null;
    public static PropertyDefaultFastFancyOff shaderPackClouds = new PropertyDefaultFastFancyOff("clouds", "Clouds", 0);
    public static PropertyDefaultTrueFalse shaderPackOldLighting = new PropertyDefaultTrueFalse("oldLighting", "Classic Lighting", 0);
    public static PropertyDefaultTrueFalse shaderPackOldHandLight = new PropertyDefaultTrueFalse("oldHandLight", "Old Hand Light", 0);
    public static PropertyDefaultTrueFalse shaderPackDynamicHandLight = new PropertyDefaultTrueFalse("dynamicHandLight", "Dynamic Hand Light", 0);
    public static PropertyDefaultTrueFalse shaderPackShadowTranslucent = new PropertyDefaultTrueFalse("shadowTranslucent", "Shadow Translucent", 0);
    public static PropertyDefaultTrueFalse shaderPackUnderwaterOverlay = new PropertyDefaultTrueFalse("underwaterOverlay", "Underwater Overlay", 0);
    public static PropertyDefaultTrueFalse shaderPackSun = new PropertyDefaultTrueFalse("sun", "Sun", 0);
    public static PropertyDefaultTrueFalse shaderPackMoon = new PropertyDefaultTrueFalse("moon", "Moon", 0);
    public static PropertyDefaultTrueFalse shaderPackVignette = new PropertyDefaultTrueFalse("vignette", "Vignette", 0);
    public static PropertyDefaultTrueFalse shaderPackBackFaceSolid = new PropertyDefaultTrueFalse("backFace.solid", "Back-face Solid", 0);
    public static PropertyDefaultTrueFalse shaderPackBackFaceCutout = new PropertyDefaultTrueFalse("backFace.cutout", "Back-face Cutout", 0);
    public static PropertyDefaultTrueFalse shaderPackBackFaceCutoutMipped = new PropertyDefaultTrueFalse("backFace.cutoutMipped", "Back-face Cutout Mipped", 0);
    public static PropertyDefaultTrueFalse shaderPackBackFaceTranslucent = new PropertyDefaultTrueFalse("backFace.translucent", "Back-face Translucent", 0);
    private static Map<String, String> shaderPackResources = new HashMap();
    private static World currentWorld = null;
    private static List<Integer> shaderPackDimensions = new ArrayList();
    private static CustomTexture[] customTexturesGbuffers = null;
    private static CustomTexture[] customTexturesComposite = null;
    private static CustomTexture[] customTexturesDeferred = null;
    private static String noiseTexturePath = null;
    private static CustomUniforms customUniforms = null;
    private static final int STAGE_GBUFFERS = 0;
    private static final int STAGE_COMPOSITE = 1;
    private static final int STAGE_DEFERRED = 2;
    private static final String[] STAGE_NAMES = new String[] {"gbuffers", "composite", "deferred"};
    public static final boolean enableShadersOption = true;
    private static final boolean enableShadersDebug = true;
    private static final boolean saveFinalShaders = System.getProperty("shaders.debug.save", "false").equals("true");
    public static float blockLightLevel05 = 0.5F;
    public static float blockLightLevel06 = 0.6F;
    public static float blockLightLevel08 = 0.8F;
    public static float aoLevel = -1.0F;
    public static float sunPathRotation = 0.0F;
    public static float shadowAngleInterval = 0.0F;
    public static int fogMode = 0;
    public static float fogColorR;
    public static float fogColorG;
    public static float fogColorB;
    public static float shadowIntervalSize = 2.0F;
    public static int terrainIconSize = 16;
    public static int[] terrainTextureSize = new int[2];
    private static ICustomTexture noiseTexture;
    private static boolean noiseTextureEnabled = false;
    private static int noiseTextureResolution = 256;
    static final int[] dfbColorTexturesA = new int[16];
    static final int[] colorTexturesToggle = new int[8];
    static final int[] colorTextureTextureImageUnit = new int[] {0, 1, 2, 3, 7, 8, 9, 10};
    static final boolean[][] programsToggleColorTextures = new boolean[44][8];
    private static final int bigBufferSize = 2548;
    private static final ByteBuffer bigBuffer = (ByteBuffer)BufferUtils.createByteBuffer(2548).limit(0);
    static final float[] faProjection = new float[16];
    static final float[] faProjectionInverse = new float[16];
    static final float[] faModelView = new float[16];
    static final float[] faModelViewInverse = new float[16];
    static final float[] faShadowProjection = new float[16];
    static final float[] faShadowProjectionInverse = new float[16];
    static final float[] faShadowModelView = new float[16];
    static final float[] faShadowModelViewInverse = new float[16];
    static final FloatBuffer projection = nextFloatBuffer(16);
    static final FloatBuffer projectionInverse = nextFloatBuffer(16);
    static final FloatBuffer modelView = nextFloatBuffer(16);
    static final FloatBuffer modelViewInverse = nextFloatBuffer(16);
    static final FloatBuffer shadowProjection = nextFloatBuffer(16);
    static final FloatBuffer shadowProjectionInverse = nextFloatBuffer(16);
    static final FloatBuffer shadowModelView = nextFloatBuffer(16);
    static final FloatBuffer shadowModelViewInverse = nextFloatBuffer(16);
    static final FloatBuffer previousProjection = nextFloatBuffer(16);
    static final FloatBuffer previousModelView = nextFloatBuffer(16);
    static final FloatBuffer tempMatrixDirectBuffer = nextFloatBuffer(16);
    static final FloatBuffer tempDirectFloatBuffer = nextFloatBuffer(16);
    static final IntBuffer dfbColorTextures = nextIntBuffer(16);
    static final IntBuffer dfbDepthTextures = nextIntBuffer(3);
    static final IntBuffer sfbColorTextures = nextIntBuffer(8);
    static final IntBuffer sfbDepthTextures = nextIntBuffer(2);
    static final IntBuffer dfbDrawBuffers = nextIntBuffer(8);
    static final IntBuffer sfbDrawBuffers = nextIntBuffer(8);
    static final IntBuffer drawBuffersNone = nextIntBuffer(8);
    static final IntBuffer drawBuffersAll = nextIntBuffer(8);
    static final IntBuffer drawBuffersClear0 = nextIntBuffer(8);
    static final IntBuffer drawBuffersClear1 = nextIntBuffer(8);
    static final IntBuffer drawBuffersClearColor = nextIntBuffer(8);
    static final IntBuffer drawBuffersColorAtt0 = nextIntBuffer(8);
    static final IntBuffer[] drawBuffersBuffer = nextIntBufferArray(44, 8);
    static Map<Block, Integer> mapBlockToEntityData;
    private static final String[] formatNames;
    private static final int[] formatIds;
    private static final Pattern patternLoadEntityDataMap;
    public static int[] entityData;
    public static int entityDataIndex;

    private static ByteBuffer nextByteBuffer(int size)
    {
        ByteBuffer buffer = bigBuffer;
        int pos = buffer.limit();
        buffer.position(pos).limit(pos + size);
        return buffer.slice();
    }

    private static IntBuffer nextIntBuffer(int size)
    {
        ByteBuffer buffer = bigBuffer;
        int pos = buffer.limit();
        buffer.position(pos).limit(pos + size * 4);
        return buffer.asIntBuffer();
    }

    private static FloatBuffer nextFloatBuffer(int size)
    {
        ByteBuffer buffer = bigBuffer;
        int pos = buffer.limit();
        buffer.position(pos).limit(pos + size * 4);
        return buffer.asFloatBuffer();
    }

    private static IntBuffer[] nextIntBufferArray(int count, int size)
    {
        IntBuffer[] aib = new IntBuffer[count];

        for (int i = 0; i < count; ++i)
        {
            aib[i] = nextIntBuffer(size);
        }

        return aib;
    }

    public static void loadConfig()
    {
        SMCLog.info("Load ShadersMod configuration.");

        try
        {
            if (!shaderpacksdir.exists())
            {
                shaderpacksdir.mkdir();
            }
        }
        catch (Exception var8)
        {
            SMCLog.severe("Failed to open the shaderpacks directory: " + shaderpacksdir);
        }

        shadersConfig = new PropertiesOrdered();
        shadersConfig.setProperty(EnumShaderOption.SHADER_PACK.getPropertyKey(), "");

        if (configFile.exists())
        {
            try
            {
                FileReader ops = new FileReader(configFile);
                shadersConfig.load(ops);
                ops.close();
            }
            catch (Exception var7)
            {
                ;
            }
        }

        if (!configFile.exists())
        {
            try
            {
                storeConfig();
            }
            catch (Exception var6)
            {
                ;
            }
        }

        EnumShaderOption[] var9 = EnumShaderOption.values();

        for (int i = 0; i < var9.length; ++i)
        {
            EnumShaderOption op = var9[i];
            String key = op.getPropertyKey();
            String def = op.getValueDefault();
            String val = shadersConfig.getProperty(key, def);
            setEnumShaderOption(op, val);
        }

        loadShaderPack();
    }

    private static void setEnumShaderOption(EnumShaderOption eso, String str)
    {
        if (str == null)
        {
            str = eso.getValueDefault();
        }

        switch (Shaders.NamelessClass1174115805.$SwitchMap$shadersmod$client$EnumShaderOption[eso.ordinal()])
        {
            case 1:
                configAntialiasingLevel = Config.parseInt(str, 0);
                break;

            case 2:
                configNormalMap = Config.parseBoolean(str, true);
                break;

            case 3:
                configSpecularMap = Config.parseBoolean(str, true);
                break;

            case 4:
                configRenderResMul = Config.parseFloat(str, 1.0F);
                break;

            case 5:
                configShadowResMul = Config.parseFloat(str, 1.0F);
                break;

            case 6:
                configHandDepthMul = Config.parseFloat(str, 0.125F);
                break;

            case 7:
                configCloudShadow = Config.parseBoolean(str, true);
                break;

            case 8:
                configOldHandLight.setPropertyValue(str);
                break;

            case 9:
                configOldLighting.setPropertyValue(str);
                break;

            case 10:
                currentshadername = str;
                break;

            case 11:
                configTweakBlockDamage = Config.parseBoolean(str, true);
                break;

            case 12:
                configShadowClipFrustrum = Config.parseBoolean(str, true);
                break;

            case 13:
                configTexMinFilB = Config.parseInt(str, 0);
                break;

            case 14:
                configTexMinFilN = Config.parseInt(str, 0);
                break;

            case 15:
                configTexMinFilS = Config.parseInt(str, 0);
                break;

            case 16:
                configTexMagFilB = Config.parseInt(str, 0);
                break;

            case 17:
                configTexMagFilB = Config.parseInt(str, 0);
                break;

            case 18:
                configTexMagFilB = Config.parseInt(str, 0);
                break;

            default:
                throw new IllegalArgumentException("Unknown option: " + eso);
        }
    }

    public static void storeConfig()
    {
        SMCLog.info("Save ShadersMod configuration.");

        if (shadersConfig == null)
        {
            shadersConfig = new PropertiesOrdered();
        }

        EnumShaderOption[] ops = EnumShaderOption.values();

        for (int ex = 0; ex < ops.length; ++ex)
        {
            EnumShaderOption op = ops[ex];
            String key = op.getPropertyKey();
            String val = getEnumShaderOption(op);
            shadersConfig.setProperty(key, val);
        }

        try
        {
            FileWriter var6 = new FileWriter(configFile);
            shadersConfig.store(var6, (String)null);
            var6.close();
        }
        catch (Exception var5)
        {
            SMCLog.severe("Error saving configuration: " + var5.getClass().getName() + ": " + var5.getMessage());
        }
    }

    public static String getEnumShaderOption(EnumShaderOption eso)
    {
        switch (Shaders.NamelessClass1174115805.$SwitchMap$shadersmod$client$EnumShaderOption[eso.ordinal()])
        {
            case 1:
                return Integer.toString(configAntialiasingLevel);

            case 2:
                return Boolean.toString(configNormalMap);

            case 3:
                return Boolean.toString(configSpecularMap);

            case 4:
                return Float.toString(configRenderResMul);

            case 5:
                return Float.toString(configShadowResMul);

            case 6:
                return Float.toString(configHandDepthMul);

            case 7:
                return Boolean.toString(configCloudShadow);

            case 8:
                return configOldHandLight.getPropertyValue();

            case 9:
                return configOldLighting.getPropertyValue();

            case 10:
                return currentshadername;

            case 11:
                return Boolean.toString(configTweakBlockDamage);

            case 12:
                return Boolean.toString(configShadowClipFrustrum);

            case 13:
                return Integer.toString(configTexMinFilB);

            case 14:
                return Integer.toString(configTexMinFilN);

            case 15:
                return Integer.toString(configTexMinFilS);

            case 16:
                return Integer.toString(configTexMagFilB);

            case 17:
                return Integer.toString(configTexMagFilB);

            case 18:
                return Integer.toString(configTexMagFilB);

            default:
                throw new IllegalArgumentException("Unknown option: " + eso);
        }
    }

    public static void setShaderPack(String par1name)
    {
        currentshadername = par1name;
        shadersConfig.setProperty(EnumShaderOption.SHADER_PACK.getPropertyKey(), par1name);
        loadShaderPack();
    }

    public static void loadShaderPack()
    {
        boolean shaderPackLoadedPrev = shaderPackLoaded;
        boolean oldLightingPrev = isOldLighting();
        shaderPackLoaded = false;

        if (shaderPack != null)
        {
            shaderPack.close();
            shaderPack = null;
            shaderPackResources.clear();
            shaderPackDimensions.clear();
            shaderPackOptions = null;
            shaderPackOptionSliders = null;
            shaderPackProfiles = null;
            shaderPackGuiScreens = null;
            shaderPackClouds.resetValue();
            shaderPackOldHandLight.resetValue();
            shaderPackDynamicHandLight.resetValue();
            shaderPackOldLighting.resetValue();
            resetCustomTextures();
            noiseTexturePath = null;
        }

        boolean shadersBlocked = false;

        if (Config.isAntialiasing())
        {
            SMCLog.info("Shaders can not be loaded, Antialiasing is enabled: " + Config.getAntialiasingLevel() + "x");
            shadersBlocked = true;
        }

        if (Config.isAnisotropicFiltering())
        {
            SMCLog.info("Shaders can not be loaded, Anisotropic Filtering is enabled: " + Config.getAnisotropicFilterLevel() + "x");
            shadersBlocked = true;
        }

        if (Config.isFastRender())
        {
            SMCLog.info("Shaders can not be loaded, Fast Render is enabled.");
            shadersBlocked = true;
        }

        String packName = shadersConfig.getProperty(EnumShaderOption.SHADER_PACK.getPropertyKey(), packNameDefault);

        if (!packName.isEmpty() && !packName.equals(packNameNone) && !shadersBlocked)
        {
            if (packName.equals(packNameDefault))
            {
                shaderPack = new ShaderPackDefault();
                shaderPackLoaded = true;
            }
            else
            {
                try
                {
                    File formatChanged = new File(shaderpacksdir, packName);

                    if (formatChanged.isDirectory())
                    {
                        shaderPack = new ShaderPackFolder(packName, formatChanged);
                        shaderPackLoaded = true;
                    }
                    else if (formatChanged.isFile() && packName.toLowerCase().endsWith(".zip"))
                    {
                        shaderPack = new ShaderPackZip(packName, formatChanged);
                        shaderPackLoaded = true;
                    }
                }
                catch (Exception var6)
                {
                    ;
                }
            }
        }

        if (shaderPack != null)
        {
            SMCLog.info("Loaded shaderpack: " + getShaderPackName());
        }
        else
        {
            SMCLog.info("No shaderpack loaded.");
            shaderPack = new ShaderPackNone();
        }

        loadShaderPackResources();
        loadShaderPackDimensions();
        shaderPackOptions = loadShaderPackOptions();
        loadShaderPackProperties();
        boolean formatChanged1 = shaderPackLoaded != shaderPackLoadedPrev;
        boolean oldLightingChanged = isOldLighting() != oldLightingPrev;

        if ((formatChanged1 || oldLightingChanged) && mc.entityRenderer != null)
        {
            mc.entityRenderer.setFxaaShader(0);
            mc.refreshResources();
        }
    }

    private static void loadShaderPackDimensions()
    {
        shaderPackDimensions.clear();

        for (int ids = -128; ids <= 128; ++ids)
        {
            String worldDir = "/shaders/world" + ids;

            if (shaderPack.hasDirectory(worldDir))
            {
                shaderPackDimensions.add(Integer.valueOf(ids));
            }
        }

        if (shaderPackDimensions.size() > 0)
        {
            Integer[] var2 = (Integer[])((Integer[])shaderPackDimensions.toArray(new Integer[shaderPackDimensions.size()]));
            Config.dbg("[Shaders] Worlds: " + Config.arrayToString((Object[])var2));
        }
    }

    private static void loadShaderPackProperties()
    {
        shaderPackClouds.resetValue();
        shaderPackOldHandLight.resetValue();
        shaderPackDynamicHandLight.resetValue();
        shaderPackOldLighting.resetValue();
        shaderPackShadowTranslucent.resetValue();
        shaderPackUnderwaterOverlay.resetValue();
        shaderPackSun.resetValue();
        shaderPackMoon.resetValue();
        shaderPackVignette.resetValue();
        shaderPackBackFaceSolid.resetValue();
        shaderPackBackFaceCutout.resetValue();
        shaderPackBackFaceCutoutMipped.resetValue();
        shaderPackBackFaceTranslucent.resetValue();
        BlockAliases.reset();
        customUniforms = null;
        LegacyUniforms.reset();

        if (shaderPack != null)
        {
            BlockAliases.update(shaderPack);
            String path = "/shaders/shaders.properties";

            try
            {
                InputStream e = shaderPack.getResourceAsStream(path);

                if (e == null)
                {
                    return;
                }

                PropertiesOrdered props = new PropertiesOrdered();
                props.load(e);
                e.close();
                shaderPackClouds.loadFrom(props);
                shaderPackOldHandLight.loadFrom(props);
                shaderPackDynamicHandLight.loadFrom(props);
                shaderPackOldLighting.loadFrom(props);
                shaderPackShadowTranslucent.loadFrom(props);
                shaderPackUnderwaterOverlay.loadFrom(props);
                shaderPackSun.loadFrom(props);
                shaderPackVignette.loadFrom(props);
                shaderPackMoon.loadFrom(props);
                shaderPackBackFaceSolid.loadFrom(props);
                shaderPackBackFaceCutout.loadFrom(props);
                shaderPackBackFaceCutoutMipped.loadFrom(props);
                shaderPackBackFaceTranslucent.loadFrom(props);
                shaderPackOptionSliders = ShaderPackParser.parseOptionSliders(props, shaderPackOptions);
                shaderPackProfiles = ShaderPackParser.parseProfiles(props, shaderPackOptions);
                shaderPackGuiScreens = ShaderPackParser.parseGuiScreens(props, shaderPackProfiles, shaderPackOptions);
                customTexturesGbuffers = loadCustomTextures(props, 0);
                customTexturesComposite = loadCustomTextures(props, 1);
                customTexturesDeferred = loadCustomTextures(props, 2);
                noiseTexturePath = props.getProperty("texture.noise");

                if (noiseTexturePath != null)
                {
                    noiseTextureEnabled = true;
                }

                customUniforms = ShaderPackParser.parseCustomUniforms(props);
            }
            catch (IOException var3)
            {
                Config.warn("[Shaders] Error reading: " + path);
            }
        }
    }

    private static CustomTexture[] loadCustomTextures(Properties props, int stage)
    {
        String PREFIX_TEXTURE = "texture." + STAGE_NAMES[stage] + ".";
        Set keys = props.keySet();
        ArrayList list = new ArrayList();
        Iterator cts = keys.iterator();

        while (cts.hasNext())
        {
            String key = (String)cts.next();

            if (key.startsWith(PREFIX_TEXTURE))
            {
                String name = key.substring(PREFIX_TEXTURE.length());
                String path = props.getProperty(key).trim();
                int index = getTextureIndex(stage, name);

                if (index < 0)
                {
                    SMCLog.warning("Invalid texture name: " + key);
                }
                else
                {
                    CustomTexture ct = loadCustomTexture(index, path);

                    if (ct != null)
                    {
                        list.add(ct);
                    }
                }
            }
        }

        if (list.size() <= 0)
        {
            return null;
        }
        else
        {
            CustomTexture[] cts1 = (CustomTexture[])((CustomTexture[])list.toArray(new CustomTexture[list.size()]));
            return cts1;
        }
    }

    private static CustomTexture loadCustomTexture(int index, String path)
    {
        if (path == null)
        {
            return null;
        }
        else
        {
            path = path.trim();

            if (path.indexOf(46) < 0)
            {
                path = path + ".png";
            }

            try
            {
                String e = "shaders/" + StrUtils.removePrefix(path, "/");
                InputStream in = shaderPack.getResourceAsStream(e);

                if (in == null)
                {
                    SMCLog.warning("Texture not found: " + path);
                    return null;
                }
                else
                {
                    IOUtils.closeQuietly(in);
                    SimpleShaderTexture tex = new SimpleShaderTexture(e);
                    tex.loadTexture(mc.getResourceManager());
                    CustomTexture ct = new CustomTexture(index, e, tex);
                    return ct;
                }
            }
            catch (IOException var6)
            {
                SMCLog.warning("Error loading texture: " + path);
                SMCLog.warning("" + var6.getClass().getName() + ": " + var6.getMessage());
                return null;
            }
        }
    }

    private static int getTextureIndex(int stage, String name)
    {
        if (stage == 0)
        {
            if (name.equals("texture"))
            {
                return 0;
            }

            if (name.equals("lightmap"))
            {
                return 1;
            }

            if (name.equals("normals"))
            {
                return 2;
            }

            if (name.equals("specular"))
            {
                return 3;
            }

            if (name.equals("shadowtex0") || name.equals("watershadow"))
            {
                return 4;
            }

            if (name.equals("shadow"))
            {
                return waterShadowEnabled ? 5 : 4;
            }

            if (name.equals("shadowtex1"))
            {
                return 5;
            }

            if (name.equals("depthtex0"))
            {
                return 6;
            }

            if (name.equals("gaux1"))
            {
                return 7;
            }

            if (name.equals("gaux2"))
            {
                return 8;
            }

            if (name.equals("gaux3"))
            {
                return 9;
            }

            if (name.equals("gaux4"))
            {
                return 10;
            }

            if (name.equals("depthtex1"))
            {
                return 12;
            }

            if (name.equals("shadowcolor0") || name.equals("shadowcolor"))
            {
                return 13;
            }

            if (name.equals("shadowcolor1"))
            {
                return 14;
            }

            if (name.equals("noisetex"))
            {
                return 15;
            }
        }

        if (stage == 1 || stage == 2)
        {
            if (name.equals("colortex0") || name.equals("colortex0"))
            {
                return 0;
            }

            if (name.equals("colortex1") || name.equals("gdepth"))
            {
                return 1;
            }

            if (name.equals("colortex2") || name.equals("gnormal"))
            {
                return 2;
            }

            if (name.equals("colortex3") || name.equals("composite"))
            {
                return 3;
            }

            if (name.equals("shadowtex0") || name.equals("watershadow"))
            {
                return 4;
            }

            if (name.equals("shadow"))
            {
                return waterShadowEnabled ? 5 : 4;
            }

            if (name.equals("shadowtex1"))
            {
                return 5;
            }

            if (name.equals("depthtex0") || name.equals("gdepthtex"))
            {
                return 6;
            }

            if (name.equals("colortex4") || name.equals("gaux1"))
            {
                return 7;
            }

            if (name.equals("colortex5") || name.equals("gaux2"))
            {
                return 8;
            }

            if (name.equals("colortex6") || name.equals("gaux3"))
            {
                return 9;
            }

            if (name.equals("colortex7") || name.equals("gaux4"))
            {
                return 10;
            }

            if (name.equals("depthtex1"))
            {
                return 11;
            }

            if (name.equals("depthtex2"))
            {
                return 12;
            }

            if (name.equals("shadowcolor0") || name.equals("shadowcolor"))
            {
                return 13;
            }

            if (name.equals("shadowcolor1"))
            {
                return 14;
            }

            if (name.equals("noisetex"))
            {
                return 15;
            }
        }

        return -1;
    }

    private static void bindCustomTextures(CustomTexture[] cts)
    {
        if (cts != null)
        {
            for (int i = 0; i < cts.length; ++i)
            {
                CustomTexture ct = cts[i];
                GlStateManager.setActiveTexture(33984 + ct.getTextureUnit());
                ITextureObject tex = ct.getTexture();
                GlStateManager.bindTexture(tex.getGlTextureId());
            }
        }
    }

    private static void resetCustomTextures()
    {
        deleteCustomTextures(customTexturesGbuffers);
        deleteCustomTextures(customTexturesComposite);
        deleteCustomTextures(customTexturesDeferred);
        customTexturesGbuffers = null;
        customTexturesComposite = null;
        customTexturesDeferred = null;
    }

    private static void deleteCustomTextures(CustomTexture[] cts)
    {
        if (cts != null)
        {
            for (int i = 0; i < cts.length; ++i)
            {
                CustomTexture ct = cts[i];
                ct.deleteTexture();
            }
        }
    }

    public static ShaderOption[] getShaderPackOptions(String screenName)
    {
        ShaderOption[] ops = (ShaderOption[])shaderPackOptions.clone();

        if (shaderPackGuiScreens == null)
        {
            if (shaderPackProfiles != null)
            {
                ShaderOptionProfile var9 = new ShaderOptionProfile(shaderPackProfiles, ops);
                ops = (ShaderOption[])((ShaderOption[])Config.addObjectToArray(ops, var9, 0));
            }

            ops = getVisibleOptions(ops);
            return ops;
        }
        else
        {
            String key = screenName != null ? "screen." + screenName : "screen";
            ScreenShaderOptions sso = (ScreenShaderOptions)shaderPackGuiScreens.get(key);

            if (sso == null)
            {
                return new ShaderOption[0];
            }
            else
            {
                ShaderOption[] sos = sso.getShaderOptions();
                ArrayList list = new ArrayList();

                for (int sosExp = 0; sosExp < sos.length; ++sosExp)
                {
                    ShaderOption so = sos[sosExp];

                    if (so == null)
                    {
                        list.add((Object)null);
                    }
                    else if (so instanceof ShaderOptionRest)
                    {
                        ShaderOption[] restOps = getShaderOptionsRest(shaderPackGuiScreens, ops);
                        list.addAll(Arrays.asList(restOps));
                    }
                    else
                    {
                        list.add(so);
                    }
                }

                ShaderOption[] var10 = (ShaderOption[])((ShaderOption[])list.toArray(new ShaderOption[list.size()]));
                return var10;
            }
        }
    }

    public static int getShaderPackColumns(String screenName, int def)
    {
        String key = screenName != null ? "screen." + screenName : "screen";

        if (shaderPackGuiScreens == null)
        {
            return def;
        }
        else
        {
            ScreenShaderOptions sso = (ScreenShaderOptions)shaderPackGuiScreens.get(key);
            return sso == null ? def : sso.getColumns();
        }
    }

    private static ShaderOption[] getShaderOptionsRest(Map<String, ScreenShaderOptions> mapScreens, ShaderOption[] ops)
    {
        HashSet setNames = new HashSet();
        Set keys = mapScreens.keySet();
        Iterator list = keys.iterator();

        while (list.hasNext())
        {
            String sos = (String)list.next();
            ScreenShaderOptions so = (ScreenShaderOptions)mapScreens.get(sos);
            ShaderOption[] name = so.getShaderOptions();

            for (int v = 0; v < name.length; ++v)
            {
                ShaderOption so1 = name[v];

                if (so1 != null)
                {
                    setNames.add(so1.getName());
                }
            }
        }

        ArrayList var10 = new ArrayList();

        for (int var11 = 0; var11 < ops.length; ++var11)
        {
            ShaderOption var13 = ops[var11];

            if (var13.isVisible())
            {
                String var14 = var13.getName();

                if (!setNames.contains(var14))
                {
                    var10.add(var13);
                }
            }
        }

        ShaderOption[] var12 = (ShaderOption[])((ShaderOption[])var10.toArray(new ShaderOption[var10.size()]));
        return var12;
    }

    public static ShaderOption getShaderOption(String name)
    {
        return ShaderUtils.getShaderOption(name, shaderPackOptions);
    }

    public static ShaderOption[] getShaderPackOptions()
    {
        return shaderPackOptions;
    }

    public static boolean isShaderPackOptionSlider(String name)
    {
        return shaderPackOptionSliders == null ? false : shaderPackOptionSliders.contains(name);
    }

    private static ShaderOption[] getVisibleOptions(ShaderOption[] ops)
    {
        ArrayList list = new ArrayList();

        for (int sos = 0; sos < ops.length; ++sos)
        {
            ShaderOption so = ops[sos];

            if (so.isVisible())
            {
                list.add(so);
            }
        }

        ShaderOption[] var4 = (ShaderOption[])((ShaderOption[])list.toArray(new ShaderOption[list.size()]));
        return var4;
    }

    public static void saveShaderPackOptions()
    {
        saveShaderPackOptions(shaderPackOptions, shaderPack);
    }

    private static void saveShaderPackOptions(ShaderOption[] sos, IShaderPack sp)
    {
        Properties props = new Properties();

        if (shaderPackOptions != null)
        {
            for (int e = 0; e < sos.length; ++e)
            {
                ShaderOption so = sos[e];

                if (so.isChanged() && so.isEnabled())
                {
                    props.setProperty(so.getName(), so.getValue());
                }
            }
        }

        try
        {
            saveOptionProperties(sp, props);
        }
        catch (IOException var5)
        {
            Config.warn("[Shaders] Error saving configuration for " + shaderPack.getName());
            var5.printStackTrace();
        }
    }

    private static void saveOptionProperties(IShaderPack sp, Properties props) throws IOException
    {
        String path = shaderpacksdirname + "/" + sp.getName() + ".txt";
        File propFile = new File(Minecraft.getMinecraft().mcDataDir, path);

        if (props.isEmpty())
        {
            propFile.delete();
        }
        else
        {
            FileOutputStream fos = new FileOutputStream(propFile);
            props.store(fos, (String)null);
            fos.flush();
            fos.close();
        }
    }

    private static ShaderOption[] loadShaderPackOptions()
    {
        try
        {
            ShaderOption[] e = ShaderPackParser.parseShaderPackOptions(shaderPack, programNames, shaderPackDimensions);
            Properties props = loadOptionProperties(shaderPack);

            for (int i = 0; i < e.length; ++i)
            {
                ShaderOption so = e[i];
                String val = props.getProperty(so.getName());

                if (val != null)
                {
                    so.resetValue();

                    if (!so.setValue(val))
                    {
                        Config.warn("[Shaders] Invalid value, option: " + so.getName() + ", value: " + val);
                    }
                }
            }

            return e;
        }
        catch (IOException var5)
        {
            Config.warn("[Shaders] Error reading configuration for " + shaderPack.getName());
            var5.printStackTrace();
            return null;
        }
    }

    private static Properties loadOptionProperties(IShaderPack sp) throws IOException
    {
        Properties props = new Properties();
        String path = shaderpacksdirname + "/" + sp.getName() + ".txt";
        File propFile = new File(Minecraft.getMinecraft().mcDataDir, path);

        if (propFile.exists() && propFile.isFile() && propFile.canRead())
        {
            FileInputStream fis = new FileInputStream(propFile);
            props.load(fis);
            fis.close();
            return props;
        }
        else
        {
            return props;
        }
    }

    public static ShaderOption[] getChangedOptions(ShaderOption[] ops)
    {
        ArrayList list = new ArrayList();

        for (int cops = 0; cops < ops.length; ++cops)
        {
            ShaderOption op = ops[cops];

            if (op.isEnabled() && op.isChanged())
            {
                list.add(op);
            }
        }

        ShaderOption[] var4 = (ShaderOption[])((ShaderOption[])list.toArray(new ShaderOption[list.size()]));
        return var4;
    }

    private static String applyOptions(String line, ShaderOption[] ops)
    {
        if (ops != null && ops.length > 0)
        {
            for (int i = 0; i < ops.length; ++i)
            {
                ShaderOption op = ops[i];
                String opName = op.getName();

                if (op.matchesLine(line))
                {
                    line = op.getSourceLine();
                    break;
                }
            }

            return line;
        }
        else
        {
            return line;
        }
    }

    static ArrayList listOfShaders()
    {
        ArrayList list = new ArrayList();
        list.add(packNameNone);
        list.add(packNameDefault);

        try
        {
            if (!shaderpacksdir.exists())
            {
                shaderpacksdir.mkdir();
            }

            File[] e = shaderpacksdir.listFiles();

            for (int i = 0; i < e.length; ++i)
            {
                File file = e[i];
                String name = file.getName();

                if (file.isDirectory())
                {
                    if (!name.equals("debug"))
                    {
                        File subDir = new File(file, "shaders");

                        if (subDir.exists() && subDir.isDirectory())
                        {
                            list.add(name);
                        }
                    }
                }
                else if (file.isFile() && name.toLowerCase().endsWith(".zip"))
                {
                    list.add(name);
                }
            }
        }
        catch (Exception var6)
        {
            ;
        }

        return list;
    }

    static String versiontostring(int vv)
    {
        String vs = Integer.toString(vv);
        return Integer.toString(Integer.parseInt(vs.substring(1, 3))) + "." + Integer.toString(Integer.parseInt(vs.substring(3, 5))) + "." + Integer.toString(Integer.parseInt(vs.substring(5)));
    }

    static void checkOptifine() {}

    public static int checkFramebufferStatus(String location)
    {
        int status = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);

        if (status != 36053)
        {
            System.err.format("FramebufferStatus 0x%04X at %s\n", new Object[] {Integer.valueOf(status), location});
        }

        return status;
    }

    public static int checkGLError(String location)
    {
        int errorCode = GL11.glGetError();

        if (errorCode != 0)
        {
            boolean skipPrint = false;

            if (!skipPrint)
            {
                if (errorCode == 1286)
                {
                    int status = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);
                    System.err.format("GL error 0x%04X: %s (Fb status 0x%04X) at %s\n", new Object[] {Integer.valueOf(errorCode), GLU.gluErrorString(errorCode), Integer.valueOf(status), location});
                }
                else
                {
                    System.err.format("GL error 0x%04X: %s at %s\n", new Object[] {Integer.valueOf(errorCode), GLU.gluErrorString(errorCode), location});
                }
            }
        }

        return errorCode;
    }

    public static int checkGLError(String location, String info)
    {
        int errorCode = GL11.glGetError();

        if (errorCode != 0)
        {
            System.err.format("GL error 0x%04x: %s at %s %s\n", new Object[] {Integer.valueOf(errorCode), GLU.gluErrorString(errorCode), location, info});
        }

        return errorCode;
    }

    public static int checkGLError(String location, String info1, String info2)
    {
        int errorCode = GL11.glGetError();

        if (errorCode != 0)
        {
            System.err.format("GL error 0x%04x: %s at %s %s %s\n", new Object[] {Integer.valueOf(errorCode), GLU.gluErrorString(errorCode), location, info1, info2});
        }

        return errorCode;
    }

    private static void printChat(String str)
    {
        mc.ingameGUI.getChatGui().printChatMessage(new ChatComponentText(str));
    }

    private static void printChatAndLogError(String str)
    {
        SMCLog.severe(str);
        mc.ingameGUI.getChatGui().printChatMessage(new ChatComponentText(str));
    }

    public static void printIntBuffer(String title, IntBuffer buf)
    {
        StringBuilder sb = new StringBuilder(128);
        sb.append(title).append(" [pos ").append(buf.position()).append(" lim ").append(buf.limit()).append(" cap ").append(buf.capacity()).append(" :");
        int lim = buf.limit();

        for (int i = 0; i < lim; ++i)
        {
            sb.append(" ").append(buf.get(i));
        }

        sb.append("]");
        SMCLog.info(sb.toString());
    }

    public static void startup(Minecraft mc)
    {
        checkShadersModInstalled();
        mc = mc;
        mc = Minecraft.getMinecraft();
        capabilities = GLContext.getCapabilities();
        glVersionString = GL11.glGetString(GL11.GL_VERSION);
        glVendorString = GL11.glGetString(GL11.GL_VENDOR);
        glRendererString = GL11.glGetString(GL11.GL_RENDERER);
        SMCLog.info("ShadersMod version: 2.4.12");
        SMCLog.info("OpenGL Version: " + glVersionString);
        SMCLog.info("Vendor:  " + glVendorString);
        SMCLog.info("Renderer: " + glRendererString);
        SMCLog.info("Capabilities: " + (capabilities.OpenGL20 ? " 2.0 " : " - ") + (capabilities.OpenGL21 ? " 2.1 " : " - ") + (capabilities.OpenGL30 ? " 3.0 " : " - ") + (capabilities.OpenGL32 ? " 3.2 " : " - ") + (capabilities.OpenGL40 ? " 4.0 " : " - "));
        SMCLog.info("GL_MAX_DRAW_BUFFERS: " + GL11.glGetInteger(GL20.GL_MAX_DRAW_BUFFERS));
        SMCLog.info("GL_MAX_COLOR_ATTACHMENTS_EXT: " + GL11.glGetInteger(36063));
        SMCLog.info("GL_MAX_TEXTURE_IMAGE_UNITS: " + GL11.glGetInteger(GL20.GL_MAX_TEXTURE_IMAGE_UNITS));
        hasGlGenMipmap = capabilities.OpenGL30;
        loadConfig();
    }

    private static String toStringYN(boolean b)
    {
        return b ? "Y" : "N";
    }

    public static void updateBlockLightLevel()
    {
        if (isOldLighting())
        {
            blockLightLevel05 = 0.5F;
            blockLightLevel06 = 0.6F;
            blockLightLevel08 = 0.8F;
        }
        else
        {
            blockLightLevel05 = 1.0F;
            blockLightLevel06 = 1.0F;
            blockLightLevel08 = 1.0F;
        }
    }

    public static boolean isOldHandLight()
    {
        return !configOldHandLight.isDefault() ? configOldHandLight.isTrue() : (!shaderPackOldHandLight.isDefault() ? shaderPackOldHandLight.isTrue() : true);
    }

    public static boolean isDynamicHandLight()
    {
        return !shaderPackDynamicHandLight.isDefault() ? shaderPackDynamicHandLight.isTrue() : true;
    }

    public static boolean isOldLighting()
    {
        return !configOldLighting.isDefault() ? configOldLighting.isTrue() : (!shaderPackOldLighting.isDefault() ? shaderPackOldLighting.isTrue() : true);
    }

    public static boolean isRenderShadowTranslucent()
    {
        return !shaderPackShadowTranslucent.isFalse();
    }

    public static boolean isUnderwaterOverlay()
    {
        return !shaderPackUnderwaterOverlay.isFalse();
    }

    public static boolean isSun()
    {
        return !shaderPackSun.isFalse();
    }

    public static boolean isMoon()
    {
        return !shaderPackMoon.isFalse();
    }

    public static boolean isVignette()
    {
        return !shaderPackVignette.isFalse();
    }

    public static void init()
    {
        boolean firstInit;

        if (!isInitializedOnce)
        {
            isInitializedOnce = true;
            firstInit = true;
        }
        else
        {
            firstInit = false;
        }

        if (!isShaderPackInitialized)
        {
            checkGLError("Shaders.init pre");

            if (getShaderPackName() != null)
            {
                ;
            }

            if (!capabilities.OpenGL20)
            {
                printChatAndLogError("No OpenGL 2.0");
            }

            if (!capabilities.GL_EXT_framebuffer_object)
            {
                printChatAndLogError("No EXT_framebuffer_object");
            }

            dfbDrawBuffers.position(0).limit(8);
            dfbColorTextures.position(0).limit(16);
            dfbDepthTextures.position(0).limit(3);
            sfbDrawBuffers.position(0).limit(8);
            sfbDepthTextures.position(0).limit(2);
            sfbColorTextures.position(0).limit(8);
            usedColorBuffers = 4;
            usedDepthBuffers = 1;
            usedShadowColorBuffers = 0;
            usedShadowDepthBuffers = 0;
            usedColorAttachs = 1;
            usedDrawBuffers = 1;
            Arrays.fill(gbuffersFormat, 6408);
            Arrays.fill(gbuffersClear, true);
            Arrays.fill(shadowHardwareFilteringEnabled, false);
            Arrays.fill(shadowMipmapEnabled, false);
            Arrays.fill(shadowFilterNearest, false);
            Arrays.fill(shadowColorMipmapEnabled, false);
            Arrays.fill(shadowColorFilterNearest, false);
            centerDepthSmoothEnabled = false;
            noiseTextureEnabled = false;
            sunPathRotation = 0.0F;
            shadowIntervalSize = 2.0F;
            shadowMapWidth = 1024;
            shadowMapHeight = 1024;
            spShadowMapWidth = 1024;
            spShadowMapHeight = 1024;
            shadowMapFOV = 90.0F;
            shadowMapHalfPlane = 160.0F;
            shadowMapIsOrtho = true;
            shadowDistanceRenderMul = -1.0F;
            aoLevel = -1.0F;
            useEntityAttrib = false;
            useMidTexCoordAttrib = false;
            useMultiTexCoord3Attrib = false;
            useTangentAttrib = false;
            waterShadowEnabled = false;
            updateChunksErrorRecorded = false;
            updateBlockLightLevel();
            Smoother.reset();
            LegacyUniforms.reset();
            ShaderProfile activeProfile = ShaderUtils.detectProfile(shaderPackProfiles, shaderPackOptions, false);
            String worldPrefix = "";
            int maxDrawBuffers;

            if (currentWorld != null)
            {
                maxDrawBuffers = currentWorld.provider.dimensionId;

                if (shaderPackDimensions.contains(Integer.valueOf(maxDrawBuffers)))
                {
                    worldPrefix = "world" + maxDrawBuffers + "/";
                }
            }

            if (saveFinalShaders)
            {
                clearDirectory(new File(shaderpacksdir, "debug"));
            }

            String n;

            for (maxDrawBuffers = 0; maxDrawBuffers < 44; ++maxDrawBuffers)
            {
                String drawBuffersMap = programNames[maxDrawBuffers];

                if (drawBuffersMap.equals(""))
                {
                    programsID[maxDrawBuffers] = programsRef[maxDrawBuffers] = 0;
                    programsDrawBufSettings[maxDrawBuffers] = null;
                    programsColorAtmSettings[maxDrawBuffers] = null;
                    programsCompositeMipmapSetting[maxDrawBuffers] = 0;
                }
                else
                {
                    newDrawBufSetting = null;
                    newColorAtmSetting = null;
                    newCompositeMipmapSetting = 0;
                    String i = worldPrefix + drawBuffersMap;

                    if (activeProfile != null && activeProfile.isProgramDisabled(i))
                    {
                        SMCLog.info("Program disabled: " + i);
                        drawBuffersMap = "<disabled>";
                        i = worldPrefix + drawBuffersMap;
                    }

                    n = "/shaders/" + i;
                    int intbuf = setupProgram(maxDrawBuffers, n + ".vsh", n + ".fsh");

                    if (intbuf > 0)
                    {
                        SMCLog.info("Program loaded: " + i);
                    }

                    programsID[maxDrawBuffers] = programsRef[maxDrawBuffers] = intbuf;
                    programsDrawBufSettings[maxDrawBuffers] = intbuf != 0 ? newDrawBufSetting : null;
                    programsColorAtmSettings[maxDrawBuffers] = intbuf != 0 ? newColorAtmSetting : null;
                    programsCompositeMipmapSetting[maxDrawBuffers] = intbuf != 0 ? newCompositeMipmapSetting : 0;
                }
            }

            maxDrawBuffers = GL11.glGetInteger(GL20.GL_MAX_DRAW_BUFFERS);
            new HashMap();
            int var12;

            for (var12 = 0; var12 < 44; ++var12)
            {
                Arrays.fill(programsToggleColorTextures[var12], false);

                if (var12 == 29)
                {
                    programsDrawBuffers[var12] = null;
                }
                else if (programsID[var12] == 0)
                {
                    if (var12 == 30)
                    {
                        programsDrawBuffers[var12] = drawBuffersNone;
                    }
                    else
                    {
                        programsDrawBuffers[var12] = drawBuffersColorAtt0;
                    }
                }
                else
                {
                    n = programsDrawBufSettings[var12];

                    if (n != null)
                    {
                        IntBuffer var14 = drawBuffersBuffer[var12];
                        int numDB = n.length();

                        if (numDB > usedDrawBuffers)
                        {
                            usedDrawBuffers = numDB;
                        }

                        if (numDB > maxDrawBuffers)
                        {
                            numDB = maxDrawBuffers;
                        }

                        programsDrawBuffers[var12] = var14;
                        var14.limit(numDB);

                        for (int i1 = 0; i1 < numDB; ++i1)
                        {
                            int drawBuffer = 0;

                            if (n.length() > i1)
                            {
                                int ca = n.charAt(i1) - 48;

                                if (var12 != 30)
                                {
                                    if (ca >= 0 && ca <= 7)
                                    {
                                        programsToggleColorTextures[var12][ca] = true;
                                        drawBuffer = ca + 36064;

                                        if (ca > usedColorAttachs)
                                        {
                                            usedColorAttachs = ca;
                                        }

                                        if (ca > usedColorBuffers)
                                        {
                                            usedColorBuffers = ca;
                                        }
                                    }
                                }
                                else if (ca >= 0 && ca <= 1)
                                {
                                    drawBuffer = ca + 36064;

                                    if (ca > usedShadowColorBuffers)
                                    {
                                        usedShadowColorBuffers = ca;
                                    }
                                }
                            }

                            var14.put(i1, drawBuffer);
                        }
                    }
                    else if (var12 != 30 && var12 != 31 && var12 != 32)
                    {
                        programsDrawBuffers[var12] = dfbDrawBuffers;
                        usedDrawBuffers = usedColorBuffers;
                        Arrays.fill(programsToggleColorTextures[var12], 0, usedColorBuffers, true);
                    }
                    else
                    {
                        programsDrawBuffers[var12] = sfbDrawBuffers;
                    }
                }
            }

            hasDeferredPrograms = false;

            for (var12 = 0; var12 < 8; ++var12)
            {
                if (programsID[33 + var12] != 0)
                {
                    hasDeferredPrograms = true;
                    break;
                }
            }

            usedColorAttachs = usedColorBuffers;
            shadowPassInterval = usedShadowDepthBuffers > 0 ? 1 : 0;
            shouldSkipDefaultShadow = usedShadowDepthBuffers > 0;
            SMCLog.info("usedColorBuffers: " + usedColorBuffers);
            SMCLog.info("usedDepthBuffers: " + usedDepthBuffers);
            SMCLog.info("usedShadowColorBuffers: " + usedShadowColorBuffers);
            SMCLog.info("usedShadowDepthBuffers: " + usedShadowDepthBuffers);
            SMCLog.info("usedColorAttachs: " + usedColorAttachs);
            SMCLog.info("usedDrawBuffers: " + usedDrawBuffers);
            dfbDrawBuffers.position(0).limit(usedDrawBuffers);
            dfbColorTextures.position(0).limit(usedColorBuffers * 2);

            for (var12 = 0; var12 < usedDrawBuffers; ++var12)
            {
                dfbDrawBuffers.put(var12, 36064 + var12);
            }

            if (usedDrawBuffers > maxDrawBuffers)
            {
                printChatAndLogError("[Shaders] Error: Not enough draw buffers, needed: " + usedDrawBuffers + ", available: " + maxDrawBuffers);
            }

            sfbDrawBuffers.position(0).limit(usedShadowColorBuffers);

            for (var12 = 0; var12 < usedShadowColorBuffers; ++var12)
            {
                sfbDrawBuffers.put(var12, 36064 + var12);
            }

            for (var12 = 0; var12 < 44; ++var12)
            {
                int var13;

                for (var13 = var12; programsID[var13] == 0 && programBackups[var13] != var13; var13 = programBackups[var13])
                {
                    ;
                }

                if (var13 != var12 && var12 != 30)
                {
                    programsID[var12] = programsID[var13];
                    programsDrawBufSettings[var12] = programsDrawBufSettings[var13];
                    programsDrawBuffers[var12] = programsDrawBuffers[var13];
                }
            }

            resize();
            resizeShadow();

            if (noiseTextureEnabled)
            {
                setupNoiseTexture();
            }

            if (defaultTexture == null)
            {
                defaultTexture = ShadersTex.createDefaultTexture();
            }

            GlStateManager.pushMatrix();
            GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
            preCelestialRotate();
            postCelestialRotate();
            GlStateManager.popMatrix();
            isShaderPackInitialized = true;
            loadEntityDataMap();
            resetDisplayList();

            if (!firstInit)
            {
                ;
            }

            checkGLError("Shaders.init");
        }
    }

    public static void resetDisplayList()
    {
        ++numberResetDisplayList;
        needResetModels = true;
        SMCLog.info("Reset world renderers");
        mc.renderGlobal.loadRenderers();
    }

    public static void resetDisplayListModels()
    {
        if (needResetModels)
        {
            needResetModels = false;
            SMCLog.info("Reset model renderers");
            Iterator it = RenderManager.instance.getEntityRenderMap().values().iterator();

            while (it.hasNext())
            {
                Render ren = (Render)it.next();

                if (ren instanceof RendererLivingEntity)
                {
                    RendererLivingEntity rle = (RendererLivingEntity)ren;
                    resetDisplayListModel(rle.getMainModel());
                }
            }
        }
    }

    public static void resetDisplayListModel(ModelBase model)
    {
        if (model != null)
        {
            Iterator it = model.boxList.iterator();

            while (it.hasNext())
            {
                Object obj = it.next();

                if (obj instanceof ModelRenderer)
                {
                    resetDisplayListModelRenderer((ModelRenderer)obj);
                }
            }
        }
    }

    public static void resetDisplayListModelRenderer(ModelRenderer mrr)
    {
        mrr.resetDisplayList();

        if (mrr.childModels != null)
        {
            int i = 0;

            for (int n = mrr.childModels.size(); i < n; ++i)
            {
                resetDisplayListModelRenderer((ModelRenderer)mrr.childModels.get(i));
            }
        }
    }

    private static int setupProgram(int program, String vShaderPath, String fShaderPath)
    {
        checkGLError("pre setupProgram");
        int programid = ARBShaderObjects.glCreateProgramObjectARB();
        checkGLError("create");

        if (programid != 0)
        {
            progUseEntityAttrib = false;
            progUseMidTexCoordAttrib = false;
            progUseTangentAttrib = false;
            int vShader = createVertShader(vShaderPath);
            int fShader = createFragShader(fShaderPath);
            checkGLError("create");

            if (vShader == 0 && fShader == 0)
            {
                ARBShaderObjects.glDeleteObjectARB(programid);
                programid = 0;
            }
            else
            {
                if (vShader != 0)
                {
                    ARBShaderObjects.glAttachObjectARB(programid, vShader);
                    checkGLError("attach");
                }

                if (fShader != 0)
                {
                    ARBShaderObjects.glAttachObjectARB(programid, fShader);
                    checkGLError("attach");
                }

                if (progUseEntityAttrib)
                {
                    ARBVertexShader.glBindAttribLocationARB(programid, entityAttrib, "mc_Entity");
                    checkGLError("mc_Entity");
                }

                if (progUseMidTexCoordAttrib)
                {
                    ARBVertexShader.glBindAttribLocationARB(programid, midTexCoordAttrib, "mc_midTexCoord");
                    checkGLError("mc_midTexCoord");
                }

                if (progUseTangentAttrib)
                {
                    ARBVertexShader.glBindAttribLocationARB(programid, tangentAttrib, "at_tangent");
                    checkGLError("at_tangent");
                }

                ARBShaderObjects.glLinkProgramARB(programid);

                if (GL20.glGetProgrami(programid, 35714) != 1)
                {
                    SMCLog.severe("Error linking program: " + programid);
                }

                printLogInfo(programid, vShaderPath + ", " + fShaderPath);

                if (vShader != 0)
                {
                    ARBShaderObjects.glDetachObjectARB(programid, vShader);
                    ARBShaderObjects.glDeleteObjectARB(vShader);
                }

                if (fShader != 0)
                {
                    ARBShaderObjects.glDetachObjectARB(programid, fShader);
                    ARBShaderObjects.glDeleteObjectARB(fShader);
                }

                programsID[program] = programid;
                useProgram(program);
                ARBShaderObjects.glValidateProgramARB(programid);
                useProgram(0);
                printLogInfo(programid, vShaderPath + ", " + fShaderPath);
                int valid = GL20.glGetProgrami(programid, 35715);

                if (valid != 1)
                {
                    String Q = "\"";
                    printChatAndLogError("[Shaders] Error: Invalid program " + Q + programNames[program] + Q);
                    ARBShaderObjects.glDeleteObjectARB(programid);
                    programid = 0;
                }
            }
        }

        return programid;
    }

    private static int createVertShader(String filename)
    {
        int vertShader = ARBShaderObjects.glCreateShaderObjectARB(ARBVertexShader.GL_VERTEX_SHADER_ARB);

        if (vertShader == 0)
        {
            return 0;
        }
        else
        {
            StringBuilder vertexCode = new StringBuilder(131072);
            BufferedReader reader = null;

            try
            {
                reader = new BufferedReader(getShaderReader(filename));
            }
            catch (Exception var8)
            {
                ARBShaderObjects.glDeleteObjectARB(vertShader);
                return 0;
            }

            ShaderOption[] activeOptions = getChangedOptions(shaderPackOptions);
            ArrayList listFiles = new ArrayList();

            if (reader != null)
            {
                try
                {
                    reader = ShaderPackParser.resolveIncludes(reader, filename, shaderPack, 0, listFiles, 0);

                    while (true)
                    {
                        String e = reader.readLine();

                        if (e == null)
                        {
                            reader.close();
                            break;
                        }

                        e = applyOptions(e, activeOptions);
                        vertexCode.append(e).append('\n');
                        ShaderLine sl = ShaderParser.parseLine(e);

                        if (sl != null)
                        {
                            if (sl.isAttribute("mc_Entity"))
                            {
                                useEntityAttrib = true;
                                progUseEntityAttrib = true;
                            }
                            else if (sl.isAttribute("mc_midTexCoord"))
                            {
                                useMidTexCoordAttrib = true;
                                progUseMidTexCoordAttrib = true;
                            }
                            else if (e.contains("gl_MultiTexCoord3"))
                            {
                                useMultiTexCoord3Attrib = true;
                            }
                            else if (sl.isAttribute("at_tangent"))
                            {
                                useTangentAttrib = true;
                                progUseTangentAttrib = true;
                            }
                        }
                    }
                }
                catch (Exception var9)
                {
                    SMCLog.severe("Couldn\'t read " + filename + "!");
                    var9.printStackTrace();
                    ARBShaderObjects.glDeleteObjectARB(vertShader);
                    return 0;
                }
            }

            if (saveFinalShaders)
            {
                saveShader(filename, vertexCode.toString());
            }

            ARBShaderObjects.glShaderSourceARB(vertShader, vertexCode);
            ARBShaderObjects.glCompileShaderARB(vertShader);

            if (GL20.glGetShaderi(vertShader, 35713) != 1)
            {
                SMCLog.severe("Error compiling vertex shader: " + filename);
            }

            printShaderLogInfo(vertShader, filename, listFiles);
            return vertShader;
        }
    }

    private static int createFragShader(String filename)
    {
        int fragShader = ARBShaderObjects.glCreateShaderObjectARB(ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);

        if (fragShader == 0)
        {
            return 0;
        }
        else
        {
            StringBuilder fragCode = new StringBuilder(131072);
            BufferedReader reader = null;

            try
            {
                reader = new BufferedReader(getShaderReader(filename));
            }
            catch (Exception var12)
            {
                ARBShaderObjects.glDeleteObjectARB(fragShader);
                return 0;
            }

            ShaderOption[] activeOptions = getChangedOptions(shaderPackOptions);
            ArrayList listFiles = new ArrayList();

            if (reader != null)
            {
                try
                {
                    reader = ShaderPackParser.resolveIncludes(reader, filename, shaderPack, 0, listFiles, 0);

                    while (true)
                    {
                        String e = reader.readLine();

                        if (e == null)
                        {
                            reader.close();
                            break;
                        }

                        e = applyOptions(e, activeOptions);
                        fragCode.append(e).append('\n');
                        ShaderLine sl = ShaderParser.parseLine(e);

                        if (sl != null)
                        {
                            String val;
                            int bufferindex;

                            if (sl.isUniform())
                            {
                                val = sl.getName();

                                if ((bufferindex = ShaderParser.getShadowDepthIndex(val)) >= 0)
                                {
                                    usedShadowDepthBuffers = Math.max(usedShadowDepthBuffers, bufferindex + 1);
                                }
                                else if ((bufferindex = ShaderParser.getShadowColorIndex(val)) >= 0)
                                {
                                    usedShadowColorBuffers = Math.max(usedShadowColorBuffers, bufferindex + 1);
                                }
                                else if ((bufferindex = ShaderParser.getDepthIndex(val)) >= 0)
                                {
                                    usedDepthBuffers = Math.max(usedDepthBuffers, bufferindex + 1);
                                }
                                else if (val.equals("gdepth") && gbuffersFormat[1] == 6408)
                                {
                                    gbuffersFormat[1] = 34836;
                                }
                                else if ((bufferindex = ShaderParser.getColorIndex(val)) >= 0)
                                {
                                    usedColorBuffers = Math.max(usedColorBuffers, bufferindex + 1);
                                }
                                else if (val.equals("centerDepthSmooth"))
                                {
                                    centerDepthSmoothEnabled = true;
                                }
                            }
                            else if (!sl.isConstInt("shadowMapResolution") && !sl.isProperty("SHADOWRES"))
                            {
                                if (!sl.isConstFloat("shadowMapFov") && !sl.isProperty("SHADOWFOV"))
                                {
                                    if (!sl.isConstFloat("shadowDistance") && !sl.isProperty("SHADOWHPL"))
                                    {
                                        if (sl.isConstFloat("shadowDistanceRenderMul"))
                                        {
                                            shadowDistanceRenderMul = sl.getValueFloat();
                                            SMCLog.info("Shadow distance render mul: " + shadowDistanceRenderMul);
                                        }
                                        else if (sl.isConstFloat("shadowIntervalSize"))
                                        {
                                            shadowIntervalSize = sl.getValueFloat();
                                            SMCLog.info("Shadow map interval size: " + shadowIntervalSize);
                                        }
                                        else if (sl.isConstBool("generateShadowMipmap", true))
                                        {
                                            Arrays.fill(shadowMipmapEnabled, true);
                                            SMCLog.info("Generate shadow mipmap");
                                        }
                                        else if (sl.isConstBool("generateShadowColorMipmap", true))
                                        {
                                            Arrays.fill(shadowColorMipmapEnabled, true);
                                            SMCLog.info("Generate shadow color mipmap");
                                        }
                                        else if (sl.isConstBool("shadowHardwareFiltering", true))
                                        {
                                            Arrays.fill(shadowHardwareFilteringEnabled, true);
                                            SMCLog.info("Hardware shadow filtering enabled.");
                                        }
                                        else if (sl.isConstBool("shadowHardwareFiltering0", true))
                                        {
                                            shadowHardwareFilteringEnabled[0] = true;
                                            SMCLog.info("shadowHardwareFiltering0");
                                        }
                                        else if (sl.isConstBool("shadowHardwareFiltering1", true))
                                        {
                                            shadowHardwareFilteringEnabled[1] = true;
                                            SMCLog.info("shadowHardwareFiltering1");
                                        }
                                        else if (sl.isConstBool("shadowtex0Mipmap", "shadowtexMipmap", true))
                                        {
                                            shadowMipmapEnabled[0] = true;
                                            SMCLog.info("shadowtex0Mipmap");
                                        }
                                        else if (sl.isConstBool("shadowtex1Mipmap", true))
                                        {
                                            shadowMipmapEnabled[1] = true;
                                            SMCLog.info("shadowtex1Mipmap");
                                        }
                                        else if (sl.isConstBool("shadowcolor0Mipmap", "shadowColor0Mipmap", true))
                                        {
                                            shadowColorMipmapEnabled[0] = true;
                                            SMCLog.info("shadowcolor0Mipmap");
                                        }
                                        else if (sl.isConstBool("shadowcolor1Mipmap", "shadowColor1Mipmap", true))
                                        {
                                            shadowColorMipmapEnabled[1] = true;
                                            SMCLog.info("shadowcolor1Mipmap");
                                        }
                                        else if (sl.isConstBool("shadowtex0Nearest", "shadowtexNearest", "shadow0MinMagNearest", true))
                                        {
                                            shadowFilterNearest[0] = true;
                                            SMCLog.info("shadowtex0Nearest");
                                        }
                                        else if (sl.isConstBool("shadowtex1Nearest", "shadow1MinMagNearest", true))
                                        {
                                            shadowFilterNearest[1] = true;
                                            SMCLog.info("shadowtex1Nearest");
                                        }
                                        else if (sl.isConstBool("shadowcolor0Nearest", "shadowColor0Nearest", "shadowColor0MinMagNearest", true))
                                        {
                                            shadowColorFilterNearest[0] = true;
                                            SMCLog.info("shadowcolor0Nearest");
                                        }
                                        else if (sl.isConstBool("shadowcolor1Nearest", "shadowColor1Nearest", "shadowColor1MinMagNearest", true))
                                        {
                                            shadowColorFilterNearest[1] = true;
                                            SMCLog.info("shadowcolor1Nearest");
                                        }
                                        else if (!sl.isConstFloat("wetnessHalflife") && !sl.isProperty("WETNESSHL"))
                                        {
                                            if (!sl.isConstFloat("drynessHalflife") && !sl.isProperty("DRYNESSHL"))
                                            {
                                                if (sl.isConstFloat("eyeBrightnessHalflife"))
                                                {
                                                    eyeBrightnessHalflife = sl.getValueFloat();
                                                    SMCLog.info("Eye brightness halflife: " + eyeBrightnessHalflife);
                                                }
                                                else if (sl.isConstFloat("centerDepthHalflife"))
                                                {
                                                    centerDepthSmoothHalflife = sl.getValueFloat();
                                                    SMCLog.info("Center depth halflife: " + centerDepthSmoothHalflife);
                                                }
                                                else if (sl.isConstFloat("sunPathRotation"))
                                                {
                                                    sunPathRotation = sl.getValueFloat();
                                                    SMCLog.info("Sun path rotation: " + sunPathRotation);
                                                }
                                                else if (sl.isConstFloat("ambientOcclusionLevel"))
                                                {
                                                    aoLevel = Config.limit(sl.getValueFloat(), 0.0F, 1.0F);
                                                    SMCLog.info("AO Level: " + aoLevel);
                                                }
                                                else if (sl.isConstInt("superSamplingLevel"))
                                                {
                                                    int val1 = sl.getValueInt();

                                                    if (val1 > 1)
                                                    {
                                                        SMCLog.info("Super sampling level: " + val1 + "x");
                                                        superSamplingLevel = val1;
                                                    }
                                                    else
                                                    {
                                                        superSamplingLevel = 1;
                                                    }
                                                }
                                                else if (sl.isConstInt("noiseTextureResolution"))
                                                {
                                                    noiseTextureResolution = sl.getValueInt();
                                                    noiseTextureEnabled = true;
                                                    SMCLog.info("Noise texture enabled");
                                                    SMCLog.info("Noise texture resolution: " + noiseTextureResolution);
                                                }
                                                else if (sl.isConstIntSuffix("Format"))
                                                {
                                                    val = StrUtils.removeSuffix(sl.getName(), "Format");
                                                    String bufferindex2 = sl.getValue();
                                                    int bufferindex1 = getBufferIndexFromString(val);
                                                    int format = getTextureFormatFromString(bufferindex2);

                                                    if (bufferindex1 >= 0 && format != 0)
                                                    {
                                                        gbuffersFormat[bufferindex1] = format;
                                                        SMCLog.info("%s format: %s", new Object[] {val, bufferindex2});
                                                    }
                                                }
                                                else if (sl.isConstBoolSuffix("Clear", false))
                                                {
                                                    if (ShaderParser.isComposite(filename) || ShaderParser.isDeferred(filename))
                                                    {
                                                        val = StrUtils.removeSuffix(sl.getName(), "Clear");
                                                        bufferindex = getBufferIndexFromString(val);

                                                        if (bufferindex >= 0)
                                                        {
                                                            gbuffersClear[bufferindex] = false;
                                                            SMCLog.info("%s clear disabled", new Object[] {val});
                                                        }
                                                    }
                                                }
                                                else if (sl.isProperty("GAUX4FORMAT", "RGBA32F"))
                                                {
                                                    gbuffersFormat[7] = 34836;
                                                    SMCLog.info("gaux4 format : RGB32AF");
                                                }
                                                else if (sl.isProperty("GAUX4FORMAT", "RGB32F"))
                                                {
                                                    gbuffersFormat[7] = 34837;
                                                    SMCLog.info("gaux4 format : RGB32F");
                                                }
                                                else if (sl.isProperty("GAUX4FORMAT", "RGB16"))
                                                {
                                                    gbuffersFormat[7] = 32852;
                                                    SMCLog.info("gaux4 format : RGB16");
                                                }
                                                else if (sl.isConstBoolSuffix("MipmapEnabled", true))
                                                {
                                                    if (ShaderParser.isComposite(filename) || ShaderParser.isDeferred(filename) || ShaderParser.isFinal(filename))
                                                    {
                                                        val = StrUtils.removeSuffix(sl.getName(), "MipmapEnabled");
                                                        bufferindex = getBufferIndexFromString(val);

                                                        if (bufferindex >= 0)
                                                        {
                                                            newCompositeMipmapSetting |= 1 << bufferindex;
                                                            SMCLog.info("%s mipmap enabled", new Object[] {val});
                                                        }
                                                    }
                                                }
                                                else if (sl.isProperty("DRAWBUFFERS"))
                                                {
                                                    val = sl.getValue();

                                                    if (ShaderParser.isValidDrawBuffers(val))
                                                    {
                                                        newDrawBufSetting = val;
                                                    }
                                                    else
                                                    {
                                                        SMCLog.warning("Invalid draw buffers: " + val);
                                                    }
                                                }
                                            }
                                            else
                                            {
                                                drynessHalfLife = sl.getValueFloat();
                                                SMCLog.info("Dryness halflife: " + drynessHalfLife);
                                            }
                                        }
                                        else
                                        {
                                            wetnessHalfLife = sl.getValueFloat();
                                            SMCLog.info("Wetness halflife: " + wetnessHalfLife);
                                        }
                                    }
                                    else
                                    {
                                        shadowMapHalfPlane = sl.getValueFloat();
                                        shadowMapIsOrtho = true;
                                        SMCLog.info("Shadow map distance: " + shadowMapHalfPlane);
                                    }
                                }
                                else
                                {
                                    shadowMapFOV = sl.getValueFloat();
                                    shadowMapIsOrtho = false;
                                    SMCLog.info("Shadow map field of view: " + shadowMapFOV);
                                }
                            }
                            else
                            {
                                spShadowMapWidth = spShadowMapHeight = sl.getValueInt();
                                shadowMapWidth = shadowMapHeight = Math.round((float)spShadowMapWidth * configShadowResMul);
                                SMCLog.info("Shadow map resolution: " + spShadowMapWidth);
                            }
                        }
                    }
                }
                catch (Exception var13)
                {
                    SMCLog.severe("Couldn\'t read " + filename + "!");
                    var13.printStackTrace();
                    ARBShaderObjects.glDeleteObjectARB(fragShader);
                    return 0;
                }
            }

            if (saveFinalShaders)
            {
                saveShader(filename, fragCode.toString());
            }

            ARBShaderObjects.glShaderSourceARB(fragShader, fragCode);
            ARBShaderObjects.glCompileShaderARB(fragShader);

            if (GL20.glGetShaderi(fragShader, 35713) != 1)
            {
                SMCLog.severe("Error compiling fragment shader: " + filename);
            }

            printShaderLogInfo(fragShader, filename, listFiles);
            return fragShader;
        }
    }

    private static Reader getShaderReader(String filename)
    {
        Reader r = ShadersBuiltIn.getShaderReader(filename);
        return (Reader)(r != null ? r : new InputStreamReader(shaderPack.getResourceAsStream(filename)));
    }

    private static void saveShader(String filename, String code)
    {
        try
        {
            File e = new File(shaderpacksdir, "debug/" + filename);
            e.getParentFile().mkdirs();
            Config.writeFile(e, code);
        }
        catch (IOException var3)
        {
            Config.warn("Error saving: " + filename);
            var3.printStackTrace();
        }
    }

    private static void clearDirectory(File dir)
    {
        if (dir.exists())
        {
            if (dir.isDirectory())
            {
                File[] files = dir.listFiles();

                if (files != null)
                {
                    for (int i = 0; i < files.length; ++i)
                    {
                        File file = files[i];

                        if (file.isDirectory())
                        {
                            clearDirectory(file);
                        }

                        file.delete();
                    }
                }
            }
        }
    }

    private static boolean printLogInfo(int obj, String name)
    {
        IntBuffer iVal = BufferUtils.createIntBuffer(1);
        ARBShaderObjects.glGetObjectParameterARB(obj, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB, iVal);
        int length = iVal.get();

        if (length > 1)
        {
            ByteBuffer infoLog = BufferUtils.createByteBuffer(length);
            iVal.flip();
            ARBShaderObjects.glGetInfoLogARB(obj, iVal, infoLog);
            byte[] infoBytes = new byte[length];
            infoLog.get(infoBytes);

            if (infoBytes[length - 1] == 0)
            {
                infoBytes[length - 1] = 10;
            }

            String out = new String(infoBytes);
            SMCLog.info("Info log: " + name + "\n" + out);
            return false;
        }
        else
        {
            return true;
        }
    }

    private static boolean printShaderLogInfo(int shader, String name, List<String> listFiles)
    {
        IntBuffer iVal = BufferUtils.createIntBuffer(1);
        int length = GL20.glGetShaderi(shader, 35716);

        if (length <= 1)
        {
            return true;
        }
        else
        {
            for (int log = 0; log < listFiles.size(); ++log)
            {
                String path = (String)listFiles.get(log);
                SMCLog.info("File: " + (log + 1) + " = " + path);
            }

            String var7 = GL20.glGetShaderInfoLog(shader, length);
            SMCLog.info("Shader info log: " + name + "\n" + var7);
            return false;
        }
    }

    public static void setDrawBuffers(IntBuffer drawBuffers)
    {
        if (drawBuffers == null)
        {
            drawBuffers = drawBuffersNone;
        }

        if (activeDrawBuffers != drawBuffers)
        {
            activeDrawBuffers = drawBuffers;
            GL20.glDrawBuffers(drawBuffers);
        }
    }

    public static void useProgram(int program)
    {
        checkGLError("pre-useProgram");

        if (isShadowPass)
        {
            program = 30;

            if (programsID[30] == 0)
            {
                normalMapEnabled = false;
                return;
            }
        }

        if (activeProgram != program)
        {
            activeProgram = program;
            ARBShaderObjects.glUseProgramObjectARB(programsID[program]);

            if (programsID[program] == 0)
            {
                normalMapEnabled = false;
            }
            else
            {
                if (checkGLError("useProgram ", programNames[program]) != 0)
                {
                    programsID[program] = 0;
                }

                IntBuffer drawBuffers = programsDrawBuffers[program];

                if (isRenderingDfb)
                {
                    setDrawBuffers(drawBuffers);
                    checkGLError(programNames[program], " draw buffers = ", programsDrawBufSettings[program]);
                }

                activeCompositeMipmapSetting = programsCompositeMipmapSetting[program];
                uniformEntityColor.setProgram(programsID[activeProgram]);
                uniformEntityId.setProgram(programsID[activeProgram]);
                uniformBlockEntityId.setProgram(programsID[activeProgram]);

                switch (program)
                {
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                    case 10:
                    case 11:
                    case 12:
                    case 13:
                    case 16:
                    case 18:
                    case 19:
                    case 20:
                    case 41:
                        normalMapEnabled = true;
                        setProgramUniform1i("texture", 0);
                        setProgramUniform1i("lightmap", 1);
                        setProgramUniform1i("normals", 2);
                        setProgramUniform1i("specular", 3);
                        setProgramUniform1i("shadow", waterShadowEnabled ? 5 : 4);
                        setProgramUniform1i("watershadow", 4);
                        setProgramUniform1i("shadowtex0", 4);
                        setProgramUniform1i("shadowtex1", 5);
                        setProgramUniform1i("depthtex0", 6);

                        if (customTexturesGbuffers != null || hasDeferredPrograms)
                        {
                            setProgramUniform1i("gaux1", 7);
                            setProgramUniform1i("gaux2", 8);
                            setProgramUniform1i("gaux3", 9);
                            setProgramUniform1i("gaux4", 10);
                        }

                        setProgramUniform1i("depthtex1", 12);
                        setProgramUniform1i("shadowcolor", 13);
                        setProgramUniform1i("shadowcolor0", 13);
                        setProgramUniform1i("shadowcolor1", 14);
                        setProgramUniform1i("noisetex", 15);
                        break;

                    case 14:
                    case 15:
                    case 17:
                    default:
                        normalMapEnabled = false;
                        break;

                    case 21:
                    case 22:
                    case 23:
                    case 24:
                    case 25:
                    case 26:
                    case 27:
                    case 28:
                    case 29:
                    case 33:
                    case 34:
                    case 35:
                    case 36:
                    case 37:
                    case 38:
                    case 39:
                    case 40:
                    case 42:
                    case 43:
                        normalMapEnabled = false;
                        setProgramUniform1i("gcolor", 0);
                        setProgramUniform1i("gdepth", 1);
                        setProgramUniform1i("gnormal", 2);
                        setProgramUniform1i("composite", 3);
                        setProgramUniform1i("gaux1", 7);
                        setProgramUniform1i("gaux2", 8);
                        setProgramUniform1i("gaux3", 9);
                        setProgramUniform1i("gaux4", 10);
                        setProgramUniform1i("colortex0", 0);
                        setProgramUniform1i("colortex1", 1);
                        setProgramUniform1i("colortex2", 2);
                        setProgramUniform1i("colortex3", 3);
                        setProgramUniform1i("colortex4", 7);
                        setProgramUniform1i("colortex5", 8);
                        setProgramUniform1i("colortex6", 9);
                        setProgramUniform1i("colortex7", 10);
                        setProgramUniform1i("shadow", waterShadowEnabled ? 5 : 4);
                        setProgramUniform1i("watershadow", 4);
                        setProgramUniform1i("shadowtex0", 4);
                        setProgramUniform1i("shadowtex1", 5);
                        setProgramUniform1i("gdepthtex", 6);
                        setProgramUniform1i("depthtex0", 6);
                        setProgramUniform1i("depthtex1", 11);
                        setProgramUniform1i("depthtex2", 12);
                        setProgramUniform1i("shadowcolor", 13);
                        setProgramUniform1i("shadowcolor0", 13);
                        setProgramUniform1i("shadowcolor1", 14);
                        setProgramUniform1i("noisetex", 15);
                        break;

                    case 30:
                    case 31:
                    case 32:
                        setProgramUniform1i("tex", 0);
                        setProgramUniform1i("texture", 0);
                        setProgramUniform1i("lightmap", 1);
                        setProgramUniform1i("normals", 2);
                        setProgramUniform1i("specular", 3);
                        setProgramUniform1i("shadow", waterShadowEnabled ? 5 : 4);
                        setProgramUniform1i("watershadow", 4);
                        setProgramUniform1i("shadowtex0", 4);
                        setProgramUniform1i("shadowtex1", 5);

                        if (customTexturesGbuffers != null)
                        {
                            setProgramUniform1i("gaux1", 7);
                            setProgramUniform1i("gaux2", 8);
                            setProgramUniform1i("gaux3", 9);
                            setProgramUniform1i("gaux4", 10);
                        }

                        setProgramUniform1i("shadowcolor", 13);
                        setProgramUniform1i("shadowcolor0", 13);
                        setProgramUniform1i("shadowcolor1", 14);
                        setProgramUniform1i("noisetex", 15);
                }

                ItemStack stack = mc.thePlayer != null ? mc.thePlayer.getHeldItem() : null;
                Item item = stack != null ? stack.getItem() : null;
                int itemID = -1;
                Block block = null;

                if (item != null)
                {
                    itemID = Item.itemRegistry.getIDForObject(item);
                    block = (Block)Block.blockRegistry.getObjectForID(itemID);
                }

                int blockLight = block != null ? block.getLightValue() : 0;
                setProgramUniform1i("heldItemId", itemID);
                setProgramUniform1i("heldBlockLightValue", blockLight);
                setProgramUniform1i("fogMode", fogEnabled ? fogMode : 0);
                setProgramUniform3f("fogColor", fogColorR, fogColorG, fogColorB);
                setProgramUniform3f("skyColor", skyColorR, skyColorG, skyColorB);
                setProgramUniform1i("worldTime", (int)(worldTime % 24000L));
                setProgramUniform1i("worldDay", (int)(worldTime / 24000L));
                setProgramUniform1i("moonPhase", moonPhase);
                setProgramUniform1i("frameCounter", frameCounter);
                setProgramUniform1f("frameTime", frameTime);
                setProgramUniform1f("frameTimeCounter", frameTimeCounter);
                setProgramUniform1f("sunAngle", sunAngle);
                setProgramUniform1f("shadowAngle", shadowAngle);
                setProgramUniform1f("rainStrength", rainStrength);
                setProgramUniform1f("aspectRatio", (float)renderWidth / (float)renderHeight);
                setProgramUniform1f("viewWidth", (float)renderWidth);
                setProgramUniform1f("viewHeight", (float)renderHeight);
                setProgramUniform1f("near", 0.05F);
                setProgramUniform1f("far", (float)(mc.gameSettings.renderDistanceChunks * 16));
                setProgramUniform3f("sunPosition", sunPosition[0], sunPosition[1], sunPosition[2]);
                setProgramUniform3f("moonPosition", moonPosition[0], moonPosition[1], moonPosition[2]);
                setProgramUniform3f("shadowLightPosition", shadowLightPosition[0], shadowLightPosition[1], shadowLightPosition[2]);
                setProgramUniform3f("upPosition", upPosition[0], upPosition[1], upPosition[2]);
                setProgramUniform3f("previousCameraPosition", (float)previousCameraPositionX, (float)previousCameraPositionY, (float)previousCameraPositionZ);
                setProgramUniform3f("cameraPosition", (float)cameraPositionX, (float)cameraPositionY, (float)cameraPositionZ);
                setProgramUniformMatrix4ARB("gbufferModelView", false, modelView);
                setProgramUniformMatrix4ARB("gbufferModelViewInverse", false, modelViewInverse);
                setProgramUniformMatrix4ARB("gbufferPreviousProjection", false, previousProjection);
                setProgramUniformMatrix4ARB("gbufferProjection", false, projection);
                setProgramUniformMatrix4ARB("gbufferProjectionInverse", false, projectionInverse);
                setProgramUniformMatrix4ARB("gbufferPreviousModelView", false, previousModelView);

                if (usedShadowDepthBuffers > 0)
                {
                    setProgramUniformMatrix4ARB("shadowProjection", false, shadowProjection);
                    setProgramUniformMatrix4ARB("shadowProjectionInverse", false, shadowProjectionInverse);
                    setProgramUniformMatrix4ARB("shadowModelView", false, shadowModelView);
                    setProgramUniformMatrix4ARB("shadowModelViewInverse", false, shadowModelViewInverse);
                }

                setProgramUniform1f("wetness", wetness);
                setProgramUniform1f("eyeAltitude", eyePosY);
                setProgramUniform2i("eyeBrightness", eyeBrightness & 65535, eyeBrightness >> 16);
                setProgramUniform2i("eyeBrightnessSmooth", Math.round(eyeBrightnessFadeX), Math.round(eyeBrightnessFadeY));
                setProgramUniform2i("terrainTextureSize", terrainTextureSize[0], terrainTextureSize[1]);
                setProgramUniform1i("terrainIconSize", terrainIconSize);
                setProgramUniform1i("isEyeInWater", isEyeInWater);
                setProgramUniform1f("nightVision", nightVision);
                setProgramUniform1f("blindness", blindness);
                setProgramUniform1f("screenBrightness", mc.gameSettings.gammaSetting);
                setProgramUniform1i("hideGUI", mc.gameSettings.hideGUI ? 1 : 0);
                setProgramUniform1f("centerDepthSmooth", centerDepthSmooth);
                setProgramUniform2i("atlasSize", atlasSizeX, atlasSizeY);

                if (customUniforms != null)
                {
                    customUniforms.setProgram(programsID[activeProgram]);
                    customUniforms.update();
                }

                checkGLError("useProgram ", programNames[program]);
            }
        }
    }

    public static void setProgramUniform1i(String name, int x)
    {
        int gp = programsID[activeProgram];

        if (gp != 0)
        {
            int uniform = ARBShaderObjects.glGetUniformLocationARB(gp, name);
            ARBShaderObjects.glUniform1iARB(uniform, x);

            if (isCustomUniforms())
            {
                LegacyUniforms.setInt(name, x);
            }

            checkGLError(programNames[activeProgram], name);
        }
    }

    public static void setProgramUniform2i(String name, int x, int y)
    {
        int gp = programsID[activeProgram];

        if (gp != 0)
        {
            int uniform = ARBShaderObjects.glGetUniformLocationARB(gp, name);
            ARBShaderObjects.glUniform2iARB(uniform, x, y);

            if (isCustomUniforms())
            {
                LegacyUniforms.setIntXy(name, x, y);
            }

            checkGLError(programNames[activeProgram], name);
        }
    }

    public static void setProgramUniform1f(String name, float x)
    {
        int gp = programsID[activeProgram];

        if (gp != 0)
        {
            int uniform = ARBShaderObjects.glGetUniformLocationARB(gp, name);
            ARBShaderObjects.glUniform1fARB(uniform, x);

            if (isCustomUniforms())
            {
                LegacyUniforms.setFloat(name, x);
            }

            checkGLError(programNames[activeProgram], name);
        }
    }

    public static void setProgramUniform3f(String name, float x, float y, float z)
    {
        int gp = programsID[activeProgram];

        if (gp != 0)
        {
            int uniform = ARBShaderObjects.glGetUniformLocationARB(gp, name);
            ARBShaderObjects.glUniform3fARB(uniform, x, y, z);

            if (isCustomUniforms())
            {
                if (name.endsWith("Color"))
                {
                    LegacyUniforms.setFloatRgb(name, x, y, z);
                }
                else
                {
                    LegacyUniforms.setFloatXyz(name, x, y, z);
                }
            }

            checkGLError(programNames[activeProgram], name);
        }
    }

    public static void setProgramUniformMatrix4ARB(String name, boolean transpose, FloatBuffer matrix)
    {
        int gp = programsID[activeProgram];

        if (gp != 0 && matrix != null)
        {
            int uniform = ARBShaderObjects.glGetUniformLocationARB(gp, name);
            ARBShaderObjects.glUniformMatrix4ARB(uniform, transpose, matrix);
            checkGLError(programNames[activeProgram], name);
        }
    }

    private static int getBufferIndexFromString(String name)
    {
        return !name.equals("colortex0") && !name.equals("gcolor") ? (!name.equals("colortex1") && !name.equals("gdepth") ? (!name.equals("colortex2") && !name.equals("gnormal") ? (!name.equals("colortex3") && !name.equals("composite") ? (!name.equals("colortex4") && !name.equals("gaux1") ? (!name.equals("colortex5") && !name.equals("gaux2") ? (!name.equals("colortex6") && !name.equals("gaux3") ? (!name.equals("colortex7") && !name.equals("gaux4") ? -1 : 7) : 6) : 5) : 4) : 3) : 2) : 1) : 0;
    }

    private static int getTextureFormatFromString(String par)
    {
        par = par.trim();

        for (int i = 0; i < formatNames.length; ++i)
        {
            String name = formatNames[i];

            if (par.equals(name))
            {
                return formatIds[i];
            }
        }

        return 0;
    }

    private static void setupNoiseTexture()
    {
        if (noiseTexture == null && noiseTexturePath != null)
        {
            noiseTexture = loadCustomTexture(15, noiseTexturePath);
        }

        if (noiseTexture == null)
        {
            noiseTexture = new HFNoiseTexture(noiseTextureResolution, noiseTextureResolution);
        }
    }

    private static void loadEntityDataMap()
    {
        mapBlockToEntityData = new IdentityHashMap(300);
        String e;

        if (mapBlockToEntityData.isEmpty())
        {
            Iterator reader = Block.blockRegistry.getKeys().iterator();

            while (reader.hasNext())
            {
                e = (String)reader.next();
                Block m = (Block)Block.blockRegistry.getObject(e);
                int name = Block.blockRegistry.getIDForObject(m);
                mapBlockToEntityData.put(m, Integer.valueOf(name));
            }
        }

        BufferedReader reader1 = null;

        try
        {
            reader1 = new BufferedReader(new InputStreamReader(shaderPack.getResourceAsStream("/mc_Entity_x.txt")));
        }
        catch (Exception var8)
        {
            ;
        }

        if (reader1 != null)
        {
            try
            {
                while ((e = reader1.readLine()) != null)
                {
                    Matcher m1 = patternLoadEntityDataMap.matcher(e);

                    if (m1.matches())
                    {
                        String name1 = m1.group(1);
                        String value = m1.group(2);
                        int id = Integer.parseInt(value);
                        Block block = Block.getBlockFromName(name1);

                        if (block != null)
                        {
                            mapBlockToEntityData.put(block, Integer.valueOf(id));
                        }
                        else
                        {
                            SMCLog.warning("Unknown block name %s", new Object[] {name1});
                        }
                    }
                    else
                    {
                        SMCLog.warning("unmatched %s\n", new Object[] {e});
                    }
                }
            }
            catch (Exception var9)
            {
                SMCLog.warning("Error parsing mc_Entity_x.txt");
            }
        }

        if (reader1 != null)
        {
            try
            {
                reader1.close();
            }
            catch (Exception var7)
            {
                ;
            }
        }
    }

    private static IntBuffer fillIntBufferZero(IntBuffer buf)
    {
        int limit = buf.limit();

        for (int i = buf.position(); i < limit; ++i)
        {
            buf.put(i, 0);
        }

        return buf;
    }

    public static void uninit()
    {
        if (isShaderPackInitialized)
        {
            checkGLError("Shaders.uninit pre");

            for (int i = 0; i < 44; ++i)
            {
                if (programsRef[i] != 0)
                {
                    ARBShaderObjects.glDeleteObjectARB(programsRef[i]);
                    checkGLError("del programRef");
                }

                programsRef[i] = 0;
                programsID[i] = 0;
                programsDrawBufSettings[i] = null;
                programsDrawBuffers[i] = null;
                programsCompositeMipmapSetting[i] = 0;
            }

            hasDeferredPrograms = false;

            if (dfb != 0)
            {
                EXTFramebufferObject.glDeleteFramebuffersEXT(dfb);
                dfb = 0;
                checkGLError("del dfb");
            }

            if (sfb != 0)
            {
                EXTFramebufferObject.glDeleteFramebuffersEXT(sfb);
                sfb = 0;
                checkGLError("del sfb");
            }

            if (dfbDepthTextures != null)
            {
                GlStateManager.deleteTextures(dfbDepthTextures);
                fillIntBufferZero(dfbDepthTextures);
                checkGLError("del dfbDepthTextures");
            }

            if (dfbColorTextures != null)
            {
                GlStateManager.deleteTextures(dfbColorTextures);
                fillIntBufferZero(dfbColorTextures);
                checkGLError("del dfbTextures");
            }

            if (sfbDepthTextures != null)
            {
                GlStateManager.deleteTextures(sfbDepthTextures);
                fillIntBufferZero(sfbDepthTextures);
                checkGLError("del shadow depth");
            }

            if (sfbColorTextures != null)
            {
                GlStateManager.deleteTextures(sfbColorTextures);
                fillIntBufferZero(sfbColorTextures);
                checkGLError("del shadow color");
            }

            if (dfbDrawBuffers != null)
            {
                fillIntBufferZero(dfbDrawBuffers);
            }

            if (noiseTexture != null)
            {
                noiseTexture.deleteTexture();
                noiseTexture = null;
            }

            SMCLog.info("Uninit");
            shadowPassInterval = 0;
            shouldSkipDefaultShadow = false;
            isShaderPackInitialized = false;
            checkGLError("Shaders.uninit");
        }
    }

    public static void scheduleResize()
    {
        renderDisplayHeight = 0;
    }

    public static void scheduleResizeShadow()
    {
        needResizeShadow = true;
    }

    private static void resize()
    {
        renderDisplayWidth = mc.displayWidth;
        renderDisplayHeight = mc.displayHeight;
        renderWidth = Math.round((float)renderDisplayWidth * configRenderResMul);
        renderHeight = Math.round((float)renderDisplayHeight * configRenderResMul);
        setupFrameBuffer();
    }

    private static void resizeShadow()
    {
        needResizeShadow = false;
        shadowMapWidth = Math.round((float)spShadowMapWidth * configShadowResMul);
        shadowMapHeight = Math.round((float)spShadowMapHeight * configShadowResMul);
        setupShadowFrameBuffer();
    }

    private static void setupFrameBuffer()
    {
        if (dfb != 0)
        {
            EXTFramebufferObject.glDeleteFramebuffersEXT(dfb);
            GlStateManager.deleteTextures(dfbDepthTextures);
            GlStateManager.deleteTextures(dfbColorTextures);
        }

        dfb = EXTFramebufferObject.glGenFramebuffersEXT();
        GL11.glGenTextures((IntBuffer)dfbDepthTextures.clear().limit(usedDepthBuffers));
        GL11.glGenTextures((IntBuffer)dfbColorTextures.clear().limit(16));
        dfbDepthTextures.position(0);
        dfbColorTextures.position(0);
        dfbColorTextures.get(dfbColorTexturesA).position(0);
        EXTFramebufferObject.glBindFramebufferEXT(36160, dfb);
        GL20.glDrawBuffers(0);
        GL11.glReadBuffer(0);
        int status;

        for (status = 0; status < usedDepthBuffers; ++status)
        {
            GlStateManager.bindTexture(dfbDepthTextures.get(status));
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_DEPTH_TEXTURE_MODE, GL11.GL_LUMINANCE);
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_DEPTH_COMPONENT, renderWidth, renderHeight, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (FloatBuffer)null);
        }

        EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36096, 3553, dfbDepthTextures.get(0), 0);
        GL20.glDrawBuffers(dfbDrawBuffers);
        GL11.glReadBuffer(0);
        checkGLError("FT d");

        for (status = 0; status < usedColorBuffers; ++status)
        {
            GlStateManager.bindTexture(dfbColorTexturesA[status]);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, gbuffersFormat[status], renderWidth, renderHeight, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, (ByteBuffer)null);
            EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + status, 3553, dfbColorTexturesA[status], 0);
            checkGLError("FT c");
        }

        for (status = 0; status < usedColorBuffers; ++status)
        {
            GlStateManager.bindTexture(dfbColorTexturesA[8 + status]);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, gbuffersFormat[status], renderWidth, renderHeight, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, (ByteBuffer)null);
            checkGLError("FT ca");
        }

        status = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);

        if (status == 36058)
        {
            printChatAndLogError("[Shaders] Error: Failed framebuffer incomplete formats");

            for (int i = 0; i < usedColorBuffers; ++i)
            {
                GlStateManager.bindTexture(dfbColorTextures.get(i));
                GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, renderWidth, renderHeight, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, (ByteBuffer)null);
                EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + i, 3553, dfbColorTextures.get(i), 0);
                checkGLError("FT c");
            }

            status = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);

            if (status == 36053)
            {
                SMCLog.info("complete");
            }
        }

        GlStateManager.bindTexture(0);

        if (status != 36053)
        {
            printChatAndLogError("[Shaders] Error: Failed creating framebuffer! (Status " + status + ")");
        }
        else
        {
            SMCLog.info("Framebuffer created.");
        }
    }

    private static void setupShadowFrameBuffer()
    {
        if (usedShadowDepthBuffers != 0)
        {
            if (sfb != 0)
            {
                EXTFramebufferObject.glDeleteFramebuffersEXT(sfb);
                GlStateManager.deleteTextures(sfbDepthTextures);
                GlStateManager.deleteTextures(sfbColorTextures);
            }

            sfb = EXTFramebufferObject.glGenFramebuffersEXT();
            EXTFramebufferObject.glBindFramebufferEXT(36160, sfb);
            GL11.glDrawBuffer(0);
            GL11.glReadBuffer(0);
            GL11.glGenTextures((IntBuffer)sfbDepthTextures.clear().limit(usedShadowDepthBuffers));
            GL11.glGenTextures((IntBuffer)sfbColorTextures.clear().limit(usedShadowColorBuffers));
            sfbDepthTextures.position(0);
            sfbColorTextures.position(0);
            int status;
            int filter;

            for (status = 0; status < usedShadowDepthBuffers; ++status)
            {
                GlStateManager.bindTexture(sfbDepthTextures.get(status));
                GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, 10496.0F);
                GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, 10496.0F);
                filter = shadowFilterNearest[status] ? 9728 : 9729;
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, filter);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, filter);

                if (shadowHardwareFilteringEnabled[status])
                {
                    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_COMPARE_MODE, GL14.GL_COMPARE_R_TO_TEXTURE);
                }

                GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_DEPTH_COMPONENT, shadowMapWidth, shadowMapHeight, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (FloatBuffer)null);
            }

            EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36096, 3553, sfbDepthTextures.get(0), 0);
            checkGLError("FT sd");

            for (status = 0; status < usedShadowColorBuffers; ++status)
            {
                GlStateManager.bindTexture(sfbColorTextures.get(status));
                GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, 10496.0F);
                GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, 10496.0F);
                filter = shadowColorFilterNearest[status] ? 9728 : 9729;
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, filter);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, filter);
                GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, shadowMapWidth, shadowMapHeight, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, (ByteBuffer)null);
                EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + status, 3553, sfbColorTextures.get(status), 0);
                checkGLError("FT sc");
            }

            GlStateManager.bindTexture(0);

            if (usedShadowColorBuffers > 0)
            {
                GL20.glDrawBuffers(sfbDrawBuffers);
            }

            status = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);

            if (status != 36053)
            {
                printChatAndLogError("[Shaders] Error: Failed creating shadow framebuffer! (Status " + status + ")");
            }
            else
            {
                SMCLog.info("Shadow framebuffer created.");
            }
        }
    }

    public static void beginRender(Minecraft minecraft, float partialTicks, long finishTimeNano)
    {
        checkGLError("pre beginRender");
        checkWorldChanged(mc.theWorld);
        mc = minecraft;
        mc.mcProfiler.startSection("init");
        entityRenderer = mc.entityRenderer;

        if (!isShaderPackInitialized)
        {
            try
            {
                init();
            }
            catch (IllegalStateException var7)
            {
                if (Config.normalize(var7.getMessage()).equals("Function is not supported"))
                {
                    printChatAndLogError("[Shaders] Error: " + var7.getMessage());
                    var7.printStackTrace();
                    setShaderPack(packNameNone);
                    return;
                }
            }
        }

        if (mc.displayWidth != renderDisplayWidth || mc.displayHeight != renderDisplayHeight)
        {
            resize();
        }

        if (needResizeShadow)
        {
            resizeShadow();
        }

        worldTime = mc.theWorld.getWorldTime();
        diffWorldTime = (worldTime - lastWorldTime) % 24000L;

        if (diffWorldTime < 0L)
        {
            diffWorldTime += 24000L;
        }

        lastWorldTime = worldTime;
        moonPhase = mc.theWorld.getMoonPhase();
        ++frameCounter;

        if (frameCounter >= 720720)
        {
            frameCounter = 0;
        }

        systemTime = System.currentTimeMillis();

        if (lastSystemTime == 0L)
        {
            lastSystemTime = systemTime;
        }

        diffSystemTime = systemTime - lastSystemTime;
        lastSystemTime = systemTime;
        frameTime = (float)diffSystemTime / 1000.0F;
        frameTimeCounter += frameTime;
        frameTimeCounter %= 3600.0F;
        rainStrength = minecraft.theWorld.getRainStrength(partialTicks);
        float renderViewEntity = (float)diffSystemTime * 0.01F;
        float i = (float)Math.exp(Math.log(0.5D) * (double)renderViewEntity / (double)(wetness < rainStrength ? drynessHalfLife : wetnessHalfLife));
        wetness = wetness * i + rainStrength * (1.0F - i);
        EntityLivingBase var8 = mc.renderViewEntity;
        int var9;

        if (var8 != null)
        {
            isSleeping = var8.isPlayerSleeping();
            eyePosY = (float)var8.posY * partialTicks + (float)var8.lastTickPosY * (1.0F - partialTicks);
            eyeBrightness = var8.getBrightnessForRender(partialTicks);
            i = (float)diffSystemTime * 0.01F;
            float temp2 = (float)Math.exp(Math.log(0.5D) * (double)i / (double)eyeBrightnessHalflife);
            eyeBrightnessFadeX = eyeBrightnessFadeX * temp2 + (float)(eyeBrightness & 65535) * (1.0F - temp2);
            eyeBrightnessFadeY = eyeBrightnessFadeY * temp2 + (float)(eyeBrightness >> 16) * (1.0F - temp2);
            isEyeInWater = 0;

            if (mc.gameSettings.thirdPersonView == 0 && !isSleeping)
            {
                if (var8.isInsideOfMaterial(Material.water))
                {
                    isEyeInWater = 1;
                }
                else if (var8.isInsideOfMaterial(Material.lava))
                {
                    isEyeInWater = 2;
                }
            }

            if (mc.thePlayer != null)
            {
                nightVision = 0.0F;

                if (mc.thePlayer.isPotionActive(Potion.nightVision))
                {
                    nightVision = Config.getMinecraft().entityRenderer.getNightVisionBrightness(mc.thePlayer, partialTicks);
                }

                blindness = 0.0F;

                if (mc.thePlayer.isPotionActive(Potion.blindness))
                {
                    var9 = mc.thePlayer.getActivePotionEffect(Potion.blindness).getDuration();
                    blindness = Config.limit((float)var9 / 20.0F, 0.0F, 1.0F);
                }
            }

            Vec3 var10 = mc.theWorld.getSkyColor(var8, partialTicks);
            var10 = CustomColorizer.getWorldSkyColor(var10, currentWorld, var8, partialTicks);
            skyColorR = (float)var10.xCoord;
            skyColorG = (float)var10.yCoord;
            skyColorB = (float)var10.zCoord;
        }

        isRenderingWorld = true;
        isCompositeRendered = false;
        isHandRenderedMain = false;

        if (usedShadowDepthBuffers >= 1)
        {
            GlStateManager.setActiveTexture(33988);
            GlStateManager.bindTexture(sfbDepthTextures.get(0));

            if (usedShadowDepthBuffers >= 2)
            {
                GlStateManager.setActiveTexture(33989);
                GlStateManager.bindTexture(sfbDepthTextures.get(1));
            }
        }

        GlStateManager.setActiveTexture(33984);

        for (var9 = 0; var9 < usedColorBuffers; ++var9)
        {
            GlStateManager.bindTexture(dfbColorTexturesA[var9]);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GlStateManager.bindTexture(dfbColorTexturesA[8 + var9]);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        }

        GlStateManager.bindTexture(0);

        for (var9 = 0; var9 < 4 && 4 + var9 < usedColorBuffers; ++var9)
        {
            GlStateManager.setActiveTexture(33991 + var9);
            GlStateManager.bindTexture(dfbColorTextures.get(4 + var9));
        }

        GlStateManager.setActiveTexture(33990);
        GlStateManager.bindTexture(dfbDepthTextures.get(0));

        if (usedDepthBuffers >= 2)
        {
            GlStateManager.setActiveTexture(33995);
            GlStateManager.bindTexture(dfbDepthTextures.get(1));

            if (usedDepthBuffers >= 3)
            {
                GlStateManager.setActiveTexture(33996);
                GlStateManager.bindTexture(dfbDepthTextures.get(2));
            }
        }

        for (var9 = 0; var9 < usedShadowColorBuffers; ++var9)
        {
            GlStateManager.setActiveTexture(33997 + var9);
            GlStateManager.bindTexture(sfbColorTextures.get(var9));
        }

        if (noiseTextureEnabled)
        {
            GlStateManager.setActiveTexture(33984 + noiseTexture.getTextureUnit());
            GlStateManager.bindTexture(noiseTexture.getTextureId());
        }

        bindCustomTextures(customTexturesGbuffers);
        GlStateManager.setActiveTexture(33984);
        previousCameraPositionX = cameraPositionX;
        previousCameraPositionY = cameraPositionY;
        previousCameraPositionZ = cameraPositionZ;
        previousProjection.position(0);
        projection.position(0);
        previousProjection.put(projection);
        previousProjection.position(0);
        projection.position(0);
        previousModelView.position(0);
        modelView.position(0);
        previousModelView.put(modelView);
        previousModelView.position(0);
        modelView.position(0);
        checkGLError("beginRender");
        ShadersRender.renderShadowMap(entityRenderer, 0, partialTicks, finishTimeNano);
        mc.mcProfiler.endSection();
        EXTFramebufferObject.glBindFramebufferEXT(36160, dfb);

        for (var9 = 0; var9 < usedColorBuffers; ++var9)
        {
            colorTexturesToggle[var9] = 0;
            EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + var9, 3553, dfbColorTexturesA[var9], 0);
        }

        checkGLError("end beginRender");
    }

    private static void checkWorldChanged(World world)
    {
        if (currentWorld != world)
        {
            World oldWorld = currentWorld;
            currentWorld = world;

            if (oldWorld != null && world != null)
            {
                int dimIdOld = oldWorld.provider.dimensionId;
                int dimIdNew = world.provider.dimensionId;
                boolean dimShadersOld = shaderPackDimensions.contains(Integer.valueOf(dimIdOld));
                boolean dimShadersNew = shaderPackDimensions.contains(Integer.valueOf(dimIdNew));

                if (dimShadersOld || dimShadersNew)
                {
                    uninit();
                }
            }

            Smoother.reset();
        }
    }

    public static void beginRenderPass(int pass, float partialTicks, long finishTimeNano)
    {
        if (!isShadowPass)
        {
            EXTFramebufferObject.glBindFramebufferEXT(36160, dfb);
            GL11.glViewport(0, 0, renderWidth, renderHeight);
            activeDrawBuffers = null;
            ShadersTex.bindNSTextures(defaultTexture.getMultiTexID());
            useProgram(2);
            checkGLError("end beginRenderPass");
        }
    }

    public static void setViewport(int vx, int vy, int vw, int vh)
    {
        GlStateManager.colorMask(true, true, true, true);

        if (isShadowPass)
        {
            GL11.glViewport(0, 0, shadowMapWidth, shadowMapHeight);
        }
        else
        {
            GL11.glViewport(0, 0, renderWidth, renderHeight);
            EXTFramebufferObject.glBindFramebufferEXT(36160, dfb);
            isRenderingDfb = true;
            GlStateManager.enableCull();
            GlStateManager.enableDepth();
            setDrawBuffers(drawBuffersNone);
            useProgram(2);
            checkGLError("beginRenderPass");
        }
    }

    public static int setFogMode(int val)
    {
        fogMode = val;
        return val;
    }

    public static void setFogColor(float r, float g, float b)
    {
        fogColorR = r;
        fogColorG = g;
        fogColorB = b;
        setProgramUniform3f("fogColor", fogColorR, fogColorG, fogColorB);
    }

    public static void setClearColor(float red, float green, float blue, float alpha)
    {
        GlStateManager.clearColor(red, green, blue, alpha);
        clearColorR = red;
        clearColorG = green;
        clearColorB = blue;
    }

    public static void clearRenderBuffer()
    {
        if (isShadowPass)
        {
            checkGLError("shadow clear pre");
            EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36096, 3553, sfbDepthTextures.get(0), 0);
            GL11.glClearColor(1.0F, 1.0F, 1.0F, 1.0F);
            GL20.glDrawBuffers(programsDrawBuffers[30]);
            checkFramebufferStatus("shadow clear");
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            checkGLError("shadow clear");
        }
        else
        {
            checkGLError("clear pre");

            if (gbuffersClear[0])
            {
                GL20.glDrawBuffers(36064);
                GL11.glClear(16384);
            }

            if (gbuffersClear[1])
            {
                GL20.glDrawBuffers(36065);
                GL11.glClearColor(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glClear(16384);
            }

            for (int i = 2; i < usedColorBuffers; ++i)
            {
                if (gbuffersClear[i])
                {
                    GL20.glDrawBuffers(36064 + i);
                    GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
                    GL11.glClear(16384);
                }
            }

            setDrawBuffers(dfbDrawBuffers);
            checkFramebufferStatus("clear");
            checkGLError("clear");
        }
    }

    public static void setCamera(float partialTicks)
    {
        EntityLivingBase viewEntity = mc.renderViewEntity;
        double x = viewEntity.lastTickPosX + (viewEntity.posX - viewEntity.lastTickPosX) * (double)partialTicks;
        double y = viewEntity.lastTickPosY + (viewEntity.posY - viewEntity.lastTickPosY) * (double)partialTicks;
        double z = viewEntity.lastTickPosZ + (viewEntity.posZ - viewEntity.lastTickPosZ) * (double)partialTicks;
        cameraPositionX = x;
        cameraPositionY = y;
        cameraPositionZ = z;
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, (FloatBuffer)projection.position(0));
        SMath.invertMat4FBFA((FloatBuffer)projectionInverse.position(0), (FloatBuffer)projection.position(0), faProjectionInverse, faProjection);
        projection.position(0);
        projectionInverse.position(0);
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, (FloatBuffer)modelView.position(0));
        SMath.invertMat4FBFA((FloatBuffer)modelViewInverse.position(0), (FloatBuffer)modelView.position(0), faModelViewInverse, faModelView);
        modelView.position(0);
        modelViewInverse.position(0);
        checkGLError("setCamera");
    }

    public static void setCameraShadow(float partialTicks)
    {
        EntityLivingBase viewEntity = mc.renderViewEntity;
        double x = viewEntity.lastTickPosX + (viewEntity.posX - viewEntity.lastTickPosX) * (double)partialTicks;
        double y = viewEntity.lastTickPosY + (viewEntity.posY - viewEntity.lastTickPosY) * (double)partialTicks;
        double z = viewEntity.lastTickPosZ + (viewEntity.posZ - viewEntity.lastTickPosZ) * (double)partialTicks;
        cameraPositionX = x;
        cameraPositionY = y;
        cameraPositionZ = z;
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, (FloatBuffer)projection.position(0));
        SMath.invertMat4FBFA((FloatBuffer)projectionInverse.position(0), (FloatBuffer)projection.position(0), faProjectionInverse, faProjection);
        projection.position(0);
        projectionInverse.position(0);
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, (FloatBuffer)modelView.position(0));
        SMath.invertMat4FBFA((FloatBuffer)modelViewInverse.position(0), (FloatBuffer)modelView.position(0), faModelViewInverse, faModelView);
        modelView.position(0);
        modelViewInverse.position(0);
        GL11.glViewport(0, 0, shadowMapWidth, shadowMapHeight);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();

        if (shadowMapIsOrtho)
        {
            GL11.glOrtho((double)(-shadowMapHalfPlane), (double)shadowMapHalfPlane, (double)(-shadowMapHalfPlane), (double)shadowMapHalfPlane, 0.05000000074505806D, 256.0D);
        }
        else
        {
            GLU.gluPerspective(shadowMapFOV, (float)shadowMapWidth / (float)shadowMapHeight, 0.05F, 256.0F);
        }

        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0F, 0.0F, -100.0F);
        GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
        celestialAngle = mc.theWorld.getCelestialAngle(partialTicks);
        sunAngle = celestialAngle < 0.75F ? celestialAngle + 0.25F : celestialAngle - 0.75F;
        float angle = celestialAngle * -360.0F;
        float angleInterval = shadowAngleInterval > 0.0F ? angle % shadowAngleInterval - shadowAngleInterval * 0.5F : 0.0F;

        if ((double)sunAngle <= 0.5D)
        {
            GL11.glRotatef(angle - angleInterval, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(sunPathRotation, 1.0F, 0.0F, 0.0F);
            shadowAngle = sunAngle;
        }
        else
        {
            GL11.glRotatef(angle + 180.0F - angleInterval, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(sunPathRotation, 1.0F, 0.0F, 0.0F);
            shadowAngle = sunAngle - 0.5F;
        }

        float raSun;
        float x1;

        if (shadowMapIsOrtho)
        {
            raSun = shadowIntervalSize;
            x1 = raSun / 2.0F;
            GL11.glTranslatef((float)x % raSun - x1, (float)y % raSun - x1, (float)z % raSun - x1);
        }

        raSun = sunAngle * ((float)Math.PI * 2F);
        x1 = (float)Math.cos((double)raSun);
        float y1 = (float)Math.sin((double)raSun);
        float raTilt = sunPathRotation * ((float)Math.PI * 2F);
        float x2 = x1;
        float y2 = y1 * (float)Math.cos((double)raTilt);
        float z2 = y1 * (float)Math.sin((double)raTilt);

        if ((double)sunAngle > 0.5D)
        {
            x2 = -x1;
            y2 = -y2;
            z2 = -z2;
        }

        shadowLightPositionVector[0] = x2;
        shadowLightPositionVector[1] = y2;
        shadowLightPositionVector[2] = z2;
        shadowLightPositionVector[3] = 0.0F;
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, (FloatBuffer)shadowProjection.position(0));
        SMath.invertMat4FBFA((FloatBuffer)shadowProjectionInverse.position(0), (FloatBuffer)shadowProjection.position(0), faShadowProjectionInverse, faShadowProjection);
        shadowProjection.position(0);
        shadowProjectionInverse.position(0);
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, (FloatBuffer)shadowModelView.position(0));
        SMath.invertMat4FBFA((FloatBuffer)shadowModelViewInverse.position(0), (FloatBuffer)shadowModelView.position(0), faShadowModelViewInverse, faShadowModelView);
        shadowModelView.position(0);
        shadowModelViewInverse.position(0);
        setProgramUniformMatrix4ARB("gbufferProjection", false, projection);
        setProgramUniformMatrix4ARB("gbufferProjectionInverse", false, projectionInverse);
        setProgramUniformMatrix4ARB("gbufferPreviousProjection", false, previousProjection);
        setProgramUniformMatrix4ARB("gbufferModelView", false, modelView);
        setProgramUniformMatrix4ARB("gbufferModelViewInverse", false, modelViewInverse);
        setProgramUniformMatrix4ARB("gbufferPreviousModelView", false, previousModelView);
        setProgramUniformMatrix4ARB("shadowProjection", false, shadowProjection);
        setProgramUniformMatrix4ARB("shadowProjectionInverse", false, shadowProjectionInverse);
        setProgramUniformMatrix4ARB("shadowModelView", false, shadowModelView);
        setProgramUniformMatrix4ARB("shadowModelViewInverse", false, shadowModelViewInverse);
        mc.gameSettings.thirdPersonView = 1;
        checkGLError("setCamera");
    }

    public static void preCelestialRotate()
    {
        GL11.glRotatef(sunPathRotation * 1.0F, 0.0F, 0.0F, 1.0F);
        checkGLError("preCelestialRotate");
    }

    public static void postCelestialRotate()
    {
        FloatBuffer modelView = tempMatrixDirectBuffer;
        modelView.clear();
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelView);
        modelView.get(tempMat, 0, 16);
        SMath.multiplyMat4xVec4(sunPosition, tempMat, sunPosModelView);
        SMath.multiplyMat4xVec4(moonPosition, tempMat, moonPosModelView);
        System.arraycopy(shadowAngle == sunAngle ? sunPosition : moonPosition, 0, shadowLightPosition, 0, 3);
        setProgramUniform3f("sunPosition", sunPosition[0], sunPosition[1], sunPosition[2]);
        setProgramUniform3f("moonPosition", moonPosition[0], moonPosition[1], moonPosition[2]);
        setProgramUniform3f("shadowLightPosition", shadowLightPosition[0], shadowLightPosition[1], shadowLightPosition[2]);
        checkGLError("postCelestialRotate");
    }

    public static void setUpPosition()
    {
        FloatBuffer modelView = tempMatrixDirectBuffer;
        modelView.clear();
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelView);
        modelView.get(tempMat, 0, 16);
        SMath.multiplyMat4xVec4(upPosition, tempMat, upPosModelView);
        setProgramUniform3f("upPosition", upPosition[0], upPosition[1], upPosition[2]);
    }

    public static void genCompositeMipmap()
    {
        if (hasGlGenMipmap)
        {
            for (int i = 0; i < usedColorBuffers; ++i)
            {
                if ((activeCompositeMipmapSetting & 1 << i) != 0)
                {
                    GlStateManager.setActiveTexture(33984 + colorTextureTextureImageUnit[i]);
                    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
                    GL30.glGenerateMipmap(3553);
                }
            }

            GlStateManager.setActiveTexture(33984);
        }
    }

    public static void drawComposite()
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0.0F, 0.0F);
        GL11.glVertex3f(0.0F, 0.0F, 0.0F);
        GL11.glTexCoord2f(1.0F, 0.0F);
        GL11.glVertex3f(1.0F, 0.0F, 0.0F);
        GL11.glTexCoord2f(1.0F, 1.0F);
        GL11.glVertex3f(1.0F, 1.0F, 0.0F);
        GL11.glTexCoord2f(0.0F, 1.0F);
        GL11.glVertex3f(0.0F, 1.0F, 0.0F);
        GL11.glEnd();
    }

    public static void renderDeferred()
    {
        if (hasDeferredPrograms)
        {
            checkGLError("pre-render Deferred");
            renderComposites(33, 8, false);
            mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        }
    }

    public static void renderCompositeFinal()
    {
        checkGLError("pre-render CompositeFinal");
        renderComposites(21, 8, true);
    }

    private static void renderComposites(int programBase, int countPrograms, boolean renderFinal)
    {
        if (!isShadowPass)
        {
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GL11.glOrtho(0.0D, 1.0D, 0.0D, 1.0D, 0.0D, 1.0D);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableTexture2D();
            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();
            GlStateManager.enableDepth();
            GlStateManager.depthFunc(519);
            GlStateManager.depthMask(false);
            GlStateManager.disableLighting();

            if (usedShadowDepthBuffers >= 1)
            {
                GlStateManager.setActiveTexture(33988);
                GlStateManager.bindTexture(sfbDepthTextures.get(0));

                if (usedShadowDepthBuffers >= 2)
                {
                    GlStateManager.setActiveTexture(33989);
                    GlStateManager.bindTexture(sfbDepthTextures.get(1));
                }
            }

            int enableAltBuffers;

            for (enableAltBuffers = 0; enableAltBuffers < usedColorBuffers; ++enableAltBuffers)
            {
                GlStateManager.setActiveTexture(33984 + colorTextureTextureImageUnit[enableAltBuffers]);
                GlStateManager.bindTexture(dfbColorTexturesA[enableAltBuffers]);
            }

            GlStateManager.setActiveTexture(33990);
            GlStateManager.bindTexture(dfbDepthTextures.get(0));

            if (usedDepthBuffers >= 2)
            {
                GlStateManager.setActiveTexture(33995);
                GlStateManager.bindTexture(dfbDepthTextures.get(1));

                if (usedDepthBuffers >= 3)
                {
                    GlStateManager.setActiveTexture(33996);
                    GlStateManager.bindTexture(dfbDepthTextures.get(2));
                }
            }

            for (enableAltBuffers = 0; enableAltBuffers < usedShadowColorBuffers; ++enableAltBuffers)
            {
                GlStateManager.setActiveTexture(33997 + enableAltBuffers);
                GlStateManager.bindTexture(sfbColorTextures.get(enableAltBuffers));
            }

            if (noiseTextureEnabled)
            {
                GlStateManager.setActiveTexture(33984 + noiseTexture.getTextureUnit());
                GlStateManager.bindTexture(noiseTexture.getTextureId());
            }

            if (renderFinal)
            {
                bindCustomTextures(customTexturesComposite);
            }
            else
            {
                bindCustomTextures(customTexturesDeferred);
            }

            GlStateManager.setActiveTexture(33984);
            boolean var8 = true;
            int programLast;

            for (programLast = 0; programLast < usedColorBuffers; ++programLast)
            {
                EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + programLast, 3553, dfbColorTexturesA[8 + programLast], 0);
            }

            EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36096, 3553, dfbDepthTextures.get(0), 0);
            GL20.glDrawBuffers(dfbDrawBuffers);
            checkGLError("pre-composite");
            int i;
            int t0;
            int t1;

            for (programLast = 0; programLast < countPrograms; ++programLast)
            {
                if (programsID[programBase + programLast] != 0)
                {
                    useProgram(programBase + programLast);
                    checkGLError(programNames[programBase + programLast]);

                    if (activeCompositeMipmapSetting != 0)
                    {
                        genCompositeMipmap();
                    }

                    drawComposite();

                    for (i = 0; i < usedColorBuffers; ++i)
                    {
                        if (programsToggleColorTextures[programBase + programLast][i])
                        {
                            t0 = colorTexturesToggle[i];
                            t1 = colorTexturesToggle[i] = 8 - t0;
                            GlStateManager.setActiveTexture(33984 + colorTextureTextureImageUnit[i]);
                            GlStateManager.bindTexture(dfbColorTexturesA[t1 + i]);
                            EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + i, 3553, dfbColorTexturesA[t0 + i], 0);
                        }
                    }

                    GlStateManager.setActiveTexture(33984);
                }
            }

            checkGLError("composite");
            programLast = renderFinal ? 43 : 42;

            if (programsID[programLast] != 0)
            {
                useProgram(programLast);
                checkGLError(programNames[programLast]);

                if (activeCompositeMipmapSetting != 0)
                {
                    genCompositeMipmap();
                }

                drawComposite();

                for (i = 0; i < usedColorBuffers; ++i)
                {
                    if (programsToggleColorTextures[programLast][i])
                    {
                        t0 = colorTexturesToggle[i];
                        t1 = colorTexturesToggle[i] = 8 - t0;
                        GlStateManager.setActiveTexture(33984 + colorTextureTextureImageUnit[i]);
                        GlStateManager.bindTexture(dfbColorTexturesA[t1 + i]);
                        EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + i, 3553, dfbColorTexturesA[t0 + i], 0);
                    }
                }

                GlStateManager.setActiveTexture(33984);
            }

            if (renderFinal)
            {
                renderFinal();
            }

            if (renderFinal)
            {
                isCompositeRendered = true;
            }

            if (!renderFinal)
            {
                for (i = 0; i < usedColorBuffers; ++i)
                {
                    EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + i, 3553, dfbColorTexturesA[i], 0);
                }

                setDrawBuffers(programsDrawBuffers[12]);
            }

            GlStateManager.enableLighting();
            GlStateManager.enableTexture2D();
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.depthFunc(515);
            GlStateManager.depthMask(true);
            GL11.glPopMatrix();
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glPopMatrix();
            useProgram(0);
        }
    }

    private static void renderFinal()
    {
        isRenderingDfb = false;
        mc.getFramebuffer().bindFramebuffer(true);
        EXTFramebufferObject.glFramebufferTexture2DEXT(GlStateManager.GL_FRAMEBUFFER, GlStateManager.GL_COLOR_ATTACHMENT0, 3553, mc.getFramebuffer().framebufferTexture, 0);
        GL11.glViewport(0, 0, mc.displayWidth, mc.displayHeight);

        if (EntityRenderer.anaglyphEnable)
        {
            boolean maskR = EntityRenderer.anaglyphField != 0;
            GlStateManager.colorMask(maskR, !maskR, !maskR, true);
        }

        GlStateManager.depthMask(true);
        GL11.glClearColor(clearColorR, clearColorG, clearColorB, 1.0F);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableTexture2D();
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.enableDepth();
        GlStateManager.depthFunc(519);
        GlStateManager.depthMask(false);
        checkGLError("pre-final");
        useProgram(29);
        checkGLError("final");

        if (activeCompositeMipmapSetting != 0)
        {
            genCompositeMipmap();
        }

        drawComposite();
        checkGLError("renderCompositeFinal");
    }

    public static void endRender()
    {
        if (isShadowPass)
        {
            checkGLError("shadow endRender");
        }
        else
        {
            if (!isCompositeRendered)
            {
                renderCompositeFinal();
            }

            isRenderingWorld = false;
            GlStateManager.colorMask(true, true, true, true);
            useProgram(0);
            RenderHelper.disableStandardItemLighting();
            checkGLError("endRender end");
        }
    }

    public static void beginSky()
    {
        isRenderingSky = true;
        fogEnabled = true;
        setDrawBuffers(dfbDrawBuffers);
        useProgram(5);
        pushEntity(-2, 0);
    }

    public static void setSkyColor(Vec3 v3color)
    {
        skyColorR = (float)v3color.xCoord;
        skyColorG = (float)v3color.yCoord;
        skyColorB = (float)v3color.zCoord;
        setProgramUniform3f("skyColor", skyColorR, skyColorG, skyColorB);
    }

    public static void drawHorizon()
    {
        Tessellator tess = Tessellator.instance;
        float farDistance = (float)(mc.gameSettings.renderDistanceChunks * 16);
        double xzq = (double)farDistance * 0.9238D;
        double xzp = (double)farDistance * 0.3826D;
        double xzn = -xzp;
        double xzm = -xzq;
        double top = 16.0D;
        double bot = -cameraPositionY;
        tess.startDrawingQuads();
        tess.addVertex(xzn, bot, xzm);
        tess.addVertex(xzn, top, xzm);
        tess.addVertex(xzm, top, xzn);
        tess.addVertex(xzm, bot, xzn);
        tess.addVertex(xzm, bot, xzn);
        tess.addVertex(xzm, top, xzn);
        tess.addVertex(xzm, top, xzp);
        tess.addVertex(xzm, bot, xzp);
        tess.addVertex(xzm, bot, xzp);
        tess.addVertex(xzm, top, xzp);
        tess.addVertex(xzn, top, xzp);
        tess.addVertex(xzn, bot, xzp);
        tess.addVertex(xzn, bot, xzp);
        tess.addVertex(xzn, top, xzp);
        tess.addVertex(xzp, top, xzq);
        tess.addVertex(xzp, bot, xzq);
        tess.addVertex(xzp, bot, xzq);
        tess.addVertex(xzp, top, xzq);
        tess.addVertex(xzq, top, xzp);
        tess.addVertex(xzq, bot, xzp);
        tess.addVertex(xzq, bot, xzp);
        tess.addVertex(xzq, top, xzp);
        tess.addVertex(xzq, top, xzn);
        tess.addVertex(xzq, bot, xzn);
        tess.addVertex(xzq, bot, xzn);
        tess.addVertex(xzq, top, xzn);
        tess.addVertex(xzp, top, xzm);
        tess.addVertex(xzp, bot, xzm);
        tess.addVertex(xzp, bot, xzm);
        tess.addVertex(xzp, top, xzm);
        tess.addVertex(xzn, top, xzm);
        tess.addVertex(xzn, bot, xzm);
        tess.draw();
    }

    public static void preSkyList()
    {
        setUpPosition();
        GL11.glColor3f(fogColorR, fogColorG, fogColorB);
        drawHorizon();
        GL11.glColor3f(skyColorR, skyColorG, skyColorB);
    }

    public static void endSky()
    {
        isRenderingSky = false;
        setDrawBuffers(dfbDrawBuffers);
        useProgram(lightmapEnabled ? 3 : 2);
        popEntity();
    }

    public static void beginUpdateChunks()
    {
        checkGLError("beginUpdateChunks1");
        checkFramebufferStatus("beginUpdateChunks1");

        if (!isShadowPass)
        {
            useProgram(7);
        }

        checkGLError("beginUpdateChunks2");
        checkFramebufferStatus("beginUpdateChunks2");
    }

    public static void endUpdateChunks()
    {
        checkGLError("endUpdateChunks1");
        checkFramebufferStatus("endUpdateChunks1");

        if (!isShadowPass)
        {
            useProgram(7);
        }

        checkGLError("endUpdateChunks2");
        checkFramebufferStatus("endUpdateChunks2");
    }

    public static boolean shouldRenderClouds(GameSettings gs)
    {
        if (!shaderPackLoaded)
        {
            return true;
        }
        else
        {
            checkGLError("shouldRenderClouds");
            return isShadowPass ? configCloudShadow : gs.clouds;
        }
    }

    public static void beginClouds()
    {
        fogEnabled = true;
        pushEntity(-3, 0);
        useProgram(6);
    }

    public static void endClouds()
    {
        disableFog();
        popEntity();
        useProgram(lightmapEnabled ? 3 : 2);
    }

    public static void beginTerrain()
    {
        if (isRenderingWorld)
        {
            if (isShadowPass)
            {
                GL11.glDisable(GL11.GL_CULL_FACE);
            }

            fogEnabled = true;
            useProgram(7);
        }
    }

    public static void endTerrain()
    {
        if (isRenderingWorld)
        {
            if (isShadowPass)
            {
                GL11.glEnable(GL11.GL_CULL_FACE);
            }

            useProgram(lightmapEnabled ? 3 : 2);
        }
    }

    public static void beginEntities()
    {
        if (isRenderingWorld)
        {
            useProgram(16);
            resetDisplayListModels();
        }
    }

    public static void nextEntity(Entity entity)
    {
        if (isRenderingWorld)
        {
            useProgram(16);
            setEntityId(entity);
        }
    }

    public static void setEntityId(Entity entity)
    {
        if (isRenderingWorld && !isShadowPass && uniformEntityId.isDefined())
        {
            int id = EntityList.getEntityID(entity);
            uniformEntityId.setValue(id);
        }
    }

    public static void beginSpiderEyes()
    {
        if (isRenderingWorld && programsID[18] != programsID[0])
        {
            useProgram(18);
            GlStateManager.enableAlpha();
            GlStateManager.alphaFunc(516, 0.0F);
            GlStateManager.blendFunc(770, 771);
        }
    }

    public static void endSpiderEyes()
    {
        if (isRenderingWorld && programsID[18] != programsID[0])
        {
            useProgram(16);
            GlStateManager.disableAlpha();
        }
    }

    public static void endEntities()
    {
        if (isRenderingWorld)
        {
            useProgram(lightmapEnabled ? 3 : 2);
        }
    }

    public static void setEntityColor(float r, float g, float b, float a)
    {
        if (isRenderingWorld && !isShadowPass)
        {
            uniformEntityColor.setValue(r, g, b, a);
        }
    }

    public static void beginLivingDamage()
    {
        if (isRenderingWorld)
        {
            ShadersTex.bindTexture(defaultTexture);

            if (!isShadowPass)
            {
                setDrawBuffers(drawBuffersColorAtt0);
            }
        }
    }

    public static void endLivingDamage()
    {
        if (isRenderingWorld && !isShadowPass)
        {
            setDrawBuffers(programsDrawBuffers[16]);
        }
    }

    public static void beginBlockEntities()
    {
        if (isRenderingWorld)
        {
            checkGLError("beginBlockEntities");
            useProgram(13);
        }
    }

    public static void nextBlockEntity(TileEntity tileEntity)
    {
        if (isRenderingWorld)
        {
            checkGLError("nextBlockEntity");
            useProgram(13);
            setBlockEntityId(tileEntity);
        }
    }

    public static void setBlockEntityId(TileEntity tileEntity)
    {
        if (isRenderingWorld && !isShadowPass && uniformBlockEntityId.isDefined())
        {
            Block block = tileEntity.getBlockType();
            int blockId = Block.getIdFromBlock(block);
            uniformBlockEntityId.setValue(blockId);
        }
    }

    public static void endBlockEntities()
    {
        if (isRenderingWorld)
        {
            checkGLError("endBlockEntities");
            useProgram(lightmapEnabled ? 3 : 2);
            ShadersTex.bindNSTextures(defaultTexture.getMultiTexID());
        }
    }

    public static void beginBlockDestroyProgress()
    {
        if (isRenderingWorld)
        {
            useProgram(7);

            if (configTweakBlockDamage)
            {
                setDrawBuffers(drawBuffersColorAtt0);
                GL11.glDepthMask(false);
            }
        }
    }

    public static void endBlockDestroyProgress()
    {
        if (isRenderingWorld)
        {
            GL11.glDepthMask(true);
            useProgram(3);
        }
    }

    public static void beginLitParticles()
    {
        useProgram(3);
    }

    public static void beginParticles()
    {
        useProgram(2);
    }

    public static void endParticles()
    {
        useProgram(3);
    }

    public static void readCenterDepth()
    {
        if (!isShadowPass && centerDepthSmoothEnabled)
        {
            tempDirectFloatBuffer.clear();
            GL11.glReadPixels(renderWidth / 2, renderHeight / 2, 1, 1, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, tempDirectFloatBuffer);
            centerDepth = tempDirectFloatBuffer.get(0);
            float fadeScalar = (float)diffSystemTime * 0.01F;
            float fadeFactor = (float)Math.exp(Math.log(0.5D) * (double)fadeScalar / (double)centerDepthSmoothHalflife);
            centerDepthSmooth = centerDepthSmooth * fadeFactor + centerDepth * (1.0F - fadeFactor);
        }
    }

    public static void beginWeather()
    {
        if (!isShadowPass)
        {
            if (usedDepthBuffers >= 3)
            {
                GlStateManager.setActiveTexture(33996);
                GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 0, 0, renderWidth, renderHeight);
                GlStateManager.setActiveTexture(33984);
            }

            GlStateManager.enableDepth();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
            GlStateManager.enableAlpha();
            useProgram(20);
        }
    }

    public static void endWeather()
    {
        GlStateManager.disableBlend();
        useProgram(3);
    }

    public static void preWater()
    {
        if (usedDepthBuffers >= 2)
        {
            GlStateManager.setActiveTexture(33995);
            checkGLError("pre copy depth");
            GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 0, 0, renderWidth, renderHeight);
            checkGLError("copy depth");
            GlStateManager.setActiveTexture(33984);
        }

        ShadersTex.bindNSTextures(defaultTexture.getMultiTexID());
    }

    public static void beginWater()
    {
        if (isRenderingWorld)
        {
            if (!isShadowPass)
            {
                renderDeferred();
                useProgram(12);
                GlStateManager.enableBlend();
                GlStateManager.depthMask(true);
            }
            else
            {
                GlStateManager.depthMask(true);
            }
        }
    }

    public static void endWater()
    {
        if (isRenderingWorld)
        {
            if (isShadowPass)
            {
                ;
            }

            useProgram(lightmapEnabled ? 3 : 2);
        }
    }

    public static void beginProjectRedHalo()
    {
        if (isRenderingWorld)
        {
            useProgram(1);
        }
    }

    public static void endProjectRedHalo()
    {
        if (isRenderingWorld)
        {
            useProgram(3);
        }
    }

    public static void applyHandDepth()
    {
        if ((double)configHandDepthMul != 1.0D)
        {
            GL11.glScaled(1.0D, 1.0D, (double)configHandDepthMul);
        }
    }

    public static void beginHand(boolean translucent)
    {
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);

        if (translucent)
        {
            useProgram(41);
        }
        else
        {
            useProgram(19);
        }

        checkGLError("beginHand");
        checkFramebufferStatus("beginHand");
    }

    public static void endHand()
    {
        checkGLError("pre endHand");
        checkFramebufferStatus("pre endHand");
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPopMatrix();
        GlStateManager.blendFunc(770, 771);
        checkGLError("endHand");
    }

    public static void beginFPOverlay()
    {
        GlStateManager.disableLighting();
        GlStateManager.disableBlend();
    }

    public static void endFPOverlay() {}

    public static void glEnableWrapper(int cap)
    {
        GL11.glEnable(cap);

        if (cap == 3553)
        {
            enableTexture2D();
        }
        else if (cap == 2912)
        {
            enableFog();
        }
    }

    public static void glDisableWrapper(int cap)
    {
        GL11.glDisable(cap);

        if (cap == 3553)
        {
            disableTexture2D();
        }
        else if (cap == 2912)
        {
            disableFog();
        }
    }

    public static void sglEnableT2D(int cap)
    {
        GL11.glEnable(cap);
        enableTexture2D();
    }

    public static void sglDisableT2D(int cap)
    {
        GL11.glDisable(cap);
        disableTexture2D();
    }

    public static void sglEnableFog(int cap)
    {
        GL11.glEnable(cap);
        enableFog();
    }

    public static void sglDisableFog(int cap)
    {
        GL11.glDisable(cap);
        disableFog();
    }

    public static void enableTexture2D()
    {
        if (isRenderingSky)
        {
            useProgram(5);
        }
        else if (activeProgram == 1)
        {
            useProgram(lightmapEnabled ? 3 : 2);
        }
    }

    public static void disableTexture2D()
    {
        if (isRenderingSky)
        {
            useProgram(4);
        }
        else if (activeProgram == 2 || activeProgram == 3)
        {
            useProgram(1);
        }
    }

    public static void beginLeash()
    {
        useProgram(1);
    }

    public static void endLeash()
    {
        useProgram(16);
    }

    public static void enableFog()
    {
        fogEnabled = true;
        setProgramUniform1i("fogMode", fogMode);
    }

    public static void disableFog()
    {
        fogEnabled = false;
        setProgramUniform1i("fogMode", 0);
    }

    public static void setFog(int fogMode)
    {
        GlStateManager.setFog(fogMode);
        fogMode = fogMode;

        if (fogEnabled)
        {
            setProgramUniform1i("fogMode", fogMode);
        }
    }

    public static void sglFogi(int pname, int param)
    {
        GL11.glFogi(pname, param);

        if (pname == 2917)
        {
            fogMode = param;

            if (fogEnabled)
            {
                setProgramUniform1i("fogMode", fogMode);
            }
        }
    }

    public static void enableLightmap()
    {
        lightmapEnabled = true;

        if (activeProgram == 2)
        {
            useProgram(3);
        }
    }

    public static void disableLightmap()
    {
        lightmapEnabled = false;

        if (activeProgram == 3)
        {
            useProgram(2);
        }
    }

    public static int getEntityData()
    {
        return entityData[entityDataIndex * 2];
    }

    public static int getEntityData2()
    {
        return entityData[entityDataIndex * 2 + 1];
    }

    public static int setEntityData1(int data1)
    {
        entityData[entityDataIndex * 2] = entityData[entityDataIndex * 2] & 65535 | data1 << 16;
        return data1;
    }

    public static int setEntityData2(int data2)
    {
        entityData[entityDataIndex * 2 + 1] = entityData[entityDataIndex * 2 + 1] & -65536 | data2 & 65535;
        return data2;
    }

    public static void pushEntity(int data0, int data1)
    {
        ++entityDataIndex;
        entityData[entityDataIndex * 2] = data0 & 65535 | data1 << 16;
        entityData[entityDataIndex * 2 + 1] = 0;
    }

    public static void pushEntity(int data0)
    {
        ++entityDataIndex;
        entityData[entityDataIndex * 2] = data0 & 65535;
        entityData[entityDataIndex * 2 + 1] = 0;
    }

    public static void pushEntity(Block block)
    {
        int blockId = Block.blockRegistry.getIDForObject(block);
        byte metadata = 0;
        blockId = BlockAliases.getMappedBlockId(blockId, metadata);
        ++entityDataIndex;
        entityData[entityDataIndex * 2] = blockId & 65535 | block.getRenderType() << 16;
        entityData[entityDataIndex * 2 + 1] = metadata;
    }

    public static void pushEntity(RenderBlocks rb, Block block, int x, int y, int z)
    {
        int blockId = Block.blockRegistry.getIDForObject(block);
        int metadata = rb.blockAccess.getBlockMetadata(x, y, z);
        blockId = BlockAliases.getMappedBlockId(blockId, metadata);
        ++entityDataIndex;
        entityData[entityDataIndex * 2] = blockId & 65535 | block.getRenderType() << 16;
        entityData[entityDataIndex * 2 + 1] = metadata;
    }

    public static void popEntity()
    {
        entityData[entityDataIndex * 2] = 0;
        entityData[entityDataIndex * 2 + 1] = 0;
        --entityDataIndex;
    }

    public static void mcProfilerEndSection()
    {
        mc.mcProfiler.endSection();
    }

    public static String getShaderPackName()
    {
        return shaderPack == null ? null : (shaderPack instanceof ShaderPackNone ? null : shaderPack.getName());
    }

    public static InputStream getShaderPackResourceStream(String path)
    {
        return shaderPack == null ? null : shaderPack.getResourceAsStream(path);
    }

    public static void nextAntialiasingLevel()
    {
        configAntialiasingLevel += 2;
        configAntialiasingLevel = configAntialiasingLevel / 2 * 2;

        if (configAntialiasingLevel > 4)
        {
            configAntialiasingLevel = 0;
        }

        configAntialiasingLevel = Config.limit(configAntialiasingLevel, 0, 4);
    }

    public static void checkShadersModInstalled()
    {
        Class cls = null;

        try
        {
            cls = Class.forName("shadersmod.transform.SMCClassTransformer");
        }
        catch (Throwable var3)
        {
            ;
        }

        try
        {
            cls = Class.forName("shadersmodcore.transform.SMCClassTransformer");
        }
        catch (Throwable var2)
        {
            ;
        }

        if (cls != null)
        {
            throw new RuntimeException("Shaders Mod detected. Please remove it, OptiFine has built-in support for shaders.");
        }
    }

    public static void resourcesReloaded()
    {
        loadShaderPackResources();

        if (shaderPackLoaded)
        {
            BlockAliases.resourcesReloaded();
        }
    }

    private static void loadShaderPackResources()
    {
        shaderPackResources = new HashMap();

        if (shaderPackLoaded)
        {
            ArrayList listFiles = new ArrayList();
            String PREFIX = "/shaders/lang/";
            String EN_US = "en_US";
            String SUFFIX = ".lang";
            listFiles.add(PREFIX + EN_US + SUFFIX);

            if (!Config.getGameSettings().language.equals(EN_US))
            {
                listFiles.add(PREFIX + Config.getGameSettings().language + SUFFIX);
            }

            try
            {
                Iterator e = listFiles.iterator();

                while (e.hasNext())
                {
                    String file = (String)e.next();
                    InputStream in = shaderPack.getResourceAsStream(file);

                    if (in != null)
                    {
                        Properties props = new Properties();
                        Lang.loadLocaleData(in, props);
                        in.close();
                        Set keys = props.keySet();
                        Iterator itp = keys.iterator();

                        while (itp.hasNext())
                        {
                            String key = (String)itp.next();
                            String value = props.getProperty(key);
                            shaderPackResources.put(key, value);
                        }
                    }
                }
            }
            catch (IOException var12)
            {
                var12.printStackTrace();
            }
        }
    }

    public static String translate(String key, String def)
    {
        String str = (String)shaderPackResources.get(key);
        return str == null ? def : str;
    }

    public static boolean isProgramPath(String program)
    {
        if (program == null)
        {
            return false;
        }
        else if (program.length() <= 0)
        {
            return false;
        }
        else
        {
            int pos = program.lastIndexOf("/");

            if (pos >= 0)
            {
                program = program.substring(pos + 1);
            }

            return Arrays.asList(programNames).contains(program);
        }
    }

    public static void setItemToRenderMain(ItemStack itemToRenderMain)
    {
        itemToRenderMainTranslucent = isTranslucentBlock(itemToRenderMain);
    }

    public static boolean isItemToRenderMainTranslucent()
    {
        return itemToRenderMainTranslucent;
    }

    private static boolean isTranslucentBlock(ItemStack stack)
    {
        if (stack == null)
        {
            return false;
        }
        else
        {
            Item item = stack.getItem();

            if (item == null)
            {
                return false;
            }
            else if (!(item instanceof ItemBlock))
            {
                return false;
            }
            else
            {
                ItemBlock itemBlock = (ItemBlock)item;
                Block block = (Block)Reflector.getFieldValue((ItemBlock)item, Reflector.ItemBlock_block);

                if (block == null)
                {
                    return false;
                }
                else
                {
                    int renderPass = block.getRenderBlockPass();
                    return renderPass != 0;
                }
            }
        }
    }

    public static boolean isRenderBothHands()
    {
        return false;
    }

    public static boolean isHandRenderedMain()
    {
        return isHandRenderedMain;
    }

    public static void setHandRenderedMain(boolean isHandRenderedMain)
    {
        isHandRenderedMain = isHandRenderedMain;
    }

    public static float getShadowRenderDistance()
    {
        return shadowDistanceRenderMul < 0.0F ? -1.0F : shadowMapHalfPlane * shadowDistanceRenderMul;
    }

    public static void setRenderingFirstPersonHand(boolean flag)
    {
        isRenderingFirstPersonHand = flag;
    }

    public static boolean isRenderingFirstPersonHand()
    {
        return isRenderingFirstPersonHand;
    }

    public static void beginBeacon()
    {
        if (isRenderingWorld)
        {
            useProgram(14);
        }
    }

    public static void endBeacon()
    {
        if (isRenderingWorld)
        {
            useProgram(13);
        }
    }

    public static World getCurrentWorld()
    {
        return currentWorld;
    }

    public static Vec3 getCameraPosition()
    {
        return Vec3.createVectorHelper(cameraPositionX, cameraPositionY, cameraPositionZ);
    }

    public static boolean isCustomUniforms()
    {
        return customUniforms != null;
    }

    static
    {
        drawBuffersNone.limit(0);
        drawBuffersColorAtt0.put(36064).position(0).limit(1);
        formatNames = new String[] {"R8", "RG8", "RGB8", "RGBA8", "R8_SNORM", "RG8_SNORM", "RGB8_SNORM", "RGBA8_SNORM", "R16", "RG16", "RGB16", "RGBA16", "R16_SNORM", "RG16_SNORM", "RGB16_SNORM", "RGBA16_SNORM", "R16F", "RG16F", "RGB16F", "RGBA16F", "R32F", "RG32F", "RGB32F", "RGBA32F", "R32I", "RG32I", "RGB32I", "RGBA32I", "R32UI", "RG32UI", "RGB32UI", "RGBA32UI", "R3_G3_B2", "RGB5_A1", "RGB10_A2", "R11F_G11F_B10F", "RGB9_E5"};
        formatIds = new int[] {33321, 33323, 32849, 32856, 36756, 36757, 36758, 36759, 33322, 33324, 32852, 32859, 36760, 36761, 36762, 36763, 33325, 33327, 34843, 34842, 33326, 33328, 34837, 34836, 33333, 33339, 36227, 36226, 33334, 33340, 36209, 36208, 10768, 32855, 32857, 35898, 35901};
        patternLoadEntityDataMap = Pattern.compile("\\s*([\\w:]+)\\s*=\\s*([-]?\\d+)\\s*");
        entityData = new int[32];
        entityDataIndex = 0;
    }

    static class NamelessClass1174115805
    {
        static final int[] $SwitchMap$shadersmod$client$EnumShaderOption = new int[EnumShaderOption.values().length];

        static
        {
            try
            {
                $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.ANTIALIASING.ordinal()] = 1;
            }
            catch (NoSuchFieldError var18)
            {
                ;
            }

            try
            {
                $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.NORMAL_MAP.ordinal()] = 2;
            }
            catch (NoSuchFieldError var17)
            {
                ;
            }

            try
            {
                $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.SPECULAR_MAP.ordinal()] = 3;
            }
            catch (NoSuchFieldError var16)
            {
                ;
            }

            try
            {
                $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.RENDER_RES_MUL.ordinal()] = 4;
            }
            catch (NoSuchFieldError var15)
            {
                ;
            }

            try
            {
                $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.SHADOW_RES_MUL.ordinal()] = 5;
            }
            catch (NoSuchFieldError var14)
            {
                ;
            }

            try
            {
                $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.HAND_DEPTH_MUL.ordinal()] = 6;
            }
            catch (NoSuchFieldError var13)
            {
                ;
            }

            try
            {
                $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.CLOUD_SHADOW.ordinal()] = 7;
            }
            catch (NoSuchFieldError var12)
            {
                ;
            }

            try
            {
                $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.OLD_HAND_LIGHT.ordinal()] = 8;
            }
            catch (NoSuchFieldError var11)
            {
                ;
            }

            try
            {
                $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.OLD_LIGHTING.ordinal()] = 9;
            }
            catch (NoSuchFieldError var10)
            {
                ;
            }

            try
            {
                $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.SHADER_PACK.ordinal()] = 10;
            }
            catch (NoSuchFieldError var9)
            {
                ;
            }

            try
            {
                $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.TWEAK_BLOCK_DAMAGE.ordinal()] = 11;
            }
            catch (NoSuchFieldError var8)
            {
                ;
            }

            try
            {
                $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.SHADOW_CLIP_FRUSTRUM.ordinal()] = 12;
            }
            catch (NoSuchFieldError var7)
            {
                ;
            }

            try
            {
                $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.TEX_MIN_FIL_B.ordinal()] = 13;
            }
            catch (NoSuchFieldError var6)
            {
                ;
            }

            try
            {
                $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.TEX_MIN_FIL_N.ordinal()] = 14;
            }
            catch (NoSuchFieldError var5)
            {
                ;
            }

            try
            {
                $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.TEX_MIN_FIL_S.ordinal()] = 15;
            }
            catch (NoSuchFieldError var4)
            {
                ;
            }

            try
            {
                $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.TEX_MAG_FIL_B.ordinal()] = 16;
            }
            catch (NoSuchFieldError var3)
            {
                ;
            }

            try
            {
                $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.TEX_MAG_FIL_N.ordinal()] = 17;
            }
            catch (NoSuchFieldError var2)
            {
                ;
            }

            try
            {
                $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.TEX_MAG_FIL_S.ordinal()] = 18;
            }
            catch (NoSuchFieldError var1)
            {
                ;
            }
        }
    }
}

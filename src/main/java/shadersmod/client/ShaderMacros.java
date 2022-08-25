package shadersmod.client;

import net.minecraft.src.Config;
import net.minecraft.util.Util;

public class ShaderMacros
{
    private static String PREFIX_MACRO = "MC_";
    public static final String MC_VERSION = "MC_VERSION";
    public static final String MC_GL_VERSION = "MC_GL_VERSION";
    public static final String MC_GLSL_VERSION = "MC_GLSL_VERSION";
    public static final String MC_OS_WINDOWS = "MC_OS_WINDOWS";
    public static final String MC_OS_MAC = "MC_OS_MAC";
    public static final String MC_OS_LINUX = "MC_OS_LINUX";
    public static final String MC_OS_OTHER = "MC_OS_OTHER";
    public static final String MC_GL_VENDOR_ATI = "MC_GL_VENDOR_ATI";
    public static final String MC_GL_VENDOR_INTEL = "MC_GL_VENDOR_INTEL";
    public static final String MC_GL_VENDOR_NVIDIA = "MC_GL_VENDOR_NVIDIA";
    public static final String MC_GL_VENDOR_XORG = "MC_GL_VENDOR_XORG";
    public static final String MC_GL_VENDOR_OTHER = "MC_GL_VENDOR_OTHER";
    public static final String MC_GL_RENDERER_RADEON = "MC_GL_RENDERER_RADEON";
    public static final String MC_GL_RENDERER_GEFORCE = "MC_GL_RENDERER_GEFORCE";
    public static final String MC_GL_RENDERER_QUADRO = "MC_GL_RENDERER_QUADRO";
    public static final String MC_GL_RENDERER_INTEL = "MC_GL_RENDERER_INTEL";
    public static final String MC_GL_RENDERER_GALLIUM = "MC_GL_RENDERER_GALLIUM";
    public static final String MC_GL_RENDERER_MESA = "MC_GL_RENDERER_MESA";
    public static final String MC_GL_RENDERER_OTHER = "MC_GL_RENDERER_OTHER";
    public static final String MC_FXAA_LEVEL = "MC_FXAA_LEVEL";
    public static final String MC_NORMAL_MAP = "MC_NORMAL_MAP";
    public static final String MC_SPECULAR_MAP = "MC_SPECULAR_MAP";
    public static final String MC_RENDER_QUALITY = "MC_RENDER_QUALITY";
    public static final String MC_SHADOW_QUALITY = "MC_SHADOW_QUALITY";
    public static final String MC_HAND_DEPTH = "MC_HAND_DEPTH";
    public static final String MC_OLD_HAND_LIGHT = "MC_OLD_HAND_LIGHT";
    public static final String MC_OLD_LIGHTING = "MC_OLD_LIGHTING";
    private static String[] extensionMacros;

    public static String getOs()
    {
        Util.EnumOS os = Util.getOSType();

        switch (ShaderMacros.NamelessClass2138009012.$SwitchMap$net$minecraft$util$Util$EnumOS[os.ordinal()])
        {
            case 1:
                return "MC_OS_WINDOWS";

            case 2:
                return "MC_OS_MAC";

            case 3:
                return "MC_OS_LINUX";

            default:
                return "MC_OS_OTHER";
        }
    }

    public static String getVendor()
    {
        String vendor = Config.openGlVendor;

        if (vendor == null)
        {
            return "MC_GL_VENDOR_OTHER";
        }
        else
        {
            vendor = vendor.toLowerCase();
            return vendor.startsWith("ati") ? "MC_GL_VENDOR_ATI" : (vendor.startsWith("intel") ? "MC_GL_VENDOR_INTEL" : (vendor.startsWith("nvidia") ? "MC_GL_VENDOR_NVIDIA" : (vendor.startsWith("x.org") ? "MC_GL_VENDOR_XORG" : "MC_GL_VENDOR_OTHER")));
        }
    }

    public static String getRenderer()
    {
        String renderer = Config.openGlRenderer;

        if (renderer == null)
        {
            return "MC_GL_RENDERER_OTHER";
        }
        else
        {
            renderer = renderer.toLowerCase();
            return renderer.startsWith("amd") ? "MC_GL_RENDERER_RADEON" : (renderer.startsWith("ati") ? "MC_GL_RENDERER_RADEON" : (renderer.startsWith("radeon") ? "MC_GL_RENDERER_RADEON" : (renderer.startsWith("gallium") ? "MC_GL_RENDERER_GALLIUM" : (renderer.startsWith("intel") ? "MC_GL_RENDERER_INTEL" : (renderer.startsWith("geforce") ? "MC_GL_RENDERER_GEFORCE" : (renderer.startsWith("nvidia") ? "MC_GL_RENDERER_GEFORCE" : (renderer.startsWith("quadro") ? "MC_GL_RENDERER_QUADRO" : (renderer.startsWith("nvs") ? "MC_GL_RENDERER_QUADRO" : (renderer.startsWith("mesa") ? "MC_GL_RENDERER_MESA" : "MC_GL_RENDERER_OTHER")))))))));
        }
    }

    public static String getPrefixMacro()
    {
        return PREFIX_MACRO;
    }

    public static String[] getExtensions()
    {
        if (extensionMacros == null)
        {
            String[] exts = Config.getOpenGlExtensions();
            String[] extMacros = new String[exts.length];

            for (int i = 0; i < exts.length; ++i)
            {
                extMacros[i] = PREFIX_MACRO + exts[i];
            }

            extensionMacros = extMacros;
        }

        return extensionMacros;
    }

    public static String getMacroLines()
    {
        StringBuilder sb = new StringBuilder();
        addMacroLine(sb, "MC_VERSION", Config.getMinecraftVersionInt());
        addMacroLine(sb, "MC_GL_VERSION " + Config.getGlVersion().toInt());
        addMacroLine(sb, "MC_GLSL_VERSION " + Config.getGlslVersion().toInt());
        addMacroLine(sb, getOs());
        addMacroLine(sb, getVendor());
        addMacroLine(sb, getRenderer());

        if (Shaders.configAntialiasingLevel > 0)
        {
            addMacroLine(sb, "MC_FXAA_LEVEL", Shaders.configAntialiasingLevel);
        }

        if (Shaders.configNormalMap)
        {
            addMacroLine(sb, "MC_NORMAL_MAP");
        }

        if (Shaders.configSpecularMap)
        {
            addMacroLine(sb, "MC_SPECULAR_MAP");
        }

        addMacroLine(sb, "MC_RENDER_QUALITY", Shaders.configRenderResMul);
        addMacroLine(sb, "MC_SHADOW_QUALITY", Shaders.configShadowResMul);
        addMacroLine(sb, "MC_HAND_DEPTH", Shaders.configHandDepthMul);

        if (Shaders.isOldHandLight())
        {
            addMacroLine(sb, "MC_OLD_HAND_LIGHT");
        }

        if (Shaders.isOldLighting())
        {
            addMacroLine(sb, "MC_OLD_LIGHTING");
        }

        return sb.toString();
    }

    private static void addMacroLine(StringBuilder sb, String name, int value)
    {
        sb.append("#define ");
        sb.append(name);
        sb.append(" ");
        sb.append(value);
        sb.append("\n");
    }

    private static void addMacroLine(StringBuilder sb, String name, float value)
    {
        sb.append("#define ");
        sb.append(name);
        sb.append(" ");
        sb.append(value);
        sb.append("\n");
    }

    private static void addMacroLine(StringBuilder sb, String name)
    {
        sb.append("#define ");
        sb.append(name);
        sb.append("\n");
    }

    static class NamelessClass2138009012
    {
        static final int[] $SwitchMap$net$minecraft$util$Util$EnumOS = new int[Util.EnumOS.values().length];

        static
        {
            try
            {
                $SwitchMap$net$minecraft$util$Util$EnumOS[Util.EnumOS.WINDOWS.ordinal()] = 1;
            }
            catch (NoSuchFieldError var3)
            {
                ;
            }

            try
            {
                $SwitchMap$net$minecraft$util$Util$EnumOS[Util.EnumOS.MACOS.ordinal()] = 2;
            }
            catch (NoSuchFieldError var2)
            {
                ;
            }

            try
            {
                $SwitchMap$net$minecraft$util$Util$EnumOS[Util.EnumOS.LINUX.ordinal()] = 3;
            }
            catch (NoSuchFieldError var1)
            {
                ;
            }
        }
    }
}

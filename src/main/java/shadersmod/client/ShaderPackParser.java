package shadersmod.client;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.src.Config;
import net.minecraft.src.StrUtils;
import net.optifine.entity.model.anim.ExpressionParser;
import net.optifine.entity.model.anim.ExpressionType;
import net.optifine.entity.model.anim.IExpression;
import net.optifine.entity.model.anim.ParseException;
import shadersmod.common.SMCLog;
import shadersmod.uniform.CustomUniform;
import shadersmod.uniform.CustomUniforms;
import shadersmod.uniform.ShaderExpressionResolver;
import shadersmod.uniform.UniformType;

public class ShaderPackParser
{
    private static final Pattern PATTERN_VERSION = Pattern.compile("^\\s*#version\\s+.*$");
    private static final Pattern PATTERN_INCLUDE = Pattern.compile("^\\s*#include\\s+\"([A-Za-z0-9_/\\.]+)\".*$");
    private static final Set<String> setConstNames = makeSetConstNames();

    public static ShaderOption[] parseShaderPackOptions(IShaderPack shaderPack, String[] programNames, List<Integer> listDimensions)
    {
        if (shaderPack == null)
        {
            return new ShaderOption[0];
        }
        else
        {
            HashMap mapOptions = new HashMap();
            collectShaderOptions(shaderPack, "/shaders", programNames, mapOptions);
            Iterator options = listDimensions.iterator();

            while (options.hasNext())
            {
                int sos = ((Integer)options.next()).intValue();
                String comp = "/shaders/world" + sos;
                collectShaderOptions(shaderPack, comp, programNames, mapOptions);
            }

            Collection options1 = mapOptions.values();
            ShaderOption[] sos1 = (ShaderOption[])((ShaderOption[])options1.toArray(new ShaderOption[options1.size()]));
            Comparator comp1 = new Comparator()
            {
                public int compare(ShaderOption o1, ShaderOption o2)
                {
                    return o1.getName().compareToIgnoreCase(o2.getName());
                }
                public int compare(Object x0, Object x1)
                {
                    return this.compare((ShaderOption)x0, (ShaderOption)x1);
                }
            };
            Arrays.sort(sos1, comp1);
            return sos1;
        }
    }

    private static void collectShaderOptions(IShaderPack shaderPack, String dir, String[] programNames, Map<String, ShaderOption> mapOptions)
    {
        for (int i = 0; i < programNames.length; ++i)
        {
            String programName = programNames[i];

            if (!programName.equals(""))
            {
                String vsh = dir + "/" + programName + ".vsh";
                String fsh = dir + "/" + programName + ".fsh";
                collectShaderOptions(shaderPack, vsh, mapOptions);
                collectShaderOptions(shaderPack, fsh, mapOptions);
            }
        }
    }

    private static void collectShaderOptions(IShaderPack sp, String path, Map<String, ShaderOption> mapOptions)
    {
        String[] lines = getLines(sp, path);

        for (int i = 0; i < lines.length; ++i)
        {
            String line = lines[i];
            ShaderOption so = getShaderOption(line, path);

            if (so != null && !so.getName().startsWith(ShaderMacros.getPrefixMacro()) && (!so.checkUsed() || isOptionUsed(so, lines)))
            {
                String key = so.getName();
                ShaderOption so2 = (ShaderOption)mapOptions.get(key);

                if (so2 != null)
                {
                    if (!Config.equals(so2.getValueDefault(), so.getValueDefault()))
                    {
                        Config.warn("Ambiguous shader option: " + so.getName());
                        Config.warn(" - in " + Config.arrayToString((Object[])so2.getPaths()) + ": " + so2.getValueDefault());
                        Config.warn(" - in " + Config.arrayToString((Object[])so.getPaths()) + ": " + so.getValueDefault());
                        so2.setEnabled(false);
                    }

                    if (so2.getDescription() == null || so2.getDescription().length() <= 0)
                    {
                        so2.setDescription(so.getDescription());
                    }

                    so2.addPaths(so.getPaths());
                }
                else
                {
                    mapOptions.put(key, so);
                }
            }
        }
    }

    private static boolean isOptionUsed(ShaderOption so, String[] lines)
    {
        for (int i = 0; i < lines.length; ++i)
        {
            String line = lines[i];

            if (so.isUsedInLine(line))
            {
                return true;
            }
        }

        return false;
    }

    private static String[] getLines(IShaderPack sp, String path)
    {
        try
        {
            ArrayList e = new ArrayList();
            String str = loadFile(path, sp, 0, e, 0);

            if (str == null)
            {
                return new String[0];
            }
            else
            {
                ByteArrayInputStream is = new ByteArrayInputStream(str.getBytes());
                String[] lines = Config.readLines((InputStream)is);
                return lines;
            }
        }
        catch (IOException var6)
        {
            Config.dbg(var6.getClass().getName() + ": " + var6.getMessage());
            return new String[0];
        }
    }

    private static ShaderOption getShaderOption(String line, String path)
    {
        ShaderOption so = null;

        if (so == null)
        {
            so = ShaderOptionSwitch.parseOption(line, path);
        }

        if (so == null)
        {
            so = ShaderOptionVariable.parseOption(line, path);
        }

        if (so != null)
        {
            return so;
        }
        else
        {
            if (so == null)
            {
                so = ShaderOptionSwitchConst.parseOption(line, path);
            }

            if (so == null)
            {
                so = ShaderOptionVariableConst.parseOption(line, path);
            }

            return so != null && setConstNames.contains(so.getName()) ? so : null;
        }
    }

    private static Set<String> makeSetConstNames()
    {
        HashSet set = new HashSet();
        set.add("shadowMapResolution");
        set.add("shadowMapFov");
        set.add("shadowDistance");
        set.add("shadowDistanceRenderMul");
        set.add("shadowIntervalSize");
        set.add("generateShadowMipmap");
        set.add("generateShadowColorMipmap");
        set.add("shadowHardwareFiltering");
        set.add("shadowHardwareFiltering0");
        set.add("shadowHardwareFiltering1");
        set.add("shadowtex0Mipmap");
        set.add("shadowtexMipmap");
        set.add("shadowtex1Mipmap");
        set.add("shadowcolor0Mipmap");
        set.add("shadowColor0Mipmap");
        set.add("shadowcolor1Mipmap");
        set.add("shadowColor1Mipmap");
        set.add("shadowtex0Nearest");
        set.add("shadowtexNearest");
        set.add("shadow0MinMagNearest");
        set.add("shadowtex1Nearest");
        set.add("shadow1MinMagNearest");
        set.add("shadowcolor0Nearest");
        set.add("shadowColor0Nearest");
        set.add("shadowColor0MinMagNearest");
        set.add("shadowcolor1Nearest");
        set.add("shadowColor1Nearest");
        set.add("shadowColor1MinMagNearest");
        set.add("wetnessHalflife");
        set.add("drynessHalflife");
        set.add("eyeBrightnessHalflife");
        set.add("centerDepthHalflife");
        set.add("sunPathRotation");
        set.add("ambientOcclusionLevel");
        set.add("superSamplingLevel");
        set.add("noiseTextureResolution");
        return set;
    }

    public static ShaderProfile[] parseProfiles(Properties props, ShaderOption[] shaderOptions)
    {
        String PREFIX_PROFILE = "profile.";
        ArrayList list = new ArrayList();
        Set keys = props.keySet();
        Iterator profs = keys.iterator();

        while (profs.hasNext())
        {
            String key = (String)profs.next();

            if (key.startsWith(PREFIX_PROFILE))
            {
                String name = key.substring(PREFIX_PROFILE.length());
                props.getProperty(key);
                HashSet parsedProfiles = new HashSet();
                ShaderProfile p = parseProfile(name, props, parsedProfiles, shaderOptions);

                if (p != null)
                {
                    list.add(p);
                }
            }
        }

        if (list.size() <= 0)
        {
            return null;
        }
        else
        {
            ShaderProfile[] profs1 = (ShaderProfile[])((ShaderProfile[])list.toArray(new ShaderProfile[list.size()]));
            return profs1;
        }
    }

    public static Set<String> parseOptionSliders(Properties props, ShaderOption[] shaderOptions)
    {
        HashSet sliders = new HashSet();
        String value = props.getProperty("sliders");

        if (value == null)
        {
            return sliders;
        }
        else
        {
            String[] names = Config.tokenize(value, " ");

            for (int i = 0; i < names.length; ++i)
            {
                String name = names[i];
                ShaderOption so = ShaderUtils.getShaderOption(name, shaderOptions);

                if (so == null)
                {
                    Config.warn("Invalid shader option: " + name);
                }
                else
                {
                    sliders.add(name);
                }
            }

            return sliders;
        }
    }

    private static ShaderProfile parseProfile(String name, Properties props, Set<String> parsedProfiles, ShaderOption[] shaderOptions)
    {
        String PREFIX_PROFILE = "profile.";
        String key = PREFIX_PROFILE + name;

        if (parsedProfiles.contains(key))
        {
            Config.warn("[Shaders] Profile already parsed: " + name);
            return null;
        }
        else
        {
            parsedProfiles.add(name);
            ShaderProfile prof = new ShaderProfile(name);
            String val = props.getProperty(key);
            String[] parts = Config.tokenize(val, " ");

            for (int i = 0; i < parts.length; ++i)
            {
                String part = parts[i];

                if (part.startsWith(PREFIX_PROFILE))
                {
                    String tokens = part.substring(PREFIX_PROFILE.length());
                    ShaderProfile option = parseProfile(tokens, props, parsedProfiles, shaderOptions);

                    if (prof != null)
                    {
                        prof.addOptionValues(option);
                        prof.addDisabledPrograms(option.getDisabledPrograms());
                    }
                }
                else
                {
                    String[] var16 = Config.tokenize(part, ":=");
                    String var17;

                    if (var16.length == 1)
                    {
                        var17 = var16[0];
                        boolean value = true;

                        if (var17.startsWith("!"))
                        {
                            value = false;
                            var17 = var17.substring(1);
                        }

                        String so = "program.";

                        if (var17.startsWith(so))
                        {
                            String so1 = var17.substring(so.length());

                            if (!Shaders.isProgramPath(so1))
                            {
                                Config.warn("Invalid program: " + so1 + " in profile: " + prof.getName());
                            }
                            else if (value)
                            {
                                prof.removeDisabledProgram(so1);
                            }
                            else
                            {
                                prof.addDisabledProgram(so1);
                            }
                        }
                        else
                        {
                            ShaderOption var20 = ShaderUtils.getShaderOption(var17, shaderOptions);

                            if (!(var20 instanceof ShaderOptionSwitch))
                            {
                                Config.warn("[Shaders] Invalid option: " + var17);
                            }
                            else
                            {
                                prof.addOptionValue(var17, String.valueOf(value));
                                var20.setVisible(true);
                            }
                        }
                    }
                    else if (var16.length != 2)
                    {
                        Config.warn("[Shaders] Invalid option value: " + part);
                    }
                    else
                    {
                        var17 = var16[0];
                        String var18 = var16[1];
                        ShaderOption var19 = ShaderUtils.getShaderOption(var17, shaderOptions);

                        if (var19 == null)
                        {
                            Config.warn("[Shaders] Invalid option: " + part);
                        }
                        else if (!var19.isValidValue(var18))
                        {
                            Config.warn("[Shaders] Invalid value: " + part);
                        }
                        else
                        {
                            var19.setVisible(true);
                            prof.addOptionValue(var17, var18);
                        }
                    }
                }
            }

            return prof;
        }
    }

    public static Map<String, ScreenShaderOptions> parseGuiScreens(Properties props, ShaderProfile[] shaderProfiles, ShaderOption[] shaderOptions)
    {
        HashMap map = new HashMap();
        parseGuiScreen("screen", props, map, shaderProfiles, shaderOptions);
        return map.isEmpty() ? null : map;
    }

    private static boolean parseGuiScreen(String key, Properties props, Map<String, ScreenShaderOptions> map, ShaderProfile[] shaderProfiles, ShaderOption[] shaderOptions)
    {
        String val = props.getProperty(key);

        if (val == null)
        {
            return false;
        }
        else
        {
            ArrayList list = new ArrayList();
            HashSet setNames = new HashSet();
            String[] opNames = Config.tokenize(val, " ");
            String colStr;

            for (int scrOps = 0; scrOps < opNames.length; ++scrOps)
            {
                colStr = opNames[scrOps];

                if (colStr.equals("<empty>"))
                {
                    list.add((Object)null);
                }
                else if (setNames.contains(colStr))
                {
                    Config.warn("[Shaders] Duplicate option: " + colStr + ", key: " + key);
                }
                else
                {
                    setNames.add(colStr);

                    if (colStr.equals("<profile>"))
                    {
                        if (shaderProfiles == null)
                        {
                            Config.warn("[Shaders] Option profile can not be used, no profiles defined: " + colStr + ", key: " + key);
                        }
                        else
                        {
                            ShaderOptionProfile columns = new ShaderOptionProfile(shaderProfiles, shaderOptions);
                            list.add(columns);
                        }
                    }
                    else if (colStr.equals("*"))
                    {
                        ShaderOptionRest var14 = new ShaderOptionRest("<rest>");
                        list.add(var14);
                    }
                    else if (colStr.startsWith("[") && colStr.endsWith("]"))
                    {
                        String var16 = StrUtils.removePrefixSuffix(colStr, "[", "]");

                        if (!var16.matches("^[a-zA-Z0-9_]+$"))
                        {
                            Config.warn("[Shaders] Invalid screen: " + colStr + ", key: " + key);
                        }
                        else if (!parseGuiScreen("screen." + var16, props, map, shaderProfiles, shaderOptions))
                        {
                            Config.warn("[Shaders] Invalid screen: " + colStr + ", key: " + key);
                        }
                        else
                        {
                            ShaderOptionScreen sso = new ShaderOptionScreen(var16);
                            list.add(sso);
                        }
                    }
                    else
                    {
                        ShaderOption var15 = ShaderUtils.getShaderOption(colStr, shaderOptions);

                        if (var15 == null)
                        {
                            Config.warn("[Shaders] Invalid option: " + colStr + ", key: " + key);
                            list.add((Object)null);
                        }
                        else
                        {
                            var15.setVisible(true);
                            list.add(var15);
                        }
                    }
                }
            }

            ShaderOption[] var13 = (ShaderOption[])((ShaderOption[])list.toArray(new ShaderOption[list.size()]));
            colStr = props.getProperty(key + ".columns");
            int var17 = Config.parseInt(colStr, 2);
            ScreenShaderOptions var18 = new ScreenShaderOptions(key, var13, var17);
            map.put(key, var18);
            return true;
        }
    }

    public static BufferedReader resolveIncludes(BufferedReader reader, String filePath, IShaderPack shaderPack, int fileIndex, List<String> listFiles, int includeLevel) throws IOException
    {
        String fileDir = "/";
        int pos = filePath.lastIndexOf("/");

        if (pos >= 0)
        {
            fileDir = filePath.substring(0, pos);
        }

        CharArrayWriter caw = new CharArrayWriter();
        int macroInsertPosition = -1;
        LinkedHashSet setExtensions = new LinkedHashSet();
        int lineNumber = 1;

        while (true)
        {
            String chars = reader.readLine();
            String strExt;
            String sbAll;
            String strAll;

            if (chars == null)
            {
                char[] var21 = caw.toCharArray();

                if (macroInsertPosition >= 0 && setExtensions.size() > 0)
                {
                    StringBuilder var18 = new StringBuilder();
                    Iterator var22 = setExtensions.iterator();

                    while (var22.hasNext())
                    {
                        sbAll = (String)var22.next();
                        var18.append("#define ");
                        var18.append(sbAll);
                        var18.append("\n");
                    }

                    strExt = var18.toString();
                    StringBuilder var25 = new StringBuilder(new String(var21));
                    var25.insert(macroInsertPosition, strExt);
                    strAll = var25.toString();
                    var21 = strAll.toCharArray();
                }

                CharArrayReader var19 = new CharArrayReader(var21);
                return new BufferedReader(var19);
            }

            Matcher car;

            if (macroInsertPosition < 0)
            {
                car = PATTERN_VERSION.matcher(chars);

                if (car.matches())
                {
                    strExt = ShaderMacros.getMacroLines();
                    sbAll = chars + "\n" + strExt;
                    strAll = "#line " + (lineNumber + 1) + " " + fileIndex;
                    chars = sbAll + strAll;
                    macroInsertPosition = caw.size() + sbAll.length();
                }
            }

            car = PATTERN_INCLUDE.matcher(chars);

            if (car.matches())
            {
                strExt = car.group(1);
                boolean var23 = strExt.startsWith("/");
                strAll = var23 ? "/shaders" + strExt : fileDir + "/" + strExt;

                if (!listFiles.contains(strAll))
                {
                    listFiles.add(strAll);
                }

                int includeFileIndex = listFiles.indexOf(strAll) + 1;
                chars = loadFile(strAll, shaderPack, includeFileIndex, listFiles, includeLevel);

                if (chars == null)
                {
                    throw new IOException("Included file not found: " + filePath);
                }

                if (chars.endsWith("\n"))
                {
                    chars = chars.substring(0, chars.length() - 1);
                }

                chars = "#line 1 " + includeFileIndex + "\n" + chars + "\n" + "#line " + (lineNumber + 1) + " " + fileIndex;
            }

            if (macroInsertPosition >= 0 && chars.contains(ShaderMacros.getPrefixMacro()))
            {
                String[] var20 = findExtensions(chars, ShaderMacros.getExtensions());

                for (int var24 = 0; var24 < var20.length; ++var24)
                {
                    strAll = var20[var24];
                    setExtensions.add(strAll);
                }
            }

            caw.write(chars);
            caw.write("\n");
            ++lineNumber;
        }
    }

    private static String[] findExtensions(String line, String[] extensions)
    {
        ArrayList list = new ArrayList();

        for (int exts = 0; exts < extensions.length; ++exts)
        {
            String ext = extensions[exts];

            if (line.contains(ext))
            {
                list.add(ext);
            }
        }

        String[] var5 = (String[])((String[])list.toArray(new String[list.size()]));
        return var5;
    }

    private static String loadFile(String filePath, IShaderPack shaderPack, int fileIndex, List<String> listFiles, int includeLevel) throws IOException
    {
        if (includeLevel >= 10)
        {
            throw new IOException("#include depth exceeded: " + includeLevel + ", file: " + filePath);
        }
        else
        {
            ++includeLevel;
            InputStream in = shaderPack.getResourceAsStream(filePath);

            if (in == null)
            {
                return null;
            }
            else
            {
                InputStreamReader isr = new InputStreamReader(in, "ASCII");
                BufferedReader br = new BufferedReader(isr);
                br = resolveIncludes(br, filePath, shaderPack, fileIndex, listFiles, includeLevel);
                CharArrayWriter caw = new CharArrayWriter();

                while (true)
                {
                    String line = br.readLine();

                    if (line == null)
                    {
                        return caw.toString();
                    }

                    caw.write(line);
                    caw.write("\n");
                }
            }
        }
    }

    public static CustomUniforms parseCustomUniforms(Properties props)
    {
        String UNIFORM = "uniform";
        String VARIABLE = "variable";
        String PREFIX_UNIFORM = UNIFORM + ".";
        String PREFIX_VARIABLE = VARIABLE + ".";
        HashMap mapExpressions = new HashMap();
        ArrayList listUniforms = new ArrayList();
        Set keys = props.keySet();
        Iterator cusArr = keys.iterator();

        while (cusArr.hasNext())
        {
            String cus = (String)cusArr.next();
            String[] keyParts = Config.tokenize(cus, ".");

            if (keyParts.length == 3)
            {
                String kind = keyParts[0];
                String type = keyParts[1];
                String name = keyParts[2];
                String src = props.getProperty(cus).trim();

                if (mapExpressions.containsKey(name))
                {
                    SMCLog.warning("Expression already defined: " + name);
                }
                else if (kind.equals(UNIFORM) || kind.equals(VARIABLE))
                {
                    SMCLog.info("Custom " + kind + ": " + name);
                    CustomUniform cu = parseCustomUniform(kind, name, type, src, mapExpressions);

                    if (cu != null)
                    {
                        mapExpressions.put(name, cu.getExpression());

                        if (!kind.equals(VARIABLE))
                        {
                            listUniforms.add(cu);
                        }
                    }
                }
            }
        }

        if (listUniforms.size() <= 0)
        {
            return null;
        }
        else
        {
            CustomUniform[] cusArr1 = (CustomUniform[])((CustomUniform[])listUniforms.toArray(new CustomUniform[listUniforms.size()]));
            CustomUniforms cus1 = new CustomUniforms(cusArr1);
            return cus1;
        }
    }

    private static CustomUniform parseCustomUniform(String kind, String name, String type, String src, Map<String, IExpression> mapExpressions)
    {
        try
        {
            UniformType e = UniformType.parse(type);

            if (e == null)
            {
                SMCLog.warning("Unknown " + kind + " type: " + e);
                return null;
            }
            else
            {
                ShaderExpressionResolver resolver = new ShaderExpressionResolver(mapExpressions);
                ExpressionParser parser = new ExpressionParser(resolver);
                IExpression expr = parser.parse(src);
                ExpressionType expressionType = expr.getExpressionType();

                if (!e.matchesExpressionType(expressionType))
                {
                    SMCLog.warning("Expression type does not match " + kind + " type, expression: " + expressionType + ", " + kind + ": " + e + " " + name);
                    return null;
                }
                else
                {
                    CustomUniform cu = new CustomUniform(name, e, expr);
                    return cu;
                }
            }
        }
        catch (ParseException var11)
        {
            SMCLog.warning(var11.getClass().getName() + ": " + var11.getMessage());
            return null;
        }
    }
}

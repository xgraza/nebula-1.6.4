package net.minecraft.util;

public class StatCollector
{
    private static StringTranslate localizedName = StringTranslate.getInstance();
    private static StringTranslate fallbackTranslator = new StringTranslate();
    private static final String __OBFID = "CL_00001211";

    public static String translateToLocal(String par0Str)
    {
        return localizedName.translateKey(par0Str);
    }

    public static String translateToLocalFormatted(String par0Str, Object ... par1ArrayOfObj)
    {
        return localizedName.translateKeyFormat(par0Str, par1ArrayOfObj);
    }

    public static String translateToFallback(String p_150826_0_)
    {
        return fallbackTranslator.translateKey(p_150826_0_);
    }

    public static boolean canTranslate(String par0Str)
    {
        return localizedName.containsTranslateKey(par0Str);
    }

    public static long getLastTranslationUpdateTimeInMilliseconds()
    {
        return localizedName.getLastUpdateTimeInMilliseconds();
    }
}

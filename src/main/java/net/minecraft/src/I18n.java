package net.minecraft.src;

public class I18n
{
    private static Locale i18nLocale;

    static void setLocale(Locale par0Locale)
    {
        i18nLocale = par0Locale;
    }

    public static String getString(String par0Str)
    {
        return i18nLocale.translateKey(par0Str);
    }

    public static String getStringParams(String par0Str, Object ... par1ArrayOfObj)
    {
        return i18nLocale.formatMessage(par0Str, par1ArrayOfObj);
    }
}

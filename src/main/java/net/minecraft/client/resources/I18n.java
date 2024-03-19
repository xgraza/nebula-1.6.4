package net.minecraft.client.resources;

import java.util.Map;

public class I18n
{
    private static Locale i18nLocale;
    private static final String __OBFID = "CL_00001094";

    static void setLocale(Locale par0Locale)
    {
        i18nLocale = par0Locale;
    }

    /**
     * format(a, b) is equivalent to String.format(translate(a), b). Args: translationKey, params...
     */
    public static String format(String par0Str, Object ... par1ArrayOfObj)
    {
        return i18nLocale.formatMessage(par0Str, par1ArrayOfObj);
    }

    public static Map getLocaleProperties()
    {
        return i18nLocale.field_135032_a;
    }
}

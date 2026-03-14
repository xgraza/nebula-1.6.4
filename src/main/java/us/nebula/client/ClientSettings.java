package us.nebula.client;

/**
 * @author xgraza
 * @since 03/05/25
 * best coding practices here!
 */
public final class ClientSettings
{
    public static final String VERSION = String.format("%s+%s/%s-%s",
            BuildConfig.VERSION,
            BuildConfig.BUILD,
            BuildConfig.BRANCH,
            BuildConfig.HASH);

    /**
     * If features should use verbose logging
     */
    public static boolean VERBOSE_LOGGING;

    /**
     * If features should use heavier debugging
     */
    public static boolean DEBUG;

    /**
     * If to use Nebula splash text on the main menu screen
     */
    public static boolean USE_CUSTOM_SPLASH_TEXT;
}

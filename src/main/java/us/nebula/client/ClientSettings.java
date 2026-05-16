package us.nebula.client;

/**
 * @author xgraza
 * @since 03/05/25
 * best coding practices here!
 */
public final class ClientSettings
{
    public static final String SHORT_VERSION = String.format("%s+%s",
            BuildConfig.VERSION, BuildConfig.BUILD);
    public static final String VERSION = String.format("%s/%s-%s",
            SHORT_VERSION, BuildConfig.BRANCH, BuildConfig.HASH);

    public static final String GITHUB_REPO = "https://github.com/xgraza/nebula-1.7.2/tree/"
            + BuildConfig.BRANCH;

    /**
     * If features should use heavier debugging
     */
    public static boolean DEBUG;

    /**
     * If to use Nebula splash text on the main menu screen
     */
    public static boolean USE_CUSTOM_SPLASH_TEXT;
}

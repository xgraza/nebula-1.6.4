package ez.nebula.client;

/**
 * @author xgraza
 * @since 03/05/25
 * best coding practices here!
 */
public final class ClientConfig
{
    // 4.0.0-beta.91.rewrite+a1b2c3d
    public static final String FULL_VERSION = BuildConfig.VERSION
            + "-" + BuildConfig.ENV
            + "." + BuildConfig.BUILD
            + "." + BuildConfig.BRANCH
            + "+" + BuildConfig.HASH;

    public static final String GITHUB_REPO = "https://github.com/xgraza/nebula-1.7.2/tree/"
            + BuildConfig.BRANCH;

    public static boolean FOLK_VALLEY = false;

    /**
     * If features should use heavier debugging
     */
    public static boolean DEBUG;

    /**
     * If to use Nebula splash text on the main menu screen
     */
    public static boolean USE_CUSTOM_SPLASH_TEXT;

    /**
     * If the user has opened the ClickGUI for the first time
     */
    public static boolean OPENED_GUI_BEFORE;
}

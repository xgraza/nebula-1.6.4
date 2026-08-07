package ez.nebula.client.core;

import ez.nebula.client.BuildConfig;

/**
 * @author xgraza
 * @since 03/05/25
 * best coding practices here!
 */
public final class ClientConfig
{
    public static final String SHORT_VERSION = String.format("%s+%s",
            BuildConfig.VERSION, BuildConfig.BUILD);
    public static final String VERSION = String.format("%s/%s-%s",
            SHORT_VERSION, BuildConfig.BRANCH, BuildConfig.HASH);

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

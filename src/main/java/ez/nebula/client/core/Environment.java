package ez.nebula.client.core;

/**
 * @author xgraza
 * @since 6/2/26
 */
public enum Environment
{
    STABLE("", "stable"),
    PUBLIC_BETA("pb", "public beta"),
    PRIVATE("p", "private"),
    RELEASE_CANDIDATE("rc", "release candidate"),
    DEV("d", "developer");

    private final String str, friendly;

    Environment(String str, String friendly)
    {
        this.str = str;
        this.friendly = friendly;
    }

    @Override
    public String toString()
    {
        return str;
    }

    public String getFriendly()
    {
        return friendly;
    }
}

package ez.nebula.client.core;

/**
 * @author xgraza
 * @since 6/2/26
 */
public enum Environment
{
    STABLE(""),
    PUBLIC_BETA("pb"),
    PRIVATE("p"),
    RELEASE_CANDIDATE("rc"),
    DEV("d");

    private final String str;

    Environment(String str)
    {
        this.str = str;
    }

    @Override
    public String toString()
    {
        return str;
    }
}

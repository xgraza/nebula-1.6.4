package us.nebula.client;

/**
 * @author xgraza
 * @since 6/2/26
 */
public enum Environment
{
    STABLE("Stable"),
    PUBLIC_BETA("Public Beta"),
    PRIVATE("Private"),
    RELEASE_CANDIDATE("Release Candidate"),
    DEV("Dev");

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

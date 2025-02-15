package us.nebula.api.manager.cheat;

/**
 * @author xgraza
 * @since 02/14/25
 */
public enum CheatCategory
{
    COMBAT("Combat"),
    EXPLOIT("Exploit"),
    MOVEMENT("Movement"),
    PLAYER("Player"),
    RENDER("Render");

    private final String name;

    CheatCategory(final String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return name;
    }
}

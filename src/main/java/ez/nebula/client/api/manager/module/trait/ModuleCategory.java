package ez.nebula.client.api.manager.module.trait;

/**
 * @author xgraza
 * @since 02/14/25
 */
public enum ModuleCategory
{
    COMBAT("Combat", "a"),
    EXPLOIT("Exploit", "b"),
    // MISCELLANEOUS("Miscellaneous", "F"),
    MOVEMENT("Movement", "E"),
    PLAYER("Player", "d"),
    RENDER("Render", "c"),
    WORLD("World", "e");

    private final String name, icon;

    ModuleCategory(final String name, final String icon)
    {
        this.name = name;
        this.icon = icon;
    }

    public String getIcon()
    {
        return icon;
    }

    @Override
    public String toString()
    {
        return name;
    }
}

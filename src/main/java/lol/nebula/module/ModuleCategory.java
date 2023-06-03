package lol.nebula.module;

/**
 * @author aesthetical
 * @since 04/27/23
 */
public enum ModuleCategory {
    COMBAT("Combat", "A"),
    EXPLOIT("Exploit", "b"),
    MOVEMENT("Movement", "E"),
    PLAYER("Player", "d"),
    VISUAL("Visual", "D"),
    WORLD("World", "e");

    private final String display, iconChar;

    ModuleCategory(String display, String iconChar) {
        this.display = display;
        this.iconChar = iconChar;
    }

    public String getDisplay() {
        return display;
    }

    public String getIconChar() {
        return iconChar;
    }
}

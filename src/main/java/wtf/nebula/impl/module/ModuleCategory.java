package wtf.nebula.impl.module;

public enum ModuleCategory {
    COMBAT("Combat"),
    MISC("Misc"),
    MOVEMENT("Movement"),
    RENDER("Render"),
    WORLD("World");

    public final String displayName;

    ModuleCategory(String displayName) {
        this.displayName = displayName;
    }
}

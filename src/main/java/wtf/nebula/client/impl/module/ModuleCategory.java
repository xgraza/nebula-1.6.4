package wtf.nebula.client.impl.module;

public enum ModuleCategory {
    COMBAT("Combat"),
    MOVEMENT("Movement"),
    MISCELLANEOUS("Miscellaneous"),
    WORLD("World"),
    VISUALS("Visuals"),
    ACTIVE("Always Active");

    public final String displayName;

    ModuleCategory(String displayName) {
        this.displayName = displayName;
    }
}

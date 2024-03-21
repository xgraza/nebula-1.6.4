package nebula.client.module;

/**
 * @author Gavin
 * @since 08/09/23
 */
public enum ModuleCategory {
  COMBAT("Combat"),
  EXPLOIT("Exploit"),
  MOVEMENT("Movement"),
  PLAYER("Player"),
  RENDER("Render");

  private final String display;

  ModuleCategory(String display) {
    this.display = display;
  }

  public String display() {
    return display;
  }
}

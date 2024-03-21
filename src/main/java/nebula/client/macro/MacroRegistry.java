package nebula.client.macro;

import nebula.client.Nebula;
import nebula.client.config.ConfigLoader;
import nebula.client.listener.bus.Listener;
import nebula.client.listener.bus.Subscribe;
import nebula.client.listener.event.io.EventKeyInput;
import nebula.client.listener.event.io.EventMouseInput;
import nebula.client.util.registry.Registry;
import net.minecraft.client.Minecraft;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.input.Keyboard.KEY_NONE;

/**
 * @author Gavin
 * @since 08/09/23
 */
@SuppressWarnings("unused")
public class MacroRegistry implements Registry<Macro> {

  /**
   * The minecraft game instance
   */
  private final Minecraft mc = Minecraft.getMinecraft();

  /**
   * A map of the macro identifier and its instance
   */
  private final Map<String, Macro> macroIdMap = new HashMap<>();

  @Override
  public void init() {
    Nebula.BUS.subscribe(this);
    ConfigLoader.add(new MacroConfig());
  }

  @Subscribe
  private final Listener<EventKeyInput> keyInput = event -> {
    if (event.key() <= KEY_NONE || mc.currentScreen != null) return;

    for (Macro macro : macroIdMap.values()) {
      if (macro.key() > KEY_NONE
        && macro.key() == event.key()
        && macro.type() == MacroType.KEYBOARD) {

        macro.setEnabled(!macro.toggled());
      }
    }
  };

  @Subscribe
  private final Listener<EventMouseInput> mouseInput = event -> {
    if (event.button() <= -1 || mc.currentScreen != null) return;

    for (Macro macro : macroIdMap.values()) {
      if (macro.key() > -1
        && macro.key() == event.button()
        && macro.type() == MacroType.MOUSE) {

        macro.setEnabled(!macro.toggled());
      }
    }
  };

  @Override
  public void add(Macro... elements) {

  }

  @Override
  public void remove(Macro... elements) {

  }

  public void add(String id, Macro macro) {
    macroIdMap.put(id, macro);
  }

  public Macro get(String name) {
    return macroIdMap.get(name);
  }

  @Override
  public Collection<Macro> values() {
    return macroIdMap.values();
  }

  public Map<String, Macro> mapped() {
    return macroIdMap;
  }
}

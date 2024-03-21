package nebula.client.module.impl.render.clickgui;

import nebula.client.gui.module.nebula.ModuleConfigScreen;
import nebula.client.module.Module;
import nebula.client.module.ModuleMeta;
import nebula.client.util.value.Setting;
import nebula.client.util.value.SettingMeta;

import static org.lwjgl.input.Keyboard.KEY_RSHIFT;

/**
 * @author Gavin
 * @since 08/09/23
 */
@SuppressWarnings("unused")
@ModuleMeta(name = "ClickGUI",
  description = "Displays a configuration menu for modules",
  defaultMacro = KEY_RSHIFT)
public class ClickGUIModule extends Module {

  @SettingMeta("Mode")
  private final Setting<ClickGUIMode> mode = new Setting<>(
    ClickGUIMode.FUTURE);

  @SettingMeta("Pause")
  public final Setting<Boolean> pause = new Setting<>(
    false);

  private ModuleConfigScreen screen;

  @Override
  public void enable() {
    super.enable();

    if (mc.thePlayer == null || mc.theWorld == null) return;
    mc.displayGuiScreen(mode.value().instance(this));
  }
}

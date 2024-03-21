package nebula.client.module.impl.movement.guimove;

import com.google.common.collect.Lists;
import nebula.client.listener.bus.Listener;
import nebula.client.listener.bus.Subscribe;
import nebula.client.listener.event.player.EventUpdate;
import nebula.client.module.Module;
import nebula.client.module.ModuleMeta;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.List;

/**
 * @author Gavin
 * @since 08/09/23
 */
@SuppressWarnings("unused")
@ModuleMeta(name = "GuiMove", description = "Allows you to move around in GUIs")
public class GuiMoveModule extends Module {

  /**
   * A list containing blacklisted GUI screens
   */
  private static final List<Class<? extends GuiScreen>> BLACKLIST = Lists.newArrayList(
    GuiChat.class, GuiContainerCreative.class, GuiRepair.class, GuiEditSign.class);

  private KeyBinding[] moveBinds;

  @Override
  public void enable() {
    super.enable();

    moveBinds = new KeyBinding[] {
      mc.gameSettings.keyBindForward,
      mc.gameSettings.keyBindBack,
      mc.gameSettings.keyBindRight,
      mc.gameSettings.keyBindLeft,
      mc.gameSettings.keyBindSprint
    };
  }

  @Override
  public void disable() {
    super.disable();
    moveBinds = null;
  }

  @Subscribe
  private final Listener<EventUpdate> update = event -> {
    if (moveBinds == null || moveBinds.length == 0) return;

    // if we are not in a GUI or the GUI we're in is blacklisted, do not use
    if (mc.currentScreen == null || BLACKLIST.contains(mc.currentScreen.getClass())) return;

    // for every cached bind, see if the key to that KeyBinding is pressed, handling mouse binds as well
    for (KeyBinding keyBinding : moveBinds) {
      int keyCode = keyBinding.getKeyCode();
      if (keyCode < -100) {
        int mouseButton = 100 + keyCode;
        keyBinding.pressed = Mouse.isButtonDown(mouseButton);
      } else {
        keyBinding.pressed = Keyboard.isKeyDown(keyCode);
      }
    }
  };
}

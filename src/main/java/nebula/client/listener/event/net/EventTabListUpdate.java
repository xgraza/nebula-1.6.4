package nebula.client.listener.event.net;

import net.minecraft.client.gui.GuiPlayerInfo;

/**
 * @author Gavin
 * @since 08/24/23
 */
public record EventTabListUpdate(Action action, GuiPlayerInfo info) {
  public enum Action {
    ADD, REMOVE
  }
}

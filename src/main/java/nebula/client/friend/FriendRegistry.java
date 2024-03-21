package nebula.client.friend;

import nebula.client.Nebula;
import nebula.client.config.ConfigLoader;
import nebula.client.listener.bus.Listener;
import nebula.client.listener.bus.Subscribe;
import nebula.client.listener.event.render.overlay.EventRenderTabPlayerName;
import nebula.client.util.registry.Registry;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;

import java.util.*;

/**
 * @author Gavin
 * @since 08/13/23
 */
public class FriendRegistry implements Registry<Friend> {

  private final Minecraft mc = Minecraft.getMinecraft();

  /**
   * A list containing the friend object
   */
  private final List<Friend> friendList = new ArrayList<>();

  @Override
  public void init() {
    Nebula.BUS.subscribe(this);
    ConfigLoader.add(new FriendConfig());
  }

  @Subscribe
  private final Listener<EventRenderTabPlayerName> renderTabPlayerNameListener = event -> {
    if (isFriend(event.getContent())) {
      event.setContent(EnumChatFormatting.AQUA + event.getContent());
    }
  };

  @Override
  public void add(Friend... elements) {
    Collections.addAll(friendList, elements);
  }

  @Override
  public void remove(Friend... elements) {
    for (Friend friend : elements) {
      friendList.remove(friend);
    }
  }

  public void clear() {
    friendList.clear();
  }

  @Override
  public Collection<Friend> values() {
    return friendList;
  }

  public boolean isFriend(EntityPlayer player) {
    if (player == null) return false;
    return isFriend(player.getCommandSenderName());
  }

  public boolean isFriend(final String name) {
    if (name.equals(mc.getSession().getUsername())) {
      return true;
    }

    return get(name) != null;
  }

  /**
   * Gets a friend by their alias
   * @param name the name/alias
   * @return the friend object or null
   */
  public Friend get(String name) {
    // lol
    for (Friend friend : friendList) {
      if (friend.username().equalsIgnoreCase(name)
        || (friend.alias() != null
          && friend.alias().equalsIgnoreCase(name))) return friend;
    }

    return null;
  }
}

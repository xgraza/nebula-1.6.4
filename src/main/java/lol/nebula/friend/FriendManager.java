package lol.nebula.friend;

import io.netty.util.internal.ConcurrentSet;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Set;

/**
 * @author aesthetical
 * @since 05/01/23
 */
public class FriendManager {

    private final Set<String> friendList = new ConcurrentSet<>();

    public FriendManager() {
        new FriendConfig(this);
    }

    /**
     * Adds a friend
     * @param name the friend's game profile name
     */
    public void addFriend(String name) {
        friendList.add(name);
    }

    /**
     * Adds a friend
     * @param player the player entity
     */
    public void addFriend(EntityPlayer player) {
        addFriend(player.getGameProfile().getName());
    }

    /**
     * Removes a friend
     * @param name the friend's game profile name
     */
    public void removeFriend(String name) {
        friendList.remove(name);
    }

    /**
     * Removes a friend
     * @param player the player entity
     */
    public void removeFriend(EntityPlayer player) {
        removeFriend(player.getGameProfile().getName());
    }

    /**
     * Checks if a player is a friend
     * @param name the friend's game profile name
     * @return if the player has been added
     */
    public boolean isFriend(String name) {
        return friendList.contains(name);
    }

    /**
     * Checks if a player is a friend
     * @param player the player entity
     * @return if the player has been added
     */
    public boolean isFriend(EntityPlayer player) {
        return isFriend(player.getGameProfile().getName());
    }

    /**
     * Gets your friends list
     * @return a set of friends
     */
    public Set<String> getFriendList() {
        return friendList;
    }
}

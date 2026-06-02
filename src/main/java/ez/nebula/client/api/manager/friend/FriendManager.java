package ez.nebula.client.api.manager.friend;

import ez.nebula.client.impl.config.FriendConfig;
import net.minecraft.entity.player.EntityPlayer;
import ez.nebula.client.core.Nebula;
import ez.nebula.client.api.manager.ITypedManager;

import java.util.LinkedList;
import java.util.List;

/**
 * @author xgraza
 * @since 03/26/25
 */
public final class FriendManager implements ITypedManager<String>
{
    private final List<String> friendList = new LinkedList<>();

    @Override
    public void init()
    {
        Nebula.INSTANCE.getConfigurationManager()
                .addConfiguration(new FriendConfig(this));
    }

    public boolean isFriend(final String name)
    {
        return friendList.contains(name);
    }

    public boolean isFriend(final EntityPlayer player)
    {
        return isFriend(player.getGameProfile().getName());
    }

    public void addFriend(final String name)
    {
        if (!friendList.contains(name))
        {
            friendList.add(name);
        }
    }

    public void removeFriend(final String name)
    {
        friendList.remove(name);
    }

    @Override
    public List<String> getAll()
    {
        return friendList;
    }
}

package ez.nebula.client.api.manager.friend;

import ez.nebula.client.Nebula;
import ez.nebula.client.api.manager.ITypedManager;
import ez.nebula.client.impl.config.FriendConfig;
import net.minecraft.entity.player.EntityPlayer;

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
        Nebula.CONFIGS.register(new FriendConfig(this));
    }

    public boolean has(final String name)
    {
        return friendList.contains(name);
    }

    public boolean has(final EntityPlayer player)
    {
        return has(player.getGameProfile().getName());
    }

    public void add(final String name)
    {
        if (!friendList.contains(name))
        {
            friendList.add(name);
        }
    }

    public void remove(final String name)
    {
        friendList.remove(name);
    }

    @Override
    public List<String> getAll()
    {
        return friendList;
    }
}

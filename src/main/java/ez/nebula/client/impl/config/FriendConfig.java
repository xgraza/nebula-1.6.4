package ez.nebula.client.impl.config;

import ez.nebula.client.Nebula;
import ez.nebula.client.api.config.IConfig;
import ez.nebula.client.api.manager.friend.FriendManager;

import java.io.File;
import java.util.StringJoiner;

/**
 * @author xgraza
 * @since 03/26/25
 */
public final class FriendConfig implements IConfig
{
    private final FriendManager manager;

    public FriendConfig(final FriendManager manager)
    {
        this.manager = manager;
    }

    @Override
    public String write()
    {
        final StringJoiner joiner = new StringJoiner("\n");
        for (final String friend : manager.getAll())
        {
            joiner.add(friend);
        }
        return joiner.toString();
    }

    @Override
    public void read(final String data)
    {
        if (data == null || data.isEmpty())
        {
            return;
        }
        manager.getAll().clear();
        for (final String line : data.split("\n"))
        {
            if (line.isEmpty())
            {
                continue;
            }
            manager.add(line.trim());
        }
    }

    @Override
    public File getLocation()
    {
        return new File(Nebula.NEBULA_ROOT, "friends.txt");
    }
}

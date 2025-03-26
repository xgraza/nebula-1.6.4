package us.nebula.api.manager.friend;

import us.nebula.Nebula;
import us.nebula.api.config.IConfiguration;

import java.io.File;
import java.util.StringJoiner;

/**
 * @author xgraza
 * @since 03/26/25
 */
public final class FriendConfig implements IConfiguration
{
    private final FriendManager manager;

    public FriendConfig(final FriendManager manager)
    {
        this.manager = manager;
    }

    @Override
    public String save()
    {
        final StringJoiner joiner = new StringJoiner("\n");
        for (final String friend : manager.getAll())
        {
            joiner.add(friend);
        }
        return joiner.toString();
    }

    @Override
    public void load(final String data)
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
            manager.addFriend(line.trim());
        }
    }

    @Override
    public File getFile()
    {
        return new File(Nebula.INSTANCE.getNebulaRootDir(), "friends.txt");
    }
}

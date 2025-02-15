package us.nebula.api.config;

import us.nebula.Nebula;
import us.nebula.util.FileUtil;

import java.io.File;
import java.io.IOException;

/**
 * @author xgraza
 * @since 02/14/25
 */
public final class ConfigurationSaveThread extends Thread
{
    private final ConfigurationManager manager;

    public ConfigurationSaveThread(final ConfigurationManager manager)
    {
        this.manager = manager;
        setName("Configuration Save Thread");
    }

    @Override
    public void run()
    {
        for (final IConfiguration configuration : manager.getConfigList())
        {
            final File file = configuration.getFile();
            if (!file.getParentFile().exists())
            {
                file.getParentFile().mkdir();
            }
            if (!file.exists())
            {
                try
                {
                    file.createNewFile();
                } catch (final IOException e)
                {
                    Nebula.INSTANCE.getLogger().error(e);
                    continue;
                }
            }

            final String data = configuration.save();
            if (data == null || data.isEmpty())
            {
                Nebula.INSTANCE.getLogger().warn("Save data for {} was empty", file);
                continue;
            }

            try
            {
                FileUtil.save(file, data);
            } catch (final IOException e)
            {
                Nebula.INSTANCE.getLogger().error(e);
            }
        }
    }
}

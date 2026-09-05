package ez.nebula.client.api.config;

import ez.nebula.client.Nebula;
import ez.nebula.client.api.manager.IManager;
import ez.nebula.client.impl.config.ClientSettingConfig;
import ez.nebula.client.util.io.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xgraza
 * @since 02/14/25
 */
public final class ConfigManager implements IManager
{
    private final List<IConfig> configList = new ArrayList<>();

    @Override
    public void init()
    {
        Runtime.getRuntime().addShutdownHook(
                new ConfigSaveThread(this));
        addConfiguration(new ClientSettingConfig());
        try
        {
            loadConfigs();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public boolean saveConfig(final IConfig configuration)
    {
        final File file = configuration.getFile();
        if (!file.getParentFile().exists())
        {
            if (!file.getParentFile().mkdir())
            {
                Nebula.INSTANCE.getLogger().error("Failed to create parent directory {}", file);
                return false;
            }
            Nebula.INSTANCE.getLogger().info("Created parent directory {}", file);
        }
        if (!file.exists())
        {
            try
            {
                if (!file.createNewFile())
                {
                    Nebula.INSTANCE.getLogger().error("Failed to create file {}", file);
                }
            } catch (final IOException e)
            {
                Nebula.INSTANCE.getLogger().error(e);
                return false;
            }
        }

        final String data = configuration.save();
        if (data == null || data.isEmpty())
        {
            Nebula.INSTANCE.getLogger().warn("Save data for {} was empty", file);
            return false;
        }

        try
        {
            FileUtil.save(file, data);
        } catch (final IOException e)
        {
            Nebula.INSTANCE.getLogger().error(e);
        }
        return false;
    }

    private void loadConfigs() throws IOException
    {
        Nebula.INSTANCE.getLogger().info("Loading {} configs...", configList.size());
        for (final IConfig configuration : configList)
        {
            final File file = configuration.getFile();
            if (!file.exists())
            {
                Nebula.INSTANCE.getLogger().warn("Configuration file {} does not exist", file);
                continue;
            }
            final String data = FileUtil.read(file);
            if (!data.isEmpty())
            {
                configuration.load(data);
            }
        }
    }

    public void addConfiguration(final IConfig configuration)
    {
        configList.add(configuration);
    }

    public List<IConfig> getConfigList()
    {
        return configList;
    }
}

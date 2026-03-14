package us.nebula.client.api.config;

import us.nebula.client.Nebula;
import us.nebula.client.api.manager.IManager;
import us.nebula.client.impl.config.ClientSettingConfig;
import us.nebula.client.util.io.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xgraza
 * @since 02/14/25
 */
public final class ConfigurationManager implements IManager
{
    private final List<IConfiguration> configList = new ArrayList<>();

    @Override
    public void init()
    {
        Runtime.getRuntime().addShutdownHook(
                new ConfigurationSaveThread(this));
        addConfiguration(new ClientSettingConfig());
        try
        {
            loadConfigs();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private void loadConfigs() throws IOException
    {
        Nebula.INSTANCE.getLogger().info("Loading {} configs...", configList.size());
        for (final IConfiguration configuration : configList)
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

    public void addConfiguration(final IConfiguration configuration)
    {
        configList.add(configuration);
    }

    public List<IConfiguration> getConfigList()
    {
        return configList;
    }
}

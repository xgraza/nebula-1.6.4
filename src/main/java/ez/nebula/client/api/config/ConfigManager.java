package ez.nebula.client.api.config;

import ez.nebula.client.api.manager.IManager;
import ez.nebula.client.impl.config.ClientSettingConfig;
import ez.nebula.client.util.io.FileUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    private static final Logger LOGGER = LogManager.getLogger("Configs");
    
    private final List<IConfig> configList = new ArrayList<>();

    @Override
    public void init()
    {
        Runtime.getRuntime().addShutdownHook(
                new ConfigSaveThread(this));
        register(new ClientSettingConfig());
        try
        {
            load();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public boolean save(final IConfig configuration)
    {
        final File file = configuration.getFile();
        if (!file.getParentFile().exists())
        {
            if (!file.getParentFile().mkdir())
            {
                LOGGER.error("Failed to create parent directory {}", file);
                return false;
            }
            LOGGER.info("Created parent directory {}", file);
        }
        if (!file.exists())
        {
            try
            {
                if (!file.createNewFile())
                {
                    LOGGER.error("Failed to create file {}", file);
                }
            } catch (final IOException e)
            {
                LOGGER.error(e);
                return false;
            }
        }

        final String data = configuration.save();
        if (data == null || data.isEmpty())
        {
            LOGGER.warn("Save data for {} was empty", file);
            return false;
        }

        try
        {
            FileUtil.save(file, data);
        } catch (final IOException e)
        {
            LOGGER.error(e);
        }
        return false;
    }

    private void load() throws IOException
    {
        LOGGER.info("Loading {} configs...", configList.size());
        for (final IConfig configuration : configList)
        {
            final File file = configuration.getFile();
            if (!file.exists())
            {
                LOGGER.warn("Configuration file {} does not exist", file);
                continue;
            }
            final String data = FileUtil.read(file);
            if (!data.isEmpty())
            {
                configuration.load(data);
            }
        }
    }

    public void register(final IConfig configuration)
    {
        configList.add(configuration);
    }

    public List<IConfig> getConfigs()
    {
        return configList;
    }
}

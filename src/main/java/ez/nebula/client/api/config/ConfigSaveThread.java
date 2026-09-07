package ez.nebula.client.api.config;

import ez.nebula.client.impl.config.ModuleConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * @author xgraza
 * @since 02/14/25
 */
public final class ConfigSaveThread extends Thread
{
    private static final Logger LOGGER = LogManager.getLogger("Config Save Thread");

    private final ConfigManager manager;

    public ConfigSaveThread(final ConfigManager manager)
    {
        this.manager = manager;
        setName("Configuration Save Thread");
    }

    @Override
    public void run()
    {
        try
        {
            ModuleConfig.saveConfig("default");
            LOGGER.info("Saved module config");
        } catch (final IOException e)
        {
            LOGGER.error("Could not save default module config!", e);
        }
        for (final IConfig configuration : manager.getConfigList())
        {
            manager.saveConfig(configuration);
        }
    }
}

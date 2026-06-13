package ez.nebula.client.api.config;

import ez.nebula.client.core.Nebula;
import ez.nebula.client.impl.config.ModuleConfig;
import ez.nebula.client.util.io.FileUtil;

import java.io.File;
import java.io.IOException;

/**
 * @author xgraza
 * @since 02/14/25
 */
public final class ConfigSaveThread extends Thread
{
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
            Nebula.INSTANCE.getLogger().info("Saved module config");
        } catch (final IOException e)
        {
            Nebula.INSTANCE.getLogger().error(e);
        }
        for (final IConfig configuration : manager.getConfigList())
        {
            manager.saveConfig(configuration);
        }
    }
}

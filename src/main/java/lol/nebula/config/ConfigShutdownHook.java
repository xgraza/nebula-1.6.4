package lol.nebula.config;

import lol.nebula.Nebula;
import lol.nebula.util.math.timing.Timer;

import java.util.List;

import static lol.nebula.module.ModuleManager.DEFAULT_CONFIG;

/**
 * @author aesthetical
 * @since 04/27/23
 */
public class ConfigShutdownHook extends Thread {
    private final ConfigManager configs;

    /**
     * Creates a new config shutdown hook
     * @param configs the config manager
     */
    public ConfigShutdownHook(ConfigManager configs) {
        this.configs = configs;
    }

    @Override
    public void run() {

        List<Config> configList = configs.getConfigs();
        if (!configList.isEmpty()) {

            Timer timer = new Timer();

            for (Config config : configList) {
                try {
                    config.save();
                    System.out.printf("Saved config %s\n", config.getFile());
                } catch (Exception e) {
                    System.out.printf("Failed to save %s\n", config.getFile());
                    e.printStackTrace();
                }
            }

            System.out.printf("Saved %s configs in %sms\n", configList.size(), timer.getTimeElapsedMS());
        }

        System.out.println(Nebula.getInstance().getModules().saveModules(DEFAULT_CONFIG));
    }
}

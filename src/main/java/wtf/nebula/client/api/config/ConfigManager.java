package wtf.nebula.client.api.config;

import wtf.nebula.client.core.Launcher;
import wtf.nebula.client.utils.io.FileUtils;

import java.util.ArrayList;
import java.util.List;

public class ConfigManager {
    public static final List<Config> configs = new ArrayList<>();

    public static void loadConfigurations() {
        Launcher.LOGGER.info("Loading configurations...");
        configs.forEach((config) -> config.load(FileUtils.read(config.getFile())));
    }

    public static void saveConfigurations() {
        Launcher.LOGGER.info("Saving configurations...");
        configs.forEach(Config::save);
    }
}

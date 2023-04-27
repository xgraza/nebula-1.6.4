package lol.nebula.config;

import lol.nebula.Nebula;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author aesthetical
 * @since 04/27/23
 */
public class ConfigManager {

    public static final File ROOT = new File(Minecraft.getMinecraft().mcDataDir, "nebula");

    /**
     * The list of configs to load and save
     */
    private final List<Config> configList = new ArrayList<>();

    public ConfigManager() {

        if (!ROOT.exists()) {
            boolean result = ROOT.mkdir();
            Nebula.getLogger().info("Created directory {} {}", ROOT, result ? "successfully" : "unsuccessfully");
        }

        // add a shutdown hook
        Runtime.getRuntime().addShutdownHook(new ConfigShutdownHook(this));
    }

    /**
     * Adds a config to be loaded and saved by this manager
     * @param config the {@link Config} object
     */
    public void addConfig(Config config) {
        configList.add(config);
    }

    /**
     * Gets the list of configs registered
     * @return the config list
     */
    public List<Config> getConfigs() {
        return configList;
    }
}

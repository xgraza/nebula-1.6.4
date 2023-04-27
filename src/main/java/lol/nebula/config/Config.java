package lol.nebula.config;

import lol.nebula.Nebula;

import java.io.File;
import java.io.IOException;

/**
 * @author aesthetical
 * @since 04/27/23
 */
public abstract class Config {

    private final File file;

    public Config(File file) {
        this.file = file;

        if (!file.exists()) {
            try {
                boolean result = file.createNewFile();
                Nebula.getLogger().info("Created file {} {}", file, result ? "successfully" : "unsuccessfully");
            } catch (IOException e) {
                Nebula.getLogger().error("Failed to create config file {}", file);
                e.printStackTrace();
            }
        }

        // automatically add the class
        Nebula.getInstance().getConfigs().addConfig(this);
    }

    /**
     * Saves this config
     */
    public abstract void save();

    /**
     * Loads this config
     */
    public abstract void load();

    /**
     * Gets the file for this config
     * @return the config {@link File} object
     */
    public File getFile() {
        return file;
    }
}

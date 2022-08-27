package wtf.nebula.client.api.config;

import wtf.nebula.client.core.Launcher;
import wtf.nebula.client.utils.io.FileUtils;

import java.io.File;
import java.io.IOException;

public abstract class Config {
    private final File file;

    public Config(String name) {
        file = new File(FileUtils.CLIENT_DIRECTORY, name);
        if (!name.contains(".")) {
            file.mkdir();
        } else {
            try {
                file.createNewFile();
            } catch (IOException e) {
                if (Launcher.VERSION.isDev()) {
                    e.printStackTrace();
                } else {
                    Launcher.LOGGER.info("Could not create file {}", file);
                }
            }
        }

        ConfigManager.configs.add(this);
    }

    public abstract void load(String element);
    public abstract void save();

    public File getFile() {
        return file;
    }
}

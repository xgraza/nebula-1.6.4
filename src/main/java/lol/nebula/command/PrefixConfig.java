package lol.nebula.command;

import lol.nebula.Nebula;
import lol.nebula.config.Config;
import lol.nebula.config.ConfigManager;
import lol.nebula.util.system.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * @author aesthetical
 * @since 04/28/23
 */
public class PrefixConfig extends Config {
    private final CommandManager commands;

    public PrefixConfig(CommandManager commands) {
        super(new File(ConfigManager.ROOT, "prefix.txt"));
        this.commands = commands;
    }

    @Override
    public void save() {
        try {
            FileUtils.write(getFile(), commands.getCommandPrefix());
        } catch (IOException e) {
            Nebula.getLogger().error("Failed to save command prefix");
            e.printStackTrace();
        }
    }

    @Override
    public void load() {
        // do not try to read a non-existent file
        if (!getFile().exists()) return;

        String content;
        try {
            content = FileUtils.read(getFile());
        } catch (IOException e) {
            Nebula.getLogger().error("Failed to read command prefix");
            e.printStackTrace();

            return;
        }

        // do not try to parse an empty file
        if (content.isEmpty()) return;

        // set the new command prefix
        commands.setCommandPrefix(String.valueOf(content.trim().charAt(0)), false);
    }
}

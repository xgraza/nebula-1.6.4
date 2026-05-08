package us.nebula.client.cheat;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import us.nebula.client.Nebula;
import us.nebula.client.util.io.FileUtil;

import java.io.File;
import java.io.IOException;

/**
 * @author xgraza
 * @since 03/03/25
 */
public final class CheatConfig
{
    public static final File CHEAT_CONFIG_DIR = new File(
            Nebula.INSTANCE.getNebulaRootDir(), "configs");

    static
    {
        if (!CHEAT_CONFIG_DIR.exists())
        {
            if (!CHEAT_CONFIG_DIR.mkdir())
            {
                throw new RuntimeException("Failed to create " + CHEAT_CONFIG_DIR.getAbsolutePath());
            }
            Nebula.INSTANCE.getLogger().info("Created {} successfully", CHEAT_CONFIG_DIR.getAbsolutePath());
        }
    }

    public static void saveConfig(final String configName) throws IOException
    {
        final File file = new File(CHEAT_CONFIG_DIR, configName + ".cfg");
        final JsonObject object = new JsonObject();
        for (final Cheat cheat : Nebula.INSTANCE.getCheatManager().getAll())
        {
            object.add(cheat.getManifest().name(), cheat.toJSON());
        }
        final String data = FileUtil.GSON.toJson(object);
        FileUtil.save(file, data);
    }

    public static void loadConfig(final String configName) throws IOException
    {
        final File file = new File(CHEAT_CONFIG_DIR, configName + ".cfg");
        if (!file.exists())
        {
            if (!file.createNewFile())
            {
                throw new RuntimeException("failed to create config file");
            }
        }
        final String data = FileUtil.read(file);
        if (!data.isEmpty())
        {
            final JsonElement element = FileUtil.JSON_PARSER.parse(data);
            if (element == null || !element.isJsonObject())
            {
                return;
            }
            final JsonObject object = element.getAsJsonObject();
            for (final Cheat cheat : Nebula.INSTANCE.getCheatManager().getAll())
            {
                final String cheatName = cheat.getManifest().name();
                if (!object.has(cheatName))
                {
                    continue;
                }
                try
                {
                    cheat.fromJSON(object.get(cheatName));
                } catch (final Exception e)
                {
                    Nebula.INSTANCE.getLogger().error(e);
                }
            }
        }
    }
}

package us.nebula.api.manager.cheat;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import us.nebula.Nebula;
import us.nebula.api.config.IConfiguration;
import us.nebula.util.FileUtil;

import java.io.File;

/**
 * @author xgraza
 * @since 03/03/25
 */
public final class CheatConfig implements IConfiguration
{
    private static final File CHEAT_CONFIG_DIR = new File(
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

    private final CheatManager manager;
    private final String configName;

    public CheatConfig(final CheatManager manager, final String configName)
    {
        this.manager = manager;
        this.configName = configName;
    }

    @Override
    public String save()
    {
        final JsonObject object = new JsonObject();
        for (final Cheat cheat : manager.getAll())
        {
            object.add(cheat.getManifest().name(), cheat.toJSON());
        }
        return FileUtil.GSON.toJson(object);
    }

    @Override
    public void load(final String data)
    {
        final JsonElement element = FileUtil.JSON_PARSER.parse(data);
        if (element == null || !element.isJsonObject())
        {
            return;
        }
        final JsonObject object = element.getAsJsonObject();
        for (final Cheat cheat : manager.getAll())
        {
            final String cheatName = cheat.getManifest().name();
            if (!object.has(cheatName))
            {
                continue;
            }
            cheat.fromJSON(object.get(cheatName));
        }
    }

    @Override
    public File getFile()
    {
        return new File(CHEAT_CONFIG_DIR, configName + ".cfg");
    }
}

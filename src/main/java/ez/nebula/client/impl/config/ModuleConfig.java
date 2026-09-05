package ez.nebula.client.impl.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.Nebula;
import ez.nebula.client.util.io.FileUtil;

import java.io.File;
import java.io.IOException;

/**
 * @author xgraza
 * @since 03/03/25
 */
public final class ModuleConfig
{
    public static final File MODULE_CONFIG_DIR = new File(
            Nebula.INSTANCE.getNebulaRootDir(), "configs");

    static
    {
        if (!MODULE_CONFIG_DIR.exists())
        {
            if (!MODULE_CONFIG_DIR.mkdir())
            {
                throw new RuntimeException("Failed to create " + MODULE_CONFIG_DIR.getAbsolutePath());
            }
            Nebula.INSTANCE.getLogger().info("Created {} successfully", MODULE_CONFIG_DIR.getAbsolutePath());
        }
    }

    public static void saveConfig(final String configName) throws IOException
    {
        final File file = new File(MODULE_CONFIG_DIR, configName + ".cfg");
        final JsonObject object = new JsonObject();
        for (final Module module : Nebula.INSTANCE.getModuleManager().getAll())
        {
            object.add(module.getManifest().name(), module.toJSON());
        }
        final String data = FileUtil.GSON.toJson(object);
        FileUtil.save(file, data);
    }

    public static void loadConfig(final String configName) throws IOException
    {
        final File file = new File(MODULE_CONFIG_DIR, configName + ".cfg");
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
            for (final Module module : Nebula.INSTANCE.getModuleManager().getAll())
            {
                final String moduleName = module.getManifest().name();
                if (!object.has(moduleName))
                {
                    continue;
                }
                try
                {
                    module.fromJSON(object.get(moduleName));
                } catch (final Exception e)
                {
                    Nebula.INSTANCE.getLogger().error(e);
                }
            }
        }
    }
}

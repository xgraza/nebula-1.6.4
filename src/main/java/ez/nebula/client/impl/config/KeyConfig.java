package ez.nebula.client.impl.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ez.nebula.client.Nebula;
import ez.nebula.client.api.config.IConfig;
import ez.nebula.client.api.manager.key.KeyManager;
import ez.nebula.client.util.io.FileUtil;

import java.io.File;

/**
 * @author xgraza
 * @since 02/15/25
 */
public final class KeyConfig implements IConfig
{
    private final KeyManager manager;

    public KeyConfig(final KeyManager manager)
    {
        this.manager = manager;
    }

    @Override
    public String save()
    {
        final JsonObject object = new JsonObject();
        manager.getKeyIdMap().forEach((k, v)
                -> object.add(k, v.toJSON()));
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
        for (final String id : manager.getKeyIdMap().keySet())
        {
            final JsonElement keyElement = object.get(id);
            if (keyElement == null)
            {
                continue;
            }
            if (!keyElement.isJsonObject())
            {
                throw new RuntimeException("must be JsonObject");
            }
            manager.getReference(id).fromJSON(keyElement);
        }
    }

    @Override
    public File getFile()
    {
        return new File(Nebula.NEBULA_ROOT, "keys.json");
    }
}

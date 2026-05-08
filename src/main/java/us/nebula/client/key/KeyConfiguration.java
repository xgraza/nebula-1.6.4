package us.nebula.client.key;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import us.nebula.client.Nebula;
import us.nebula.client.config.IConfiguration;
import us.nebula.client.util.io.FileUtil;

import java.io.File;

/**
 * @author xgraza
 * @since 02/15/25
 */
public final class KeyConfiguration implements IConfiguration
{
    private final KeyManager manager;

    public KeyConfiguration(final KeyManager manager)
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
        return new File(Nebula.INSTANCE.getNebulaRootDir(), "keys.json");
    }
}

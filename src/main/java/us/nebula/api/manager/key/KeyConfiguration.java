package us.nebula.api.manager.key;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import us.nebula.Nebula;
import us.nebula.api.config.IConfiguration;
import us.nebula.util.FileUtil;

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
        manager.getKeyIdMap().forEach((k, v) -> object.add(k, v.toJSON()));
        return object.toString();
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
        return new File(Nebula.INSTANCE.getNebulaRootDir(), "keys.txt");
    }
}

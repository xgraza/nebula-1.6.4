package us.nebula.client.api.manager.hud;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import us.nebula.client.Nebula;
import us.nebula.client.api.config.IConfiguration;
import us.nebula.client.util.io.FileUtil;

import java.io.File;

/**
 * @author xgraza
 * @since 3/23/26
 */
public final class HUDConfig implements IConfiguration
{
    private final HUDManager manager;

    public HUDConfig(final HUDManager manager)
    {
        this.manager = manager;
    }

    @Override
    public String save()
    {
        final JsonObject object = new JsonObject();
        for (final HUDElement hudElement : manager.getAll())
        {
            object.add(hudElement.getManifest().name(), hudElement.toJSON());
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
        for (final HUDElement hudElement : manager.getAll())
        {
            if (!object.has(hudElement.getManifest().name()))
            {
                continue;
            }
            final JsonElement jsonElement = object.get(hudElement.getManifest().name());
            if (jsonElement != null && jsonElement.isJsonObject())
            {
                hudElement.fromJSON(jsonElement);
            }
        }
    }

    @Override
    public File getFile()
    {
        return new File(Nebula.INSTANCE.getNebulaRootDir(), "hud_elements.json");
    }
}

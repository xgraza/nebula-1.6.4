package ez.nebula.client.impl.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ez.nebula.client.Nebula;
import ez.nebula.client.api.config.IConfig;
import ez.nebula.client.api.manager.hud2.HUDElement;
import ez.nebula.client.api.manager.hud2.HUDElementManager;
import ez.nebula.client.util.io.FileUtil;

import java.io.File;

/**
 * @author xgraza
 * @since 9/6/26
 */
public final class HUD2Config implements IConfig
{
    private final HUDElementManager manager;

    private boolean loaded;

    public HUD2Config(final HUDElementManager manager)
    {
        this.manager = manager;
    }

    @Override
    public String save()
    {
        final JsonObject object = new JsonObject();
        object.addProperty("height", manager.prevHeight);
        object.addProperty("width", manager.prevWidth);
        object.addProperty("scale", manager.prevScale);
        for (final HUDElement element : manager.getAll())
        {
            object.add(element.getManifest().value(), element.toJSON());
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

        if (object.has("height") && object.has("width") && object.has("scale"))
        {
            manager.prevHeight = object.get("height").getAsDouble();
            manager.prevWidth = object.get("width").getAsDouble();
            manager.prevScale = object.get("scale").getAsInt();
        }

        loaded = true;
    }

    @Override
    public File getFile()
    {
        return new File(Nebula.INSTANCE.getNebulaRootDir(), "hud.json");
    }

    public boolean isLoaded()
    {
        return loaded;
    }
}

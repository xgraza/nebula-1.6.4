package ez.nebula.client.impl.config;

import com.google.gson.JsonObject;
import ez.nebula.client.Nebula;
import ez.nebula.client.api.config.type.JSONConfig;
import ez.nebula.client.api.manager.hud2.HUDElement;
import ez.nebula.client.api.manager.hud2.HUDElementManager;

import java.io.File;

/**
 * @author xgraza
 * @since 9/6/26
 */
public final class HUD2Config extends JSONConfig<JsonObject>
{
    private final HUDElementManager manager;

    private boolean loaded;

    public HUD2Config(final HUDElementManager manager)
    {
        this.manager = manager;
    }

    @Override
    public JsonObject writeJSON()
    {
        final JsonObject object = new JsonObject();
        object.addProperty("height", manager.prevHeight);
        object.addProperty("width", manager.prevWidth);
        object.addProperty("scale", manager.prevScale);
        for (final HUDElement element : manager.getAll())
        {
            object.add(element.getManifest().value(), element.toJSON());
        }
        return object;
    }

    @Override
    public void readJSON(final JsonObject json)
    {
        if (json.has("height") && json.has("width") && json.has("scale"))
        {
            manager.prevHeight = json.get("height").getAsDouble();
            manager.prevWidth = json.get("width").getAsDouble();
            manager.prevScale = json.get("scale").getAsInt();
        }

        loaded = true;
    }

    @Override
    public File getLocation()
    {
        return new File(Nebula.NEBULA_ROOT, "hud.json");
    }

    public boolean isLoaded()
    {
        return loaded;
    }
}

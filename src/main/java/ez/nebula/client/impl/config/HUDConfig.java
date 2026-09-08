package ez.nebula.client.impl.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ez.nebula.client.Nebula;
import ez.nebula.client.api.config.type.JSONConfig;
import ez.nebula.client.api.manager.hud.HUDElement;
import ez.nebula.client.api.manager.hud.HUDManager;
import ez.nebula.client.impl.module.render.HUDModule;
import ez.nebula.client.util.render.gui.Render2D;

import java.io.File;

/**
 * @author xgraza
 * @since 3/23/26
 */
public final class HUDConfig extends JSONConfig<JsonObject>
{
    private final HUDManager manager;

    public HUDConfig(final HUDManager manager)
    {
        this.manager = manager;
    }

    @Override
    public JsonObject writeJSON()
    {
        final JsonObject object = new JsonObject();
        if (Render2D.RESOLUTION != null)
        {
            object.addProperty("savedWidth", Render2D.RESOLUTION.getScaledWidth());
            object.addProperty("savedHeight", Render2D.RESOLUTION.getScaledHeight());
        }
        for (final HUDElement hudElement : manager.getAll())
        {
            object.add(hudElement.getManifest().name(), hudElement.toJSON());
        }
        return object;
    }

    @Override
    public void readJSON(JsonObject json)
    {
        if (json.has("savedWidth"))
        {
            HUDModule.INSTANCE.prevWidth = json.get("savedWidth").getAsInt();
        }
        if (json.has("savedHeight"))
        {
            HUDModule.INSTANCE.prevHeight = json.get("savedHeight").getAsInt();
        }
        for (final HUDElement hudElement : manager.getAll())
        {
            if (!json.has(hudElement.getManifest().name()))
            {
                continue;
            }
            final JsonElement jsonElement = json.get(hudElement.getManifest().name());
            if (jsonElement != null && jsonElement.isJsonObject())
            {
                hudElement.fromJSON(jsonElement);
            }
        }
    }

    @Override
    public File getLocation()
    {
        return new File(Nebula.NEBULA_ROOT, "hud_elements.json");
    }
}

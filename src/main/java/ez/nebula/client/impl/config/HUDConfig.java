package ez.nebula.client.impl.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ez.nebula.client.Nebula;
import ez.nebula.client.api.config.IConfig;
import ez.nebula.client.api.manager.hud.HUDElement;
import ez.nebula.client.api.manager.hud.HUDManager;
import ez.nebula.client.impl.module.render.HUDModule;
import ez.nebula.client.util.io.FileUtil;
import ez.nebula.client.util.render.gui.Render2D;

import java.io.File;

/**
 * @author xgraza
 * @since 3/23/26
 */
public final class HUDConfig implements IConfig
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
        if (Render2D.RESOLUTION != null)
        {
            object.addProperty("savedWidth", Render2D.RESOLUTION.getScaledWidth());
            object.addProperty("savedHeight", Render2D.RESOLUTION.getScaledHeight());
        }
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
        if (object.has("savedWidth"))
        {
            HUDModule.INSTANCE.prevWidth = object.get("savedWidth").getAsInt();
        }
        if (object.has("savedHeight"))
        {
            HUDModule.INSTANCE.prevHeight = object.get("savedHeight").getAsInt();
        }
    }

    @Override
    public File getFile()
    {
        return new File(Nebula.INSTANCE.getNebulaRootDir(), "hud_elements.json");
    }
}

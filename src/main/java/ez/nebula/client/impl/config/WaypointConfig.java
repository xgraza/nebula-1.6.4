package ez.nebula.client.impl.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import ez.nebula.client.api.config.IConfig;
import ez.nebula.client.api.manager.waypoint.Waypoint;
import ez.nebula.client.api.manager.waypoint.WaypointManager;
import ez.nebula.client.core.Nebula;
import ez.nebula.client.util.io.FileUtil;

import java.io.File;

/**
 * @author xgraza
 * @since 6/12/26
 */
public final class WaypointConfig implements IConfig
{
    private final WaypointManager manager;

    public WaypointConfig(final WaypointManager manager)
    {
        this.manager = manager;
    }

    @Override
    public String save()
    {
        final JsonArray array = new JsonArray();
        for (final Waypoint waypoint : manager.getAll())
        {
            array.add(waypoint.toJSON());
        }
        return FileUtil.GSON.toJson(array);
    }

    @Override
    public void load(final String data)
    {
        if (data == null || data.isEmpty())
        {
            return;
        }
        final JsonElement element = FileUtil.JSON_PARSER.parse(data);
        if (element == null || !element.isJsonArray())
        {
            return;
        }
        manager.clearWaypoints();
        final JsonArray array = element.getAsJsonArray();
        for (final JsonElement waypointElement : array)
        {
            if (!waypointElement.isJsonObject())
            {
                continue;
            }
            final Waypoint waypoint = new Waypoint();
            waypoint.fromJSON(waypointElement);
            manager.registerWaypoint(waypoint);
        }
    }

    @Override
    public File getFile()
    {
        return new File(Nebula.INSTANCE.getNebulaRootDir(), "waypoints.json");
    }
}

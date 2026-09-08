package ez.nebula.client.impl.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import ez.nebula.client.Nebula;
import ez.nebula.client.api.config.type.JSONConfig;
import ez.nebula.client.api.manager.waypoint.Waypoint;
import ez.nebula.client.api.manager.waypoint.WaypointManager;

import java.io.File;

/**
 * @author xgraza
 * @since 6/12/26
 */
public final class WaypointConfig extends JSONConfig<JsonArray>
{
    private final WaypointManager manager;

    public WaypointConfig(final WaypointManager manager)
    {
        this.manager = manager;
    }

    @Override public JsonArray writeJSON()
    {
        final JsonArray array = new JsonArray();
        for (final Waypoint waypoint : manager.getAll())
        {
            array.add(waypoint.toJSON());
        }
        return array;
    }

    @Override
    public void readJSON(final JsonArray json)
    {
        manager.clear();
        for (final JsonElement element : json)
        {
            if (!element.isJsonObject())
            {
                continue;
            }
            final Waypoint waypoint = new Waypoint();
            waypoint.fromJSON(element);
            manager.register(waypoint);
        }
    }

    @Override
    public File getLocation()
    {
        return new File(Nebula.NEBULA_ROOT, "waypoints.json");
    }
}

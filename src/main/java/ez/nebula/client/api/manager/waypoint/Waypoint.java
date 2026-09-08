package ez.nebula.client.api.manager.waypoint;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ez.nebula.client.util.io.IJSONSerializable;
import net.minecraft.util.MathHelper;

/**
 * @author xgraza
 * @since 6/12/26
 */
public final class Waypoint implements IJSONSerializable
{
    private String serverIP, name;
    private double x, y, z;
    private int dimension;

    public Waypoint()
    {

    }

    public Waypoint(String serverIP, String name, double x, double y, double z, int dimension)
    {
        this.serverIP = serverIP;
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.dimension = dimension;
    }

    public String getServerIP()
    {
        return serverIP;
    }

    public String getName()
    {
        return name;
    }

    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }

    public double getZ()
    {
        return z;
    }

    public int getDimension()
    {
        return dimension;
    }

    @Override
    public void fromJSON(final JsonElement element)
    {
        if (!element.isJsonObject())
        {
            return;
        }
        final JsonObject object = element.getAsJsonObject();
        if (!object.has("server")
                || !object.has("name")
                || !object.has("x")
                || !object.has("y")
                || !object.has("z"))
        {
            throw new RuntimeException("Waypoint must have server, name, x, y, z fields");
        }
        serverIP = object.get("server").getAsString();
        if (serverIP == null)
        {
            throw new RuntimeException("Waypoint server IP cannot be null!");
        }
        name = object.get("name").getAsString();
        if (name == null)
        {
            throw new RuntimeException("Waypoint name cannot be null!");
        }
        x = object.get("x").getAsDouble();
        y = object.get("y").getAsDouble();
        z = object.get("z").getAsDouble();
        dimension = object.get("dimension").getAsInt();

        if (Double.isNaN(x) || Double.isNaN(y) || Double.isNaN(z))
        {
            throw new RuntimeException("Waypoint XYZ must be a valid number");
        }

        y = MathHelper.clamp_double(y, 0, 256);
        x = MathHelper.clamp_double(x, -30000000, 30000000);
        z = MathHelper.clamp_double(z, -30000000, 30000000);
    }

    @Override
    public JsonElement toJSON()
    {
        final JsonObject object = new JsonObject();
        object.addProperty("server", serverIP);
        object.addProperty("name", name);
        object.addProperty("x", x);
        object.addProperty("y", y);
        object.addProperty("z", z);
        object.addProperty("dimension", dimension);
        return object;
    }
}

package wtf.nebula.repository.impl.waypoint;

import net.minecraft.src.Vec3;

public class Waypoint {
    private final String server;
    private final String name;
    private final Vec3 coordinates;
    private final int dimension;

    public Waypoint(String server, String name, Vec3 coordinates, int dimension) {
        this.server = server;
        this.name = name;
        this.coordinates = coordinates;
        this.dimension = dimension;
    }

    public String getServer() {
        return server;
    }

    public String getName() {
        return name;
    }

    public Vec3 getCoordinates() {
        return coordinates;
    }

    public int getDimension() {
        return dimension;
    }
}

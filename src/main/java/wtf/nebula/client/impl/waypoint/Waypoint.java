package wtf.nebula.client.impl.waypoint;

import net.minecraft.util.Vec3;

public class Waypoint {
    private final Vec3 pos;
    private final String name;
    private final int dimension;

    public Waypoint(Vec3 pos, String name, int dimension) {
        this.pos = pos;
        this.name = name;
        this.dimension = dimension;
    }

    public Vec3 getPos() {
        return pos;
    }

    public String getName() {
        return name;
    }

    public int getDimension() {
        return dimension;
    }
}

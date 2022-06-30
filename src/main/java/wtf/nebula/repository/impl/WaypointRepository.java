package wtf.nebula.repository.impl;

import com.google.gson.*;
import net.minecraft.src.Vec3;
import wtf.nebula.repository.BaseRepository;
import wtf.nebula.repository.Repositories;
import wtf.nebula.repository.Repository;
import wtf.nebula.repository.impl.waypoint.Waypoint;
import wtf.nebula.util.FileUtil;

import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository("Waypoints")
public class WaypointRepository extends BaseRepository<Waypoint> {
    public final Map<String, CopyOnWriteArrayList<Waypoint>> serverWaypoints = new HashMap<>();

    @Override
    public void init() {
        load();
        log.logInfo("Loaded " + serverWaypoints.size() + " server waypoints with a total of " + children.size() + " waypoints.");
    }

    @Override
    public void addChild(Waypoint child) {
        children.add(child);

        CopyOnWriteArrayList<Waypoint> list = serverWaypoints.computeIfAbsent(child.getServer(), (s) -> new CopyOnWriteArrayList<>());
        list.add(child);
        serverWaypoints.put(child.getServer(), list);
    }

    public boolean removeChild(String server, String name) {
        CopyOnWriteArrayList<Waypoint> waypoints = serverWaypoints.getOrDefault(server, null);
        if (waypoints == null || waypoints.isEmpty()) {
            return false;
        }

        boolean removed = waypoints.removeIf((waypoint) -> waypoint.getName().equalsIgnoreCase(name));
        removed &= children.removeIf((waypoint) -> waypoint.getName().equalsIgnoreCase(name));

        return removed;
    }

    public void save() {
        if (!Files.exists(FileUtil.WAYPOINTS)) {
            FileUtil.create(FileUtil.WAYPOINTS);
        }

        JsonObject object = new JsonObject();

        for (Entry<String, CopyOnWriteArrayList<Waypoint>> entry : serverWaypoints.entrySet()) {

            JsonArray serverWaypoints = new JsonArray();

            for (Waypoint waypoint : entry.getValue()) {
                JsonObject wp = new JsonObject();

                wp.addProperty("name", waypoint.getName());

                JsonObject coords = new JsonObject();
                coords.addProperty("x", waypoint.getCoordinates().xCoord);
                coords.addProperty("y", waypoint.getCoordinates().yCoord);
                coords.addProperty("z", waypoint.getCoordinates().zCoord);
                wp.add("coordinates", coords);

                wp.addProperty("dimension", waypoint.getDimension());

                serverWaypoints.add(wp);
            }

            object.add(entry.getKey(), serverWaypoints);
        }

        FileUtil.write(FileUtil.WAYPOINTS, new GsonBuilder().setPrettyPrinting().create().toJson(object));
    }

    public void load() {
        if (!Files.exists(FileUtil.WAYPOINTS)) {
            return;
        }

        String str = FileUtil.read(FileUtil.WAYPOINTS);
        if (str == null || str.isEmpty()) {
            return;
        }

        JsonObject object = new JsonParser().parse(str).getAsJsonObject();

        for (Entry<String, JsonElement> entry : object.entrySet()) {
            if (entry.getValue().isJsonArray()) {
                JsonArray array = entry.getValue().getAsJsonArray();
                for (JsonElement element : array) {
                    if (!element.isJsonObject()) {
                        continue;
                    }

                    JsonObject obj = element.getAsJsonObject();

                    Vec3 coords = Vec3.createVectorHelper(0, 0, 0);
                    if (obj.has("coordinates")) {
                        JsonObject c = obj.get("coordinates").getAsJsonObject();

                        double x = 0.0;
                        double y = 0.0;
                        double z = 0.0;

                        if (c.has("x")) {
                            x = c.get("x").getAsDouble();
                        }

                        if (c.has("y")) {
                            y = c.get("y").getAsDouble();
                        }

                        if (c.has("z")) {
                            z = c.get("z").getAsDouble();
                        }

                        coords = Vec3.createVectorHelper(x, y, z);
                    }

                    addChild(new Waypoint(
                            entry.getKey(),
                            obj.get("name").getAsString(),
                            coords,
                            obj.get("dimension").getAsInt()));
                }
            }
        }
    }

    public static WaypointRepository get() {
        return Repositories.getRepo(WaypointRepository.class);
    }
}

package wtf.nebula.client.impl.waypoint;

import com.google.gson.*;
import me.bush.eventbus.annotation.EventListener;
import me.bush.eventbus.annotation.ListenerPriority;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.Vec3;
import wtf.nebula.client.api.config.Config;
import wtf.nebula.client.core.Nebula;
import wtf.nebula.client.impl.event.impl.render.EventGluPerspective;
import wtf.nebula.client.impl.event.impl.render.EventRender3D;
import wtf.nebula.client.impl.module.visuals.Waypoints;
import wtf.nebula.client.utils.client.Wrapper;
import wtf.nebula.client.utils.io.FileUtils;
import wtf.nebula.client.utils.render.RenderEngine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.lwjgl.opengl.GL11.*;

public class WaypointManager implements Wrapper {
    public final Map<String, List<Waypoint>> waypointMap = new ConcurrentHashMap<>();

    public WaypointManager() {
        Nebula.BUS.subscribe(this);

        new Config("waypoints") {

            @Override
            public void load(String element) {
                if (!getFile().exists()) {
                    return;
                }

                File[] files = getFile().listFiles();
                if (files == null || files.length == 0) {
                    return;
                }

                waypointMap.clear();

                for (File file : files) {
                    if (file.isDirectory()) {
                        continue;
                    }

                    String content = FileUtils.read(file);
                    if (content == null || content.isEmpty()) {
                        continue;
                    }

                    JsonArray arr = new JsonParser().parse(content).getAsJsonArray();
                    for (JsonElement e : arr) {
                        if (!e.isJsonObject()) {
                            continue;
                        }

                        JsonObject obj = e.getAsJsonObject();

                        Vec3 vec = null;
                        String name;
                        int dimension = 0;

                        if (!obj.has("pos") || !obj.has("name")) {
                            continue;
                        }

                        if (obj.has("dimension")) {
                            dimension = obj.get("dimension").getAsInt();
                        }

                        if (obj.get("pos").isJsonObject()) {
                            JsonObject posVec = obj.get("pos").getAsJsonObject();

                            double x = posVec.get("x").getAsDouble();
                            double y = posVec.get("y").getAsDouble();
                            double z = posVec.get("z").getAsDouble();

                            vec = new Vec3(Vec3.fakePool, x, y, z);
                        } else {
                            continue;
                        }

                        name = obj.get("name").getAsString();

                        String k = file.getName().replace(".json", "");
                        List<Waypoint> waypoints = waypointMap.computeIfAbsent(k, (s) -> new CopyOnWriteArrayList<>());
                        waypoints.add(new Waypoint(vec, name, dimension));

                        waypointMap.put(k, waypoints);
                    }
                }
            }

            @Override
            public void save() {
                for (String key : waypointMap.keySet()) {
                    File file = new File(getFile(), key + ".json");
                    if (!file.exists()) {
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            continue;
                        }
                    }

                    JsonArray o = new JsonArray();

                    for (Waypoint waypoint : waypointMap.get(key)) {
                        JsonObject obj = new JsonObject();

                        obj.addProperty("name", waypoint.getName());
                        obj.addProperty("dimension", waypoint.getDimension());

                        JsonObject pos = new JsonObject();
                        pos.addProperty("x", waypoint.getPos().xCoord);
                        pos.addProperty("y", waypoint.getPos().yCoord);
                        pos.addProperty("z", waypoint.getPos().zCoord);

                        obj.add("pos", pos);
                        o.add(obj);
                    }

                    FileUtils.write(file, new GsonBuilder().setPrettyPrinting().create().toJson(o));
                }
            }

        };
    }

    @EventListener(priority = ListenerPriority.HIGH)
    public void onRender3D(EventRender3D event) {

        if (!Waypoints.render) {
            return;
        }

        if (!Nebula.getInstance().getModuleManager().getModule(Waypoints.class).isRunning()) {
            return;
        }

        ServerData data = mc.currentServerData;
        if (!mc.isSingleplayer() && data != null) {
            List<Waypoint> waypoints = waypointMap.getOrDefault(data.serverIP, null);
            if (waypoints == null || waypoints.isEmpty()) {
                return;
            }

            for (Waypoint waypoint : waypoints) {

                if (waypoint.getDimension() != mc.theWorld.provider.dimensionId) {
                    continue;
                }

                double renderX = RenderManager.renderPosX;
                double renderY = RenderManager.renderPosY;
                double renderZ = RenderManager.renderPosZ;

                double x = waypoint.getPos().xCoord - renderX;
                double y = waypoint.getPos().yCoord - renderY;
                double z = waypoint.getPos().zCoord - renderZ;

                double distance = mc.renderViewEntity.getDistance(x + renderX, y + renderY, z + renderZ);
                double scale = (Waypoints.scaling.getValue() * Math.max(distance, 4.0)) / 50.0;

                glPushMatrix();

                RenderHelper.enableGUIStandardItemLighting();
                glDisable(GL_LIGHTING);

                glEnable(GL_POLYGON_OFFSET_FILL);
                glPolygonOffset(1.0f, -1500000.0f);

                //glEnable(GL_RESCALE_NORMAL);

                glTranslated(x, y + 0.45, z);
                glRotated(-RenderManager.instance.playerViewY, 0.0f, 1.0f, 0.0f);
                glRotated(RenderManager.instance.playerViewX, mc.gameSettings.thirdPersonView == 2 ? -1.0f : 1.0f, 0.0f, 0.0f);
                glScaled(-scale, -scale, scale);

                glDisable(GL_DEPTH_TEST);

                int hTimes = 1;

                int textY = -(mc.fontRenderer.FONT_HEIGHT - 2);

                if (Waypoints.render) {
                    if (Waypoints.coordinates.getValue()) {
                        textY -= mc.fontRenderer.FONT_HEIGHT + 2;
                        hTimes++;
                    }

                    if (Waypoints.distance.getValue()) {
                        textY -= mc.fontRenderer.FONT_HEIGHT + 2;
                        hTimes++;
                    }
                }

                double maxWidth = 0.0;

                String text = waypoint.getName();
                int textWidth = mc.fontRenderer.getStringWidth(text) / 2;

                if (textWidth > maxWidth) {
                    maxWidth = textWidth;
                }

                double rectY = textY;
                mc.fontRenderer.drawStringWithShadow(text, (int) -(textWidth - 4.0), textY, -1);

                if (Waypoints.coordinates.getValue()) {
                    textY += mc.fontRenderer.FONT_HEIGHT + 2;

                    text = String.format("%.2f", x + renderX) + ", "
                            + String.format("%.2f", y + renderY) + ", "
                            + String.format("%.2f", z + renderZ);
                    textWidth = mc.fontRenderer.getStringWidth(text) / 2;

                    if (textWidth > maxWidth) {
                        maxWidth = textWidth;
                    }

                    mc.fontRenderer.drawStringWithShadow(text, (int) -(textWidth - 4.0), textY, -1);
                }

                if (Waypoints.distance.getValue()) {
                    textY += mc.fontRenderer.FONT_HEIGHT + 2;

                    text = String.format("%.2f", mc.thePlayer.getDistance(x + renderX, y + renderY, z + renderZ)) + " blocks";
                    textWidth = mc.fontRenderer.getStringWidth(text) / 2;

                    if (textWidth > maxWidth) {
                        maxWidth = textWidth;
                    }

                    mc.fontRenderer.drawStringWithShadow(text, (int) -(textWidth - 4.0), textY, -1);
                }

                if (Waypoints.rectangle.getValue()) {
                    RenderEngine.rectangle(-(maxWidth - 2.0), rectY, (maxWidth * 2.0) + 2.0, hTimes * (mc.fontRenderer.FONT_HEIGHT + 2.0), 0x70000000);
                }

                glPolygonOffset(1.0f, 1500000.0f);
                glDisable(GL_POLYGON_OFFSET_FILL);

                //glDisable(GL_RESCALE_NORMAL);

                glEnable(GL_DEPTH_TEST);
                glEnable(GL_LIGHTING);

                RenderHelper.enableStandardItemLighting();

                glEnable(GL_ALPHA_TEST);

                glPopMatrix();
            }
        }
    }

    @EventListener
    public void onPerspective(EventGluPerspective event) {
        if (!waypointMap.isEmpty()) {

            if (!Nebula.getInstance().getModuleManager().getModule(Waypoints.class).isRunning()) {
                return;
            }

            // set the minimum thing u can render out 30 mill blocks
            event.setzFar(30000000);
        }
    }

    public void add(String ip, Waypoint waypoint) {
        List<Waypoint> waypointList = waypointMap.computeIfAbsent(ip, (s) -> new CopyOnWriteArrayList<>());
        waypointList.add(waypoint);
        waypointMap.put(ip, waypointList);
    }

    public boolean remove(String ip, String name) {
        List<Waypoint> waypointList = waypointMap.computeIfAbsent(ip, (s) -> new CopyOnWriteArrayList<>());
        return waypointList.removeIf((wp) -> wp.getName().equals(name));
    }

    public List<Waypoint> get(String ip) {
        return waypointMap.getOrDefault(ip, new ArrayList<>());
    }
}

package wtf.nebula.impl.module.render;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.src.EnumChatFormatting;
import net.minecraft.src.RenderHelper;
import net.minecraft.src.RenderManager;
import wtf.nebula.event.RenderWorldEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.impl.value.Value;
import wtf.nebula.repository.impl.WaypointRepository;
import wtf.nebula.repository.impl.waypoint.Waypoint;
import wtf.nebula.util.render.RenderUtil;
import wtf.nebula.util.world.ServerUtil;

import java.util.concurrent.CopyOnWriteArrayList;

import static org.lwjgl.opengl.GL11.*;

public class Waypoints extends Module {
    public Waypoints() {
        super("Waypoints", ModuleCategory.RENDER);
        drawn.setValue(false);
        setState(true);
    }

    public final Value<Boolean> showCoordinates = new Value<>("ShowCoordinates", false);
    public final Value<Boolean> showDistance = new Value<>("ShowDistance", true);

    @EventListener
    public void onRenderWorld(RenderWorldEvent event) {
        CopyOnWriteArrayList<Waypoint> waypoints = WaypointRepository
                .get()
                .serverWaypoints
                .getOrDefault(ServerUtil.getServerName(), null);

        if (waypoints == null || waypoints.isEmpty()) {
            return;
        }

        for (Waypoint waypoint : waypoints) {

            // dimension check
            if (waypoint.getDimension() != mc.theWorld.provider.dimensionId) {
                continue;
            }

            double x = waypoint.getCoordinates().xCoord - RenderManager.renderPosX;
            double y = waypoint.getCoordinates().yCoord - RenderManager.renderPosY;
            double z = waypoint.getCoordinates().zCoord - RenderManager.renderPosZ;

            double distance = mc.renderViewEntity.getDistance(x + RenderManager.renderPosX, y + RenderManager.renderPosY, z + RenderManager.renderPosZ);
            double scale = (0.3 * Math.max(distance, 2.5)) / 50.0;

            glPushMatrix();

            RenderHelper.enableGUIStandardItemLighting();
            glDisable(GL_LIGHTING);

            glEnable(GL_POLYGON_OFFSET_FILL);
            glPolygonOffset(1.0f, -1500000.0f);

            glTranslated(x, y + 1.5, z);
            glRotated(-RenderManager.instance.playerViewY, 0.0f, 1.0f, 0.0f);
            glRotated(RenderManager.instance.playerViewX, mc.gameSettings.thirdPersonView == 2 ? -1.0f : 1.0f, 0.0f, 0.0f);
            glScaled(-scale, -scale, scale);

            glDisable(GL_DEPTH_TEST);

            StringBuilder text = new StringBuilder()
                    .append(EnumChatFormatting.AQUA)
                    .append(waypoint.getName())
                    .append(EnumChatFormatting.RESET);

            if (showCoordinates.getValue()) {
                text.append(" ").append(waypoint.getCoordinates().toStringTruncated());
            }

            if (showDistance.getValue()) {
                double dist = mc.thePlayer.getDistance(waypoint.getCoordinates().xCoord, waypoint.getCoordinates().yCoord - 1.5, waypoint.getCoordinates().zCoord);
                text.append(" ").append(String.format("%.1f", dist)).append("m");
            }

            String info = text.toString();

            float width = mc.fontRenderer.getStringWidth(info) / 2.0f;

            RenderUtil.drawRect(-(width - 2.0), -(mc.fontRenderer.FONT_HEIGHT - 1.0), (width * 2.0) + 2.0, mc.fontRenderer.FONT_HEIGHT + 2.0, 0x70000000);
            mc.fontRenderer.drawStringWithShadow(info, (int) -(width - 4.0), -(mc.fontRenderer.FONT_HEIGHT - 2), -1);

            glPolygonOffset(1.0f, 1500000.0f);
            glDisable(GL_POLYGON_OFFSET_FILL);

            glEnable(GL_DEPTH_TEST);
            glEnable(GL_LIGHTING);

            glPopMatrix();
        }
    }
}

package lol.nebula.module.visual;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.EventStage;
import lol.nebula.listener.events.render.EventRenderChest;
import lol.nebula.listener.events.render.EventRenderPlayer;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.setting.Setting;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author aesthetical
 * @since 05/04/23
 */
public class Chams extends Module {

    private final Setting<Boolean> players = new Setting<>(true, "Players");
    private final Setting<Boolean> chests = new Setting<>(true, "Chests");

    public Chams() {
        super("Chams", "Renders entities through walls", ModuleCategory.VISUAL);
    }

    @Listener
    public void onRenderPlayer(EventRenderPlayer event) {
        if (!players.getValue()) return;

        if (event.getStage() == EventStage.PRE) {
            glEnable(GL_POLYGON_OFFSET_FILL);
            glPolygonOffset(1.0f, -1100000.0f);
        } else {
            glPolygonOffset(1.0f, 1100000.0f);
            glDisable(GL_POLYGON_OFFSET_FILL);
        }
    }

    @Listener
    public void onRenderChest(EventRenderChest event) {
        if (!chests.getValue()) return;

        if (event.getStage() == EventStage.PRE) {
            glEnable(GL_POLYGON_OFFSET_FILL);
            glPolygonOffset(1.0f, -1100000.0f);
        } else {
            glPolygonOffset(1.0f, 1100000.0f);
            glDisable(GL_POLYGON_OFFSET_FILL);
        }
    }
}

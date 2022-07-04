package wtf.nebula.impl.module.render;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.src.*;
import wtf.nebula.event.PacketEvent;
import wtf.nebula.event.PacketEvent.Era;
import wtf.nebula.event.RenderWorldEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.util.render.ColorUtil;

import java.awt.*;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import static org.lwjgl.opengl.GL11.*;

public class NewChunks extends Module {
    public NewChunks() {
        super("NewChunks", ModuleCategory.RENDER);
    }

    private final Set<Chunk> chunks = new CopyOnWriteArraySet<>();

    @Override
    protected void onDeactivated() {
        super.onDeactivated();
        chunks.clear();
    }

    @EventListener
    public void onWorldRender(RenderWorldEvent event) {
        for (Chunk chunk : chunks) {

            if (chunk == null) {
                continue;
            }

            glPushMatrix();

            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

            glDisable(GL_TEXTURE_2D);
            glDisable(GL_DEPTH_TEST);

            glLineWidth(1.5f);

            glEnable(GL_LINE_SMOOTH);
            glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);

            glTranslated(-RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ);

            ColorUtil.setColor(Color.RED.getRGB());

            glBegin(GL_LINE_LOOP);
            glVertex3d(chunk.xPosition * 16, 0, chunk.zPosition * 16);
            glVertex3d(chunk.xPosition * 16 + 16, 0, chunk.zPosition * 16);
            glVertex3d(chunk.xPosition * 16 + 16, 0, chunk.zPosition * 16 + 16);
            glVertex3d(chunk.xPosition * 16, 0, chunk.zPosition * 16 + 16);
            glEnd();

            glDisable(GL_LINE_SMOOTH);

            glDisable(GL_BLEND);
            glEnable(GL_TEXTURE_2D);
            glEnable(GL_DEPTH_TEST);

            glLineWidth(1.0f);
            glPopMatrix();
        }
    }

    @EventListener
    public void onPacketReceive(PacketEvent.Receive event) {

        if (event.getPacket() instanceof Packet51MapChunk && event.getEra().equals(Era.PRE)) {
            Packet51MapChunk packet = event.getPacket();

            if (packet.isChunkDataPacket && packet.includeInitialize) {
                Chunk chunk = mc.theWorld.getChunkFromChunkCoords(packet.xCh, packet.zCh);
                if (chunk != null) {
                    chunks.add(chunk);
                }
            }
        }
    }
}

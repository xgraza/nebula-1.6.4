package wtf.nebula.impl.module.render;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.RenderManager;
import org.lwjgl.input.Keyboard;
import wtf.nebula.event.RenderWorldEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;

import static org.lwjgl.opengl.GL11.*;

// thanks gav lol, this is from gavhack-legacy, not my code
public class ChunkBorders extends Module {
    public ChunkBorders() {
        super("ChunkBorders", ModuleCategory.RENDER);
        setBind(Keyboard.KEY_P);
    }

    @EventListener
    public void onRenderWorld(RenderWorldEvent event) {
        final int chunkX = mc.thePlayer.chunkCoordX * 16;
        final int chunkZ = mc.thePlayer.chunkCoordZ * 16;

        final AxisAlignedBB chunkBB = AxisAlignedBB.getBoundingBox(
                chunkX, 0.0, chunkZ,
                chunkX + 16.0, 255.0, chunkZ + 16.0
        );

        glPushMatrix();

        glColor3f(1.0f, 1.0f, 0.0f);

        glDisable(GL_TEXTURE_2D);
        glDisable(GL_DEPTH_TEST);

        glTranslated(-RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ);
        glLineWidth(1.0f);

        mc.renderGlobal.drawOutlinedBoundingBox(chunkBB);

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);

        glPopMatrix();
    }
}

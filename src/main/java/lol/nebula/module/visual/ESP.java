package lol.nebula.module.visual;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.render.world.EventRender3D;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.setting.Setting;
import lol.nebula.util.render.shader.ESPShader;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;

import static java.lang.Integer.MIN_VALUE;
import static org.lwjgl.opengl.GL11.*;

/**
 * @author aesthetical
 * @since 06/11/23
 */
public class ESP extends Module {
    private final Setting<Mode> mode = new Setting<>(Mode.SHADER, "Mode");

    private final Setting<Float> lineWidth = new Setting<>(
            1.5f, 0.01f, 0.1f, 5.0f, "Line Width");
    private final Setting<Float> transparency = new Setting<>(
            () -> mode.getValue() == Mode.SHADER, 0.5f, 0.01f, 0.0f, 1.0f, "Transparency");

    private final ESPShader espShader = new ESPShader();
    private Framebuffer framebuffer;
    private int w, h, s;

    public ESP() {
        super("ESP", "oooo pretty", ModuleCategory.VISUAL);
    }

    @Listener(eventPriority = MIN_VALUE)
    public void onRender3D(EventRender3D event) {
        if (mode.getValue() != Mode.SHADER || mc.gameSettings.ofFastRender) return;

        glEnable(GL_ALPHA_TEST);
        glPushMatrix();
        glPushAttrib(8256);

        ScaledResolution res = new ScaledResolution(
                mc.gameSettings, mc.displayWidth, mc.displayHeight);

        if (framebuffer != null) {
            framebuffer.framebufferClear();

            if (res.getScaledHeight() != h || res.getScaledWidth() != w || res.getScaleFactor() != s) {
                framebuffer.deleteFramebuffer();

                framebuffer = new Framebuffer(mc.displayWidth, mc.displayHeight, true);
                framebuffer.framebufferClear();
            }

            w = res.getScaledWidth();
            h = res.getScaledHeight();
            s = res.getScaleFactor();
        } else {
            framebuffer = new Framebuffer(mc.displayWidth, mc.displayHeight, true);
        }

        framebuffer.bindFramebuffer(false);

//        boolean shadows = Render.renderShadow;
//        Render.renderShadow = false;

        mc.entityRenderer.setupCameraTransform(event.getPartialTicks(), 0);

        mc.theWorld.loadedEntityList.forEach((entity) -> {
            if (entity != null) {
                if (entity instanceof EntityPlayer && !mc.thePlayer.equals(entity)) {
                    RenderManager.instance.func_147936_a((Entity) entity, event.getPartialTicks(), true);
                }
            }
        });

        new ArrayList<TileEntity>(mc.theWorld.field_147482_g).forEach((tileEntity) -> {
            if (tileEntity != null) {
                try {
                    TileEntityRendererDispatcher.instance.func_147544_a(tileEntity, event.getPartialTicks());
                } catch (Exception ignored) {
                    // empty catch block - lol
                }
            }
        });

        //Render.renderShadow = shadows;

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        framebuffer.unbindFramebuffer();
        mc.getFramebuffer().bindFramebuffer(true);

        mc.entityRenderer.disableLightmap(0.0);
        RenderHelper.disableStandardItemLighting();

        espShader.setColor(Interface.color.getValue());
        espShader.setLineWidth(lineWidth.getValue());
        espShader.setOpacity(transparency.getValue());

        mc.entityRenderer.setupOverlayRendering();

        espShader.use();
        espShader.updateUniforms();

        glBindTexture(GL_TEXTURE_2D, framebuffer.framebufferTexture);
        glBegin(GL_QUADS);
        glTexCoord2d(0, 1);
        glVertex2d(0, 0);
        glTexCoord2d(0, 0);
        glVertex2d(0, res.getScaledHeight());
        glTexCoord2d(1, 0);
        glVertex2d(res.getScaledWidth(), res.getScaledHeight());
        glTexCoord2d(1, 1);
        glVertex2d(res.getScaledWidth(), 0);
        glEnd();

        espShader.stopUse();

        mc.entityRenderer.enableLightmap(0.0);

        glPopAttrib();
        glPopMatrix();

        mc.entityRenderer.setupOverlayRendering();
    }

    public enum Mode {
        SHADER, CSGO, BOX
    }
}

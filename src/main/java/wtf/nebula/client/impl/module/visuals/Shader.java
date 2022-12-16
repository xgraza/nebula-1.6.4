package wtf.nebula.client.impl.module.visuals;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.impl.event.impl.render.EventRenderHand;
import wtf.nebula.client.impl.event.impl.render.EventRender3D;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;
import wtf.nebula.client.impl.module.active.Colors;
import wtf.nebula.client.utils.render.shader.impl.FillShader;
import wtf.nebula.client.utils.world.EntityUtils;

import java.awt.*;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

public class Shader extends ToggleableModule {
    private final Property<Mode> mode = new Property<>(Mode.FILL, "Mode", "m", "type");
    private final Property<Float> lineWidth = new Property<>(1.5f, 0.1f, 5.0f, "Width", "linewidth");
    private final Property<Float> opacity = new Property<>(0.5f, 0.1f, 1.0f, "Opacity", "op")
            .setVisibility(() -> mode.getValue().equals(Mode.FILL));

    private final Property<Boolean> players = new Property<>(true, "Players", "plrs");
    private final Property<Boolean> mobs = new Property<>(true, "Mobs", "hostile");
    private final Property<Boolean> passive = new Property<>(true, "Passive", "nice", "animals", "ambient");
    private final Property<Boolean> tileEntities = new Property<>(true, "Tile Entities", "tile", "tileentities");
    private final Property<Boolean> dropped = new Property<>(true, "Dropped Items", "dropped");
    private final Property<Boolean> held = new Property<>(true, "Held Items", "held");

    private Framebuffer framebuffer;
    private int w, h, s;

    private final FillShader fillShader = new FillShader();

    public Shader() {
        super("Shader", new String[]{"shaderesp", "esp"}, ModuleCategory.VISUALS);
        offerProperties(mode, lineWidth, opacity, players, mobs, passive, tileEntities, dropped, held);
    }

    @Override
    public String getTag() {
        return mode.getFixedValue();
    }

    @EventListener
    public void onRenderWorld(EventRender3D event) {
        if (mc.gameSettings.ofFastRender) {
            print("You cannot use this module while using fast render!");
            setRunning(false);
            return;
        }

        glEnable(GL_ALPHA_TEST);
        glPushMatrix();
        glPushAttrib(8256);

        ScaledResolution res = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);

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

        boolean shadows = Render.renderShadow;
        Render.renderShadow = false;

        mc.entityRenderer.setupCameraTransform(event.getPartialTicks(), 0);

        mc.theWorld.loadedEntityList.forEach((entity) -> {
            if (entity != null) {
                if (!(entity instanceof EntityItem)) {
                    if (entity.equals(mc.renderViewEntity) && mc.gameSettings.thirdPersonView == 0) {
                        return;
                    }

                    if (!(entity instanceof EntityLivingBase)) {
                        return;
                    }

                    if (entity instanceof EntityPlayer && !players.getValue()) {
                        return;
                    }

                    if (EntityUtils.isMob(entity) && !mobs.getValue()) {
                        return;
                    }

                    if (EntityUtils.isPassive(entity) && !passive.getValue()) {
                        return;
                    }
                } else {
                    if (!dropped.getValue()) {
                        return;
                    }
                }

                RenderManager.instance.renderEntityStatic(entity, event.getPartialTicks(), true);
            }
        });

        if (tileEntities.getValue()) {
            new ArrayList<TileEntity>(mc.theWorld.tileEntities).forEach((tileEntity) -> {
                if (tileEntity != null) {
                    try {
                        TileEntityRendererDispatcher.instance.renderTileEntity(tileEntity, event.getPartialTicks());
                    } catch (Exception ignored) {
                        // empty catch block - lol
                    }
                }
            });
        }

        Render.renderShadow = shadows;

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        framebuffer.unbindFramebuffer();
        mc.getFramebuffer().bindFramebuffer(true);

        mc.entityRenderer.disableLightmap(0.0);
        RenderHelper.disableStandardItemLighting();

        glPushMatrix();

        wtf.nebula.client.utils.render.shader.Shader shader = null;

        Color color = Colors.getColor();
        if (Colors.rainbow.getValue()) {
            color = Colors.getClientRainbowAwt(0);
        }

        switch (mode.getValue()) {

            case FILL: {
                shader = fillShader;

                fillShader.setColor(color);
                fillShader.setLineWidth(lineWidth.getValue());
                fillShader.setOpacity(opacity.getValue());

                fillShader.use();
                fillShader.updateUniforms();
                break;
            }

        }

        mc.entityRenderer.setupOverlayRendering();

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

        if (shader != null) {
            shader.stopUse();
        }

        glPopMatrix();

        mc.entityRenderer.enableLightmap(0.0);

        glPopMatrix();
        glPopAttrib();

        mc.entityRenderer.setupOverlayRendering();
    }

    @EventListener
    public void onRenderHand(EventRenderHand event) {
        if (!held.getValue()) {
            return;
        }

        if (mc.gameSettings.ofFastRender) {
            print("You cannot use this module while using fast render!");
            setRunning(false);
            return;
        }

        glEnable(GL_ALPHA_TEST);
        glPushMatrix();
        glPushAttrib(8256);

        ScaledResolution res = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);

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

        boolean shadows = Render.renderShadow;
        Render.renderShadow = false;

        mc.entityRenderer.setupCameraTransform(event.getPartialTicks(), 0);

        mc.entityRenderer.renderHand(event.getPartialTicks(), event.getWtf());

        Render.renderShadow = shadows;

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        framebuffer.unbindFramebuffer();
        mc.getFramebuffer().bindFramebuffer(true);

        mc.entityRenderer.disableLightmap(0.0);
        RenderHelper.disableStandardItemLighting();

        glPushMatrix();

        wtf.nebula.client.utils.render.shader.Shader shader = null;

        Color color = Colors.getColor();
        if (Colors.rainbow.getValue()) {
            color = Colors.getClientRainbowAwt(0);
        }

        switch (mode.getValue()) {
            case FILL: {
                shader = fillShader;

                fillShader.setColor(color);
                fillShader.setLineWidth(lineWidth.getValue());

                fillShader.use();
                fillShader.updateUniforms();
                break;
            }

        }

        mc.entityRenderer.setupOverlayRendering();

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

        if (shader != null) {
            shader.stopUse();
        }

        glPopMatrix();

        mc.entityRenderer.enableLightmap(0.0);

        glPopMatrix();
        glPopAttrib();

        mc.entityRenderer.setupOverlayRendering();
    }

    public enum Mode {
        FILL
    }
}

package ez.nebula.client.impl.module.render;

import ez.nebula.client.Nebula;
import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.listener.event.render.EventRender2D;
import ez.nebula.client.api.manager.hud2.HUDElement;
import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.setting.Setting;
import ez.nebula.client.impl.gui.hud2.HUDEditorScreen;
import ez.nebula.client.util.render.font.AWTFontRenderer;
import ez.nebula.client.util.render.gui.Render2D;
import net.minecraft.client.gui.GuiChat;

import static org.lwjgl.opengl.GL11.*;

@ModuleManifest(name = "HUDTest", description = "Test HUD", category = ModuleCategory.RENDER)
public final class HUDTestModule extends Module
{
    private final Setting<Boolean> showGUISetting = builder("Open HUD Editor...", false)
            .setDescription("Click this to open the HUD editor")
            .onValueChanged(this::handleGUI)
            .build();

    private void handleGUI(final boolean value)
    {
        if (MC.thePlayer == null || MC.theWorld == null || !value)
        {
            return;
        }
        if (showGUISetting != null)
        {
            showGUISetting.setValue(false);
        }
        MC.displayGuiScreen(new HUDEditorScreen(MC.currentScreen));
    }

    @Subscribe
    private final EventListener<EventRender2D> render2DEventListener = event ->
    {
        if (MC.currentScreen instanceof HUDEditorScreen || MC.gameSettings.showDebugInfo)
        {
            return;
        }

        MC.mcProfiler.startSection("nebulaHud");

        glPushMatrix();
        glScaled(Render2D.getGUIScaleFactor(), Render2D.getGUIScaleFactor(), Render2D.getGUIScaleFactor());

        double maxPosY = event.getResolution().getScaledHeight_double();
        if (MC.currentScreen instanceof GuiChat)
        {
            maxPosY -= 15;
        }

        AWTFontRenderer.DYNAMIC_FONT_RESIZING = false;

        for (final HUDElement element : Nebula.HUD_NEW.getAll())
        {
            if (!element.isToggled())
            {
                continue;
            }

            final double translateY = ((element.getY() + element.getHeight()) - maxPosY);
            if (translateY >= 1)
            {
                glTranslated(0, -translateY, 0);
            }

            element.render(event.getResolution());

            if (translateY >= 1)
            {
                glTranslated(0, translateY, 0);
            }
        }

        AWTFontRenderer.DYNAMIC_FONT_RESIZING = true;

        glPopMatrix();

        MC.mcProfiler.endSection();
    };
}

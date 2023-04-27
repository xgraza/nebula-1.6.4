package lol.nebula.module.visual;

import lol.nebula.Nebula;
import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.render.EventRender2D;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;

/**
 * @author aesthetical
 * @since 04/27/23
 */
public class Interface extends Module {
    public Interface() {
        super("Interface", "Renders an overlay over the vanilla HUD", ModuleCategory.VISUAL);

        // by default, set on
        setState(true);
    }

    @Listener
    public void onRender2D(EventRender2D event) {

        // if the F3 debug menu is open, do not render over it
        if (mc.gameSettings.showDebugInfo) return;

        mc.fontRenderer.drawStringWithShadow(Nebula.getName() + " v" + Nebula.getVersion(), 3, 3, -1);
    }
}

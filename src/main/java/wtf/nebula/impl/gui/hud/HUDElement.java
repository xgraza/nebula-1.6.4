package wtf.nebula.impl.gui.hud;

import net.minecraft.client.gui.ScaledResolution;
import wtf.nebula.impl.gui.ui.DraggableComponent;
import wtf.nebula.impl.module.render.HUD;

public abstract class HUDElement extends DraggableComponent {
    protected final HUD hud;

    public HUDElement(HUD hud, String name) {
        super(name);
        this.hud = hud;
    }

    public abstract void onRender(ScaledResolution resolution);

    @Override
    public void drawComponent(int mouseX, int mouseY, float partialTicks) {
        // TODO: dragging
        onRender(new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight));
    }

    @Override
    public void mouseClick(int mouseX, int mouseY, int button) {
        // TODO: dragging
    }

    @Override
    public void mouseRelease(int mouseX, int mouseY, int state) {
        // TODO: dragging
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }
}

package wtf.nebula.client.impl.module.active.clickgui;

import net.minecraft.client.gui.GuiScreen;
import wtf.nebula.client.core.Launcher;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.active.ClickGUI;
import wtf.nebula.client.impl.module.active.clickgui.elements.Panel;

import java.util.ArrayList;
import java.util.List;

public class ClickGUIScreen extends GuiScreen {

    private static ClickGUIScreen instance;

    private final List<Panel> panels = new ArrayList<>();

    protected ClickGUIScreen() {
        double x = 4.0;

        for (ModuleCategory category : ModuleCategory.values()) {
            Panel panel = new Panel(category);
            panel.x = x;
            panel.y = 22.0;

            panels.add(panel);

            x += 90.0;
        }
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        drawDefaultBackground();
        panels.forEach((panel) -> panel.drawScreen(par1, par2, par3));
    }

    @Override
    protected void mouseClicked(int par1, int par2, int par3) {
        super.mouseClicked(par1, par2, par3);
        panels.forEach((panel) -> panel.mouseClick(par1, par2, par3));
    }

    @Override
    protected void keyTyped(char par1, int par2) {
        panels.forEach((panel) -> panel.keyTyped(par1, par2));
        super.keyTyped(par1, par2);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        Launcher.getInstance().getModuleManager().getModule(ClickGUI.class).setRunning(false);
    }

    public static ClickGUIScreen getInstance() {
        if (instance == null) {
            instance = new ClickGUIScreen();
        }

        return instance;
    }
}

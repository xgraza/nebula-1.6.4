package wtf.nebula.impl.gui.ui;

import net.minecraft.src.GuiScreen;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.impl.module.render.ClickGUI;
import wtf.nebula.repository.impl.ModuleRepository;

import java.util.ArrayList;
import java.util.List;

public class ClickGUIScreen extends GuiScreen {
    private static ClickGUIScreen instance;

    private final List<ModulePanel> panels = new ArrayList<>();

    public static boolean disableClose = false;

    private ClickGUIScreen() {
        double x = 6.0;

        for (ModuleCategory category : ModuleCategory.values()) {
            List<Module> modules = ModuleRepository.get().modulesByCategory.getOrDefault(category, null);
            if (modules == null) {
                continue;
            }

            panels.add(new ModulePanel(x, category.displayName, modules));
            x += 110.0;
        }
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        panels.forEach((panel) -> panel.drawComponent(par1, par2, par3));
    }

    @Override
    protected void mouseClicked(int par1, int par2, int par3) {
        super.mouseClicked(par1, par2, par3);
        panels.forEach((panel) -> panel.mouseClick(par1, par2, par3));
    }

    @Override
    protected void keyTyped(char par1, int par2) {

        if (!disableClose) {
            super.keyTyped(par1, par2);
        }

        panels.forEach((panel) -> panel.keyTyped(par1, par2));
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();

        // disable the ClickGUI
        ModuleRepository.get().getModule(ClickGUI.class).setState(false);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public static ClickGUIScreen getInstance() {
        if (instance == null) {
            instance = new ClickGUIScreen();
        }

        return instance;
    }
}

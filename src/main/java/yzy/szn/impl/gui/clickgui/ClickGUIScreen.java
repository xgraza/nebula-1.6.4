package yzy.szn.impl.gui.clickgui;

import net.minecraft.client.gui.GuiScreen;
import yzy.szn.api.module.Module;
import yzy.szn.api.module.ModuleCategory;
import yzy.szn.impl.module.render.ModuleClickGUI;
import yzy.szn.launcher.YZY;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author graza
 * @since 02/18/24
 */
public final class ClickGUIScreen extends GuiScreen {

    private static final double PANEL_POS_Y = 25.0;
    private static final double PANEL_PADDING = 8.0;

    private final ModuleClickGUI module;

    private final List<ClickGUIModulePanel> panels = new LinkedList<>();

    public ClickGUIScreen(ModuleClickGUI module) {
        this.module = module;

        double POS_X = 4.0;
        for (final ModuleCategory category : ModuleCategory.values()) {
            final List<Module> modules = YZY.INSTANCE.getModuleManager().getModules()
                    .stream()
                    .filter((mod) -> mod.getManifest().category() == category)
                    .collect(Collectors.toList());
            if (!modules.isEmpty()) {
                final ClickGUIModulePanel panel = new ClickGUIModulePanel(category, modules);
                panel.setX(POS_X);
                panel.setY(PANEL_POS_Y);

                panels.add(panel);
                POS_X += panel.getWidth() + PANEL_PADDING;
            }
        }
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {

    }

    @Override
    protected void mouseClicked(int par1, int par2, int par3) {
        super.mouseClicked(par1, par2, par3);
    }

    @Override
    protected void keyTyped(char par1, int par2) {
        super.keyTyped(par1, par2);
    }
}

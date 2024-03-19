package yzy.szn.impl.gui.clickgui;

import yzy.szn.api.gui.Component;
import yzy.szn.api.gui.IComponentListener;
import yzy.szn.api.module.Module;
import yzy.szn.api.module.ModuleCategory;

import java.util.List;

/**
 * @author graza
 * @since 02/18/24
 */
public final class ClickGUIModulePanel extends Component implements IComponentListener {

    private static final double PANEL_WIDTH = 120.0;
    private static final double PANEL_HEADER_HEIGHT = 16.0;

    private final ModuleCategory category;
    private final List<Module> modules;

    public ClickGUIModulePanel(ModuleCategory category, List<Module> modules) {
        this.category = category;
        this.modules = modules;

        setWidth(PANEL_WIDTH);
        setHeight(PANEL_HEADER_HEIGHT);
    }

    @Override
    public void render(int mouseX, int mouseY) {

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {

    }

    @Override
    public void keyTyped(int keyCode) {

    }
}

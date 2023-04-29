package lol.nebula.ui;

import lol.nebula.Nebula;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.ui.component.category.CategoryFrame;
import net.minecraft.client.gui.GuiScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author aesthetical
 * @since 04/27/23
 */
public class ClickUIScreen extends GuiScreen {

    private final List<CategoryFrame> categoryFrames = new ArrayList<>();

    public ClickUIScreen() {
        double x = 4.0;
        for (ModuleCategory moduleCategory : ModuleCategory.values()) {
            List<Module> modules = Nebula.getInstance().getModules().getModules()
                    .stream()
                    .filter((m) -> m.getCategory() == moduleCategory)
                    .collect(Collectors.toList());

            if (modules.isEmpty()) continue;

            CategoryFrame categoryFrame = new CategoryFrame(moduleCategory.getDisplay(), modules);
            categoryFrame.setX(x);
            categoryFrame.setY(30.0);
            categoryFrame.setWidth(120.0);
            categoryFrame.setHeight(16.5);

            categoryFrames.add(categoryFrame);
            x += categoryFrame.getWidth() + 6.0;
        }
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        for (CategoryFrame categoryFrame : categoryFrames) {
            categoryFrame.render(par1, par2, par3);
        }
    }

    @Override
    protected void mouseClicked(int par1, int par2, int par3) {
        super.mouseClicked(par1, par2, par3);
        for (CategoryFrame categoryFrame : categoryFrames) {
            categoryFrame.mouseClicked(par1, par2, par3);
        }
    }

    @Override
    protected void keyTyped(char par1, int par2) {
        super.keyTyped(par1, par2);
        for (CategoryFrame categoryFrame : categoryFrames) {
            categoryFrame.keyTyped(par1, par2);
        }
    }
}

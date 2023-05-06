package lol.nebula.ui;

import lol.nebula.Nebula;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.ui.component.Component;
import lol.nebula.ui.component.category.CategoryFrame;
import lol.nebula.ui.component.module.ModuleComponent;
import lol.nebula.util.math.timing.Timer;
import lol.nebula.util.render.RenderUtils;
import lol.nebula.util.render.animation.Animation;
import lol.nebula.util.render.animation.Easing;
import lol.nebula.util.render.font.Fonts;
import net.minecraft.client.gui.GuiScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static lol.nebula.module.ModuleManager.DEFAULT_CONFIG;

/**
 * @author aesthetical
 * @since 04/27/23
 */
public class ClickUIScreen extends GuiScreen {

    private final List<CategoryFrame> categoryFrames = new ArrayList<>();

    private final Animation descriptionAnimation = new Animation(Easing.CUBIC_IN_OUT, 250, false);
    private final Timer descriptionTimer = new Timer();
    private String renderedDescription;

    public ClickUIScreen() {
        double x = 4.0;
        for (ModuleCategory moduleCategory : ModuleCategory.values()) {
            List<Module> modules = Nebula.getInstance().getModules().getModules()
                    .stream()
                    .filter((m) -> m.getCategory() == moduleCategory)
                    .collect(Collectors.toList());

            if (modules.isEmpty()) continue;

            CategoryFrame categoryFrame = new CategoryFrame(moduleCategory, modules);
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

        for (CategoryFrame categoryFrame : categoryFrames) {
            for (Component component : categoryFrame.getChildren()) {
                if (!(component instanceof ModuleComponent)) continue;

                ModuleComponent modComp = (ModuleComponent) component;
                if (modComp.getModule() == null || !modComp.isInBounds(par1, par2)) {
                    continue;
                }

                if (descriptionTimer.ms(230L, false)) {
                    String renderedDescription = modComp.getModule().getDescription();

                    double x = par1 + 6;
                    double y = par2 + 4;
                    double width = Fonts.axiforma.getStringWidth(renderedDescription) + 6.0;
                    double height = Fonts.axiforma.FONT_HEIGHT + 4.0;

                    RenderUtils.rect(x, y, width, height, ModuleComponent.UNTOGGLED_BG.getRGB());
                    Fonts.axiforma.drawStringWithShadow(renderedDescription, (float) (x + 2.0), (float) (y + 2.0), -1);
                }

                return;
            }
        }

        descriptionTimer.resetTime();
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

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        Nebula.getInstance().getModules().saveModules(DEFAULT_CONFIG);
        renderedDescription = null;
    }
}

package lol.nebula.ui.component.category;

import lol.nebula.module.Module;
import lol.nebula.ui.component.Component;
import lol.nebula.ui.component.DraggableComponent;
import lol.nebula.ui.component.module.ModuleComponent;
import lol.nebula.util.render.RenderUtils;
import lol.nebula.util.render.font.Fonts;

import java.awt.*;
import java.util.List;

/**
 * @author aesthetical
 * @since 04/27/23
 */
public class CategoryFrame extends DraggableComponent {

    private static final Color PANEL_COLOR = new Color(30, 30, 30);

    private final String categoryName;

    public CategoryFrame(String categoryName, List<Module> modules) {
        this.categoryName = categoryName;

        for (Module module : modules) getChildren().add(new ModuleComponent(module));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        RenderUtils.rect(getX(), getY(), getWidth(), getHeight(), PANEL_COLOR.getRGB());
        RenderUtils.scissor(getX(), getY(), getWidth(), getHeight());
        Fonts.axiforma.drawStringWithShadow(
                categoryName,
                (float) (getX() + 2.0),
                (float) (getY() + (super.getHeight() / 2.0) - (Fonts.axiforma.FONT_HEIGHT / 2.0)),
                -1);

        if (!getChildren().isEmpty()) {
            double y = getY() + super.getHeight() + 1.0;
            for (Component component : getChildren()) {
                component.setX(getX() + 1.0);
                component.setY(y);
                component.setHeight(13.5);
                component.setWidth(getWidth() - 2.0);

                component.render(mouseX, mouseY, partialTicks);
                y += component.getHeight();
            }
        }

        RenderUtils.endScissor();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        for (Component component : getChildren()) {
            component.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        for (Component component : getChildren()) {
            component.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    public double getHeight() {
        double height = super.getHeight() + 2.0;
        for (Component component : getChildren()) {
            height += component.getHeight();
        }
        return height;
    }
}

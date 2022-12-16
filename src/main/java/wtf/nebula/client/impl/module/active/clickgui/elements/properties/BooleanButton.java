package wtf.nebula.client.impl.module.active.clickgui.elements.properties;

import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.impl.module.active.Colors;
import wtf.nebula.client.impl.module.active.clickgui.elements.Element;
import wtf.nebula.client.utils.render.RenderEngine;
import wtf.nebula.client.utils.render.enums.Dimension;
import wtf.nebula.client.utils.render.renderers.impl.two.GradientBox;

public class BooleanButton extends Element {
    private final Property<Boolean> property;

    public BooleanButton(Property<Boolean> property) {
        super(property.getLabel());
        this.property = property;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderEngine.RenderStack stack = RenderEngine.of(Dimension.TWO);

        int topColor = -1;
        if (property.getValue()) {
            topColor = Colors.getClientColor(isHovering(mouseX, mouseY) ? 55 : 77);
        } else {
            topColor = isHovering(mouseX, mouseY) ? 0x77AAAAAB : -1;
        }

        int bottomColor = -1;
        if (property.getValue()) {
            bottomColor = Colors.getClientColor(isHovering(mouseX, mouseY) ? 55 : 77);
        } else {
            bottomColor = isHovering(mouseX, mouseY) ? 0x66AAAAAB : -1;
        }

        if (topColor != -1 && bottomColor != -1) {
            stack.add(new GradientBox(x, y, width, 15, topColor, bottomColor));
        }

        stack.render();

        mc.fontRenderer.drawStringWithShadow(property.getLabel(), (int) (x + 2), (int) (y + 4), -1);
    }

    @Override
    public void mouseClick(int mouseX, int mouseY, int button) {
        if (isHovering(mouseX, mouseY) && button == 0) {
            Element.playClickSound();
            property.setValue(!property.getValue());
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }

    @Override
    public boolean isVisible() {
        return property.isVisible();
    }
}

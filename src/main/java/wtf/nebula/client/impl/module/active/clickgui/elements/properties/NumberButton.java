package wtf.nebula.client.impl.module.active.clickgui.elements.properties;

import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Mouse;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.impl.module.active.Colors;
import wtf.nebula.client.impl.module.active.clickgui.elements.Element;
import wtf.nebula.client.utils.render.RenderEngine;
import wtf.nebula.client.utils.render.enums.Dimension;
import wtf.nebula.client.utils.render.renderers.impl.two.Box;
import wtf.nebula.client.utils.render.renderers.impl.two.GradientBox;

public class NumberButton extends Element {
    private final Property<Number> property;
    private boolean dragging = false;
    private float difference;

    public NumberButton(Property<Number> property) {
        super(property.getLabel());
        this.property = property;
        this.difference = property.getMax().floatValue() - property.getMin().floatValue();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (dragging) {
            if (!Mouse.isButtonDown(0)) {
                dragging = false;
            } else {
                setValue(mouseX);
            }
        }

        RenderEngine.RenderStack stack = RenderEngine.of(Dimension.TWO);

        double percent = (property.getValue().floatValue() - property.getMin().floatValue()) / difference;
        double barWidth = width * percent;

        stack.add(new Box(x, y, barWidth, height, Colors.getClientColor(isHovering(mouseX, mouseY) ? 55 : 77)));

        stack.render();

        mc.fontRenderer.drawStringWithShadow(property.getLabel() + ": " + EnumChatFormatting.GRAY + property.getValue(), (int) (x + 2), (int) (y + 4), -1);
    }

    @Override
    public void mouseClick(int mouseX, int mouseY, int button) {
        if (isHovering(mouseX, mouseY) && button == 0) {
            dragging = true;
            Element.playClickSound();
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }

    private void setValue(int mouseX) {
        float percent = (float) ((mouseX - x) / (width));
        if (property.getValue() instanceof Double) {
            double result = (Double)property.getMin() + (difference * percent);
            property.setValue(Math.round(10.0 * result) / 10.0);
        } else if (property.getValue() instanceof Float) {
            float result = (Float)property.getMin() + (difference * percent);
            property.setValue(Math.round(10.0f * result) / 10.0f);
        } else if (property.getValue() instanceof Integer) {
            property.setValue(((Integer)property.getMin() + (int)(difference * percent)));
        }
    }

    @Override
    public boolean isVisible() {
        return property.isVisible();
    }
}

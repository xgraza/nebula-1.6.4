package wtf.nebula.client.impl.module.active.clickgui.elements.properties;

import net.minecraft.util.EnumChatFormatting;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.impl.module.active.clickgui.elements.Element;
import wtf.nebula.client.utils.render.RenderEngine;
import wtf.nebula.client.utils.render.enums.Dimension;
import wtf.nebula.client.utils.render.renderers.impl.two.GradientBox;

public class EnumButton extends Element {
    private final Property<Enum> property;

    public EnumButton(Property<Enum> property) {
        super(property.getLabel());
        this.property = property;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderEngine.RenderStack stack = RenderEngine.of(Dimension.TWO);

        if (isHovering(mouseX, mouseY)) {
            stack.add(new GradientBox(x, y, width, height, 0x77AAAAAB, 0x66AAAAAB));
        }

        stack.render();

        mc.fontRenderer.drawStringWithShadow(property.getLabel() + ": " + EnumChatFormatting.GRAY + property.getFixedValue(), (int) (x + 2), (int) (y + 4), -1);
    }

    @Override
    public void mouseClick(int mouseX, int mouseY, int button) {
        if (isHovering(mouseX, mouseY)) {
            Element.playClickSound();

            if (button == 0) {
                property.next();
            } else if (button == 1) {
                property.previous();
            }
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }
}

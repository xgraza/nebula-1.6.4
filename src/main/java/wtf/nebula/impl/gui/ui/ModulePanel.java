package wtf.nebula.impl.gui.ui;

import net.minecraft.src.EnumChatFormatting;
import org.lwjgl.input.Mouse;
import wtf.nebula.impl.module.Module;
import wtf.nebula.util.render.RenderUtil;

import java.awt.*;
import java.util.List;

public class ModulePanel extends DraggableComponent {
    private boolean settingsOpened = false;

    public ModulePanel(double x, String name, List<Module> modules) {
        super(name);

        this.x = x;
        this.y = 12.0;
        this.width = 105.0;
        this.height = 14.0;

        modules.forEach((module) -> children.add(new ModuleComponent(module)));

        // default open state
        setState(true);
    }

    @Override
    public void drawComponent(int mouseX, int mouseY, float partialTicks) {

        if (isMouseWithinBounds(mouseX, mouseY, x, y, width, totalCompHeight() + 1.0)) {
            int scroll = Mouse.getDWheel();
            if (scroll > 0) {
                y -= 10.0;
            } else if (scroll < 0) {
                y += 10.0;
            }
        }

        RenderUtil.drawRect(x, y, width, height, 0x90000000);

        double toggleButtonDimensions = height - 4.0;
        RenderUtil.drawRect(
                x + width - toggleButtonDimensions - 2.0,
                y + 2.0,
                toggleButtonDimensions, toggleButtonDimensions,
                Color.gray.getRGB());

        toggleButtonDimensions -= 2.0;
        RenderUtil.drawRect(
                x + width - toggleButtonDimensions - 3.0,
                y + 3.0,
                toggleButtonDimensions, toggleButtonDimensions,
                getState() ? Color.black.getRGB() : Color.darkGray.getRGB());

        mc.fontRenderer.drawStringWithShadow(
                EnumChatFormatting.GREEN + name + EnumChatFormatting.RESET + " (" + children.size() + ")",
                (int) (x + 2.3),
                (int) (y + (height / 2) - mc.fontRenderer.FONT_HEIGHT / 2),
                -1);

        if (getState()) {

            double componentY = y + height + 0.5;

            // draw rect
            RenderUtil.drawRect(x, componentY - 0.5, width, totalCompHeight() + 1.0, new Color(0, 0, 0, 111).getRGB());

            for (Component component : children) {
                component.x = x + 2.0;
                component.y = componentY;
                component.height = 14.0;
                component.width = width - 4;

                component.drawComponent(mouseX, mouseY, partialTicks);

                componentY += component.height + 1.0;
            }
        }
    }

    @Override
    public void mouseClick(int mouseX, int mouseY, int button) {
        if (button == 0) {

            // toggle button dimensions
            double toggleButtonDimensions = height - 4.0;

            // in the toggle button bounds
            if (isMouseWithinBounds(
                    mouseX,
                    mouseY,
                    x + width - toggleButtonDimensions - 2.0,
                    y + 2.0, toggleButtonDimensions, toggleButtonDimensions)) {

                playClickSound();
                setState(!getState());
            }

            else {

                if (isInBounds(mouseX, mouseY)) {
                    settingsOpened = !settingsOpened;
                }
            }
        }

        if (getState()) {
            children.forEach((child) -> child.mouseClick(mouseX, mouseY, button));
        }
    }

    @Override
    public void mouseRelease(int mouseX, int mouseY, int state) {
        if (getState()) {
            children.forEach((child) -> child.mouseRelease(mouseX, mouseY, state));
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (state) {
            children.forEach((child) -> child.keyTyped(typedChar, keyCode));
        }
    }

    private double totalCompHeight() {
        double totalHeight = 1.0;
        for (Component component : children) {
            totalHeight += component.height + 1.0;
        }

        return totalHeight;
    }
}

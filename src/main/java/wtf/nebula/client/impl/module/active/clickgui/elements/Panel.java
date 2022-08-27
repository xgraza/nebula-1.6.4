package wtf.nebula.client.impl.module.active.clickgui.elements;

import net.minecraft.util.ResourceLocation;
import wtf.nebula.client.core.Launcher;
import wtf.nebula.client.impl.module.Module;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.active.Colors;
import wtf.nebula.client.utils.render.enums.Dimension;
import wtf.nebula.client.utils.render.RenderEngine;
import wtf.nebula.client.utils.render.renderers.impl.two.Box;
import wtf.nebula.client.utils.render.renderers.impl.two.TextureRenderer;

public class Panel extends Element {
    private static final ResourceLocation ARROW = new ResourceLocation("nebula/arrow.png");

    private boolean opened = true;
    private float angle = 180.0f;

    public Panel(ModuleCategory category) {
        super(category.displayName);

        width = 88.0;
        height = 15.0;

        for (Module module : Launcher.getInstance().getModuleManager().getModulesByCategory(category)) {
            elements.add(new ModuleElement(module));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderEngine.RenderStack stack = RenderEngine.of(Dimension.TWO);
        stack.add(new Box(x, y, width, height, Colors.getClientColor(77)));

        if (opened) {
            double totalHeight = 0.0;
            for (Element element : elements) {
                totalHeight += element.height + 1.5;
            }

            stack.add(new Box(x, y + height, width, totalHeight + 1.5, 0x77000000));
        }

        if (!opened) {
            if (angle > 0) {
                angle -= 3;
            }
        } else if (angle < 180) {
            angle += 3;
        }

        stack.add(new TextureRenderer(ARROW, (x + width) - 7, y + 7.5, 10.0, 10.0, RenderEngine.calculateRotation(angle)));

        stack.render();

        mc.fontRenderer.drawStringWithShadow(getLabel(), (int) (x + 3), (int) (y + 3), -1);

        if (opened) {
            double posY = y + height + 1.0;
            for (Element element : elements) {
                element.x = x + 2;
                element.y = posY;
                element.width = width - 4;

                element.drawScreen(mouseX, mouseY, partialTicks);

                posY += element.height + 1.5;
            }
        }
    }

    @Override
    public void mouseClick(int mouseX, int mouseY, int button) {
        if (isHovering(mouseX, mouseY)) {
            if (button == 0) {

            } else if (button == 1) {
                opened = !opened;
                Element.playClickSound();
            }
        }

        if (opened) {
            elements.forEach((element) -> element.mouseClick(mouseX, mouseY, button));
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (opened) {
            elements.forEach((element) -> element.keyTyped(typedChar, keyCode));
        }
    }
}

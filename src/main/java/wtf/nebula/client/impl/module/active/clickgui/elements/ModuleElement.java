package wtf.nebula.client.impl.module.active.clickgui.elements;

import net.minecraft.util.ResourceLocation;
import wtf.nebula.client.impl.module.Module;
import wtf.nebula.client.impl.module.ToggleableModule;
import wtf.nebula.client.impl.module.active.Colors;
import wtf.nebula.client.impl.module.active.clickgui.elements.properties.BindButton;
import wtf.nebula.client.impl.module.active.clickgui.elements.properties.BooleanButton;
import wtf.nebula.client.impl.module.active.clickgui.elements.properties.EnumButton;
import wtf.nebula.client.impl.module.active.clickgui.elements.properties.NumberButton;
import wtf.nebula.client.utils.render.RenderEngine;
import wtf.nebula.client.utils.render.enums.Dimension;
import wtf.nebula.client.utils.render.renderers.impl.two.Box;
import wtf.nebula.client.utils.render.renderers.impl.two.GradientBox;
import wtf.nebula.client.utils.render.renderers.impl.two.TextureRenderer;

public class ModuleElement extends Element {
    private static final ResourceLocation GEAR = new ResourceLocation("nebula/gear.png");

    private final Module module;
    private boolean opened = false;

    private boolean state = false;

    private int gearProgress = 0;

    public ModuleElement(Module module) {
        super(module.getLabel());
        this.module = module;

        height = 15;

        module.getPropertyMap().forEach((name, prop) -> {
            if (prop.getValue() instanceof Boolean) {
                elements.add(new BooleanButton(prop));
            } else if (prop.getValue() instanceof Number) {
                elements.add(new NumberButton(prop));
            } else if (prop.getValue() instanceof Enum) {
                elements.add(new EnumButton(prop));
            }
        });

        if (module instanceof ToggleableModule) {
            ToggleableModule m = (ToggleableModule) module;
            state = m.isRunning();
            elements.add(new BindButton(m));
        } else {
            state = true;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderEngine.RenderStack stack = RenderEngine.of(Dimension.TWO);

        int topColor = -1;
        if (state) {
            topColor = Colors.getClientColor(isHovering(mouseX, mouseY) ? 55 : 77);
        } else {
            topColor = isHovering(mouseX, mouseY) ? 0x77AAAAAB : 0x33555555;
        }

        int bottomColor = -1;
        if (state) {
            bottomColor = Colors.getClientColor(isHovering(mouseX, mouseY) ? 55 : 77);
        } else {
            bottomColor = isHovering(mouseX, mouseY) ? 0x66AAAAAB : 0x55555555;
        }

        stack.add(new GradientBox(x, y, width, 15, topColor, bottomColor));

        if (opened) {
            gearProgress++;
        }

        if (!elements.isEmpty()) {
            stack.add(new TextureRenderer(GEAR, (x + width) - 7, y + 7.5, 10.0, 10.0, RenderEngine.calculateRotation(gearProgress)));
        }

        stack.render();

        mc.fontRenderer.drawStringWithShadow(getLabel(), (int) (x + 3), (int) (y + 4), -1);

        if (opened) {
            height = 15;
            for (Element element : elements) {
                height += element.height + 0.5;
            }

            double posY = y + 16.0;
            for (Element element : elements) {
                element.x = x + 1;
                element.y = posY;
                element.height = 15;
                element.width = width - 2;

                element.drawScreen(mouseX, mouseY, partialTicks);

                posY += element.height + 0.5;
            }
        } else {
            height = 15;
        }
    }

    @Override
    public void mouseClick(int mouseX, int mouseY, int button) {
        if (isHovering(mouseX, mouseY)) {
            if (button == 0) {
                if (module instanceof ToggleableModule) {
                    state = !((ToggleableModule) module).isRunning();
                    ((ToggleableModule) module).setRunning(state);
                }

                Element.playClickSound();
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

    @Override
    protected boolean isHovering(int mouseX, int mouseY) {
        return (float)mouseX >= x && (float)mouseX <= x + (float)width && (float)mouseY >= y && (float)mouseY <= y + (float)15;
    }
}

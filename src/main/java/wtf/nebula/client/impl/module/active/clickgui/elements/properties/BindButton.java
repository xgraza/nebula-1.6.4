package wtf.nebula.client.impl.module.active.clickgui.elements.properties;

import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;
import wtf.nebula.client.impl.module.Module;
import wtf.nebula.client.impl.module.ToggleableModule;
import wtf.nebula.client.impl.module.active.clickgui.elements.Element;
import wtf.nebula.client.utils.render.RenderEngine;
import wtf.nebula.client.utils.render.enums.Dimension;
import wtf.nebula.client.utils.render.renderers.impl.two.GradientBox;

public class BindButton extends Element {
    private final ToggleableModule module;
    private boolean listening = false;

    public BindButton(ToggleableModule module) {
        super("Bind: ");
        this.module = module;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderEngine.RenderStack stack = RenderEngine.of(Dimension.TWO);

        if (isHovering(mouseX, mouseY)) {
            stack.add(new GradientBox(x, y, width, height, 0x77AAAAAB, 0x66AAAAAB));
        }

        stack.render();

        String text = getLabel() + EnumChatFormatting.GRAY + (module.getBind() >= 0 ? Keyboard.getKeyName(module.getBind()) : "Unknown");
        if (listening) {
            text = EnumChatFormatting.GRAY + "Listening...";
        }

        mc.fontRenderer.drawStringWithShadow(text, (int) (x + 2), (int) (y + 4), -1);
    }

    @Override
    public void mouseClick(int mouseX, int mouseY, int button) {
        if (isHovering(mouseX, mouseY) && button == 0) {
            Element.playClickSound();
            listening = !listening;
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (listening) {
            listening = false;

            if (keyCode == Keyboard.KEY_ESCAPE) {
                module.setBind(Keyboard.KEY_NONE);
            } else {
                module.setBind(keyCode);
            }
        }
    }
}

package wtf.nebula.client.impl.module.active.clickgui.elements;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import wtf.nebula.client.utils.client.Labeled;

import java.util.ArrayList;
import java.util.List;

public abstract class Element implements Labeled {
    public double x, y, width, height;
    private final String label;

    protected final List<Element> elements = new ArrayList<>();

    public Element(String label) {
        this.label = label;
    }

    public abstract void drawScreen(int mouseX, int mouseY, float partialTicks);
    public abstract void mouseClick(int mouseX, int mouseY, int button);
    public abstract void keyTyped(char typedChar, int keyCode);

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    protected boolean isHovering(int mouseX, int mouseY) {
        return (float)mouseX >= x && (float)mouseX <= x + (float)width && (float)mouseY >= y && (float)mouseY <= y + (float)this.height;
    }

    public static void playClickSound() {
        mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("minecraft:gui.button.press"), 1.0f));
    }

    public boolean isVisible() {
        return true;
    }
}

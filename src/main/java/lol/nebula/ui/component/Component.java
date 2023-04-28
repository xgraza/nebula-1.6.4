package lol.nebula.ui.component;

import net.minecraft.client.Minecraft;

import java.util.LinkedList;
import java.util.List;

/**
 * @author aesthetical
 * @since 04/27/23
 */
public abstract class Component {

    protected final Minecraft mc = Minecraft.getMinecraft();

    private final List<Component> children = new LinkedList<>();
    private double x, y, width, height;

    public abstract void render(int mouseX, int mouseY, float partialTicks);
    public abstract void mouseClicked(int mouseX, int mouseY, int mouseButton);
    public abstract void keyTyped(char typedChar, int keyCode);

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public List<Component> getChildren() {
        return children;
    }

    protected boolean isInBounds(int mouseX, int mouseY) {
        return isInBounds(mouseX, mouseY, x, y, width, height);
    }

    public static boolean isInBounds(int mouseX, int mouseY, double x, double y, double w, double h) {
        return mouseX >= x && x + w >= mouseX && mouseY >= y && y + h >= mouseY;
    }
}

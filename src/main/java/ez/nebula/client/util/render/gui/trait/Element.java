package ez.nebula.client.util.render.gui.trait;

import net.minecraft.client.Minecraft;

public class Element
{
    protected static final Minecraft MC = Minecraft.getMinecraft();
    protected double x, y, width, height;

    public double getX()
    {
        return x;
    }

    public void setX(double x)
    {
        this.x = x;
    }

    public double getY()
    {
        return y;
    }

    public void setY(double y)
    {
        this.y = y;
    }

    public double getWidth()
    {
        return width;
    }

    public void setWidth(double width)
    {
        this.width = width;
    }

    public double getHeight()
    {
        return height;
    }

    public void setHeight(double height)
    {
        this.height = height;
    }

    public boolean isMouseIn(final int mouseX, final int mouseY)
    {
        return isMouseIn(mouseX, mouseY, x, y, width, height);
    }

    public boolean isMouseInDynamic(final int mouseX, final int mouseY)
    {
        return isMouseIn(mouseX, mouseY, getX(), getY(), getWidth(), getHeight());
    }

    public boolean isMouseIn(final int mouseX, final int mouseY, final double x, final double y, final double w, final double h)
    {
        return x <= mouseX && x + w >= mouseX && y <= mouseY && y + h >= mouseY;
    }
}

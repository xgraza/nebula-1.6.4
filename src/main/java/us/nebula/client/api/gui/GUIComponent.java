package us.nebula.client.api.gui;

import java.util.LinkedList;
import java.util.List;

/**
 * @author xgraza
 * @since 02/28/25
 */
public abstract class GUIComponent
{
    protected final List<GUIComponent> childrenComponentList = new LinkedList<>();

    protected double x, y, width, height;

    public abstract void render(final int mouseX, final int mouseY, final float partialTicks);

    public void init()
    {

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

    public List<GUIComponent> getChildrenComponentList()
    {
        return childrenComponentList;
    }
}

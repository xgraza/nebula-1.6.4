package us.nebula.api.gui;

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

    protected boolean isMouseIn(final int mouseX, final int mouseY)
    {
        return x <= mouseX && x + width >= mouseX && y <= mouseY && y + height >= mouseY;
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

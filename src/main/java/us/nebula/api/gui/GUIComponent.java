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

    public abstract void render(final int mouseX, final int mouseY, final float partialTicks);

    public List<GUIComponent> getChildrenComponentList()
    {
        return childrenComponentList;
    }
}

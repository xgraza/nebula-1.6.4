package ez.nebula.client.impl.gui.hud.component;

import ez.nebula.client.Nebula;
import ez.nebula.client.impl.gui.clickgui.component.CategoryPanel;

/**
 * @author xgraza
 * @since 3/23/26
 */
public final class HUDElementCategoryPanel extends CategoryPanel
{
    public HUDElementCategoryPanel()
    {
        super("Elements", 'c');
        Nebula.INSTANCE.getHUDManager().getAll().forEach(
                (element) -> getChildrenComponentList().add(new HUDElementPanel(element)));
        setAllowScrolling(true);
        setAllowDragging(true);
    }
}

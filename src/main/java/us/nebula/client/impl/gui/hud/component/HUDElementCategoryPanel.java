package us.nebula.client.impl.gui.hud.component;

import us.nebula.client.Nebula;
import us.nebula.client.api.gui.font.Fonts;
import us.nebula.client.impl.gui.client.component.CategoryPanel;

/**
 * @author xgraza
 * @since 3/23/26
 */
public final class HUDElementCategoryPanel extends CategoryPanel
{
    public HUDElementCategoryPanel()
    {
        super("Elements");
        Nebula.INSTANCE.getHUDManager().getAll().forEach(
                (element) -> getChildrenComponentList().add(new HUDElementPanel(element)));
        setAllowScrolling(true);
        setAllowDragging(true);
    }

    @Override
    protected void drawHeaderText()
    {
        Fonts.TYPEFACE.drawStringShadow("c", x + PADDING, y + 5, 0xAAAAAA);
        Fonts.POPPINS.drawStringShadow(name, x + 12 + PADDING, y + 2, -1);
    }
}

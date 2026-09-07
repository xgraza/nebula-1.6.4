package ez.nebula.client.impl.gui.clickgui.component.hud;

import ez.nebula.client.Nebula;
import ez.nebula.client.impl.gui.clickgui.component.CategoryPanel;
import ez.nebula.client.impl.module.render.ClickGUIModule;
import ez.nebula.client.util.render.font.Fonts;

/**
 * @author xgraza
 * @since 3/23/26
 */
public final class HUDElementCategoryPanel extends CategoryPanel
{
    public HUDElementCategoryPanel()
    {
        super("HUD", 'c');
        Nebula.INSTANCE.getHudManager2().getAll().forEach(
                (element) -> getChildrenComponentList().add(new HUDElementPanel(element)));
        setAllowScrolling(true);
        setAllowDragging(true);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        setScrollSpeed(ClickGUIModule.INSTANCE.scrollSpeedSetting.getValue());
        super.render(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void drawHeaderText()
    {
        super.drawHeaderText();

        final String componentSizeText = String.valueOf(childrenComponentList.size());
        final double textWidth = Fonts.POPPINS.getStringWidth(componentSizeText);
        Fonts.POPPINS.drawStringShadow(componentSizeText, x + width - (PADDING * 2) - textWidth, y + 2, 0xAAAAAA);
    }
}

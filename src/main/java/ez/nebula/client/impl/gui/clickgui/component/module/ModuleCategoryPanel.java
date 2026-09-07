package ez.nebula.client.impl.gui.clickgui.component.module;

import ez.nebula.client.Nebula;
import ez.nebula.client.util.render.font.Fonts;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.impl.gui.clickgui.component.CategoryPanel;
import ez.nebula.client.impl.module.render.ClickGUIModule;

/**
 * @author xgraza
 * @since 03/01/25
 */
public final class ModuleCategoryPanel extends CategoryPanel
{
    public ModuleCategoryPanel(final ModuleCategory category)
    {
        super(category.toString(), category.getIcon());
        Nebula.MODULES.getAll()
                .stream()
                .filter((module) -> module.getManifest().category().equals(category))
                .forEach((module) -> childrenComponentList.add(new ModuleComponent(module)));
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

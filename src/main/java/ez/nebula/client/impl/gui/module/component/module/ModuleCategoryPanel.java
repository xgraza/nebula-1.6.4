package ez.nebula.client.impl.gui.module.component.module;

import ez.nebula.client.core.Nebula;
import ez.nebula.client.api.render.font.Fonts;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.impl.gui.module.component.CategoryPanel;

/**
 * @author xgraza
 * @since 03/01/25
 */
public final class ModuleCategoryPanel extends CategoryPanel
{
    private final String categoryIcon;

    public ModuleCategoryPanel(final ModuleCategory category)
    {
        super(category.toString());
        Nebula.INSTANCE.getModuleManager().getAll()
                .stream()
                .filter((module) -> module.getManifest().category().equals(category))
                .forEach((module) -> childrenComponentList.add(new ModulePanel(module)));
        categoryIcon = category.getIcon();
        setAllowScrolling(true);
        setAllowDragging(true);
    }

    @Override
    protected void drawHeaderText()
    {
        Fonts.TYPEFACE.drawStringShadow(categoryIcon, x + PADDING, y + 5, 0xAAAAAA);
        Fonts.POPPINS.drawStringShadow(name, x + 12 + PADDING, y + 2, -1);
        String text = String.valueOf(childrenComponentList.size());
        double textWidth = Fonts.POPPINS.getStringWidth(text);
        Fonts.POPPINS.drawStringShadow(text, x + width - (PADDING * 2) - textWidth, y + 2, 0xAAAAAA);
    }
}

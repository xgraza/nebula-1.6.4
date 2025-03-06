package us.nebula.impl.gui.client.component.cheat;

import us.nebula.Nebula;
import us.nebula.api.gui.font.Fonts;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.impl.gui.client.component.CategoryPanel;

/**
 * @author xgraza
 * @since 03/01/25
 */
public final class CheatCategoryPanel extends CategoryPanel
{
    private final String categoryIcon;

    public CheatCategoryPanel(final CheatCategory category)
    {
        super(category.toString());
        Nebula.INSTANCE.getCheatManager().getAll()
                .stream()
                .filter((cheat) -> cheat.getManifest().category().equals(category))
                .forEach((cheat) -> childrenComponentList.add(new CheatPanel(cheat)));
        categoryIcon = category.getIcon();
    }

    @Override
    protected void drawHeaderText()
    {
        Fonts.TYPEFACE.drawStringShadow(categoryIcon, x + PADDING, y + 5, 0xAAAAAA);
        Fonts.POPPINS.drawStringShadow(name, x + 12 + PADDING, y + 2, -1);
    }
}

package us.nebula.impl.gui.cheat;

import net.minecraft.client.gui.GuiScreen;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.impl.gui.cheat.component.CheatCategoryPanel;

import java.util.LinkedList;
import java.util.List;

/**
 * @author xgraza
 * @since 03/01/25
 */
public final class ClickGUIScreen extends GuiScreen
{
    private final List<CheatCategoryPanel> categoryPanels = new LinkedList<>();

    public ClickGUIScreen()
    {
        double posX = 16.0;
        for (final CheatCategory category : CheatCategory.values())
        {
            final CheatCategoryPanel panel = new CheatCategoryPanel(category);
            panel.setX(posX);
            panel.setY(26.0);
            posX += panel.getWidth() + 5;
            categoryPanels.add(panel);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        for (final CheatCategoryPanel panel : categoryPanels)
        {
            panel.render(mouseX, mouseY, partialTicks);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        for (final CheatCategoryPanel panel : categoryPanels)
        {
            panel.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode)
    {
        super.keyTyped(typedChar, keyCode);
        for (final CheatCategoryPanel panel : categoryPanels)
        {
            panel.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }
}

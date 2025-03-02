package us.nebula.impl.gui.cheat.component;

import us.nebula.Nebula;
import us.nebula.api.gui.GUIComponent;
import us.nebula.api.gui.IGUIInputListener;
import us.nebula.api.gui.animation.Animation;
import us.nebula.api.gui.animation.AnimationEasing;
import us.nebula.api.gui.font.FontUtil;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.util.RenderUtil;

import java.awt.Color;

/**
 * @author xgraza
 * @since 03/01/25
 */
public final class CheatCategoryPanel extends GUIComponent implements IGUIInputListener
{
    private static final double PADDING = 2.0;
    private static final double PANEL_HEADER_HEIGHT = 16.0;
    private static final int PANEL_HEADER_COLOR = new Color(33, 33, 33).getRGB();
    private static final int PANEL_BACKGROUND_COLOR = new Color(48, 48, 48).getRGB();

    private final Animation animation = new Animation(
            AnimationEasing.EXPO_IN_OUT, 300.0);

    private final String categoryName, categoryIcon;

    public CheatCategoryPanel(final CheatCategory category)
    {
        Nebula.INSTANCE.getCheatManager().getAll()
                .stream()
                .filter((cheat) -> cheat.getManifest().category().equals(category))
                .forEach((cheat) ->
                {
                    childrenComponentList.add(new CheatPanel(cheat));
                });
        animation.setState(true);
        categoryName = category.toString();
        categoryIcon = category.getIcon();

        setHeight(PANEL_HEADER_HEIGHT);
        setWidth(125.0);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        RenderUtil.startScissor(x, y - 1, width, getHeight() + PADDING);

        RenderUtil.roundedRectangle2D(x, y, width, getHeight(), 6, PANEL_HEADER_COLOR);
        RenderUtil.roundedRectangle2D(x + PADDING, y + height, width - (PADDING * 2), getHeight() - height - PADDING, 2.8f, PANEL_BACKGROUND_COLOR);

        FontUtil.getFont("icon", 18).drawStringShadow(categoryIcon, x + PADDING, y + 5, 0xAAAAAA);
        FontUtil.drawStringShadow(categoryName, x + 12 + PADDING, y + 2, -1);

        if (animation.getFactor() > 0.0)
        {
            double posY = y + PANEL_HEADER_HEIGHT + PADDING;
            for (final GUIComponent component : getChildrenComponentList())
            {
                component.setX(x + PADDING);
                component.setY(posY);
                component.setWidth(width - (PADDING * 2));
                component.setHeight(PANEL_HEADER_HEIGHT - 1.5);

                component.render(mouseX, mouseY, partialTicks);

                posY += component.getHeight();
            }
        }

        RenderUtil.endScissor();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (isMouseIn(mouseX, mouseY))
        {
            if (mouseButton == 0)
            {

            } else if (mouseButton == 1)
            {
                animation.setState(!animation.getState());
            }
            return;
        }
        for (final GUIComponent component : getChildrenComponentList())
        {
            if (component instanceof IGUIInputListener)
            {
                ((IGUIInputListener) component).mouseClicked(mouseX, mouseY, mouseButton);
            }
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode)
    {
        for (final GUIComponent component : getChildrenComponentList())
        {
            if (component instanceof IGUIInputListener)
            {
                ((IGUIInputListener) component).keyTyped(typedChar, keyCode);
            }
        }
    }

    @Override
    public double getHeight()
    {
        double h = 0.0;
        for (final GUIComponent component : getChildrenComponentList())
        {
            h += component.getHeight();
        }
        return (super.getHeight() + PADDING)
                + ((h + PADDING) * animation.getEasedFactor());
    }
}

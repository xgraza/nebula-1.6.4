package ez.nebula.client.impl.gui.clickgui.component.value;

import ez.nebula.client.util.render.gui.Render2D;
import ez.nebula.client.util.render.font.Fonts;
import ez.nebula.client.util.render.gui.trait.GUIComponent;
import ez.nebula.client.util.render.gui.trait.IGUIInputListener;
import ez.nebula.client.api.setting.Setting;
import ez.nebula.client.api.setting.block.BlockSetting;
import ez.nebula.client.api.setting.block.BlockValue;
import ez.nebula.client.impl.gui.clickgui.component.ComponentWithSetting;
import ez.nebula.client.impl.gui.clickgui.component.value.block.BlockSearchComponent;
import ez.nebula.client.impl.gui.clickgui.component.value.block.BlockSelectionCallback;
import net.minecraft.item.ItemStack;

import java.awt.Color;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author xgraza
 * @since 6/4/26
 */
public final class BlockSettingComponent extends GUIComponent implements IGUIInputListener, ComponentWithSetting, BlockSelectionCallback
{
    private static final int KEY_BACKGROUND_COLOR = new Color(33, 33, 33).getRGB();
    private static final double PADDING = 1.0;

    private final BlockSetting setting;

    private boolean opened;

    public BlockSettingComponent(BlockSetting setting)
    {
        this.setting = setting;

        getChildrenComponentList().add(new BlockSearchComponent(this));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        final double middle = Fonts.getMiddlePoint(height, Fonts.POPPINS.getFontHeight());
        Fonts.POPPINS.drawStringShadow(setting.getName(), x + (PADDING * 2), y + middle, -1);
        drawItemAndText();

        if (opened)
        {
            double posY = y + height + PADDING;
            for (final GUIComponent component : getChildrenComponentList())
            {
                component.setX(getX() + PADDING);
                component.setY(posY);
                component.setWidth(getWidth() - (PADDING * 2));

                component.render(mouseX, mouseY, partialTicks);

                posY += component.getHeight() + (PADDING * 2);
            }
        }
    }

    @Override
    public void selectBlock(BlockValue blockValue)
    {
        setting.setValue(blockValue);
    }

    private void drawItemAndText()
    {
        final String text = setting.getBlock().getLocalizedName();

        final double middle = Fonts.getMiddlePoint(height, Fonts.POPPINS_SMALL.getFontHeight());
        final double textWidth = Fonts.POPPINS_SMALL.getStringWidth(text);
        final double posX = getX() + getWidth() - 16 - textWidth - PADDING;
        final double posY = y + middle;

        Render2D.roundedRectangle(posX, posY,
                textWidth + (PADDING * 2), Fonts.POPPINS_SMALL.getFontHeight(), 3.5f, KEY_BACKGROUND_COLOR);
        Fonts.POPPINS_SMALL.drawStringShadow(text, posX + 1, posY, -1);

        glPushMatrix();
        {
            glTranslated(getX() + getWidth() - 14, y + middle, 0);
            glScaled(0.8, 0.8, 0.8);
            Render2D.itemNoEffects(new ItemStack(setting.getBlock(), 1, setting.getSubType()), 0, 0);
        }
        glPopMatrix();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (isMouseInDynamic(mouseX, mouseY) && mouseButton == 1)
        {
            opened = !opened;
        }
        if (opened)
        {
            for (final GUIComponent component : getChildrenComponentList())
            {
                if (component instanceof IGUIInputListener)
                {
                    ((IGUIInputListener) component).mouseClicked(mouseX, mouseY, mouseButton);
                }
            }
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode)
    {
        if (opened)
        {
            for (final GUIComponent component : getChildrenComponentList())
            {
                if (component instanceof IGUIInputListener)
                {
                    ((IGUIInputListener) component).keyTyped(typedChar, keyCode);
                }
            }
        }
    }

    @Override
    public boolean isVisible()
    {
        return setting.isVisible();
    }

    @Override
    public double getHeight()
    {
        double height = super.getHeight();
        if (opened)
        {
            height += PADDING;
            for (final GUIComponent component : getChildrenComponentList())
            {
                height += component.getHeight() + PADDING;
            }
            height += PADDING;
        }
        return height;
    }

    @Override
    public Setting<?> getSetting()
    {
        return setting;
    }
}

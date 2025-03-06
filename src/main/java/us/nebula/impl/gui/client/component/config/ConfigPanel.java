package us.nebula.impl.gui.client.component.config;

import us.nebula.api.gui.GUIComponent;
import us.nebula.api.gui.IGUIInputListener;
import us.nebula.api.gui.font.Fonts;

import java.io.File;

/**
 * @author xgraza
 * @since 03/06/25
 */
public final class ConfigPanel extends GUIComponent implements IGUIInputListener
{
    private static final double PADDING = 1.0;

    private final File configFile;
    private final String text;

    public ConfigPanel(final File configFile)
    {
        this.configFile = configFile;
        text = configFile.getName().replace(".cfg", "");

        childrenComponentList.add(new ConfigTextButton(this, "Load")
        {
            @Override
            public void onButtonPress()
            {
                System.out.println("Pressed load");
            }
        });
        childrenComponentList.add(new ConfigTextButton(this, "Save")
        {
            @Override
            public void onButtonPress()
            {
                System.out.println("Pressed save");
            }
        });
        childrenComponentList.add(new ConfigTextButton(this, "Delete")
        {
            @Override
            public void onButtonPress()
            {
                System.out.println("Pressed delete");
            }
        });
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        Fonts.POPPINS.drawStringShadow(text, x + (PADDING * 4),
                y + Fonts.getMiddlePoint(height, Fonts.POPPINS.getFontHeight()), -1);

        double posX = x + width - getComponentsSize();
        for (final GUIComponent component : getChildrenComponentList())
        {
            component.setX(posX);
            component.setY(y + Fonts.getMiddlePoint(getHeight(), component.getHeight()));

            component.render(mouseX, mouseY, partialTicks);
            posX += component.getWidth() + (PADDING * 3);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        for (final GUIComponent component : getChildrenComponentList())
        {
            if (component instanceof IGUIInputListener)
            {
                ((IGUIInputListener)component).mouseClicked(mouseX, mouseY, mouseButton);
            }
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode)
    {

    }

    private double getComponentsSize()
    {
        double width = 0.0;
        for (final GUIComponent component : getChildrenComponentList())
        {
            width += component.getWidth() + (PADDING * 3);
        }
        return width;
    }

    public File getConfigFile()
    {
        return configFile;
    }
}

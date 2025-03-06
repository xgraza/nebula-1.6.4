package us.nebula.impl.gui.client.component.config;

import us.nebula.Nebula;
import us.nebula.api.gui.font.Fonts;
import us.nebula.impl.gui.client.component.CategoryPanel;

import java.io.File;

/**
 * @author xgraza
 * @since 03/06/25
 */
public final class ConfigCategoryPanel extends CategoryPanel
{
    private static final File CONFIGS_DIR = new File(
            Nebula.INSTANCE.getNebulaRootDir(), "configs");

    public ConfigCategoryPanel()
    {
        super("Configs");
    }

    @Override
    public void init()
    {
        childrenComponentList.clear();
        final File[] files = CONFIGS_DIR.listFiles();
        if (files == null)
        {
            return;
        }
        for (final File file : files)
        {
            if (!file.getName().endsWith(".cfg"))
            {
                continue;
            }
            childrenComponentList.add(new ConfigPanel(file));
        }
    }

    @Override
    protected void drawHeaderText()
    {
        Fonts.TYPEFACE.drawStringShadow("C", x + PADDING, y + 5, 0xAAAAAA);
        Fonts.POPPINS.drawStringShadow(name, x + 12 + PADDING, y + 2, -1);
    }
}

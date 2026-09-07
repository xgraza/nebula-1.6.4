package ez.nebula.client.impl.gui.clickgui.component.config;

import ez.nebula.client.Nebula;
import ez.nebula.client.impl.gui.clickgui.component.CategoryPanel;

import java.io.File;

/**
 * @author xgraza
 * @since 03/06/25
 */
public final class ConfigCategoryPanel extends CategoryPanel
{
    private static final File CONFIGS_DIR = new File(
            Nebula.INSTANCE.getNebulaRootDir(), "configs");

    private final ConfigTextButton button;

    public ConfigCategoryPanel()
    {
        super("Configs", 'C');
        button = new ConfigTextButton("New Config", this::onCreateConfig);
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
            childrenComponentList.add(new ConfigPanel(this, file));
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        super.render(mouseX, mouseY, partialTicks);

        button.setX(x + width - (PADDING * 2) - button.getWidth());
        button.setY(y + (PADDING * 2));
        button.render(mouseX, mouseY, partialTicks);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        button.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private void onCreateConfig()
    {
        final ConfigPanel panel = new ConfigPanel(
                ConfigCategoryPanel.this, null);
        panel.setEditing(true);
        childrenComponentList.add(panel);
    }
}

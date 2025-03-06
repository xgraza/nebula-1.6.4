package us.nebula.impl.gui.client.component.config;

import org.lwjgl.input.Keyboard;
import us.nebula.Nebula;
import us.nebula.api.gui.GUIComponent;
import us.nebula.api.gui.IGUIInputListener;
import us.nebula.api.gui.font.Fonts;
import us.nebula.api.manager.cheat.CheatConfig;
import us.nebula.impl.gui.client.ClickGUIScreen;

import java.io.File;
import java.io.IOException;

import static org.lwjgl.input.Keyboard.*;

/**
 * @author xgraza
 * @since 03/06/25
 */
public final class ConfigPanel extends GUIComponent implements IGUIInputListener
{
    private static final double PADDING = 1.0;

    private final ConfigCategoryPanel panel;

    private String configName = "";
    private boolean editing, showCursor;
    private long lastUpdateTimeMS = -1;

    public ConfigPanel(final ConfigCategoryPanel panel, final File configFile)
    {
        this.panel = panel;
        if (configFile != null)
        {
            configName = configFile.getName().replace(".cfg", "");
        }

        childrenComponentList.add(new ConfigTextButton("Load")
        {
            @Override
            public void onButtonPress()
            {
                onLoadConfig();
            }
        });
        childrenComponentList.add(new ConfigTextButton("Save")
        {
            @Override
            public void onButtonPress()
            {
                onSaveConfig();
            }
        });
        childrenComponentList.add(new ConfigTextButton("Delete")
        {
            @Override
            public void onButtonPress()
            {
                onDeleteConfig();
            }
        });
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        renderText();

        if (editing)
        {
            return;
        }
        double posX = x + width - getComponentsSize();
        for (final GUIComponent component : getChildrenComponentList())
        {
            component.setX(posX);
            component.setY(y + Fonts.getMiddlePoint(getHeight(), component.getHeight()));

            component.render(mouseX, mouseY, partialTicks);
            posX += component.getWidth() + (PADDING * 3);
        }
    }

    private void renderText()
    {
        if (editing)
        {
            if (System.currentTimeMillis() - lastUpdateTimeMS > 250L)
            {
                lastUpdateTimeMS = System.currentTimeMillis();
                showCursor = !showCursor;
            }

            Fonts.POPPINS.drawStringShadow(configName + (showCursor ? "_" : ""), x + (PADDING * 4),
                    y + Fonts.getMiddlePoint(height, Fonts.POPPINS.getFontHeight()), -1);

            return;
        }
        Fonts.POPPINS.drawStringShadow(configName, x + (PADDING * 4),
                y + Fonts.getMiddlePoint(height, Fonts.POPPINS.getFontHeight()), -1);
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
        if (!editing)
        {
            return;
        }
        if (keyCode == KEY_BACK || keyCode == KEY_DELETE)
        {
            if (configName == null || configName.isEmpty())
            {
                return;
            }
            if (Keyboard.isKeyDown(KEY_LCONTROL))
            {
                configName = "";
            } else
            {
                configName = configName.substring(0, Math.max(0, configName.length() - 1));
            }
        } else if (keyCode == KEY_ESCAPE)
        {
            ClickGUIScreen.ALLOW_EXIT_ON_ESC = true;
            panel.getChildrenComponentList().remove(this);
        } else if (keyCode == KEY_RETURN)
        {
            if (configName == null || configName.isEmpty())
            {
                return;
            }
            createConfig();
        } else
        {
            if (Character.isLetter(typedChar) || Character.isDigit(typedChar))
            {
                configName += typedChar;
            }
        }
    }

    private void createConfig()
    {
        final File file = new File(CheatConfig.CHEAT_CONFIG_DIR, configName + ".cfg");
        if (file.exists())
        {
            Nebula.INSTANCE.getToastManager().error(
                    "Cheat Config",
                    "A config with that name already exists.",
                    1700L);
            return;
        }
        try
        {
            CheatConfig.saveConfig(configName);
            Nebula.INSTANCE.getToastManager().info(
                    "Cheat Config",
                    "Created config " + configName + " successfully.",
                    1700L);
        } catch (final IOException e)
        {
            Nebula.INSTANCE.getToastManager().error(
                    "Cheat Config",
                    "Failed to create new config file.",
                    1700L);
            Nebula.INSTANCE.getLogger().error(e);
        }
        editing = false;
    }

    private void onLoadConfig()
    {
        try
        {
            CheatConfig.loadConfig(configName);
            Nebula.INSTANCE.getToastManager().info(
                    "Cheat Config",
                    "Config " + configName + " was loaded successfully",
                    1700L);
        } catch (final IOException e)
        {
            Nebula.INSTANCE.getLogger().error(e);
            Nebula.INSTANCE.getToastManager().error(
                    "Cheat Config",
                    "Failed to load config",
                    1700L);
        }
    }

    private void onSaveConfig()
    {
        try
        {
            CheatConfig.saveConfig(configName);
            Nebula.INSTANCE.getToastManager().info(
                    "Cheat Config",
                    "Config " + configName + " was saved successfully",
                    1700L);
        } catch (final IOException e)
        {
            Nebula.INSTANCE.getLogger().error(e);
            Nebula.INSTANCE.getToastManager().error(
                    "Cheat Config",
                    "Failed to save config",
                    1700L);
        }
    }

    private void onDeleteConfig()
    {
        final File file = new File(CheatConfig.CHEAT_CONFIG_DIR, configName + ".cfg");
        if (file.delete())
        {
            panel.getChildrenComponentList().remove(ConfigPanel.this);
            Nebula.INSTANCE.getToastManager().info(
                    "Cheat Config",
                    "Config " + configName + " was deleted successfully",
                    1700L);
        }
        else
        {
            Nebula.INSTANCE.getToastManager().error(
                    "Cheat Config",
                    "Failed to delete config",
                    1700L);
        }
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

    public void setEditing(boolean editing)
    {
        ClickGUIScreen.ALLOW_EXIT_ON_ESC = !editing;
        this.editing = editing;
    }
}

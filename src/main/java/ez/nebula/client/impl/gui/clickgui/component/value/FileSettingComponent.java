package ez.nebula.client.impl.gui.clickgui.component.value;

import ez.nebula.client.Nebula;
import ez.nebula.client.api.setting.Setting;
import ez.nebula.client.impl.gui.clickgui.component.ComponentWithSetting;
import ez.nebula.client.impl.gui.clickgui.component.IComponentDescription;
import ez.nebula.client.util.render.font.Fonts;
import ez.nebula.client.util.render.gui.Render2D;
import ez.nebula.client.util.render.gui.trait.GUIComponent;
import ez.nebula.client.util.render.gui.trait.IGUIInputListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.io.File;

/**
 * @author xgraza
 * @since 03/25/25
 */
public final class FileSettingComponent extends GUIComponent implements IGUIInputListener, ComponentWithSetting, IComponentDescription
{
    private static final Logger LOGGER = LogManager.getLogger("FSC");
    
    private static final int BACKGROUND_COLOR = new Color(52, 52, 52).getRGB();
    private static final double PADDING = 1.0;

    private final File baseDirectory;
    private final Setting<File> setting;
    private boolean pickingFile;
    private File selectedFile;

    public FileSettingComponent(final File baseDirectory, final Setting<File> setting)
    {
        this.baseDirectory = baseDirectory;
        this.setting = setting;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        final double middle = Fonts.getMiddlePoint(height, Fonts.POPPINS.getFontHeight());
        Fonts.POPPINS.drawStringShadow(setting.getName(), x + (PADDING * 2), y + middle, -1);
        drawFile(middle);
    }

    private void drawFile(final double middlePoint)
    {
        if (!pickingFile && selectedFile != null)
        {
            setting.setValue(selectedFile);
            pickingFile = false;
            selectedFile = null;
        }

        String name;
        if (setting.getValue() == null)
        {
            if (pickingFile)
            {
                name = "Selecting file...";
            } else
            {
                name = "Pick file...";
            }
        } else
        {
            name = setting.getValue().getName();
        }
        final double boxWidth = Fonts.POPPINS_SMALL.getStringWidth(name) + (PADDING * 4);
        final double boxHeight = Fonts.POPPINS_SMALL.getFontHeight() + (PADDING * 2);

        final double boxPosX = (x + width) - boxWidth - (PADDING * 2);
        final double boxPosY = y - (middlePoint - ((boxHeight - (PADDING * 2)) / 2.0));

        Render2D.roundedRectangle(boxPosX, boxPosY, boxWidth, boxHeight, 3.5f, BACKGROUND_COLOR);
        Fonts.POPPINS_SMALL.drawStringShadow(name, boxPosX + (PADDING * 2), boxPosY + PADDING, -1);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (isMouseIn(mouseX, mouseY))
        {
            if (mouseButton == 0)
            {
                if (GraphicsEnvironment.isHeadless())
                {
                    Nebula.TOASTS.error("File Chooser",
                            "Your graphics environment is headless. Please report to the developer",
                            7500L);
                    return;
                }
                if (pickingFile)
                {
                    return;
                }
                pickingFile = true;
                SwingUtilities.invokeLater(() ->
                {
                    final Frame frame = new Frame();
                    frame.setLocationRelativeTo(null);
                    frame.setAlwaysOnTop(true);
                    final FileDialog dialog = new FileDialog(frame, "Choose spammer file..", FileDialog.LOAD);
                    dialog.setDirectory(baseDirectory.getAbsolutePath());
                    dialog.setFile("*.txt");
                    dialog.setVisible(true);

                    final String dir = dialog.getDirectory();
                    final String name = dialog.getFile();
                    if (dir != null && name != null)
                    {
                        selectedFile = new File(dir, name);
                        if (!selectedFile.exists())
                        {
                            LOGGER.error("Selected file {} does not exist?", selectedFile);
                            selectedFile = null;
                        } else
                        {
                            LOGGER.info("Selected file {}", selectedFile);
                        }
                    } else
                    {
                        LOGGER.warn("Failed to select file");
                    }

                    pickingFile = false;
                    frame.dispose();
                });
            } else if (mouseButton == 2)
            {
                setting.setValue(null);
            }
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode)
    {

    }

    @Override
    public boolean isVisible()
    {
        return setting.isVisible();
    }

    @Override
    public Setting<File> getSetting()
    {
        return setting;
    }

    @Override
    public String getDescription()
    {
        return setting.getDescription();
    }
}

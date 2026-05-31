package us.nebula.client.cheat.gui.component.cheat.value;

import us.nebula.client.Nebula;
import us.nebula.client.setting.Setting;
import us.nebula.client.util.render.gui.GUIComponent;
import us.nebula.client.util.render.gui.IGUIInputListener;
import us.nebula.client.util.render.gui.font.Fonts;
import us.nebula.client.util.render.RenderUtil;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * @author xgraza
 * @since 03/25/25
 */
public final class FileSettingComponent extends GUIComponent implements IGUIInputListener
{
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

        RenderUtil.renderRoundedRectangle(boxPosX, boxPosY, boxWidth, boxHeight, 3.5f, BACKGROUND_COLOR);
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
                    Nebula.INSTANCE.getToastManager().error("File Chooser",
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
                            Nebula.INSTANCE.getLogger().error("Selected file {} does not exist?", selectedFile);
                            selectedFile = null;
                        } else
                        {
                            Nebula.INSTANCE.getLogger().info("Selected file {}", selectedFile);
                        }
                    } else
                    {
                        Nebula.INSTANCE.getLogger().warn("Failed to select file");
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
}

package us.nebula.impl.gui.client.component.cheat.value;

import us.nebula.Nebula;
import us.nebula.api.gui.GUIComponent;
import us.nebula.api.gui.IGUIInputListener;
import us.nebula.api.gui.font.Fonts;
import us.nebula.api.value.Setting;
import us.nebula.util.render.RenderUtil;

import javax.swing.*;
import java.awt.Color;
import java.io.File;

/**
 * @author xgraza
 * @since 03/25/25
 */
public final class FileSettingComponent extends GUIComponent implements IGUIInputListener
{
    private static final double PADDING = 1.0;

    private static final int BACKGROUND_COLOR = new Color(52, 52, 52).getRGB();

    private final Setting<File> setting;

    public FileSettingComponent(final Setting<File> setting)
    {
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
        String name = "";
        if (setting.getValue() == null)
        {
            name = "Pick file...";
        } else
        {
            name = setting.getValue().getName();
        }
        final double boxWidth = Fonts.POPPINS_SMALL.getStringWidth(name) + (PADDING * 4);
        final double boxHeight = Fonts.POPPINS_SMALL.getFontHeight() + (PADDING * 2);

        final double boxPosX = (x + width) - boxWidth - (PADDING * 2);
        final double boxPosY = y - (middlePoint - ((boxHeight - (PADDING * 2)) / 2.0));

        RenderUtil.roundedRectangle2D(boxPosX, boxPosY, boxWidth, boxHeight, 3.5f, BACKGROUND_COLOR);
        Fonts.POPPINS_SMALL.drawStringShadow(name, boxPosX + (PADDING * 2), boxPosY + PADDING, -1);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (isMouseIn(mouseX, mouseY))
        {
            if (mouseButton == 0)
            {
                final File file = openFileChooser();
                if (file == null)
                {
                    return;
                }
                setting.setValue(file);
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

    private File openFileChooser()
    {
        final JFrame frame = new JFrame("Choose file...");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);

        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(setting.getBaseDirectory());

        final int result = fileChooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION)
        {
            return fileChooser.getSelectedFile();
        } else
        {
            Nebula.INSTANCE.getLogger().warn("Unexpected JFileChooser result: {}", result);
        }
        return null;
    }
}

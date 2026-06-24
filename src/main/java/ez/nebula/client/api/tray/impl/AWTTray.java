package ez.nebula.client.api.tray.impl;

import ez.nebula.client.api.tray.ITray;
import ez.nebula.client.util.io.FileUtil;

import javax.imageio.ImageIO;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public final class AWTTray implements ITray
{
    private static final String ICON_LOCATION = "/assets/nebula/texture/icon/16x.png";

    private final TrayIcon trayIcon;

    public AWTTray() throws Exception
    {
        if (!SystemTray.isSupported())
        {
            throw new RuntimeException("System Tray is not supposed by your WM/DE!");
        }
        trayIcon = new TrayIcon(Objects.requireNonNull(readIconImage()), "Nebula");
        SystemTray.getSystemTray().add(trayIcon);
    }

    @Override
    public void display(String title, String content)
    {
        trayIcon.displayMessage(content, title, TrayIcon.MessageType.INFO);
    }

    private BufferedImage readIconImage()
    {
        try
        {
            final InputStream stream = FileUtil.getResourceStream(ICON_LOCATION);
            if (stream == null)
            {
                return null;
            }
            final BufferedImage image = ImageIO.read(stream);
            stream.close();
            return image;
        } catch (final IOException e)
        {
            return null;
        }
    }
}

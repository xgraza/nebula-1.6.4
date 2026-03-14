package us.nebula.client.api.systemtray;

import org.lwjgl.opengl.Display;
import us.nebula.client.Nebula;
import us.nebula.client.util.io.FileUtil;

import javax.imageio.ImageIO;
import java.awt.AWTException;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author xgraza
 * @since 02/15/25
 */
public final class NebulaSystemTray
{
    private static final String ICON_LOCATION = "/assets/nebula/texture/icon/16x.png";

    private TrayIcon icon;

    public void init()
    {
        if (!SystemTray.isSupported())
        {
            Nebula.INSTANCE.getLogger().error("SystemTray is not supported by your WM/DE");
            return;
        }

        Nebula.INSTANCE.getLogger().info("Setting up tray icon");

        final InputStream stream = FileUtil.getResourceStream(ICON_LOCATION);
        if (stream == null)
        {
            Nebula.INSTANCE.getLogger().error("Resource stream could not be opened");
            return;
        }

        BufferedImage image;
        try
        {
            image = ImageIO.read(stream);
            stream.close();
        } catch (IOException e)
        {
            Nebula.INSTANCE.getLogger().error("Icon could not be parsed to a BufferedImage");
            throw new RuntimeException(e);
        }

        icon = new TrayIcon(image, "Nebula Client");
        icon.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                if (!Display.isActive())
                {

                }
            }
        });

        try
        {
            SystemTray.getSystemTray().add(icon);
            Nebula.INSTANCE.getLogger().info("Tray icon was successfully added!");
        } catch (AWTException e)
        {
            Nebula.INSTANCE.getLogger().error(e);
            icon = null;
        }
    }

    public void notify(final String content)
    {
        if (icon != null)
        {
            icon.displayMessage("", content, TrayIcon.MessageType.NONE);
        }
    }

    public void destroy()
    {
        if (icon != null && SystemTray.isSupported())
        {
            SystemTray.getSystemTray().remove(icon);
            icon = null;
        }
    }
}

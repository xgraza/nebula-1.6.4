package us.nebula.client.api.systemtray;

import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import us.nebula.client.BuildConfig;
import us.nebula.client.ClientSettings;
import us.nebula.client.impl.cheat.player.DiscordRPCCheat;
import us.nebula.client.util.io.FileUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author xgraza
 * @since 02/15/25
 */
public final class NebulaSystemTray implements ActionListener
{
    private static final Minecraft MC = Minecraft.getMinecraft();
    private static final Logger LOGGER = LogManager.getLogger("TrayIcon");
    private static final String ICON_LOCATION = "/assets/nebula/texture/icon/16x.png";

    private TrayIcon icon;

    public void init()
    {
        if (!SystemTray.isSupported())
        {
            LOGGER.error("SystemTray is not supported by your WM/DE");
            return;
        }

        try
        {
            final TrayIcon trayIcon = createIcon();
            SystemTray.getSystemTray().add(trayIcon);
            LOGGER.info("Tray icon was successfully added!");
            icon = trayIcon;
        } catch (final AWTException e)
        {
            LOGGER.error(e);
        }
    }

    @Override
    public void actionPerformed(final ActionEvent e)
    {
        switch (e.getActionCommand())
        {
            case "discordrpc":
            {
                LOGGER.info("Toggling DiscordRPCCHeat");
                DiscordRPCCheat.INSTANCE.toggle();
                break;
            }
            case "copy":
            {
                if (MC.thePlayer == null || MC.theWorld == null)
                {
                    return;
                }
                final String format = String.format("X: %.1f, Y: %.1f, Z: %.1f",
                        MC.thePlayer.posX, MC.thePlayer.boundingBox.minY, MC.thePlayer.posZ);
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                        new StringSelection(format), null);
                LOGGER.info("Copied to clipboard {}", format);
                break;
            }
            case "git":
            {
                try
                {
                    LOGGER.info("Browsing to GitHub repo");
                    Desktop.getDesktop().browse(new URI(ClientSettings.GITHUB_REPO));
                }
                catch (final IOException | URISyntaxException ex)
                {
                    LOGGER.error("Failed to open GitHub repo", ex);
                }
                break;
            }
        }
    }

    private TrayIcon createIcon()
    {
        final BufferedImage image = readIconImage();
        if (image == null)
        {
            return null;
        }
        final TrayIcon trayIcon = new TrayIcon(image, BuildConfig.HASH);
        trayIcon.setPopupMenu(createPopupMenu());
        trayIcon.addActionListener(this);
        return trayIcon;
    }

    private BufferedImage readIconImage()
    {
        LOGGER.info("Setting up tray icon");
        try
        {
            final InputStream stream = FileUtil.getResourceStream(ICON_LOCATION);
            if (stream == null)
            {
                LOGGER.error("Resource stream could not be opened");
                return null;
            }
            final BufferedImage image = ImageIO.read(stream);
            stream.close();
            return image;
        } catch (final IOException e)
        {
            LOGGER.error("Icon could not be parsed to a BufferedImage");
            return null;
        }
    }

    private PopupMenu createPopupMenu()
    {
        final PopupMenu menu = new PopupMenu("Nebula");
        MenuItem menuItem = new MenuItem("Toggle DiscordRPC");
        menuItem.addActionListener(this);
        menuItem.setActionCommand("discordrpc");
        menu.add(menuItem);
        menuItem = new MenuItem("Copy Coordinates");
        menuItem.addActionListener(this);
        menuItem.setActionCommand("copy");
        menu.add(menuItem);
        menuItem = new MenuItem("Open GitHub");
        menuItem.addActionListener(this);
        menuItem.setActionCommand("git");
        menu.add(menuItem);
        return menu;
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

    public boolean isActive()
    {
        return icon != null;
    }
}

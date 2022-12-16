package wtf.nebula.client.utils.io;

import wtf.nebula.client.core.Nebula;

import java.awt.*;
import java.awt.image.BufferedImage;

public class SystemTrayUtils {

    private static final String LOGO_LOCATION = "/assets/minecraft/nebula/textures/logo/logo_16px.png";

    private static TrayIcon trayIcon = null;

    public static void createIcon() {
        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();

            BufferedImage image = FileUtils.getResourceAsImage(LOGO_LOCATION);
            if (image == null) {
                Nebula.LOGGER.error("Could not find image at location {}", LOGO_LOCATION);
                return;
            }

            trayIcon = new TrayIcon(image, "Nebula");
            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                Nebula.LOGGER.error("System does not support tray icons.");
                e.printStackTrace();
            }
        }
    }

    public static void remove() {
        if (trayIcon != null) {
            SystemTray.getSystemTray().remove(trayIcon);
            trayIcon = null;
        }
    }

    public static void showMessage(String message) {
        if (SystemTray.isSupported() && trayIcon != null) {
            trayIcon.displayMessage("", message, TrayIcon.MessageType.NONE);
        }
    }

    public static boolean isCreated() {
        return trayIcon != null;
    }

    public static TrayIcon getTrayIcon() {
        return trayIcon;
    }
}

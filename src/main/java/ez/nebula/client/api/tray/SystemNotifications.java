package ez.nebula.client.api.tray;

import ez.nebula.client.api.tray.impl.AWTTray;
import ez.nebula.client.api.tray.impl.MacOSTray;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author xgraza
 * @since 02/15/25
 */
public final class SystemNotifications
{
    private static final Logger LOGGER = LogManager.getLogger(SystemNotifications.class);
    private static ITray TRAY;

    public static void init()
    {
        switch (Util.getOSType())
        {
            case MACOS:
            {
                TRAY = new MacOSTray();
                break;
            }
            default:
            {
                try
                {
                    TRAY = new AWTTray();
                } catch (Exception e)
                {
                    TRAY = null;
                    LOGGER.error("Failed to use setback AWT tray");
                }
                break;
            }
        }
        if (TRAY != null)
        {
            LOGGER.info("Using system notification tray {}", TRAY);
        }
    }

    public static boolean hasTray()
    {
        return TRAY != null;
    }

    public static void info(String title, String content)
    {
        if (TRAY != null)
        {
            TRAY.display(title, content);
        }
    }
}

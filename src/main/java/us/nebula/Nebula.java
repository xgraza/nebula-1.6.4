package us.nebula;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;
import us.nebula.api.gui.font.Fonts;
import us.nebula.api.manager.command.CommandManager;
import us.nebula.api.config.ConfigurationManager;
import us.nebula.api.manager.cheat.CheatManager;
import us.nebula.api.manager.key.KeyManager;
import us.nebula.api.manager.overlay.OverlayManager;
import us.nebula.api.manager.rotate.RotationManager;
import us.nebula.api.manager.toast.ToastManager;
import us.nebula.api.systemtray.NebulaSystemTray;
import us.nebula.util.render.RenderUtil;

import java.io.File;
import java.io.IOException;

/**
 * @author xgraza
 * @since 02/12/25
 */
public enum Nebula
{
    INSTANCE;

    private final Logger logger = LogManager.getLogger("Nebula");
    private File nebulaRootDir;

    private NebulaSystemTray systemTray;
    private ConfigurationManager configurationManager;
    private KeyManager keyManager;
    private CommandManager commandManager;
    private OverlayManager overlayManager;
    private CheatManager cheatManager;
    private ToastManager toastManager;
    private RotationManager rotationManager;

    public void init(final File gameDir) throws IOException
    {
        logBuildInfo();
        setTitle("Setting up Nebula...");

        nebulaRootDir = new File(gameDir, "nebula-client");
        if (!nebulaRootDir.exists())
        {
            if (nebulaRootDir.mkdir())
            {
                logger.info("Created {} successfully", nebulaRootDir.getAbsolutePath());
            } else
            {
                throw new RuntimeException("Failed to create nebula directory");
            }
        }

        systemTray = new NebulaSystemTray();
        configurationManager = new ConfigurationManager();
        keyManager = new KeyManager();
        commandManager = new CommandManager();
        overlayManager = new OverlayManager();
        cheatManager = new CheatManager();
        toastManager = new ToastManager();
        rotationManager = new RotationManager();

        keyManager.init();
        commandManager.init();
        overlayManager.init();
        cheatManager.init();
        configurationManager.init();
        systemTray.init();
        toastManager.init();
        rotationManager.init();

        RenderUtil.initShaders();
        Fonts.initFonts();

        setTitle("Nebula Client | Minecraft 1.7.2");
        logger.info("Instantiated Nebula successfully!");
    }

    void setTitle(final String title)
    {
        final Util.EnumOS os = Util.getOSType();
        if (os == Util.EnumOS.WINDOWS || os == Util.EnumOS.MACOS)
        {
            Display.setTitle(title);
        } else
        {
            Minecraft.func_147105_a(title);
        }
    }

    void logBuildInfo()
    {
        logger.info("Version: {}", ClientSettings.VERSION);
        logger.info("Build Time: " + BuildConfig.BUILD_TIME);
    }

    public Logger getLogger()
    {
        return logger;
    }

    public File getNebulaRootDir()
    {
        return nebulaRootDir;
    }

    public ConfigurationManager getConfigurationManager()
    {
        return configurationManager;
    }

    public KeyManager getKeyManager()
    {
        return keyManager;
    }

    public CommandManager getCommandManager()
    {
        return commandManager;
    }

    public OverlayManager getOverlayManager()
    {
        return overlayManager;
    }

    public CheatManager getCheatManager()
    {
        return cheatManager;
    }

    public ToastManager getToastManager()
    {
        return toastManager;
    }

    public RotationManager getRotationManager()
    {
        return rotationManager;
    }

    public NebulaSystemTray getSystemTray()
    {
        return systemTray;
    }
}

package us.nebula;

import com.github.lunatrius.schematica.Schematica;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;
import us.nebula.api.gui.font.Fonts;
import us.nebula.api.manager.command.CommandManager;
import us.nebula.api.config.ConfigurationManager;
import us.nebula.api.manager.cheat.CheatManager;
import us.nebula.api.manager.friend.FriendManager;
import us.nebula.api.manager.inventory.InventoryManager;
import us.nebula.api.manager.key.KeyManager;
import us.nebula.api.manager.overlay.OverlayManager;
import us.nebula.api.manager.rotate.RotationManager;
import us.nebula.api.manager.toast.ToastManager;
import us.nebula.api.systemtray.NebulaSystemTray;
import us.nebula.util.render.RenderUtil;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author xgraza
 * @since 02/12/25
 */
public enum Nebula
{
    INSTANCE;

    private final Logger logger = LogManager.getLogger("Nebula");
    private File nebulaRootDir;

    private final Executor executor = Executors.newFixedThreadPool(1);

    private NebulaSystemTray systemTray;
    private ConfigurationManager configurationManager;
    private KeyManager keyManager;
    private CommandManager commandManager;
    private OverlayManager overlayManager;
    private CheatManager cheatManager;
    private FriendManager friendManager;
    private ToastManager toastManager;
    private InventoryManager inventoryManager;
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
        friendManager = new FriendManager();
        toastManager = new ToastManager();
        inventoryManager = new InventoryManager();
        rotationManager = new RotationManager();

        keyManager.init();
        commandManager.init();
        overlayManager.init();
        cheatManager.init();
        configurationManager.init();
        systemTray.init();
        toastManager.init();
        inventoryManager.init();
        rotationManager.init();
        friendManager.init();

        RenderUtil.initShaders();
        Fonts.initFonts();

        // Init schematica
        Schematica.load();

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

    public Executor getExecutor()
    {
        return executor;
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

    public FriendManager getFriendManager()
    {
        return friendManager;
    }

    public ToastManager getToastManager()
    {
        return toastManager;
    }

    public InventoryManager getInventoryManager()
    {
        return inventoryManager;
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

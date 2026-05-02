package us.nebula.client;

import com.github.lunatrius.schematica.Schematica;
import net.minecraft.client.Minecraft;
import net.minecraft.client.SplashTextProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;
import us.nebula.client.api.config.ConfigurationManager;
import us.nebula.client.api.manager.account.AccountManager;
import us.nebula.client.api.manager.cheat.CheatManager;
import us.nebula.client.api.manager.command.CommandManager;
import us.nebula.client.api.manager.friend.FriendManager;
import us.nebula.client.api.manager.hud.HUDManager;
import us.nebula.client.api.manager.inventory.InventoryManager;
import us.nebula.client.api.manager.key.KeyManager;
import us.nebula.client.api.manager.rotate.RotationManager;
import us.nebula.client.api.manager.toast.ToastManager;
import us.nebula.client.api.movement.MovementController;
import us.nebula.client.api.plugin.PluginManager;
import us.nebula.client.api.systemtray.NebulaSystemTray;
import us.nebula.client.impl.gui.loading.LoadingScreen;
import us.nebula.client.util.render.RenderUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
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
    private HUDManager hudManager;
    private CheatManager cheatManager;
    private AccountManager accountManager;
    private FriendManager friendManager;
    private ToastManager toastManager;
    private InventoryManager inventoryManager;
    private RotationManager rotationManager;
    private PluginManager pluginManager;

    private MovementController movementController;

    public void init(final File gameDir)
    {
        SplashTextProvider.addSplashTextProvider(
                new ResourceLocation("nebula", "splashs.txt"));
        LoadingScreen.setTotalLoadingStages(12);
        LoadingScreen.setStage(1, "Setting up Nebula");

        logBuildInfo();
        setTitle("Setting up Nebula...");

        nebulaRootDir = new File(gameDir, "nebula-client");
        if (!nebulaRootDir.exists())
        {
            LoadingScreen.setStage(2, "Creating Nebula directories");
            if (nebulaRootDir.mkdir())
            {
                logger.info("Created {} successfully", nebulaRootDir.getAbsolutePath());
            } else
            {
                throw new RuntimeException("Failed to create nebula directory");
            }
        }

        LoadingScreen.setStage(3, "Creating Nebula core");

        systemTray = new NebulaSystemTray();
        configurationManager = new ConfigurationManager();
        keyManager = new KeyManager();
        commandManager = new CommandManager();
        hudManager = new HUDManager();
        cheatManager = new CheatManager();
        accountManager = new AccountManager();
        friendManager = new FriendManager();
        toastManager = new ToastManager();
        inventoryManager = new InventoryManager();
        rotationManager = new RotationManager();
        //pluginManager = new PluginManager();
        movementController = new MovementController();

        LoadingScreen.setStage(4, "Initializing Nebula core");

        keyManager.init();
        commandManager.init();
        hudManager.init();
        cheatManager.init();
        accountManager.init();
        configurationManager.init();
        systemTray.init();
        toastManager.init();
        inventoryManager.init();
        rotationManager.init();
        friendManager.init();
        //pluginManager.init();

        LoadingScreen.setStage(5, "Initializing Nebula shaders");
        try
        {
            RenderUtil.initShaders();
        } catch (Exception e)
        {
            logger.error(e);
        }

        LoadingScreen.setStage(6, "Initializing Schematica");
        // Init schematica
        Schematica.load();

        setIcon();
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
            Minecraft.setTitle(title);
        }
    }

    void setIcon()
    {
        try
        {
            ByteBuffer buffer16x = readImage("/assets/nebula/texture/icon/16x.png");
            ByteBuffer buffer32x = readImage("/assets/nebula/texture/icon/32x.png");
            ByteBuffer buffer128x = readImage("/assets/nebula/texture/icon/128x.png");
            if (buffer16x == null || buffer32x == null || buffer128x == null)
            {
                logger.error("Failed to read Nebula icon buffer(s).");
                return;
            }

            Display.setIcon(new ByteBuffer[]{ buffer16x, buffer32x, buffer128x });
        } catch (Exception exception)
        {
            logger.error("Couldn't set icon", exception);
        }
    }

    ByteBuffer readImage(String location)
    {
        try (final InputStream is = Nebula.class.getResourceAsStream(location))
        {
            if (is == null)
            {
                return null;
            }
            BufferedImage bufferedimage = ImageIO.read(is);
            int[] aint = bufferedimage.getRGB(0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), null, 0, bufferedimage.getWidth());
            ByteBuffer bytebuffer = ByteBuffer.allocate(4 * aint.length);

            for (int i : aint)
            {
                bytebuffer.putInt(i << 8 | i >> 24 & 255);
            }

            bytebuffer.flip();
            return bytebuffer;
        } catch (IOException e)
        {
            return null;
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

    public HUDManager getHUDManager() {
        return hudManager;
    }

    public CheatManager getCheatManager()
    {
        return cheatManager;
    }

    public AccountManager getAccountManager()
    {
        return accountManager;
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

    public MovementController getMovementController()
    {
        return movementController;
    }
}

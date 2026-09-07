package ez.nebula.client;

import com.github.lunatrius.schematica.Schematica;
import ez.nebula.client.api.manager.hud2.HUDElementManager;
import ez.nebula.client.api.manager.waypoint.WaypointManager;
import ez.nebula.client.api.nws.NWS;
import ez.nebula.client.api.player.InteractionManager;
import ez.nebula.client.api.tray.SystemNotifications;
import net.minecraft.client.Minecraft;
import net.minecraft.client.SplashTextProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;
import ez.nebula.client.api.config.ConfigManager;
import ez.nebula.client.api.manager.account.AccountManager;
import ez.nebula.client.api.manager.module.ModuleManager;
import ez.nebula.client.api.manager.command.CommandManager;
import ez.nebula.client.api.manager.friend.FriendManager;
import ez.nebula.client.api.manager.hud.HUDManager;
import ez.nebula.client.api.player.server.InventoryManager;
import ez.nebula.client.api.manager.key.KeyManager;
import ez.nebula.client.api.player.server.RotationManager;
import ez.nebula.client.api.player.server.ServerManager;
import ez.nebula.client.api.manager.toast.ToastManager;
import ez.nebula.client.api.player.movement.MovementController;
import ez.nebula.client.impl.gui.startup.LoadingScreen;
import ez.nebula.client.util.render.RenderUtil;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
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

    private final Logger logger = LogManager.getLogger(BuildConfig.NAME);
    private File nebulaRootDir;

    private final Executor executor = Executors.newFixedThreadPool(1);

    private ConfigManager configManager;
    private KeyManager keyManager;
    private CommandManager commandManager;
    private HUDManager hudManager;
    private HUDElementManager hudManager2;
    private ModuleManager moduleManager;
    private AccountManager accountManager;
    private FriendManager friendManager;
    private ToastManager toastManager;
    private InventoryManager inventoryManager;
    private RotationManager rotationManager;
    private ServerManager serverManager;
    private WaypointManager waypointManager;

    private MovementController movementController;

    public void init(final File gameDir)
    {
        logBuildInfo();

        LoadingScreen.setTotalLoadingStages(12);
        LoadingScreen.setStage(1, "Setting up Nebula");
        setTitle("Setting up Nebula...");

        SplashTextProvider.addSplashTextProvider(
                new ResourceLocation("nebula", "splashs.txt"));

        LoadingScreen.setStage(2, "Initializing Nebula directories");
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

        LoadingScreen.setStage(3, "Initializing Nebula core");
        final long startTime = System.nanoTime();

        NWS.start(false);

        configManager = new ConfigManager();

        // core features
        keyManager = new KeyManager();
        keyManager.init();
        hudManager = new HUDManager();
        hudManager.init();
        hudManager2 = new HUDElementManager();
        hudManager2.init();
        commandManager = new CommandManager();
        commandManager.init();
        moduleManager = new ModuleManager();
        moduleManager.init();

        // server features
        serverManager = new ServerManager();
        serverManager.init();
        rotationManager = new RotationManager();
        rotationManager.init();
        inventoryManager = new InventoryManager();
        inventoryManager.init();
        movementController = new MovementController();
        InteractionManager.INSTANCE.init();

        // bullshit with fur
        accountManager = new AccountManager();
        accountManager.init();
        toastManager = new ToastManager();
        toastManager.init();
        friendManager = new FriendManager();
        friendManager.init();
        waypointManager = new WaypointManager();
        waypointManager.init();
        SystemNotifications.init();

        // init schematica
        Schematica.load();

        final long endTime = System.nanoTime();
        logger.info("Instantiated Nebula successfully in {}ms",
                String.format("%.2f", (endTime - startTime) / 1000000.0));

        LoadingScreen.setStage(4, "Loading configs");
        configManager.init();

        LoadingScreen.setStage(5, "Initializing Nebula render features");
        try
        {
            RenderUtil.initShaders();
        } catch (Exception e)
        {
            logger.error(e);
        }
        LoadingScreen.setStage(6, "Finishing Nebula initialization");
        setIcon();
        setTitle("Nebula " + ClientConfig.FULL_VERSION);
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
        if (Util.getOSType() == Util.EnumOS.MACOS)
        {
            try
            {
                final InputStream is = Nebula.class.getResourceAsStream("/assets/nebula/texture/icon/128x.png");
                final BufferedImage image = ImageIO.read(is);
                final Class<?> clazz = Class.forName("com.apple.eawt.Application");
                final Method getApplicationMethod = clazz.getDeclaredMethod("getApplication");
                final Object application = getApplicationMethod.invoke(null);
                final Method setDockIconMethod = application.getClass().getDeclaredMethod("setDockIconImage", Image.class);
                setDockIconMethod.invoke(application, image);
                return;
            } catch (final Exception e)
            {
                logger.error("Failed to set dock icon with EAWT, fallback to LWJGL2");
            }
        }

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
        } catch (final Exception exception)
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
        logger.info("Version: {}", ClientConfig.FULL_VERSION);
        logger.info("Build Time: " + BuildConfig.BUILD_TIME);
        if (ClientConfig.DEBUG)
        {
            logger.warn("\t###");
            logger.warn("\tNebula debug is enabled!");
            logger.warn("\t##");
        }
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

    public ConfigManager getConfigurationManager()
    {
        return configManager;
    }

    public KeyManager getKeyManager()
    {
        return keyManager;
    }

    public CommandManager getCommandManager()
    {
        return commandManager;
    }

    public HUDManager getHUDManager()
    {
        return hudManager;
    }

    public HUDElementManager getHudManager2()
    {
        return hudManager2;
    }

    public ModuleManager getModuleManager()
    {
        return moduleManager;
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

    public ServerManager getServerManager()
    {
        return serverManager;
    }

    public MovementController getMovementController()
    {
        return movementController;
    }

    public WaypointManager getWaypointManager()
    {
        return waypointManager;
    }
}

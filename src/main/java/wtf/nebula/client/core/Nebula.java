package wtf.nebula.client.core;

import me.bush.eventbus.bus.EventBus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;
import wtf.nebula.client.api.config.ConfigManager;
import wtf.nebula.client.core.versioning.Version;
import wtf.nebula.client.core.versioning.VersionCheck;
import wtf.nebula.client.impl.account.AccountManager;
import wtf.nebula.client.impl.command.CommandManager;
import wtf.nebula.client.impl.discord.DiscordRPCHandler;
import wtf.nebula.client.impl.manager.*;
import wtf.nebula.client.impl.module.ModuleManager;
import wtf.nebula.client.impl.waypoint.WaypointManager;
import wtf.nebula.client.utils.io.FileUtils;
import wtf.nebula.client.utils.io.SystemTrayUtils;

public class Nebula {
    private static Nebula INSTANCE;

    public static final String NAME = "Nebula";
    public static final Version VERSION = new Version(
            2,
            0,
            0,
            ClientEnvironment.RELEASE);

    public static final Logger LOGGER = LogManager.getLogger(NAME);
    public static final EventBus BUS = new EventBus();

    public static long START_TIME;

    private ModuleManager moduleManager;
    private CommandManager commandManager;
    private FriendManager friendManager;
    private RotationManager rotationManager;
    private InventoryManager inventoryManager;
    private TickManager tickManager;
    private AccountManager accountManager;
    private WaypointManager waypointManager;
    private CapeManager capeManager;

    private Nebula() {
        if (INSTANCE != null) {
            LOGGER.warn("Tried to instantiate Launcher twice");
            return;
        }

        START_TIME = System.nanoTime();

        LOGGER.info("Loading {} v{}", NAME, VERSION.getVersionString());
        if (VERSION.getEnv().equals(ClientEnvironment.DEVELOPMENT)) {
            LOGGER.info("Running in development mode, client will produce more detailed stack traces.");
        }

        if (!FileUtils.CLIENT_DIRECTORY.exists()) {
            boolean result = FileUtils.CLIENT_DIRECTORY.mkdir();
            LOGGER.info("{} created the client configuration directory", (result ? "Successfully" : "Unsuccessfully"));
        }

        LOGGER.info("Checking version...");
        // VersionCheck.check(); // TODO: show window if outdated

        SystemTrayUtils.createIcon();
        if (!SystemTrayUtils.isCreated()) {
            LOGGER.info("Couldn't create system tray icon");
        }

        // our managers
        moduleManager = new ModuleManager();
        commandManager = new CommandManager();
        friendManager = new FriendManager();
        rotationManager = new RotationManager();
        inventoryManager = new InventoryManager();
        tickManager = new TickManager();
        accountManager = new AccountManager();
        waypointManager = new WaypointManager();
        capeManager = new CapeManager();

        // load configurations
        ConfigManager.loadConfigurations();

        // set a shutdown hook for when the JVM is about to shut down
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            ConfigManager.saveConfigurations();
            DiscordRPCHandler.stop();
        }, "Nebula-Shutdown-Thread"));

        LOGGER.info("Launched {} v{} successfully in {} ms!", NAME, VERSION, (System.nanoTime() - START_TIME) / 1000000L);

        String title = NAME + " " + VERSION.getVersionString();
        if (VersionCheck.isOutdated) {
            title += " (Outdated)";
        }

        Display.setTitle(title);
    }

    public static void launch() {
        if (INSTANCE != null) {
            LOGGER.warn("Launcher#launch() called twice");
            return;
        }

        INSTANCE = new Nebula();
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public FriendManager getFriendManager() {
        return friendManager;
    }

    public RotationManager getRotationManager() {
        return rotationManager;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public TickManager getTickManager() {
        return tickManager;
    }

    public AccountManager getAccountManager() {
        return accountManager;
    }

    public WaypointManager getWaypointManager() {
        return waypointManager;
    }

    public CapeManager getCapeManager() {
        return capeManager;
    }

    public static Nebula getInstance() {
        return INSTANCE;
    }
}

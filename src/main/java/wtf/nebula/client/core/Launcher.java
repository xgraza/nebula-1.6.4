package wtf.nebula.client.core;

import me.bush.eventbus.bus.EventBus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wtf.nebula.client.api.config.ConfigManager;
import wtf.nebula.client.core.versioning.Version;
import wtf.nebula.client.core.versioning.VersionCheck;
import wtf.nebula.client.impl.manager.InventoryManager;
import wtf.nebula.client.impl.manager.RotationManager;
import wtf.nebula.client.impl.module.ModuleManager;

public class Launcher {
    private static Launcher INSTANCE;

    public static final String NAME = "Nebula";
    public static final Version VERSION = new Version(
            1,
            5,
            0,
            ClientEnvironment.DEVELOPMENT);

    public static final Logger LOGGER = LogManager.getLogger(NAME);
    public static final EventBus BUS = new EventBus();

    public static long START_TIME;

    private ModuleManager moduleManager;
    private RotationManager rotationManager;
    private InventoryManager inventoryManager;

    private Launcher() {
        if (INSTANCE != null) {
            LOGGER.warn("Tried to instantiate Launcher twice");
            return;
        }

        START_TIME = System.nanoTime();

        LOGGER.info("Loading {} v{}", NAME, VERSION.getVersionString());
        if (VERSION.getEnv().equals(ClientEnvironment.DEVELOPMENT)) {
            LOGGER.info("Running in development mode, client will produce more detailed stack traces.");
        }

        LOGGER.info("Checking version...");
        VersionCheck.check(); // TODO: show window if outdated

        moduleManager = new ModuleManager();
        rotationManager = new RotationManager();
        inventoryManager = new InventoryManager();

        // load configurations
        ConfigManager.loadConfigurations();

        LOGGER.info("Launched {} v{} successfully in {} ms!", NAME, VERSION, (System.nanoTime() - START_TIME) / 1000000L);
    }

    public static void launch() {
        if (INSTANCE != null) {
            LOGGER.warn("Launcher#launch() called twice");
            return;
        }

        INSTANCE = new Launcher();
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public RotationManager getRotationManager() {
        return rotationManager;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public static Launcher getInstance() {
        return INSTANCE;
    }
}

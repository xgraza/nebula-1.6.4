package lol.nebula;

import lol.nebula.bind.BindManager;
import lol.nebula.config.Config;
import lol.nebula.config.ConfigManager;
import lol.nebula.listener.bus.EventBus;
import lol.nebula.module.ModuleManager;
import lol.nebula.util.math.timing.Timer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

import static java.lang.String.format;

/**
 * @author aesthetical
 * @since 04/27/23
 */
public class Nebula {

    /**
     * The Nebula client singleton
     */
    private static Nebula singleton;

    private static final String name = "Nebula";
    private static final String version = "3.0";

    /**
     * The global client logger
     */
    private static final Logger logger = LogManager.getLogger(name);

    /**
     * The event bus used to dispatch and receive events
     */
    private static final EventBus bus = new EventBus();

    private final ConfigManager configs;
    private final BindManager binds;
    private final ModuleManager modules;

    private Nebula() {
        singleton = this;

        logger.info("Loading {} v{}", name, version);
        Display.setTitle(format("Loading %s v%s", name, version));

        // time measuring for starting the base
        Timer timer = new Timer();

        // init managers
        configs = new ConfigManager();
        binds = new BindManager();
        modules = new ModuleManager();

        // subscribe listeners
        Nebula.getBus().subscribe(binds);

        logger.info("Instantiated {} {} in {}ms", name, version, timer.getTimeElapsedMS());

        // load all configs
        logger.info("Loading configs");
        configs.getConfigs().forEach(Config::load);

        Display.setTitle(format("%s v%s", name, version));
    }

    /**
     * Gets the config manager instance
     * @return the config manager instance
     */
    public ConfigManager getConfigs() {
        return configs;
    }

    /**
     * Gets the bind manager instance
     * @return the bind manager instance
     */
    public BindManager getBinds() {
        return binds;
    }

    /**
     * Gets the module manager instance
     * @return the module manager instance
     */
    public ModuleManager getModules() {
        return modules;
    }

    /**
     * Gets the nebula client singleton
     * @return the instance of nebula
     */
    public static Nebula getInstance() {
        if (singleton == null) {
            return new Nebula();
        }

        return singleton;
    }

    /**
     * Gets the name of  the client
     * @return the client name
     */
    public static String getName() {
        return name;
    }

    /**
     * Gets the formatted version of the client
     * @return the client version
     */
    public static String getVersion() {
        return version;
    }

    /**
     * Get the logger nebula uses for debug output
     * @return the logger instance
     */
    public static Logger getLogger() {
        return logger;
    }

    /**
     * Gets the event bus used to dispatch and receive events
     * @return the event bus instance
     */
    public static EventBus getBus() {
        return bus;
    }
}

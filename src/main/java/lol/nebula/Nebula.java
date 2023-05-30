package lol.nebula;

import com.github.lunatrius.schematica.Schematica;
import lol.nebula.account.AccountManager;
import lol.nebula.bind.BindManager;
import lol.nebula.command.CommandManager;
import lol.nebula.config.Config;
import lol.nebula.config.ConfigManager;
import lol.nebula.listener.bus.EventBus;
import lol.nebula.management.InventoryManager;
import lol.nebula.management.RotationManager;
import lol.nebula.friend.FriendManager;
import lol.nebula.management.TickManager;
import lol.nebula.module.ModuleManager;
import lol.nebula.util.math.timing.Timer;
import lol.nebula.util.render.font.Fonts;
import net.minecraft.client.Minecraft;
import net.minecraft.crash.CrashReport;
import net.minecraft.util.Util;
import net.minecraft.util.Util.EnumOS;
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

    /**
     * If the client is in a development environment
     */
    public static boolean developmentSwitch;

    private static final String name = "Nebula";
    private static final String version = "3.0.0";

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
    private final CommandManager commands;
    private final FriendManager friends;
    private final AccountManager accounts;
    private final InventoryManager inventory;
    private final RotationManager rotations;
    private final TickManager tick;

    private Nebula() {
        singleton = this;

        if (developmentSwitch) logger.info("Development switch is on");

        System.out.println("Running Nebula " + version + " on git " + BuildConfig.HASH + "/" + BuildConfig.BRANCH);
        System.out.println(new CrashReport("THIS IS NOT A CRASH REPORT", new Throwable("gabagoo")).getCompleteReport());

        logger.info("Loading {} v{}", name, version);

        String title = format("Loading %s v%s", name, version);
        EnumOS os = Util.getOSType();
        if (os != EnumOS.WINDOWS && os != EnumOS.MACOS) {
            Minecraft.func_147105_a(title);
        } else {
            Display.setTitle(title);
        }

        // time measuring for starting the base
        Timer timer = new Timer();

        // init managers
        configs = new ConfigManager();
        binds = new BindManager();
        modules = new ModuleManager();
        commands = new CommandManager();
        friends = new FriendManager();
        accounts = new AccountManager();
        inventory = new InventoryManager();
        rotations = new RotationManager();
        tick = new TickManager();

        // subscribe listeners
        bus.subscribe(binds);
        bus.subscribe(commands);
        bus.subscribe(inventory);
        bus.subscribe(rotations);
        bus.subscribe(tick);

        // load fonts
        Fonts.loadFonts();

        logger.info("Instantiated {} {}-{}-{} in {}ms", name, version, BuildConfig.BRANCH, BuildConfig.HASH, timer.getTimeElapsedMS());

        // load all configs
        logger.info("Loading configs");
        configs.getConfigs().forEach(Config::load);

        logger.info("Loading Schematica");
        Schematica.init();

        Display.setTitle(getFormatted());
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
     * Gets the command manager instance
     * @return the command manager instance
     */
    public CommandManager getCommands() {
        return commands;
    }

    /**
     * Gets the friend manager instance
     * @return the friend manager instance
     */
    public FriendManager getFriends() {
        return friends;
    }

    /**
     * Gets the account manager instance
     * @return the account manager instance
     */
    public AccountManager getAccounts() {
        return accounts;
    }

    /**
     * Gets the inventory manager instance
     * @return the inventory manager instance
     */
    public InventoryManager getInventory() {
        return inventory;
    }

    /**
     * Gets the rotation manager instance
     * @return the rotation manager instance
     */
    public RotationManager getRotations() {
        return rotations;
    }

    /**
     * Gets the tick manager instance
     * @return the tick manager instance
     */
    public TickManager getTick() {
        return tick;
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
     * Gets the formatted version
     * @return the formatted version
     */
    public static String getFormatted() {
        return format("%s %s+%s-%s", getName(), getVersion(), BuildConfig.BUILD_NUMBER, BuildConfig.HASH);
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

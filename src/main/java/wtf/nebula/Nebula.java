package wtf.nebula;

import me.bush.eventbus.bus.EventBus;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wtf.nebula.impl.module.render.XRay;
import wtf.nebula.repository.Repositories;
import wtf.nebula.repository.impl.CommandRepository;
import wtf.nebula.repository.impl.FriendRepository;
import wtf.nebula.repository.impl.ModuleRepository;
import wtf.nebula.repository.impl.WaypointRepository;

/**
 * The main class of the client
 *
 * @author aesthetical
 * @since 06/27/22
 */
public class Nebula {
    public static final String NAME = "Nebula";
    public static final String VERSION = "1.4.0";
    public static final String TAG = "dev"; // rel, beta, dev, rc(INT)

    public static Logger log = LogManager.getLogger(NAME);
    public static EventBus BUS;

    public static void init() {
        log.info("Loading BushBus:tm:");

        // our event bus
        BUS = new EventBus(log::error);

        log.info("Loading {} v{}...", NAME, VERSION);

        // repos
        Repositories.add(new ModuleRepository());
        Repositories.add(new CommandRepository());
        Repositories.add(new FriendRepository());
        Repositories.add(new WaypointRepository());

        log.info("Entering aestheti-botnet...");
        log.info("Loaded client.");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Saving repository states...");

            ModuleRepository.get().save();
            FriendRepository.get().save();
            WaypointRepository.get().save();
            CommandRepository.save(CommandRepository.get().getPrefix());
            XRay.save();

            log.info("Aesthetical owns you!");
        }, "Shutdown-Save-Thread"));

        log.info("Aesthetical owns you!");
    }
}

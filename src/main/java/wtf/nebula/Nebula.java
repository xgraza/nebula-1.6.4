package wtf.nebula;

import me.bush.eventbus.bus.EventBus;
import net.minecraft.src.LogAgent;
import net.minecraft.src.Minecraft;
import net.minecraft.src.Session;
import wtf.nebula.impl.module.render.XRay;
import wtf.nebula.repository.Repositories;
import wtf.nebula.repository.impl.CommandRepository;
import wtf.nebula.repository.impl.FriendRepository;
import wtf.nebula.repository.impl.ModuleRepository;
import wtf.nebula.repository.impl.WaypointRepository;
import wtf.nebula.util.FileUtil;
import wtf.nebula.util.Globals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

/**
 * The main class of the client
 *
 * @author aesthetical
 * @since 06/27/22
 */
public class Nebula {
    public static final String NAME = "Nebula";
    public static final String VERSION = "1.3.0";
    public static final String TAG = "rel"; // rel, beta, dev, rc(INT)

    public static LogAgent log;
    public static EventBus BUS;

    public static void init() {
        setupLogger();
        log.logInfo("Loading BushBus:tm:");

        // our event bus
        BUS = new EventBus(log::logInfo);

        log.logInfo("Loading " + NAME + " v" + VERSION + "...");

        // repos
        Repositories.add(new ModuleRepository());
        Repositories.add(new CommandRepository());
        Repositories.add(new FriendRepository());
        Repositories.add(new WaypointRepository());

        log.logInfo("Loaded client, henlo!!!!?!?!");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.logInfo("Saving repository states...");

            ModuleRepository.get().save();
            FriendRepository.get().save();
            WaypointRepository.get().save();
            CommandRepository.save(CommandRepository.get().getPrefix());
            XRay.save();
        }, "Shutdown-Save-Thread"));

        log.logInfo("Aesthetical owns you!");
    }

    private static void setupLogger() {

        Path path = FileUtil.ROOT.resolve("nebula_logs");

        // create logger folder
        if (!Files.exists(path)) {
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        log = new LogAgent("Nebula", " [Nebula]", path.toFile().getAbsolutePath());
    }
}

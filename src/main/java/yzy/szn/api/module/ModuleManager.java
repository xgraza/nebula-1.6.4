package yzy.szn.api.module;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yzy.szn.api.eventbus.Subscribe;
import yzy.szn.impl.event.EventKey;
import yzy.szn.impl.module.movement.ModuleSprint;
import yzy.szn.impl.module.render.ModuleClickGUI;
import yzy.szn.impl.module.render.ModuleHUD;
import yzy.szn.launcher.YZY;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import static org.lwjgl.input.Keyboard.KEY_NONE;

/**
 * @author graza
 * @since 02/17/24
 */
public final class ModuleManager {

    public static final Logger LOGGER = LogManager.getLogger("yzy.szn.modules");

    private final Map<Class<? extends Module>, Module> moduleInstanceMap = new LinkedHashMap<>();
    private final Set<Module> modules = new LinkedHashSet<>();

    public ModuleManager() {
        YZY.BUS.subscribe(this);

        register(new ModuleSprint());
        register(new ModuleClickGUI());
        register(new ModuleHUD());

        LOGGER.info("Collected {} modules", modules.size());
    }

    private void register(final Module module) {
        moduleInstanceMap.put(module.getClass(), module);
        modules.add(module);
    }

    @Subscribe
    private final Consumer<EventKey> keyListener = event -> {
        final int keyCode = event.getKeyCode();
        if (keyCode > KEY_NONE) {
            for (final Module module : moduleInstanceMap.values()) {
                if (module.getKeyCode() != KEY_NONE && module.getKeyCode() == keyCode) {
                    module.toggle();
                }
            }
        }
    };

    public Set<Module> getModules() {
        return modules;
    }
}

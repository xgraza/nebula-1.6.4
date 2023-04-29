package lol.nebula.module;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lol.nebula.Nebula;
import lol.nebula.config.ConfigManager;
import lol.nebula.module.combat.*;
import lol.nebula.module.exploit.NoRotateSet;
import lol.nebula.module.movement.*;
import lol.nebula.module.player.AutoRespawn;
import lol.nebula.module.player.FastPlace;
import lol.nebula.module.player.NoFall;
import lol.nebula.module.player.Scaffold;
import lol.nebula.module.visual.ClickUI;
import lol.nebula.module.visual.Fullbright;
import lol.nebula.module.visual.Interface;
import lol.nebula.module.visual.NoRender;
import lol.nebula.util.system.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.lang.String.format;

/**
 * @author aesthetical
 * @since 04/27/23
 */
public class ModuleManager {

    /**
     * The default config
     */
    public static final String DEFAULT_CONFIG = "defaults";

    private final Map<Class<? extends Module>, Module> moduleClassMap = new LinkedHashMap<>();
    private final Map<String, Module> moduleNameMap = new LinkedHashMap<>();

    /**
     * The configs folder
     */
    private final File configs = new File(ConfigManager.ROOT, "configs");

    public ModuleManager() {

        if (!configs.exists()) {
            boolean result = configs.mkdir();
            Nebula.getLogger().info("Directory {} was created {}", configs, result ? "successfully" : "unsuccessfully");
        }

        register(
                new Criticals(),
                new KillAura(),
                new Regen(),
                new VehicleOneTap(),
                new Velocity(),

                new NoRotateSet(),

                new AutoWalk(),
                new Flight(),
                new InvWalk(),
                new Jesus(),
                new NoSlow(),
                new Speed(),
                new Sprint(),

                new AutoRespawn(),
                new FastPlace(),
                new NoFall(),
                new Scaffold(),

                new ClickUI(),
                new Fullbright(),
                new Interface(),
                new NoRender()
        );

        Nebula.getLogger().info("Loaded {} modules", moduleClassMap.size());

        // load default config
        loadModules(DEFAULT_CONFIG);
    }

    /**
     * Registers modules
     * @param modules the modules to register
     */
    private void register(Module... modules) {
        for (Module module : modules) {
            module.loadSettings();

            moduleClassMap.put(module.getClass(), module);
            moduleNameMap.put(module.getTag(), module);
        }
    }

    /**
     * Saves a module config
     * @param configName the config name
     * @return the result for command output
     */
    public String saveModules(String configName) {
        File file = new File(configs, configName + ".cfg");
        if (!file.exists()) {
            try {
                boolean result = file.createNewFile();
                Nebula.getLogger().info("Config file {} was created {}", file, result ? "successfully" : "unsuccessfully");
            } catch (IOException e) {
                Nebula.getLogger().error("Failed to create file {}", file);
                e.printStackTrace();

                return "Failed to create config file";
            }
        }

        JsonArray object = new JsonArray();
        moduleNameMap.values().forEach((module) -> object.add(module.toJson()));

        try {
            FileUtils.write(file, FileUtils.getGSON().toJson(object));
            return format("Wrote config %s (%s) successfully", configName, file);
        } catch (IOException e) {
            Nebula.getLogger().error("Failed write to config {}", file);
            e.printStackTrace();
        }

        return "Failed to write to config";
    }

    /**
     * Loads a module config
     * @param configName the config name
     * @return the result for command output
     */
    public String loadModules(String configName) {
        File file = new File(configs, configName + ".cfg");
        if (!file.exists()) return "No config found with that name";

        String content;
        try {
            content = FileUtils.read(file);
        } catch (IOException e) {
            Nebula.getLogger().error("Failed to read config {}", configName);
            e.printStackTrace();

            return "Failed to load config";
        }

        if (content.isEmpty()) return "Empty config file";

        JsonArray object = FileUtils.getGSON().fromJson(content, JsonArray.class);
        if (object == null) return "Could not read config file";

        object.forEach((element) -> {
            if (!element.isJsonObject()) return;

            JsonObject value = element.getAsJsonObject();
            if (value.has("tag")) {
                Module module = get(value.get("tag").getAsString());
                if (module != null) module.fromJson(value);
            }
        });

        return format("Loaded config %s (%s) successfully", configName, file);
    }

    /**
     * Gets the module instance based off the supplied class
     * @param clazz the class of the specific module
     * @return the module that is this class, or null
     * @param <T> the extended module class
     */
    public <T extends Module> T get(Class<T> clazz) {
        return (T) moduleClassMap.getOrDefault(clazz, null);
    }

    /**
     * Gets the module instance based off the supplied name
     * @param name the name of the specific module
     * @return the module that is this class, or null
     * @param <T> the extended module class
     */
    public <T extends Module> T get(String name) {
        return (T) moduleNameMap.getOrDefault(name, null);
    }

    /**
     * Gets all the modules
     * @return the collection of modules registered
     */
    public Collection<Module> getModules() {
        return moduleClassMap.values();
    }
}

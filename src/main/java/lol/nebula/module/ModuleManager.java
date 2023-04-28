package lol.nebula.module;

import lol.nebula.Nebula;
import lol.nebula.module.movement.Flight;
import lol.nebula.module.movement.Sprint;
import lol.nebula.module.visual.Interface;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author aesthetical
 * @since 04/27/23
 */
public class ModuleManager {

    private final Map<Class<? extends Module>, Module> moduleClassMap = new LinkedHashMap<>();
    private final Map<String, Module> moduleNameMap = new LinkedHashMap<>();

    public ModuleManager() {

        register(

                new Flight(),
                new Sprint(),

                new Interface()
        );

        Nebula.getLogger().info("Loaded {} modules", moduleClassMap.size());
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

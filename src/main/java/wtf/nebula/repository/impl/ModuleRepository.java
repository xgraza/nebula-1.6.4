package wtf.nebula.repository.impl;

import com.google.gson.*;
import me.bush.eventbus.annotation.EventListener;
import wtf.nebula.event.KeyInputEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.impl.module.combat.Criticals;
import wtf.nebula.impl.module.combat.KillAura;
import wtf.nebula.impl.module.combat.TargetStrafe;
import wtf.nebula.impl.module.misc.*;
import wtf.nebula.impl.module.movement.*;
import wtf.nebula.impl.module.render.*;
import wtf.nebula.impl.value.Bind;
import wtf.nebula.impl.value.Value;
import wtf.nebula.repository.BaseRepository;
import wtf.nebula.repository.Repositories;
import wtf.nebula.repository.Repository;
import wtf.nebula.util.FileUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository("Modules")
public class ModuleRepository extends BaseRepository<Module> {
    public final Map<String, Module> moduleByName = new HashMap<>();
    public final Map<ModuleCategory, List<Module>> modulesByCategory = new HashMap<>();

    @Override
    public void init() {

        // combat moudles
        addChild(new Criticals());
        addChild(new KillAura());
        addChild(new TargetStrafe());

        // misc modules
        addChild(new AntiAFK());
        addChild(new ChatSuffix());
        addChild(new EnderchestBP());
        addChild(new Freecam());
        addChild(new MiddleClick());
        addChild(new Notifications());
        addChild(new PortalChat());
        addChild(new XCarry());

        // movement modules
        addChild(new AntiHunger());
        addChild(new AutoWalk());
        addChild(new IceSpeed());
        addChild(new InventoryMove());
        addChild(new Jesus());
        addChild(new NoSlow());
        addChild(new Speed());
        addChild(new Sprint());
        addChild(new Velocity());

        // render modules
        addChild(new ChunkBorders());
        addChild(new ClickGUI());
        addChild(new Fullbright());
        addChild(new HUD());
        addChild(new Nametags());
        addChild(new NoOverlay());
        addChild(new NoWeather());
        addChild(new StorageESP());
        addChild(new Tracers());
        addChild(new Waypoints());
        addChild(new XRay());

        log.info("Loaded " + children.size() + " modules. Loading configurations.");
        load();
    }

    @Override
    public void addChild(Module child) {
        child.reflectValues();

        children.add(child);

        moduleByName.put(child.getName(), child);
        childMap.put(child.getClass(), child);

        List<Module> categoryMods = modulesByCategory.computeIfAbsent(child.getCategory(), (s) -> new CopyOnWriteArrayList<>());
        categoryMods.add(child);
        modulesByCategory.put(child.getCategory(), categoryMods);
    }

    @EventListener
    public void onKeyInput(KeyInputEvent event) {

        // if we are not in a gui screen & the key is done being pressed
        if (mc.currentScreen == null && !event.isState()) {

            // loop through all modules
            // TODO: keybind manager?
            for (Module module : children) {

                // if we found a bind match, toggle module
                if (module.getBind() == event.getKeyCode()) {
                    module.setState(!module.getState());
                }
            }
        }
    }

    public <T extends Module> T getModule(Class<T> clazz) {
        return (T) childMap.getOrDefault(clazz, null);
    }

    public void save() {
        if (!Files.exists(FileUtil.MODULES)) {
            try {
                Files.createDirectory(FileUtil.MODULES);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (Entry<String, Module> entry : moduleByName.entrySet()) {
            Path moduleConfig = FileUtil.MODULES.resolve(entry.getKey() + ".json");
            if (!Files.exists(moduleConfig)) {
                FileUtil.create(moduleConfig);
            }

            Module module = entry.getValue();

            JsonObject object = new JsonObject();
            object.addProperty("state", module.getState());

            for (Entry<String, Value> e : module.getValueMap().entrySet()) {
                Object value = e.getValue().getValue();

                if (e.getValue() instanceof Bind) {
                    object.addProperty(e.getValue().getName(), (int) value);
                    continue;
                }

                if (value instanceof Boolean) {
                    object.addProperty(e.getKey(), (boolean) value);
                }

                else if (value instanceof Number) {

                    if (value instanceof Integer) {
                        object.addProperty(e.getKey(), (int) value);
                    }

                    else if (value instanceof Double) {
                        object.addProperty(e.getKey(), (double) value);
                    }

                    else if (value instanceof Float) {
                        object.addProperty(e.getKey(), (float) value);
                    }
                }
            }

            FileUtil.write(moduleConfig, new GsonBuilder().setPrettyPrinting().create().toJson(object));
        }

        log.info("Saved modules.");
    }

    public void load() {
        if (!Files.exists(FileUtil.MODULES)) {
            return;
        }

        for (Entry<String, Module> entry : moduleByName.entrySet()) {
            Path moduleConfig = FileUtil.MODULES.resolve(entry.getKey() + ".json");
            if (!Files.exists(moduleConfig)) {
                continue;
            }

            String str = FileUtil.read(moduleConfig);
            if (str == null || str.isEmpty()) {
                continue;
            }

            JsonObject object = new JsonParser().parse(str).getAsJsonObject();

            for (Entry<String, JsonElement> e : object.entrySet()) {
                if (e.getKey().equals("state")) {
                    entry.getValue().setState(e.getValue().getAsBoolean());
                }

                if (e.getKey().equals("Keybind")) {
                    entry.getValue().setBind(e.getValue().getAsInt());
                    continue;
                }

                Value value = entry.getValue().getValue(e.getKey());
                if (value == null) {
                    continue;
                }

                Object type = value.getValue();

                if (type instanceof Boolean) {
                    value.setValue((boolean) type);
                }

                else if (type instanceof Number) {

                    if (type instanceof Integer) {
                        value.setValue((int) type);
                    }

                    else if (type instanceof Double) {
                        value.setValue((double) type);
                    }

                    else if (type instanceof Float) {
                        value.setValue((float) type);
                    }
                }
            }
        }

        log.info("Loaded modules");
    }

    public static ModuleRepository get() {
        return Repositories.getRepo(ModuleRepository.class);
    }
}

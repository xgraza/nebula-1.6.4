package wtf.nebula.client.impl.module;

import com.google.gson.*;
import me.bush.eventbus.annotation.EventListener;
import org.lwjgl.input.Keyboard;
import wtf.nebula.client.api.config.Config;
import wtf.nebula.client.api.registry.BaseRegistry;
import wtf.nebula.client.core.Nebula;
import wtf.nebula.client.impl.event.impl.input.EventKeyInput;
import wtf.nebula.client.impl.event.impl.render.EventOpenGUI;
import wtf.nebula.client.impl.module.active.*;
import wtf.nebula.client.impl.module.combat.*;
import wtf.nebula.client.impl.module.exploits.*;
import wtf.nebula.client.impl.module.miscellaneous.*;
import wtf.nebula.client.impl.module.movement.*;
import wtf.nebula.client.impl.module.visuals.*;
import wtf.nebula.client.impl.module.world.*;
import wtf.nebula.client.utils.client.Timer;
import wtf.nebula.client.utils.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ModuleManager extends BaseRegistry<Module> {
    private final Map<ModuleCategory, List<Module>> categoryListMap = new HashMap<>();
    private final Map<String, Module> moduleNameMap = new HashMap<>();
    private final List<ToggleableModule> toggleableModules = new ArrayList<>();

    private final Timer timer = new Timer();

    private File configsFolder;
    public final Map<String, File> moduleConfigs = new HashMap<>();

    public ModuleManager() {
        Nebula.BUS.subscribe(this);

        // Active
        add(new Capes());
        add(new ClickGUI());
        add(new Colors());
        add(new HUD());
        add(new Notifications());

        // Combat
        add(new Aura());
        add(new AutoArmor());
        add(new AutoBed());
        add(new AutoCrystal());
        add(new AutoLog());
        add(new AutoPot());
        add(new BowAim());
        add(new Burrow());
        add(new Criticals());
        add(new Friendly()); // todo: better name? lol
        add(new Reach());
        add(new WTap());

        // Exploits
        add(new ChestBackpack());
        add(new FastBow());
        add(new FastEat());
        add(new Franky());
        add(new NewChunks());
        add(new NoRotate());
        add(new PacketCancel());
        add(new PingSpoof());
        add(new Portal());
        add(new PotionSaver());
        add(new Regen());
        add(new ResetVL());
        add(new XCarry());
        add(new Zoot());

        // Miscellaneous
        // add(new Announcer());
        add(new AntiAim());
        add(new AutoEat());
        // add(new AutoLogin());
        add(new AutoReconnect());
        add(new AutoRespawn());
        add(new DiscordRPC());
        add(new DoorOpener());
        // add(new EndermanLook());
        add(new ExtraTab());
        add(new InfiniteMover());
        add(new MiddleClick());
        add(new Replenish());
        add(new Spammer());
        add(new Translator());

        // Movement
        add(new AntiHunger());
        add(new AntiVoid());
        add(new AutoWalk());
        add(new FakeLag());
        add(new FastFall());
        add(new FastSwim());
        add(new Fly());
        add(new IceSpeed());
        add(new LongJump());
        add(new Jesus());
        add(new NoFall());
        add(new NoSlow());
        add(new SafeWalk());
        add(new Speed());
        add(new Sprint());
        add(new Static());
        add(new Step());
        add(new TargetStrafe());
        // add(new TestFly());
        add(new TickShift());
        add(new Velocity());

        // Visuals
        add(new Animations());
        add(new Aspect());
        add(new Chams());
        add(new FreeLook());
        add(new Fullbright());
        add(new ItemPhysics());
        add(new LightOverlay());
        add(new LogoutSpots());
        add(new NameTags());
        add(new NoRender());
        add(new NoWeather());
        add(new Shader());
        add(new Tracers());
        add(new Trails());
        add(new Trajectories());
        add(new ViewClip());
        add(new Waypoints());
        add(new XRay());
        add(new Yaw());

        // World
        add(new AutoFish());
        add(new AutoSign());
        add(new AutoTool());
        add(new AutoTunnel());
        add(new Avoid());
        add(new ChestStealer());
        add(new FastBreak());
        add(new FastUse());
        add(new FreeCamera());
        add(new GameSpeed());
        add(new Interact());
        add(new Nuker());
        add(new Scaffold());
        add(new SoundLocator());
        add(new StashFinder());

        new Config("modules") {
            @Override
            public void load(String element) {
                registry.forEach((m) -> {
                    String fileName = m.getLabel().toLowerCase().replaceAll(" ", "");
                    File file = new File(getFile(), fileName + ".json");
                    if (file.exists()) {
                        String content = FileUtils.read(file);
                        if (content == null || content.isEmpty()) {
                            return;
                        }

                        JsonObject obj = new JsonParser().parse(content).getAsJsonObject();
                        if (obj != null) {
                            m.fromConfig(obj);
                        }
                    }
                });
            }

            @Override
            public void save() {
                registry.forEach((m) -> {
                    String fileName = m.getLabel().toLowerCase().replaceAll(" ", "");
                    File file = new File(getFile(), fileName + ".json");

                    if (!file.exists()) {
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    FileUtils.write(file, m.toConfig().toString());
                });
            }
        };

        new Config("configs") {
            @Override
            public void load(String element) {
                configsFolder = getFile();

                File[] files = getFile().listFiles();
                if (files == null || files.length == 0) {
                    return;
                }

                for (File file : files) {
                    String name = file.getName();
                    if (file.isDirectory() || !name.endsWith(".cfg")) {
                        continue;
                    }

                    moduleConfigs.put(name.replaceAll(".cfg", ""), file);
                }
            }

            @Override
            public void save() {

            }
        };

    }

    @EventListener
    public void onKeyInput(EventKeyInput event) {
        if (event.getKeyCode() != Keyboard.KEY_NONE && !event.isState() && mc.currentScreen == null && timer.hasPassed(200L, false)) {
            registryMap.forEach((c, m) -> {
                if (m instanceof ToggleableModule) {
                    ToggleableModule module = (ToggleableModule) m;
                    if (module.getBind() == event.getKeyCode()) {
                        module.setRunning(!module.isRunning());
                    }
                }
            });
        }
    }

    @EventListener
    public void onGuiClose(EventOpenGUI event) {
        if (event.getCurrent() == null) {
            timer.resetTime();
        }
    }

    @Override
    protected void add(Module instance) {
        super.add(instance);

        if (instance instanceof ToggleableModule) {
            toggleableModules.add((ToggleableModule) instance);
        }

        moduleNameMap.put(instance.getLabel().toLowerCase(), instance);
        Arrays.stream(instance.getAliases()).forEach((alias) -> moduleNameMap.put(alias.toLowerCase(), instance));

        List<Module> modules = categoryListMap.computeIfAbsent(instance.category, (s) -> new ArrayList<>());
        modules.add(instance);
        categoryListMap.put(instance.category, modules);
    }

    public <T extends Module> T getModule(Class<T> clazz) {
        return (T) registryMap.getOrDefault(clazz, null);
    }

    public List<Module> getModulesByCategory(ModuleCategory category) {
        return categoryListMap.getOrDefault(category, new ArrayList<>());
    }

    public List<ToggleableModule> getToggleableModules() {
        return toggleableModules;
    }

    public Map<String, Module> getModuleNameMap() {
        return moduleNameMap;
    }

    public void saveConfig(String name) {
        File file = new File(configsFolder, name + ".cfg");
        if (!file.exists()) {
            moduleConfigs.put(name, file);
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        JsonObject obj = new JsonObject();
        for (Module module : getRegistry()) {
            try {
                obj.add(module.getLabel(), module.toConfig());
            } catch (Exception e) {
                // empty catch block
            }
        }

        FileUtils.write(file, new GsonBuilder().setPrettyPrinting().create().toJson(obj));
    }

    public boolean loadConfig(String name) {
        File file = moduleConfigs.getOrDefault(name, null);
        if (file == null || !file.exists()) {
            return false;
        }

        String content = FileUtils.read(file);
        if (content == null || content.isEmpty()) {
            return false;
        }

        JsonObject obj = JsonParser.parseString(content).getAsJsonObject();
        for (Module module : getRegistry()) {
            JsonObject o = obj.get(module.getLabel()).getAsJsonObject();
            module.fromConfig(o);
        }

        return true;
    }

    public boolean deleteConfig(String name) {
        File file = moduleConfigs.getOrDefault(name, null);
        if (file == null || !file.exists()) {
            return false;
        }


        boolean result = file.delete();
        if (result) {
            moduleConfigs.remove(name);
        }

        return result;
    }
}

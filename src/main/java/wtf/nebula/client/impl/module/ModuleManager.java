package wtf.nebula.client.impl.module;

import me.bush.eventbus.annotation.EventListener;
import org.lwjgl.input.Keyboard;
import wtf.nebula.client.api.registry.BaseRegistry;
import wtf.nebula.client.core.Launcher;
import wtf.nebula.client.impl.event.impl.input.KeyInputEvent;
import wtf.nebula.client.impl.module.active.ClickGUI;
import wtf.nebula.client.impl.module.active.Colors;
import wtf.nebula.client.impl.module.active.HUD;
import wtf.nebula.client.impl.module.combat.Aura;
import wtf.nebula.client.impl.module.combat.Criticals;
import wtf.nebula.client.impl.module.miscellaneous.Announcer;
import wtf.nebula.client.impl.module.miscellaneous.AutoRespawn;
import wtf.nebula.client.impl.module.miscellaneous.XCarry;
import wtf.nebula.client.impl.module.movement.*;
import wtf.nebula.client.impl.module.visuals.Fullbright;
import wtf.nebula.client.impl.module.visuals.Yaw;
import wtf.nebula.client.impl.module.world.ChestStealer;
import wtf.nebula.client.impl.module.world.FastUse;
import wtf.nebula.client.impl.module.world.Portal;
import wtf.nebula.client.impl.module.world.Scaffold;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModuleManager extends BaseRegistry<Module> {
    private final Map<ModuleCategory, List<Module>> categoryListMap = new HashMap<>();

    public ModuleManager() {
        Launcher.BUS.subscribe(this);

        // Active
        add(new ClickGUI());
        add(new Colors());
        add(new HUD());

        // Combat
        add(new Aura());
        add(new Criticals());

        // Miscellaneous
        add(new Announcer());
        add(new AutoRespawn());
        add(new XCarry());

        // Movement
        add(new AntiVoid());
        add(new AutoWalk());
        add(new SafeWalk());
        add(new Sprint());
        add(new Velocity());

        // Visuals
        add(new Fullbright());
        add(new Yaw());

        // World
        add(new ChestStealer());
        add(new FastUse());
        add(new Portal());
        add(new Scaffold());
    }

    @EventListener
    public void onKeyInput(KeyInputEvent event) {
        if (event.getKeyCode() != Keyboard.KEY_NONE && !event.isState() && mc.currentScreen == null) {
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

    @Override
    protected void add(Module instance) {
        super.add(instance);

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
}

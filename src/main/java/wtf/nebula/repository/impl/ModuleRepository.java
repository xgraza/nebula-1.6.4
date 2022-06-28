package wtf.nebula.repository.impl;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.src.EnumChatFormatting;
import wtf.nebula.event.KeyInputEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.impl.module.combat.Criticals;
import wtf.nebula.impl.module.combat.KillAura;
import wtf.nebula.impl.module.combat.TargetStrafe;
import wtf.nebula.impl.module.misc.ChatSuffix;
import wtf.nebula.impl.module.misc.Freecam;
import wtf.nebula.impl.module.misc.MiddleClick;
import wtf.nebula.impl.module.movement.*;
import wtf.nebula.impl.module.render.*;
import wtf.nebula.repository.BaseRepository;
import wtf.nebula.repository.Repositories;
import wtf.nebula.repository.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        addChild(new ChatSuffix());
        addChild(new Freecam());
        addChild(new MiddleClick());

        // movement modules
        addChild(new InventoryMove());
        addChild(new Jesus());
        addChild(new NoSlow());
        addChild(new Speed());
        addChild(new Sprint());
        addChild(new Velocity());

        // render modules
        addChild(new ClickGUI());
        addChild(new Fullbright());
        addChild(new HUD());
        addChild(new Nametags());
        addChild(new StorageESP());
        addChild(new Tracers());
        addChild(new XRay());

        log.info("Loaded " + children.size() + " modules.");
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

                    // if the nullcheck passes, send a toggle message
                    if (!module.nullCheck()) {
                        String toggleMsg = module.getState() ?
                                EnumChatFormatting.GREEN + "enabled" :
                                EnumChatFormatting.RED + "disabled";
                        sendChatMessage(module.hashCode(), module.getName() + " " + toggleMsg + EnumChatFormatting.RESET + ".");
                    }
                }
            }
        }
    }

    public <T extends Module> T getModule(Class<T> clazz) {
        return (T) childMap.getOrDefault(clazz, null);
    }

    public static ModuleRepository get() {
        return Repositories.getRepo(ModuleRepository.class);
    }
}

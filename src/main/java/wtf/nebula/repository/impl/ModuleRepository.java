package wtf.nebula.repository.impl;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.src.EnumChatFormatting;
import wtf.nebula.event.KeyInputEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.combat.Criticals;
import wtf.nebula.impl.module.combat.KillAura;
import wtf.nebula.impl.module.movement.*;
import wtf.nebula.impl.module.render.Fullbright;
import wtf.nebula.repository.BaseRepository;
import wtf.nebula.repository.Repositories;
import wtf.nebula.repository.Repository;

@Repository("Modules")
public class ModuleRepository extends BaseRepository<Module> {
    @Override
    public void init() {

        // combat moudles
        addChild(new Criticals());
        addChild(new KillAura());

        // movement modules
        addChild(new Jesus());
        addChild(new NoSlow());
        addChild(new Speed());
        addChild(new Sprint());
        addChild(new Step());
        addChild(new Velocity());

        // render modules
        addChild(new Fullbright());

        log.info("Loaded " + children.size() + " modules.");
    }

    @Override
    public void addChild(Module child) {
        children.add(child);
        childMap.put(child.getClass(), child);
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

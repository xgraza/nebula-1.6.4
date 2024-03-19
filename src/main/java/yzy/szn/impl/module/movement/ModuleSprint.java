package yzy.szn.impl.module.movement;

import yzy.szn.api.eventbus.Subscribe;
import yzy.szn.api.module.Module;
import yzy.szn.api.module.ModuleCategory;
import yzy.szn.api.module.ModuleManifest;
import yzy.szn.impl.event.EventUpdate;

import java.util.function.Consumer;

import static yzy.szn.api.MinecraftInstance.mc;

/**
 * @author graza
 * @since 02/17/24
 */
@ModuleManifest(
        name = "Sprint",
        category = ModuleCategory.MOVEMENT)
public final class ModuleSprint extends Module {

    public ModuleSprint() {
        setToggled(true);
    }

    @Subscribe
    private final Consumer<EventUpdate> updateListener = event -> {
        mc.gameSettings.keyBindSprint.setPressed(true);
    };
}

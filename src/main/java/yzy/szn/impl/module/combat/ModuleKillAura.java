package yzy.szn.impl.module.combat;

import net.minecraft.entity.EntityLivingBase;
import yzy.szn.api.eventbus.Subscribe;
import yzy.szn.api.module.Module;
import yzy.szn.impl.event.EventUpdate;

import java.util.function.Consumer;

/**
 * @author graza
 * @since 02/18/24
 */
public final class ModuleKillAura extends Module {

    private EntityLivingBase target;
    private boolean blocking;

    @Subscribe
    private final Consumer<EventUpdate> updateListener = event -> {

    };

    private enum TargetMode {
        SINGLE, SWITCH
    }
}

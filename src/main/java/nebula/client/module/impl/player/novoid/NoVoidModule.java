package nebula.client.module.impl.player.novoid;

import nebula.client.listener.bus.Listener;
import nebula.client.listener.bus.Subscribe;
import nebula.client.listener.event.player.EventMove;
import nebula.client.module.Module;
import nebula.client.module.ModuleMeta;
import nebula.client.util.value.Setting;
import nebula.client.util.value.SettingMeta;

/**
 * @author Gavin
 * @since 03/20/24
 */
@ModuleMeta(name = "NoVoid",
        description = "Prevents you from falling into the void")
public final class NoVoidModule extends Module {

    @SettingMeta("Mode")
    private final Setting<NoVoidMode> mode = new Setting<>(
            NoVoidMode.FLOAT);

    @Subscribe
    private final Listener<EventMove> move = event -> {
        if (mc.thePlayer.boundingBox.minY <= 0.0) {
            event.setY(0.0);
            mc.thePlayer.motionY = 0.0;
        }
    };
}

package wtf.nebula.impl.module.misc;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.src.ServerData;
import wtf.nebula.event.TickEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.impl.value.Value;

public class AutoReconnect extends Module {
    public ServerData serverData;

    public AutoReconnect() {
        super("AutoReconnect", ModuleCategory.MISC);
    }

    public final Value<Integer> notRespondingTimeout = new Value<>("NotResponding", 10, 1, 29);
    public final Value<Integer> reconnectTimeout = new Value<>("Delay", 10, 5, 30);

    @EventListener
    public void onTick(TickEvent event) {
        if (mc.currentServerData != null) {
            serverData = mc.currentServerData;
        }
    }

    @Override
    protected void onDeactivated() {
        super.onDeactivated();
        serverData = null;
    }
}

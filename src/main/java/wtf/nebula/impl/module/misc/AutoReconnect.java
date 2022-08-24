package wtf.nebula.impl.module.misc;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.client.multiplayer.ServerData;
import wtf.nebula.event.WorldUnloadEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.impl.value.Value;

public class AutoReconnect extends Module {
    public ServerData serverData;

    public AutoReconnect() {
        super("AutoReconnect", ModuleCategory.MISC);
    }

    public final Value<Integer> notRespondingTimeout = new Value<>("NotResponding", 10, 1, 29);
    public final Value<Integer> delay = new Value<>("Delay", 5, 0, 30);

    @Override
    protected void onDeactivated() {
        super.onDeactivated();
        serverData = null;
    }

    @EventListener
    public void onWorldUnload(WorldUnloadEvent event) {
        if (mc.currentServerData != null) {
            serverData = mc.currentServerData;
        }
    }
}

package wtf.nebula.client.impl.module.miscellaneous;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.client.multiplayer.ServerData;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.impl.event.impl.network.EventServerJoin;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;

public class AutoReconnect extends ToggleableModule {
    public final Property<Integer> delay = new Property<>(5, 0, 20, "Delay", "d");
    public ServerData serverData;

    public AutoReconnect() {
        super("Auto Reconnect", new String[]{"autoreconnect", "reconnect", "autoconnect"}, ModuleCategory.MISCELLANEOUS);
        offerProperties(delay);
    }

    @Override
    public String getTag() {
        return String.valueOf(delay.getValue());
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        serverData = null;
    }

    @EventListener
    public void onServerJoin(EventServerJoin event) {
        if (event.getServerData() != null) {
            serverData = event.getServerData();
        }
    }
}

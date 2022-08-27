package wtf.nebula.client.impl.module.world;

import me.bush.eventbus.annotation.EventListener;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.impl.event.impl.client.TickEvent;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;

public class Portal extends ToggleableModule {
    private final Property<Boolean> chat = new Property<>(true, "Chat");
    private final Property<Boolean> sounds = new Property<>(false, "Sounds", "sfx");

    public Portal() {
        super("Portal", new String[]{"portal", "portaltweaks"}, ModuleCategory.WORLD);
        offerProperties(chat, sounds);
    }

    @EventListener
    public void onTick(TickEvent event) {
        mc.thePlayer.inPortal = !chat.getValue();
    }
}

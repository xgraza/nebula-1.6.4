package wtf.nebula.client.impl.module.world;

import me.bush.eventbus.annotation.EventListener;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.impl.event.impl.world.EventLiquidCollideCheck;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;

public class Interact extends ToggleableModule {
    private final Property<Boolean> liquidPlace = new Property<>(false, "Liquid Place", "liquidplace", "waterplace");

    public Interact() {
        super("Interact", new String[]{"interactwiththingsweirdly"}, ModuleCategory.WORLD);
        offerProperties(liquidPlace);
    }

    @EventListener
    public void onLiquidCollideCheck(EventLiquidCollideCheck event) {
        event.setIn(event.isIn() || liquidPlace.getValue());
    }
}

package wtf.nebula.client.impl.module.combat;

import me.bush.eventbus.annotation.EventListener;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.impl.event.impl.world.EventReachModifier;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;

public class Reach extends ToggleableModule {
    private final Property<Float> interactDistance = new Property<>(4.5f, 4.0f, 6.0f, "Interact");
    private final Property<Double> hitDistance = new Property<>(3.0, 3.0, 6.0, "Attack");

    public Reach() {
        super("Reach", new String[]{"extrareach"}, ModuleCategory.COMBAT);
        offerProperties(interactDistance, hitDistance);
    }

    @EventListener
    public void onReach(EventReachModifier event) {
        event.setReach(event.getType().equals(EventReachModifier.Type.ATTACK) ?
                hitDistance.getValue() :
                interactDistance.getValue().doubleValue()
        );
        event.setCancelled(true);
    }
}

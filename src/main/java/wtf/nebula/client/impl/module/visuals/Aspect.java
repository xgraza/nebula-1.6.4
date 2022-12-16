package wtf.nebula.client.impl.module.visuals;

import me.bush.eventbus.annotation.EventListener;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.impl.event.impl.render.EventGluPerspective;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;

public class Aspect extends ToggleableModule {
    private final Property<Float> ratio = new Property<>(1.0f, 0.1f, 3.0f, "Ratio");

    public Aspect() {
        super("Aspect", new String[]{"aspectratio"}, ModuleCategory.VISUALS);
        offerProperties(ratio);
    }

    @EventListener
    public void onPerspective(EventGluPerspective event) {
        event.setAspect(ratio.getValue());
    }
}

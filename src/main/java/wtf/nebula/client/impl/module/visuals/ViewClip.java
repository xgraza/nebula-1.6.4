package wtf.nebula.client.impl.module.visuals;

import me.bush.eventbus.annotation.EventListener;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.impl.event.impl.render.EventCameraOutOfBounds;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;

public class ViewClip extends ToggleableModule {
    private final Property<Float> distance = new Property<>(3.0f, 1.0f, 50.0f, "Distance", "dist");

    public ViewClip() {
        super("View Clip", new String[]{"cameraclip", "viewclip"}, ModuleCategory.VISUALS);
        offerProperties(distance);
    }

    @EventListener
    public void onViewClip(EventCameraOutOfBounds event) {
        event.setDistance(distance.getValue());
        event.setCancelled(true);
    }
}

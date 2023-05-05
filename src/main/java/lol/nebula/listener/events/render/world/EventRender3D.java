package lol.nebula.listener.events.render.world;

/**
 * @author aesthetical
 * @since 05/01/23
 */
public class EventRender3D {
    private final float partialTicks;

    public EventRender3D(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return partialTicks;
    }
}

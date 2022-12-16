package wtf.nebula.client.impl.event.impl.world;

import me.bush.eventbus.event.Event;

public class EventReachModifier extends Event {
    private final Type type;
    private double reach;

    public EventReachModifier(Type type, double reach) {
        this.type = type;
        this.reach = reach;
    }

    public Type getType() {
        return type;
    }

    public double getReach() {
        return reach;
    }

    public void setReach(double reach) {
        this.reach = reach;
    }

    @Override
    protected boolean isCancellable() {
        return true;
    }

    public enum Type {
        INTERACT, ATTACK
    }
}

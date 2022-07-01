package wtf.nebula.impl.module.movement;

import me.bush.eventbus.annotation.EventListener;
import wtf.nebula.event.MotionEvent;
import wtf.nebula.event.PacketEvent;
import wtf.nebula.event.PacketEvent.Era;
import wtf.nebula.event.TickEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;

public class Phase extends Module {
    public Phase() {
        super("Phase", ModuleCategory.MOVEMENT);
    }

    @Override
    protected void onDeactivated() {
        super.onDeactivated();

        mc.thePlayer.noClip = false;
    }

    @EventListener
    public void onTick(TickEvent event) {

        if (isPhased()) {

        }
    }

    @EventListener
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getEra().equals(Era.PRE)) {

        }
    }

    @EventListener
    public void onMotion(MotionEvent event) {
        mc.thePlayer.noClip = true;
    }

    private boolean isPhased() {
        return !mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.boundingBox
                .instantCopy()
                .expand(-0.0625, -0.0625, -0.026))
                .isEmpty();
    }
}

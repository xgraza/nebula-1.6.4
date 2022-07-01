package wtf.nebula.impl.module.movement;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.src.Vec3;
import wtf.nebula.event.MotionUpdateEvent;
import wtf.nebula.event.MotionUpdateEvent.Era;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.util.world.BlockUtil;

public class Scaffold extends Module {
    public Scaffold() {
        super("Scaffold", ModuleCategory.MOVEMENT);
    }

    @EventListener
    public void onMotionUpdate(MotionUpdateEvent event) {
        Vec3 pos = Vec3.createVectorHelper(mc.thePlayer.posX, mc.thePlayer.boundingBox.minY, mc.thePlayer.posZ);

        if (event.getEra().equals(Era.POST)) {
            BlockUtil.placeBlock(pos, mc.thePlayer.inventory.currentItem);
        }
    }


}

package wtf.nebula.impl.module.world;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.src.Packet11PlayerPosition;
import wtf.nebula.event.TickEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.impl.value.Value;

public class Avoid extends Module {
    public Avoid() {
        super("Avoid", ModuleCategory.WORLD);
    }

    public final Value<Boolean> fire = new Value<>("Fire", true);
    public final Value<Boolean> cacti = new Value<>("Cactus", true);
    public final Value<Boolean> theVoid = new Value<>("Void", true);

    public final Value<Boolean> falling = new Value<>("Falling", false);
    public final Value<Float> fallDistance = new Value<>("FallDistance", 10.0f, 3.0f, 20.0f);

    @EventListener
    public void onTick(TickEvent event) {
        if (falling.getValue() && mc.thePlayer.fallDistance >= fallDistance.getValue()) {
            mc.thePlayer.fallDistance = 0.0f;
            mc.thePlayer.sendQueue.addToSendQueue(new Packet11PlayerPosition(
                    mc.thePlayer.posX, mc.thePlayer.boundingBox.minY + 1.0, mc.thePlayer.posY + 1.0, mc.thePlayer.posZ, false));
        }

        if (theVoid.getValue() && mc.thePlayer.boundingBox.minY <= 0.1) {
            mc.thePlayer.motionY = 0.0;
        }
    }
}

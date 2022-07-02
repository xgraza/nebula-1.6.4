package wtf.nebula.impl.module.world;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.src.Packet19EntityAction;
import wtf.nebula.event.TickEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;

import java.util.concurrent.ThreadLocalRandom;

public class AntiAFK extends Module {
    public AntiAFK() {
        super("AntiAFK", ModuleCategory.WORLD);
    }

    private long lastAction = 0L;

    @Override
    protected void onDeactivated() {
        super.onDeactivated();
        lastAction = 0L;
    }

    @EventListener
    public void onTick(TickEvent event) {
        if (System.currentTimeMillis() - lastAction >= 5000L) {

            lastAction = System.currentTimeMillis();

            int randomAction = randomInBounds(0, 3);
            switch (randomAction) {
                case 0:
                    mc.thePlayer.swingItem();
                    break;

                case 1:
                    mc.thePlayer.sendQueue.addToSendQueue(new Packet19EntityAction(mc.thePlayer, 1));
                    mc.thePlayer.sendQueue.addToSendQueue(new Packet19EntityAction(mc.thePlayer, 2));
                    break;

                case 2:
                    mc.thePlayer.jump();
                    break;

                case 3:
                    mc.thePlayer.rotationYaw = randomInBounds(0, 360);
                    mc.thePlayer.rotationPitch = randomInBounds(-90, 90);
                    break;
            }

        }
    }

    private int randomInBounds(int min, int max) {
        return ThreadLocalRandom.current().nextInt((max + 1) - min) + min;
    }
}

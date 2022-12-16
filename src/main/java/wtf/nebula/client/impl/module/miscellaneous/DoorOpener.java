package wtf.nebula.client.impl.module.miscellaneous;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.init.Blocks;
import net.minecraft.util.Vec3;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.core.Nebula;
import wtf.nebula.client.impl.event.impl.client.EventTick;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;
import wtf.nebula.client.utils.client.Timer;
import wtf.nebula.client.utils.player.PlayerUtils;
import wtf.nebula.client.utils.player.RotationUtils;
import wtf.nebula.client.utils.world.WorldUtils;

public class DoorOpener extends ToggleableModule {
    private final Property<Double> speed = new Property<>(15.0, 0.1, 20.0, "Speed", "s");
    private final Property<Double> range = new Property<>(5.0, 1.0, 6.0, "Range", "r", "dist");
    private final Property<Boolean> rotate = new Property<>(true, "Rotate", "rot", "face");
    private final Property<Boolean> swing = new Property<>(true, "Swing", "swingarm");

    private final Timer timer = new Timer();

    public DoorOpener() {
        super("Door Opener", new String[]{"dooropener", "opener"}, ModuleCategory.MISCELLANEOUS);
        offerProperties(speed, range, rotate, swing);
    }

    @EventListener
    public void onTick(EventTick event) {
        Vec3 pos = next();
        if (pos == null) {
            return;
        }

        if (timer.getTimePassedMs() / 50.0 >= 20.0 - speed.getValue()) {
            if (rotate.getValue()) {
                Nebula.getInstance().getRotationManager().setRotations(RotationUtils.calcAngles(pos));
            }

            if (mc.playerController.onPlayerRightClick(
                    mc.thePlayer,
                    mc.theWorld,
                    Nebula.getInstance().getInventoryManager().getHeld(),
                    (int) pos.xCoord,
                    (int) pos.yCoord,
                    (int) pos.zCoord,
                    PlayerUtils.getFacing().order_a,
                    pos.addVector(0.5, 0.5, 0.5)
            )) {

                timer.resetTime();

                if (swing.getValue()) {
                    mc.thePlayer.swingItem();
                } else {
                    mc.thePlayer.swingItemSilent();
                }
            }
        }
    }

    private Vec3 next() {
        Vec3 pos = PlayerUtils.getPosAt();
        double r = range.getValue();
        for (double x = -r; x <= r; ++x) {
            for (double y = -r; y <= r; ++y) {
                for (double z = -r; z <= r; ++z) {
                    Vec3 at = pos.addVector(x, y, z);
                    if (mc.thePlayer.getDistanceSq(at.xCoord + 0.5, at.yCoord + 1.0, at.zCoord + 0.5) >= r * r) {
                        continue;
                    }

                    if (WorldUtils.getBlock(at).equals(Blocks.wooden_door)) {
                        return at;
                    }
                }
            }
        }

        return null;
    }
}

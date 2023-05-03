package lol.nebula.module.player;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.EventStage;
import lol.nebula.listener.events.entity.EventWalkingUpdate;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.setting.Setting;
import lol.nebula.util.math.Pair;
import lol.nebula.util.math.RotationUtils;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static lol.nebula.util.world.WorldUtils.getOpposite;
import static lol.nebula.util.world.WorldUtils.isReplaceable;

/**
 * @author aesthetical
 * @since 05/02/23
 */
public class Flatten extends Module {
    private final Setting<Double> radius = new Setting<>(3.0, 0.5, 1.0, 6.0, "Radius");
    private final Setting<Boolean> instant = new Setting<>(false, "Instant");
    private final Setting<Integer> blocks = new Setting<>(6, 1, 50, "Blocks")
            .setVisibility(() -> !instant.getValue());
    private final Setting<Boolean> rotate = new Setting<>(true, "Rotate");

    // a queue of all of the placement positions
    private final Queue<Pair<Vec3, EnumFacing>> placeQueue = new ConcurrentLinkedQueue<>();

    public Flatten() {
        super("Flatten", "Flattens an area by placing blocks below you", ModuleCategory.PLAYER);
    }

    @Override
    public String getMetadata() {
        String meta = String.valueOf(radius.getValue());
        if (!placeQueue.isEmpty()) meta += " " + placeQueue.size();
        return meta;
    }

    @Listener
    public void onWalkingUpdate(EventWalkingUpdate event) {

        // if the currently held item is not a block, return
        ItemStack held = mc.thePlayer.getHeldItem();
        if (held == null || !(held.getItem() instanceof ItemBlock)) return;

        // update queue (10/10 code)
        updateQueue();

        // if there is nothing to place, fuck off
        if (placeQueue.isEmpty()) return;

        if (event.getStage() == EventStage.POST) {

            // the counter of how many blocks have been placed
            int blocksPlaced = 0;

            // if the instant boolean is toggled, just place everything. otherwise wait until the threshold
            // is exceeded (blocksPlaced < blocks)
            while (instant.getValue() ? !placeQueue.isEmpty() : blocksPlaced < blocks.getValue()) {

                // get the next place data, and if it is null do not continue
                Pair<Vec3, EnumFacing> next = placeQueue.poll();
                if (next == null) break;

                // rotate if specified
                if (rotate.getValue()) RotationUtils.setRotations(
                        event, RotationUtils.toBlock(next.getKey(), next.getValue()));

                // place block
                boolean result = mc.playerController.onPlayerRightClick(mc.thePlayer,
                        mc.theWorld,
                        held,
                        (int) next.getKey().xCoord,
                        (int) next.getKey().yCoord,
                        (int) next.getKey().zCoord,
                        next.getValue().getOrder_a(),
                        next.getKey().addVector(0.5, 0.5, 0.5)
                );

                // if the placement was successful, increment blocksPlaced and swing the item we're holding server-sided
                if (result) {
                    ++blocksPlaced;
                    mc.thePlayer.swingItemSilent();
                }

            }

        }
    }

    /**
     * Updates the place queue
     */
    private void updateQueue() {
        placeQueue.clear();

        int r = radius.getValue().intValue();
        for (int x = -r; x <= r; ++x) {
            for (int z = -r; z <= r; ++z) {
                Vec3 pos = mc.thePlayer.getGroundPosition().addVector(x, -1, z);
                if (mc.thePlayer.getDistanceSq(
                        pos.xCoord + 0.5,
                        pos.yCoord,
                        pos.zCoord + 0.5) <= radius.getValue() * radius.getValue()) {

                    for (EnumFacing facing : EnumFacing.values()) {
                        Vec3 neighbor = pos.addVector(facing.getFrontOffsetX(),
                                facing.getFrontOffsetY(), facing.getFrontOffsetZ());
                        if (!isReplaceable(neighbor)) {
                            placeQueue.add(new Pair<>(neighbor, getOpposite(facing)));
                            break;
                        }
                    }
                }
            }
        }
    }

}

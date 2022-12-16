package wtf.nebula.client.impl.module.world;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.core.Nebula;
import wtf.nebula.client.impl.event.impl.client.EventTick;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;
import wtf.nebula.client.utils.client.Timer;
import wtf.nebula.client.utils.player.PlayerUtils;
import wtf.nebula.client.utils.world.WorldUtils;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AutoTunnel extends ToggleableModule {

    private final Property<Integer> range = new Property<>(3, 1, 6, "Range", "r", "dist", "distance");
    private final Property<Double> speed = new Property<>(15.0, 0.1, 20.0, "Speed", "s");
    private final Property<Boolean> rotate = new Property<>(true, "Rotate", "rot", "face");
    private final Property<Boolean> swing = new Property<>(true, "Swing", "swingarm");
    private final Property<Swap> swap = new Property<>(Swap.SERVER, "Swap", "s");
    private final Property<Boolean> fill = new Property<>(false, "Fill");

    private final Queue<Vec3> breakPositions = new ConcurrentLinkedQueue<>();
    private final Queue<Vec3> fillPositions = new ConcurrentLinkedQueue<>();

    private final Timer timer = new Timer();
    private int state = 0;

    private int bX, bY, bZ;
    private double progress = 0.0;

    private boolean broken = false;
    private boolean swapped = false;

    public AutoTunnel() {
        super("Auto Tunnel", new String[]{"autotunnel", "tunneler"}, ModuleCategory.WORLD);
        offerProperties(range, speed, rotate, swap, swing, fill);
    }

    @Override
    protected void onDisable() {
        super.onDisable();

        breakPositions.clear();
        fillPositions.clear();

        state = 0;

        if (progress > 0.0 && bX != -1 && bY != -1 && bZ != -1 && !isNull()) {
            mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(1, bX, bY, bZ, -1));

        }

        bX = -1;
        bY = -1;
        bZ = -1;
        progress = 0.0;
    }

    @EventListener
    public void onTick(EventTick event) {

        int x = (int) Math.floor(mc.thePlayer.posX);
        int y = (int) mc.thePlayer.boundingBox.minY;
        int z = (int) Math.floor(mc.thePlayer.posZ);

        if (breakPositions.isEmpty()) {
            state = 1;

            EnumFacing facing = PlayerUtils.getFacing();

            Vec3 pos = new Vec3(Vec3.fakePool, x, y, z);
            for (int i = 1; i <= range.getValue(); ++i) {
                pos = pos.offset(facing);
                if (canBreak(pos) && !breakPositions.contains(pos)) {
                    breakPositions.add(pos);
                }

                Vec3 up = pos.offset(EnumFacing.UP);
                if (canBreak(up) && !breakPositions.contains(up)) {
                    breakPositions.add(up);
                }
            }
        }

        if (fill.getValue()) {

            int r = range.getValue();
            for (int x1 = -r; x1 <= r; ++x1) {
                for (int y1 = -r; y1 <= r; ++y1) {
                    for (int z1 = -r; z1 <= r; ++z1) {
                        Vec3 vec = new Vec3(Vec3.fakePool, x + x1, y + y1, z + z1);

                        Block block = WorldUtils.getBlock(vec);
                        if (block.equals(Blocks.lava) || block.equals(Blocks.flowing_lava) || block.equals(Blocks.water) || block.equals(Blocks.flowing_water)) {
                            fillPositions.add(vec);
                        }
                    }
                }
            }

        } else {
            fillPositions.clear();
        }

        if (state == 0) {

            if (breakPositions.isEmpty()) {
                state = 1;
                return;
            }

            int face = 0;
            try {
                face = PlayerUtils.getFacing().getOpposite().order_a;
            } catch (NullPointerException ignored) {

            }

            if (bX == -1 && bY == -1 && bZ == -1) {
                Vec3 next = breakPositions.poll();
                if (next == null) {
                    return;
                }

                if (!canBreak(next)) {
                    return;
                }

                if (mc.thePlayer.getDistanceSq(next.xCoord, next.yCoord, next.zCoord) > range.getValue() * range.getValue()) {
                    return;
                }

                bX = (int) next.xCoord;
                bY = (int) next.yCoord;
                bZ = (int) next.zCoord;
                progress = 0.0;

                mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(0, x, y, z, face));
            } else {

                if (!canBreak(new Vec3(Vec3.fakePool, bX, bY, bZ))) {
                    bX = -1;
                    bY = -1;
                    bZ = -1;
                    progress = 0.0;

                    if (swapped && swap.getValue().equals(Swap.SERVER)) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                        swapped = false;
                    }

                    broken = false;

                    return;
                }
            }

            int slot = AutoTool.getBestToolAt(bX, bY, bZ);
            if (slot == -1) {
                slot = Nebula.getInstance().getInventoryManager().serverSlot;
            }

            if (swap.getValue().equals(Swap.NONE) && Nebula.getInstance().getInventoryManager().serverSlot != slot) {
                return;
            }

//            if (progress == 0.0) {
//                mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(0, x, y, z, face));
//            }

            progress += FastBreak.getStrength(bX, bY, bZ, slot);

            if (progress >= 0.92) {

                if (Nebula.getInstance().getInventoryManager().serverSlot != slot) {
                    swapped = true;
                    switch (swap.getValue()) {
                        case CLIENT: {
                            mc.thePlayer.inventory.currentItem = slot;
                            break;
                        }

                        case SERVER: {
                            mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(slot));
                            break;
                        }
                    }
                }

                if (!broken) {
                    broken = true;
                    mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(2, x, y, z, face));
                }
            }

        } else if (state == 1) {

            fillPositions.removeIf((vec) ->
                    mc.thePlayer.getDistanceSq(
                            vec.xCoord, vec.yCoord, vec.zCoord) > range.getValue() * range.getValue());

            if (!fillPositions.isEmpty()) {

                if (timer.getTimePassedMs() / 50.0 < 20.0 - speed.getValue()) {
                    return;
                }

                int slot = -1;
                for (int i = 0; i < 9; ++i) {
                    ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
                    if (stack != null && stack.getItem() instanceof ItemBlock) {
                        slot = i;
                        break;
                    }
                }

                if (slot == -1) {
                    state = 0;
                    return;
                }

                Vec3 vec = fillPositions.poll();
                if (vec == null) {
                    state = 0;
                    return;
                }

                for (EnumFacing facing : EnumFacing.values()) {

                    Vec3 pos = vec.offset(facing);
                    if (WorldUtils.isReplaceable(pos)) {
                        continue;
                    }

                    boolean needsSwap = Nebula.getInstance().getInventoryManager().serverSlot != slot;
                    if (needsSwap) {
                        switch (swap.getValue()) {
                            case SERVER: {
                                mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(slot));
                                break;
                            }

                            case CLIENT: {
                                mc.thePlayer.inventory.currentItem = slot;
                                break;
                            }

                            case NONE: {
                                return;
                            }
                        }
                    }

                    boolean sneak = WorldUtils.SNEAK_BLOCKS.contains(WorldUtils.getBlock(pos));
                    if (sneak) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, 1));
                    }

                    boolean result = mc.playerController.onPlayerRightClick(
                            mc.thePlayer,
                            mc.theWorld,
                            Nebula.getInstance().getInventoryManager().getHeld(),
                            (int) pos.xCoord,
                            (int) pos.yCoord,
                            (int) pos.zCoord,
                            facing.getOpposite().order_a, // todo: better facing value
                            pos.addVector(0.5, 0.5, 0.5)
                    );

                    if (result) {
                        timer.resetTime();

                        if (swing.getValue()) {
                            mc.thePlayer.swingItem();
                        } else {
                            mc.thePlayer.swingItemSilent();
                        }

                    }

                    if (needsSwap) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                    }

                    if (sneak) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, 2));
                    }

                    return;
                }


            } else {
                state = 0;
            }
        }
    }

    private boolean canBreak(Vec3 pos) {
        Block block = WorldUtils.getBlock(pos);
        return !WorldUtils.isReplaceable(pos) && block.blockHardness != -1;
    }

    public enum Swap {
        NONE, CLIENT, SERVER
    }
}

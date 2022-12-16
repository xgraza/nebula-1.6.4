package wtf.nebula.client.impl.module.world;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.core.Nebula;
import wtf.nebula.client.impl.event.base.Era;
import wtf.nebula.client.impl.event.impl.move.EventMotionUpdate;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;
import wtf.nebula.client.impl.module.movement.AutoWalk;
import wtf.nebula.client.utils.client.Timer;
import wtf.nebula.client.utils.player.MoveUtils;
import wtf.nebula.client.utils.player.PlayerUtils;
import wtf.nebula.client.utils.player.RotationUtils;
import wtf.nebula.client.utils.world.WorldUtils;

public class Scaffold extends ToggleableModule {
    private final Property<Mode> mode = new Property<>(Mode.NORMAL, "Mode", "m", "type");
    private final Property<Double> speed = new Property<>(15.0, 0.1, 20.0, "Speed", "s");
    private final Property<Integer> extend = new Property<>(0, 0, 6, "Extend", "length");
    private final Property<Boolean> swing = new Property<>(true, "Swing", "swingarm");
    private final Property<Boolean> rotate = new Property<>(true, "Rotate", "rot", "face");
    private final Property<Boolean> tower = new Property<>(true, "Tower", "fastup")
            .setVisibility(() -> mode.getValue().equals(Mode.NORMAL));
    private final Property<Boolean> autoWalk = new Property<>(false, "Auto Walk", "autowalk")
            .setVisibility(() -> mode.getValue().equals(Mode.RADIAL));

    private final Timer towerTimer = new Timer();
    private final Timer placeTimer = new Timer();

    private PlaceInfo previous, current;
    private float[] angles;

    public Scaffold() {
        super("Scaffold", new String[]{"scaffold", "blockfly", "aladdin", "bridge", "autobridge"}, ModuleCategory.WORLD);
        offerProperties(mode, speed, extend, swing, rotate, tower, autoWalk);
    }

    @Override
    public String getTag() {
        return mode.getFixedValue();
    }

    @Override
    protected void onDisable() {
        super.onDisable();

        angles = null;
        previous = null;
        current = null;

        mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));

        mc.timer.timerSpeed = 1.0f;
    }

    @EventListener
    public void onMotionUpdate(EventMotionUpdate event) {

        if (current != null) {
            previous = current;
        }

        int slot = -1;
        if (PlayerUtils.isHolding(ItemBlock.class)) {
            slot = mc.thePlayer.inventory.currentItem;
        } else {

            for (int i = 0; i < 9; ++i) {
                ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
                if (stack == null || !(stack.getItem() instanceof ItemBlock)) {
                    continue;
                }

                if (slot == -1) {
                    slot = i;
                } else {
                    ItemStack s = mc.thePlayer.inventory.getStackInSlot(slot);
                    if (stack.stackSize > s.stackSize) {
                        slot = i;
                    }
                }
            }
        }

        if (slot == -1) {
            return;
        }

        if (previous != null) {
            angles = RotationUtils.calcAngles(previous.vec3, previous.facing);
        }

        if (angles != null && rotate.getValue()) {
            Nebula.getInstance().getRotationManager().setRotations(angles);
        }

        current = calcNext();
        if (current == null) {
            AutoWalk.pause = false;
            return;
        }

        AutoWalk.pause = autoWalk.getValue();

        if (event.getEra().equals(Era.POST) && placeTimer.getTimePassedMs() / 50.0 >= 20.0 - speed.getValue()) {

            boolean needsSwap = mc.thePlayer.inventory.currentItem != slot;
            if (needsSwap) {
                mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(slot));
            }

            boolean sneak = WorldUtils.SNEAK_BLOCKS.contains(WorldUtils.getBlock(current.vec3));
            if (sneak) {
                mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, 1));
            }

            boolean result = mc.playerController.onPlayerRightClick(
                    mc.thePlayer,
                    mc.theWorld,
                    Nebula.getInstance().getInventoryManager().getHeld(),
                    (int) current.vec3.xCoord,
                    (int) current.vec3.yCoord,
                    (int) current.vec3.zCoord,
                    current.facing.order_a,
                    current.vec3.addVector(0.5, 0.5, 0.5)
            );

            if (result) {
                placeTimer.resetTime();

                if (swing.getValue()) {
                    mc.thePlayer.swingItem();
                } else {
                    mc.thePlayer.swingItemSilent();
                }

                if (tower.getValue() && mc.gameSettings.keyBindJump.pressed && mode.getValue().equals(Mode.NORMAL)) {
                    mc.thePlayer.jump();
                    mc.thePlayer.motionX *= 0.2;
                    mc.thePlayer.motionZ *= 0.2;

                    if (mc.thePlayer.ticksExisted % 5 == 1) {
                        mc.timer.timerSpeed = 1.3f;
                    } else {
                        mc.timer.timerSpeed = 1.0f;
                    }

                    if (towerTimer.hasPassed(1200L, true)) {
                        mc.thePlayer.motionY = -0.28;
                    }
                } else {
                    mc.timer.timerSpeed = 1.0f;
                }
            }

            if (needsSwap) {
                mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
            }

            if (sneak) {
                mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, 2));
            }
        }
    }

    private PlaceInfo calcNext() {

        Vec3 pos = PlayerUtils.getPosUnder();

        if (mode.getValue().equals(Mode.RADIAL)) {
            double r = extend.getValue();
            for (double x = -r; x <= r; ++x) {
                for (double z = -r; z <= r; ++z) {
                    Vec3 below = pos.addVector(x, 0.0, z);

                    if (!WorldUtils.isReplaceable(below)) {
                        continue;
                    }

                    for (EnumFacing facing : EnumFacing.values()) {
                        Vec3 n = below.offset(facing);
                        if (!WorldUtils.isReplaceable(n)) {
                            return new PlaceInfo(n, facing.getOpposite());
                        }
                    }

                    for (EnumFacing facing : EnumFacing.values()) {
                        Vec3 n = below.offset(facing);
                        if (WorldUtils.isReplaceable(n)) {
                            for (EnumFacing dir : EnumFacing.values()) {
                                Vec3 v = n.offset(dir);
                                if (!WorldUtils.isReplaceable(v)) {
                                    return new PlaceInfo(v, dir.getOpposite());
                                }
                            }
                        }
                    }
                }
            }
        } else if (mode.getValue().equals(Mode.NORMAL)) {

            if (MoveUtils.isMoving() && !mc.gameSettings.keyBindJump.pressed && extend.getValue() != 0.0) {
                double distance = extend.getValue() + 0.0001;
                double blocks = 0.0;

                while (blocks <= distance) {
                    double[] strafe = MoveUtils.calcStrafe(blocks);
                    Vec3 v = new Vec3(Vec3.fakePool,
                            Math.floor(mc.thePlayer.posX) + strafe[0],
                            pos.yCoord,
                            Math.floor(mc.thePlayer.posZ) + strafe[1]);
                    if (WorldUtils.isReplaceable(v)) {
                        pos = v;
                        break;
                    }

                    blocks += distance / Math.floor(distance);
                }
            } else {
                pos = PlayerUtils.getPosUnder();
            }

            if (!WorldUtils.isReplaceable(pos)) {
                return null;
            }

            for (EnumFacing facing : EnumFacing.values()) {
                Vec3 n = pos.offset(facing);
                if (!WorldUtils.isReplaceable(n)) {
                    return new PlaceInfo(n, facing.getOpposite());
                }
            }

            for (EnumFacing facing : EnumFacing.values()) {
                Vec3 n = pos.offset(facing);
                if (WorldUtils.isReplaceable(n)) {
                    for (EnumFacing dir : EnumFacing.values()) {
                        Vec3 v = n.offset(dir);
                        if (!WorldUtils.isReplaceable(v)) {
                            return new PlaceInfo(v, dir.getOpposite());
                        }
                    }
                }
            }
        }

        return null;
    }

    public enum Mode {
        NORMAL, RADIAL
    }

    public static class PlaceInfo {
        private final Vec3 vec3;
        private final EnumFacing facing;

        public PlaceInfo(Vec3 vec3, EnumFacing facing) {
            this.vec3 = vec3;
            this.facing = facing;
        }
    }
}

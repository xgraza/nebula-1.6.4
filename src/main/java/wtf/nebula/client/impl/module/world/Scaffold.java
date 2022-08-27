package wtf.nebula.client.impl.module.world;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.core.Launcher;
import wtf.nebula.client.impl.event.base.Era;
import wtf.nebula.client.impl.event.impl.move.MotionUpdateEvent;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;
import wtf.nebula.client.utils.client.Timer;
import wtf.nebula.client.utils.player.PlayerUtils;
import wtf.nebula.client.utils.player.RotationUtils;
import wtf.nebula.client.utils.world.WorldUtils;

public class Scaffold extends ToggleableModule {
    private final Property<Boolean> swing = new Property<>(true, "Swing", "swingarm");
    private final Property<Boolean> rotate = new Property<>(true, "Rotate", "rot", "face");
    private final Property<Boolean> tower = new Property<>(true, "Tower", "fastup");

    private final Timer timer = new Timer();
    private PlaceInfo previous, current;
    private float[] angles;

    public Scaffold() {
        super("Scaffold", new String[]{"scaffold", "blockfly", "aladdin", "bridge", "autobridge"}, ModuleCategory.WORLD);
        offerProperties(swing, rotate, tower);
    }

    @EventListener
    public void onMotionUpdate(MotionUpdateEvent event) {
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

        boolean needsSwap = mc.thePlayer.inventory.currentItem != slot;
        if (needsSwap) {
            mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(slot));
        }

        if (previous != null) {
            angles = RotationUtils.calcAngles(previous.vec3, previous.facing);
        }

        if (angles != null && rotate.getValue()) {
            Launcher.getInstance().getRotationManager().setRotations(angles);
        }

        current = calcNext();
        if (current == null) {
            return;
        }

        if (event.getEra().equals(Era.POST)) {

            boolean result = mc.playerController.onPlayerRightClick(
                    mc.thePlayer,
                    mc.theWorld,
                    Launcher.getInstance().getInventoryManager().getHeld(),
                    (int) current.vec3.xCoord,
                    (int) current.vec3.yCoord,
                    (int) current.vec3.zCoord,
                    current.facing.order_a,
                    current.vec3.addVector(0.5, 0.5, 0.5)
            );

            if (result) {
                if (swing.getValue()) {
                    mc.thePlayer.swingItem();
                } else {
                    mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
                }

                if (tower.getValue() && mc.gameSettings.keyBindJump.pressed) {
                    mc.thePlayer.motionY = 0.4;
                    mc.thePlayer.motionX *= 0.2;
                    mc.thePlayer.motionZ *= 0.2;

                    if (timer.hasPassed(1200L, true)) {
                        mc.thePlayer.motionY = -0.28;
                        mc.timer.timerSpeed = 1.0f;
                    }
                }

                if (needsSwap) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                }
            }
        }
    }

    @Override
    protected void onDisable() {
        super.onDisable();

        angles = null;
        previous = null;
        current = null;

        mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
    }

    private PlaceInfo calcNext() {
        Vec3 below = PlayerUtils.getPosUnder();

        if (!WorldUtils.isReplaceable(below)) {
            return null;
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

        return null;
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

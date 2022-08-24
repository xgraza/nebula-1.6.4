package wtf.nebula.impl.module.world;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import optifine.BlockPos;
import wtf.nebula.event.MotionUpdateEvent;
import wtf.nebula.event.PacketEvent;
import wtf.nebula.event.SafewalkEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.impl.value.Value;
import wtf.nebula.util.Timer;
import wtf.nebula.util.world.BlockUtil;
import wtf.nebula.util.world.player.inventory.InventoryRegion;
import wtf.nebula.util.world.player.inventory.InventoryUtil;

public class Scaffold extends Module {
    public Scaffold() {
        super("Scaffold", ModuleCategory.WORLD);
    }

    public final Value<Boolean> tower = new Value<>("Tower", false);
    public final Value<Boolean> swing = new Value<>("Swing", true);

    private boolean sentCancelSprint = false;
    private final Timer towerTimer = new Timer();

    @Override
    protected void onActivated() {
        super.onActivated();

        if (!nullCheck()) {
            sentCancelSprint = true;
            mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, 5));
        }
    }

    @Override
    protected void onDeactivated() {
        super.onDeactivated();

        sentCancelSprint = false;

        // re-sync
        mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));

        if (sentCancelSprint && mc.thePlayer.isSprinting()) {
            mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, 4));
        }
    }

    @EventListener
    public void onSafewalk(SafewalkEvent event) {
        event.setCancelled(true);
    }

    @EventListener
    public void onMotionUpdate(MotionUpdateEvent event) {
        int slot = InventoryUtil.findSlot(InventoryRegion.HOTBAR,
                (stack) -> stack != null && stack.getItem() instanceof ItemBlock && stack.stackSize > 0);
        if (slot == -1) {
            mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
            return;
        }

        if (!sentCancelSprint) {
            sentCancelSprint = true;
            mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, 5));
        }

        PlaceInfo next = getNextPlacement();
        if (next != null && event.getEra().equals(MotionUpdateEvent.Era.POST)) {
            mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(slot));

            boolean sneak = BlockUtil.SNEAK_BLOCKS.contains(BlockUtil.getBlockFromVec(next.pos)) && !mc.thePlayer.isSneaking();
            if (sneak) {
                mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, 1));
            }

            int x = (int) next.pos.xCoord;
            int y = (int) next.pos.yCoord;
            int z = (int) next.pos.zCoord;

            boolean success = mc.playerController.onPlayerRightClick(
                    mc.thePlayer,
                    mc.theWorld,
                    mc.thePlayer.inventory.getStackInSlot(slot),
                    x, y, z,
                    next.facing,
                    next.pos.addVector(0.5, 0.0, 0.5)
            );

            if (success) {
                mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());

                if (tower.getValue() && mc.gameSettings.keyBindJump.isPressed()) {
                    mc.thePlayer.jump();
                    mc.thePlayer.motionX *= 0.3;
                    mc.thePlayer.motionZ *= 0.3;

                    if (towerTimer.passedTime(1200L, true)) {
                        mc.thePlayer.motionY = -0.28;
                    }
                }

                mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
            }

            if (sneak) {
                mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, 2));
            }
        }
    }

    private PlaceInfo getNextPlacement() {
        Vec3 below = Vec3.createVectorHelper(
                Math.floor(mc.thePlayer.posX),
                (int) mc.thePlayer.posY - 2,
                Math.floor(mc.thePlayer.posZ));

        if (!BlockUtil.isReplaceable(below)) {
            return null;
        }

        for (EnumFacing facing : EnumFacing.values()) {
            Vec3 n = below.offset(facing);
            if (!BlockUtil.isReplaceable(n)) {
                return new PlaceInfo(n, facing.order_b);
            }
        }

        for (EnumFacing facing : EnumFacing.values()) {
            Vec3 n = below.offset(facing);
            if (BlockUtil.isReplaceable(n)) {
                for (EnumFacing dir : EnumFacing.values()) {
                    Vec3 v = n.offset(dir);
                    if (!BlockUtil.isReplaceable(v)) {
                        return new PlaceInfo(v, dir.order_b);
                    }
                }
            }
        }

        return null;
    }

    @EventListener
    public void onPacketSend(PacketEvent.Send event) {

        if (event.getPacket() instanceof C0BPacketEntityAction) {

            C0BPacketEntityAction packet = event.getPacket();

            // do not allow us to start sprinting, but we can sprint client-side
            if (packet.action == 4 && sentCancelSprint) {
                event.setCancelled(true);
            }
        }
    }

    private static class PlaceInfo {
        private final Vec3 pos;
        private final int facing;

        public PlaceInfo(Vec3 pos, int facing) {
            this.pos = pos;
            this.facing = facing;
        }
    }
}

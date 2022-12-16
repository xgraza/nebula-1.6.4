package wtf.nebula.client.impl.module.combat;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.core.Nebula;
import wtf.nebula.client.impl.event.base.Era;
import wtf.nebula.client.impl.event.impl.move.EventMotionUpdate;
import wtf.nebula.client.impl.event.impl.network.EventPacket;
import wtf.nebula.client.impl.event.impl.render.EventRender3D;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;
import wtf.nebula.client.impl.module.active.Colors;
import wtf.nebula.client.utils.player.PlayerUtils;
import wtf.nebula.client.utils.player.RotationUtils;
import wtf.nebula.client.utils.render.RenderEngine;
import wtf.nebula.client.utils.render.enums.Dimension;
import wtf.nebula.client.utils.render.renderers.impl.three.FilledBox;
import wtf.nebula.client.utils.world.WorldUtils;

public class Burrow extends ToggleableModule {
    private static final double[] OFFSETS = { 0.419, 0.753, 1.00, 1.16 };

    private final Property<Boolean> flag = new Property<>(true, "Flag");
    private final Property<Boolean> instant = new Property<>(false, "Instant", "i");
    private final Property<Boolean> swing = new Property<>(true, "Swing", "swingarm");
    private final Property<Boolean> rotate = new Property<>(true, "Rotate", "rot", "face");
    private final Property<Boolean> render = new Property<>(true, "Render", "draw");
    private final Property<Boolean> autoDisable = new Property<>(true, "Auto Disable", "autodisable", "disable");

    private boolean placing = false;
    private boolean swapped = false;

    public Burrow() {
        super("Burrow", new String[]{"selffill"}, ModuleCategory.COMBAT);
        offerProperties(flag, instant, swing, rotate, render, autoDisable);
    }

    @Override
    protected void onDisable() {
        super.onDisable();

        if (!isNull() && swapped) {
            mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
        }

        swapped = false;

        placing = false;
    }

    @EventListener
    public void onRenderWorld(EventRender3D event) {

        if (render.getValue()) {
            Vec3 at = PlayerUtils.getPosAt();

            RenderEngine.of(Dimension.THREE)
                    .add(new FilledBox(
                            new AxisAlignedBB(at)
                                    .offset(-RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ),
                            Colors.getClientColor(77)))
                    .render();
        }
    }

    @EventListener
    public void onMotionUpdate(EventMotionUpdate event) {

        if (event.getEra().equals(Era.POST)) {
            burrow();
        }
    }

    @EventListener
    public void onPacket(EventPacket event) {
        if (event.getEra().equals(Era.PRE) && !isNull()) {
            Vec3 at = PlayerUtils.getPosAt();

            if (event.getPacket() instanceof S23PacketBlockChange && instant.getValue()) {
                S23PacketBlockChange packet = event.getPacket();

                Vec3 vec3 = new Vec3(Vec3.fakePool, packet.x, packet.y, packet.z);

                if (vec3.xCoord == at.xCoord && vec3.yCoord == at.yCoord && vec3.zCoord == at.zCoord && WorldUtils.isReplaceable(vec3)) {
                    burrow();
                }
            }
        }
    }

    private boolean isBurrowed() {
        return !WorldUtils.isReplaceable(PlayerUtils.getPosAt());
    }

    private void burrow() {
        if (placing) {
            return;
        }

        Vec3 at = PlayerUtils.getPosAt();
        if (isBurrowed()) {

            if (autoDisable.getValue()) {
                print("You are already in the block!", hashCode());
                setRunning(false);
            }

            return;
        }

        int slot = -1;
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            if (stack == null || !(stack.getItem() instanceof ItemBlock)) {
                continue;
            }

            Block block = ((ItemBlock) stack.getItem()).block;
            if (block.equals(Blocks.obsidian) || block.equals(Blocks.ender_chest)) {
                slot = i;
            }
        }

        if (slot == -1) {
            print("There is no obsidian/ender chest in your hotbar.", hashCode());
            setRunning(false);
            return;
        }

        placing = true;

        Vec3 vec = at.offset(EnumFacing.DOWN);
        EnumFacing facing = EnumFacing.UP;

        if (Nebula.getInstance().getInventoryManager().serverSlot != slot) {
            swapped = true;
            mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(slot));
        }

        if (rotate.getValue()) {
            float[] angles = RotationUtils.calcAngles(vec, facing);
            Nebula.getInstance().getRotationManager().setRotations(angles);
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(angles[0], angles[1], mc.thePlayer.onGround));
        }

        if (flag.getValue()) {
            for (double offset : OFFSETS) {
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(
                        mc.thePlayer.posX,
                        mc.thePlayer.boundingBox.minY + offset,
                        mc.thePlayer.posY + offset,
                        mc.thePlayer.posZ,
                        false
                ));
            }
        }

        Vec3 hitVec = vec.addVector(0.5, 0.5, 0.5);

        float facingX = (float) (hitVec.xCoord - vec.xCoord);
        float facingY = (float) (hitVec.yCoord - vec.yCoord);
        float facingZ = (float) (hitVec.zCoord - vec.zCoord);

        mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(
                (int) vec.xCoord,
                (int) vec.yCoord,
                (int) vec.zCoord,
                facing.order_a,
                mc.thePlayer.inventory.getStackInSlot(slot),
                facingX,
                facingY,
                facingZ
        ));

        if (swing.getValue()) {
            mc.thePlayer.swingItem();
        } else {
            mc.thePlayer.swingItemSilent();
        }

        if (flag.getValue()) {
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(
                    mc.thePlayer.posX,
                    mc.thePlayer.boundingBox.minY + 4.0,
                    mc.thePlayer.posY + 4.0,
                    mc.thePlayer.posZ,
                    false
            ));
        }

        mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
        swapped = false;

        placing = false;

        if (autoDisable.getValue()) {
            setRunning(false);
        }
    }
}

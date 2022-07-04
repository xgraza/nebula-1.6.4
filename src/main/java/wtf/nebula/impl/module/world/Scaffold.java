package wtf.nebula.impl.module.world;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.src.*;
import org.lwjgl.input.Keyboard;
import wtf.nebula.event.MotionUpdateEvent;
import wtf.nebula.event.MotionUpdateEvent.Era;
import wtf.nebula.event.PacketEvent;
import wtf.nebula.event.RenderWorldEvent;
import wtf.nebula.event.SafewalkEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.impl.value.Value;
import wtf.nebula.util.Timer;
import wtf.nebula.util.render.ColorUtil;
import wtf.nebula.util.render.RenderUtil;
import wtf.nebula.util.world.BlockUtil;
import wtf.nebula.util.world.player.inventory.InventoryRegion;
import wtf.nebula.util.world.player.inventory.InventoryUtil;

import static org.lwjgl.opengl.GL11.*;

public class Scaffold extends Module {
    public Scaffold() {
        super("Scaffold", ModuleCategory.WORLD);
    }

    public final Value<Boolean> tower = new Value<>("Tower", false);
    public final Value<Boolean> swing = new Value<>("Swing", true);
    public final Value<Boolean> render = new Value<>("Render", true);

    private Vec3 pos;
    private boolean sentCancelSprint = false;

    private final Timer towerTimer = new Timer();

    @Override
    protected void onActivated() {
        super.onActivated();

        if (!nullCheck()) {
            sentCancelSprint = true;
            mc.thePlayer.sendQueue.addToSendQueue(new Packet19EntityAction(mc.thePlayer, 5));
        }
    }

    @Override
    protected void onDeactivated() {
        super.onDeactivated();

        pos = null;
        sentCancelSprint = false;

        // re-sync
        mc.thePlayer.sendQueue.addToSendQueue(new Packet16BlockItemSwitch(mc.thePlayer.inventory.currentItem));

        if (sentCancelSprint && mc.thePlayer.isSprinting()) {
            mc.thePlayer.sendQueue.addToSendQueue(new Packet19EntityAction(mc.thePlayer, 4));
        }
    }

    @EventListener
    public void onRenderWorld(RenderWorldEvent event) {

        if (render.getValue() && pos != null) {

            AxisAlignedBB box = new AxisAlignedBB(
                    pos.xCoord, pos.yCoord, pos.zCoord,
                    pos.xCoord + 1, pos.yCoord + 1, pos.zCoord + 1
            ).offset(-RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ);

            glPushMatrix();

            glDisable(GL_DEPTH_TEST);

            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

            glEnable(GL_LINE_SMOOTH);
            glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);

            glLineWidth(1.5f);

            ColorUtil.setColor(0x90FFFFFF);
            RenderUtil.drawFilledBoundingBox(box);
            ColorUtil.setColor(0xFFFFFFFF);
            mc.renderGlobal.drawOutlinedBoundingBox(box);

            glDisable(GL_LINE_SMOOTH);
            glEnable(GL_DEPTH_TEST);
            glDisable(GL_BLEND);

            glPopMatrix();
        }
    }

    @EventListener
    public void onSafewalk(SafewalkEvent event) {
        event.setCancelled(true);
    }

    @EventListener
    public void onMotionUpdate(MotionUpdateEvent event) {

        if (!sentCancelSprint) {
            sentCancelSprint = true;
            mc.thePlayer.sendQueue.addToSendQueue(new Packet19EntityAction(mc.thePlayer, 5));
        }

        int slot = InventoryUtil.findSlot(InventoryRegion.HOTBAR,
                (stack) -> stack.getItem() != null && stack.getItem() instanceof ItemBlock);

        if (slot == -1) {
            return;
        }

        int oldSlot = mc.thePlayer.inventory.currentItem;
        mc.thePlayer.sendQueue.addToSendQueue(new Packet16BlockItemSwitch(slot));

        if (event.getEra().equals(Era.PRE)) {
            pos = Vec3.createVectorHelper(
                    Math.floor(mc.thePlayer.posX),
                    mc.thePlayer.posY - 2,
                    Math.floor(mc.thePlayer.posZ));

            if (BlockUtil.placeBlock(pos, swing.getValue(), slot)) {

                if (tower.getValue() && Keyboard.isKeyDown(mc.gameSettings.keyBindJump.keyCode)) {
                    mc.thePlayer.jump();
                    mc.thePlayer.motionX *= 0.3;
                    mc.thePlayer.motionZ *= 0.3;

                    if (towerTimer.passedTime(1200L, true)) {
                        mc.thePlayer.motionY = -0.28;
                    }
                }
            }

            mc.thePlayer.sendQueue.addToSendQueue(new Packet16BlockItemSwitch(oldSlot));
        }
    }

    @EventListener
    public void onPacketSend(PacketEvent.Send event) {

        if (event.getPacket() instanceof Packet19EntityAction) {

            Packet19EntityAction packet = event.getPacket();

            // do not allow us to start sprinting, but we can sprint client-side
            if (packet.action == 4 && sentCancelSprint) {
                event.setCancelled(true);
            }
        }
    }
}

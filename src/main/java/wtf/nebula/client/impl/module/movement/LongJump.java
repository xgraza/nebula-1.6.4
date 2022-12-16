package wtf.nebula.client.impl.module.movement;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.core.Nebula;
import wtf.nebula.client.impl.event.base.Era;
import wtf.nebula.client.impl.event.impl.client.EventTick;
import wtf.nebula.client.impl.event.impl.move.EventMotion;
import wtf.nebula.client.impl.event.impl.move.EventMotionUpdate;
import wtf.nebula.client.impl.event.impl.network.EventPacket;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;
import wtf.nebula.client.utils.player.MoveUtils;
import wtf.nebula.client.utils.player.PlayerUtils;

import java.util.List;

public class LongJump extends ToggleableModule {

    private final Property<Mode> mode = new Property<>(Mode.VANILLA, "Mode", "m", "type");
    private final Property<Double> boost = new Property<>(4.5, 1.0, 7.0, "Boost", "speed", "movespped", "boostspeed")
            .setVisibility(() -> mode.getValue().equals(Mode.VANILLA));

    private final Property<Damage> damage = new Property<>(Damage.NONE, "Damage", "dmg", "selfdamage", "selfdmg");
    private final Property<Integer> pullTicks = new Property<>(4, 3, 20, "Pull Ticks", "pullticks", "ticks")
            .setVisibility(() -> damage.getValue().equals(Damage.BOW));

    private final Property<Boolean> glide = new Property<>(true, "Glide", "gl");

    private int stage = 1;
    private double moveSpeed = 0.0;
    private double distance = 0.0;

    private boolean damaged = false;
    private int pullBackTicks = 0;

    public LongJump() {
        super("Long Jump", new String[]{"longjump", "lj"}, ModuleCategory.MOVEMENT);
        offerProperties(mode, boost, damage, pullTicks, glide);
    }

    @Override
    protected void onEnable() {
        super.onEnable();

        if (isNull()) {
            setRunning(false);
            return;
        }

        if (mode.getValue().equals(Mode.VANILLA)) {
            stage = 2;
            moveSpeed = boost.getValue() * MoveUtils.getBaseNcpSpeed() - 0.01;
            distance = 0.0;
        } else if (mode.getValue().equals(Mode.NCP)) {
            stage = 0;
        }

        if (isNull()) {
            setRunning(false);
            return;
        }

        switch (damage.getValue()) {
            case PACKET: {
                for (int i = 0; i < 49; ++i) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(
                            mc.thePlayer.posX, mc.thePlayer.boundingBox.minY + 0.0624,
                            mc.thePlayer.posY + 0.0624, mc.thePlayer.posZ,
                            false
                    ));
                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(
                            mc.thePlayer.posX, mc.thePlayer.boundingBox.minY,
                            mc.thePlayer.posY, mc.thePlayer.posZ,
                            false
                    ));
                }

                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer());
                break;
            }

            case FAKE: {
                mc.thePlayer.sendQueue.handleEntityStatus(new S19PacketEntityStatus(mc.thePlayer, (byte) 2));
                damaged = true;
                break;
            }

            case BOW: {
                damaged = false;
                pullBackTicks = 0;
                break;
            }

            case NONE: {
                damaged = true;
                break;
            }
        }
    }

    @Override
    protected void onDisable() {
        super.onDisable();

        mc.timer.timerSpeed = 1.0f;
        damaged = false;

        if (!isNull()) {
            Nebula.getInstance().getInventoryManager().sync();
        }
    }

    @EventListener
    public void onTick(EventTick event) {

        if (mc.thePlayer.hurtTime != 0) {
            damaged = true;
            Nebula.getInstance().getInventoryManager().sync();
        }

        if (damage.getValue().equals(Damage.BOW) && !damaged) {

            int slot = -1;
            for (int i = 0; i < 9; ++i) {
                ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
                if (stack != null && stack.getItem() instanceof ItemBow) {
                    slot = i;
                    break;
                }
            }

            if (slot == -1) {
                print("You need a bow in your hotbar");
                setRunning(false);
                return;
            }

            if (Nebula.getInstance().getInventoryManager().serverSlot != slot) {

                damaged = false;
                pullBackTicks = 0;

                boolean found = false;
                for (int i = 0; i < 36; ++i) {
                    ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
                    if (stack != null && stack.getItem().equals(Items.arrow)) {
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    print("No arrows found in inventory.");
                    setRunning(false);
                    return;
                }

                mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(slot));
                return;
            }

            Nebula.getInstance().getRotationManager().setRotations(new float[] { mc.thePlayer.rotationYaw, -90.0f });

            if (pullBackTicks < pullTicks.getValue()) {
                mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(-1, -1, -1, 255, Nebula.getInstance().getInventoryManager().getHeld(), 0.0f, 0.0f, 0.0f));
                ++pullBackTicks;
            } else {
                PlayerUtils.stopUseCurrentItem();
            }
        }
    }

    @EventListener
    public void onMotion(EventMotion event) {

        if (!damaged) {
            event.x = 0.0;
            event.z = 0.0;
            return;
        }

        if (mode.getValue().equals(Mode.NCP)) {

            if (MoveUtils.isMoving()) {

                if (stage == 0) {
                    moveSpeed = 1.0 + MoveUtils.getBaseNcpSpeed() - 0.05;
                } else if (stage == 1) {

                    mc.thePlayer.motionY = 0.42;
                    event.y = mc.thePlayer.motionY;

                    moveSpeed *= 2.13;
                } else if (stage == 2) {
                    double diff = 0.66 * (distance - MoveUtils.getBaseNcpSpeed());
                    moveSpeed = distance - diff;
                } else {
                    moveSpeed = distance - distance / 159.0;
                }

                moveSpeed = Math.max(moveSpeed, MoveUtils.getBaseNcpSpeed());

                double[] strafe = MoveUtils.calcStrafe(moveSpeed);

                event.x = strafe[0];
                event.z = strafe[1];

                if (glide.getValue()) {

                    List list1 = mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.boundingBox.copy().offset(0.0, mc.thePlayer.motionY, 0.0));
                    List list2 = mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.boundingBox.copy().offset(0.0, -0.2, 0.0));

                    if (!mc.thePlayer.isCollidedVertically && (list1.size() > 0 || list2.size() > 0)) {
                        mc.thePlayer.motionY = -1.0E-4;
                        event.y = mc.thePlayer.motionY;
                    }
                }

                ++stage;
            }

        } else if (mode.getValue().equals(Mode.VANILLA)) {
            if (mc.thePlayer.onGround) {
                moveSpeed = boost.getValue() * MoveUtils.getBaseNcpSpeed() - 0.01;
                stage = 2;
            }

            if (stage == 2) {

                mc.timer.timerSpeed = 1.0f;
                stage = 3;
                moveSpeed *= 2.149;

                if (mc.thePlayer.onGround && MoveUtils.isMoving()) {
                    mc.thePlayer.motionY = MoveUtils.getJumpHeight(false);
                    event.y = mc.thePlayer.motionY;
                }
            } else if (stage == 3) {
                stage = 4;

                double adjusted = 0.66 * (distance - MoveUtils.getBaseNcpSpeed());
                moveSpeed = distance - adjusted;
            } else {
                List bbs = mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.boundingBox.copy());
                if (!bbs.isEmpty() || mc.thePlayer.isCollidedVertically) {
                    stage = 1;
                }

                moveSpeed -= moveSpeed / 159.0;
            }

            //mc.timer.timerSpeed = 0.8f;

            if (glide.getValue()) {
                List list1 = mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.boundingBox.copy().offset(0.0, mc.thePlayer.motionY, 0.0));
                List list2 = mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.boundingBox.copy().offset(0.0, -0.4, 0.0));

                if (!mc.thePlayer.isCollidedVertically && (list1.size() > 0 || list2.size() > 0)) {
                    mc.thePlayer.motionY = -1.0E-4;
                    event.y = mc.thePlayer.motionY;
                }
            }

            moveSpeed = Math.max(moveSpeed, MoveUtils.getBaseNcpSpeed());

            double[] strafing = MoveUtils.calcStrafe(moveSpeed);

            event.x = strafing[0];
            event.z = strafing[1];
        }
    }

    @EventListener
    public void onMotionUpdate(EventMotionUpdate event) {

        if (event.getEra().equals(Era.POST)) {
            double diffX = mc.thePlayer.posX - mc.thePlayer.prevPosX;
            double diffZ = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;

            distance = Math.sqrt(diffX * diffX + diffZ * diffZ);
        }
    }

    @EventListener
    public void onPacket(EventPacket event) {
        if (event.getPacket() instanceof S08PacketPlayerPosLook) {
            setRunning(false);
        }
    }

    public enum Mode {
        NCP, VANILLA
    }

    public enum Damage {
        NONE, PACKET, FAKE, BOW
    }
}

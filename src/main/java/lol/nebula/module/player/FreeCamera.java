package lol.nebula.module.player;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.entity.EventEntityTurn;
import lol.nebula.listener.events.entity.EventUpdate;
import lol.nebula.listener.events.entity.move.EventMove;
import lol.nebula.listener.events.input.EventUpdateMoveState;
import lol.nebula.listener.events.net.EventPacket;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.setting.Setting;
import lol.nebula.util.player.MoveUtils;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.MovementInputFromOptions;

import static lol.nebula.util.player.MoveUtils.isMoving;

/**
 * @author aesthetical
 * @since 06/03/23
 */
public class FreeCamera extends Module {
    public static final int CAMERA_ENTITY_ID = 133769420;

    private static final Setting<Mode> mode = new Setting<>(Mode.INTERACT, "Mode");
    private static final Setting<Double> speed = new Setting<>(1.0, 0.01, 0.1, 5.0, "Speed");

    private CameraGuy cameraGuy;
    private Mode previousMode;

    private double x = -1.0, y = -1.0, z = -1.0;

    public FreeCamera() {
        super("Free Camera", "off the za", ModuleCategory.PLAYER);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        previousMode = mode.getValue();
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if (mc.thePlayer != null && mc.theWorld != null) {
            if (mode.getValue() == Mode.FAKE) {
                mc.thePlayer.noClip = false;
                mc.thePlayer.setPosition(x, y, z);
            }

            if (cameraGuy != null) {

                if (mode.getValue() == Mode.INTERACT) {
                    mc.renderViewEntity = mc.thePlayer;
                }

                mc.theWorld.removeEntityFromWorld(CAMERA_ENTITY_ID);
                mc.theWorld.removePlayerEntityDangerously(cameraGuy);

                cameraGuy = null;
            }
        }

        x = -1.0;
        y = -1.0;
        z = -1.0;
    }

    @Listener
    public void onUpdate(EventUpdate event) {
        mc.gameSettings.thirdPersonView = 0;

        if (previousMode != mode.getValue()) {
            print("dont change the mode retard");
            mode.setValue(previousMode);
        }

        if (mc.thePlayer.isDead || mc.thePlayer.getHealth() <= 0.0f) {
            if (mode.getValue() == Mode.INTERACT) {
                mc.renderViewEntity = mc.thePlayer;
            }

            mc.theWorld.removeEntityFromWorld(CAMERA_ENTITY_ID);
            mc.theWorld.removePlayerEntityDangerously(cameraGuy);

            cameraGuy = null;
        } else if (cameraGuy == null && mc.thePlayer.ticksExisted > 5) {

            if (mode.getValue() == Mode.FAKE && (x == -1.0 && y == -1.0 && z == -1.0)) {
                x = mc.thePlayer.posX;
                y = mc.thePlayer.posY;
                z = mc.thePlayer.posZ;
            }

            cameraGuy = new CameraGuy();
            mc.theWorld.addEntityToWorld(CAMERA_ENTITY_ID, cameraGuy);

            if (mode.getValue() == Mode.INTERACT) {
                mc.renderViewEntity = cameraGuy;
            }
        }

    }

    @Listener
    public void onMove(EventMove event) {
        if (mode.getValue() != Mode.FAKE || cameraGuy == null) return;

        mc.thePlayer.noClip = true;

        MoveUtils.strafe(event, isMoving() ? speed.getValue() : 0.0);

        if (mc.gameSettings.keyBindJump.pressed) {
            mc.thePlayer.motionY = speed.getValue();
        } else if (mc.gameSettings.keyBindSneak.pressed) {
            mc.thePlayer.motionY = -speed.getValue();
        } else {
            mc.thePlayer.motionY = 0.0;
        }

        event.setY(mc.thePlayer.motionY);
    }

    @Listener
    public void onUpdateMoveState(EventUpdateMoveState event) {
        if (mode.getValue() == Mode.INTERACT && event.getInput() instanceof MovementInputFromOptions) {
            event.getInput().moveForward = 0.0f;
            event.getInput().moveStrafe = 0.0f;
            event.getInput().jump = false;
            event.getInput().sneak = false;

            if (cameraGuy != null) event.cancel();
        }
    }

    @Listener
    public void onEntityTurn(EventEntityTurn event) {
        if (mode.getValue() == Mode.INTERACT
                && cameraGuy != null
                && event.getEntity().equals(mc.thePlayer)) {

            event.cancel();
            cameraGuy.setAngles(event.getYaw(), event.getPitch());
        }
    }

    @Listener
    public void onPacketOutbound(EventPacket.Outbound event) {
        if (mc.thePlayer == null || mc.theWorld == null) return;

        if (event.getPacket() instanceof C03PacketPlayer) {
            if (mode.getValue() != Mode.FAKE) return;
            event.cancel();
        } else if (event.getPacket() instanceof C02PacketUseEntity) {
            C02PacketUseEntity packet = event.getPacket();
            if (mc.thePlayer.equals(packet.getEntity(mc.theWorld))) event.cancel();
        }
    }

    public enum Mode {
        INTERACT, FAKE
    }

    private static class CameraGuy extends EntityOtherPlayerMP {

        public CameraGuy() {
            super(mc.theWorld, mc.thePlayer.getGameProfile());

            setLocationAndAngles(
                    mc.thePlayer.posX, mc.thePlayer.boundingBox.minY, mc.thePlayer.posZ,
                    mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
            setEntityId(CAMERA_ENTITY_ID);

            capabilities.allowFlying = true;
            capabilities.isFlying = true;
        }

        @Override
        public void onLivingUpdate() {
            super.onLivingUpdate();

            if (mode.getValue() != Mode.INTERACT) return;

            updateEntityActionState();

            noClip = true;

            if (mc.gameSettings.keyBindForward.pressed) {
                moveForward = 1.0f;
            } else if (mc.gameSettings.keyBindBack.pressed) {
                moveForward = -1.0f;
            } else {
                moveForward = 0.0f;
            }

            if (mc.gameSettings.keyBindRight.pressed) {
                moveStrafing = -1.0f;
            } else if (mc.gameSettings.keyBindLeft.pressed) {
                moveStrafing = 1.0f;
            } else {
                moveStrafing = 0.0f;
            }

            double x, z;
            double y = 0.0;

            if (moveForward != 0.0f || moveStrafing != 0.0f) {

                float rad = MoveUtils.getDirectionYaw(this, rotationYaw);
                x = -Math.sin(rad) * speed.getValue();
                z = Math.cos(rad) * speed.getValue();
            } else {
                x = 0.0;
                z = 0.0;
            }

            if (mc.gameSettings.keyBindJump.pressed) {
                y = speed.getValue();
            } else if (mc.gameSettings.keyBindSneak.pressed) {
                y = -speed.getValue();
            }

            moveEntity(x, y, z);
        }

        @Override
        public boolean isInvisible() {
            return mode.getValue() == Mode.INTERACT;
        }

        @Override
        public boolean isInvisibleToPlayer(EntityPlayer par1EntityPlayer) {
            return isInvisible();
        }
    }
}

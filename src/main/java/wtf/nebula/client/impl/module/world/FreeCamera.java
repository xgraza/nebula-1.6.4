package wtf.nebula.client.impl.module.world;

import com.mojang.authlib.GameProfile;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.world.World;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.impl.event.impl.client.EventTick;
import wtf.nebula.client.impl.event.impl.input.EventInputUpdate;
import wtf.nebula.client.impl.event.impl.network.EventPacket;
import wtf.nebula.client.impl.event.impl.player.EventPlayerTurn;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;

public class FreeCamera extends ToggleableModule {
    private final Property<Double> speed = new Property<>(0.5, 0.1, 5.0, "Speed");
    private final Property<Boolean> cancelPackets = new Property<>(false, "Cancel Packets", "cancelpackets");

    private CameraGuy cameraGuy;

    public FreeCamera() {
        super("Free Camera", new String[]{"freecamera", "freecam"}, ModuleCategory.WORLD);
        offerProperties(speed, cancelPackets);
    }

    @Override
    protected void onEnable() {
        super.onEnable();

        if (!isNull()) {
            createCameraGuy();
        }
    }

    @Override
    protected void onDisable() {
        super.onDisable();

        mc.renderViewEntity = mc.thePlayer;
        mc.theWorld.removeEntity(cameraGuy);
        mc.theWorld.removePlayerEntityDangerously(cameraGuy);
    }

    @EventListener
    public void onInputUpdate(EventInputUpdate event) {
        if (event.getInput() instanceof MovementInputFromOptions) {
            event.getInput().moveForward = 0.0f;
            event.getInput().moveStrafe = 0.0f;
            event.getInput().jump = false;
            event.getInput().sneak = false;

            event.setCancelled(cameraGuy != null);
        }
    }

    @EventListener
    public void onPlayerTurn(EventPlayerTurn event) {
        if (event.getEntity().equals(mc.thePlayer) && cameraGuy != null) {
            event.setCancelled(true);
            cameraGuy.setAngles(event.getYaw(), event.getPitch());
        }
    }

    @EventListener
    public void onPacket(EventPacket event) {
        if (event.getPacket() instanceof C02PacketUseEntity) {
            C02PacketUseEntity packet = event.getPacket();
            if (packet.entityId == mc.thePlayer.getEntityId()) {
                event.setCancelled(true);
            }
        } else if (event.getPacket() instanceof C03PacketPlayer) {
            if (cancelPackets.getValue()) {
                event.setCancelled(true);
            }
        }
    }

    @EventListener
    public void onTick(EventTick event) {
        if (mc.thePlayer.isDead || mc.thePlayer.getHealth() <= 0.0f) {

            mc.renderViewEntity = mc.thePlayer;

            if (cameraGuy != null) {
                mc.theWorld.removeEntity(cameraGuy);
                mc.theWorld.removePlayerEntityDangerously(cameraGuy);
            }
        } else if (cameraGuy == null && mc.thePlayer.ticksExisted > 5) {
            createCameraGuy();
        }
    }

    private void createCameraGuy() {
        cameraGuy = new CameraGuy(mc.theWorld, mc.thePlayer.getGameProfile());
        cameraGuy.copyLocationAndAnglesFrom(mc.thePlayer);
        cameraGuy.inventory.copyInventory(mc.thePlayer.inventory);

        mc.theWorld.addEntityToWorld(-133769420, cameraGuy);
        mc.renderViewEntity = cameraGuy;
    }

    private class CameraGuy extends EntityOtherPlayerMP {

        public CameraGuy(World p_i45075_1_, GameProfile p_i45075_2_) {
            super(p_i45075_1_, p_i45075_2_);

            capabilities.allowFlying = true;
            capabilities.isFlying = true;
        }

        @Override
        public void onLivingUpdate() {
            super.onLivingUpdate();

            inventory.copyInventory(mc.thePlayer.inventory);
            updateEntityActionState();

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

            motionY = 0.0;

            if (!isMoving()) {
                motionX = 0.0;
                motionZ = 0.0;
            } else {
                float yaw = getMovementYaw(rotationYaw);

                motionX = -Math.sin(yaw) * speed.getValue();
                motionZ = Math.cos(yaw) * speed.getValue();
            }

            if (mc.gameSettings.keyBindJump.pressed) {
                motionY = speed.getValue();
            } else if (mc.gameSettings.keyBindSneak.pressed) {
                motionY = -speed.getValue();
            }

            moveEntity(motionX, motionY, motionZ);
        }

        private boolean isMoving() {
            return moveForward != 0.0f || moveStrafing != 0.0f;
        }

        private float getMovementYaw(float yaw) {
            float rotationYaw = yaw;
            float n = 1.0f;

            if (moveForward < 0.0f) {
                rotationYaw += 180.0f;
                n = -0.5f;
            } else if (moveForward > 0.0f) {
                n = 0.5f;
            }

            if (moveStrafing > 0.0f) {
                rotationYaw -= 90.0f * n;
            }

            if (moveStrafing < 0.0f) {
                rotationYaw += 90.0f * n;
            }

            return rotationYaw * 0.017453292f;
        }
    }
}

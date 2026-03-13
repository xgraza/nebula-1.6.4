package us.nebula.client.impl.cheat.player;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.world.World;
import us.nebula.client.api.listener.EventListener;
import us.nebula.client.api.listener.Subscribe;
import us.nebula.client.api.manager.cheat.Cheat;
import us.nebula.client.api.manager.cheat.CheatCategory;
import us.nebula.client.api.manager.cheat.CheatManifest;
import us.nebula.client.api.value.Setting;
import us.nebula.client.impl.event.game.EventUpdate;
import us.nebula.client.impl.event.input.EventRotateCamera;
import us.nebula.client.impl.event.input.EventUpdateInput;
import us.nebula.client.impl.event.network.EventPacket;
import us.nebula.client.impl.event.player.EventRaytrace;
import us.nebula.client.impl.event.player.EventSneakSlowdown;
import us.nebula.client.util.player.MoveUtil;

/**
 * @author xgraza
 * @since 03/24/25
 */
@CheatManifest(name = "Freecam",
        description = "Allows you to view the world from a free flying perspective",
        category = CheatCategory.PLAYER)
public final class FreecamCheat extends Cheat
{
    public static final int CAMERA_ENTITY_ID = 1337420;

    private static final Setting<Double> SPEED_SETTING = new Setting<>(
            "Speed", 1.0, 0.1, 7.0, 0.05);
    private final Setting<Boolean> interactSetting = new Setting<>(
            "Interact", true);

    private CameraPlayerEntity playerEntity;

    @Override
    protected void onDisable()
    {
        super.onDisable();
        if (playerEntity != null)
        {
            MC.theWorld.removeEntityFromWorld(CAMERA_ENTITY_ID);
        }
        if (MC.theWorld != null)
        {
            MC.renderViewEntity = MC.thePlayer;
        }
        playerEntity = null;
    }

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        if (MC.thePlayer.isDead || MC.thePlayer.getHealth() <= 0.0f)
        {
            MC.renderViewEntity = MC.thePlayer;
            MC.theWorld.removeEntityFromWorld(CAMERA_ENTITY_ID);
            playerEntity = null;
            return;
        }

        if (playerEntity == null)
        {
            playerEntity = new CameraPlayerEntity(MC.theWorld, MC.thePlayer);
            MC.theWorld.addEntityToWorld(CAMERA_ENTITY_ID, playerEntity);
        }

        if (MC.renderViewEntity != null && !MC.renderViewEntity.equals(playerEntity))
        {
            MC.renderViewEntity = playerEntity;
        }
    };

    @Subscribe
    private final EventListener<EventRaytrace> raytraceEventListener = event ->
    {
        if (event.getEntity().equals(playerEntity))
        {
            event.cancel();
            if (!interactSetting.getValue())
            {
                event.setResult(null);
            }
        }
    };

    @Subscribe
    private final EventListener<EventUpdateInput> updateInputEventListener = event ->
    {
        if (playerEntity != null)
        {
            event.cancel();
        }
    };

    @Subscribe
    private final EventListener<EventRotateCamera> rotateCameraEventListener = event ->
    {
        if (playerEntity != null && event.getEntity().equals(MC.thePlayer))
        {
            event.cancel();
            playerEntity.setAngles(event.getDiffYaw(), event.getDiffPitch());
            playerEntity.renderPitch = playerEntity.rotationPitch;
            playerEntity.rotationYawHead = playerEntity.rotationYaw;
        }
    };

    @Subscribe
    private final EventListener<EventSneakSlowdown> sneakSlowdownEventListener = event ->
    {
        if (playerEntity != null && event.getInput().equals(playerEntity.getInput()))
        {
            event.cancel();
        }
    };

    @Subscribe
    private final EventListener<EventPacket.Outbound> outboundEventListener = event ->
    {
        if (playerEntity == null || MC.thePlayer == null)
        {
            return;
        }
        if (event.getPacket() instanceof C02PacketUseEntity)
        {
            final C02PacketUseEntity packet = event.getPacket();
            final Entity entity = packet.func_149564_a(MC.theWorld);
            if (MC.thePlayer.equals(entity) || playerEntity.equals(entity))
            {
                event.setCanceled(true);
            }
        }
    };

    private static GameProfile getPlayerProfile()
    {
        return new GameProfile(MC.thePlayer.getGameProfile().getId(), "CameraEntity");
    }

    private static final class CameraPlayerEntity extends EntityOtherPlayerMP
    {
        private final MovementInputFromOptions input;

        public CameraPlayerEntity(final World world, final EntityPlayer player)
        {
            super(world, getPlayerProfile());
            yOffset = 1.62f;
            setSize(0.6F, 1.8F);
            setEntityId(CAMERA_ENTITY_ID);
            setInvisible(true);
            setLocationAndAngles(player.posX, player.boundingBox.minY, player.posZ, player.rotationYaw, player.rotationPitch);
            inventory.copyInventory(player.inventory);
            input = new MovementInputFromOptions(MC.gameSettings);
        }

        @Override
        public void onUpdate()
        {
            super.onUpdate();
            input.updatePlayerMoveState();
            moveForward = input.moveForward;
            moveStrafing = input.moveStrafe;

            if (input.jump)
            {
                motionY = SPEED_SETTING.getValue();
            } else if (input.sneak)
            {
                motionY = -SPEED_SETTING.getValue();
            } else
            {
                motionY = 0.0;
            }

            if (moveForward != 0.0f || moveStrafing != 0.0f)
            {
                final double[] motion = MoveUtil.getStrafeMotion(
                        MoveUtil.getDirectionRadians(this, rotationYaw),
                        SPEED_SETTING.getValue());
                motionX = motion[0];
                motionZ = motion[1];
            } else
            {
                motionX = 0.0;
                motionZ = 0.0;
            }

            moveEntity(motionX, motionY, motionZ);
        }

        public MovementInputFromOptions getInput()
        {
            return input;
        }
    }
}

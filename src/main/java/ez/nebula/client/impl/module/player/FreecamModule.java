package ez.nebula.client.impl.module.player;

import com.mojang.authlib.GameProfile;
import ez.nebula.client.api.manager.module.Module;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.world.World;
import ez.nebula.client.api.manager.module.trait.ModuleInstance;
import ez.nebula.client.api.listener.Event;
import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.setting.Setting;
import ez.nebula.client.api.listener.event.game.EventUpdate;
import ez.nebula.client.api.listener.event.input.EventRotateCamera;
import ez.nebula.client.api.listener.event.input.EventUpdateInput;
import ez.nebula.client.api.listener.event.network.EventPacket;
import ez.nebula.client.api.listener.event.player.EventRaytrace;
import ez.nebula.client.api.listener.event.render.EventRenderWaterEffects;
import ez.nebula.client.util.minecraft.player.MoveUtil;

/**
 * @author xgraza
 * @since 03/24/25
 */
@ModuleManifest(name = "Freecam",
        description = "Allows you to view the world from a free flying perspective",
        category = ModuleCategory.PLAYER)
public final class FreecamModule extends Module
{
    @ModuleInstance
    public static FreecamModule INSTANCE;
    public static final int CAMERA_ENTITY_ID = 1337420;

    private final Setting<Double> speedSetting = numberBuilder("Speed", 1.0)
            .setMin(0.1)
            .setMax(7.0)
            .setScale(0.05)
            .setDescription("The speed to travel the camera guy at")
            .build();
    private final Setting<Boolean> interactSetting = builder("Interact", true)
            .setDescription("If to allow world interactions (i.e. block place, block break)")
            .build();

    private CameraPlayerEntity playerEntity;

    @Override
    public void onDisable()
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
        if (MC.thePlayer.isDead
                || MC.thePlayer.getHealth() <= 0.0f
                || (playerEntity != null && playerEntity.dimension != MC.thePlayer.dimension))
        {
            MC.renderViewEntity = MC.thePlayer;
            MC.theWorld.removeEntityFromWorld(CAMERA_ENTITY_ID);
            playerEntity = null;
            return;
        }

        if (!MC.getNetHandler().doneLoadingTerrain)
        {
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
        if (playerEntity != null && event.getInput().equals(MC.thePlayer.movementInput))
        {
            event.cancel();
        }
    };

    @Subscribe
    private final EventListener<EventUpdateInput.Post> postUpdateInputEventListener = event ->
    {
        if (playerEntity != null && event.getInput().equals(playerEntity.getInput()))
        {
            event.setModifySneaking(false);
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

    @Subscribe
    private final EventListener<EventRenderWaterEffects> renderWaterEffectsEventListener = Event::cancel;

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
            //setSize(0.6F, 1.8F);
            setEntityId(CAMERA_ENTITY_ID);
            setInvisible(true);
            setLocationAndAngles(player.posX, player.boundingBox.minY, player.posZ, player.rotationYaw, player.rotationPitch);
            inventory.copyInventory(player.inventory);
            input = new MovementInputFromOptions(MC.gameSettings);
        }

        @Override
        public void onLivingUpdate()
        {
            super.onLivingUpdate();
            input.updatePlayerMoveState();
            updateEntityActionState();
            noClip = true;
            moveForward = input.moveForward;
            moveStrafing = input.moveStrafe;

            if (input.jump)
            {
                motionY = INSTANCE.speedSetting.getValue();
            } else if (input.sneak)
            {
                motionY = -INSTANCE.speedSetting.getValue();
            } else
            {
                motionY = 0.0;
            }

            if (moveForward != 0.0f || moveStrafing != 0.0f)
            {
                final double[] motion = MoveUtil.getStrafeMotion(
                        MoveUtil.getDirectionRadians(this, rotationYaw),
                        INSTANCE.speedSetting.getValue());
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

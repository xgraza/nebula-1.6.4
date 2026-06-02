package ez.nebula.client.impl.module.movement;

import ez.nebula.client.api.manager.module.Module;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.AxisAlignedBB;
import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.listener.event.network.EventPacket;
import ez.nebula.client.api.listener.event.player.EventMove;
import ez.nebula.client.api.listener.event.player.EventMoveUpdate;
import ez.nebula.client.api.setting.Setting;
import ez.nebula.client.util.minecraft.player.MoveUtil;

/**
 * @author xgraza
 * @since 06/27/25
 */
@ModuleManifest(name = "LongJump",
        description = "Allows you to jump a much further distance forward than normal",
        category = ModuleCategory.MOVEMENT)
public final class LongJumpModule extends Module
{
    private final Setting<Double> boostSetting = numberBuilder("Boost", 4.5)
            .setMin(1.0)
            .setMax(6.0)
            .setScale(0.1)
            .setDescription("The base speed to use for the long jump")
            .build();
    private final Setting<Boolean> glideSetting = builder("NCP Glide", false)
            .setDescription("If to use a glide that is compatible with the NCP AntiCheat")
            .build();
    private final Setting<Boolean> autoDisableSetting = builder("Auto Disable", true)
            .setDescription("If to automatically disable once an AntiCheat lagback occurs")
            .build();

    private double moveSpeed, distance;
    private int stage;

    @Override
    public void onDisable()
    {
        super.onDisable();
        moveSpeed = 0.0;
        distance = 0.0;
        stage = 1;
    }

    @Subscribe
    private final EventListener<EventMove> moveEventListener = event ->
    {
        if (MoveUtil.isMoving())
        {
            switch (stage)
            {
                case 1:
                {
                    moveSpeed = boostSetting.getValue() * MoveUtil.NCP_BASE_SPEED - 0.01;
                    stage = 2;
                    break;
                }
                case 2:
                {
                    if (MC.thePlayer.onGround)
                    {
                        MC.thePlayer.motionY = MoveUtil.getJumpHeight(0.3995);
                        event.setY(MC.thePlayer.motionY);
                    }
                    moveSpeed *= 2.149;
                    stage = 3;
                    break;
                }
                case 3:
                {
                    moveSpeed = distance - (0.66 * (distance - MoveUtil.NCP_BASE_SPEED));
                    stage = 4;
                    break;
                }
                default:
                {
                    moveSpeed = moveSpeed - moveSpeed / 159.0;
                    break;
                }
            }
        }
        if (glideSetting.getValue() && MC.thePlayer.motionY < 0.06416928114945335)
        {
            // amazing Doogie13 code
            final AxisAlignedBB bb = MC.thePlayer.boundingBox;
            if (MC.theWorld.checkBlockCollision(bb.copy().offset(0, -0.4, 0))
                    && MC.theWorld.checkBlockCollision(bb.copy().offset(0, event.getY(), 0)))
            {
                event.setY(MC.thePlayer.motionY = -0.001);
            }
        }
        MoveUtil.setSpeed(event, moveSpeed);
    };

    @Subscribe
    private final EventListener<EventMoveUpdate> moveUpdateEventListener = event ->
    {
        final double deltaX = MC.thePlayer.posX - MC.thePlayer.prevPosX;
        final double deltaZ = MC.thePlayer.posZ - MC.thePlayer.prevPosZ;
        distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
    };

    @Subscribe
    private final EventListener<EventPacket.Inbound> inboundEventListener = event ->
    {
        if (event.getPacket() instanceof S08PacketPlayerPosLook && autoDisableSetting.getValue())
        {
            notifyWarn("Disabled LongJump due to a server/anti-cheat lagback.", 5000L);
            setToggled(false);
        }
    };
}

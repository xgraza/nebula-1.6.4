package us.nebula.impl.cheat.movement;

import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.AxisAlignedBB;
import sun.util.resources.cldr.uk.CalendarData_uk_UA;
import us.nebula.Nebula;
import us.nebula.api.listener.EventListener;
import us.nebula.api.listener.Subscribe;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.api.value.Setting;
import us.nebula.impl.event.network.EventPacket;
import us.nebula.impl.event.player.EventMove;
import us.nebula.impl.event.player.EventMoveUpdate;
import us.nebula.util.player.ChatUtil;
import us.nebula.util.player.MoveUtil;

import java.util.List;

/**
 * @author xgraza
 * @since 06/27/25
 */
@CheatManifest(name = "LongJump",
        description = "Jumps long... what else?",
        category = CheatCategory.MOVEMENT)
public final class LongJumpCheat extends Cheat
{
    private final Setting<Double> boostSetting = new Setting<>(
            "Boost", 4.5, 1.0, 6.0, 0.1);
    private final Setting<Boolean> glideSetting = new Setting<>(
            "NCP-Glide", false);
    private final Setting<Boolean> autoDisableSetting = new Setting<>(
            "Auto Disable", true);

    private double moveSpeed, distance;
    private int stage;

    @Override
    protected void onDisable()
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
            Nebula.INSTANCE.getToastManager().warn(
                    "LongJump",
                    "Disabled LongJump due to a server/anti-cheat lagback.",
                    7500L);
            setToggled(false);
        }
    };
}

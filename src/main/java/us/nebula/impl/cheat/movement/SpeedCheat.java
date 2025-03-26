package us.nebula.impl.cheat.movement;

import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import us.nebula.api.listener.EventListener;
import us.nebula.api.listener.Subscribe;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.api.value.Setting;
import us.nebula.impl.event.network.EventPacket;
import us.nebula.impl.event.player.EventMove;
import us.nebula.impl.event.player.EventMoveUpdate;
import us.nebula.util.player.MoveUtil;

/**
 * @author xgraza
 * @since 03/26/25
 */
@CheatManifest(name = "Speed",
        description = "Allows you to move faster",
        category = CheatCategory.MOVEMENT)
public final class SpeedCheat extends Cheat
{
    private final Setting<Mode> modeSetting = new Setting<>(
            "Mode", Mode.STRAFE);

    private double lastDistance, speed;
    private int lagTicks, stage;
    private boolean boost;

    @Override
    protected void onDisable()
    {
        super.onDisable();
        lastDistance = 0.0;
        speed = 0.0;
        lagTicks = 0;
        boost = false;
        MC.timer.timerSpeed = 1.0f;
    }

    @Subscribe
    private final EventListener<EventMove> moveEventListener = event ->
    {
        if (--lagTicks > 0)
        {
            return;
        }

        if (modeSetting.getValue() == Mode.STRAFE)
        {
            if (MoveUtil.isMoving() && MC.thePlayer.onGround)
            {
                stage = 0;
            }

            switch (stage)
            {
                case 0:
                case 1:
                {
                    if (stage == 0)
                    {
                        MC.timer.timerSpeed = 1.0f;
                        speed = 1.2945 * MoveUtil.getBaseNcpSpeed(20) - 0.01;
                        stage = 1;
                    }
                    if (MoveUtil.isMoving() && MC.thePlayer.onGround)
                    {
                        MC.thePlayer.motionY = MoveUtil.getJumpHeight(0.4f);
                        event.setY(MC.thePlayer.motionY);
                        speed *= boost ? 1.655 : 1.48;
                        MC.timer.timerSpeed = boost ? 1.044f : 1.088f;
                        stage = 2;
                    }
                    break;
                }
                case 2:
                {
                    final double mod = boost ? 0.76 : 0.66;
                    final double diff = mod * (speed - MoveUtil.getBaseNcpSpeed(20));
                    speed = lastDistance - diff;
                    stage = 3;
                    boost = !boost;
                    break;
                }
                case 3:
                {
                    MC.timer.timerSpeed = 1.0f;
                    if (!MoveUtil.isMoving() && MC.thePlayer.onGround)
                    {
                        stage = 0;
                    }
                    speed = speed - speed / 159.0;
                    break;
                }
            }

            speed = Math.max(speed, MoveUtil.getBaseNcpSpeed(20));

            if (MoveUtil.isMoving())
            {
                MoveUtil.setSpeed(event, speed);
            }
        }
    };

    @Subscribe
    private final EventListener<EventMoveUpdate> moveUpdateEventListener = event ->
    {
        final double diffX = MC.thePlayer.posX - MC.thePlayer.lastTickPosX;
        final double diffZ = MC.thePlayer.posZ - MC.thePlayer.lastTickPosZ;
        lastDistance = Math.sqrt(diffX * diffX + diffZ * diffZ);
    };

    @Subscribe
    private final EventListener<EventPacket.Inbound> inboundEventListener = event ->
    {
        if (event.getPacket() instanceof S08PacketPlayerPosLook)
        {
            lagTicks = 8;
        }
    };

    private enum Mode
    {
        STRAFE
    }
}

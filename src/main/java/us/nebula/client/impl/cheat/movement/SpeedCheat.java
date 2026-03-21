package us.nebula.client.impl.cheat.movement;

import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import us.nebula.client.api.listener.EventListener;
import us.nebula.client.api.listener.Subscribe;
import us.nebula.client.api.manager.cheat.Cheat;
import us.nebula.client.api.manager.cheat.CheatCategory;
import us.nebula.client.api.manager.cheat.CheatInstance;
import us.nebula.client.api.manager.cheat.CheatManifest;
import us.nebula.client.api.value.Setting;
import us.nebula.client.impl.cheat.world.ScaffoldCheat;
import us.nebula.client.impl.event.network.EventPacket;
import us.nebula.client.impl.event.player.EventMove;
import us.nebula.client.impl.event.player.EventMoveUpdate;
import us.nebula.client.impl.gui.client.component.cheat.value.EnumSettingComponent;
import us.nebula.client.util.player.MoveUtil;

/**
 * @author xgraza
 * @since 03/26/25
 */
@CheatManifest(name = "Speed",
        description = "Allows you to move faster",
        category = CheatCategory.MOVEMENT)
public final class SpeedCheat extends Cheat
{
    @CheatInstance
    public static SpeedCheat INSTANCE;

    public final Setting<Mode> modeSetting = new Setting<>(
            "Mode", Mode.STRAFE);
    public final Setting<Boolean> timerSetting = new Setting<>(
            "Use Timer", false)
            .setVisibility(() -> modeSetting.getValue() == Mode.STRAFE);
    public final Setting<Integer> advanceSetting = new Setting<>(
            "Advance", 1, 1, 10, 1)
            .setVisibility(() -> modeSetting.getValue() == Mode.TICK_ADVANCE);

    private double lastDistance, speed;
    private int lagTicks, stage;

    @Override
    protected void onDisable()
    {
        super.onDisable();
        lastDistance = 0.0;
        speed = 0.0;
        lagTicks = 0;
        MC.timer.timerSpeed = 1.0f;
    }

    @Subscribe
    private final EventListener<EventMove> moveEventListener = event ->
    {
        if (--lagTicks > 0)
        {
            MC.timer.timerSpeed = 1.0f;
            return;
        }

        if (modeSetting.getValue() == Mode.STRAFE)
        {
            final boolean useTimer = timerSetting.getValue() && !ScaffoldCheat.INSTANCE.isToggled();

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
                        speed = 1.22 * MoveUtil.getBaseNcpSpeed(20);
                        stage = 1;
                    }
                    if (MoveUtil.isMoving() && MC.thePlayer.onGround)
                    {
                        MC.thePlayer.motionY = MoveUtil.getJumpHeight(0.3995f);
                        event.setY(MC.thePlayer.motionY);
                        speed *= 1.59;
                        stage = 2;
                    }
                    if (useTimer)
                    {
                        MC.timer.timerSpeed = 1.088f;
                    }
                    break;
                }
                case 2:
                {
                    final double diff = 0.7 * (speed - MoveUtil.getBaseNcpSpeed(20));
                    speed = lastDistance - diff;
                    stage = 3;
                    break;
                }
                case 3:
                {
                    if (!MoveUtil.isMoving() && MC.thePlayer.onGround)
                    {
                        stage = 0;
                    }
                    speed = speed - speed / 139.0;
                    break;
                }
            }

            speed = Math.max(speed, MoveUtil.getBaseNcpSpeed(20));

            if (MoveUtil.isMoving())
            {
                MoveUtil.setSpeed(event, speed);
            } else
            {
                MC.timer.timerSpeed = 1.0f;
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
            MC.timer.timerSpeed = 1.0f;
            lagTicks = 8;
        }
    };

    @Override
    public String getMetadata()
    {
        return EnumSettingComponent.formatEnum(modeSetting.getValue());
    }

    @Override
    public boolean isActive()
    {
        return isToggled() && MoveUtil.isMoving();
    }

    public enum Mode
    {
        STRAFE, TICK_ADVANCE
    }
}

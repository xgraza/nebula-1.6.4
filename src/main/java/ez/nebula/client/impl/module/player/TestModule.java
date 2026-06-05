package ez.nebula.client.impl.module.player;

import ez.nebula.client.api.DebugFeature;
import ez.nebula.client.api.listener.event.player.EventMove;
import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.util.minecraft.player.MoveUtil;
import net.minecraft.src.BlockPos;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import org.lwjgl.input.Keyboard;
import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.listener.event.input.EventKey;
import ez.nebula.client.api.listener.event.render.EventRender3D;
import ez.nebula.client.util.minecraft.player.ChatUtil;
import ez.nebula.client.util.minecraft.player.PlayerUtil;
import ez.nebula.client.util.render.RenderUtil;

@DebugFeature
@ModuleManifest(name = "Test", category = ModuleCategory.PLAYER)
public final class TestModule extends Module
{
    private boolean attemptExit, doJump;
    private int inWaterTicks = 0;

    @Subscribe
    private final EventListener<EventMove> moveEventListener = event ->
    {
        if (MC.thePlayer.isInWater())
        {
            if (++inWaterTicks < 3)
            {
                ChatUtil.sendNebula("Water ticks: %s", inWaterTicks);
                return;
            }
            attemptExit = true;
            MC.thePlayer.motionY = 0.11f;
            event.setY(MC.thePlayer.motionY);
            doJump = false;
        } else
        {
            inWaterTicks = 0;
            if (attemptExit)
            {
                MC.thePlayer.motionY = 0.3;
                event.setY(MC.thePlayer.motionY);
                attemptExit = false;
            } else
            {
                MC.thePlayer.onGround = true;
                if (!doJump)
                {
                    doJump = true;
                    //MC.thePlayer.motionY = 0.3f;
                } else
                {
                    if (MoveUtil.isMoving())
                    {
                        //MoveUtil.setSpeed(event, 0.2);
                    }
                }
            }
        }
    };
}

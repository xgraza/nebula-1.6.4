package ez.nebula.client.impl.module.movement;

import ez.nebula.client.api.DebugFeature;
import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.listener.event.player.EventMove;
import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.util.minecraft.player.MoveUtil;

@DebugFeature
@ModuleManifest(name = "FastSwim", description = "", category = ModuleCategory.MOVEMENT)
public final class FastSwimModule extends Module
{
    private boolean bl;
    private int t;

    @Override
    public void onDisable()
    {
        super.onDisable();
        bl = false;
        t = 0;
    }

    @Subscribe
    private final EventListener<EventMove> moveEventListener = event ->
    {
        if (!MC.thePlayer.isInWater())
        {
            return;
        }

        ++t;

        double speed = 0.3;

        if (t == 4)
        {
            speed = 0.4;
        }

        if (t < 5)
        {
            return;
        }

        t = 0;

        if (MoveUtil.isMoving())
        {
            MoveUtil.setSpeed(event, speed);
        }
    };
}

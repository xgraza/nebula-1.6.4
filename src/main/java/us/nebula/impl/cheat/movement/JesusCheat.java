package us.nebula.impl.cheat.movement;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.util.AxisAlignedBB;
import us.nebula.api.listener.EventListener;
import us.nebula.api.listener.Subscribe;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatInstance;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.impl.event.game.EventUpdate;
import us.nebula.impl.event.player.EventMoveUpdate;
import us.nebula.impl.event.world.EventModifyBoundBox;

/**
 * @author xgraza
 * @since 03/01/25
 */
@CheatManifest(name = "Jesus",
        description = "Walks on water or lava",
        category = CheatCategory.MOVEMENT)
public final class JesusCheat extends Cheat
{
    @CheatInstance
    public static JesusCheat INSTANCE;

    private static final AxisAlignedBB LIQUID_FULL_AABB = new AxisAlignedBB(
            0, 0, 0, 1, 0.99, 1);

    private boolean attemptExit, lastTickSpoof;

    @Override
    protected void onDisable()
    {
        super.onDisable();
        attemptExit = false;
        lastTickSpoof = false;
    }

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        if (MC.thePlayer.fallDistance > 3.0f)
        {
            return;
        }

        if (MC.thePlayer.isInWater())
        {
            MC.thePlayer.motionY = 0.11;
            attemptExit = true;
        } else
        {
            if (attemptExit)
            {
                MC.thePlayer.motionY = 0.3;
                attemptExit = false;
            }
        }
    };

    @Subscribe
    private final EventListener<EventMoveUpdate> moveUpdateEventListener = event ->
    {
        if (!isAboveWater() || MC.thePlayer.isInWater())
        {
            lastTickSpoof = false;
            return;
        }

        if (MC.thePlayer.ticksExisted % 2 == 0
                && MC.thePlayer.groundTicks >= 2
                && !MC.gameSettings.keyBindJump.pressed)
        {
            lastTickSpoof = true;
            event.setY(event.getY() + 0.01);
            event.setStance(event.getStance() + 0.01);
            event.setOnGround(false);
            return;
        }
        lastTickSpoof = false;
    };

    @Subscribe
    private final EventListener<EventModifyBoundBox> modifyBoundBoxEventListener = event ->
    {
        if (MC.thePlayer == null
                || MC.thePlayer.fallDistance > 3.0f
                || MC.thePlayer.isInWater()
                || !isAboveWater()
                || attemptExit)
        {
            return;
        }
        final Block block = event.getWorld().getBlock(event.getX(), event.getY(), event.getZ());
        if (block instanceof BlockLiquid && MC.thePlayer.equals(event.getEntity()))
        {
            event.setAabb(LIQUID_FULL_AABB.copy().offset(event.getX(), event.getY(), event.getZ()));
        }
    };

    private boolean isAboveWater()
    {
        if (MC.thePlayer.isInWater())
        {
            return false;
        }
        for (double y = 0.0; y <= 1.0; y += 0.1)
        {
            final Block block = MC.theWorld.getBlock(
                    (int) Math.floor(MC.thePlayer.posX),
                    (int) Math.floor(MC.thePlayer.boundingBox.minY - y),
                    (int) Math.floor(MC.thePlayer.posZ));
            if (block instanceof BlockLiquid)
            {
                return true;
            }
        }
        return false;
    }
}

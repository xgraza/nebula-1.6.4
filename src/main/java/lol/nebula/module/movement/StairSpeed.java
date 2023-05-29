package lol.nebula.module.movement;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.entity.move.EventMove;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.util.player.MoveUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;

import static lol.nebula.util.world.WorldUtils.getBlock;

/**
 * @author aesthetical
 * @since 05/28/23
 */
public class StairSpeed extends Module {
    public StairSpeed() {
        super("Stair Speed", "Makes you move up stairs faster", ModuleCategory.MOVEMENT);
    }

    @Listener
    public void onMove(EventMove event) {
        // this is aids

        Block block = getBlock(mc.thePlayer.getGroundPosition().addVector(0, -1, 0));
        if (!(block instanceof BlockStairs) || !MoveUtils.isMoving()) return;

        double speed = 1.2 * MoveUtils.getBaseNcpSpeed(0) - 0.01;
        if (mc.thePlayer.ticksExisted % 3 == 0) speed -= 0.06;

        if (mc.thePlayer.onGround) {
            double y = 0.42f;
            mc.thePlayer.motionY = y;
            event.setY(y);
            speed *= 1.6;
        } else {
            speed = MoveUtils.getSpeed();
        }

        MoveUtils.strafe(event, speed);

    }
}

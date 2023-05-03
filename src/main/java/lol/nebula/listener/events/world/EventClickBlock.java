package lol.nebula.listener.events.world;

import lol.nebula.listener.bus.CancelableEvent;
import net.minecraft.util.EnumFacing;

/**
 * @author aesthetical
 * @since 05/03/23
 */
public class EventClickBlock extends CancelableEvent {
    private final int x, y, z;
    private final EnumFacing facing;

    public EventClickBlock(int x, int y, int z, int facing) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.facing = facing == -1 ? null : EnumFacing.faceList[facing];
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public EnumFacing getFacing() {
        return facing;
    }
}

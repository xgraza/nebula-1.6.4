package lol.nebula.listener.events.world;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;

/**
 * @author aesthetical
 * @since 04/29/23
 */
public class EventCollisionBox {
    private final Block block;
    private final Entity entity;
    private final int x, y, z;
    private AxisAlignedBB aabb;

    public EventCollisionBox(Block block, Entity entity, int x, int y, int z, AxisAlignedBB aabb) {
        this.block = block;
        this.entity = entity;
        this.x = x;
        this.y = y;
        this.z = z;
        this.aabb = aabb;
    }

    public Block getBlock() {
        return block;
    }

    public Entity getEntity() {
        return entity;
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

    public AxisAlignedBB getAabb() {
        return aabb;
    }

    public void setAabb(AxisAlignedBB aabb) {
        this.aabb = aabb;
    }
}

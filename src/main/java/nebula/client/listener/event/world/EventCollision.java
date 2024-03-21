package nebula.client.listener.event.world;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;

/**
 * @author Gavin
 * @since 08/24/23
 */
public class EventCollision {
  private AxisAlignedBB box;
  private final Block block;
  private final Entity entity;
  private final int x, y, z;

  public EventCollision(AxisAlignedBB box, Block block, Entity entity, int x, int y, int z) {
    this.box = box;
    this.block = block;
    this.entity = entity;
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public AxisAlignedBB box() {
    return box;
  }

  public void setBox(AxisAlignedBB box) {
    this.box = box;
  }

  public Block block() {
    return block;
  }

  public Entity entity() {
    return entity;
  }

  public int x() {
    return x;
  }

  public int y() {
    return y;
  }

  public int z() {
    return z;
  }
}

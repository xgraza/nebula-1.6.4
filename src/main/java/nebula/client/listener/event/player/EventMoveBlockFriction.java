package nebula.client.listener.event.player;

import net.minecraft.block.Block;

/**
 * @author Gavin
 * @since 08/17/23
 */
public class EventMoveBlockFriction {
  private final Block block;
  private float slipperiness;

  public EventMoveBlockFriction(Block block, float slipperiness) {
    this.block = block;
    this.slipperiness = slipperiness;
  }

  public Block block() {
    return block;
  }

  public float slipperiness() {
    return slipperiness;
  }

  public void setSlipperiness(float slipperiness) {
    this.slipperiness = slipperiness;
  }
}

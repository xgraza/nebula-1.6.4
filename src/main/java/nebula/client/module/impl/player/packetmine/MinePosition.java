package nebula.client.module.impl.player.packetmine;

/**
 * @author Gavin
 * @since 08/18/23
 * @param x
 * @param y
 * @param z
 * @param face the face the block was broken at
 */
public record MinePosition(int x, int y, int z, int face) {

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof MinePosition)) return false;
    return o.hashCode() == hashCode();
  }

  @Override
  public int hashCode() {
    int result = x;
    result = 31 * result + y;
    result = 31 * result + z;
    result = 31 * result + face;
    return result;
  }
}

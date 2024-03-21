package nebula.client.module.impl.render.chams;

import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_LINE;

/**
 * @author Gavin
 * @since 08/18/23
 */
public enum ChamsMode {
  FILL(GL_FILL),
  LINE(GL_LINE);

  private final int cap;

  ChamsMode(int cap) {
    this.cap = cap;
  }

  public int cap() {
    return cap;
  }
}

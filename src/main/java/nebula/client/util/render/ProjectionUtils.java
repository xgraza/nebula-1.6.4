package nebula.client.util.render;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.glu.GLU;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;

public class ProjectionUtils {

  private static FloatBuffer modelMatrix;
  private static FloatBuffer projMatrix;
  private static IntBuffer viewport;

  /**
   * Updates the modelMatrix, projectionMatrix, and the viewport
   */
  public static void updateProjection() {
    if (modelMatrix == null || projMatrix == null || viewport == null) {
      modelMatrix = BufferUtils.createFloatBuffer(4 * 4);
      projMatrix = BufferUtils.createFloatBuffer(4 * 4);
      viewport = BufferUtils.createIntBuffer(4 * 4);
    }

    glGetFloat(GL_MODELVIEW_MATRIX, modelMatrix);
    glGetFloat(GL_PROJECTION_MATRIX, projMatrix);
    glGetInteger(GL_VIEWPORT, viewport);
  }

  /**
   * Converts the x, y, z coordinates from the 3D space into a 2D plane
   * @param x x coordinate
   * @param y y coordinate
   * @param z z coordinate
   * @return the resulting xyz coordinates
   */
  public static float[] project(double x, double y, double z) {
    FloatBuffer winPos = BufferUtils.createFloatBuffer(3);
    GLU.gluProject((float) x, (float) y, (float) z, modelMatrix, projMatrix, viewport, winPos);
    return new float[] { winPos.get(0), Display.getHeight() - winPos.get(1), winPos.get(2) };
  }
}

package us.nebula.client.util.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.GL_QUADS;

/**
 * @author xgraza
 * @since 07/03/25
 */
public final class Renderer3D
{
    private static final Minecraft MC = Minecraft.getMinecraft();

    private boolean createdBuffers;

    private Mesh quadMesh;

    public void createBuffers()
    {
        quadMesh = new Mesh(GL_QUADS, 240);
    }

    public void quadAllFaces(final AxisAlignedBB bb, final int argb)
    {
        quad(bb, QuadFaces.ALL_SIDES, argb);
    }

    public void quad(final AxisAlignedBB bb, final int sides, final int argb)
    {

    }

    public void draw()
    {

    }

    private Vec3 getCamera()
    {
        return Vec3.createVectorHelper(RenderManager.renderPosX,
                RenderManager.renderPosY,
                RenderManager.renderPosZ);
    }

    public interface QuadFaces
    {
        int UP = 0;
        int DOWN = 1;
        int NORTH = 2;
        int SOUTH = 3;
        int EAST = 4;
        int WEST = 5;

        int ALL_SIDES = UP | DOWN | NORTH | SOUTH | EAST | WEST;
    }

    private static final class Mesh
    {
        private final int glMode, size;
        private final FloatBuffer vertexBuffer, colorBuffer;

        public Mesh(int glMode, int size)
        {
            this.glMode = glMode;
            this.size = size;

            vertexBuffer = BufferUtils.createFloatBuffer(size * 3);
            colorBuffer = BufferUtils.createFloatBuffer(size * 4);
        }

        public int getGlMode()
        {
            return glMode;
        }

        public int getSize()
        {
            return size;
        }

        public FloatBuffer getVertexBuffer()
        {
            return vertexBuffer;
        }

        public FloatBuffer getColorBuffer()
        {
            return colorBuffer;
        }
    }
}

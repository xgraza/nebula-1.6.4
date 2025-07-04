package us.nebula.api.render;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.src.BlockPos;
import net.minecraft.src.GlStateManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.GL15;
import us.nebula.impl.cheat.render.EntityCullingCheat;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.lwjgl.opengl.GL11.GL_QUAD_STRIP;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL33.GL_ANY_SAMPLES_PASSED;

/**
 * @author xgraza
 * @since 03/25/25
 * Credit to <a href="https://github.com/Sk1erLLC/Patcher/blob/4ce6e196e5ad1339f8a0ab96eb5680c2f6464583/src/main/java/club/sk1er/patcher/util/world/render/culling/EntityCulling.java">...</a>
 */
public final class EntityCulling
{
    private static final Map<UUID, Result> QUERY_RESULTS = new ConcurrentHashMap<>();

    public static boolean isActive()
    {
        return EntityCullingCheat.INSTANCE.isToggled();
    }

    public static int getQuery()
    {
        return GL15.glGenQueries();
    }

    public static void reset()
    {
        for (final UUID uuid : QUERY_RESULTS.keySet())
        {
            final Result result = QUERY_RESULTS.get(uuid);
            if (result.id != 0)
            {
                glDeleteQueries(result.id);
                result.id = 0;
            }
            QUERY_RESULTS.remove(uuid);
        }
    }

    public static void removeEntity(final Entity entity)
    {
        if (!isActive())
        {
            return;
        }
        if (!QUERY_RESULTS.containsKey(entity.getUniqueID()))
        {
            return;
        }
        final Result result = QUERY_RESULTS.get(entity.getUniqueID());
        if (result.id != 0)
        {
            glDeleteQueries(result.id);
        }
        QUERY_RESULTS.remove(entity.getUniqueID());
    }

    public static void checkCulling()
    {
        if (!isActive())
        {
            return;
        }
        for (final Result result : QUERY_RESULTS.values())
        {
            if (result.id == 0)
            {
                continue;
            }
            if (glGetQueryObjecti(result.id, GL_QUERY_RESULT_AVAILABLE) == 0)
            {
                continue;
            }
            result.value = glGetQueryObjecti(result.id, GL_QUERY_RESULT) != 0;
            glDeleteQueries(result.id);
            result.id = 0;
        }
    }

    public static void queryTileEntity(final TileEntity entity)
    {
        if (!isActive())
        {
            return;
        }
        final Result result = QUERY_RESULTS.computeIfAbsent(
                entity.getRandomUUID(), (x) -> new Result());

        if (System.currentTimeMillis() - result.reQueryAt < 50L)
        {
            return;
        }

        result.id = getQuery();
        glBeginQuery(GL_ANY_SAMPLES_PASSED, result.id);
        final AxisAlignedBB box = new AxisAlignedBB(new BlockPos(entity.xCoord, entity.yCoord, entity.zCoord));
        final AxisAlignedBB renderBox = box.copy()
                .expand(0.2, 0.2, 0.2)
                .offset(-RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ);
        drawOutlinedBoundingBox(renderBox);
        glEndQuery(GL_ANY_SAMPLES_PASSED);
        result.reQueryAt = System.currentTimeMillis() + 50L;
    }

    public static void queryEntity(final Entity entity)
    {
        if (!isActive())
        {
            return;
        }
        final Result result = QUERY_RESULTS.computeIfAbsent(
                entity.getUniqueID(), (x) -> new Result());

        if (System.currentTimeMillis() - result.reQueryAt < 50L)
        {
            return;
        }

        result.id = getQuery();
        glBeginQuery(GL_ANY_SAMPLES_PASSED, result.id);
        final AxisAlignedBB renderBox = entity.boundingBox.copy()
                        .expand(0.2, 0.2, 0.2)
                        .offset(-RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ);
        drawOutlinedBoundingBox(renderBox);
        glEndQuery(GL_ANY_SAMPLES_PASSED);
        result.reQueryAt = System.currentTimeMillis() + 50L;
    }

    public static boolean shouldRenderEntity(final Entity entity)
    {
        if (!isActive())
        {
            return true;
        }
        if (!QUERY_RESULTS.containsKey(entity.getUniqueID()))
        {
            return true;
        }
        return QUERY_RESULTS.get(entity.getUniqueID()).value;
    }

    public static boolean shouldRenderTileEntity(final TileEntity entity)
    {
        if (!isActive())
        {
            return true;
        }
        if (!QUERY_RESULTS.containsKey(entity.getRandomUUID()))
        {
            return false;
        }
        return QUERY_RESULTS.get(entity.getRandomUUID()).value;
    }

    public static void drawOutlinedBoundingBox(AxisAlignedBB bb)
    {
        GlStateManager.disableAlpha();
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);
        GlStateManager.colorMask(false, false, false, false);
        Tessellator var2 = Tessellator.instance;
        var2.startDrawing(GL_QUAD_STRIP);
        var2.addVertex(bb.maxX, bb.maxY, bb.maxZ);
        var2.addVertex(bb.maxX, bb.maxY, bb.minZ);
        var2.addVertex(bb.minX, bb.maxY, bb.maxZ);
        var2.addVertex(bb.minX, bb.maxY, bb.minZ);
        var2.addVertex(bb.minX, bb.minY, bb.maxZ);
        var2.addVertex(bb.minX, bb.minY, bb.minZ);
        var2.addVertex(bb.minX, bb.maxY, bb.minZ);
        var2.addVertex(bb.minX, bb.minY, bb.minZ);
        var2.addVertex(bb.maxX, bb.maxY, bb.minZ);
        var2.addVertex(bb.maxX, bb.minY, bb.minZ);
        var2.addVertex(bb.maxX, bb.maxY, bb.maxZ);
        var2.addVertex(bb.maxX, bb.minY, bb.maxZ);
        var2.addVertex(bb.minX, bb.maxY, bb.maxZ);
        var2.addVertex(bb.minX, bb.minY, bb.maxZ);
        var2.addVertex(bb.minX, bb.minY, bb.maxZ);
        var2.addVertex(bb.maxX, bb.minY, bb.maxZ);
        var2.addVertex(bb.minX, bb.minY, bb.minZ);
        var2.addVertex(bb.maxX, bb.minY, bb.minZ);
        var2.draw();
        GlStateManager.depthMask(true);
        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.enableAlpha();
    }

    private static final class Result
    {
        private long reQueryAt;
        private int id;
        private boolean value;
    }
}

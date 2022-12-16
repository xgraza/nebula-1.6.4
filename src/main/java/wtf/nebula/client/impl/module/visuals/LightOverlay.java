package wtf.nebula.client.impl.module.visuals;

import me.bush.eventbus.annotation.EventListener;
import me.bush.eventbus.annotation.ListenerPriority;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.SpawnerAnimals;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.impl.event.impl.render.EventRender3D;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;
import wtf.nebula.client.utils.render.RenderEngine;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class LightOverlay extends ToggleableModule {
    private final Property<Integer> range = new Property<>(16, 5, 200, "Distance", "range", "dist");

    public LightOverlay() {
        super("Light Overlay", new String[]{"lightoverlay", "mobspawn", "mobspawns", "mobspawnesp"}, ModuleCategory.VISUALS);
        offerProperties(range);
    }

    @EventListener(priority = ListenerPriority.HIGHEST)
    public void onRenderWorld(EventRender3D event) {
        List<BlockPos> vecs = getPossibleMobSpawns();
        if (!vecs.isEmpty()) {
            glPushMatrix();
            glEnable(GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 0, 1);
            glDisable(GL_TEXTURE_2D);
            glDisable(GL_DEPTH_TEST);
            glDepthMask(false);

            glLoadIdentity();
            mc.entityRenderer.orientCamera(event.getPartialTicks());

            glTranslated(-RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ);

            RenderEngine.color(new Color(255, 255, 0, 80).getRGB());

            for (BlockPos vec : vecs) {

                glBegin(GL_QUADS);
                {
                    glVertex3d(vec.x, vec.y, vec.z + 1);
                    glVertex3d(vec.x + 1, vec.y, vec.z + 1);
                    glVertex3d(vec.x + 1, vec.y, vec.z);
                    glVertex3d(vec.x, vec.y, vec.z);
                }
                glEnd();
            }

            glDepthMask(true);
            glEnable(GL_DEPTH_TEST);
            glEnable(GL_TEXTURE_2D);
            glDisable(GL_BLEND);
            glPopMatrix();
        }
    }

    private List<BlockPos> getPossibleMobSpawns() {
        List<BlockPos> positions = new ArrayList<>();

        int posX = MathHelper.floor_double(mc.thePlayer.posX);
        int posY = MathHelper.floor_double(mc.thePlayer.posY);
        int posZ = MathHelper.floor_double(mc.thePlayer.posZ);

        for (int x = -range.getValue(); x <= range.getValue(); ++x) {
            for (int y = -range.getValue(); y <= range.getValue(); ++y) {
                for (int z = -range.getValue(); z <= range.getValue(); ++z) {
                    BlockPos pos = new BlockPos(posX + x, posY + y, posZ + z);

                    if (!mc.theWorld.isAirBlock(pos.x, pos.y, pos.z)) {
                        continue;
                    }

                    BiomeGenBase base = mc.theWorld.getBiomeGenForCoords(pos.x & 15, pos.z & 15);
                    if (base.getSpawningChance() > 0.0f && !base.getSpawnableList(EnumCreatureType.monster).isEmpty()) {
                        Chunk chunk = mc.theWorld.getChunkFromBlockCoords(pos.x, pos.z);

                        if (SpawnerAnimals.canCreatureTypeSpawnAtLocation(EnumCreatureType.monster, mc.theWorld, pos.x, pos.y, pos.z)) {
                            int bl = chunk.getSavedLightValue(EnumSkyBlock.Block, pos.x & 15, pos.y, pos.z & 15);
                            if (bl == 0) {
                                positions.add(pos);
                            }
                        }
                    }
                }
            }
        }

        return positions;
    }

    private static class BlockPos {
        private final int x, y, z;

        public BlockPos(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}

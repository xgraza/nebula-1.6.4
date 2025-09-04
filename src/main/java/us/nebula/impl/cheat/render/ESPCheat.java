package us.nebula.impl.cheat.render;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import us.nebula.Nebula;
import us.nebula.api.listener.EventListener;
import us.nebula.api.listener.Subscribe;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.api.value.Setting;
import us.nebula.impl.cheat.player.FreecamCheat;
import us.nebula.impl.event.game.EventUpdate;
import us.nebula.impl.event.render.EventRender2D;
import us.nebula.impl.event.render.EventRender3D;
import us.nebula.util.player.EntityUtil;
import us.nebula.util.render.ProjectionUtil;
import us.nebula.util.render.RenderUtil;

import java.awt.Color;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author xgraza
 * @since 09/04/2025
 */
@CheatManifest(name = "ESP", category = CheatCategory.RENDER)
public final class ESPCheat extends Cheat
{
    private final Setting<Mode> modeSetting = new Setting<>("Mode", Mode.SIMPLE);

    private final Setting<Boolean> labelsSetting = new Setting<>(
            "Labels", true)
            .setVisibility(() -> modeSetting.getValue() == Mode.CS_GO);

    // entities
    private final Setting<Boolean> playersSetting = new Setting<>(
            "Players", true);
    private final Setting<Boolean> hostileSetting = new Setting<>(
            "Hostile", true);
    private final Setting<Boolean> passiveSetting = new Setting<>(
            "Passive", true);
    private final Setting<Boolean> chestsSetting = new Setting<>(
            "Chests", true);
    private final Setting<Boolean> tileEntitiesSetting = new Setting<>(
            "Other Tile Entities", true)
            .setVisibility(() -> modeSetting.getValue() != Mode.CS_GO);

    private final Map<Integer, float[][]> projected = new ConcurrentHashMap<>();
    private final List<Object> renderTargetList = new CopyOnWriteArrayList<>();

    @Override
    protected void onDisable()
    {
        super.onDisable();
        projected.clear();
        renderTargetList.clear();
    }

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        if (MC.thePlayer.ticksExisted % 20 != 0)
        {
            return;
        }

        for (int entityId : projected.keySet())
        {
            projected.remove(entityId);
        }

        renderTargetList.clear();
        for (final Entity bEntity : MC.theWorld.loadedEntityList)
        {
            if (bEntity == null
                    || bEntity.isDead
                    || bEntity.equals(MC.thePlayer)
                    || bEntity.getEntityId() == FreecamCheat.CAMERA_ENTITY_ID)
            {
                continue;
            }

            if (!playersSetting.getValue() && bEntity instanceof EntityPlayer)
            {
                continue;
            }
            if (!hostileSetting.getValue() && EntityUtil.isEntityHostile(bEntity))
            {
                continue;
            }
            if (!passiveSetting.getValue() && EntityUtil.isEntityPassive(bEntity))
            {
                continue;
            }

            renderTargetList.add(bEntity);
        }

        for (final TileEntity entity : MC.theWorld.loadedTileEntityList)
        {
            if (modeSetting.getValue() == Mode.CS_GO)
            {
                if (entity instanceof TileEntityChest && chestsSetting.getValue())
                {
                    renderTargetList.add(entity);
                }
                continue;
            }

            if (!chestsSetting.getValue() && entity instanceof TileEntityChest)
            {
                continue;
            }

            if (!tileEntitiesSetting.getValue())
            {
                continue;
            }

            renderTargetList.add(entity);
        }
    };

    @Subscribe
    private final EventListener<EventRender2D> render2DEventListener = event ->
    {
        if (!modeSetting.getValue().equals(Mode.CS_GO))
        {
            return;
        }
        renderCSGOESP();
    };

    @Subscribe
    private final EventListener<EventRender3D> render3DEventListener = event ->
    {
        for (final Object entity : renderTargetList)
        {
            switch (modeSetting.getValue())
            {
                case CS_GO:
                {
                    projectEntity(entity, event.getPartialTicks());
                    break;
                }
                case SHADER:
                {
                    break;
                }
                case SIMPLE:
                {
                    renderBoxESP(entity, event.getPartialTicks());
                    break;
                }
            }
        }
    };

    private void renderCSGOESP()
    {
        for (final int entityID : projected.keySet())
        {
            Object gEntity = MC.theWorld.getEntityByID(entityID);
            if (gEntity == null)
            {
                for (final TileEntity tileEntity : MC.theWorld.loadedTileEntityList)
                {
                    if (tileEntity.hashCode() == entityID)
                    {
                        gEntity = tileEntity;
                        break;
                    }
                }
            }

            if (gEntity instanceof EntityLivingBase)
            {
                final EntityLivingBase e = (EntityLivingBase)gEntity;
                if (e.isDead || e.getHealth() <= 0.0f)
                {
                    renderTargetList.remove(e);
                    projected.remove(entityID);
                    continue;
                }
            }

            final float[][] projection = projected.get(entityID);
            final float[] top = projection[0], bottom = projection[1];

            double height = top[1] - bottom[1];
            double width = height * 0.3;

            glPushMatrix();

            glLineWidth(1.0f);
            glDisable(GL_DEPTH_TEST);
            glDisable(GL_TEXTURE_2D);

            glScaled(0.5, 0.5, 0.5);
            glColor4f(1, 1, 1, 1);

            glBegin(GL_LINES);
            {
                glVertex2d(top[0] - width, top[1]);
                glVertex2d(top[0] + width, top[1]);

                glVertex2d(top[0] - width, top[1]);
                glVertex2d(top[0] - width, bottom[1]);

                glVertex2d(top[0] + width, top[1]);
                glVertex2d(top[0] + width, bottom[1]);

                glVertex2d(top[0] - width, bottom[1]);
                glVertex2d(top[0] + width, bottom[1]);
            }
            glEnd();

            // heath bar

            if (gEntity instanceof EntityLivingBase)
            {
                final EntityLivingBase e = (EntityLivingBase)gEntity;
                final float healthPercent = (e.getHealth() + e.getAbsorptionAmount()) / 24.0f;
                glColor4f(
                        1.0f - healthPercent,
                        healthPercent,
                        0.0f,
                        1.0f
                );

                glBegin(GL_LINES);
                {
                    glVertex2d(top[0] + (height * 0.35), top[1]);
                    glVertex2d(top[0] + (height * 0.35), bottom[1]);
                }
                glEnd();
            }

            glEnable(GL_TEXTURE_2D);
            glEnable(GL_DEPTH_TEST);

            if (labelsSetting.getValue())
            {
                String text = null;
                if (gEntity instanceof EntityLivingBase)
                {
                    final EntityLivingBase e = (EntityLivingBase)gEntity;
                    text = e.getCommandSenderName() + EnumChatFormatting.RED + " " + e.getHealth() + "\u2764";
                } else if (gEntity instanceof TileEntity)
                {
                    final TileEntity e = (TileEntity)gEntity;
                    text = e.getBlockType().getLocalizedName();
                }

                if (text != null)
                {
                    final int textWidth = MC.fontRenderer.getStringWidth(text);
                    MC.fontRenderer.drawStringWithShadow(text, (int) (top[0] - (textWidth / 2.0f)), (int) top[1] - 10, -1);
                }
            }

            glPopMatrix();
        }
    }

    private void renderBoxESP(final Object entity, final float partialTicks)
    {
        AxisAlignedBB aabb = null;
        if (entity instanceof EntityLivingBase)
        {
            final EntityLivingBase e = (EntityLivingBase)entity;

            double x = (e.lastTickPosX + (e.posX - e.lastTickPosX) * partialTicks);
            double y = (e.lastTickPosY + (e.posY - e.lastTickPosY) * partialTicks);
            double z = (e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * partialTicks);

            double o = e.width - 0.25;

            aabb = new AxisAlignedBB(x - o, y - 0.2, z - o,
                    x + o,
                    y + e.height + 0.2,
                    z + o);
        } else if (entity instanceof TileEntity)
        {
            final TileEntity e = (TileEntity)entity;
            aabb = new AxisAlignedBB(e.xCoord, e.yCoord, e.zCoord,
                    e.xCoord + 1,
                    e.yCoord + 1,
                    e.zCoord + 1);
        }

        if (aabb == null)
        {
            return;
        }

        final int color = getColor(entity);
        RenderUtil.outlinedBox3D(aabb, 2.5f, color);
    }

    // amazing gavcode 3000
    private int getColor(final Object entity)
    {
        if (entity instanceof EntityLivingBase)
        {
            if (entity instanceof EntityPlayer)
            {
                final EntityPlayer player = (EntityPlayer)entity;
                if (Nebula.INSTANCE.getFriendManager().isFriend(player))
                {
                    return Color.cyan.getRGB();
                }
                return Color.gray.getRGB();
            } else if (EntityUtil.isEntityPassive((Entity) entity))
            {
                return Color.green.getRGB();
            } else if (EntityUtil.isEntityHostile((Entity) entity))
            {
                return Color.red.getRGB();
            }
        } else if (entity instanceof TileEntity)
        {
            if (entity instanceof TileEntityEnderChest)
            {
                return Color.magenta.getRGB();
            } else if (entity instanceof TileEntityChest)
            {
                return Color.orange.getRGB();
            }
        }
        return Color.gray.getRGB();
    }

    private void projectEntity(final Object entity, final float partialTicks)
    {
        if (entity instanceof EntityLivingBase)
        {
            final EntityLivingBase e = (EntityLivingBase)entity;
            double x = (e.lastTickPosX + (e.posX - e.lastTickPosX) * partialTicks) - RenderManager.renderPosX;
            double y = (e.lastTickPosY + (e.posY - e.lastTickPosY) * partialTicks) - RenderManager.renderPosY;
            double z = (e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * partialTicks) - RenderManager.renderPosZ;

            float[] top = ProjectionUtil.project(x, y + e.height + 0.2, z);
            float[] bottom = ProjectionUtil.project(x, y - 0.2, z);

            projected.put(e.getEntityId(), new float[][] { top, bottom });
        } else if (entity instanceof TileEntity)
        {
            final TileEntity e = (TileEntity)entity;
            double x = e.xCoord;
            double y = e.yCoord;
            double z = e.zCoord;
            float[] top = ProjectionUtil.project(x, y + 1.2, z);
            float[] bottom = ProjectionUtil.project(x, y - 0.2, z);

            projected.put(e.hashCode(), new float[][] { top, bottom });
        }
    }

    private enum Mode
    {
        SIMPLE, CS_GO, SHADER
    }
}

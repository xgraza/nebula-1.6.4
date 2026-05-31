package us.nebula.client.cheat.impl.render;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import us.nebula.client.Nebula;
import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.impl.player.FreecamCheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.listener.EventListener;
import us.nebula.client.listener.Subscribe;
import us.nebula.client.listener.event.game.EventUpdate;
import us.nebula.client.listener.event.render.EventRender3D;
import us.nebula.client.setting.Setting;
import us.nebula.client.util.player.EntityUtil;
import us.nebula.client.util.render.RenderUtil;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.*;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author xgraza
 * @since 05/26/26
 */
@CheatManifest(name = "Tracers",
        description = "Draws a line to each selected entity from your crosshair",
        category = CheatCategory.RENDER)
public final class TracersCheat extends Cheat
{
    private final Setting<ColorMode> colorModeSetting = enumBuilder("Color Mode", ColorMode.DISTANCE)
            .setDescription("How to render the tracer color")
            .build();
    private final Setting<StemMode> stemSetting = enumBuilder("Stem", StemMode.TORSO)
            .setDescription("Where to render the stem on a traced entity")
            .build();

    private final Setting<Boolean> playersSetting = builder("Players", true)
            .setDescription("If to render a tracer to a player")
            .build();
    private final Setting<Boolean> friendsSetting = builder("Friends", false)
            .setDescription("If to render a tracer to your friends")
            .setVisibility((value) -> playersSetting.getValue())
            .build();
    private final Setting<Boolean> passiveSetting = builder("Passive", false)
            .setDescription("If to render a tracer to passive mobs")
            .build();
    private final Setting<Boolean> hostileSetting = builder("Hostile", false)
            .setDescription("If to render a tracer to hostile mob")
            .build();

    private final Setting<Float> lineWidthSetting = numberBuilder("Line Width", 1.5f)
            .setMin(0.5f)
            .setMax(5.0f)
            .setScale(0.1f)
            .setDescription("The width of the tracer")
            .build();

    private final List<Entity> renderEntityList = new CopyOnWriteArrayList<>();

    @Override
    public void onDisable()
    {
        super.onDisable();
        renderEntityList.clear();
    }

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        renderEntityList.clear();
        for (final Entity entity : MC.theWorld.loadedEntityList)
        {
            if (entity instanceof EntityPlayer)
            {
                if (!playersSetting.getValue())
                {
                    continue;
                }

                if (entity.equals(MC.thePlayer) && (MC.gameSettings.thirdPersonView == 0 && !FreecamCheat.INSTANCE.isToggled()))
                {
                    continue;
                }

                if (FreecamCheat.INSTANCE.isToggled() && entity.getEntityId() == FreecamCheat.CAMERA_ENTITY_ID)
                {
                    continue;
                }

                if (!friendsSetting.getValue() && Nebula.INSTANCE.getFriendManager().isFriend(entity.getCommandSenderName()))
                {
                    continue;
                }

                renderEntityList.add(entity);
            }

            if (EntityUtil.isEntityPassive(entity) && passiveSetting.getValue())
            {
                renderEntityList.add(entity);
            }

            if (EntityUtil.isEntityHostile(entity) && hostileSetting.getValue())
            {
                renderEntityList.add(entity);
            }
        }
    };

    @Subscribe
    private final EventListener<EventRender3D> render3DEventListener = event ->
    {
        if (renderEntityList.isEmpty())
        {
            return;
        }

        glPushMatrix();

        glDisable(GL_TEXTURE_2D);
        glDisable(GL_DEPTH_TEST);

        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        glLineWidth(lineWidthSetting.getValue());

        glDisable(GL_LIGHTING);

        glLoadIdentity();
        MC.entityRenderer.orientCamera(event.getPartialTicks());

        for (final Entity entity : renderEntityList)
        {
            setCorForEntity(entity);

            glBegin(GL_LINES);
            {
                final double x = entity.prevPosX + (entity.posX - entity.prevPosX) * event.getPartialTicks();
                double y = entity.prevPosY + (entity.posY - entity.prevPosY) * event.getPartialTicks();
                if (entity.equals(MC.thePlayer))
                {
                    y -= entity.height;
                }
                final double z = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * event.getPartialTicks();
                final Vec3 eyes = MC.renderViewEntity.getLook(event.getPartialTicks());

                glVertex3d(eyes.xCoord, eyes.yCoord, eyes.zCoord);
                glVertex3d(x - RenderManager.renderPosX, y - RenderManager.renderPosY, z - RenderManager.renderPosZ);

                if (stemSetting.getValue() != StemMode.FEET)
                {
                    glVertex3d(x - RenderManager.renderPosX, y - RenderManager.renderPosY, z - RenderManager.renderPosZ);
                    glVertex3d(x - RenderManager.renderPosX, (y + stemSetting.getValue().getOffset(entity.height)) - RenderManager.renderPosY, z - RenderManager.renderPosZ);
                }
            }
            glEnd();
        }

        glEnable(GL_LIGHTING);

        glLineWidth(1.0f);
        glDisable(GL_LINE_SMOOTH);

        glEnable(GL_TEXTURE_2D);
        glEnable(GL_DEPTH_TEST);

        glPopMatrix();
    };

    private void setCorForEntity(final Entity entity)
    {
        switch (colorModeSetting.getValue())
        {
            case DISTANCE:
            {
                if (entity instanceof EntityPlayer && Nebula.INSTANCE.getFriendManager().isFriend(entity.getCommandSenderName()))
                {
                    glColor3d(0.0, 1.0, 1.0);
                } else
                {
                    final double dist = MC.thePlayer.getDistanceToEntity(entity) / 20.0;
                    glColor3d(2.0 - dist, dist, 0.0);
                }
                return;
            }
            case COLORED:
            {
                if (entity instanceof EntityPlayer)
                {
                    if (Nebula.INSTANCE.getFriendManager().isFriend(entity.getCommandSenderName()))
                    {
                        glColor3d(0.0, 1.0, 1.0);
                    } else
                    {
                        glColor3d(1, 1, 1);
                    }
                } else if (EntityUtil.isEntityHostile(entity))
                {
                    glColor3d(1, 0, 0);
                } else if (EntityUtil.isEntityPassive(entity))
                {
                    if (entity instanceof EntityTameable)
                    {
                        final EntityTameable tameable = (EntityTameable) entity;
                        final String owner = tameable.getOwnerName();
                        if (owner != null && !owner.isEmpty())
                        {
                            if (owner.equals(MC.thePlayer.getCommandSenderName()))
                            {
                                glColor3d(0.0, 0.5, 0.5);
                            } else
                            {
                                glColor3d(0.5, 0.5, 0.5);
                            }
                        }
                    } else
                    {
                        glColor3d(0, 1, 0);
                    }
                } else
                {
                    glColor3d(0.7, 0.7, 0.7);
                }
                break;
            }
            case CLIENT:
            {
                RenderUtil.setGLColor(HUDCheat.INSTANCE.getBaseColor(10));
                break;
            }
        }
    }

    private enum ColorMode
    {
        DISTANCE, COLORED, CLIENT
    }

    private enum StemMode
    {
        FEET((height) -> 0.0),
        LEGS((height) -> height / 3.0),
        TORSO((height) -> height / 2.0),
        HEAD((height) -> height);

        private final DoubleFunction<Double> offsetSupplier;

        StemMode(DoubleFunction<Double> offsetSupplier)
        {
            this.offsetSupplier = offsetSupplier;
        }

        public double getOffset(final double entityHeight)
        {
            return offsetSupplier.apply(entityHeight);
        }
    }
}

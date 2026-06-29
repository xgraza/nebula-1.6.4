package ez.nebula.client.impl.module.combat;

import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.listener.event.game.EventPostUpdate;
import ez.nebula.client.api.listener.event.game.EventUpdate;
import ez.nebula.client.api.listener.event.network.EventPacket;
import ez.nebula.client.api.listener.event.render.EventRender3D;
import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.setting.NumberSetting;
import ez.nebula.client.api.setting.Setting;
import ez.nebula.client.core.Nebula;
import ez.nebula.client.util.math.AngleUtil;
import io.netty.util.internal.ConcurrentSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.network.play.server.S0EPacketSpawnObject;

import java.util.Set;
import java.util.TreeMap;

/**
 * @author xgraza
 * @since 6/21/26
 */
@ModuleManifest(name = "AntiFireball",
        description = "Automatically hits away fireballs",
        category = ModuleCategory.COMBAT)
public final class AntiFireballModule extends Module
{
    private static final int ANTIFIREBALL_ROTATION_PRIORITY = 20;
    private static final int FIREBALL_TYPE = 63;

    private final NumberSetting<Double> rangeSetting = numberBuilder("Range", 4.5)
            .setMin(1.0)
            .setMax(6.0)
            .setScale(0.1)
            .setDescription("How far away to reflect fireballs from")
            .build();
    private final Setting<Boolean> rotateSetting = builder("Rotate", false)
            .setDescription("If to rotate towards the entity")
            .build();
    private final Setting<Boolean> multiSetting = builder("Multi", false)
            .setDescription("If to hit multiple fireballs in one tick")
            .setVisibility((value) -> !rotateSetting.getValue())
            .build();

    private final Set<Integer> trackedFireballList = new ConcurrentSet<>();
    private EntityLargeFireball entity;
    private float[] angles;

    @Override
    public void onDisable()
    {
        super.onDisable();
        trackedFireballList.clear();
        entity = null;
        angles = null;
    }

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        final TreeMap<Double, EntityLargeFireball> entityFireballTreeMap = new TreeMap<>();
        for (final Integer trackedEntityID : trackedFireballList)
        {
            final Entity entity = MC.theWorld.getEntityByID(trackedEntityID);
            if (!(entity instanceof EntityLargeFireball))
            {
                trackedFireballList.remove(trackedEntityID);
                continue;
            }

            final double distance = MC.thePlayer.getDistanceToEntity(entity);
            if (distance > rangeSetting.getValue())
            {
                continue;
            }
            entityFireballTreeMap.put(distance, (EntityLargeFireball) entity);
        }

        if (entityFireballTreeMap.isEmpty())
        {
            return;
        }
        entity = entityFireballTreeMap.firstEntry().getValue();

        if (rotateSetting.getValue())
        {
            if (angles == null)
            {
                return;
            }
            if (!Nebula.INSTANCE.getRotationManager().spoof(angles[0], angles[1], ANTIFIREBALL_ROTATION_PRIORITY))
            {
                entity = null;
            }
            return;
        }

        for (final EntityLargeFireball fireballEntity : entityFireballTreeMap.values())
        {
            MC.thePlayer.swingItem();
            MC.playerController.attackEntity(MC.thePlayer, fireballEntity);
            if (!multiSetting.getValue())
            {
                return;
            }
        }
    };

    @Subscribe
    private final EventListener<EventPostUpdate> postUpdateEventListener = event ->
    {
        if (entity != null && angles != null)
        {
            MC.thePlayer.swingItem();
            MC.playerController.attackEntity(MC.thePlayer, entity);
            entity = null;
            angles = null;
        }
    };

    @Subscribe
    private final EventListener<EventRender3D> render3DEventListener = event ->
    {
        if (entity != null)
        {
            if (!rotateSetting.getValue())
            {
                angles = null;
                return;
            }
            angles = AngleUtil.entityAngles(entity, entity.height / 2.0, event.getPartialTicks());
        }
    };

    @Subscribe
    private final EventListener<EventPacket.Inbound> inboundEventListener = event ->
    {
        if (event.getPacket() instanceof S0EPacketSpawnObject)
        {
            final S0EPacketSpawnObject packet = event.getPacket();
            if (packet.getType() != FIREBALL_TYPE)
            {
                return;
            }
            trackedFireballList.add(packet.getID());
        }
    };
}

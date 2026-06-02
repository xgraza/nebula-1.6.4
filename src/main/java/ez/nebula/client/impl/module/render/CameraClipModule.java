package ez.nebula.client.impl.module.render;

import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleInstance;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.listener.event.render.EventCameraDistance;
import ez.nebula.client.api.setting.Setting;

/**
 * @author xgraza
 * @since 03/06/25
 */
@ModuleManifest(name = "CameraClip",
        description = "Clips your camera through blocks and extends the distance you can see in third person",
        category = ModuleCategory.RENDER)
public final class CameraClipModule extends Module
{
    @ModuleInstance
    public static CameraClipModule INSTANCE;

    private final Setting<Double> distanceSetting = numberBuilder("Distance", 4.0)
            .setMin(0.5)
            .setMax(50.0)
            .setScale(0.1)
            .setDescription("How many blocks to clip your camera")
            .build();
    public final Setting<Boolean> phasePerspective = builder("Phase Perspective", true)
            .setDescription("If to disable the forced third person perspective when phased in a block")
            .build();

    @Subscribe
    private final EventListener<EventCameraDistance> cameraDistanceEventListener = event ->
            event.setCameraDistance(distanceSetting.getValue());
}

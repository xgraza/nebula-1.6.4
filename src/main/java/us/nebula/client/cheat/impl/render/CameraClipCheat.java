package us.nebula.client.cheat.impl.render;

import us.nebula.client.listener.EventListener;
import us.nebula.client.listener.Subscribe;
import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatInstance;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.listener.event.render.EventCameraDistance;
import us.nebula.client.setting.Setting;

/**
 * @author xgraza
 * @since 03/06/25
 */
@CheatManifest(name = "CameraClip",
        description = "Clips your camera through blocks and extends the distance you can see in third person",
        category = CheatCategory.RENDER)
public final class CameraClipCheat extends Cheat
{
    @CheatInstance
    public static CameraClipCheat INSTANCE;

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

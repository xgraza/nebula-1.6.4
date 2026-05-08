package us.nebula.client.cheat.impl.render;

import us.nebula.client.listener.EventListener;
import us.nebula.client.listener.Subscribe;
import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatInstance;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.util.value.Setting;
import us.nebula.client.listener.event.render.EventCameraDistance;

/**
 * @author xgraza
 * @since 03/06/25
 */
@CheatManifest(name = "CameraClip",
        description = "Clips your camera through blocks and extends the distance",
        category = CheatCategory.RENDER)
public final class CameraClipCheat extends Cheat
{
    @CheatInstance
    public static CameraClipCheat INSTANCE;

    private final Setting<Double> distanceSetting = new Setting<>(
            "Distance", 4.0, 0.5, 50.0, 0.5);
    public final Setting<Boolean> phasePerspective = new Setting<>(
            "Phase Perspective", true);

    @Subscribe
    private final EventListener<EventCameraDistance> cameraDistanceEventListener = event ->
            event.setCameraDistance(distanceSetting.getValue());
}

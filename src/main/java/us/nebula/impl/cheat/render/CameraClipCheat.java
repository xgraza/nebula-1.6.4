package us.nebula.impl.cheat.render;

import us.nebula.api.listener.EventListener;
import us.nebula.api.listener.Subscribe;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.api.value.Setting;
import us.nebula.impl.event.render.EventCameraDistance;

/**
 * @author xgraza
 * @since 03/06/25
 */
@CheatManifest(name = "CameraClip",
        description = "Clips your camera through blocks and extends the distance",
        category = CheatCategory.RENDER)
public final class CameraClipCheat extends Cheat
{
    private final Setting<Double> distanceSetting = new Setting<>(
            "Distance", 4.0, 0.5, 50.0, 0.5);

    @Subscribe
    private final EventListener<EventCameraDistance> cameraDistanceEventListener = event ->
            event.setCameraDistance(distanceSetting.getValue());
}

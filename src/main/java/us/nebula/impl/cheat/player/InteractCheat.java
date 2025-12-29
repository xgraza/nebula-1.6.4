package us.nebula.impl.cheat.player;

import us.nebula.api.listener.EventListener;
import us.nebula.api.listener.Subscribe;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatInstance;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.api.value.Setting;
import us.nebula.impl.event.game.EventUpdate;

/**
 * @author xgraza
 * @since 06/16/25
 */
@CheatManifest(name = "Interact",
        description = "Changes how you interact with things",
        category = CheatCategory.PLAYER)
public final class InteractCheat extends Cheat
{
    @CheatInstance
    public static InteractCheat INSTANCE;

    private final Setting<Integer> placeDelaySetting = new Setting<>(
            "Place Delay", 0, 0, 4, 1);

    public final Setting<Double> attackReachSetting = new Setting<>(
            "Attack Reach", 3.0, 1.0, 6.0, 0.1);
    public final Setting<Float> placeReachSetting = new Setting<>(
            "Place Reach", 4.5f, 1.0f, 6.0f, 0.1f);

    public final Setting<Boolean> waterPlaceSetting = new Setting<>(
            "Water Place", false);

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        MC.rightClickDelayTimer = placeDelaySetting.getValue();
    };
}

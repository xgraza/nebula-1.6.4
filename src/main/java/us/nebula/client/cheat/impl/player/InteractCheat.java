package us.nebula.client.cheat.impl.player;

import us.nebula.client.listener.EventListener;
import us.nebula.client.listener.Subscribe;
import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatInstance;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.listener.event.game.EventUpdate;
import us.nebula.client.setting.Setting;

/**
 * @author xgraza
 * @since 06/16/25
 */
@CheatManifest(name = "Interact",
        description = "Changes how you interact with blocks or entities",
        category = CheatCategory.PLAYER)
public final class InteractCheat extends Cheat
{
    @CheatInstance
    public static InteractCheat INSTANCE;

    public final Setting<Integer> placeDelaySetting = numberBuilder("Place Delay", 0)
            .setMin(0)
            .setMax(4)
            .setScale(1)
            .setDescription("How many ticks it should take before allowing you to place another block")
            .build();

    public final Setting<Double> attackReachSetting = numberBuilder("Attach Reach", 3.0)
            .setMin(1.0)
            .setMax(6.0)
            .setScale(0.1)
            .setDescription("The distance in blocks you are able to interact with entities")
            .build();
    public final Setting<Double> placeReachSetting = numberBuilder("Place Reach", 3.0)
            .setMin(1.0)
            .setMax(6.0)
            .setScale(0.1)
            .setDescription("The distance in blocks you are able to interact with blocks")
            .build();

    public final Setting<Boolean> waterPlaceSetting = builder("Water Place", false)
            .setDescription("If to allow placing in water")
            .build();

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
            MC.rightClickDelayTimer = placeDelaySetting.getValue();
}

package us.nebula.client.impl.cheat.render;

import us.nebula.client.api.listener.EventListener;
import us.nebula.client.api.listener.Subscribe;
import us.nebula.client.api.manager.cheat.Cheat;
import us.nebula.client.api.manager.cheat.CheatCategory;
import us.nebula.client.api.manager.cheat.CheatManifest;
import us.nebula.client.api.value.Setting;
import us.nebula.client.impl.event.render.EventGamma;

/**
 * @author xgraza
 * @since 02/16/25
 */
@CheatManifest(name = "Fullbright",
        description = "Forces gamma all the way up to see in the dark",
        category = CheatCategory.RENDER)
public final class FullbrightCheat extends Cheat
{
    private final Setting<Mode> modeSetting = new Setting<>("Mode", Mode.GAMMA);

    @Subscribe
    private final EventListener<EventGamma> gammaEventListener = event
            -> event.setGamma(100.0f);

    private enum Mode
    {
        GAMMA, POTION
    }
}

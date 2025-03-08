package us.nebula.impl.cheat.render;

import us.nebula.api.listener.EventListener;
import us.nebula.api.listener.Subscribe;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.api.value.Setting;
import us.nebula.impl.event.render.EventGamma;

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

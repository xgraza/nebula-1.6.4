package us.nebula.impl.cheat.render;

import us.nebula.api.listener.EventListener;
import us.nebula.api.listener.Subscribe;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.impl.event.render.EventGamma;

/**
 * @author xgraza
 * @since 02/16/25
 */
@CheatManifest(name = "Fullbright", category = CheatCategory.RENDER)
public final class FullbrightCheat extends Cheat
{
    @Subscribe
    private final EventListener<EventGamma> gammaEventListener = event
            -> event.setGamma(1.0f);
}

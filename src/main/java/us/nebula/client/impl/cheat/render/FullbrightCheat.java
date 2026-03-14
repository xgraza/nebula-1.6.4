package us.nebula.client.impl.cheat.render;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import us.nebula.client.api.listener.EventListener;
import us.nebula.client.api.listener.Subscribe;
import us.nebula.client.api.manager.cheat.Cheat;
import us.nebula.client.api.manager.cheat.CheatCategory;
import us.nebula.client.api.manager.cheat.CheatInstance;
import us.nebula.client.api.manager.cheat.CheatManifest;
import us.nebula.client.api.value.Setting;
import us.nebula.client.impl.event.game.EventUpdate;
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
    @CheatInstance
    public static FullbrightCheat INSTANCE;

    private static final PotionEffect FAKE_NIGHT_VISION_EFFECT = new PotionEffect(
            Potion.nightVision.id, 9999, 67, true);

    static
    {
        FAKE_NIGHT_VISION_EFFECT.setPotionDurationMax(true);
    }

    public final Setting<Mode> modeSetting = new Setting<>("Mode", Mode.GAMMA);

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        if (modeSetting.getValue() == Mode.POTION)
        {
            if (MC.thePlayer.isPotionActive(Potion.nightVision))
            {
                return;
            }
            MC.thePlayer.addPotionEffect(FAKE_NIGHT_VISION_EFFECT);
        } else
        {
            if (!MC.thePlayer.isPotionActive(Potion.nightVision))
            {
                return;
            }
            // we only want to remove our effect, not if the player actually gets it
            final PotionEffect effect = MC.thePlayer.getActivePotionEffect(Potion.nightVision);
            if (effect.getAmplifier() == 67)
            {
                MC.thePlayer.removePotionEffect(effect.getPotionID());
            }
        }
    };

    @Subscribe
    private final EventListener<EventGamma> gammaEventListener = event ->
    {
        if (modeSetting.getValue() != Mode.GAMMA)
        {
            return;
        }
        event.setGamma(100.0f);
    };

    public enum Mode
    {
        GAMMA, POTION
    }
}

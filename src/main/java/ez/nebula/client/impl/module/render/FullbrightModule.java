package ez.nebula.client.impl.module.render;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleInstance;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.listener.event.game.EventUpdate;
import ez.nebula.client.api.listener.event.render.EventGamma;
import ez.nebula.client.api.setting.Setting;

/**
 * @author xgraza
 * @since 02/16/25
 */
@ModuleManifest(name = "Fullbright",
        description = "Forces your gamma setting all the way up to aid seeing in the dark",
        category = ModuleCategory.RENDER)
public final class FullbrightModule extends Module
{
    @ModuleInstance
    public static FullbrightModule INSTANCE;

    private static final PotionEffect FAKE_NIGHT_VISION_EFFECT = new PotionEffect(
            Potion.nightVision.id, 9999, 67, true);

    static
    {
        FAKE_NIGHT_VISION_EFFECT.setPotionDurationMax(true);
    }

    public final Setting<Mode> modeSetting = enumBuilder("Mode", Mode.GAMMA)
            .setDescription("How to brighten the world")
            .build();

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

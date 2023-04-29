package lol.nebula.module.visual;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.entity.EventUpdate;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

/**
 * @author aesthetical
 * @since 04/28/23
 */
public class Fullbright extends Module {

    // if we have given the fake night vision effect
    private boolean fakePotion;

    public Fullbright() {
        super("Fullbright", "Forces your game to be bright", ModuleCategory.VISUAL);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if (fakePotion && mc.thePlayer != null && mc.thePlayer.isPotionActive(Potion.nightVision)) {
            mc.thePlayer.removePotionEffect(Potion.nightVision.getId());
        }
    }

    @Listener
    public void onUpdate(EventUpdate event) {
        if (!mc.thePlayer.isPotionActive(Potion.nightVision)) {
            fakePotion = true;

            PotionEffect effect = new PotionEffect(Potion.nightVision.getId(), Integer.MAX_VALUE, 0);
            effect.setPotionDurationMax(true); // force potion to be infinite

            // add potion to player
            mc.thePlayer.addPotionEffect(effect);
        }
    }
}

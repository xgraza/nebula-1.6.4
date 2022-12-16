package wtf.nebula.client.impl.module.visuals;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.impl.event.impl.client.EventTick;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;

public class Fullbright extends ToggleableModule {
    private final Property<Mode> mode = new Property<>(Mode.GAMMA, "Mode", "m", "type");

    private float[] oldLightMap = null;
    private float oldGamma = -1.0f;
    private boolean gavePotion = false;

    public Fullbright() {
        super("Full Bright", new String[]{"fullbright", "brightness"}, ModuleCategory.VISUALS);
        offerProperties(mode);
    }

    @Override
    protected void onDisable() {
        super.onDisable();

        if (!isNull()) {
            if (oldLightMap != null) {
                mc.theWorld.provider.lightBrightnessTable = oldLightMap;
                oldLightMap = null;
            }

            if (gavePotion) {
                mc.thePlayer.removePotionEffect(Potion.nightVision.id);
                gavePotion = false;
            }
        }

        if (oldGamma != -1.0f) {
            mc.gameSettings.gammaSetting = oldGamma;
            oldGamma = -1.0f;
        }
    }

    @EventListener
    public void onTick(EventTick event) {
        lightmap();
        gamma();
        potion();
    }

    private void lightmap() {
        if (mode.getValue().equals(Mode.LIGHTMAP)) {
            if (oldLightMap == null) {
                oldLightMap = mc.theWorld.provider.lightBrightnessTable;
            }

            for (int i = 0; i < 16; ++i) {
                mc.theWorld.provider.lightBrightnessTable[i] = 1.0f;
            }
        } else {
            if (oldLightMap != null) {
                mc.theWorld.provider.lightBrightnessTable = oldLightMap;
                oldLightMap = null;
            }
        }
    }

    private void gamma() {
        if (mode.getValue().equals(Mode.GAMMA)) {
            if (oldGamma == -1.0f) {
                oldGamma = mc.gameSettings.gammaSetting;
            }

            mc.gameSettings.gammaSetting = 1000.0f;
        } else {
            if (oldGamma != -1.0f) {
                mc.gameSettings.gammaSetting = oldGamma;
                oldGamma = -1.0f;
            }
        }
    }

    private void potion() {
        if (mode.getValue().equals(Mode.POTION)) {
            if (!mc.thePlayer.isPotionActive(Potion.nightVision)) {
                gavePotion = true;
                mc.thePlayer.addPotionEffect(new PotionEffect(Potion.nightVision.id, 1, Integer.MAX_VALUE));
            }
        } else {
            if (gavePotion) {
                mc.thePlayer.removePotionEffect(Potion.nightVision.id);
                gavePotion = false;
            }
        }
    }

    public enum Mode {
        GAMMA, POTION, LIGHTMAP
    }
}

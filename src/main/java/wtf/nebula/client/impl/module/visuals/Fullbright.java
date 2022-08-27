package wtf.nebula.client.impl.module.visuals;

import me.bush.eventbus.annotation.EventListener;
import wtf.nebula.client.impl.event.impl.client.TickEvent;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;

public class Fullbright extends ToggleableModule {
    public Fullbright() {
        super("Full Bright", new String[]{"fullbright", "brightness"}, ModuleCategory.VISUALS);
    }

    private float[] oldLightMap;

    @Override
    protected void onDisable() {
        super.onDisable();

        if (!isNull()) {
            mc.theWorld.provider.lightBrightnessTable = oldLightMap;
            oldLightMap = null;
        }
    }

    @EventListener
    public void onTick(TickEvent event) {
        if (oldLightMap == null) {
            oldLightMap = mc.theWorld.provider.lightBrightnessTable;
        } else {
            for (int i = 0; i < 16; ++i) {
                mc.theWorld.provider.lightBrightnessTable[i] = 1.0f;
            }
        }
    }
}

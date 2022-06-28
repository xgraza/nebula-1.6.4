package wtf.nebula.impl.module.render;

import me.bush.eventbus.annotation.EventListener;
import wtf.nebula.event.TickEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;

public class Fullbright extends Module {
    public Fullbright() {
        super("Fullbright", ModuleCategory.RENDER);
    }

    private float[] brightnessMap = new float[16];
    private boolean filled = false;

    @Override
    protected void onDeactivated() {
        super.onDeactivated();

        if (!nullCheck()) {
            System.arraycopy(brightnessMap, 0, mc.theWorld.provider.lightBrightnessTable, 0, brightnessMap.length);
        }

        filled = false;
        brightnessMap = new float[16];
    }

    @EventListener
    public void onTick(TickEvent event) {
        if (!filled) {
            filled = true;
            System.arraycopy(mc.theWorld.provider.lightBrightnessTable, 0, brightnessMap, 0, brightnessMap.length);
        }

        for (int i = 0; i < brightnessMap.length; ++i) {
            mc.theWorld.provider.lightBrightnessTable[i] = 1.0f;
        }
    }
}

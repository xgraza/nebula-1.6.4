package wtf.nebula.impl.module.render;

import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;

public class Fullbright extends Module {
    public Fullbright() {
        super("Fullbright", ModuleCategory.RENDER);
    }

    private float[] brightnessMap = new float[20];

    @Override
    protected void onActivated() {
        super.onActivated();

    }

    @Override
    protected void onDeactivated() {
        super.onDeactivated();
        brightnessMap = new float[20];
    }
}

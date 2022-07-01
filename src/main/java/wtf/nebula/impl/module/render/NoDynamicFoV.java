package wtf.nebula.impl.module.render;

import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;

// Hooks into EntityPlayerSP#getFOVMultiplier()float
public class NoDynamicFoV extends Module {
    public NoDynamicFoV() {
        super("NoDynamicFoV", ModuleCategory.RENDER);
    }
}

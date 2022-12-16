package wtf.nebula.client.impl.module.active;

import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;

public class Capes extends ToggleableModule {
    public Capes() {
        super("Capes", new String[]{"showcapes"}, ModuleCategory.ACTIVE);

        setRunning(true);
        setDrawn(false);
    }
}

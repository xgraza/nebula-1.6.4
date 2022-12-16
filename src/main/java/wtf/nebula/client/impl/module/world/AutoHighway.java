package wtf.nebula.client.impl.module.world;

import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;

public class AutoHighway extends ToggleableModule {


    public AutoHighway() {
        super("Auto Highway", new String[]{"autohighway", "highwaybuilder", "highwaypaver"}, ModuleCategory.WORLD);
    }
}

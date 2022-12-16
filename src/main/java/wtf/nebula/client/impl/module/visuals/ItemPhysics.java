package wtf.nebula.client.impl.module.visuals;

import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;

public class ItemPhysics extends ToggleableModule {
    public ItemPhysics() {
        super("Item Physics", new String[]{"itemphysics", "dropphysics"}, ModuleCategory.VISUALS);
    }
}

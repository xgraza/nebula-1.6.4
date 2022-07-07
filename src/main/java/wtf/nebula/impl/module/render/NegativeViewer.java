package wtf.nebula.impl.module.render;

import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.impl.value.Value;

public class NegativeViewer extends Module {
    public NegativeViewer() {
        super("NegativeViewer", ModuleCategory.RENDER);
    }

    public final Value<Boolean> simple = new Value<>("Simple", false);
}

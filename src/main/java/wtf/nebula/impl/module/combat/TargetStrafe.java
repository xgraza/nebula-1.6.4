package wtf.nebula.impl.module.combat;

import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.impl.value.Value;

public class TargetStrafe extends Module {
    public TargetStrafe() {
        super("TargetStrafe", ModuleCategory.COMBAT);
    }

    public Value<Double> range = new Value<>("Range", 4.0, 1.0, 6.0);
}

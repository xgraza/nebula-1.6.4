package wtf.nebula.impl.module.movement;

import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.impl.value.Value;

public class Chase extends Module {
    public Chase() {
        super("Chase", ModuleCategory.MOVEMENT);
    }

    public final Value<Double> range = new Value<>("OutOfRange", 3.0, 1.0, 5.0);
}

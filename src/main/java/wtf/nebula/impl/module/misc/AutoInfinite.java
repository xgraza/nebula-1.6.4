package wtf.nebula.impl.module.misc;

import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;

public class AutoInfinite extends Module {
    public AutoInfinite() {
        super("AutoInfinite", ModuleCategory.MISC);
    }

    private int toMakeInf;

    @Override
    protected void onActivated() {
        super.onActivated();

        if (nullCheck()) {
            setState(false);
            return;
        }


    }
}

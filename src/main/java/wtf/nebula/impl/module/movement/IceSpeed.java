package wtf.nebula.impl.module.movement;

import net.minecraft.src.Block;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;

public class IceSpeed extends Module {
    public IceSpeed() {
        super("IceSpeed", ModuleCategory.MOVEMENT);
    }

    @Override
    protected void onActivated() {
        super.onActivated();

        Block.ice.slipperiness = 0.4f;
    }

    @Override
    protected void onDeactivated() {
        super.onDeactivated();

        Block.ice.slipperiness = 0.98f;
    }
}

package wtf.nebula.impl.module.movement;

import net.minecraft.init.Blocks;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;

public class IceSpeed extends Module {
    public IceSpeed() {
        super("IceSpeed", ModuleCategory.MOVEMENT);
    }

    @Override
    protected void onActivated() {
        super.onActivated();

        Blocks.ice.slipperiness = 0.4f;
        Blocks.packed_ice.slipperiness = 0.4f;
    }

    @Override
    protected void onDeactivated() {
        super.onDeactivated();

        Blocks.ice.slipperiness = 0.98f;
        Blocks.packed_ice.slipperiness = 0.98f;
    }
}

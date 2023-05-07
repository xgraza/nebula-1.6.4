package lol.nebula.module.movement;

import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import net.minecraft.init.Blocks;

/**
 * @author aesthetical
 * @since 05/07/23
 */
public class IceSpeed extends Module {

    public IceSpeed() {
        super("Ice Speed", "Makes you go faster on ice", ModuleCategory.MOVEMENT);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        Blocks.ice.slipperiness = 0.4f;
        Blocks.packed_ice.slipperiness = 0.4f;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        Blocks.ice.slipperiness = 0.98f;
        Blocks.packed_ice.slipperiness = 0.98f;
    }
}

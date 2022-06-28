package wtf.nebula.impl.module.render;

import net.minecraft.src.Block;
import org.lwjgl.input.Keyboard;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;

import java.util.HashSet;
import java.util.Set;

public class XRay extends Module {
    public final Set<Integer> blocks = new HashSet<Integer>() {{
        add(Block.oreRedstone.blockID);
        add(Block.oreCoal.blockID);
        add(Block.oreDiamond.blockID);
        add(Block.oreIron.blockID);
        add(Block.oreEmerald.blockID);
        add(Block.oreLapis.blockID);
        add(Block.oreGold.blockID);
        add(Block.oreNetherQuartz.blockID);
    }};

    public XRay() {
        super("XRay", ModuleCategory.RENDER);
        setBind(Keyboard.KEY_X);
    }

    @Override
    protected void onActivated() {
        super.onActivated();

        if (nullCheck()) {
            setState(false);
            return;
        }

        mc.renderGlobal.loadRenderers();
    }

    @Override
    protected void onDeactivated() {
        super.onDeactivated();
        mc.renderGlobal.loadRenderers();
    }
}

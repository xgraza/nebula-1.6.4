package lol.nebula.module.world;

import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.util.feature.DevelopmentFeature;

/**
 * @author aesthetical
 * @since 05/31/23
 */
@DevelopmentFeature
public class AutoFish extends Module {

    public AutoFish() {
        super("Auto Fish", "Automatically fishes for you", ModuleCategory.WORLD);
    }
}

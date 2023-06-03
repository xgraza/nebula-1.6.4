package lol.nebula.module.world;

import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.setting.Setting;
import lol.nebula.util.feature.DevelopmentFeature;

/**
 * @author aesthetical
 * @since 06/03/23
 */
@DevelopmentFeature
public class AutoTunnel extends Module {

    private final Setting<Boolean> rotate = new Setting<>(true, "Rotate");
    private final Setting<Boolean> holeFill = new Setting<>(true, "Hole Fill");

    public AutoTunnel() {
        super("Auto Tunnel", "Automatically tunnels for you", ModuleCategory.WORLD);
    }
}

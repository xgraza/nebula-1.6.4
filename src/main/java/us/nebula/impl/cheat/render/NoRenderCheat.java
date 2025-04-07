package us.nebula.impl.cheat.render;

import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatInstance;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.api.value.Setting;

/**
 * @author xgraza
 * @since 03/24/25
 */
@CheatManifest(name = "NoRender",
        description = "Prevents rendering",
        category = CheatCategory.RENDER)
public final class NoRenderCheat extends Cheat
{
    @CheatInstance
    public static NoRenderCheat INSTANCE;

    public final Setting<Boolean> hurtCameraSetting = new Setting<>(
            "Hurt Camera", false);
    public final Setting<Boolean> fogSetting = new Setting<>(
            "Fog", false);
    public final Setting<Boolean> toastsSetting = new Setting<>(
            "Toasts", false);
    public final Setting<Boolean> fireSetting = new Setting<>(
            "Fire", false);
    public final Setting<Boolean> blockSetting = new Setting<>(
            "Blocks", false);
    public final Setting<Boolean> waterSetting = new Setting<>(
            "Water", false);
    public final Setting<Boolean> pumpkinSetting = new Setting<>(
            "Pumpkin", false);
    public final Setting<Boolean> portalSetting = new Setting<>(
            "Portal", false);
    public final Setting<Boolean> voidParticlesSetting = new Setting<>(
            "Void Particles", false);
    public final Setting<Boolean> signTextSetting = new Setting<>(
            "Sign Text", false);
}

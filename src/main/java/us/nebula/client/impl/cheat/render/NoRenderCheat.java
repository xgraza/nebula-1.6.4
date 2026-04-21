package us.nebula.client.impl.cheat.render;

import us.nebula.client.api.listener.EventListener;
import us.nebula.client.api.listener.Subscribe;
import us.nebula.client.api.manager.cheat.Cheat;
import us.nebula.client.api.manager.cheat.CheatCategory;
import us.nebula.client.api.manager.cheat.CheatInstance;
import us.nebula.client.api.manager.cheat.CheatManifest;
import us.nebula.client.api.value.Setting;
import us.nebula.client.impl.event.render.EventRenderWaterEffects;

/**
 * @author xgraza
 * @since 03/24/25
 */
@CheatManifest(name = "NoRender",
        description = "Prevents visual annoyances from rendering",
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
    public final Setting<Boolean> batsSetting = new Setting<>(
            "Bats", false);
    public final Setting<Boolean> weatherSetting = new Setting<>(
            "Weather", false);

    @Subscribe
    private final EventListener<EventRenderWaterEffects> renderWaterEffectsEventListener = event ->
            event.setCanceled(waterSetting.getValue());
}

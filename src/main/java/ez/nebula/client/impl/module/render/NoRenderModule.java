package ez.nebula.client.impl.module.render;

import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleInstance;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.listener.event.render.EventRenderWaterEffects;
import ez.nebula.client.api.setting.Setting;

/**
 * @author xgraza
 * @since 03/24/25
 */
@ModuleManifest(name = "NoRender",
        description = "Prevents visual annoyances from rendering",
        category = ModuleCategory.RENDER)
public final class NoRenderModule extends Module
{
    @ModuleInstance
    public static NoRenderModule INSTANCE;

    public final Setting<Boolean> hurtCameraSetting = builder("Hurt Camera", false)
            .setDescription("If to disable the camera tilt when damage is taken")
            .build();
    public final Setting<Boolean> fogSetting = builder("Fog", false)
            .setDescription("If to disable rendering the fog")
            .build();
    public final Setting<Boolean> toastsSetting = builder("Toasts", false)
            .setDescription("If to disable rendering of Minecraft's default notifications (i.e. advancements earned)")
            .build();
    public final Setting<Boolean> fireSetting = builder("Fire", false)
            .setDescription("If to disable the rendering of the fire overlay when on fire")
            .build();
    public final Setting<Boolean> blockSetting = builder("Blocks", false)
            .setDescription("If to disable the rendering of the block overlay when suffocating")
            .build();
    public final Setting<Boolean> waterSetting = builder("Water", false)
            .setDescription("If to disable the rendering of the blue tint overlay when under water")
            .build();
    public final Setting<Boolean> pumpkinSetting = builder("Pumpkin", false)
            .setDescription("If to disable the rendering a pumpkin head when equipped in the helmet armor slot")
            .build();
    public final Setting<Boolean> portalSetting = builder("Portal", false)
            .setDescription("If to disable the rendering of the portal overlay tint when standing in a nether portal")
            .build();
    public final Setting<Boolean> voidParticlesSetting = builder("Void Particles", false)
            .setDescription("If to disbale the darkening of the screen when nearing the void")
            .build();
    public final Setting<Boolean> signTextSetting = builder("Sign Text", false)
            .setDescription("If to disable rendering text on a sign")
            .build();
    public final Setting<Boolean> batsSetting = builder("Bats", false)
            .setDescription("If to disable rendering of bat entities")
            .build();
    public final Setting<Boolean> weatherSetting = builder("Weather", false)
            .setDescription("If to disable rendering weather effects (i.e. rain, snow, lightning)")
            .build();
    public final Setting<Boolean> nauseaSetting = builder("Nausea", false)
            .setDescription("If to remove the warp camera effect when in a portal or the nausea effect is active")
            .build();

    @Subscribe
    private final EventListener<EventRenderWaterEffects> renderWaterEffectsEventListener = event ->
            event.setCanceled(waterSetting.getValue());
}

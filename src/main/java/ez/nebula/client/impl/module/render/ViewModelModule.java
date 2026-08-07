package ez.nebula.client.impl.module.render;

import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleInstance;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.setting.EnumSetting;
import ez.nebula.client.api.setting.NumberSetting;
import ez.nebula.client.api.setting.Setting;

/**
 * @author xgraza
 * @since 05/27/25
 */
@ModuleManifest(name = "ViewModel",
        description = "Tweaks how the currently held item is rendered",
        category = ModuleCategory.RENDER)
public final class ViewModelModule extends Module
{
    @ModuleInstance
    public static ViewModelModule INSTANCE;

    public final EnumSetting<SwordAnimation> swordAnimationSetting = enumBuilder("Sword Animation", SwordAnimation.VANILLA)
            .setDescription("The animation to use when blocking and swinging a sword")
            .build();
    public final NumberSetting<Integer> swingSpeedSetting = numberBuilder("Swing Speed", 14)
            .setMin(1)
            .setMax(20)
            .setScale(1)
            .setDescription("The speed to swing your held item at")
            .build();

    public final NumberSetting<Double> translateXSetting = numberBuilder("Translate X", 0.0)
            .setMin(-3.0)
            .setMax(3.0)
            .setScale(0.1)
            .setDescription("The offset X position to render your held item at")
            .build();
    public final NumberSetting<Double> translateYSetting = numberBuilder("Translate Y", 0.0)
            .setMin(-3.0)
            .setMax(3.0)
            .setScale(0.1)
            .setDescription("The offset Y position to render your held item at")
            .build();
    public final NumberSetting<Double> translateZSetting = numberBuilder("Translate Z", 0.0)
            .setMin(-3.0)
            .setMax(3.0)
            .setScale(0.1)
            .setDescription("The offset Z position to render your held item at")
            .build();

    public final NumberSetting<Double> scaleXSetting = numberBuilder("Scale X", 1.0)
            .setMin(-3.0)
            .setMax(3.0)
            .setScale(0.1)
            .setDescription("The X offset scale to render your held item at")
            .build();
    public final NumberSetting<Double> scaleYSetting = numberBuilder("Scale Y", 1.0)
            .setMin(-3.0)
            .setMax(3.0)
            .setScale(0.1)
            .setDescription("The Y offset scale to render your held item at")
            .build();
    public final NumberSetting<Double> scaleZSetting = numberBuilder("Scale Z", 1.0)
            .setMin(-3.0)
            .setMax(3.0)
            .setScale(0.1)
            .setDescription("The Z offset scale to render your held item at")
            .build();

    public enum SwordAnimation
    {
        VANILLA,
        _1_8
                {
                    @Override
                    public String toString()
                    {
                        return "1.8";
                    }
                },
        EXHIBITION,
        AVATAR,
        JIGSAW,
        SIGMA,
        TAP,
        FATHUM,
        PULL,
        BONK,
        NEBULA
    }
}

package us.nebula.client.cheat.impl.render;

import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatInstance;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.setting.Setting;

/**
 * @author xgraza
 * @since 05/27/25
 */
@CheatManifest(name = "ViewModel",
        description = "Tweaks how the currently held item is rendered",
        category = CheatCategory.RENDER)
public final class ViewModelCheat extends Cheat
{
    @CheatInstance
    public static ViewModelCheat INSTANCE;

    public final Setting<SwordAnimation> swordAnimationSetting = enumBuilder("Sword Animation", SwordAnimation.VANILLA)
            .setDescription("The animation to use when blocking and swinging a sword")
            .build();
    public final Setting<Integer> swingSpeedSetting = numberBuilder("Swing Speed", 14)
            .setMin(1)
            .setMax(20)
            .setScale(1)
            .setDescription("The speed to swing your held item at")
            .build();

    public final Setting<Double> translateXSetting = numberBuilder("Translate X", 0.0)
            .setMin(-3.0)
            .setMax(3.0)
            .setScale(0.1)
            .setDescription("The offset X position to render your held item at")
            .build();
    public final Setting<Double> translateYSetting = numberBuilder("Translate Y", 0.0)
            .setMin(-3.0)
            .setMax(3.0)
            .setScale(0.1)
            .setDescription("The offset Y position to render your held item at")
            .build();
    public final Setting<Double> translateZSetting = numberBuilder("Translate Z", 0.0)
            .setMin(-3.0)
            .setMax(3.0)
            .setScale(0.1)
            .setDescription("The offset Z position to render your held item at")
            .build();

    public final Setting<Double> scaleXSetting = numberBuilder("Scale X", 1.0)
            .setMin(-3.0)
            .setMax(3.0)
            .setScale(0.1)
            .setDescription("The X offset scale to render your held item at")
            .build();
    public final Setting<Double> scaleYSetting = numberBuilder("Scale Y", 1.0)
            .setMin(-3.0)
            .setMax(3.0)
            .setScale(0.1)
            .setDescription("The Y offset scale to render your held item at")
            .build();
    public final Setting<Double> scaleZSetting = numberBuilder("Scale Z", 1.0)
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
        AVATAR
    }
}

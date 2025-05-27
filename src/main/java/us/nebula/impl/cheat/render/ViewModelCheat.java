package us.nebula.impl.cheat.render;

import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatInstance;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.api.value.Setting;

/**
 * @author xgraza
 * @since 05/27/25
 */
@CheatManifest(name = "ViewModel",
        description = "Tweaks how the held item is rendered",
        category = CheatCategory.RENDER)
public final class ViewModelCheat extends Cheat
{
    @CheatInstance
    public static ViewModelCheat INSTANCE;

    public final Setting<SwordAnimation> swordAnimationSetting = new Setting<>(
            "Sword Animation", SwordAnimation.VANILLA);
    public final Setting<Integer> swingSpeedSetting = new Setting<>(
            "Swing Speed", 14, 1, 20, 1);

    public final Setting<Double> translateXSetting = new Setting<>(
            "Translate X", 0.0, -3.0, 3.0, 0.1);
    public final Setting<Double> translateYSetting = new Setting<>(
            "Translate Y", 0.0, -3.0, 3.0, 0.1);
    public final Setting<Double> translateZSetting = new Setting<>(
            "Translate Z", 0.0, -3.0, 3.0, 0.1);

    public final Setting<Double> scaleXSetting = new Setting<>(
            "Scale X", 1.0, -3.0, 3.0, 0.1);
    public final Setting<Double> scaleYSetting = new Setting<>(
            "Scale Y", 1.0, -3.0, 3.0, 0.1);
    public final Setting<Double> scaleZSetting = new Setting<>(
            "Scale Z", 1.0, -3.0, 3.0, 0.1);

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
                }
    }
}

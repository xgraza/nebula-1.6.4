package us.nebula.impl.gui.overlay;

import net.minecraft.client.gui.ScaledResolution;
import us.nebula.Nebula;
import us.nebula.api.gui.animation.Animation;
import us.nebula.api.gui.animation.AnimationEasing;
import us.nebula.api.gui.font.Fonts;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.overlay.Overlay;
import us.nebula.api.manager.overlay.OverlayManifest;
import us.nebula.api.manager.overlay.StaticPosition;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author xgraza
 * @since 03/10/25
 */
@StaticPosition
@OverlayManifest("Arraylist")
public final class ArraylistOverlay extends Overlay
{
    private static final Map<Cheat, Animation> CHEAT_ANIMATION_MAP = new HashMap<>();
    private static final double PADDING = 1.5;

    @Override
    public void render(final ScaledResolution resolution, final float partialTicks)
    {
        final double screenBoundsX = resolution.getScaledWidth_double() - PADDING;

        double posY = PADDING;

        final List<Cheat> activeCheats = getActiveCheats();
        for (final Cheat cheat : activeCheats)
        {
            final Animation animation = getAnimation(cheat);
            if (animation.getState() != cheat.isToggled())
            {
                animation.setState(cheat.isToggled());
                animation.reset(false);
            }
            final String display = getCheatDisplay(cheat);
            final double factor = animation.getEasedFactor();
            Fonts.POPPINS.drawStringShadow(display,
                    screenBoundsX - (Fonts.POPPINS.getStringWidth(display) * factor),
                    posY,
                    -1);
            posY += (Fonts.POPPINS.getFontHeight() + PADDING) * factor;
        }
    }

    private Animation getAnimation(final Cheat cheat)
    {
        return CHEAT_ANIMATION_MAP.computeIfAbsent(cheat, (x) ->
                new Animation(AnimationEasing.CUBIC_IN_OUT, 250));
    }

    private List<Cheat> getActiveCheats()
    {
        return Nebula.INSTANCE.getCheatManager().getAll()
                .stream()
                .filter((cheat) -> !cheat.isHidden()
                        && (cheat.isToggled() || getAnimation(cheat).getFactor() > 0.0))
                .sorted(Comparator.comparingDouble((cheat) ->
                        -Fonts.POPPINS.getStringWidth(getCheatDisplay(cheat))))
                .collect(Collectors.toList());
    }

    private String getCheatDisplay(final Cheat cheat)
    {
        return cheat.getManifest().name();
    }
}

package us.nebula.client.impl.gui.overlay;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;
import us.nebula.client.Nebula;
import us.nebula.client.api.gui.animation.Animation;
import us.nebula.client.api.gui.animation.AnimationEasing;
import us.nebula.client.api.gui.font.Fonts;
import us.nebula.client.api.manager.cheat.Cheat;
import us.nebula.client.api.manager.overlay.Overlay;
import us.nebula.client.api.manager.overlay.OverlayManifest;
import us.nebula.client.api.manager.overlay.StaticPosition;
import us.nebula.client.impl.cheat.render.HUDCheat;

import java.util.*;
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

        final List<Cheat> activeCheats = Nebula.INSTANCE.getCheatManager().getAll()
                .stream()
                .filter((cheat) -> !cheat.isHidden()
                        && (cheat.isToggled() || getAnimation(cheat).getFactor() > 0.0))
                .collect(Collectors.toList());
        final Map<String, Cheat> cheatMetaMap = new TreeMap<>(Comparator.comparingDouble(
                (text) -> -Fonts.POPPINS.getStringWidth(text)));
        for (final Cheat cheat : activeCheats)
        {
            final StringBuilder builder = new StringBuilder();
            builder.append(cheat.getManifest().name());
            final String metadata = cheat.getMetadata();
            if (metadata != null && !metadata.isEmpty())
            {
                builder.append(" ");
                builder.append(EnumChatFormatting.GRAY);
                builder.append(metadata);
                builder.append(EnumChatFormatting.RESET);
            }
            cheatMetaMap.put(builder.toString(), cheat);
        }

        int i = 0;
        for (final String display : cheatMetaMap.keySet())
        {
            final Cheat cheat = cheatMetaMap.get(display);
            final Animation animation = getAnimation(cheat);
            if (animation.getState() != cheat.isToggled())
            {
                animation.setState(cheat.isToggled());
                animation.reset(false);
            }
            final double factor = animation.getEasedFactor();
            Fonts.POPPINS.drawStringShadow(display,
                    screenBoundsX - (Fonts.POPPINS.getStringWidth(display) * factor) - PADDING,
                    posY,
                    HUDCheat.INSTANCE.getBaseColor(i * 10));
            posY += (Fonts.POPPINS.getFontHeight() + PADDING) * factor;
            ++i;
        }
    }

    private Animation getAnimation(final Cheat cheat)
    {
        return CHEAT_ANIMATION_MAP.computeIfAbsent(cheat, (x) ->
                new Animation(AnimationEasing.CUBIC_IN_OUT, 250));
    }
}

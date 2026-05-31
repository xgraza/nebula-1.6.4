package us.nebula.client.hud.impl;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;
import us.nebula.client.Nebula;
import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.impl.render.HUDCheat;
import us.nebula.client.hud.HUDElement;
import us.nebula.client.hud.HUDManifest;
import us.nebula.client.setting.Setting;
import us.nebula.client.util.render.gui.animation.Animation;
import us.nebula.client.util.render.gui.animation.AnimationEasing;
import us.nebula.client.util.render.gui.font.Fonts;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author xgraza
 * @since 5/16/26
 * Warning! Shit code!
 */
@HUDManifest(name = "Arraylist",
        description = "Shows a cascading list of all enabled & shown cheats",
        width = 50, height = 50)
public final class ArraylistHUDElement extends HUDElement
{
    private final Setting<Boolean> showMetadataSetting = builder("Show Metadata", true)
            .setDescription("If to show cheat metadata")
            .build();

    private final Map<Cheat, Animation> cheatAnimationMap = new HashMap<>();

    @Override
    public void onDisable()
    {
        super.onDisable();
        cheatAnimationMap.clear();
    }

    @Override
    public void render(ScaledResolution res)
    {
        Nebula.INSTANCE.getCheatManager().getAll().forEach((cheat) ->
        {
            final Animation animation = cheatAnimationMap.computeIfAbsent(cheat,
                    (__) -> new Animation(AnimationEasing.EXPO_IN_OUT, 350));
            animation.setState(cheat.isToggled() && !cheat.isHidden());
            animation.getEasedFactor();
        });

        final Quadrant quadrant = getQuadrant(res);
        final List<Cheat> displayList = getListedCheats(quadrant);
        if (displayList.isEmpty())
        {
            return;
        }

        final double originX = quadrant == Quadrant.TOP_RIGHT || quadrant == Quadrant.BOTTOM_RIGHT
                ? getX() + getWidth() - (getPadding() * 2)
                : getX() + getPadding();
        double posY = quadrant == Quadrant.TOP_LEFT || quadrant == Quadrant.TOP_RIGHT
                ? getY() - Fonts.POPPINS.getFontHeight()
                : getY() + getHeight();

        for (int i = 0; i < displayList.size(); ++i)
        {
            final int index = quadrant == Quadrant.TOP_LEFT || quadrant == Quadrant.TOP_RIGHT ?
                    i :
                    displayList.size() - 1 - i;
            final Cheat cheat = displayList.get(index);
            final String display = getCheatDisplay(cheat);

            final double animationFactor = cheatAnimationMap.get(cheat).getFactor();
            final double textWidth = Fonts.POPPINS.getStringWidth(display);

            double posX;
            if (quadrant == Quadrant.TOP_LEFT || quadrant == Quadrant.BOTTOM_LEFT)
            {
                posX = originX - textWidth + (textWidth * animationFactor);
            } else
            {
                posX = originX - (textWidth * animationFactor);
            }

            if (quadrant == Quadrant.TOP_LEFT || quadrant == Quadrant.TOP_RIGHT)
            {
                posY += Fonts.POPPINS.getFontHeight() * animationFactor;
            } else
            {
                posY -= Fonts.POPPINS.getFontHeight() * animationFactor;
            }

            Fonts.POPPINS.drawStringShadow(display, posX, posY, HUDCheat.INSTANCE.getBaseColor(i * 10));
        }
    }

    private List<Cheat> getListedCheats(final Quadrant quadrant)
    {
        return Nebula.INSTANCE.getCheatManager().getAll()
                .stream()
                .filter((cheat) -> (cheat.isToggled() && !cheat.isHidden())
                        || cheatAnimationMap.get(cheat).getFactor() > 0.0)
                .sorted(Comparator.comparingDouble((cheat) ->
                {
                    final double textWidth = Fonts.POPPINS.getStringWidth(getCheatDisplay(cheat));
                    return quadrant == Quadrant.BOTTOM_LEFT || quadrant == Quadrant.BOTTOM_RIGHT ? textWidth : -textWidth;
                })).collect(Collectors.toList());
    }

    private String getCheatDisplay(final Cheat cheat)
    {
        final String name = cheat.getManifest().name();
        if (!showMetadataSetting.getValue())
        {
            return name;
        }
        final String meta = cheat.getMetadata();
        return meta == null || meta.isEmpty() ? name : name + " " + EnumChatFormatting.GRAY + meta;
    }

    private Quadrant getQuadrant(final ScaledResolution resolution)
    {
        double halfWidth = resolution.getScaledWidth() / 2.0;
        double halfHeight = resolution.getScaledHeight() / 2.0;
        double posX = x + (getWidth() / 2.0);
        double posY = y + (getHeight() / 2.0);

        if (posY < halfHeight)
        {
            return posX > halfWidth ? Quadrant.TOP_RIGHT : Quadrant.TOP_LEFT;
        } else
        {
            return posX > halfWidth ? Quadrant.BOTTOM_RIGHT : Quadrant.BOTTOM_LEFT;
        }
    }

    private enum Quadrant
    {
        TOP_RIGHT, TOP_LEFT, BOTTOM_RIGHT, BOTTOM_LEFT
    }
}

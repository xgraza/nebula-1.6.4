package ez.nebula.client.impl.hud;

import ez.nebula.client.Nebula;
import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.impl.module.render.HUDModule;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;
import ez.nebula.client.api.manager.hud.HUDElement;
import ez.nebula.client.api.manager.hud.trait.HUDManifest;
import ez.nebula.client.api.setting.Setting;
import ez.nebula.client.util.render.animation.Animation;
import ez.nebula.client.util.render.animation.AnimationEasing;
import ez.nebula.client.util.render.font.Fonts;

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
        description = "Shows a cascading list of all enabled & shown modules",
        width = 50, height = 50)
public final class ArraylistHUDElement extends HUDElement
{
    private final Setting<Boolean> showMetadataSetting = builder("Show Metadata", true)
            .setDescription("If to show module metadata")
            .build();

    private final Map<Module, Animation> moduleAnimationHashMap = new HashMap<>();

    @Override
    public void onDisable()
    {
        super.onDisable();
        moduleAnimationHashMap.clear();
    }

    @Override
    public void render(ScaledResolution res)
    {
        Nebula.MODULES.getAll().forEach((module) ->
        {
            final Animation animation = moduleAnimationHashMap.computeIfAbsent(module,
                    (__) -> new Animation(AnimationEasing.EXPO_IN_OUT, 350));
            animation.setState(module.isToggled() && !module.isHidden());
            animation.getEasedFactor();
        });

        final Quadrant quadrant = getQuadrant(res);
        final List<Module> displayList = getListedModules(quadrant);
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
            final Module module = displayList.get(index);
            final String display = getModuleDisplay(module);

            final double animationFactor = moduleAnimationHashMap.get(module).getFactor();
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

            Fonts.POPPINS.drawStringShadow(display, posX, posY, HUDModule.INSTANCE.getBaseColor(i * 10));
        }
    }

    private List<Module> getListedModules(final Quadrant quadrant)
    {
        return Nebula.MODULES.getAll()
                .stream()
                .filter((module) -> (module.isToggled() && !module.isHidden())
                        || moduleAnimationHashMap.get(module).getFactor() > 0.0)
                .sorted(Comparator.comparingDouble((module) ->
                {
                    final double textWidth = Fonts.POPPINS.getStringWidth(getModuleDisplay(module));
                    return quadrant == Quadrant.BOTTOM_LEFT || quadrant == Quadrant.BOTTOM_RIGHT ? textWidth : -textWidth;
                })).collect(Collectors.toList());
    }

    private String getModuleDisplay(final Module module)
    {
        final String name = module.getManifest().name();
        if (!showMetadataSetting.getValue())
        {
            return name;
        }
        final String meta = module.getMetadata();
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

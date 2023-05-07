package lol.nebula.ui.click.component.module;

import lol.nebula.bind.Bind;
import lol.nebula.module.Module;
import lol.nebula.module.visual.Interface;
import lol.nebula.setting.Setting;
import lol.nebula.ui.click.component.Component;
import lol.nebula.ui.click.component.module.setting.*;
import lol.nebula.util.render.RenderUtils;
import lol.nebula.util.render.animation.Animation;
import lol.nebula.util.render.animation.Easing;
import lol.nebula.util.render.font.Fonts;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author aesthetical
 * @since 04/27/23
 */
public class ModuleComponent extends Component {

    private static final ResourceLocation ARROW = new ResourceLocation("nebula/textures/click/arrow.png");
    public static final Color UNTOGGLED_BG = new Color(35, 35, 35);

    private final Animation openAnimation = new Animation(Easing.CUBIC_IN_OUT, 500, false);
    private final Animation hoverAnimation = new Animation(Easing.CUBIC_IN_OUT, 200, false);

    private final Module module;
    private boolean expanded;

    private float arrowAnimation = 180.0f;

    public ModuleComponent(Module module) {
        this.module = module;

        for (Setting<?> setting : module.getSettings()) {
            if (setting.getValue() instanceof Boolean) {
                getChildren().add(new BooleanSettingComponent((Setting<Boolean>) setting));
            } else if (setting.getValue() instanceof Enum<?>) {
                getChildren().add(new EnumSettingComponent((Setting<Enum<?>>) setting));
            } else if (setting.getValue() instanceof Number) {
                getChildren().add(new NumberSettingComponent((Setting<Number>) setting));
            } else if (setting.getValue() instanceof Bind) {
                getChildren().add(new BindSettingComponent((Setting<Bind>) setting));
            } else if (setting.getValue() instanceof Color) {
                getChildren().add(new ColorSettingComponent((Setting<Color>) setting));
            }
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        if (openAnimation.getState() != expanded) openAnimation.setState(expanded);

        boolean bounds = isInBounds(mouseX, mouseY, getX(), getY(), getWidth(), super.getHeight());
        if (hoverAnimation.getState() != bounds) hoverAnimation.setState(bounds);

        RenderUtils.rect(getX(), getY(), getWidth(), super.getHeight(), (module.isToggled() ? Interface.color.getValue() : UNTOGGLED_BG).getRGB());
        Fonts.axiforma.drawStringWithShadow(
                module.getTag(),
                (float) (getX() + 1.0 + (2.0 * hoverAnimation.getFactor())),
                (float) (getY() + (super.getHeight() / 2.0) - (Fonts.axiforma.FONT_HEIGHT / 2.0)),
                -1);

        if (!module.getSettings().isEmpty()) {
            arrowAnimation = (float) ((expanded ? -180.0f : 180.0f) * openAnimation.getFactor());

            glPushMatrix();
            glEnable(GL_TEXTURE_2D);

            RenderUtils.setColor(0xFFFFFFFF);
            glTranslated(((getX() + getWidth()) - 12.0f) + 5, getY() + (super.getHeight() / 2.0) - 5.0 + 5, 0.0f);
            glRotated(arrowAnimation, 0.0f, 0.0f, expanded ? -1.0f : 1.0f);
            mc.getTextureManager().bindTexture(ARROW);
            Gui.func_146110_a(-5, -5, 0, 0, 10, 10, 10.0f, 10.0f);

            glPopMatrix();
        }

        if (openAnimation.getFactor() > 0.0) {
            double y = getY() + super.getHeight() + 1.0;
            for (Component component : getChildren()) {
                component.setX(getX() + 1.0);
                component.setWidth(getWidth() - 2.0);
                component.setY(y);
                component.setHeight(13.5);

                component.render(mouseX, mouseY, partialTicks);

                y += component.getHeight();
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isInBounds(mouseX, mouseY)) {
            if (mouseButton == 0) {
                module.setState(!module.isToggled());
            } else if (mouseButton == 1) {
                expanded = !expanded;
            }
        }

        if (expanded) {
            for (Component component : getChildren()) {
                component.mouseClicked(mouseX, mouseY, mouseButton);
            }
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (expanded) {
            for (Component component : getChildren()) {
                component.keyTyped(typedChar, keyCode);
            }
        }
    }

    @Override
    public double getHeight() {
        double openedHeight = 2.0;
        for (Component component : getChildren()) {
            openedHeight += component.getHeight();
        }

        return super.getHeight() + (openedHeight * openAnimation.getFactor());
    }

    public Module getModule() {
        return module;
    }
}

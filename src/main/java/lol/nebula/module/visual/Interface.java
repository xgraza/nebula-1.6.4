package lol.nebula.module.visual;

import lol.nebula.Nebula;
import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.render.gui.overlay.EventRender2D;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.setting.Setting;
import lol.nebula.util.render.ColorUtils;
import lol.nebula.util.render.font.Fonts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static net.minecraft.client.resources.I18n.format;
import static org.lwjgl.opengl.GL11.*;

/**
 * @author aesthetical
 * @since 04/27/23
 */
public class Interface extends Module {
    /**
     * The location of the inventory textures
     * Used for potion effects
     */
    private static final ResourceLocation CONTAINER_LOCATION = new ResourceLocation("textures/gui/container/inventory.png");

    public static final Setting<Color> color = new Setting<>(new Color(162, 108, 222), "Color");
    private final Setting<Colors> colors = new Setting<>(Colors.RAINBOW, "Colors");
    private final Setting<Double> colorSpeed = new Setting<>(
            () -> colors.getValue() == Colors.RAINBOW, 5.0, 0.01, 1.0, 10.0, "Color Speed");
    private final Setting<Float> minBrightness = new Setting<>(
            () -> colors.getValue() == Colors.GRADIENT, 0.65f, 0.01f, 0.1f, 1.0f, "Min Brightness");

    public static final Setting<Boolean> customFont = new Setting<>(true, "Custom Font");
    private final Setting<Boolean> coordinates = new Setting<>(true, "Coordinates");
    private final Setting<Boolean> potions = new Setting<>(true, "Potions");
    private final Setting<Boolean> speed = new Setting<>(true, "Speed");
    private final Setting<Boolean> tps = new Setting<>(true, "TPS");
    private final Setting<Boolean> fps = new Setting<>(true, "FPS");

    public Interface() {
        super("Interface", "Renders an overlay over the vanilla HUD", ModuleCategory.VISUAL);

        // by default, set on
        setState(true);

        // do not draw to array list by default
        setDrawn(false);
    }

    @Listener
    public void onRender2D(EventRender2D event) {

        // if the F3 debug menu is open, do not render over it
        if (mc.gameSettings.showDebugInfo) return;

        Fonts.shadow(Nebula.getFormatted(), 3.0f, 3.0f, getColor(100));

        List<Module> enabled = Nebula.getInstance().getModules().getModules()
                .stream()
                .filter((m) -> m.isDrawn() && (m.isToggled() || m.getAnimation().getFactor() > 0.0))
                .sorted(Comparator.comparingInt((m) -> -Fonts.width(formatModule(m))))
                .collect(Collectors.toList());

        if (!enabled.isEmpty()) {
            double y = 3.0;
            for (int i = 0; i < enabled.size(); ++i) {
                Module module = enabled.get(i);
                String tag = formatModule(module);

                double x = event.getRes().getScaledWidth_double() - 4.0
                        - (Fonts.width(tag) * module.getAnimation().getFactor());

                Fonts.shadow(tag, x, y, getColor(i * 100));

                y += (Fonts.height() + 2) * module.getAnimation().getFactor();
            }
        }

        // render held item and armor
        {
            double x = event.getRes().getScaledWidth_double() / 2.0 + 9.0;
            double y = event.getRes().getScaledHeight() - 56.0;

            if (mc.thePlayer.getHeldItem() != null) {
                glPushMatrix();
                RenderHelper.enableGUIStandardItemLighting();
                RenderItem renderItem = (RenderItem) RenderManager.instance.getEntityClassRenderObject(EntityItem.class);

                renderItem.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), mc.thePlayer.getHeldItem(), (int) x, (int) y);
                renderItem.renderItemOverlayIntoGUI(mc.fontRenderer, mc.getTextureManager(), mc.thePlayer.getHeldItem(), (int) x, (int) y);
                glPopMatrix();

                x += 16.0;
            } else {
                x += 6.0;
            }

            for (int i = 3; i >= 0; --i) {
                ItemStack stack = mc.thePlayer.inventory.armorInventory[i];
                if (stack != null) {
                    glPushMatrix();
                    RenderHelper.enableGUIStandardItemLighting();
                    RenderItem renderItem = (RenderItem) RenderManager.instance.getEntityClassRenderObject(EntityItem.class);

                    renderItem.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), stack, (int) x, (int) y);
                    renderItem.renderItemOverlayIntoGUI(mc.fontRenderer, mc.getTextureManager(), stack, (int) x, (int) y);

                    RenderHelper.disableStandardItemLighting();
                    glPopMatrix();

                    x += 16;
                }
            }
        }

        double yOffset = mc.currentScreen instanceof GuiChat ? 14.0 : 0.0;
        double y = event.getRes().getScaledHeight_double() - 3.0 - Fonts.height() - yOffset;

        // render potion effects
        potionRender: {

            if (!potions.getValue()) break potionRender;

            Collection<PotionEffect> activeEffects = mc.thePlayer.getActivePotionEffects();

            // do not continue if there are no potion effects
            if (activeEffects.isEmpty()) break potionRender;

            for (PotionEffect potionEffect : activeEffects) {
                String formatted = format("%s %s %s",
                        I18n.format(potionEffect.getEffectName()),
                        String.valueOf(potionEffect.getAmplifier() + 1),
                        EnumChatFormatting.GRAY + Potion.getDurationString(potionEffect));
                double x = event.getRes().getScaledWidth_double() - 3.0 - Fonts.width(formatted);
                Potion potion = Potion.potionTypes[potionEffect.getPotionID()];

                Fonts.shadow(formatted, x, y, potion.getLiquidColor());

                if (potion.hasStatusIcon()) {
                    glPushMatrix();
                    glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                    glDisable(GL_LIGHTING);
                    int var9 = potion.getStatusIconIndex();
                    mc.getTextureManager().bindTexture(CONTAINER_LOCATION);
                    glTranslated(x - 2 - (18.0 / 2.0), y - 1, 0.0);
                    glScaled(0.5, 0.5, 0.5);
                    Gui.drawTexturedModalRectX(0, 0, var9 % 8 * 18, 198 + var9 / 8 * 18, 18, 18);
                    glPopMatrix();
                }

                y -= (Fonts.height() + 2.0);
            }
        }

        speedRender: {

            if (!speed.getValue()) break speedRender;

            double diffX = mc.thePlayer.posX - mc.thePlayer.lastTickPosX;
            double diffZ = mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ;

            // thanks father linus
            double speedTraveled = (Math.sqrt(diffX * diffX + diffZ * diffZ) / 1000) / (0.05 / 3600);
            speedTraveled *= mc.timer.timerSpeed;
            speedTraveled /= 3.6;

            String formatted = format("%sSpeed %s%.2f bps",
                    EnumChatFormatting.GRAY,
                    EnumChatFormatting.RESET,
                    speedTraveled);
            double x = event.getRes().getScaledWidth_double() - 3.0 - Fonts.width(formatted);

            Fonts.shadow(formatted, (float) x, (float) y, -1);

            y -= (Fonts.height() + 2.0);

        }

        tpsRender: {

            if (!tps.getValue()) break tpsRender;

            String formatted = format("%sTPS %s%.2f",
                    EnumChatFormatting.GRAY,
                    EnumChatFormatting.RESET,
                    Nebula.getInstance().getTick().getTps());
            double x = event.getRes().getScaledWidth_double() - 3.0 - Fonts.width(formatted);

            Fonts.shadow(formatted, (float) x, (float) y, -1);

            y -= (Fonts.height() + 2.0);

        }

        fpsRender: {

            if (!fps.getValue()) break fpsRender;

            String formatted = format("%sFPS %s%s",
                    EnumChatFormatting.GRAY,
                    EnumChatFormatting.RESET,
                    Minecraft.debugFPS);
            double x = event.getRes().getScaledWidth_double() - 3.0 - Fonts.width(formatted);

            Fonts.shadow(formatted, (float) x, (float) y, -1);

            y -= (Fonts.height() + 2.0);
        }

        if (coordinates.getValue()) {

            Fonts.shadow(
                    "XYZ " + EnumChatFormatting.GRAY +
                            String.format("%.1f", mc.thePlayer.posX) + ", " +
                            String.format("%.1f", mc.thePlayer.boundingBox.minY) + ", " +
                            String.format("%.1f", mc.thePlayer.posZ),
                    3.0f,
                    (float) (event.getRes().getScaledHeight_double() - Fonts.height() - 3.0 - yOffset),
                    color.getValue().getRGB()
            );
        }
    }

    private int getColor(int delay) {
        switch (colors.getValue()) {
            default:
            case STATIC:
                return color.getValue().getRGB();

            case RAINBOW:
                return ColorUtils.rainbowCycle(delay, colorSpeed.getValue());

            case GRADIENT:
                return ColorUtils.gradientRainbow(color.getValue(), minBrightness.getValue(), delay);
        }
    }

    private String formatModule(Module module) {
        String tag = module.getTag();
        if (module.getMetadata() != null) {
            tag += " " + EnumChatFormatting.GRAY + module.getMetadata();
        }
        return tag;
    }

    public enum Colors {
        STATIC, RAINBOW, GRADIENT
    }
}

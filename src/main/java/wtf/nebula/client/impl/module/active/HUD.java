package wtf.nebula.client.impl.module.active;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiPlayerInfo;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.core.Nebula;
import wtf.nebula.client.impl.event.impl.move.EventMotionUpdate;
import wtf.nebula.client.impl.event.impl.render.EventRender2D;
import wtf.nebula.client.impl.module.Module;
import wtf.nebula.client.impl.module.ToggleableModule;
import wtf.nebula.client.impl.module.combat.Aura;
import wtf.nebula.client.utils.render.RenderEngine;

import java.awt.*;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL11.*;

public class HUD extends Module {
    public static String WATERMARK = Nebula.NAME;

    private final Property<Boolean> watermark = new Property<>(true, "Watermark", "name", "displayname");
    private final Property<Boolean> arraylist = new Property<>(true, "Active", "arraylist");
    private final Property<Boolean> armor = new Property<>(true, "Armor", "armorhud");
    private final Property<Counter> speed = new Property<>(Counter.KMH, "Speed", "speedcounter");
    private final Property<Boolean> fps = new Property<>(true, "FPS", "frames", "framespersec");
    private final Property<Boolean> tps = new Property<>(true, "TPS", "tickspersec", "performance");
    private final Property<Boolean> ping = new Property<>(true, "Ping", "latency");
    private final Property<Boolean> potions = new Property<>(true, "Potions", "potionhud");
    private final Property<Boolean> coordinates = new Property<>(true, "Coordinates", "coords");
    private final Property<Boolean> target = new Property<>(false, "Target HUD", "targethud", "target");
    private final Property<Boolean> lag = new Property<>(true, "Lag");

    private double speedTraveled = 0.0;

    public HUD() {
        super("HUD", new String[]{"hud", "overlay", "hudoverlay"});
        offerProperties(watermark, arraylist, armor, speed, fps, tps, ping, potions, coordinates, target, lag);
    }

    @EventListener
    public void onRenderHUD(EventRender2D event) {
        if (!Minecraft.isGuiEnabled() || mc.gameSettings.showDebugInfo) {
            return;
        }

        if (lag.getValue()) {

            if (!Nebula.getInstance().getTickManager().responding) {
                double time = (System.currentTimeMillis() - Nebula.getInstance().getTickManager().lastTick) / 1000.0;
                String text = "Server has been lagging for " + String.format("%.2f", time) + "s ";
                mc.fontRenderer.drawStringWithShadow(text, (int) (event.getResolution().getScaledWidth_double() / 2 - (mc.fontRenderer.getStringWidth(text) / 2)), 20, 11184810);
            }
        }

        if (watermark.getValue()) {
            mc.fontRenderer.drawStringWithShadow(
                    WATERMARK + " " + Nebula.VERSION.getVersionString(),
                    3, 3,
                    Colors.rainbow.getValue() ? Colors.getClientRainbow(0) : Colors.getColorInt());
        }

        if (arraylist.getValue()) {
            List<ToggleableModule> modules = Nebula.getInstance()
                    .getModuleManager().getToggleableModules()
                    .stream()
                    .filter((m) -> m.isRunning() && m.isDrawn())
                    .sorted(Comparator.comparingInt((s) -> -mc.fontRenderer.getStringWidth(s.getInfo())))
                    .collect(Collectors.toList());

            if (!modules.isEmpty()) {
                int width = event.getResolution().getScaledWidth();
                int y = 3;

                int i = 0;
                for (ToggleableModule module : modules) {
                    int textWidth = mc.fontRenderer.getStringWidth(module.getInfo());
                    int color = Colors.rainbow.getValue() ? Colors.getClientRainbow(i * 200) : Colors.getColorInt();

                    mc.fontRenderer.drawStringWithShadow(module.getInfo(), width - textWidth - 3, y, color);
                    y += mc.fontRenderer.FONT_HEIGHT + 1;
                    ++i;
                }
            }
        }

        ScaledResolution res = event.getResolution();

        if (armor.getValue()) {
            for (int i = 0; i < 4; ++i) {
                ItemStack stack = mc.thePlayer.inventory.armorInventory[i];
                if (stack == null || stack.getItem() == null) {
                    continue;
                }

                double x = (res.getScaledWidth() / 2.0) + ((9 - i) * 16) - 80.0;

                double y1 = res.getScaledHeight();
                if (mc.thePlayer.isInsideOfMaterial(Material.water) && !mc.thePlayer.capabilities.isCreativeMode) {
                    y1 -= 65.0;
                } else {
                    if (mc.thePlayer.capabilities.isCreativeMode) {
                        y1 -= 38.0;
                    } else {
                        y1 -= 55.0;
                    }
                }

                glPushMatrix();

                RenderHelper.enableGUIStandardItemLighting();

                RenderItem renderItem = (RenderItem) RenderManager.instance.getEntityClassRenderObject(EntityItem.class);

                renderItem.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), stack, (int) x, (int) y1);
                renderItem.renderItemOverlayIntoGUI(mc.fontRenderer, mc.getTextureManager(), stack, (int) x, (int) y1);

                RenderHelper.disableStandardItemLighting();

                glPopMatrix();
            }
        }

        int baseY = event.getResolution().getScaledHeight() - (mc.currentScreen instanceof GuiChat ? 24 : 10);
        int y = baseY;

        // lower right

        if (potions.getValue()) {
            Collection<PotionEffect> effects = mc.thePlayer.getActivePotionEffects();
            if (!effects.isEmpty()) {
                for (PotionEffect effect : effects) {
                    Potion potion = Potion.potionTypes[effect.getPotionID()];

                    String name = I18n.format(potion.getName());
                    name += String.format(" \u00a77%s : %s", effect.getAmplifier() + 1, Potion.getDurationString(effect));

                    mc.fontRenderer.drawStringWithShadow(name, res.getScaledWidth() - mc.fontRenderer.getStringWidth(name) - 3, y, potion.getLiquidColor());

                    y -= mc.fontRenderer.FONT_HEIGHT + 1;
                }
            }
        }

        if (!speed.getValue().equals(Counter.NONE)) {
            String time = EnumChatFormatting.GRAY + "Speed: " + EnumChatFormatting.RESET + String.format("%.2f", speedTraveled) + " " + speed.getValue().name().toLowerCase();
            mc.fontRenderer.drawStringWithShadow(time, res.getScaledWidth() - mc.fontRenderer.getStringWidth(time) - 4, y, -1);
            y -= mc.fontRenderer.FONT_HEIGHT + 1;
        }

        if (tps.getValue()) {
            double t = Nebula.getInstance().getTickManager().getTps();
            String time = EnumChatFormatting.GRAY + "TPS: " + EnumChatFormatting.RESET + String.format("%.2f", t);
            mc.fontRenderer.drawStringWithShadow(time, res.getScaledWidth() - mc.fontRenderer.getStringWidth(time) - 4, y, -1);
            y -= mc.fontRenderer.FONT_HEIGHT + 1;
        }

        if (ping.getValue()) {
            int l = 0;

            for (GuiPlayerInfo o : mc.thePlayer.sendQueue.playerInfoList) {
                if (o.name.equals(mc.session.getUsername())) {
                    l = o.responseTime;
                    break;
                }
            }

            String time = EnumChatFormatting.GRAY + "Ping: " + EnumChatFormatting.RESET + l + "ms";
            mc.fontRenderer.drawStringWithShadow(time, res.getScaledWidth() - mc.fontRenderer.getStringWidth(time) - 4, y, -1);
            y -= mc.fontRenderer.FONT_HEIGHT + 1;
        }

        if (fps.getValue()) {
            String time = EnumChatFormatting.GRAY + "FPS: " + EnumChatFormatting.RESET + Minecraft.debugFPS;
            mc.fontRenderer.drawStringWithShadow(time, res.getScaledWidth() - mc.fontRenderer.getStringWidth(time) - 4, y, -1);
            y -= mc.fontRenderer.FONT_HEIGHT + 1;
        }

        // lower left
        y = baseY;

        if (coordinates.getValue()) {
            String text = EnumChatFormatting.GRAY + "XYZ: " + EnumChatFormatting.WHITE
                    + String.format("%.1f", mc.thePlayer.posX) + ", "
                    + String.format("%.1f", mc.thePlayer.boundingBox.minY) + ", "
                    + String.format("%.1f", mc.thePlayer.posZ);

            mc.fontRenderer.drawStringWithShadow(text, 3, y, -1);
            y -= mc.fontRenderer.FONT_HEIGHT + 1;
        }

        if (target.getValue()) {
            if (Aura.target == null || !(Aura.target instanceof EntityPlayer)) {
                return;
            }

            EntityPlayer playerTarget = (EntityPlayer) Aura.target;

            ResourceLocation playerSkin = new ResourceLocation("textures/entity/steve.png");
            if (playerTarget instanceof AbstractClientPlayer) {
                playerSkin = ((AbstractClientPlayer) playerTarget).getLocationSkin();
            }

            double posX = (res.getScaledWidth_double() / 2.0) + 30;
            double posY = (res.getScaledHeight() / 2.0) + 30;

            RenderEngine.rectangle(posX, posY, 175.0, 45.0, new Color(50, 50, 50, 175).getRGB());

            glPushMatrix();
            glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

            mc.getTextureManager().bindTexture(playerSkin);
            Gui.drawScaledCustomSizeModalRect((int) (posX + 4), (int) (posY + 4), 200, 8, 8, 8, 37, 37, 64.0F, 32.0F);

            glPopMatrix();

            mc.fontRenderer.drawStringWithShadow(playerTarget.getCommandSenderName(), (int) (posX + 45.0), (int) (posY + 10), -1);

            double barWidth = (175.0 - 4.0) - 45;

            double percent = (playerTarget.getHealth() + playerTarget.getAbsorptionAmount()) / 24.0;
            barWidth *= percent;

            RenderEngine.rectangle(posX + 45.0, posY + 25.0, barWidth, 12, Colors.getClientColor(180)); //new Color(0, 255, 0, 200).getRGB());

            String percentageText = String.format("%.1f", (percent * 100.0)) + "%";
            mc.fontRenderer.drawStringWithShadow(percentageText, (int) (posX + (175.0 / 2.0) - mc.fontRenderer.getStringWidth(percentageText) / 2) + 22, (int) (posY + 28), -1);
        }
    }

    @EventListener
    public void onMotionUpdate(EventMotionUpdate event) {
        double diffX = mc.thePlayer.posX - mc.thePlayer.lastTickPosX;
        double diffZ = mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ;

        speedTraveled = (Math.sqrt(diffX * diffX + diffZ * diffZ) / 1000) / (0.05 / 3600);
        speedTraveled *= mc.timer.timerSpeed;

        if (speed.getValue().equals(Counter.BPS)) {
            speedTraveled /= 3.6;
        }
    }

    public enum Counter {
        NONE, KMH, BPS
    }
}

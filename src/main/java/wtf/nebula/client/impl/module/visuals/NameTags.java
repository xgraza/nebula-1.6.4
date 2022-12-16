package wtf.nebula.client.impl.module.visuals;

import me.bush.eventbus.annotation.EventListener;
import me.bush.eventbus.annotation.ListenerPriority;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiPlayerInfo;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.core.Nebula;
import wtf.nebula.client.impl.event.impl.render.EventRenderVanillaNameTag;
import wtf.nebula.client.impl.event.impl.render.EventRender3D;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;
import wtf.nebula.client.impl.module.world.FreeCamera;
import wtf.nebula.client.utils.client.MathUtils;
import wtf.nebula.client.utils.render.RenderEngine;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

public class NameTags extends ToggleableModule {
    private final Property<Boolean> armor = new Property<>(true, "Armor", "showarmor");
    private final Property<Boolean> held = new Property<>(true, "Held", "helditem");
    private final Property<Boolean> enchantments = new Property<>(true, "Enchantments");
    private final Property<Boolean> ping = new Property<>(true, "Ping", "latency");
    private final Property<Boolean> health = new Property<>(true, "Health", "showhealth");
    private final Property<Boolean> background = new Property<>(false, "Background", "rectangle");
    private final Property<Boolean> outline = new Property<>(true, "Outline", "outlined");
    private final Property<Float> lineWidth = new Property<>(1.5f, 0.1f, 5.0f, "Line Width", "linewidth", "width")
            .setVisibility(outline::getValue);
    private final Property<Double> scaling = new Property<>(0.3, 0.1, 1.0, "Scaling", "scale");

    public NameTags() {
        super("NameTags", new String[]{"headdisplay", "customnametags"}, ModuleCategory.VISUALS);
        offerProperties(armor, held, enchantments, background, outline, lineWidth, scaling);
    }

    @EventListener(priority = ListenerPriority.HIGHEST)
    public void onRenderWorld(EventRender3D event) {
        if (mc.renderViewEntity != null) {
            for (EntityPlayer player : mc.theWorld.playerEntities) {
                if (player.isDead || player.getHealth() <= 0.0f) {
                    continue;
                }

                if (player.equals(mc.thePlayer) && !Nebula.getInstance().getModuleManager().getModule(FreeCamera.class).isRunning()) {
                    continue;
                }

                double x = MathUtils.interpolate(player.posX, player.lastTickPosX, event.getPartialTicks()) - RenderManager.renderPosX;
                double y = MathUtils.interpolate(player.posY, player.lastTickPosY, event.getPartialTicks()) + (player.equals(mc.thePlayer) ? 0.0 : 1.625) - RenderManager.renderPosY;
                double z = MathUtils.interpolate(player.posZ, player.lastTickPosZ, event.getPartialTicks()) - RenderManager.renderPosZ;

                renderNametag(player, x, y, z);
            }
        }
    }

    @EventListener
    public void onRenderEntityNametag(EventRenderVanillaNameTag event) {
        event.setCancelled(true);
    }

    private void renderNametag(EntityPlayer player, double x, double y, double z) {

        double renderX = RenderManager.renderPosX;
        double renderY = RenderManager.renderPosY;
        double renderZ = RenderManager.renderPosZ;

        double distance = mc.renderViewEntity.getDistance(x + renderX, y + renderY, z + renderZ);
        double scale = (scaling.getValue() * Math.max(distance, 4.0)) / 50.0;

        glPushMatrix();

        RenderHelper.enableGUIStandardItemLighting();
        glDisable(GL_LIGHTING);

        glEnable(GL_POLYGON_OFFSET_FILL);
        glPolygonOffset(1.0f, -1500000.0f);

        //glEnable(GL_RESCALE_NORMAL);

        glTranslated(x, y + 0.6, z);
        glRotated(-RenderManager.instance.playerViewY, 0.0f, 1.0f, 0.0f);
        glRotated(RenderManager.instance.playerViewX, mc.gameSettings.thirdPersonView == 2 ? -1.0f : 1.0f, 0.0f, 0.0f);
        glScaled(-scale, -scale, scale);

        //glDisable(GL_DEPTH_TEST);

        String text = getDisplayInfo(player);
        int width = mc.fontRenderer.getStringWidth(text) / 2;

        double rectX = -(width - 2.0);
        double rectY = -(mc.fontRenderer.FONT_HEIGHT - 1.0);
        double rectWidth = (width * 2.0) + 2.0;
        double rectHeight = mc.fontRenderer.FONT_HEIGHT + 2.0;

        if (background.getValue()) {
            RenderEngine.rectangle(rectX, rectY, rectWidth, rectHeight, 0x70000000);
        }

        if (outline.getValue()) {
            glPushMatrix();

            glEnable(GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 0, 1);
            glDisable(GL_TEXTURE_2D);
            glDisable(GL_DEPTH_TEST);

            glEnable(GL_LINE_SMOOTH);
            glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);

            glLineWidth(lineWidth.getValue());

            RenderEngine.color(Color.black.getRGB());

            glBegin(GL_LINES);
            {
                glVertex3d(rectX, rectY, 0.0);
                glVertex3d(rectX + rectWidth, rectY, 0.0);

                glVertex3d(rectX + rectWidth, rectY, 0.0);
                glVertex3d(rectX + rectWidth, rectY + rectHeight, 0.0);

                glVertex3d(rectX + rectWidth, rectY + rectHeight, 0.0);
                glVertex3d(rectX, rectY + rectHeight, 0.0);

                glVertex3d(rectX, rectY + rectHeight, 0.0);
                glVertex3d(rectX, rectY, 0.0);
            }
            glEnd();

            glDisable(GL_BLEND);
            glEnable(GL_TEXTURE_2D);
            glEnable(GL_DEPTH_TEST);

            glLineWidth(1.0f);
            glDisable(GL_LINE_SMOOTH);

            glPopMatrix();
        }

        glDisable(GL_DEPTH_TEST);
        mc.fontRenderer.drawStringWithShadow(text, (int) -(width - 4.0), -(mc.fontRenderer.FONT_HEIGHT - 2), -1);
        glEnable(GL_DEPTH_TEST);

        int xOffset = (-24 / 2 * player.inventory.armorInventory.length) + 12;

        if (held.getValue()) {
            ItemStack stack = player.getHeldItem();
            if (stack != null && stack.getItem() != null) {
                renderItem(stack, xOffset, 1.0);
                xOffset += 16;
            }
        }

        if (armor.getValue()) {
            for (int i = 3; i >= 0; --i) {
                ItemStack stack = player.inventory.armorInventory[i];
                if (stack == null || stack.getItem() == null) {
                    continue;
                }

                renderItem(stack, xOffset, 1.0);
                xOffset += 16;
            }
        }

        glPolygonOffset(1.0f, 1500000.0f);
        glDisable(GL_POLYGON_OFFSET_FILL);

        //glDisable(GL_RESCALE_NORMAL);

        //glEnable(GL_DEPTH_TEST);
        glEnable(GL_LIGHTING);

        RenderHelper.enableStandardItemLighting();

        glEnable(GL_ALPHA_TEST);

        glPopMatrix();
    }

    private void renderItem(ItemStack stack, int x, double scale) {
        glPushMatrix();

        RenderHelper.enableGUIStandardItemLighting();

        RenderItem renderItem = (RenderItem) RenderManager.instance.getEntityClassRenderObject(EntityItem.class);

        //renderItem.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), stack, (int) x, (int) -26);

        renderItem.renderItemIntoGUI(mc.fontRenderer, mc.getTextureManager(), stack, x, -26);

        if (stack.hasEffect()) {

            glPushMatrix();
            glDepthMask(false);
            //glDisable(GL_DEPTH_TEST);
            glDepthMask(false);
            glDepthFunc(514);
            glDisable(GL_LIGHTING);
            glEnable(GL_BLEND);
            glBlendFunc(768, 1);
            mc.getTextureManager().bindTexture(RenderItem.RES_ITEM_GLINT);

            glMatrixMode(5890);
            //glPushMatrix();

            glColor4f(0.5F, 0.25F, 0.8F, 1.0F);
            renderItem.renderGlint(x * 431278612 + -26 * 32178161, x - 2, -26 - 2, 20, 20);

            glDisable(GL_BLEND);

            glMatrixMode(5888);
            glDepthFunc(515);
            glDepthMask(true);
            //glDisable(GL_DEPTH_TEST);
            glPopMatrix();
        }

        renderItem.renderItemOverlayIntoGUI(mc.fontRenderer, mc.getTextureManager(), stack, (int) x, (int) -26);

        RenderHelper.disableStandardItemLighting();

        if (enchantments.getValue()) {
            renderEnchantmentText(stack, x, -26, scale);
        }

        glPopMatrix();
    }

    private void renderEnchantmentText(ItemStack stack, int x, int y, double scale) {
        Map<Integer, Integer> enchantmentList = EnchantmentHelper.getEnchantments(stack);

        List<String> lines = new ArrayList<>();

        if (stack.getItem() instanceof ItemAppleGold && stack.hasEffect()) {
            lines.add("god");
        }

        if (!enchantmentList.isEmpty()) {

            for (Map.Entry<Integer, Integer> e : enchantmentList.entrySet()) {
                int id = e.getKey();
                int level = e.getValue();

                Enchantment enchantment;
                try {
                    enchantment = Enchantment.enchantmentsList[id];
                } catch (IndexOutOfBoundsException ignored) {
                    continue;
                }

                if (level >= Short.MAX_VALUE) {
                    lines.clear();
                    lines.add("32k");
                    break;
                }

                if (enchantment != null) {
                    String str = enchantment.getTranslatedName(level);
                    lines.add(str.substring(0, 3) + (level > 1 ? (" " + level) : ""));
                }
            }

            double s = 0.5;

            for (String ln : lines) {
                if (y < lines.size() * ((mc.fontRenderer.FONT_HEIGHT + 1) * s)) {
                    y -= ((mc.fontRenderer.FONT_HEIGHT + 16) * s);
                }

                glPushMatrix();

                glEnable(GL_BLEND);
                glDisable(GL_DEPTH_TEST);
                glDepthMask(false);
                glScaled(s, s, s);

                int color = -1;
                if (ln.equals("32k") || ln.equals("god")) {
                    color = new Color(255, 0, 0).getRGB();
                }

                mc.fontRenderer.drawString(ln, (int) (x * 2.0), y, color);

                glScaled(2.0, 2.0, 0.0);
                glDepthMask(true);
                glEnable(GL_DEPTH_TEST);

                glDisable(GL_BLEND);

                glPopMatrix();
            }

        }

//        double posY = (y - 16.0) * 2;
//
//        glPushMatrix();
//
//        glEnable(GL_BLEND);
//        glDisable(GL_DEPTH_TEST);
//        glDepthMask(false);
//        glScaled(0.5, 0.5, 0.5);
//
//        mc.fontRenderer.drawString("t", (int) (x * 2.0), (int) posY, -1);
//
//        glScaled(2.0, 2.0, 0.0);
//        glDepthMask(true);
//        glEnable(GL_DEPTH_TEST);
//
//        glDisable(GL_BLEND);

        //glPopMatrix();
    }

    private String getDisplayInfo(EntityPlayer player) {
        StringBuilder builder = new StringBuilder();

        if (LogoutSpots.isFake(player.getEntityId())) {
            return EnumChatFormatting.DARK_GRAY + player.getCommandSenderName();
        }

        if (ping.getValue()) {
            int latency = 0;

            for (GuiPlayerInfo info : mc.thePlayer.sendQueue.playerInfoList) {
                if (info.name.equals(player.getGameProfile().getName())) {
                    latency = info.responseTime;
                    break;
                }
            }

            builder.append(latency).append("ms").append(" ");
        }

        if (Nebula.getInstance().getFriendManager().isFriend(player) || player.equals(mc.thePlayer)) {
            builder.append(EnumChatFormatting.AQUA);
        } else {
            if (player.isSneaking()) {
                builder.append(EnumChatFormatting.GOLD);
            } else if (player.isPlayerSleeping()) {
                builder.append(EnumChatFormatting.DARK_PURPLE);
            }
        }

        if (player.equals(mc.thePlayer)) {
            builder.append("You");
        } else {
            builder.append(player.getCommandSenderName());
        }

        builder.append(EnumChatFormatting.RESET);

        if (health.getValue()) {
            builder.append(" ");

            float playerHealth = player.getHealth() + player.getAbsorptionAmount();

            EnumChatFormatting healthColor = playerHealth > 18.0f
                    ? EnumChatFormatting.GREEN
                    : (playerHealth > 16.0f
                        ? EnumChatFormatting.DARK_GREEN
                        : (playerHealth > 12.0f
                            ? EnumChatFormatting.YELLOW
                            : (playerHealth > 8.0f
                                ? EnumChatFormatting.GOLD
                                : (playerHealth > 5.0f
                                    ? EnumChatFormatting.RED
                                    : EnumChatFormatting.DARK_RED))));

            builder.append(healthColor).append(String.format("%.2f", playerHealth));
        }

        return builder.toString();
    }
}

package wtf.nebula.impl.module.render;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.src.*;
import wtf.nebula.event.RenderWorldEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.impl.value.Value;
import wtf.nebula.repository.impl.FriendRepository;
import wtf.nebula.util.MathUtil;
import wtf.nebula.util.render.RenderUtil;

import java.util.ArrayList;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

public class Nametags extends Module {
    public Nametags() {
        super("Nametags", ModuleCategory.RENDER);
    }

    public final Value<Boolean> armor = new Value<>("Armor", true);
    public final Value<Boolean> ping = new Value<>("Ping", true);
    public final Value<Boolean> health = new Value<>("Health", true);

    @EventListener
    public void onRenderWorld(RenderWorldEvent event) {
        for (Object obj : mc.theWorld.playerEntities) {
            if (!(obj instanceof EntityPlayer)) {
                continue;
            }

            EntityPlayer player = (EntityPlayer) obj;

            double x = MathUtil.interpolate(player.posX, player.lastTickPosX, event.getPartialTicks()) - RenderManager.renderPosX;
            double y = MathUtil.interpolate(player.posY, player.lastTickPosY, event.getPartialTicks()) + (player.equals(mc.thePlayer) ? 0.0 : 1.625) - RenderManager.renderPosY;
            double z = MathUtil.interpolate(player.posZ, player.lastTickPosZ, event.getPartialTicks()) - RenderManager.renderPosZ;

            renderPlayerTag(player, x, y, z);
        }
    }

    private void renderPlayerTag(EntityPlayer player, double x, double y, double z) {
        double renderX = RenderManager.renderPosX;
        double renderY = RenderManager.renderPosY;
        double renderZ = RenderManager.renderPosZ;

        double distance = mc.renderViewEntity.getDistance(x + renderX, y + renderY, z + renderZ);
        double scale = (0.3 * Math.max(distance, 4.0)) / 50.0;

        glPushMatrix();

        RenderHelper.enableGUIStandardItemLighting();
        glDisable(GL_LIGHTING);

        glEnable(GL_POLYGON_OFFSET_FILL);
        glPolygonOffset(1.0f, -1500000.0f);

        glTranslated(x, y + 0.5, z);
        glRotated(-RenderManager.instance.playerViewY, 0.0f, 1.0f, 0.0f);
        glRotated(RenderManager.instance.playerViewX, mc.gameSettings.thirdPersonView == 2 ? -1.0f : 1.0f, 0.0f, 0.0f);
        glScaled(-scale, -scale, scale);

        glDisable(GL_DEPTH_TEST);

        String info = getPlayerInfo(player);

        float width = mc.fontRenderer.getStringWidth(info) / 2.0f;

        RenderUtil.drawRect(-(width - 2.0), -(mc.fontRenderer.FONT_HEIGHT - 1.0), (width * 2.0) + 2.0, mc.fontRenderer.FONT_HEIGHT + 2.0, 0x70000000);
        mc.fontRenderer.drawStringWithShadow(info, (int) -(width - 4.0), -(mc.fontRenderer.FONT_HEIGHT - 2), -1);

        int xOffset = (-24 / 2 * player.inventory.armorInventory.length) + 12;

        if (player.getHeldItem() != null && player.getHeldItem().getItem() != null) {
            renderItem(player.getHeldItem(), xOffset, 1.0);
            xOffset += 16;
        }

        else {
            xOffset += 8;
        }

        if (armor.getValue()) {
            for (int i = 3; i >= 0; --i) {
                ItemStack armor = player.inventory.armorInventory[i];

                if (armor == null || armor.getItem() == null) {
                    continue;
                }

                renderItem(armor, xOffset, 1.0);
                xOffset += 16;
            }
        }

        glPolygonOffset(1.0f, 1500000.0f);
        glDisable(GL_POLYGON_OFFSET_FILL);

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_LIGHTING);

        glPopMatrix();
    }

    private void renderItem(ItemStack stack, int x, double scale) {
        glPushMatrix();

        // allow item GLINT to render for enchants
        glDepthMask(true);
        glScaled(scale, scale, scale);
        glClear(256);
        glEnable(GL_LIGHTING);

        RenderHelper.enableStandardItemLighting();
        glEnable(GL_DEPTH_TEST);

        RenderItem renderItem = (RenderItem) RenderManager.instance.getEntityClassRenderObject(EntityItem.class);

        renderItem.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), stack, x, -26);
        renderItem.renderItemOverlayIntoGUI(mc.fontRenderer, mc.getTextureManager(), stack, x, -26);

        RenderHelper.disableStandardItemLighting();
        glScaled(0.5, 0.5, 0.5);

        renderEnchantments(stack, x, -26);

        glDisable(GL_LIGHTING);
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_BLEND);

        glPopMatrix();
    }

    private void renderEnchantments(ItemStack stack, double x, double y) {
        Map<Integer, Integer> enchants = EnchantmentHelper.getEnchantments(stack);

        ArrayList<String> text = new ArrayList<>();
        if (stack.getItem() == Item.appleGold && stack.hasEffect()) {
            text.add(EnumChatFormatting.RED + "god");
        }

        if (enchants.isEmpty() && text.isEmpty()) {
            return;
        }

        for (Map.Entry<Integer, Integer> entry : enchants.entrySet()) {

            String name;

            try {
                Enchantment enchantment = Enchantment.enchantmentsList[entry.getKey()];

                if (enchantment != null) {
                    name = enchantment.getTranslatedName(entry.getValue());
                }

                else {
                    continue;
                }
            } catch (Exception ignored) {
                continue;
            }

            text.add(shortenEnchantName(name, entry.getValue()));
        }

        double scaling = 1.0;
        if (y < text.size() * ((mc.fontRenderer.FONT_HEIGHT + 1) * scaling)) {
            y -= (text.size() * ((mc.fontRenderer.FONT_HEIGHT + 1) * scaling) + 16.0);
        }

        for (String str : text) {
            glPushMatrix();
            glEnable(GL_BLEND);

            glDisable(GL_DEPTH_TEST);
            glDepthMask(false);

            glScaled(scaling, scaling, scaling);

            mc.fontRenderer.drawStringWithShadow(str, (int) (x * 2.0), (int) y, -1);
            y += (mc.fontRenderer.FONT_HEIGHT + 1);

            glScaled(2.0, 2.0, 2.0);

            glDepthMask(true);
            glEnable(GL_DEPTH_TEST);
            glDisable(GL_BLEND);

            glPopMatrix();
        }
    }

    private String shortenEnchantName(String str, int level) {
        String shortened = str.substring(0, 3);

        if (level > 1) {
            shortened += " " + level;
        }

        return shortened;
    }

    private String getPlayerInfo(EntityPlayer player) {
        StringBuilder builder = new StringBuilder();

        if (ping.getValue()) {
            int latency = 0;

            try {
                for (GuiPlayerInfo info : mc.getNetHandler().playerInfoList) {
                    if (info.name.equals(player.getEntityName())) {
                        latency = info.responseTime;
                    }
                }
            } catch (Exception ignored) {

            }

            builder.append(latency).append("ms").append(" ");
        }

        if (FriendRepository.get().isFriend(player)) {
            builder.append(EnumChatFormatting.AQUA);
        }

        else {

            if (player.isSneaking()) {
                builder.append(EnumChatFormatting.GOLD);
            }
        }

        builder.append(player.getEntityName()).append(EnumChatFormatting.RESET);

        if (health.getValue()) {
            builder.append(" ");

            float health = player.getHealth() + player.getAbsorptionAmount();

            if (health >= 20.0f) {
                builder.append(EnumChatFormatting.GREEN);
            }

            else if (health < 12.0f) {
                builder.append(EnumChatFormatting.RED);
            }

            else {
                builder.append(EnumChatFormatting.GREEN);
            }

            builder.append(String.format("%.1f", health));
        }

        return builder.toString();
    }
}

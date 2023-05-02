package lol.nebula.module.visual;

import lol.nebula.Nebula;
import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.render.EventRender3D;
import lol.nebula.listener.events.render.EventRenderLabel;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.setting.Setting;
import lol.nebula.util.render.RenderUtils;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author aesthetical
 * @since 05/01/23
 */
public class Tags extends Module {
    private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");

    private final Setting<Boolean> background = new Setting<>(true, "Background");

    public Tags() {
        super("Tags", "Renders custom 3D tags over players", ModuleCategory.VISUAL);
    }

    @Listener
    public void onRender3D(EventRender3D event) {
        // if the render view entity is null, return
        if (mc.renderViewEntity == null) return;

        List<EntityPlayer> players = mc.theWorld.playerEntities;
        for (EntityPlayer player : players) {
            if (player == null || player.isDead || player.equals(mc.renderViewEntity)) continue;

            double x = player.prevPosX + (player.posX - player.prevPosX) * event.getPartialTicks();
            double y = player.prevPosY + (player.posY - player.prevPosY) * event.getPartialTicks();
            double z = player.prevPosZ + (player.posZ - player.prevPosZ) * event.getPartialTicks();

            renderTag(player, x, y, z);
        }
    }

    @Listener
    public void onRenderLabel(EventRenderLabel event) {
        if (event.getEntity() instanceof EntityPlayer) event.cancel();
    }

    /**
     * Renders a custom tag above a player
     * @param player the player entity
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     */
    private void renderTag(EntityPlayer player, double x, double y, double z) {

        // get the render xyz coordinates
        double renderX = RenderManager.renderPosX;
        double renderY = RenderManager.renderPosY;
        double renderZ = RenderManager.renderPosZ;

        glPushMatrix();

        glEnable(GL_POLYGON_OFFSET_FILL);
        glPolygonOffset(1.0f, -1100000.0f);

        RenderHelper.disableStandardItemLighting();
        glDisable(GL_LIGHTING);

        glTranslated(x - renderX, (y + player.height + 0.3) - renderY, z - renderZ);
        glRotatef(-RenderManager.instance.playerViewY, 0.0f, 1.0f, 0.0f);
        glRotatef(RenderManager.instance.playerViewX, mc.gameSettings.thirdPersonView == 2 ? -1.0f : 1.0f, 0.0f, 0.0f);

        double distance = mc.renderViewEntity.getDistance(x, y, z);
        double scaling = (0.25 * Math.max(distance, 4.0)) / 50.0;
        glScaled(-scaling, -scaling, scaling);

        glDisable(GL_DEPTH_TEST);

        String text = getDisplayInfo(player);
        int height = mc.fontRenderer.FONT_HEIGHT;
        double width = mc.fontRenderer.getStringWidth(text) / 2.0;

        if (background.getValue()) {
            RenderUtils.rect(-width - 2, -(height + 1), (width * 2.0) + 4.0, height + 3, 0x58000000);
        }

        mc.fontRenderer.drawStringWithShadow(text, (int) -width, (int) (-height + (((height + 3) / 2.0) - (height / 2.0))), -1);

        glEnable(GL_DEPTH_TEST);

        ItemStack heldStack = player.getHeldItem();
        int xOffset = (-24 / 2 * player.inventory.armorInventory.length) + (heldStack == null ? 16 : 8);

        if (heldStack != null) {
            renderItemStack(heldStack, xOffset);
            xOffset += 16;
        }

        for (int i = 3; i >= 0; --i) {
            ItemStack stack = player.inventory.armorInventory[i];
            if (stack != null) {
                renderItemStack(stack, xOffset);
                xOffset += 16;
            }
        }

        glEnable(GL_DEPTH_TEST);
        glDepthMask(true);

        glPolygonOffset(1.0f, 1100000.0f);
        glDisable(GL_POLYGON_OFFSET_FILL);

        glEnable(GL_ALPHA_TEST);

        glPopMatrix();
    }

    /**
     * Renders an item stack into the 3D space
     * @param stack the item stack
     * @param x the position x
     */
    private void renderItemStack(ItemStack stack, int x) {
        glPushMatrix();
        RenderHelper.enableGUIStandardItemLighting();

        RenderItem renderItem = (RenderItem) RenderManager.instance.getEntityClassRenderObject(EntityItem.class);

        renderItem.renderItemIntoGUI(mc.fontRenderer, mc.getTextureManager(), stack, x, -26);
        renderItem.renderItemOverlayIntoGUI(mc.fontRenderer, mc.getTextureManager(), stack, x, -26);

        if (stack.hasEffect()) {
            glEnable(GL_BLEND);
            glDepthFunc(GL_EQUAL);
            glDisable(GL_LIGHTING);
            glDepthMask(false);
            mc.getTextureManager().bindTexture(RES_ITEM_GLINT);
            glEnable(GL_ALPHA_TEST);
            glColor4f(0.5f, 0.25f, 0.8f, 1.0f);
            renderItem.renderGlint(x * 431278612 + -26 * 32178161, x - 2, -26 - 2, 20, 20);
            glDepthMask(true);
            glDisable(GL_ALPHA_TEST);
            glEnable(GL_LIGHTING);
            glDepthFunc(GL_LEQUAL);
            glDisable(GL_BLEND);
        }

        RenderHelper.disableStandardItemLighting();

        {
            double y = -26.0;
            Map<Integer, Integer> enchantmentList = EnchantmentHelper.getEnchantments(stack);

            if (!enchantmentList.isEmpty()) {
                glPushMatrix();

                glDisable(GL_DEPTH_TEST);
                glScaled(0.5, 0.5, 0.5);

                for (int id : enchantmentList.keySet()) {
                    int level = enchantmentList.get(id);
                    String text = level >= Short.MAX_VALUE ? "32k" : "";

                    Enchantment enchantment = Enchantment.enchantmentsList[id];
                    if (enchantment != null && text.isEmpty()) {
                        text = enchantment.getTranslatedName(level).substring(0, 3) + (level > 1 ? (" " + level) : "");
                    }

                    if (!text.isEmpty()) {
                        y -= ((mc.fontRenderer.FONT_HEIGHT + 16) * 0.5);
                        mc.fontRenderer.drawString(text, (int) (x * 2.0), (int) y, text.equals("32k") ? 0xFFFF0000 : -1);
                    }
                }

                glScaled(2.0, 2.0, 0.0);
                glEnable(GL_DEPTH_TEST);

                glPopMatrix();

            }
        }

        glPopMatrix();
    }

    /**
     * Generates display information about a player
     * @param player the player
     * @return the information
     */
    private String getDisplayInfo(EntityPlayer player) {
        StringBuilder builder = new StringBuilder();

        if (Nebula.getInstance().getFriends().isFriend(player)) {
            builder.append(EnumChatFormatting.AQUA);
            builder.append("[F]");
            builder.append(EnumChatFormatting.RESET);
            builder.append(" ");
        }

        builder.append(player.func_145748_c_().getFormattedText());
        builder.append(" ");

        builder.append(EnumChatFormatting.GRAY);
        builder.append("[");
        builder.append(EnumChatFormatting.WHITE);
        builder.append(String.format("%.1f", (player.getHealth() + player.getAbsorptionAmount()) / 2.0f));
        builder.append(EnumChatFormatting.RED);
        builder.append("\u2764");
        builder.append(EnumChatFormatting.GRAY);
        builder.append("]");

        return builder.toString();
    }
}

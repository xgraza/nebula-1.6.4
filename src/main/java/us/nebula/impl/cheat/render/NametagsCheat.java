package us.nebula.impl.cheat.render;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import us.nebula.Nebula;
import us.nebula.api.listener.EventListener;
import us.nebula.api.listener.Subscribe;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.api.value.Setting;
import us.nebula.impl.cheat.player.FreecamCheat;
import us.nebula.impl.event.render.EventRender3D;
import us.nebula.util.render.RenderUtil;

import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author xgraza
 * @since 03/29/25
 */
@CheatManifest(name = "Nametags",
        description = "Shows player information",
        category = CheatCategory.RENDER)
public final class NametagsCheat extends Cheat
{
    private static final String HEART_UNICODE_CHARACTER = "\u2665";
    private static final int ITEM_RENDER_SIZE = 16;

    private final Setting<Float> sizeSetting = new Setting<>(
            "Size", 0.25f, 0.05f, 3.0f, 0.05f);

    @Subscribe
    private final EventListener<EventRender3D> render3DEventListener = event ->
    {
        for (final EntityPlayer player : MC.theWorld.playerEntities)
        {
            if (player == null
                    || player.getEntityId() == FreecamCheat.CAMERA_ENTITY_ID
                    || (player == MC.renderViewEntity && MC.gameSettings.thirdPersonView == 0))
            {
                continue;
            }
            final double x = player.prevPosX + (player.posX - player.prevPosX) * event.getPartialTicks();
            double y = player.prevPosY + (player.posY - player.prevPosY) * event.getPartialTicks();
            if (player == MC.thePlayer)
            {
                y -= MC.thePlayer.height;
            }
            final double z = player.prevPosZ + (player.posZ - player.prevPosZ) * event.getPartialTicks();
            renderPlayerTag(player,
                    x - RenderManager.renderPosX,
                    y - RenderManager.renderPosY,
                    z - RenderManager.renderPosZ);
        }
    };

    private void renderPlayerTag(final EntityPlayer player, final double x, final double y, final double z)
    {
        glPushMatrix();

        glEnable(GL_POLYGON_OFFSET_FILL);
        glPolygonOffset(1.0f, -1100000.0f);

        RenderHelper.disableStandardItemLighting();
        glDisable(GL_LIGHTING);

        glTranslated(x, y + player.height + 0.5, z);
        glRotatef(-RenderManager.instance.playerViewY, 0.0f, 1.0f, 0.0f);
        glRotatef(RenderManager.instance.playerViewX,
                MC.gameSettings.thirdPersonView == 2
                        ? -1.0f
                        : 1.0f,
                0.0f, 0.0f);

        final double distance = MC.renderViewEntity.getDistance(x + RenderManager.renderPosX,
                y + RenderManager.renderPosY,
                z + RenderManager.renderPosZ);
        final double scale = (sizeSetting.getValue() * Math.max(distance, 4.0)) / 50.0;
        glScaled(-scale, -scale, scale);

        glDisable(GL_DEPTH_TEST);

        final String text = getDisplayInfo(player);
        final int height = MC.fontRenderer.FONT_HEIGHT;
        final double width = MC.fontRenderer.getStringWidth(text) / 2.0;

        MC.fontRenderer.drawStringWithShadow(text,
                (int) -width,
                (int) (-height + (((height + 3) / 2.0) - (height / 2.0))),
                -1);

        glEnable(GL_DEPTH_TEST);

        final ItemStack heldStack = player.getHeldItem();
        int itemX = (-24 / 2 * player.inventory.armorInventory.length)
                + (heldStack == null ? ITEM_RENDER_SIZE : 8);

        if (heldStack != null)
        {
            renderItemStack(heldStack, itemX);
            itemX += ITEM_RENDER_SIZE;
        }

        for (int i = 3; i >= 0; --i)
        {
            final ItemStack stack = player.inventory.armorInventory[i];
            if (stack != null)
            {
                renderItemStack(stack, itemX);
                itemX += ITEM_RENDER_SIZE;
            }
        }

        glEnable(GL_DEPTH_TEST);
        glDepthMask(true);

        glPolygonOffset(1.0f, 1100000.0f);
        glDisable(GL_POLYGON_OFFSET_FILL);

        glEnable(GL_ALPHA_TEST);

        glPopMatrix();
    }

    private void renderItemStack(final ItemStack stack, final int x)
    {
        RenderUtil.renderItemWithEffects(stack, x, -26);

        renderEnchantmentText:
        {
            final Map<Integer, Integer> enchantmentList = EnchantmentHelper.getEnchantments(stack);
            if (enchantmentList.isEmpty())
            {
                break renderEnchantmentText;
            }

            glPushMatrix();

            glDisable(GL_DEPTH_TEST);
            glScaled(0.5, 0.5, 0.5);

            double textPosY = -26.0;
            final boolean is32kStack = enchantmentList.values().stream().anyMatch((level) -> level >= Short.MAX_VALUE);
            if (is32kStack)
            {
                textPosY -= ((MC.fontRenderer.FONT_HEIGHT + ITEM_RENDER_SIZE) * 0.5);
                MC.fontRenderer.drawStringWithShadow("32k", (int) (x * 2.0), (int) textPosY, 0xFFFF0000);
            } else
            {
                for (final int id : enchantmentList.keySet())
                {
                    final Enchantment enchantment = Enchantment.enchantmentsList[id];
                    if (enchantment == null)
                    {
                        continue;
                    }

                    final int level = enchantmentList.get(id);
                    String text = enchantment.getTranslatedName(level).substring(0, 3) + " " + level;

                    textPosY -= ((MC.fontRenderer.FONT_HEIGHT + ITEM_RENDER_SIZE) * 0.5);
                    MC.fontRenderer.drawStringWithShadow(text, (int) (x * 2.0), (int) textPosY, -1);
                }
            }

            glScaled(2.0, 2.0, 0.0);
            glEnable(GL_DEPTH_TEST);

            glPopMatrix();
        }
    }

    private String getDisplayInfo(final EntityPlayer player)
    {
        final StringBuilder builder = new StringBuilder();

        if (Nebula.INSTANCE.getFriendManager().isFriend(player)
                || player == MC.thePlayer)
        {
            builder.append(EnumChatFormatting.AQUA);
        }
        if (player.isSneaking())
        {
            builder.append(EnumChatFormatting.GOLD);
        }
        builder.append(player.func_145748_c_().getFormattedText());
        builder.append(EnumChatFormatting.RESET);

        builder.append(" ");
        builder.append(EnumChatFormatting.GRAY);
        builder.append("[");
        builder.append(EnumChatFormatting.WHITE);
        builder.append(String.format("%.1f", (player.getHealth() + player.getAbsorptionAmount()) / 2.0f));
        builder.append(EnumChatFormatting.RED);
        builder.append(HEART_UNICODE_CHARACTER);
        builder.append(EnumChatFormatting.GRAY);
        builder.append("]");

        return builder.toString();
    }
}

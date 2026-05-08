package us.nebula.client.cheat.impl.render;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import us.nebula.client.Nebula;
import us.nebula.client.util.render.gui.font.Fonts;
import us.nebula.client.listener.EventListener;
import us.nebula.client.listener.Subscribe;
import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.util.value.Setting;
import us.nebula.client.cheat.impl.player.FreecamCheat;
import us.nebula.client.listener.event.render.EventRender3D;
import us.nebula.client.util.io.NetworkUtil;
import us.nebula.client.util.render.RenderUtil;

import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author xgraza
 * @since 03/29/25
 */
@CheatManifest(name = "Nametags",
        description = "Shows entity information above their head",
        category = CheatCategory.RENDER)
public final class NametagsCheat extends Cheat
{
    private static final ItemStack FAKE_BONE_STACK = new ItemStack(Items.bone, 1);
    private static final int ITEM_RENDER_SIZE = 16;

    private final Setting<Float> sizeSetting = new Setting<>(
            "Size", 0.25f, 0.05f, 3.0f, 0.05f);
    private final Setting<Boolean> pingSetting = new Setting<>(
            "Ping", true);

    @Subscribe
    private final EventListener<EventRender3D> render3DEventListener = event ->
    {
        for (final Entity entity : MC.theWorld.loadedEntityList)
        {
            if (!(entity instanceof EntityPlayer || entity instanceof EntityTameable)
                    || entity.getEntityId() == FreecamCheat.CAMERA_ENTITY_ID
                    || (entity == MC.renderViewEntity && MC.gameSettings.thirdPersonView == 0))
            {
                continue;
            }
            final double x = entity.prevPosX + (entity.posX - entity.prevPosX) * event.getPartialTicks();
            double y = entity.prevPosY + (entity.posY - entity.prevPosY) * event.getPartialTicks();
            if (entity == MC.thePlayer)
            {
                y -= MC.thePlayer.height;
            }
            final double z = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * event.getPartialTicks();
            renderPlayerTag(entity,
                    x - RenderManager.renderPosX,
                    y - RenderManager.renderPosY,
                    z - RenderManager.renderPosZ);
        }
    };

    private void renderPlayerTag(final Entity entity, final double x, final double y, final double z)
    {
        glPushMatrix();

        glEnable(GL_POLYGON_OFFSET_FILL);
        glPolygonOffset(1.0f, -1100000.0f);

        RenderHelper.disableStandardItemLighting();
        glDisable(GL_LIGHTING);

        double offset = entity.height;
        if (entity instanceof EntityPlayer)
        {
            offset += 0.5;
        }

        glTranslated(x, y + offset, z);
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

        final String text = getDisplayInfo(entity);
        final double width = Fonts.POPPINS.getStringWidth(text) / 2.0;
        if (!text.isEmpty())
        {
            final int height = MC.fontRenderer.FONT_HEIGHT;
            Fonts.POPPINS.drawStringShadow(text,
                    (int) -width,
                    (int) (-height + (((height + 3) / 2.0) - (height / 2.0))),
                    -1);
        }

        glEnable(GL_DEPTH_TEST);

        if (entity instanceof EntityPlayer)
        {
            final EntityPlayer player = (EntityPlayer) entity;
            final ItemStack heldStack = player.getHeldItem();
            int itemX = (-24 / 2 * player.inventory.armorInventory.length)
                    + (heldStack == null ? ITEM_RENDER_SIZE : 8);

            if (heldStack != null)
            {
                renderItemStack(heldStack, itemX, -26);
                itemX += ITEM_RENDER_SIZE;
            }

            for (int i = 3; i >= 0; --i)
            {
                final ItemStack stack = player.inventory.armorInventory[i];
                if (stack != null)
                {
                    renderItemStack(stack, itemX, -26);
                    itemX += ITEM_RENDER_SIZE;
                }
            }
        } else if (entity instanceof EntityTameable && !text.isEmpty())
        {
            renderItemStack(FAKE_BONE_STACK, (int) -(width + 16), -9);
        }

        glEnable(GL_DEPTH_TEST);
        glDepthMask(true);

        glPolygonOffset(1.0f, 1100000.0f);
        glDisable(GL_POLYGON_OFFSET_FILL);

        glEnable(GL_ALPHA_TEST);

        glPopMatrix();
    }

    private void renderItemStack(final ItemStack stack, final int x, final int y)
    {
        RenderUtil.renderItemWithEffects(stack, x, y);

        final Map<Integer, Integer> enchantmentList = EnchantmentHelper.getEnchantments(stack);
        if (enchantmentList.isEmpty())
        {
            return;
        }

        glPushMatrix();

        glDisable(GL_DEPTH_TEST);
        glScaled(0.5, 0.5, 0.5);

        double textPosY = y;
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

    private String getDisplayInfo(final Entity entity)
    {
        if (entity instanceof EntityTameable)
        {
            return ((EntityTameable) entity).getOwnerName();
        }
        final EntityPlayer player = (EntityPlayer) entity;
        final StringBuilder builder = new StringBuilder();

        if (pingSetting.getValue())
        {
            builder.append(EnumChatFormatting.GRAY);
            builder.append(NetworkUtil.getLatency(player));
            builder.append("ms ");
            builder.append(EnumChatFormatting.RESET);
        }

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

        final float health = player.getHealth() + player.getAbsorptionAmount();

        if (health >= 20.0f)
        {
            builder.append(EnumChatFormatting.GREEN);
        } else if (health >= 10.0f)
        {
            builder.append(EnumChatFormatting.YELLOW);
        } else if (health >= 8.0f)
        {
            builder.append(EnumChatFormatting.RED);
        } else
        {
            builder.append(EnumChatFormatting.DARK_RED);
        }

        builder.append(String.format("%.1f", health));

        return builder.toString();
    }
}
